package org.company.spacedrepetitionbot.exception;

public class WebhookProcessingException extends RuntimeException {
    public WebhookProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
