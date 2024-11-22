package amanzat.com.exception;

public class KafkaResponseParseException extends RuntimeException {
    public KafkaResponseParseException(String message) {
        super(message);
    }
}
