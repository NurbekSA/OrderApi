package org.example.persistence.model.exception;

public class KafkaResponseFailedException extends RuntimeException {
    public KafkaResponseFailedException(String message) {
        super(message);
    }
}
