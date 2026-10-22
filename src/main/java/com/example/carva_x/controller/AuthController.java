package com.example.carva_x.controller;

import com.example.carva_x.model.request.LoginResquest;
import com.example.carva_x.model.request.UserRegisterRequest;
import com.example.carva_x.model.response.ApiResponse;
import com.example.carva_x.model.response.LoginResponse;
import com.example.carva_x.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService userService;

    @PostMapping("register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Validated @RequestBody UserRegisterRequest userRegisterRequest) {

        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .result(true)
                .data(userService.registerUser(userRegisterRequest))
                .build());
    }

    @PostMapping("login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Validated @RequestBody LoginResquest loginResquest) {

        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .result(true)
                .data(userService.login(loginResquest))
                .build());
    }

}