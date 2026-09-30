package com.spring.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.spring.demo.dao.StudentDAO;
import com.spring.demo.domain.Student;

import org.springframework.transaction.annotation.Transactional;

@Service 
public class StudentService {

    private final StudentDAO studentDAO;
    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    public StudentService(@Qualifier("entityManagerStudentDAO") StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    @Transactional 
    public Student insertStudent(Student student) {
        return studentDAO.insert(student);
    }

    @Transactional(readOnly=true)
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudentById(int id) {
        return studentDAO.findById(id);
    }
}
