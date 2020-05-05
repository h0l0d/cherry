package ru.dkovalev.logger.test

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.slf4j.LoggerFactory
import spock.lang.Specification

class LogProducerTest extends Specification {

    ListAppender<ILoggingEvent> testAppender
    Logger rootLogger

    void setup() {
        LoggerContext context = LoggerFactory.getILoggerFactory() as LoggerContext
        rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME)

        testAppender = new ListAppender()
        testAppender.setContext(context)
        testAppender.start()
        rootLogger.addAppender(testAppender)
    }

    void cleanup() {
        rootLogger?.detachAppender(testAppender)
    }

    def "logs warning"() {
        given:
        def producer = new LogProducer({ true })

        when:
        producer.produce()

        then:
        testAppender.list.size() == 1
        def event = testAppender.list.get(0)
        event.message == "Some issue"
        event.level == Level.WARN
    }

    def "logs nothing"() {
        given:
        def producer = new LogProducer({ false })

        when:
        producer.produce()

        then:
        testAppender.list.isEmpty()
    }
}
