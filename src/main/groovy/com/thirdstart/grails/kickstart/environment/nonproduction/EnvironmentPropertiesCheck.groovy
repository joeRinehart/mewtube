package com.thirdstart.grails.kickstart.environment.nonproduction

import groovy.util.logging.Log
import org.springframework.beans.factory.config.YamlMapFactoryBean
import org.springframework.core.io.FileSystemResource
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

/**
 * Makes sure an environment.yml file is present and can be read. If it can't,
 * it'll provide friendly instructions as to how to fix it.
 */
@Log
class EnvironmentPropertiesCheck extends AbstractNonProductionConfigurationCheck {

    void execute() {
        File environmentFile = new File('./grails-app/conf/environment/environment.yml')
        File templateFile = new File('./grails-app/conf/environment/environment.yml.template')

        log.info("Starting a check for an environment.yml file at ${environmentFile.canonicalPath}")

        if ( !environmentFile.exists() ) {
            log.info("You don't have an environment.yml file yet: I'll create one for you based on the current template.")
            environmentFile << templateFile.text
            throw new Exception("Startup stopped: go edit your new environment.yml file and try again.")
        }

        log.info("Local environment file found, checking for new environment properties...")

        Yaml yaml = new Yaml(
                new DumperOptions( indent: 2, defaultFlowStyle: DumperOptions.FlowStyle.BLOCK )
        )
        Object templateRoot = yaml.load( new FileInputStream(templateFile) )
        Object targetRoot = yaml.load( new FileInputStream(environmentFile) )


        Boolean newKeysFound = false
        Closure mergeConfig

        mergeConfig = { Object templateParent, Object targetParent ->
            if ( templateParent instanceof Map ) {
                Map map = templateParent as Map
                map.eachWithIndex{ k, v, i ->
                    // If key doesn't exist in target, copy it...
                    if ( !targetParent[k] ) {
                        targetParent[k] = v
                        newKeysFound = true
                    } else {
                        // If it exists, and it's a map, crawl it.
                        if ( v instanceof Map ) {
                            mergeConfig(v, targetParent[k])
                        }
                    }
                 }
            } else if ( templateParent instanceof List ) {
                // Lists are a semantically hard thing to determine what to do with. Punt until there's a use case.
            }
        }
        mergeConfig(templateRoot, targetRoot)

        if ( newKeysFound ) {
            yaml.dump(targetRoot, environmentFile.newWriter())
            throw new Exception("Stopping startup. Your environment.yml file has been updated with new settings. Check them for validity and then run-app again.")
        } else {
            log.info("  -> your environment looks up to date!")
        }
    }

}
