package com.spring.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AssignRoleRequestDTO {

    @NotBlank
    @Size(min = 3, max = 50)
    private String userName;

    @NotBlank
    @Size(max = 50)
    private String role;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}