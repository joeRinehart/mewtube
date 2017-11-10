package com.thirdstart.grails.kickstart.ui

class BootplateTagLib {

    static namespace = 'bp'

    static defaultEncodeAs = [taglib:'none']

    Map getConfig() {
        return grailsApplication.config.bootplate
    }

    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    /**
     * Wrapper for a bootstrap alert.
     * <pre>
     * {@code
     * <bp:alert style="success" heading="Hello there!" dismissable="true">Well now, you've gone and done it!</bp:alert>
     * }
     * </pre>
     *
     * @attr intent - The bootstrap style of alert, defaulting to info: primary, secondary, success, danger, warning, info, light, dark
     * @attr heading - Optional header text at top of alert
     * @attr dismissable - make this alert dismissable, adding a close button?
     */
    def alert = { attrs, body ->
        attrs.intent = attrs.intent ?: 'info'
        attrs.dismissable = attrs.dismissable == null ? false : attrs.dismissable.toString().toBoolean()

        out << """
            <div ${ifTruthy(attrs.id, "id=\"${attrs.id}\"", false)} class="alert alert-${safely(attrs.intent)} ${ifTruthy(attrs.dismissable, 'alert-dismissible')} ${ifTruthy(attrs['class'])}" role="alert">
        """

        if (attrs.heading) {
            out << """
                <h4 class="alert-heading">${safely(attrs.heading)}</h4>
            """
        }

        out << body()

        if ( attrs.dismissable ) {
            out << """
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true"><i class="fa fa-close"></i></span>
                </button>
            """
        }

        out << """
            </div>
        """
    }

    /**
     * Simplification of a modal.
     *
     * @attr id - The id of the modal
     * @attr title - The title of the modal
     * @attr closeable - Should this modal show a close button in the title?
     * @attr class - Any additional CSS classes to add to the outermodal 'modal'-classed div
     * @attr dialog-class - Any additional CSS classes to add to the inner 'modal-dialog'-classed div
     * @attr content-class - Any additional CSS classes to add to the inner 'modal-content'-classed div
     */
    def modal = { attrs, body ->
        out << """
            <div class="modal ${ifTruthy(attrs['class'])}" id="${safely(attrs.id)}" tabindex="-1" role="dialog" aria-labelledby="${safely(attrs.id)}Label" aria-hidden="true">
                <div class="modal-dialog ${ifTruthy(attrs['dialog-class'])}" role="document">
                    <div class="modal-content ${ifTruthy(attrs['content-class'])}">
        """

        if ( attrs.title ) {
            out << """
                <div class="modal-header">
                    <h5 class="modal-title" id="${safely(attrs.id)}Label">${safely(attrs.title)}</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            """
        }

        out << body()

        out << """
                    </div>
                </div>
            </div>
        """
    }

    /**
     * Body of a modal.
     *
     * @attr class - Any additional CSS classes to apply to the wrapping modal-body div
     */
    def modalBody = { attrs, body ->
        out << """
            <div class="modal-body ${ifTruthy(attrs['class'])}">
        """

        out << body()

        out << """
            </div>
        """

        """
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary">Save changes</button>
                        </div>

        """
    }

    /**
     * Body of a modal.
     *
     * @attr class - Any additional CSS classes to apply to the wrapping modal-footer div
     */
    def modalFooter = { attrs, body ->
        out << """
            <div class="modal-footer ${ifTruthy(attrs['class'])}">
        """

        out << body()

        out << """
            </div>
        """
    }

