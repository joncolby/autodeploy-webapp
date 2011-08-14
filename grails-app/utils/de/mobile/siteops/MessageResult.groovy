package de.mobile.siteops

class MessageResult {

    def id
    def type
    def message
    def fields = []

    static def defaultErrorMessage() {
        return new MessageResult(type: 'error', message: 'Internal Error occured').build()
    }

    static def defaultErrorMessage(id) {
        return new MessageResult(id: id, type: 'error', message: 'Internal Error occured').build()
    }

    static def defaultSuccessMessage() {
        return new MessageResult(type: 'success', message: 'Execution successful').build()
    }

    static def defaultSuccessMessage(id) {
        return new MessageResult(id: id, type: 'success', message: 'Execution successful').build()
    }

    static def warningMessage(message) {
        return new MessageResult(type: 'warning', message: message).build()
    }

    static def errorMessage(message) {
        return new MessageResult(type: 'error', message: message).build()
    }

    static def errorMessage(id, message) {
        return new MessageResult(id: id, type: 'error', message: message).build()
    }

    static def successMessage(message) {
        return new MessageResult(type: 'success', message: message).build()
    }

    static def successMessage(id, message) {
        return new MessageResult(id: id, type: 'success', message: message).build()
    }

    static def addFieldErrors(obj) {
        addFieldErrors(null, obj)
    }

    static def addFieldErrors(id, obj) {
        def result = new MessageResult()
        if (obj && obj?.errors) {
            result.message = 'Validation error'
            result.type = 'error'
            result.id = id
            obj.errors.fieldErrors.each { error ->
                def msg = ""
                if (error.rejectedValue == '') {
                    msg = "Cannot be blank"
                } else {
                    msg = "Invalid value '$error.rejectedValue'"
                }
               result.fields += [ field: error.field, code: error.code, message: msg]
            }
            return result.build()
        } else {
            return MessageResult.defaultErrorMessage(id)
        }
    }

    def build() {
        def title
        def finalMessage
        if (type == 'success') {
            title = "Operation successful"
            finalMessage = message
        } else if (type == 'error') {
            title = "An error occured"
            finalMessage = message + "<br/><div style='text-align: right'; font-size: small'>(please close)</div>"
        } else if (type == 'warning') {
            title = "Warning"
            finalMessage = message + "<br/><div style='text-align: right'; font-size: small'>(please close)</div>"
        }

        def result = [ type: type, title: title, message: finalMessage ]
        if (id) result.put('id', id)
        if (fields && !fields.empty) result.put('fields', fields)
        return result
    }

}
