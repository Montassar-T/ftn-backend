package com.ftn.backend.controller;

import com.ftn.backend.dtos.*;
import com.ftn.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<SingleResultDto<UserDto>> register(
            @Valid @RequestBody RegisterRequestDto request
    ) {
        return ResponseEntity.ok(
                new SingleResultDto<>(userService.register(request))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<SingleResultDto<TokenResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletResponse response
    ) {
        TokenResponseDto token = userService.login(request);

        Cookie cookie = new Cookie("refresh_token", token.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth/refresh");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);

        token.setRefreshToken(null);
        return ResponseEntity.ok(new SingleResultDto<>(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<SingleResultDto<TokenResponseDto>> refresh(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response
    ) {
        TokenResponseDto token = userService.refresh(refreshToken);

        Cookie cookie = new Cookie("refresh_token", token.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth/refresh");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);

        token.setRefreshToken(null);
        return ResponseEntity.ok(new SingleResultDto<>(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response
    ) {
        userService.logout(refreshToken);

        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth/refresh");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<SingleResultDto<UserDto>> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                new SingleResultDto<>(userService.getCurrentUser(jwt.getSubject()))
        );
    }
}