package com.ftn.backend.exception.auth;

public class JwtExpiredException extends AuthException {

    public JwtExpiredException(String message) {
        super(message);
    }
}
