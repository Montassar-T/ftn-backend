package com.ftn.backend.controller;

import com.ftn.backend.dtos.*;

import com.ftn.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User APIs")
public class UserController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<SingleResultDto<UserDto>> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateUserDto dto
    ) {

        String keycloakId = jwt.getSubject();

        return ResponseEntity.ok(
                new SingleResultDto<>(
                        userService.updateCurrentUser(keycloakId, dto)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<UserDto>> getUserById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                new SingleResultDto<>(
                        userService.getUserById(id)
                )
        );
    }

    @GetMapping
    public ResponseEntity<PageDto<UserDto>> getAllUsers(
            @RequestParam(required = false) Map<String, String> params
    ) {

        return ResponseEntity.ok(userService.getAllUsers(params));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InformativeMessage> deleteUser(
            @PathVariable Long id
    ) {

        userService.deleteUser(id);
        return ResponseEntity.ok(new InformativeMessage("User deleted successfully"));
    }
}