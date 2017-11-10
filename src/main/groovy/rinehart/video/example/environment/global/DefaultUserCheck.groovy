package rinehart.video.example.environment.global

import com.thirdstart.grails.kickstart.environment.IStartupConfigurationCheck
import com.thirdstart.grails.kickstart.security.ApplicationUser
import grails.core.GrailsApplication
import groovy.sql.Sql
import groovy.util.logging.Log

@Log
/**
 * Makes sure any preconfigured users for this environment are created
 * (config: rinehart.video.defaultUsers[{username: foo, password: bar}]
 */
class DefaultUserCheck implements IStartupConfigurationCheck {

    Boolean production = true
    Boolean test = true
    Boolean development = true

    Sql sql
    GrailsApplication grailsApplication

    List getConfiguredUserList() {
        return grailsApplication.config.rinehart.video.example.users
    }

    void execute() {
        log.info("Checking to make sure default users exist...")
        configuredUserList.each{ Map user ->
            if ( !ApplicationUser.findByUsername(user.username) ) {
                // ApplicationUserPasswordEncoderListener will hash password before DB write...
                new ApplicationUser(
                        username: user.username,
                        password: user.password,
                        enabled: true,
                        accountLocked: false,
                        accountExpired: false,
                        passwordExpired: false
                ).save(failOnError: true)
                log.info("  -> user ${user.username} created with password '${user.password}'.")
            } else {
                log.info("  -> user ${user.username} already existed.")
            }
        }
    }
}
