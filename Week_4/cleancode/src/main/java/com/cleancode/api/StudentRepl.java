package com.cleancode.api;

import com.cleancode.domain.Student;
import com.cleancode.service.StudentService;
import java.util.Scanner;

public class StudentRepl {
    private final StudentService service;
    private final Scanner scanner = new Scanner(System.in);

    public StudentRepl(StudentService service) {
        this.service = service;
    }

    public void run() {
        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();

            if (command.equals("exit")) {
                return;
            }

            try {
                handle(command);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handle(String command) {
        switch (command) {
            case "add" -> service.addStudent(readStudent());
            case "find" -> findStudent();
            case "list" -> service.findAllStudents()
                    .forEach(System.out::println);
            case "update" -> service.updateStudent(readStudent());
            case "delete" -> service.deleteStudent(readInt("Student ID: "));
            case "help" -> printHelp();
            default -> System.out.println("Unknown command");
        }
    }

    private void findStudent() {
        Student student = service.findStudent(readInt("Student ID: "));
        if (student == null) {
            System.out.println("Student not found");
            return;
        }

        System.out.println(student);
    }

    private Student readStudent() {
        int id = readInt("Student ID: ");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Major: ");
        String major = scanner.nextLine().trim();
        double gpa = readDouble("GPA: ");
        return new Student(id, name, major, gpa);
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        return Double.parseDouble(scanner.nextLine().trim());
    }

    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("add - Add a student");
        System.out.println("find - Find a student by ID");
        System.out.println("list - List all students");
        System.out.println("update - Update a student");
        System.out.println("delete - Delete a student by ID");
        System.out.println("help - Show this help message");
        System.out.println("exit - Exit the application");
    }
}
