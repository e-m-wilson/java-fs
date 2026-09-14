package com.cleancode.service;

import com.cleancode.domain.Student;
import com.cleancode.persistence.StudentDAO;
import java.util.List;

public class StudentServiceImpl implements StudentService {
    private final StudentDAO studentDAO;

    public StudentServiceImpl(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    @Override
    public void addStudent(Student student) {
        if (studentDAO.getStudentById(student.getId()) != null) {
            throw new IllegalArgumentException("Student ID already exists");
        }

        studentDAO.addStudent(student);
    }

    @Override
    public Student findStudent(int id) {
        return studentDAO.getStudentById(id);
    }

    @Override
    public List<Student> findAllStudents() {
        return studentDAO.getAllStudents();
    }

    @Override
    public void updateStudent(Student student) {
        if (studentDAO.getStudentById(student.getId()) == null) {
            throw new IllegalArgumentException("Student not found");
        }

        studentDAO.updateStudent(student);
    }

    @Override
    public void deleteStudent(int id) {
        if (studentDAO.getStudentById(id) == null) {
            throw new IllegalArgumentException("Student not found");
        }

        studentDAO.deleteStudent(id);
    }
}
