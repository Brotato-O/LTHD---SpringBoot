package com.example.LTHD.dto.response;

public class AuthenticateResponse {
    String token;
    boolean authenticated;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public AuthenticateResponse() {
    }

    public AuthenticateResponse(String token, boolean authenticated) {
        this.token = token;
        this.authenticated = authenticated;
    }

}
