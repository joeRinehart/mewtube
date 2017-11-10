package com.thirdstart.grails.kickstart

import com.thirdstart.grails.kickstart.security.ApplicationUser
import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityService

/**
 * Base controller providing conveniences.
 */
class AbstractApplicationController {

    SpringSecurityService springSecurityService
    GrailsApplication grailsApplication

    def getConfig() {
        return grailsApplication.config
    }

    ApplicationUser getCurrentUser() {
        return springSecurityService.currentUser as ApplicationUser
    }

    Map getDefaultModel() {
        return [
            currentUser: currentUser,
            maxVideos: config.rinehart.video.example.max_videos,
            maxFilesize: config.aws.max_filesize_label,
        ]
    }

}
