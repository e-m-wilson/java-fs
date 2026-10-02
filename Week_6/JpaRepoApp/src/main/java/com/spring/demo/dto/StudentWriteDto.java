package com.spring.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class StudentWriteDto {

    // when converting between java and json
    // we can make requests as 'student_id' instead of 'studentId'
    @JsonProperty("school_id")
    private Integer schoolId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @NotBlank 
    @Email 
    private String email;

    public Integer getSchoolId() {
        return schoolId;
    }
    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
