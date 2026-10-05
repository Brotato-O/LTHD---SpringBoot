package com.example.LTHD.dto.response;

import com.example.LTHD.common.Role;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;

    public UserResponse() {
    }

    public UserResponse(
            Long id,
            String username,
            String email,
            String fullName,
            Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}