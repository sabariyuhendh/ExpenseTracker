package com.expense.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.model.Category;
import com.model.Expense;
import com.expense.util.DatabaseConnection;

public class ExpenseDAO {
    private static final String INSERT_CATEGORY = "INSERT INTO Categories(name,description) VALUES (?,?)";
    private static final String GET_ALL_CATEGORY = "SELECT * FROM Category";
    private static final String GET_CATEGORY_BY_ID = "SELECT * FROM Category WHERE id=?";
    private static final String UPDATE_CATEGORY = "UPDATE Category SET name=?,description = ? WHERE id=?";
    private static final String DELETE_CATEGORY = "DELETE FROM Category WHERE id=?";
    private static final String INSERT_EXPENSE = "INSERT INTO Expense(category_id,payment_method,amount,description,expense_date,creates_at) VALUES (?,?,?,?,?,?)";
    private static final String GET_EXPENSE_BY_ID = "SELECT * FROM Expense WHERE id=?";
    private static final String DELETE_EXPENSE = "DELETE FROM Expense WHERE id=?";
    private static final String GET_ALL_EXPENSE = "SELECT * FROM Expense";
    private static final String UPDATE_EXPENSE = "UPDATE Expense SET category_id - ?, payment_method = ?,amount = ?,description = ?,expense_date=? WHERE expense_id=?";

    // Create a New Category
    public int createCategory(Category category) throws SQLException{
        try(
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_CATEGORY);
        ){
            stmt.setString(1,category.getCategoryname());
            stmt.setString(2, category.getCategorydescription());

            int rows =  stmt.executeUpdate();
            if(rows <=0){
                throw new SQLException("Error while inserting category");
            }
            try {
                ResultSet rs = stmt.getGeneratedKeys();
                if(rs.next()){
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return -1;
    }

    public boolean updateCategory(Category category) throws SQLException{
        try(
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_CATEGORY);
        ){
            stmt.setString(1,category.getCategoryname());
            stmt.setString(2, category.getCategorydescription());
            stmt.setInt(3,category.getCategoryid());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCategory(Category category) throws SQLException{
        try(
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_CATEGORY);
        ){
            stmt.setInt(1,category.getCategoryid());
            return stmt.executeUpdate() > 0;
        }
    }
    public List<Category> getAllCategories() throws SQLException{
        List<Category> categories = new ArrayList<>();
        try(
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(GET_ALL_CATEGORY);
                ResultSet rs = stmt.executeQuery();
        ){
            while (rs.next()){
                categories.add(getCategoryRow(rs));
            }
        }
        return categories;
    }

    public Category getCategoryRow(ResultSet rs) throws SQLException{
        return new Category(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }

    public int createExpense(Expense expense) throws SQLException{
        try(
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_EXPENSE);
        ){
                stmt.setInt(1, expense.getCategory_id());
                stmt.setString(2,expense.getPaymentMethod().toString());
                stmt.setDouble(3,expense.getAmount());
                stmt.setString(4,expense.getDescription());
                stmt.setTimestamp(5, Timestamp.valueOf(expense.getExpense_date()));
                stmt.setTimestamp(6, Timestamp.valueOf(expense.getCreated_at()));

                int rows =  stmt.executeUpdate();
                if(rows <=0){
                    throw new SQLException("Error while inserting expense");
                }
                try {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if(rs.next()){
                        return rs.getInt(1);
                    }
                }
                catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        }
        return -1;
    }

    public boolean updateExpense(Expense expense) throws SQLException{
        try(
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_EXPENSE);
        ){
            stmt.setInt(1, expense.getCategory_id());
            stmt.setString(2,expense.getPaymentMethod().toString());
            stmt.setDouble(3,expense.getAmount());
            stmt.setString(4,expense.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(expense.getExpense_date()));
            stmt.setTimestamp(6, Timestamp.valueOf(expense.getCreated_at()));
            int rows =  stmt.executeUpdate();
            if(rows <=0){
                throw new SQLException("Error while updating expense");
            }
            try {
                ResultSet rs = stmt.getGeneratedKeys();
                if(rs.next()){
                    return rs.getInt(1) == expense.getExpense_id();
                }
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public boolean deleteExpense(Expense expense) throws SQLException{
        try(
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_EXPENSE);

        ){
            stmt.setInt(1, expense.getExpense_id());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Expense> getAllExpenses() throws SQLException{
        List<Expense> expenses = new ArrayList<>();
        try(
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(GET_ALL_EXPENSE);
                ResultSet res = stmt.executeQuery();
        ){
            while (res.next()){
                expenses.add(getExpenseRow(res));
            }
        }
        return expenses;
    }
    private Expense getExpenseRow(ResultSet rs) throws SQLException{
        return new Expense(
                rs.getInt("expense_id"),
                rs.getInt("category_id"),
                rs.getString("payment_method"),
                rs.getString("amount"),
                rs.getString("description"),
                rs.getDate("expense_date"),
                rs.getDate("creates_at")
        );
    }
}
