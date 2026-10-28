package com.example.LTHD.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AuthenticationRequest {
    String email;
    @NotBlank(message = "Password không được để trống")
    String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
