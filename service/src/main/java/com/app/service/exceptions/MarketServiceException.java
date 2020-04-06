package com.app.service.exceptions;

public class MarketServiceException extends RuntimeException {
    public MarketServiceException(String message) {
        super(message);
    }
}
