package com.example.LTHD.service;

import com.example.LTHD.dto.request.AuthenticationRequest;
import com.example.LTHD.dto.response.AuthenticateResponse;

public interface AuthenticationService {
    public AuthenticateResponse authenticate(AuthenticationRequest authenticationRequest);

    void logout(String token);
}
