package edu.kit.aifb.atks;

public class MensaScraperError extends RuntimeException {
    public MensaScraperError() {
    }

    public MensaScraperError(String message) {
        super(message);
    }

    public MensaScraperError(String message, Throwable cause) {
        super(message, cause);
    }

    public MensaScraperError(Throwable cause) {
        super(cause);
    }

    public MensaScraperError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
