package com.charlieknudsen.dw.common.transfer

class Errors {
    List<FieldError> fieldErrors = []
    List<ClassError> objectErrors = []

    Errors() {}

    Errors(String objectError) {
        objectErrors << new ClassError(message: objectError)
    }
}