    /**
     * Wrapper around a form field
     *
     * @attr label - text for the generated label
     * @attr inputId - for the 'for' attribute of the generated label
     * @attr help - text to display as a hint below the control
     * @attr validation-feedback - text to display as validation text if your control as the is-invalid class added.
     * @attr prepend - text to use as an input add-on before the input
     * @attr append - text to use as an input add-on after the input
     * @attr group - wrap your control in an input-group div. Automatically true if you provide an append or prepend attribute.
     * @attr responsive - Defaults to true. If false, we'll always do a vertical-style form field.
     * @attr group-class - Any additional CSS classes to add to the form-group div.
     * @attr label-class - Any additional CSS classes to add to the label.
     * @attr help-class - Any additional CSS classes to add to help text.
     */
    def field = { attrs, body ->
        attrs.responsive = attrs.responsive == null ? true : attrs.responsive.toString().toBoolean()
        attrs.group = attrs.group == null ? false : true
        attrs.group = attrs.group || attrs.prepend || attrs.prepend


        out << """
            <div class="form-group ${ifTruthy(attrs.responsive, 'row')} ${ifTruthy(attrs['group-class'])}">
                <label for="${safely(attrs.inputId)}" class="${ifTruthy(attrs.responsive, config.form.labelColClass)} col-form-label ${ifTruthy(attrs['label-class'])}">${safely(attrs.label)}</label>
                ${ifTruthy(attrs.responsive, "<div class=\"${config.form.controlColClass}\">", false)}
                ${ifTruthy(attrs.group, '<div class="input-group flex-wrap">', false)}
        """

        if ( attrs.prepend ) {
            out << """
                <div class="input-group-addon">${safely(attrs.prepend)}</div>
            """
        }

        out << body()

        if ( attrs.append ) {
            out << """
                <div class="input-group-addon">${safely(attrs.append)}</div>
            """
        }

        if ( attrs['validation-feedback']) {
            out << """
                <div class="invalid-feedback w-100">${safely(attrs['validation-feedback'])}</div>
            """
        }

        if ( attrs.help ) {
            out << """
                <small id="${safely(attrs.inputId)}Help" class="form-text text-muted ${ifTruthy(attrs['help-class'])}">${safely(attrs.help)}</small>
            """
        }

        out << """
                ${ifTruthy(attrs.group, '</div>', false)}
                ${ifTruthy(attrs.responsive, '</div>', false)}
            </div>
        """
    }

    /**
     * Created a bootstrap-style form-check wrapped radio
     *
     * @attr name - input name
     * @attr id - input id
     * @attr value - value for the radio
     * @attr checked - is this checked? (Strings are ok, and will be toBoolean()'d instead of truthy'd)
     * @attr label - label for the radio
     * @attr class - Any additional CSS classes for the wrapping div
     * @attr label-class - Any additional CSS classes for the label
     * @attr input-class - Any additional CSS classes for the radio input
     */
    def radio = { attrs, body ->
        attrs.type = 'radio'
        out << check( attrs, body )
    }

    /**
     * Created a bootstrap-style form-check wrapped radio
     *
     * @attr name - input name
     * @attr id - input id
     * @attr value - value for the radio
     * @attr checked - is this checked? (Strings are ok, and will be toBoolean()'d instead of truthy'd)
     * @attr label - label for the radio
     * @attr class - Any additional CSS classes for the wrapping div
     * @attr label-class - Any additional CSS classes for the label
     * @attr input-class - Any additional CSS classes for the checkbox input
     */
    def check = { attrs, body ->
        attrs.type = attrs.type ?: 'checkbox'
        out << """
            <div class="form-check ${ifTruthy(attrs.disabled, 'disabled')} ${ifTruthy(attrs['class'])}">
                <label class="form-check-label ${ifTruthy(attrs['label-class'])}">
                    <input class="form-check-input ${ifTruthy(attrs['input-class'])}" type="${safely(attrs.type)}" name="${safely(attrs.name)}" id="${safely(attrs.id)}" value="${safely(attrs.value)}" ${ifTruthy(attrs.checked, 'checked')}  ${ifTruthy(attrs.disabled, 'disabled')}>
                    ${safely(attrs.label)}
                </label>
            </div>
        """
    }

