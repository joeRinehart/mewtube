package rinehart.video.example.amazon

import com.amazonaws.HttpMethod
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClientBuilder
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput
import com.amazonaws.services.elastictranscoder.model.CreateJobPlaylist
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest
import com.amazonaws.services.elastictranscoder.model.CreateJobResult
import com.amazonaws.services.elastictranscoder.model.Job
import com.amazonaws.services.elastictranscoder.model.JobInput
import com.amazonaws.services.elastictranscoder.model.ReadJobRequest
import com.amazonaws.services.elastictranscoder.model.ReadJobResult
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.S3Object
import grails.converters.JSON
import grails.core.GrailsApplication
import rinehart.video.example.UploadedVideo

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Abstracts use of Amazon into a single class.
 */
class AmazonGateway {

    GrailsApplication grailsApplication

    /**
     * Configured AWS credentials
     */
    AWSCredentials awsCredentials

    /**
     * Factory method for an AWSCredentialsProvider
     */
    AWSCredentialsProvider createCredentialsProvider() {
        new AWSStaticCredentialsProvider( awsCredentials )
    }

    /**
     * Factory method for an S3 client
     */
    AmazonS3 createS3Client() {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion( config.region )
                .withCredentials( createCredentialsProvider() ).build()

    }

    /**
     * Factory method for an ET client
     */
    AmazonElasticTranscoder createEtClient() {
        return AmazonElasticTranscoderClientBuilder
                .standard()
                .withRegion( config.region )
                .withCredentials( createCredentialsProvider() ).build()
    }

    /**
     * AWS configuration
     */
    Map getConfig() {
        return grailsApplication.config.aws as Map
    }

    /**
     * What's the maximum file size we support in bytes?
     */
    Long getMaxFilesize() {
        return config.max_filesize
    }

    /**
     * What's a friendly label for the maximum file size?
     */
    String getMaxFilesizeLabel() {
        return config.max_filesize_label
    }


    /**
     * Amazon's date format. Could be externalized, not bothering for this.
     */
    static final String AMAZON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"


    /**
     * Formats a date for Amazon SDK calls
     */
    protected formatDateForAmazon(Date date) {
        return date.format(AMAZON_DATE_FORMAT)
    }

    /**
     * Creates a transcoding job for a given key
     */
    Job startTranscoding(UploadedVideo video) {
        AmazonElasticTranscoder et = createEtClient()

        return et.createJob(
                new CreateJobRequest(
                        pipelineId: config.et_pipeline,
                        input: new JobInput(
                           key: originalKeyForVideo(video.id)
                        ),
                        output: new CreateJobOutput(
                            presetId: config.et_preset_id,
                            key: transcodedKeyForVideo(video),
                            segmentDuration: 10,
                            thumbnailPattern: video.id.toString() + '/thumb-{count}',
                        ),
                        playlists: [
                            new CreateJobPlaylist(
                                name: video.id.toString(),
                                format: 'HLSv3',
                                outputKeys: [ transcodedKeyForVideo(video) ],
                            )
                        ]
                )
        ).job
    }

    /**
     * Returns the transcoding job for a given video.
     *
     * @param video
     * @return
     */
    Job getTranscodingJobForVideo(UploadedVideo video) {
        return createEtClient().readJob(
                new ReadJobRequest().withId(video.transcodingJobId)
        ).job
    }

    /**
     * Returns information about the input for a transcoded video.
     *
     * @param video
     * @return
     */
    JobInput getTranscodingInputForVideo(UploadedVideo video) {
        return getTranscodingJobForVideo(video)?.inputs?.first()
    }


    /**
     * Returns the expected original key for a uuid
     * @param video
     * @return
     */
    String originalKeyForVideo(UUID uuid) {
        return uuid.toString() + '/original'
    }

    /**
     * Returns the expected transcoded key for a video
     * @param video
     * @return
     */
    String transcodedKeyForVideo(UploadedVideo video) {
        return video.id.toString() + '/hls_400_k'
    }

    /**
     * Returns a protocol-relative URI for a transcoded video
     */
    String transcodedUriForVideo(UploadedVideo video) {
        return signedGetUrlForKey(
                transcodedKeyForVideo(video) + '.m3u8'
        )
    }

    /**
     * Returns the expected thumbnail key for a video
     * @param video
     * @return
     */
    String thumbKeyForVideo(UploadedVideo video) {
        return video.id.toString() + '/thumb-00001.png'
    }

    /**
     * Returns a signed URL for a thumbnail
     */
    String thumbUriForVideo(UploadedVideo video) {
        return signedGetUrlForKey(
            thumbKeyForVideo(video)
        )
    }

    /**
     * Returns a signed url for an original
     */
    String originalUriForVideo(UploadedVideo video) {
        return signedGetUrlForKey(
                originalKeyForVideo(video.id)
        )
    }

    /**
     * Returns a signed GET request URL for a given key
     */
    String signedGetUrlForKey( String key ) {
        createS3Client().generatePresignedUrl(
                config.bucket as String,
                key,
                new Date( new Date().time + 1000 * 60 * 60 ),
        ).toString()
    }

    /**
     * Returns a policy that can be used to upload a video
     *
     * @param UUID key
     * @return
     */
    Map createVideoUploadPolicy(String key) {
        return [
            expiration: formatDateForAmazon( new Date( new Date().time + (1000 * 60 * 60 * 12) ) ), // expire in three hours...
            conditions: [
                ['bucket': config.bucket],
                ['success_action_status': '201'],
                ['starts-with', '$key', key],
                ['starts-with', '$Filename', key],
                ['starts-with', '$name', ''],
            ]
        ]
    }

    /**
     * Creates a signed policy from a Map representing an AWS policy
     * @param policy
     * @return
     */
    SignedPolicy createSignedPolicy(Map policy) {
        return new SignedPolicy(policy, config.secret_key)
    }

    /**
     * Gets the original object for an id
     */
    S3Object originalObjectById(UUID id) {
        return createS3Client().getObject( config.bucket, originalKeyForVideo( id ) )
    }

    /**
     * Deletes the original object by id
     */
    void deleteOriginalById(UUID id) {
        createS3Client().deleteObject(config.bucket, originalKeyForVideo( id ) )
    }

    /**
     * Deletes all assets for a video
     */
    void deleteAssetsForVideo(UploadedVideo video) {
        AmazonS3 s3 = createS3Client()

        s3.deleteObject(config.bucket, originalKeyForVideo( video.id ) )
        s3.deleteObject(config.bucket, thumbKeyForVideo( video ) )
        s3.deleteObject(config.bucket, transcodedKeyForVideo( video ) )
    }

    /**
     * Does a given key exist in our configured bucket?
     */
    Boolean keyExists(String key) {
        AmazonS3 s3 = createS3Client()
        s3.doesObjectExist(config.bucket, key)
    }


}

/**
 * Helper class wrapping the boilerplate involved in signing an AWS policy.
 */
class SignedPolicy {
    static final String HASH_TYPE = 'HmacSHA1'

    Map rawPolicy
    String encodedPolicy
    String signature

    SignedPolicy(Map policyMap, String secretKey) {
        rawPolicy = policyMap
        encodedPolicy = (policyMap as JSON).toString().bytes.encodeBase64().toString()

        SecretKeySpec keySpec = new SecretKeySpec(secretKey.bytes, HASH_TYPE)
        Mac mac = Mac.getInstance(HASH_TYPE)
        mac.init(keySpec)

        byte[] rawHmac = mac.doFinal(encodedPolicy.bytes)
        signature = rawHmac.encodeBase64()toString()
    }
}
