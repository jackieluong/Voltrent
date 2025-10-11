package com.hcmut.voltrent.controller;

import com.hcmut.voltrent.dtos.RestResponse;
import com.hcmut.voltrent.dtos.UserDto;
import com.hcmut.voltrent.dtos.request.LoginRequest;
import com.hcmut.voltrent.dtos.request.RegisterRequest;
import com.hcmut.voltrent.dtos.response.LoginResponse;
import com.hcmut.voltrent.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest){

        UserDto userDto = authService.register(registerRequest);

       RestResponse restResponse = RestResponse.builder()
               .code(HttpStatus.OK.value())
               .message("Register successfully")
               .data(userDto)
               .build();

       return ResponseEntity.ok(restResponse);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){

        LoginResponse loginResponse = authService.login(loginRequest);

        RestResponse restResponse = RestResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Login successfully")
                .data(loginResponse)
                .build();

        return ResponseEntity.ok(restResponse);
    }

}
