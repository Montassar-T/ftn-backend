package com.ftn.backend.exception.auth;

public class JwtInvalidException extends AuthException {

    public JwtInvalidException(String message) {
        super(message);
    }
}
