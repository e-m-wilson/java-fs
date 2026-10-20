package com.spring.demo.dto;

import java.util.List;

public class AuthResponseDTO {

    private final String userName;
    private final List<String> roles;

    public AuthResponseDTO(String userName, List<String> roles) {
        this.userName = userName;
        this.roles = List.copyOf(roles);
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getRoles() {
        return roles;
    }
}