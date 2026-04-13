package com.carServices.backend.controller;

import com.carServices.backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    //    @GetMapping("/users/me")
    //    public ResponseEntity<SingleResultDto<UserDto>> getUserDetails(){
    //
    //    }

}
