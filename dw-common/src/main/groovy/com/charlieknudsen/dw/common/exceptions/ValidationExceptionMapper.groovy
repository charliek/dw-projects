package com.charlieknudsen.dw.common.exceptions

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    Response toResponse(ValidationException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(exception.validationErrors)
                .type(MediaType.APPLICATION_JSON)
                .build()
    }
}
