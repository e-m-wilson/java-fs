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

}
