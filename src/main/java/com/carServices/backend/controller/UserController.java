package com.carServices.backend.controller;

import com.carServices.backend.dtos.*;
import com.carServices.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/me")
    public ResponseEntity<SingleResultDto<UserDto>> updateProfile(
            @RequestBody UpdateUserDto dto
    ) {
        return ResponseEntity.ok(
                new SingleResultDto<>(userService.updateCurrentUser(dto))
        );
    }

    @PutMapping("/me/password")
    public ResponseEntity<InformativeMessage> changePassword(
            @RequestBody ChangePasswordDto dto
    ) {
        userService.changePassword(dto);
        return ResponseEntity.ok(
                new InformativeMessage("Password updated successfully")
        );
    }
}