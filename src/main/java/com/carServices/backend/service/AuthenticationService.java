package com.carServices.backend.service;

import com.carServices.backend.dtos.AuthResponse;
import com.carServices.backend.dtos.LoginDto;
import com.carServices.backend.dtos.LoginResult;
import com.carServices.backend.enums.ActivityLogAction;
import com.carServices.backend.exception.auth.AuthenticationException;
import com.carServices.backend.model.User;
import com.carServices.backend.repository.UserRepository;
import com.carServices.backend.security.JwtService;
import com.carServices.backend.security.aop.TrackActivity;
import com.carServices.backend.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String INVALID_CREDENTIALS = "Invalid email or password";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    @TrackActivity(action = ActivityLogAction.LOGIN, entityType = "SYSTEM")
    public LoginResult login(LoginDto request) {

        String email = EmailUtils.normalize(request.getEmail());
        User user = userRepository
                .findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new AuthenticationException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException(INVALID_CREDENTIALS);
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();

        return LoginResult.builder()
                .authResponse(authResponse)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public LoginResult refresh(String refreshToken) {

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new AuthenticationException(INVALID_CREDENTIALS);
        }

        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new AuthenticationException(INVALID_CREDENTIALS));

        String newAccessToken = jwtService.generateAccessToken(user.getEmail());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(newAccessToken)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();

        return LoginResult.builder()
                .authResponse(authResponse)
                .refreshToken(refreshToken)
                .build();
    }
}
