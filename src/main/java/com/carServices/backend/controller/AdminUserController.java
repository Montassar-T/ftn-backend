package com.carServices.backend.controller;

import com.carServices.backend.dtos.*;
import com.carServices.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageDto<UserDto>> getAllUsers(
            @RequestParam Map<String, String> params) {

        return userService.getAllUsers(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<UserDto>> getUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                new SingleResultDto<>(userService.getUserById(id))
        );
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<UserDto>> createUser(
            @RequestBody NewUserDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SingleResultDto<>(userService.createUser(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<UserDto>> updateUser(
            @PathVariable Long id,
            @RequestBody NewUserDto dto) {

        return ResponseEntity.ok(
                new SingleResultDto<>(userService.updateUser(id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InformativeMessage> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                new InformativeMessage("User deleted successfully")
        );
    }
}