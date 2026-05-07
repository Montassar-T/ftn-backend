package com.ftn.backend.controller;

import com.ftn.backend.dtos.*;
import com.ftn.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User APIs")
public class UserController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<SingleResultDto<UserDto>> updateProfile(@RequestBody UpdateUserDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(userService.updateCurrentUser(dto)));
    }

    @PutMapping("/password")
    public ResponseEntity<InformativeMessage> changePassword(@RequestBody ChangePasswordDto dto) {
        userService.changePassword(dto);
        return ResponseEntity.ok(new InformativeMessage("Password updated successfully"));
    }
}
