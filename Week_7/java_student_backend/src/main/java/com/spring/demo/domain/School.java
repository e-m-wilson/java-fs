package com.spring.demo.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "school")
public class School {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 20)
    @NotBlank 
    @Column(name = "name", length=20, nullable=false)
    private String name;

    @OneToMany(
        mappedBy = "school",
        fetch = FetchType.LAZY
    ) 
    @JsonIgnore 
    private List<Student> students = new ArrayList<>();

    public School() {}

    public School(String name) {
        this.name = name;
    }

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void addStudent(Student student) {
        this.students.add(student);
        student.setSchool(this);
    }

    public void removeStudent(Student student) {
        this.students.remove(student);
        student.setSchool(null);
    }
    

}
