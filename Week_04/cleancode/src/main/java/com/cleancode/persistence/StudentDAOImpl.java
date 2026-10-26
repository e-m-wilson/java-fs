package com.cleancode.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.cleancode.domain.Student;

public class StudentDAOImpl implements StudentDAO {

    private final String fileName;

    public StudentDAOImpl(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void addStudent(Student student) {
        List<Student> students = getAllStudents();

        students.add(student);

        saveAllStudents(students);
    }

    @Override
    public Student getStudentById(int id) {
        List<Student> students = getAllStudents();

        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }

        return null;
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();

        File file = new File(fileName);

        if (!file.exists()) {
            return students;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    students.add(Student.fromFileString(line));
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return students;
    }

    @Override
    public void updateStudent(Student updatedStudent) {
        List<Student> students = getAllStudents();

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == updatedStudent.getId()) {
                students.set(i, updatedStudent);
                break;
            }
        }

        saveAllStudents(students);
    }

    @Override
    public void deleteStudent(int id) {
        List<Student> students = getAllStudents();

        students.removeIf(student -> student.getId() == id);

        saveAllStudents(students);
    }

    private void saveAllStudents(List<Student> students) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(fileName))) {

            for (Student student : students) {
                writer.write(student.toFileString());
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}

