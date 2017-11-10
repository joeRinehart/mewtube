package grails.bootstrap.kickstart

import com.thirdstart.grails.kickstart.environment.ConfigurationChecker
import grails.converters.JSON

class BootStrap {

    ConfigurationChecker configurationChecker

    def init = { servletContext ->
        configurationChecker.runStartupChecks()

        // Not going to get too fancy and externalize this
        JSON.registerObjectMarshaller(UUID) { UUID it ->
            return it.toString()
        }
    }
    def destroy = {
    }

}
