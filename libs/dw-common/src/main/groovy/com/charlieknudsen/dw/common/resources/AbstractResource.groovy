package com.charlieknudsen.dw.common.resources

import com.charlieknudsen.dw.common.exceptions.NotFoundException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

class AbstractResource {

    protected final ObjectMapper objectMapper

    AbstractResource(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

    protected  <T> T translateAndReturn(Object obj, Class<T> klass, String errorMessage=null) {
        verifyNotNull(obj, errorMessage)
        return convert(obj, klass)
    }

    protected  <T> T translateAndReturn(Object obj, TypeReference<T> ref, String errorMessage=null) {
        verifyNotNull(obj, errorMessage)
        return convert(obj, ref)
    }

    protected void verifyNotNull(Object obj, String errorMessage=null) {
        if (obj == null) {
            String msg = errorMessage ?: 'Not Found'
            throw new NotFoundException(msg)
        }
    }

    protected  <T> T convert(Object obj, Class<T> klass) {
        return objectMapper.convertValue(obj, klass)
    }

    protected  <T> T convert(Object obj, TypeReference<T> ref) {
        return objectMapper.convertValue(obj, ref)
    }
}
