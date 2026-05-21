package com.ftn.backend.controller;

import com.ftn.backend.dtos.RegisterRequestDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.UserDto;
import com.ftn.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @GetMapping("/me")
    public ResponseEntity<SingleResultDto<UserDto>> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UserDto user =
                userService.getCurrentUser(jwt.getSubject());

        return ResponseEntity.ok(
                new SingleResultDto<>(user)
        );
    }
}