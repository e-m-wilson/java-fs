package com.cleancode.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cleancode.domain.Student;
import com.cleancode.persistence.StudentDAO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentServiceImplTest {

    private StudentDAO dao;
    private StudentService service;

    @BeforeEach
    void setUp() {
        dao = mock(StudentDAO.class);
        service = new StudentServiceImpl(dao);
    }

    @Test
    void addStudentStoresNewStudent() {
        Student student = student(1, "Ada", "Mathematics", 4.0);

        service.addStudent(student);

        verify(dao).addStudent(student);
    }

    @Test
    void addStudentRejectsDuplicateId() {
        Student existing = student(1, "Ada", "Mathematics", 4.0);
        when(dao.getStudentById(1)).thenReturn(existing);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addStudent(student(1, "Grace", "Computer Science", 3.9)));

        assertEquals("Student ID already exists", exception.getMessage());
        verify(dao).getStudentById(1);
    }

    @Test
    void findStudentReturnsExistingStudent() {
        Student student = student(1, "Ada", "Mathematics", 4.0);
        when(dao.getStudentById(1)).thenReturn(student);

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
        when(dao.getAllStudents()).thenReturn(List.of(first, second));

        assertIterableEquals(List.of(first, second), service.findAllStudents());
    }

    @Test
    void findAllStudentsReturnsEmptyListWhenThereAreNoStudents() {
        when(dao.getAllStudents()).thenReturn(List.of());

        assertEquals(List.of(), service.findAllStudents());
    }

    @Test
    void updateStudentChangesExistingStudent() {
        Student updated = student(1, "Ada Lovelace", "Computer Science", 4.0);
        when(dao.getStudentById(1)).thenReturn(student(1, "Ada", "Mathematics", 4.0));

        service.updateStudent(updated);

        verify(dao).updateStudent(updated);
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
        when(dao.getStudentById(1)).thenReturn(student(1, "Ada", "Mathematics", 4.0));

        service.deleteStudent(1);

        verify(dao).deleteStudent(1);
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

}