package com.charlieknudsen.dw.test

import ch.qos.logback.classic.Level

import com.yammer.dropwizard.config.Configuration
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.config.LoggingConfiguration
import com.yammer.dropwizard.config.LoggingFactory
import com.yammer.dropwizard.db.DatabaseConfiguration
import com.yammer.dropwizard.hibernate.SessionFactoryFactory

import com.fasterxml.jackson.databind.ObjectMapper
import com.yammer.dropwizard.json.ObjectMapperFactory
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction

import spock.lang.Shared
import spock.lang.Specification

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

    DatabaseConfiguration getDatabaseConfiguration() {
        databaseId = UUID.randomUUID()
        def properties = ['hibernate.current_session_context_class': 'thread',
                'hibernate.show_sql': 'false',
                'hibernate.generate_statistics': 'false',
                'hibernate.use_sql_comments': 'false',
                'hibernate.hbm2ddl.auto': 'create']
        return new DatabaseConfiguration(driverClass: 'org.h2.Driver',
                user: 'sa', password: 'sa',
                url: "jdbc:h2:build/${databaseId}",
                properties: properties)
    }

    private SessionFactory buildSessionFactory() {
        if (!entities) {
            throw new IllegalStateException('No database entities found while setting up test')
        }
        LoggingConfiguration.ConsoleConfiguration consoleConfiguration = new LoggingConfiguration.ConsoleConfiguration()
        consoleConfiguration.setThreshold(Level.INFO)
        LoggingConfiguration loggingConfiguration = new LoggingConfiguration(consoleConfiguration: consoleConfiguration)
        loggingConfiguration.setLoggers(['org.hibernate': Level.INFO])

        Configuration configuration = new Configuration(loggingConfiguration: loggingConfiguration)
        new LoggingFactory(configuration.loggingConfiguration,'DAOTest').configure()
        SessionFactoryFactory factory = new SessionFactoryFactory()
        Environment environment = new Environment('DAOTest', configuration, null, null)
        factory.build(environment, databaseConfiguration, entities)
    }
}
