package amanzat.com.exception;

public class KafkaResponseFailedException extends RuntimeException {
    public KafkaResponseFailedException(String message) {
        super(message);
    }
}