    /**
     * Wraps a set of content-containing tab tags. Combine the nav-class and class attributes to switch
     * to pills or vertical tabs.
     *
     * @attr id - tab id. One will be generated if you don't provide it (it's how we track content and generate
     * this stuff).
     * @attr nav-class - The type of nav to use for your tabs. Defaults to nav-tabs.
     * @attr class - Any additional CSS classes to apply to the outer nav div.
     */
    def tabs = { attrs, body ->
        Map typeStorage = bootplateComponentTypeStorageFor('tab')
        Map instanceStorage = bootplateComponentStorageFor('tab', attrs.id)

        typeStorage.currentlyRendering = [ id: instanceStorage.id, tabs: [] ]

        String stashedBody = body()


        out << """
            <ul class="nav nav-tabs" id="${instanceStorage.id}" role="tablist">
        """

        typeStorage.currentlyRendering.tabs.each{ tabAttrs ->
            if ( typeStorage.currentlyRendering.activeTab && isTruthy(tabAttrs.active) ) {
                throw new Exception("I can't make the tab '${tabAttrs.id}' active because you're already said that '${typeStorage.currentlyRendering.activeTab.id}' is active!")
            }

            out << """
                <li class="nav-item">
                    <a class="nav-link ${ifTruthy(tabAttrs.active, 'active')}" id="${safely(tabAttrs.id)}-tab" data-toggle="tab" href="#${safely(tabAttrs.id)}" role="tab" aria-controls="${safely(tabAttrs.id)}" aria-selected="${tabAttrs.active.toString().toBoolean() ? 'true' : 'false'}">${tabAttrs.unsafeLabel ? tabAttrs.label : safely(tabAttrs.label)}</a>
                </li>
            """

            if ( !typeStorage.currentlyRendering.activeTab && isTruthy(tabAttrs.active) ) {
                typeStorage.currentlyRendering.activeTab = tabAttrs
            }
        }
        out << """
            </ul>
            <div class="tab-content" id="${instanceStorage.id}-content">
        """

        out << stashedBody

        out << """
            </div>
        """

        typeStorage.currentlyRendering = null
    }

    /**
     * Wraps a tab's content.
     *
     * @id - Required! Becomes the id attribute of the div wrapping your content.
     * @label - Not required, but obviously pretty handy...
     * @active - Is this the active tab? Uses toBoolean() on strings, so the string "false" does not become true...
     * @class - Any additional css classes to add to the div wrapping your content
     * @unsafeLabel - Allows your label to render HTML (so you can include icons, etc. This'll stop it from encoding as HTML, so please know what you're doing!
     */
    def tab = { attrs, body ->
        if ( !attrs.id ) {
            throw new Exception("You need to define an id attribute for any use of the tab tag!")
        }
        Map typeStorage = bootplateComponentTypeStorageFor('tab')
        Map parentTabData = typeStorage.currentlyRendering

        parentTabData.tabs << attrs

        println "Hey: ${attrs['class']}, ${isTruthy(attrs['class'])}, ${ifTruthy(attrs['class'])}"

        out << """
            <div class="tab-pane ${ifTruthy(attrs.active, 'show active')} ${ifTruthy(attrs['class'])}" id="${safely(attrs.id)}" role="tabpanel" aria-labelledby="home-${safely(attrs.id)}">
        """

        out << body()

        out << """
            </div>
        """
    }

    protected Boolean isTruthy(Object expression) {
        return expression ? true : false
    }

    protected ifTruthy(Object expression, Object show = null, Boolean encode = true) {
        show = show ? show.toString() : expression.toString()
        String output = isTruthy( expression ) ? show : ''
        return encode ? safely(output) : output
    }

    protected safely(Object object) {
        return object.toString().encodeAsHTML()
    }

    protected Map getBootplateStorage() {
        if ( pageScope.bootplateStorage == null ) {
            pageScope.bootplateStorage = [:]
        }

        return pageScope.bootplateStorage
    }

    protected Map bootplateComponentTypeStorageFor(String type) {
        if ( !bootplateStorage[type] ) {
            bootplateStorage[type] = [
                    instances: [:],
                    etc: [:]
            ]
        }

        return bootplateStorage[type]

    }

    protected Map bootplateComponentStorageFor(String type, String id = null) {

        Map instancesStorage = bootplateComponentTypeStorageFor(type).instances

        if ( !id ) {
            id = type + '-' + instancesStorage.size()
        }

        if ( !instancesStorage[id] ) {
            instancesStorage[id] = [ id : id ]
        }
        return instancesStorage[id]
    }
}
