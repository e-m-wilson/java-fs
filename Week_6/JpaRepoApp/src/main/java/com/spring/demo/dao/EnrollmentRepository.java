package com.spring.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.demo.domain.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

}
