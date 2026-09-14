package com.cleancode.persistence;

import java.util.List;

import com.cleancode.domain.Student;

public interface StudentDAO {

    void addStudent(Student student);

    Student getStudentById(int id);

    List<Student> getAllStudents();

    void updateStudent(Student student);

    void deleteStudent(int id);
}
