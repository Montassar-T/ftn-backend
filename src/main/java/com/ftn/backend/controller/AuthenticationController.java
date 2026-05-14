package com.ftn.backend.controller;

import com.ftn.backend.dtos.*;
import com.ftn.backend.security.CookieService;
import com.ftn.backend.security.principal.UserPrincipal;
import com.ftn.backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<SingleResultDto<AuthResponse>> login(@RequestBody LoginDto request) {

        LoginResult result = authenticationService.login(request);

        ResponseCookie cookie = cookieService.buildRefreshCookie(result.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(SingleResultDto.<AuthResponse>builder()
                        .data(result.getAuthResponse())
                        .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<SingleResultDto<AuthResponse>> refresh(@CookieValue("refreshToken") String refreshToken) {

        LoginResult result = authenticationService.refresh(refreshToken);

        ResponseCookie cookie = cookieService.buildRefreshCookie(result.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(SingleResultDto.<AuthResponse>builder()
                        .data(result.getAuthResponse())
                        .build());
    }

    @GetMapping("/me")
    public ResponseEntity<SingleResultDto<UserDto>> me(Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();

        return ResponseEntity.ok(
                SingleResultDto.<UserDto>builder().data(userDto).build());
    }
}
