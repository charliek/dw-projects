package com.charlieknudsen.dw.test

import com.yammer.dropwizard.hibernate.AbstractDAO

abstract class DAOSpecification<T extends AbstractDAO> extends DatabaseSpecification {

    T dao

    def setup() {
        dao = buildDAO()
    }

    abstract T buildDAO()
}
