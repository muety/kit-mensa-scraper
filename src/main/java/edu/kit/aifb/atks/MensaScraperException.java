package edu.kit.aifb.atks;

public class MensaScraperException extends RuntimeException {
    public MensaScraperException() {
    }

    public MensaScraperException(String message) {
        super(message);
    }

    public MensaScraperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MensaScraperException(Throwable cause) {
        super(cause);
    }

    public MensaScraperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
