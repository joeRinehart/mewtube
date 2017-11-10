package com.thirdstart.grails.kickstart

import grails.plugin.springsecurity.annotation.Secured


@Secured(['IS_AUTHENTICATED_FULLY'])
class SimplePageController extends AbstractApplicationController {

    /**
     * Simple action to serve a page that gets the default model.
     */
    def index() {
        render(model: defaultModel, view: params.viewName ?: 'index')
    }

}
