package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseServiceTest {
    private DatabaseService dbService;

    @BeforeEach
    public void setUp() throws SQLException {
        dbService = new DatabaseService();
        dbService.init();
        dbService.clear();
    }

    @Test
    public void testElementsCount() throws SQLException {
        // Create a test set of 30 elements
        for (int i = 1; i <= 30; i++) {
            dbService.addElement(i, "Element " + i);
        }

        // Test the number of elements in the database
        int count = dbService.getCount();
        assertEquals(30, count, "The number of elements in the database should be 30");
        System.out.println("Test passed: Database contains " + count + " elements.");
    }
}
