package rinehart.video.example

import com.thirdstart.grails.kickstart.security.ApplicationUser

class UploadedVideo {

    UUID id
    ApplicationUser user
    Boolean uploaded = false
    Boolean transcodingStarted = false
    Boolean transcoded = false
    String transcodingJobId
    String resolution
    String duration

    static mapping = {
        id generator: 'assigned'
    }

    static constraints = {
        transcodingJobId nullable: true
        resolution nullable: true
        duration nullable: true
    }
}
