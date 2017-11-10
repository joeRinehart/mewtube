package rinehart.video.example

import com.thirdstart.grails.kickstart.AbstractApplicationController
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Log
import rinehart.video.example.amazon.AmazonGateway
import rinehart.video.example.amazon.SignedPolicy

@Log
@Secured(['IS_AUTHENTICATED_FULLY'])
class VideoController extends AbstractApplicationController {

    AmazonGateway amazonGateway
    VideoService videoService

    /**
     * Gets a video instance based on an S3 key, validating it
     * for existence.
     */
    def index() {
        Map model = [:]
        try {
            model.video = videoService.findById( UUID.fromString( params.id ) )
        } catch ( VideoServiceFormatException e ) {
            model.error = e.message
        }


        render (model as JSON)
    }

    /**
     * Creates a policy to upload a new video
     */
    def policy() {
        UUID uuid = UUID.randomUUID()
        String key = amazonGateway.originalKeyForVideo(uuid)

        // add in what we need to do an upload
        Map model = [
            id: uuid.toString(),
            key: key,
            bucket: config.aws.bucket,
            accessKey: config.aws.access_key
        ]

        SignedPolicy policy = amazonGateway.createSignedPolicy(
                amazonGateway.createVideoUploadPolicy( key )
        )
        model.uploadPolicy = [
            parameters: policy.rawPolicy,
            policy: policy.encodedPolicy,
            signature: policy.signature
        ]

        // Could use respond, but we're doing this quick/dirty and want nothing but JSON all the time
        render ( model as JSON )
    }

    /**
     * Removes a video and any S3 assets
     *
     * @return
     */
    def remove() {
        videoService.delete(UUID.fromString( params.id ))
        render ([removed: true] as JSON)
    }

    /**
     * Non-API method: renders an HTML5 viewer for a video
     */
    def play() {
        UploadedVideo video = videoService.findById( UUID.fromString( params.id ) )

        if ( !video ) {
            response.sendError(404)
        }

        return defaultModel << [
            poster: amazonGateway.thumbUriForVideo(video),
            src: amazonGateway.transcodedUriForVideo(video)
        ]
    }

    /**
     * Lists all videos for the current user
     */
    def list() {
        // JSON views would improve this, but it's a quick example app.
        Map videos = [
                videos: videoService.findAll().collect { UploadedVideo video ->
                    [
                        id: video.id,
                        transcodingStarted: video.transcodingStarted,
                        transcoded: video.transcoded,
                        thumbUri: amazonGateway.thumbUriForVideo(video),
                        downloadUri: amazonGateway.transcodedUriForVideo(video),
                        duration: video.duration,
                        resolution: video.resolution
                    ]
                }
        ]
        render ( [ videos: videos ] as JSON)

    }

    /**
     * Checks whether or not a given video has started and completed transcoding
     */
    def transcodingStatus() {
        UploadedVideo video = videoService.findById( UUID.fromString( params.id ) )

        if ( !video ) {
            response.sendError(404)
        }


        Map status = [
            transcodingStarted: video.transcodingStarted,
            transcoded: videoService.isTranscoded( video )
        ]
        render ( [ status: status ] as JSON )
    }

    private genericException(String message) {
        throw new Exception(message)
    }


}