package com.spring.demo.dao;

import com.spring.demo.domain.Student;
import java.util.List;

public interface StudentDAO {

    Student insert(Student student);
    List<Student> findAll();
    Student findById(int id);
    void delete(Student student);
    List<Student> findByLastName(String lastName);

}
