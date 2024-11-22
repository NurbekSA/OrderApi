package amanzat.com.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    private ErrorDetails createErrorDetails(Exception ex, WebRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.error("Ошибка ID={}, сообщение={}, детали={}", errorId, ex.getMessage(), request.getDescription(false), ex);
        return new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false) + " | Error ID: " + errorId);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = createErrorDetails(ex, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(KafkaResponseFailedException.class)
    public ResponseEntity<ErrorDetails> handleKafkaResponseFailedException(KafkaResponseFailedException ex, WebRequest request) {
        ErrorDetails errorDetails = createErrorDetails(ex, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE); // Лучше подходит для временной ошибки
    }

    @ExceptionHandler(KafkaRequestFailedException.class)
    public ResponseEntity<ErrorDetails> handleKafkaRequestFailedException(KafkaRequestFailedException ex, WebRequest request) {
        ErrorDetails errorDetails = createErrorDetails(ex, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST); // Соответствует ошибке запроса
    }

    @ExceptionHandler(KafkaResponseParseException.class)
    public ResponseEntity<ErrorDetails> handleKafkaResponseParseException(KafkaResponseParseException ex, WebRequest request) {
        ErrorDetails errorDetails = createErrorDetails(ex, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = createErrorDetails(ex, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
