package com.spring.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterAndLoginRequestDTO {

        @NotBlank 
        @Size(min = 3, max = 50) 
        private String userName;

        @NotBlank 
        @Size(min = 8, max = 100) 
        private String password;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

}