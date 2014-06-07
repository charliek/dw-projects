package com.charlieknudsen.dw.common.exceptions

import com.charlieknudsen.dw.common.transfer.Errors

class ValidationException extends RuntimeException {

    Errors validationErrors

    ValidationException(String s) {
        super(s)
        validationErrors = new Errors(s)
    }

    ValidationException(String s, Throwable throwable) {
        super(s, throwable)
        validationErrors = new Errors(s)
    }
}
