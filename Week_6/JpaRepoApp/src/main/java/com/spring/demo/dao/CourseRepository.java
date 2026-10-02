package com.spring.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.demo.domain.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {

}