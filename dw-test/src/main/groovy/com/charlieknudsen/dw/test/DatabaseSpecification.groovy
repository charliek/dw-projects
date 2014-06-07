package com.charlieknudsen.dw.test

import com.charlieknudsen.dw.common.ObjectMapperFactory
import com.charlieknudsen.dw.common.exceptions.NotFoundExceptionMapper
import com.charlieknudsen.dw.common.exceptions.ValidationExceptionMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableList
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.hibernate.HibernateBundle
import io.dropwizard.hibernate.SessionFactoryFactory
import io.dropwizard.setup.Environment
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import spock.lang.Shared
import spock.lang.Specification

import javax.validation.Validation

abstract class DatabaseSpecification extends Specification {

    @Shared SessionFactory sessionFactory
    @Shared String databaseId
    @Shared ObjectMapper objectMapper

    Session session
    Transaction transaction

    def setupSpec() {
        sessionFactory = buildSessionFactory()
        objectMapper = new ObjectMapperFactory().build()
    }

    def setup() {
        session = sessionFactory.currentSession
        transaction = session.beginTransaction()
    }

    def cleanup() {
        session?.flush()
        session?.clear()
        transaction?.rollback()

        session = null
        transaction = null
    }

    def cleanupSpec() {
        sessionFactory = null
        ["build/${databaseId}.h2.db", "build/${databaseId}.trace.db"].each {
            File dbFile = new File(it)
            if (dbFile.exists()) {
                dbFile.deleteOnExit()
            }
        }
    }

    abstract List<Class<?>> getEntities()

    DataSourceFactory getDatabaseConfiguration() {
        databaseId = UUID.randomUUID()
        def properties = ['hibernate.current_session_context_class': 'thread',
                'hibernate.show_sql': 'false',
                'hibernate.generate_statistics': 'false',
                'hibernate.use_sql_comments': 'false',
                'hibernate.hbm2ddl.auto': 'create']
        return new DataSourceFactory(driverClass: 'org.h2.Driver',
                user: 'sa', password: 'sa',
                url: "jdbc:h2:build/${databaseId}",
                properties: properties)
    }

    private SessionFactory buildSessionFactory() {
        if (!entities) {
            throw new IllegalStateException('No database entities found while setting up test')
        }

        // TODO setup logging configuration
        SessionFactoryFactory factory = new SessionFactoryFactory()

        Environment environment = new Environment(
                'DAOTest',
                new ObjectMapperFactory().build(),
                Validation.buildDefaultValidatorFactory().validator,
                null, // metrics registry
                Thread.classLoader)

        // TODO need to make this abstract so it can vary based on the service
        environment.jersey().register(NotFoundExceptionMapper)
        environment.jersey().register(ValidationExceptionMapper)

        ImmutableList<Class<?>> classList = new ImmutableList.Builder<Class<?>>()
                .addAll(entities)
                .build()

        HibernateBundle<Configuration> bundle = new HibernateBundle<Configuration>(
                classList, new SessionFactoryFactory()) {
            @Override
            DataSourceFactory getDataSourceFactory(Configuration configuration) {
                return databaseConfiguration
            }
        }

        factory.build(bundle, environment, databaseConfiguration, entities)
    }
}
