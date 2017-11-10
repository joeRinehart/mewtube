package spring

import com.amazonaws.auth.BasicAWSCredentials
import com.thirdstart.grails.kickstart.security.ApplicationUserPasswordEncoderListener

import com.thirdstart.grails.kickstart.environment.nonproduction.EnvironmentPropertiesCheck
import com.thirdstart.grails.kickstart.environment.ConfigurationChecker

import groovy.sql.Sql
import rinehart.video.example.amazon.AmazonGateway
import rinehart.video.example.environment.global.DefaultUserCheck;

// Place your Spring DSL code here
beans = {

    // BOOTSTRAPPING

    /*
    TO ADD A NEW CONFIGURATION CHECK:
        1. Register your check as a bean: myCheck(MyCheck), wiring anything to it you need, like Sql (or autowire...)
        2. Add it to the 'checks' list of the configurationChecker bean
    */
    environmentPropertiesCheck(EnvironmentPropertiesCheck)
    defaultUserCheck(DefaultUserCheck) { bean ->
        bean.autowire = 'byName'
    }

    configurationChecker(ConfigurationChecker) {
        checks = [
                ref('environmentPropertiesCheck'),
                ref('defaultUserCheck')
        ]
    }

    // COMMON

    /* A Groovy Sql instance tied to the datasource: because it's just plain handy and fast. */
    sql(Sql, ref('dataSource')){}


    // SPRING SECURITY
    applicationUserPasswordEncoderListener(ApplicationUserPasswordEncoderListener, ref('hibernateDatastore'))

    // AMAZON
    awsCredentials(BasicAWSCredentials, application.config.aws.access_key, application.config.aws.secret_key)

    amazonGateway(AmazonGateway){ bean ->
        bean.autowire = 'byName'
    }

}
