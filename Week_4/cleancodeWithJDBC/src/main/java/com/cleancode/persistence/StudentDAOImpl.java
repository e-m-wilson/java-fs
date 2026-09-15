package com.cleancode.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cleancode.domain.Student;

public class StudentDAOImpl implements StudentDAO {

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                major VARCHAR(255) NOT NULL,
                gpa DOUBLE PRECISION NOT NULL
            )
            """;
    private static final String INSERT_SQL = "INSERT INTO students (id, name, major, gpa) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, major, gpa FROM students WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT id, name, major, gpa FROM students ORDER BY id";
    private static final String UPDATE_SQL = "UPDATE students SET name = ?, major = ?, gpa = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM students WHERE id = ?";

    public StudentDAOImpl() {
        initializeSchema();
    }

    @Override
    public void addStudent(Student student) {
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setInt(1, student.getId());
            statement.setString(2, student.getName());
            statement.setString(3, student.getMajor());
            statement.setDouble(4, student.getGpa());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw databaseError("Could not add student", e);
        }
    }

    @Override
    public Student getStudentById(int id) {
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapStudent(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw databaseError("Could not find student", e);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(mapStudent(resultSet));
            }
            return students;
        } catch (SQLException e) {
            throw databaseError("Could not list students", e);
        }
    }

    @Override
    public void updateStudent(Student updatedStudent) {
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, updatedStudent.getName());
            statement.setString(2, updatedStudent.getMajor());
            statement.setDouble(3, updatedStudent.getGpa());
            statement.setInt(4, updatedStudent.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw databaseError("Could not update student", e);
        }
    }

    @Override
    public void deleteStudent(int id) {
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw databaseError("Could not delete student", e);
        }
    }

    private void initializeSchema() {
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
                PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_SQL)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw databaseError("Could not initialize database schema", e);
        }
    }

    private Student mapStudent(ResultSet resultSet) throws SQLException {
        return new Student(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("major"),
                resultSet.getDouble("gpa"));
    }

    private IllegalStateException databaseError(String message, SQLException cause) {
        return new IllegalStateException(message, cause);
    }
}
