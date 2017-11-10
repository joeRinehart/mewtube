package com.thirdstart.grails.kickstart.environment.nonproduction

import com.thirdstart.grails.kickstart.environment.IStartupConfigurationCheck

/**
 * Convenient base for checks that will run in development AND test.
 */
abstract class AbstractNonProductionConfigurationCheck implements IStartupConfigurationCheck {

    Boolean test = true
    Boolean development = true
    Boolean production = false

    abstract void execute()
}
