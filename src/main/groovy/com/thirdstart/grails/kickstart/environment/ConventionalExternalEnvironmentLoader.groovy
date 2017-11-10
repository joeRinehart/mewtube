package com.thirdstart.grails.kickstart.environment

import groovy.util.logging.Log
import org.springframework.beans.factory.config.YamlMapFactoryBean
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

@Log
/**
 * Honors a custom.config.location java system variable first
 * and looks through conventionally-named directories for
 * additional .yml files for use in configuring your application.
 *
 * You probably don't need to use this in production because of
 * spring.config.location being honored within Grails.
 */
class ConventionalExternalEnvironmentLoader {

    void configure(Environment environment) {
        List paths = determineConfigurationPaths()

        if ( paths ) {
            YamlMapFactoryBean yamlFb = new YamlMapFactoryBean(
                    resources: paths.collect{ String configPath ->
                        log.info "Adding config .yml: ${configPath}..."
                        File environmentFile = new File(configPath)
                        return new FileSystemResource(environmentFile.canonicalPath)
                    }
            )
            yamlFb.afterPropertiesSet()

            environment.propertySources.addFirst(new PropertiesPropertySource("custom", yamlFb.getObject()))
        }
    }

    protected List<String> determineConfigurationPaths() {
        String paths = System.properties["custom.config.location"]
        List<String> result

        if ( paths != null ) {
            result = paths.tokenize(',')
        } else {
            File environmentDirectory = new File("./grails-app/conf/environment")

            if ( environmentDirectory.exists() && environmentDirectory.canRead() ) {
                result = new FileNameByRegexFinder().getFileNames(environmentDirectory.canonicalPath, /.*\.yml$/)
            }
        }

        return result
    }
}
