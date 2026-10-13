package com.example.LTHD.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.LTHD.dto.request.AuthenticationRequest;
import com.example.LTHD.dto.response.ApiResponse;
import com.example.LTHD.dto.response.AuthenticateResponse;
import com.example.LTHD.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    ApiResponse<AuthenticateResponse> authenticate(@RequestBody AuthenticationRequest request) {
        ApiResponse<AuthenticateResponse> response = new ApiResponse<>();
        AuthenticateResponse result = authenticationService.authenticate(request);
        response.setData(result);
        return response;
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authorization) {

        authenticationService.logout(authorization);

        return new ApiResponse<>(
                200,
                "Đăng xuất thành công",
                null);
    }

}
