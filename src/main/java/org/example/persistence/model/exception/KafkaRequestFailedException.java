package org.example.persistence.model.exception;

public class KafkaRequestFailedException extends RuntimeException{
    public KafkaRequestFailedException(String message){ super(message);}
}
