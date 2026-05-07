package com.ftn.backend.security;

import com.ftn.backend.configuration.CookieProperties;
import com.ftn.backend.configuration.JwtProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final CookieProperties cookieProperties;
    private final JwtProperties jwtProperties;

    public ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofMillis(jwtProperties.getRefreshExpiration()))
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .path("/api/v1/auth/refresh")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
