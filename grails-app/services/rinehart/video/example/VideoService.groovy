package rinehart.video.example

import com.amazonaws.services.elastictranscoder.model.Job
import com.amazonaws.services.elastictranscoder.model.JobInput
import com.amazonaws.services.elastictranscoder.model.TimeSpan
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.transfer.Upload
import com.thirdstart.grails.kickstart.security.ApplicationUser
import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityService
import rinehart.video.example.amazon.AmazonGateway

import java.util.concurrent.TimeUnit

/**
 * Transactionally handles coarse-grained operations for videos
 */
class VideoService {

    GrailsApplication grailsApplication
    SpringSecurityService springSecurityService
    AmazonGateway amazonGateway

    ApplicationUser getCurrentUser() {
        return springSecurityService.currentUser as ApplicationUser
    }

    /**
     * Starts an AWS transcoding job for a given video, marking it as started
     *
     * @param video
     * @return
     */
    UploadedVideo startTranscoding( UploadedVideo video ) {
        if ( !video.transcodingStarted ) {
            Job job = amazonGateway.startTranscoding( video )
            video.transcodingJobId = job.id
            video.transcodingStarted = true
            save( video )
        } else {
            log.info("Duplicate transcoding request received for UploadedVideo:${video.id}.")
        }

        return video
    }

    /**
     * Determines if a video is completely transcoded (by presence of its playlist
     * in S3). If newly transcoded, updates metadata.
     *
     * @param video
     * @return
     */
    Boolean isTranscoded( UploadedVideo video ) {
        Boolean newStatus = amazonGateway.keyExists( video.id.toString() + '.m3u8' )

        if ( newStatus != video.transcoded ) {
            video.transcoded = newStatus

            if ( video.transcoded ) {
                JobInput input = amazonGateway.getTranscodingInputForVideo( video )
                video.resolution = input.detectedProperties.width + 'x' + input.detectedProperties.height
                Long milliseconds = input.detectedProperties.durationMillis
                video.duration = String.format("%d minutes %d seconds",
                        TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                        TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
                )
            }
        }

        save( video )

        return video.transcoded
    }

    /**
     * Finds all videos for current user.
     *
     * @return
     */
    List<UploadedVideo> findAll() {
        return UploadedVideo.findAllByUser( currentUser )
    }

    /**
     * Finds video by id, enforcing "must be owned by current user"
     *
     * @param id
     * @return
     */
    UploadedVideo findById(UUID id) {
        UploadedVideo video = UploadedVideo.findById( id )

        if ( video && video.user != currentUser ) {
            throw new Exception("Permission denied.")
        }

        if ( !video ) {
            video = createForKey( id )
        }

        return video
    }

    /**
     * Deletes a video (owned by current user) and cleans up its S3 assets.
     *
     * @param id
     */
    void delete(UUID id) {
        UploadedVideo video = findById(id)
        amazonGateway.deleteAssetsForVideo( video )
        video.delete(flush:true)
    }

    /**
     * Internal helper that deals with creating a new video instance, confirming
     * that a source S3 asset exists and reasonably suitable for use.
     *
     * @param key
     * @return
     */

    protected UploadedVideo createForKey(UUID key) {
        UploadedVideo video = new UploadedVideo(
                user: currentUser,
                transcoded: false,
        )

        video.id = key

        // Make sure there's an uploaded video
        video.uploaded = amazonGateway.keyExists( amazonGateway.originalKeyForVideo( key ) )

        if ( !video.uploaded ) {
            throw new Exception("Original video hasn't been uploaded.")
        }

        // We're only allowing a set number per user
        if ( findAll().size() >= grailsApplication.config.rinehart.video.example.max_videos ) {
            amazonGateway.deleteOriginalById( key )
            throw new VideoServiceFormatException("You're at your limit of videos. Remove one to add another.")
        }

        // Validate its content
        S3Object s3Object = amazonGateway.originalObjectById( key )

        if ( s3Object.objectMetadata.contentLength > amazonGateway.maxFilesize ) {
            amazonGateway.deleteOriginalById( key )
            throw new VideoServiceFormatException("Original video is too large: we only support up to ${amazonGateway.maxFilesizeLabel}.")
        }

        if ( !video.transcoded ) {
            startTranscoding( video )
        }

        save( video )

        return video
    }

    /**
     * Saves a video, wrapping flush and failure options.
     *
     * @param video
     * @return
     */
    UploadedVideo save(UploadedVideo video) {
        video.save(failOnError: true, flush: true)
    }

}

class VideoServiceFormatException extends Exception {

    VideoServiceFormatException(String message) {
        super(message)
    }

}
