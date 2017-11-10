function BootstrapKickstartUtils() {
    var self = this

    self.OK = 'Ok'
    self.CANCEL = 'Cancel'
    self.modalOptions = { keyboard: false }
    
    /**
     * Gets a div before the closing body tag with the given id, creating
     * it if it doesn't exist.
     */
    self.contentDiv = function( id ) {
        result = $('#'+ id)

        if ( result.size > 0 ) {
            return result.first()
        } else {
            $('body').append('<div id="' + id + '">HERE!!!!</div>');
            return $('#'+ id).first()
        }
    }

    self.createModalParts = function(id, title) {

        var parts = {
            lead: '',
            mid: '',
            foot: ''
        }

        parts.lead += '<div class="modal" tabindex="-1" role="dialog" id="' + id + '">'
        parts.lead += '  <div class="modal-dialog" role="document">'
        parts.lead += '    <div class="modal-content">'
        if ( title != undefined ) {
            parts.lead += '      <div class="modal-header">'
            parts.lead += '        <h5 class="modal-title">' + title + '</h5>'
            parts.lead+= '      </div>'
        }
        parts.lead += '      <div class="modal-body">'

        parts.mid += '      </div>'
        parts.mid += '      <div class="modal-footer">'

        parts.foot += '      </div>'
        parts.foot += '    </div>'
        parts.foot += '  </div>'
        parts.foot += '</div>'

        return parts
    }

    /**
     * Replacement for JavaScript alert() with a BootStrap modal. Returns a Promise
     * so that you can easily do whatever you want to do after the alert.
     *
     * @param message - The message for your alert
     * @param title - Optional title for your alert
     * @param buttonLabel - Optional button label for your alert, defaulting to kickstart.bootstrap.OK
     */
    self.alert = function( message, title, buttonLabel ) {
        var container = self.contentDiv('kickstart-bootstrap-alert')
        var id = 'kickstart-bootstrap-alert-modal'
        buttonLabel = buttonLabel || self.OK

        var modalParts = self.createModalParts(id, title)
        var content = ''

        content += modalParts.lead
        content += '<p>' + message + '</p>'
        content += modalParts.mid
        content += '<button type="button" class="btn btn-primary" data-dismiss="modal">' + buttonLabel + '</button>'
        content += modalParts.foot

        return new Promise( function( fulfill, reject ) {
            container.html( content )
            var modal = $('#' + id).modal(self.modalOptions)

            modal.on('hidden.bs.modal', function() {
                container.remove()
                fulfill()
            })
        })
    }
    
    /**
     * Replacement for JavaScript confirm() with a BootStrap modal. Returns a Promise
     * so that you can easily do whatever you want to do after the alert. Your .then()
     * method will be passed true ("ok" was clicked) or false ("cancel" was clicked)
     *
     * @param message - The message for your confirm
     * @param title - Optional title for your confirm
     * @param okButtonLabel - Optional OK button label for your confirm, defaulting to kickstart.bootstrap.OK
     * @param cancelButtonLabel - Optional cancel button label for your confirm, defaulting to kickstart.bootstrap.CANCEL
     */
    self.confirm = function( message, title, okButtonLabel, cancelButtonLabel ) {
        var container = self.contentDiv('kickstart-bootstrap-confirm')
        var id = 'kickstart-bootstrap-confirm-modal'
        okButtonLabel = okButtonLabel || self.OK
        cancelButtonLabel = cancelButtonLabel || self.CANCEL

        var modalParts = self.createModalParts(id, title)
        var content = ''

        content += modalParts.lead
        content += '<p>' + message + '</p>'
        content += modalParts.mid
        content += '<button type="button" class="btn btn-secondary" data-dismiss="modal" value="false">' + cancelButtonLabel + '</button>'
        content += '<button type="button" class="btn btn-primary" data-dismiss="modal" value="true">' + okButtonLabel + '</button>'
        content += modalParts.foot

        return new Promise( function( fulfill ) {
            container.html( content )
            var modal = $('#' + id).modal(self.modalOptions)
            var confirmed = true

            $('#' + id + ' .modal-footer button').on('click', function(event) {
                confirmed = confirmed && $(event.target).val() == 'true'
            });

            modal.on('hidden.bs.modal', function() {
                container.remove()
                fulfill(confirmed)
            })
        })
    }


    /**
     * Replacement for JavaScript prompt() with a BootStrap modal. Returns a Promise
     * so that you can easily do whatever you want to do after the alert. Your .then()
     * method will be passed whatever was entered and whether or not the prompt was cancelled
     * (if cancellable is true)
     *
     * @param title - The title for your confirmation
     * @param label - Label for your text input
     * @param value - Value for your text input
     * @param help - optional help text to show beneath your text input
     * @param placeholder - optional placeholder text to show in your text input
     * @param cancellable- Should a cancel button be shown?
     * @param okButtonLabel - Optional OK button label for your confirm, defaulting to kickstart.bootstrap.OK
     * @param cancelButtonLabel - Optional cancel button label for your confirm, defaulting to kickstart.bootstrap.CANCEL
     */
    self.prompt = function( title, label, placeholder, help, value, cancellable, okButtonLabel, cancelButtonLabel ) {
        var container = self.contentDiv('kickstart-bootstrap-confirm')
        var id = 'kickstart-bootstrap-confirm-modal'
        okButtonLabel = okButtonLabel || self.OK
        cancellable = cancellable == undefined ? true : cancellable
        cancelButtonLabel = cancelButtonLabel || self.CANCEL

        var modalParts = self.createModalParts(id, title)
        var content = '<form id="kickstart-bootstrap-prompt-modal-form">'

        content += modalParts.lead
        
        content += '  <div class="form-group">'
        if ( label ) {
            content += '    <label for="kickstart-bootstrap-prompt-modal-input">' + label + '</label>'
        }
        content += '    <input type="text" class="form-control" id="kickstart-bootstrap-prompt-modal-input" aria-describedby="kickstart-bootstrap-prompt-modal-help"'
        if ( value ) {
            content += 'value="' + value + '"'
        }
        if ( placeholder ) {
            content += 'placeholder="' + placeholder + '"'
        }
        content += '>'

        if ( help ) {
            content += '    <small id="kickstart-bootstrap-prompt-modal-help" class="form-text text-muted">' + help + '</small>'
        }
        content += '  </div>'
        
        content += modalParts.mid
        if ( cancellable ) {
            content += '<button type="button" class="btn btn-secondary" data-dismiss="modal" value="false">' + cancelButtonLabel + '</button>'
        }
        content += '<button type="submit" class="btn btn-primary" data-dismiss="modal" value="true">' + okButtonLabel + '</button>'
        content += modalParts.foot
        content += '</form>'

        return new Promise( function( fulfill ) {
            container.html( content )


            var modal = $('#' + id).modal(self.modalOptions).on('shown.bs.modal', function() {
                console.log("shown", $('#kickstart-bootstrap-prompt-modal-input'))
                $('#kickstart-bootstrap-prompt-modal-input').focus()
            })
            var val = null
            var cancelled = false


            modal.on('hidden.bs.modal', function() {
                container.remove()
                fulfill( { value: val, cancelled: cancelled } )
            })

            var closeItAll = function(wasCancelled) {
                val = $('#kickstart-bootstrap-prompt-modal-input').val()
                cancelled = wasCancelled
                modal.modal('hide')
            }

            $('#kickstart-bootstrap-prompt-modal-form').submit(function(e) {
                e.preventDefault()
                closeItAll(false)
            })

            $('#' + id + ' .modal-footer button').on('click', function(event) {
                closeItAll( $(event.target).val() == 'false' )
            });

        })
    }

}


var kickstart = kickstart || {}
kickstart.bootstrap = new BootstrapKickstartUtils()
