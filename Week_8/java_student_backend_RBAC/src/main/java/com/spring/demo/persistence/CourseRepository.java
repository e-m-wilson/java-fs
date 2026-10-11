package com.spring.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.demo.domain.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {

}