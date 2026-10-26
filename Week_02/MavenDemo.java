package org.example;

import io.bretty.console.table.Alignment;
import io.bretty.console.table.ColumnFormatter;
import io.bretty.console.table.Precision;
import io.bretty.console.table.Table;

public class Main {

    public static void main(String[] args) {

        String[] names = {
                "Alice",
                "Bob",
                "Charlie"
        };

        Integer[] ages = {
                20,
                22,
                21
        };

        Double[] grades = {
                0.95,
                0.87,
                0.92
        };

        ColumnFormatter<String> nameFormatter =
                ColumnFormatter.text(Alignment.LEFT, 12);

        ColumnFormatter<Number> ageFormatter =
                ColumnFormatter.number(Alignment.RIGHT, 5, Precision.ZERO);

        ColumnFormatter<Number> gradeFormatter =
                ColumnFormatter.percentage(Alignment.RIGHT, 8, Precision.ONE);

        Table.Builder builder =
                new Table.Builder("Name", names, nameFormatter);

        builder.addColumn("Age", ages, ageFormatter);
        builder.addColumn("Grade", grades, gradeFormatter);

        Table table = builder.build();

        System.out.println(table);
    }
}
