package com.spring.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.spring.demo.dao.StudentDAO;
import com.spring.demo.domain.Student;
import com.spring.demo.exceptions.StudentNotFoundException;

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

        Student student = studentDAO.findById(id);

        if(student == null) {
            throw new StudentNotFoundException("Student not found with id: " + id);
        }

        return student; 
    }

    @Transactional 
    public void deleteStudent(int id) {
        Student student = getStudentById(id);
        studentDAO.delete(student);
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsByLastName(String lastName) {
        return studentDAO.findByLastName(lastName);
    }

    // we don't need a call to DAO here
    // since this is in a transaction and existingStudent is a 
    // managed entitiy, any changes to it will be tracked
    // before the transaction closes, dirty checking will occur 
    // this will detect the change to the managed entity, and the 
    // changes will be auto commited 
    @Transactional 
    public Student updateStudent(int id, Student student) {
        Student existingStudent = getStudentById(id);

        existingStudent.setEmail(student.getEmail());
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());

        return existingStudent;
    }

}
