package com.ftn.backend.service;

import com.ftn.backend.dtos.*;
import com.ftn.backend.enums.UserRole;
import com.ftn.backend.enums.UserStatus;
import com.ftn.backend.exception.auth.AuthenticationException;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.UserRepository;
import com.ftn.backend.utils.EmailUtils;
import com.ftn.backend.utils.JpaQueryFilters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {

        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String keycloakId) {

        User user = userRepository
                .findByKeycloakIdAndDeletedAtIsNull(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));

        return mapToDto(user);
    }

    @Transactional(readOnly = true)
    public PageDto<UserDto> getAllUsers(Map<String, String> params) {

        JpaQueryFilters<User> filters = new JpaQueryFilters<>(params, User.class);

        Page<User> page = userRepository.findAll(
                filters.getSpecification(),
                filters.getPageable()
        );

        List<UserDto> data = page.stream()
                .map(this::mapToDto)
                .toList();


        return PageDto.<UserDto>builder()
                        .data(data)
                        .total(page.getTotalElements())
                        .build();
    }

    @Transactional
    public void syncUser(Jwt jwt) {

        String keycloakId = jwt.getSubject();

        if (userRepository.existsByKeycloakIdAndDeletedAtIsNull(keycloakId)) {
            return;
        }

        User user = User.builder()
                .keycloakId(keycloakId)
                .email(jwt.getClaimAsString("email"))
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .role(UserRole.ROLE_ADMIN)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public UserDto register(RegisterRequestDto request) {

        if (request.getRole() == UserRole.ROLE_ADMIN) {
            throw new IllegalArgumentException("Admin cannot be self-registered");
        }

        // 1. Create user in Keycloak
        String keycloakId = keycloakService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                String.valueOf(request.getRole())
        );

        // 2. Save in DB
        User user = User.builder()
                .keycloakId(keycloakId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        return mapToDto(user);
    }

    @Transactional
    public UserDto createUser(NewUserDto request, String keycloakId) {

        if (userRepository.findByEmailAndDeletedAtIsNull(
                EmailUtils.normalize(request.getEmail())
        ).isPresent()) {
            throw new ConflictException("Email already exists");
        }

        User user = User.builder()
                .keycloakId(keycloakId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(EmailUtils.normalize(request.getEmail()))
                .status(UserStatus.ACTIVE)
                .build();

        return mapToDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserDto request) {

        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        return mapToDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateCurrentUser(String keycloakId, UpdateUserDto dto) {

        User user = userRepository.findByKeycloakIdAndDeletedAtIsNull(keycloakId)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        return mapToDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public UserDto mapToDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }
}