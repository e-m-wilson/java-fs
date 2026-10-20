package com.spring.demo.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.demo.domain.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findByLastName(String lastName);
    List<Student> findBySchool_Name(String name);
}
