package com.ftn.backend.service;

import com.ftn.backend.dtos.AuthResponse;
import com.ftn.backend.dtos.LoginDto;
import com.ftn.backend.dtos.LoginResult;
import com.ftn.backend.dtos.NewUserDto;
import com.ftn.backend.enums.Role;
import com.ftn.backend.enums.UserStatus;
import com.ftn.backend.exception.auth.AuthenticationException;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.UserRepository;
import com.ftn.backend.security.JwtService;
import com.ftn.backend.utils.EmailUtils;
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
    public LoginResult register(NewUserDto request) {
        String email = EmailUtils.normalize(request.getEmail());
        if (userRepository.findByEmailAndDeletedAtIsNull(email).isPresent()) {
            throw new ConflictException("Un compte avec cet email existe déjà.");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();
        User saved = userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(saved.getEmail());
        String refreshToken = jwtService.generateRefreshToken(saved.getEmail());
        AuthResponse authResponse = AuthResponse.builder()
                .id(saved.getId())
                .accessToken(accessToken)
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .role(saved.getRole())
                .build();
        return LoginResult.builder()
                .authResponse(authResponse)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
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
                .id(user.getId())
                .accessToken(accessToken)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
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
                .id(user.getId())
                .accessToken(newAccessToken)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();

        return LoginResult.builder()
                .authResponse(authResponse)
                .refreshToken(refreshToken)
                .build();
    }
}
