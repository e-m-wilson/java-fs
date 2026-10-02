package com.spring.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity 
@Table(name="student")
public class Student {

    /*
        Strategy Types
        IDENTITY - uses the datase auto-increment/identity column
        SEQUENCE - use the dataase provided sequence
        TABLE - JPA uses a table to simulate a sequence
        AUTO - JPA/provider chooses the strategoy automatically     
    */

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne 
    @JoinColumn(name = "school_id")
    private School school;

    @Size(max=20)
    @Column(name="first_name", length = 45)
    private String firstName;

    @Size(max=20)
    @Column(name="last_name", length = 45)
    private String lastName;

    // worth being aware of 
    // @NotNull
    // @NotBlank
    // @NotEmpty
    // @Size - for strings
    // @Min - for numbers 
    // @Max - for numbers 
    // @Positive
    // @PositiveOrZero
    // @Email
    // @Pattern

    // by default nullable is true 
    @Email 
    @NotBlank // ensure not null, empty, or whitespace
    @Column(name="email", length=45, nullable = false, unique = true)
    private String email;

    public Student(String firstName, String lastName, @Email @NotBlank String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // required for hibernate 
    public Student() {}

    public Integer getId() {
        return id;
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

    public School getSchool() {
        return this.school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    
}
