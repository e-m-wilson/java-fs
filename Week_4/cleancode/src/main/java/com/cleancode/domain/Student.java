package com.cleancode.domain;

public class Student {
    
    private int id;
    private String name;
    private String major;
    private double gpa;

    public Student(int id, String name, String major, double gpa) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.gpa = gpa;
    }

    @Override 
    public String toString() {
        return String.format("ID: %d | Name: %s | Major: %s | GPA: %.2f", id, name, major, gpa);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public double getGpa() {
        return gpa;
    }

    public String toFileString() {
        return id + "," + name + "," + major + "," + gpa;
    }

    // 1,Name,Major,4.0
    public static Student fromFileString(String line) {
        String[] parts = line.split(",");

        return new Student(Integer.parseInt(parts[0]), parts[1], parts[2], Double.parseDouble(parts[3]));

    }
}
