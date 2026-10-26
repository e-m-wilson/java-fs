package org.example;

import io.bretty.console.table.Alignment;
import io.bretty.console.table.ColumnFormatter;
import io.bretty.console.table.Precision;
import io.bretty.console.table.Table;

import java.util.List;

public class Main {

    record Student(String name, int age, double grade) {}

    public static void main(String[] args) {

        List<Student> students = List.of(
                new Student("Alice", 20, 0.95),
                new Student("Bob", 22, 0.87),
                new Student("Charlie", 21, 0.92)
        );

        ColumnFormatter<String> nameFormatter =
                ColumnFormatter.text(Alignment.LEFT, 12);

        ColumnFormatter<Number> ageFormatter =
                ColumnFormatter.number(Alignment.RIGHT, 5, Precision.ZERO);

        ColumnFormatter<Number> gradeFormatter =
                ColumnFormatter.percentage(Alignment.RIGHT, 8, Precision.ONE);

        String[] names = students.stream()
                .map(Student::name)
                .toArray(String[]::new);

        Integer[] ages = students.stream()
                .map(Student::age)
                .toArray(Integer[]::new);

        Double[] grades = students.stream()
                .map(Student::grade)
                .toArray(Double[]::new);

        Table.Builder builder =
                new Table.Builder("Name", names, nameFormatter);

        builder.addColumn("Age", ages, ageFormatter);
        builder.addColumn("Grade", grades, gradeFormatter);

        Table table = builder.build();

        System.out.println(table);
    }
}
