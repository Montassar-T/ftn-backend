package com.carServices.backend.service;

import com.carServices.backend.dtos.*;
import com.carServices.backend.enums.Role;
import com.carServices.backend.exception.auth.AuthenticationException;
import com.carServices.backend.exception.business.ConflictException;
import com.carServices.backend.exception.business.ResourceNotFoundException;
import com.carServices.backend.model.User;
import com.carServices.backend.model.UserStatus;
import com.carServices.backend.repository.UserRepository;
import com.carServices.backend.utils.JpaQueryFilters;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToDto(user);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<UserDto>> getAllUsers(Map<String, String> params) {
        JpaQueryFilters<User> filters = new JpaQueryFilters<>(params, User.class);
        Page<User> page = userRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<UserDto> data =
                page.stream().map(m -> modelMapper.map(m, UserDto.class)).toList();

        return ResponseEntity.ok(PageDto.<UserDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional
    public UserDto createUser(NewUserDto request) {

        if (userRepository.findByEmailAndDeletedAtIsNull(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .role(Role.EMPLOYEE)
                .build();

        User saved = userRepository.save(user);

        return UserDto.builder()
                .id(saved.getId())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .email(saved.getEmail())
                .status(saved.getStatus())
                .build();
    }

    @Transactional
    public UserDto updateUser(Long id, NewUserDto request) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        return mapToDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateCurrentUser(UpdateUserDto dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        userRepository.save(user);

        return mapToDto(user);
    }

    @Transactional
    public void resetPassword(Long userId, ResetPasswordDto dto) {

        User user = userRepository
                .findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    @Transactional
    public void changePassword(ChangePasswordDto dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new AuthenticationException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository
                .findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .status(user.getStatus())
                .role(user.getRole())
                .build();
    }
}
