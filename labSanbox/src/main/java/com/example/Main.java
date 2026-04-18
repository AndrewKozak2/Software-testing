package com.example;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DatabaseService dbService = new DatabaseService();
        try {
            System.out.println("Initializing database...");
            dbService.init();
            dbService.clear();

            System.out.println("Creating a test set of 30 elements...");
            for (int i = 1; i <= 30; i++) {
                dbService.addElement(i, "Test Element " + i);
            }

            int count = dbService.getCount();
            System.out.println("Total elements in database: " + count);

            if (count == 30) {
                System.out.println("Test PASSED: 30 elements successfully created.");
            } else {
                System.out.println("Test FAILED: Expected 30 elements, found " + count);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
