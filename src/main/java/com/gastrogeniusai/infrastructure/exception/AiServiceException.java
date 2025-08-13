package com.gastrogeniusai.infrastructure.exception;

/**
 * Custom exception for AI service related errors.
 * Thrown when AI operations fail or are unavailable.
 */
public class AiServiceException extends RuntimeException {

    public AiServiceException(String message) {
        super(message);
    }

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiServiceException(Throwable cause) {
        super(cause);
    }
}
