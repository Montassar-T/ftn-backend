package com.carServices.backend.exception.auth;

public class JwtExpiredException extends AuthException {

    public JwtExpiredException(String message) {
        super(message);
    }
}
