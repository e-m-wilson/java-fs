package com.cleancode.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cleancode.domain.Student;
import com.cleancode.persistence.StudentDAO;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentServiceImplTest {

    private InMemoryStudentDAO dao;
    private StudentService service;

    @BeforeEach
    void setUp() {
        dao = new InMemoryStudentDAO();
        service = new StudentServiceImpl(dao);
    }

    @Test
    void addStudentStoresNewStudent() {
        Student student = student(1, "Ada", "Mathematics", 4.0);

        service.addStudent(student);

        assertEquals(student, dao.getStudentById(1));
    }

    @Test
    void addStudentRejectsDuplicateId() {
        Student existing = student(1, "Ada", "Mathematics", 4.0);
        dao.addStudent(existing);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addStudent(student(1, "Grace", "Computer Science", 3.9)));

        assertEquals("Student ID already exists", exception.getMessage());
        assertEquals(existing, dao.getStudentById(1));
    }

    @Test
    void findStudentReturnsExistingStudent() {
        Student student = student(1, "Ada", "Mathematics", 4.0);
        dao.addStudent(student);

        assertEquals(student, service.findStudent(1));
    }

    @Test
    void findStudentReturnsNullWhenStudentDoesNotExist() {
        assertNull(service.findStudent(999));
    }

    @Test
    void findAllStudentsReturnsAllStudentsInIdOrder() {
        Student first = student(1, "Ada", "Mathematics", 4.0);
        Student second = student(2, "Grace", "Computer Science", 3.9);
        dao.addStudent(second);
        dao.addStudent(first);

        assertIterableEquals(List.of(first, second), service.findAllStudents());
    }

    @Test
    void findAllStudentsReturnsEmptyListWhenThereAreNoStudents() {
        assertEquals(List.of(), service.findAllStudents());
    }

    @Test
    void updateStudentChangesExistingStudent() {
        dao.addStudent(student(1, "Ada", "Mathematics", 4.0));
        Student updated = student(1, "Ada Lovelace", "Computer Science", 4.0);

        service.updateStudent(updated);

        assertEquals(updated, dao.getStudentById(1));
    }

    @Test
    void updateStudentRejectsUnknownStudent() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateStudent(student(999, "Unknown", "History", 2.0)));

        assertEquals("Student not found", exception.getMessage());
    }

    @Test
    void deleteStudentRemovesExistingStudent() {
        dao.addStudent(student(1, "Ada", "Mathematics", 4.0));

        service.deleteStudent(1);

        assertNull(dao.getStudentById(1));
    }

    @Test
    void deleteStudentRejectsUnknownStudent() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteStudent(999));

        assertEquals("Student not found", exception.getMessage());
    }

    private Student student(int id, String name, String major, double gpa) {
        return new Student(id, name, major, gpa);
    }

    private static class InMemoryStudentDAO implements StudentDAO {
        private final List<Student> students = new ArrayList<>();

        @Override
        public void addStudent(Student student) {
            students.add(student);
        }

        @Override
        public Student getStudentById(int id) {
            return students.stream()
                    .filter(student -> student.getId() == id)
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Student> getAllStudents() {
            return students.stream()
                    .sorted(Comparator.comparingInt(Student::getId))
                    .toList();
        }

        @Override
        public void updateStudent(Student updatedStudent) {
            for (int index = 0; index < students.size(); index++) {
                if (students.get(index).getId() == updatedStudent.getId()) {
                    students.set(index, updatedStudent);
                    return;
                }
            }
        }

        @Override
        public void deleteStudent(int id) {
            students.removeIf(student -> student.getId() == id);
        }
    }
}