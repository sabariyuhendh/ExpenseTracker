package com.expense.dao;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.model.Category;
import com.model.Expense;
import com.model.PaymentMethod;
import com.expense.util.DatabaseConnection;

public class MainDAO {
    // SQL queries for Categories table (matching your actual database schema)
    private static final String INSERT_CATEGORY = "INSERT INTO categories(name, description) VALUES (?, ?)";
    private static final String GET_ALL_CATEGORY = "SELECT * FROM categories";
    private static final String UPDATE_CATEGORY = "UPDATE categories SET name=?, description=? WHERE category_id=?";
    private static final String DELETE_CATEGORY = "DELETE FROM categories WHERE category_id=?";
    
    // SQL queries for Expenses table (matching your actual database schema)
    private static final String INSERT_EXPENSE = "INSERT INTO expenses(category_id, payment_method, amount, description, expense_date, created_at) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String DELETE_EXPENSE = "DELETE FROM expenses WHERE expense_id=?";
    private static final String GET_ALL_EXPENSE = "SELECT * FROM expenses";
    private static final String UPDATE_EXPENSE = "UPDATE expenses SET category_id=?, payment_method=?, amount=?, description=?, expense_date=? WHERE expense_id=?";

    /**
     * Creates a new category in the database
     * 
     * DETAILED FLOW EXPLANATION:
     * 1. GUI calls this method -> passes Category object with name and description
     * 2. DatabaseConnection.getDBConnection() -> creates TCP connection to MySQL server
     * 3. PreparedStatement created -> SQL query prepared with placeholders (?, ?)
     * 4. Statement.RETURN_GENERATED_KEYS -> tells MySQL to return auto-generated ID
     * 5. stmt.setString() -> fills placeholders with actual values from Category object
     * 6. stmt.executeUpdate() -> sends INSERT query to MySQL server
     * 7. MySQL server -> inserts row into categories table -> returns number of affected rows
     * 8. stmt.getGeneratedKeys() -> retrieves auto-generated category_id from MySQL
     * 9. ResultSet processed -> extracts the new ID and returns it to GUI
     * 10. Connection closed -> automatically by try-with-resources
     * 
     * WHERE IT FETCHES DATA:
     * - FROM: GUI form fields (name, description)
     * - TO: categories table in MySQL database
     * - BRINGS BACK: Auto-generated category_id from database
     * 
     * WHAT HAPPENS IN BACKGROUND:
     * - TCP connection established to localhost:3306
     * - MySQL server authenticates credentials
     * - SQL INSERT query executed: "INSERT INTO categories(name, description) VALUES (?, ?)"
     * - MySQL auto-increments category_id and returns it
     * - Connection automatically closed
     * 
     * @param category Category object with name and description from GUI form
     * @return Generated category ID from database, or -1 if failed
     */
    public int createCategory(Category category) throws SQLException {
        // STEP 1: Get database connection and prepare SQL statement
        // WHY: Need connection to MySQL server to execute INSERT query
        // HOW: DatabaseConnection creates TCP socket connection to localhost:3306
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS)) {
            
            // STEP 2: Fill SQL placeholders with actual values
            // WHY: SQL injection prevention - placeholders are safe
            // HOW: setString() escapes special characters and quotes
            stmt.setString(1, category.getCategoryname()); // First ? becomes category name
            stmt.setString(2, category.getCategorydescription()); // Second ? becomes description

            // STEP 3: Execute INSERT query on MySQL server
            // WHY: Actually insert the data into database
            // HOW: MySQL server processes INSERT and returns number of affected rows
            int rows = stmt.executeUpdate();
            if (rows <= 0) {
                throw new SQLException("Error while inserting category");
            }
            
            // STEP 4: Get auto-generated ID from MySQL
            // WHY: GUI needs the new category ID for future operations
            // HOW: MySQL returns the auto-incremented category_id
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return the new category_id
                }
            }
        }
        return -1; // Return -1 if no ID was generated
    }

    /**
     * Updates an existing category in the database
     * 
     * DETAILED FLOW EXPLANATION:
     * 1. GUI calls this method -> passes Category object with updated data and existing ID
     * 2. DatabaseConnection.getDBConnection() -> creates TCP connection to MySQL server
     * 3. PreparedStatement created -> SQL UPDATE query prepared with placeholders
     * 4. stmt.setString() -> fills placeholders with new name and description
     * 5. stmt.setInt() -> fills WHERE clause with existing category ID
     * 6. stmt.executeUpdate() -> sends UPDATE query to MySQL server
     * 7. MySQL server -> finds row by ID -> updates name and description -> returns affected rows
     * 8. Method returns true if 1+ rows updated, false if no rows found
     * 9. Connection closed -> automatically by try-with-resources
     * 
     * WHERE IT FETCHES DATA:
     * - FROM: GUI form fields (updated name, description) + table selection (existing ID)
     * - TO: categories table in MySQL database (specific row by ID)
     * - BRINGS BACK: Boolean success status (true/false)
     * 
     * WHAT HAPPENS IN BACKGROUND:
     * - TCP connection established to localhost:3306
     * - MySQL server authenticates credentials
     * - SQL UPDATE query executed: "UPDATE categories SET name=?, description=? WHERE category_id=?"
     * - MySQL finds row by category_id and updates name/description
     * - Returns number of affected rows (should be 1)
     * - Connection automatically closed
     * 
     * @param category Category object with updated information and existing ID
     * @return true if update successful (1+ rows affected), false otherwise
     */
    public boolean updateCategory(Category category) throws SQLException {
        // STEP 1: Get database connection and prepare UPDATE statement
        // WHY: Need connection to MySQL server to execute UPDATE query
        // HOW: DatabaseConnection creates TCP socket connection to localhost:3306
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CATEGORY)) {
            
            // STEP 2: Fill SQL placeholders with updated values
            // WHY: SQL injection prevention - placeholders are safe
            // HOW: setString() and setInt() escape special characters
            stmt.setString(1, category.getCategoryname()); // First ? becomes new name
            stmt.setString(2, category.getCategorydescription()); // Second ? becomes new description
            stmt.setInt(3, category.getCategoryid()); // Third ? becomes WHERE clause ID

            // STEP 3: Execute UPDATE query on MySQL server
            // WHY: Actually update the data in database
            // HOW: MySQL server finds row by ID and updates name/description
            // RETURNS: Number of affected rows (1 if successful, 0 if not found)
            return stmt.executeUpdate() > 0; // Return true if 1+ rows were updated
        }
    }

    /**
     * Deletes a category from the database
     * 
     * DETAILED FLOW EXPLANATION:
     * 1. GUI calls this method -> passes Category object with ID to delete
     * 2. DatabaseConnection.getDBConnection() -> creates TCP connection to MySQL server
     * 3. PreparedStatement created -> SQL DELETE query prepared with placeholder
     * 4. stmt.setInt() -> fills WHERE clause with category ID to delete
     * 5. stmt.executeUpdate() -> sends DELETE query to MySQL server
     * 6. MySQL server -> finds row by ID -> deletes row -> returns affected rows
     * 7. Method returns true if 1+ rows deleted, false if no rows found
     * 8. Connection closed -> automatically by try-with-resources
     * 
     * WHERE IT FETCHES DATA:
     * - FROM: GUI table selection (category ID from selected row)
     * - TO: categories table in MySQL database (specific row by ID)
     * - BRINGS BACK: Boolean success status (true/false)
     * 
     * WHAT HAPPENS IN BACKGROUND:
     * - TCP connection established to localhost:3306
     * - MySQL server authenticates credentials
     * - SQL DELETE query executed: "DELETE FROM categories WHERE category_id=?"
     * - MySQL finds row by category_id and deletes it
     * - Returns number of affected rows (should be 1)
     * - Connection automatically closed
     * 
     * @param category Category object with ID to delete
     * @return true if deletion successful (1+ rows affected), false otherwise
     */
    public boolean deleteCategory(Category category) throws SQLException {
        // STEP 1: Get database connection and prepare DELETE statement
        // WHY: Need connection to MySQL server to execute DELETE query
        // HOW: DatabaseConnection creates TCP socket connection to localhost:3306
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_CATEGORY)) {
            
            // STEP 2: Fill SQL placeholder with category ID
            // WHY: SQL injection prevention - placeholders are safe
            // HOW: setInt() escapes the ID value safely
            stmt.setInt(1, category.getCategoryid()); // ? becomes WHERE clause ID

            // STEP 3: Execute DELETE query on MySQL server
            // WHY: Actually delete the data from database
            // HOW: MySQL server finds row by ID and deletes it
            // RETURNS: Number of affected rows (1 if successful, 0 if not found)
            return stmt.executeUpdate() > 0; // Return true if 1+ rows were deleted
        }
    }
    /**
     * Retrieves all categories from the database
     * 
     * DETAILED FLOW EXPLANATION:
     * 1. GUI calls this method -> needs all categories for dropdown/table display
     * 2. DatabaseConnection.getDBConnection() -> creates TCP connection to MySQL server
     * 3. PreparedStatement created -> SQL SELECT query prepared
     * 4. stmt.executeQuery() -> sends SELECT query to MySQL server
     * 5. MySQL server -> executes SELECT * FROM categories -> returns all rows
     * 6. ResultSet created -> contains all category data from database
     * 7. while(rs.next()) -> loops through each row in ResultSet
     * 8. getCategoryRow() -> converts each database row to Category object
     * 9. Category objects added to List -> returned to GUI
     * 10. Connection closed -> automatically by try-with-resources
     * 
     * WHERE IT FETCHES DATA:
     * - FROM: categories table in MySQL database (all rows)
     * - TO: GUI dropdowns and tables for display
     * - BRINGS BACK: List of Category objects with all category data
     * 
     * WHAT HAPPENS IN BACKGROUND:
     * - TCP connection established to localhost:3306
     * - MySQL server authenticates credentials
     * - SQL SELECT query executed: "SELECT * FROM categories"
     * - MySQL returns all rows with category_id, name, description
     * - ResultSet processed row by row
     * - Each row converted to Category object
     * - List of Category objects returned to GUI
     * - Connection automatically closed
     * 
     * @return List of all Category objects from database
     */
    public List<Category> getAllCategories() throws SQLException {
        // STEP 1: Create empty list to store Category objects
        // WHY: Need collection to hold multiple Category objects
        // HOW: ArrayList provides dynamic array functionality
        List<Category> categories = new ArrayList<>();
        
        // STEP 2: Get database connection and execute SELECT query
        // WHY: Need connection to MySQL server to execute SELECT query
        // HOW: DatabaseConnection creates TCP socket connection to localhost:3306
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_CATEGORY);
             ResultSet rs = stmt.executeQuery()) {
            
            // STEP 3: Process each row from database
            // WHY: Convert database rows to Category objects
            // HOW: rs.next() moves to next row, returns false when no more rows
            while (rs.next()) {
                // STEP 4: Convert database row to Category object
                // WHY: GUI needs Category objects, not raw database data
                // HOW: getCategoryRow() extracts data from ResultSet and creates Category
                categories.add(getCategoryRow(rs));
            }
        }
        // STEP 5: Return list of Category objects to GUI
        // WHY: GUI needs Category objects for dropdowns and tables
        // HOW: List contains all categories from database
        return categories;
    }

    /**
     * Helper method to create Category object from ResultSet row
     * Flow: Receives ResultSet -> extracts data -> creates and returns Category object
     * @param rs ResultSet pointing to current row
     * @return Category object with data from current row
     */
    private Category getCategoryRow(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("category_id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }

    /**
     * Creates a new expense in the database
     * Flow: Receives Expense object -> inserts into Expense table -> returns generated ID
     * @param expense Expense object with all required data
     * @return Generated expense ID or -1 if failed
     */
    public int createExpense(Expense expense) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_EXPENSE, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, expense.getCategory_id());
            stmt.setString(2, expense.getPaymentMethod().toString());
            stmt.setBigDecimal(3, BigDecimal.valueOf(expense.getAmount()));
            stmt.setString(4, expense.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(expense.getExpense_date()));
            stmt.setTimestamp(6, Timestamp.valueOf(expense.getCreated_at()));

            int rows = stmt.executeUpdate();
            if (rows <= 0) {
                throw new SQLException("Error while inserting expense");
            }
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Updates an existing expense in the database
     * Flow: Receives updated Expense object -> updates Expense table -> returns success status
     * @param expense Expense object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateExpense(Expense expense) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_EXPENSE)) {
            
            stmt.setInt(1, expense.getCategory_id());
            stmt.setString(2, expense.getPaymentMethod().toString());
            stmt.setBigDecimal(3, BigDecimal.valueOf(expense.getAmount()));
            stmt.setString(4, expense.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(expense.getExpense_date()));
            stmt.setInt(6, expense.getExpense_id());
            
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an expense from the database
     * Flow: Receives Expense object with ID -> deletes from Expense table -> returns success status
     * @param expense Expense object with ID to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteExpense(Expense expense) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_EXPENSE)) {
            
            stmt.setInt(1, expense.getExpense_id());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all expenses from the database
     * Flow: Queries Expense table -> processes ResultSet -> returns List of Expense objects
     * @return List of all expenses in the database
     */
    public List<Expense> getAllExpenses() throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_EXPENSE);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                expenses.add(getExpenseRow(rs));
            }
        }
        return expenses;
    }
    /**
     * Helper method to create Expense object from ResultSet row
     * Flow: Receives ResultSet -> extracts data -> creates and returns Expense object
     * @param rs ResultSet pointing to current row
     * @return Expense object with data from current row
     */
    private Expense getExpenseRow(ResultSet rs) throws SQLException {
        Expense expense = new Expense(
                rs.getInt("expense_id"),
                rs.getInt("category_id"),
                PaymentMethod.valueOf(rs.getString("payment_method")), // Convert string to enum
                rs.getBigDecimal("amount").intValue() // Convert decimal to int
        );
        
        // Set additional fields
        expense.setDescription(rs.getString("description"));
        expense.setExpense_date(rs.getTimestamp("expense_date").toLocalDateTime());
        expense.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
        
        return expense;
    }
}
