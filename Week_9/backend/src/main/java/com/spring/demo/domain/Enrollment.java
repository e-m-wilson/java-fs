package com.spring.demo.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity 
@Table(name="enrollment")
public class Enrollment {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // LocalDate type = YYYY-mm-dd "2026-10-02"
    @NotNull 
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "grade")
    private String grade;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="student_id", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="course_id", nullable = false)
    private Course course;

    public Enrollment () {
    }

    public Enrollment(Student student, Course course, LocalDate enrollmentDate, String grade)
    {
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.grade = grade;
    }

	public Integer getId() {
		return id;
	}

	public LocalDate getEnrollmentDate() {
		return enrollmentDate;
	}

	public void setEnrollmentDate(LocalDate enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

    
}
