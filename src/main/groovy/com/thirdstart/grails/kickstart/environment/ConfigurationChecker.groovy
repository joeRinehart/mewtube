package com.thirdstart.grails.kickstart.environment

import grails.util.Environment
import com.thirdstart.grails.kickstart.environment.IStartupConfigurationCheck
import groovy.util.logging.Log

/**
 * Utilized in the Grails application Bootstrap.groovy to perform configuration checks
 * configured in resources.groovy.
 */
@Log
class ConfigurationChecker {

    List<IStartupConfigurationCheck>checks = []

    private getCurrentEnvironmentName() {
        Environment.current.toString().toLowerCase()
    }

    void runStartupChecks() {
        log.info("Running ${currentEnvironmentName} startup checks...")

        checks.findAll{ IStartupConfigurationCheck check ->
            check[currentEnvironmentName] == true
        }.each { IStartupConfigurationCheck check ->
            check.execute()
        }

        log.info "All startup checks complete."
    }
}