package com.expense.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection utility class for managing MySQL database connections
 * 
 * FLOW DETAILED EXPLANATION:
 * 1. Class loads -> static block executes -> MySQL driver is registered
 * 2. getDBConnection() called -> DriverManager creates connection using URL, username, password
 * 3. Connection returned -> used by DAO classes for database operations
 * 4. Connection closed -> automatically by try-with-resources in calling methods
 * 
 * WHY THIS DESIGN:
 * - Static methods: No need to create instances, can be called directly
 * - Static block: Ensures MySQL driver is loaded when class is first accessed
 * - Connection pooling: Each call creates fresh connection (simple approach)
 * - Exception handling: SQLException thrown to caller for proper error handling
 */
public class DatabaseConnection {
    // MySQL JDBC driver class name
    // WHY: Required to register MySQL driver with DriverManager
    // HOW: Class.forName() loads the driver class into JVM
    public static final String driver = "com.mysql.cj.jdbc.Driver";
    
    // Database connection URL
    // WHY: Tells DriverManager where to connect (localhost, port 3306, database name)
    // FORMAT: jdbc:mysql://host:port/database_name
    public static final String url = "jdbc:mysql://localhost:3306/ExpenseTracker";
    
    // Database credentials
    // WHY: Required for MySQL authentication
    // SECURITY: In production, these should be in configuration files
    public static final String username = "root";
    public static final String password = "9345";
    
    /**
     * Static initialization block - executes when class is first loaded
     * 
     * INVOCATION FLOW:
     * 1. JVM loads DatabaseConnection class -> static block executes automatically
     * 2. Class.forName(driver) -> loads MySQL JDBC driver into JVM memory
     * 3. Driver registers itself with DriverManager -> available for connections
     * 
     * WHY STATIC BLOCK:
     * - Executes once when class loads, not every time method is called
     * - Ensures driver is registered before any connection attempts
     * - Prevents "No suitable driver found" errors
     * 
     * WHAT HAPPENS IN BACKGROUND:
     * - MySQL JDBC driver class is loaded into JVM
     * - Driver's static initialization registers it with DriverManager
     * - DriverManager can now create MySQL connections
     */
    static {
        try {
            Class.forName(driver); // Loads and registers MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found"); // MySQL JDBC driver not in classpath
        }
    }
    
    /**
     * Creates and returns a new database connection
     * 
     * INVOCATION FLOW:
     * 1. DAO method calls getDBConnection() -> needs database connection
     * 2. DriverManager.getConnection() -> uses registered drivers to create connection
     * 3. MySQL driver creates TCP connection to localhost:3306
     * 4. Database authenticates using username/password
     * 5. Connection object returned -> used for SQL operations
     * 6. Connection closed -> by try-with-resources in calling method
     * 
     * WHY THIS METHOD:
     * - Static: No need to create DatabaseConnection instance
     * - Throws SQLException: Caller can handle database connection errors
     * - Fresh connection: Each call creates new connection (simple approach)
     * 
     * WHAT HAPPENS IN BACKGROUND:
     * - DriverManager checks all registered drivers
     * - MySQL driver recognizes the JDBC URL format
     * - Driver creates TCP socket connection to MySQL server
     * - Server authenticates credentials and establishes session
     * - Connection object wraps the TCP socket for SQL operations
     * 
     * @return Connection object for database operations
     * @throws SQLException if connection fails (server down, wrong credentials, etc.)
     */
    public static Connection getDBConnection() throws SQLException {
        // INVOKES: DriverManager.getConnection() -> creates connection using registered drivers
        // WHY: DriverManager automatically selects appropriate driver based on URL
        // HOW: MySQL driver creates TCP connection to localhost:3306
        return DriverManager.getConnection(url, username, password);
    }
}
