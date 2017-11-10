package grails.bootstrap.kickstart

import com.thirdstart.grails.kickstart.environment.ConventionalExternalEnvironmentLoader
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment

class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    void setEnvironment(Environment environment) {
        new ConventionalExternalEnvironmentLoader().configure( environment )
    }

}