package amanzat.com.exception;

public class KafkaRequestFailedException extends RuntimeException{
    public KafkaRequestFailedException(String message){ super(message);}
}
