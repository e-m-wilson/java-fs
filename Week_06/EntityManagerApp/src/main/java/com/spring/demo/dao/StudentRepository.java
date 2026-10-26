package com.spring.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.demo.domain.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

}
