package com.thirdstart.grails.kickstart.environment

/**
 * Contract for a startup configuration check: it must be able to execute()
 * and state whether or not it is applicable to test, develop, and/or production
 * environments.
 */
interface IStartupConfigurationCheck {

    /**
     * Does this check apply in test?
     *
     * @return
     */
    Boolean getTest()


    /**
     * Does this check apply in development?
     *
     * @return
     */
    Boolean getDevelopment()

    /**
     * Does this check apply in production?
     *
     * @return
     */
    Boolean getProduction()

    /**
     * Perform whatever arbitrary work this check must do.
     */
    void execute()

}