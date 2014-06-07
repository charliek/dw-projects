package com.charlieknudsen.dw.common.exceptions

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    Response toResponse(NotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(['error': exception.message])
                .type(MediaType.APPLICATION_JSON)
                .build()

    }

}
