package com.carServices.backend.exception.auth;

public class JwtInvalidException extends AuthException {

    public JwtInvalidException(String message) {
        super(message);
    }
}
