package com.cleancode.service;

import com.cleancode.domain.Student;
import java.util.List;

public interface StudentService {
    void addStudent(Student student);
    Student findStudent(int id);
    List<Student> findAllStudents();
    void updateStudent(Student student);
    void deleteStudent(int id);
}
