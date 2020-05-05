package ru.dkovalev.logger.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BooleanSupplier;

public class LogProducer {

    private static final Logger logger = LoggerFactory.getLogger(LogProducer.class);

    private final BooleanSupplier predicate;

    public LogProducer(BooleanSupplier predicate) {
        this.predicate = predicate;
    }

    public void produce() {
        if (predicate.getAsBoolean()) {
            logger.warn("Some issue");
        }
    }
}
