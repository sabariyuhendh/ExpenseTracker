package com.expense;

import com.expense.gui.MainGUI;
import com.expense.util.DatabaseConnection;

import java.sql.Connection;
import javax.swing.*;

/**
 * Main entry point for the Expense Tracker application
 * 
 * FLOW DETAILED EXPLANATION:
 * 1. Application starts -> main() method is called by JVM
 * 2. Database connection test -> ensures database is accessible before GUI loads
 * 3. UI Look and Feel setup -> makes application look native to operating system
 * 4. GUI creation on EDT -> ensures thread-safe GUI creation
 * 5. MainGUI constructor -> initializes all components and event listeners
 * 
 * WHY THIS FLOW:
 * - Database test first prevents GUI from loading if database is unavailable
 * - Look and feel setup makes application look professional and native
 * - EDT (Event Dispatch Thread) ensures GUI operations are thread-safe
 * - Lambda expression simplifies the GUI creation code
 */
public class Main {
    /**
     * Main method - entry point of the application
     * 
     * INVOCATION FLOW:
     * 1. JVM calls main() when application starts
     * 2. Database connection test using try-with-resources (auto-closes connection)
     * 3. If database fails -> System.exit(1) terminates application
     * 4. UIManager setup -> changes appearance to match OS (Windows/Mac/Linux)
     * 5. SwingUtilities.invokeLater() -> schedules GUI creation on EDT
     * 6. Lambda expression -> creates MainGUI instance and makes it visible
     * 
     * WHY EACH STEP:
     * - Database test first: Prevents user from seeing broken GUI if database is down
     * - Try-with-resources: Automatically closes database connection, prevents memory leaks
     * - System.exit(1): Clean termination with error code for debugging
     * - UIManager: Makes application look professional and native to user's OS
     * - EDT: Swing requires all GUI operations on Event Dispatch Thread for thread safety
     * - Lambda: Simplifies code and makes it more readable than anonymous inner class
     */
    public static void main(String[] args) {
        // STEP 1: Test database connection before starting GUI
        // WHY: Prevents GUI from loading if database is unavailable
        // HOW: Uses try-with-resources to automatically close connection
        // INVOKES: DatabaseConnection.getDBConnection() -> creates connection to MySQL
        try (Connection cn = DatabaseConnection.getDBConnection()) {
            System.out.println("Connected to database successfully");
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
            System.exit(1); // Clean termination with error code
        }
        
        // STEP 2: Set system look and feel for better UI appearance
        // WHY: Makes application look native to user's operating system
        // HOW: UIManager changes the visual appearance of Swing components
        // INVOKES: UIManager.getSystemLookAndFeelClassName() -> gets OS-specific look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
        
        // STEP 3: Launch GUI on Event Dispatch Thread using lambda expression
        // WHY: Swing requires all GUI operations on EDT for thread safety
        // HOW: SwingUtilities.invokeLater() schedules GUI creation on EDT
        // INVOKES: MainGUI constructor -> initializes all components and event listeners
        SwingUtilities.invokeLater(() -> {
            try {
                new MainGUI().setVisible(true); // Creates MainGUI and makes it visible
            } catch (Exception e) {
                System.err.println("Failed to start GUI: " + e.getMessage());
            }
        });
    }
}
