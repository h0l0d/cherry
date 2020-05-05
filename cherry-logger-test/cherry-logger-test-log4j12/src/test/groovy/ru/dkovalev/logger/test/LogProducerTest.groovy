package ru.dkovalev.logger.test

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.spi.LoggingEvent
import spock.lang.Specification

class LogProducerTest extends Specification {

    List<LoggingEvent> log = []
    def testAppender = Spy(AppenderSkeleton) {
        doAppend(_) >> { LoggingEvent event -> log.add(event) }
    }

    void setup() {
        Logger.getRootLogger().addAppender(testAppender)
    }

    void cleanup() {
        Logger.getRootLogger().removeAppender(testAppender)
    }

    def "logs warning"() {
        given:
        def producer = new LogProducer({ true })

        when:
        producer.produce()

        then:
        log.size() == 1
        def event = log.get(0)
        event.message == "Some issue"
        event.level == Level.WARN
    }

    def "logs nothing"() {
        given:
        def producer = new LogProducer({ false })

        when:
        producer.produce()

        then:
        log.isEmpty()
    }
}
