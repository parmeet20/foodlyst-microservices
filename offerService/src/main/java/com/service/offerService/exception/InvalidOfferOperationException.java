package com.service.offerService.exception;

public class InvalidOfferOperationException extends RuntimeException {
    public InvalidOfferOperationException(String message) {
        super(message);
    }
}