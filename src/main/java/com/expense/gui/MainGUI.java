package com.expense.gui;

import javax.swing.*;
import com.expense.dao.MainDAO;
import com.model.Category;
import com.model.Expense;
import com.model.PaymentMethod;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.util.Date;


public class MainGUI extends JFrame {
    // Attributes
    private MainDAO mainDAO;
    private JPanel panel;
    private JButton category,expense;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton addCategory, deleteCategory, updateCategory;
    
    // Expense form fields - same pattern as category
    private JComboBox<Category> categoryCombo;
    private JComboBox<PaymentMethod> paymentCombo;
    private JTextField amountField;
    private JTextArea expenseDescriptionArea;
    private JSpinner dateSpinner;
    private JTable expenseTable;
    private DefaultTableModel expenseTableModel;
    private JButton addExpense, deleteExpense, updateExpense;

    // Constructor
    public MainGUI() {
        mainDAO = new MainDAO();
        initializeComponents();
        setupComponents();
        setupEventListeners();
    }

    /**
     * Clears the category form fields and refreshes the category table
     * This method is called after successful operations to reset the form
     */
    private void clearCategoryForm() {
        nameField.setText("");
        descriptionArea.setText("");
        loadCategories();
    }
    

    private void initializeComponents() {
        setTitle("Expense Tracker");
        setVisible(true);
        setSize(1920,1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupComponents() {
        setLayout(new BorderLayout());

        panel = new JPanel(new GridBagLayout()); // centers content both ways
        panel.setBackground(Color.BLACK);

        category = new JButton("Category");
        expense = new JButton("Expense");
        category.setPreferredSize(new Dimension(150,50));
        expense.setPreferredSize(new Dimension(150,50));
        category.setFont(new Font("Serif", Font.BOLD, 20));
        expense.setFont(new Font("Serif", Font.BOLD, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // spacing between buttons

        // Add first button
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(category, gbc);

        // Add second button
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(expense, gbc);

        add(panel, BorderLayout.CENTER);
    }

    /**
     * Creates and displays the Category management window
     * This method sets up the UI for adding, updating, and deleting categories
     * Flow: User clicks Category button -> This method creates new JFrame -> Sets up form fields and table -> Loads existing categories
     */
    private void Category() {
        JFrame frame = new JFrame("Category");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Label - Name
        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(nameLabel, gbc);

        // Text field - Name
        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(nameField, gbc);

        // Label - Description
        JLabel descLabel = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        frame.add(descLabel, gbc);

        // Text area - Description
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(scrollPane, gbc);

        // Table model
        String[] columnNames = {"ID", "Category", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // no direct editing in table
            }
        };

        categoryTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(categoryTable);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(tableScroll, gbc);

        // Buttons
        addCategory = new JButton("Add Category");
        deleteCategory = new JButton("Delete Category");
        updateCategory = new JButton("Update Category");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(addCategory);
        buttonPanel.add(deleteCategory);
        buttonPanel.add(updateCategory);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(buttonPanel, gbc);
        
        // Set up event listeners for category operations
        setupCategoryEvent();
        
        // Add table selection listener to load selected category into form
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCategory();
            }
        });
        
        // Load existing categories into the table
        loadCategories();
        
        frame.setVisible(true);
    }
    /**
     * Sets up event listeners for category management buttons using lambda expressions
     * Each button triggers its corresponding method when clicked
     */
    private void setupCategoryEvent() {
        addCategory.addActionListener(e -> addCategory());
        deleteCategory.addActionListener(e -> deleteCategory());
        updateCategory.addActionListener(e -> updateCategory());
    }
    /**
     * Adds a new category to the database
     * Flow: User fills form -> clicks Add -> validates input -> creates Category object -> calls DAO -> refreshes table
     */
    private void addCategory() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (name.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return;
        }
        
        try {
            Category category = new Category(0, name, description); // ID will be set by database
            int categoryId = mainDAO.createCategory(category);
            if (categoryId > 0) {
                JOptionPane.showMessageDialog(this, "Category added successfully!");
                clearCategoryForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add category");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
    /**
     * Deletes the selected category from the database
     * Flow: User selects row -> clicks Delete -> confirms deletion -> calls DAO delete method -> refreshes table
     */
    private void deleteCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete");
            return;
        }
        
        int id = (int) categoryTable.getValueAt(row, 0);
        String name = (String) categoryTable.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete category: " + name + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Category category = new Category(id, name, "");
                if (mainDAO.deleteCategory(category)) {
                    JOptionPane.showMessageDialog(this, "Category deleted successfully!");
                    loadCategories();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete category");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage());
            }
        }
    }
    /**
     * Updates the selected category with new information
     * Flow: User selects row -> modifies form fields -> clicks Update -> validates input -> calls DAO update method -> refreshes table
     */
    private void updateCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to update");
            return;
        }
        
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (name.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return;
        }
        
        try {
            int id = (int) categoryTable.getValueAt(row, 0);
            Category category = new Category(id, name, description);
            if (mainDAO.updateCategory(category)) {
                JOptionPane.showMessageDialog(this, "Category updated successfully!");
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update category");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage());
        }
    }
    
    /**
     * Loads all categories from database and populates the table
     * Flow: Called on window open and after CRUD operations -> fetches data from DAO -> clears table -> adds rows
     */
    private void loadCategories() {
        try {
            List<Category> categories = mainDAO.getAllCategories();
            tableModel.setRowCount(0); // Clear existing rows
            
            categories.forEach(category -> {
                Object[] row = {
                    category.getCategoryid(),
                    category.getCategoryname(),
                    category.getCategorydescription()
                };
                tableModel.addRow(row);
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Loads the selected category data into the form fields for editing
     * Flow: User selects table row -> this method populates form fields with selected category data
     */
    private void loadSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row != -1) {
            String name = (String) categoryTable.getValueAt(row, 1);
            String description = (String) categoryTable.getValueAt(row, 2);
                nameField.setText(name);
                descriptionArea.setText(description);
        }
    }

    /**
     * Creates and displays the Expense management window
     * This method sets up the UI for adding, updating, and deleting expenses
     * Flow: User clicks Expense button -> This method creates new JFrame -> Sets up form fields and table -> Loads existing expenses
     */
    private void Expense() {
        JFrame frame = new JFrame("Expense Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Category selection
        JLabel categoryLabel = new JLabel("Category:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(categoryLabel, gbc);

        categoryCombo = new JComboBox<Category>() {
            @Override
            public String toString() {
                Category cat = (Category) getSelectedItem();
                return cat != null ? cat.getCategoryname() : "";
            }
        };
        // Set custom renderer to display category names properly
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    setText(((Category) value).getCategoryname());
                }
                return this;
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(categoryCombo, gbc);

        // Payment method
        JLabel paymentLabel = new JLabel("Payment Method:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(paymentLabel, gbc);

        paymentCombo = new JComboBox<>(PaymentMethod.values());
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(paymentCombo, gbc);

        // Amount
        JLabel amountLabel = new JLabel("Amount:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(amountLabel, gbc);

        amountField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(amountField, gbc);

        // Description
        JLabel descLabel = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        frame.add(descLabel, gbc);

        expenseDescriptionArea = new JTextArea(3, 20);
        expenseDescriptionArea.setLineWrap(true);
        expenseDescriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(expenseDescriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(descScrollPane, gbc);

        // Expense date and time picker
        JLabel dateLabel = new JLabel("Expense Date & Time:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(dateLabel, gbc);

        // Create date/time spinner with current time as default
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateModel.setValue(new Date()); // Set current date/time as default
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm");
        dateSpinner.setEditor(dateEditor);
        gbc.gridx = 1;
        gbc.gridy = 4;
        frame.add(dateSpinner, gbc);

        // Table for expenses
        String[] columnNames = {"ID", "Category", "Payment Method", "Amount", "Description", "Date"};
        expenseTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No direct editing in table
            }
        };

        expenseTable = new JTable(expenseTableModel);
        JScrollPane tableScroll = new JScrollPane(expenseTable);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(tableScroll, gbc);

        // Buttons
        addExpense = new JButton("Add Expense");
        deleteExpense = new JButton("Delete Expense");
        updateExpense = new JButton("Update Expense");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(addExpense);
        buttonPanel.add(deleteExpense);
        buttonPanel.add(updateExpense);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(buttonPanel, gbc);

        // Set up event listeners
        setupExpenseEvents();
        
        // Load categories and expenses
        loadCategoriesForExpense();
        loadExpenses();
        
        // Add table selection listener
        expenseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedExpense();
            }
        });
        
        frame.setVisible(true);
    }

    /**
     * Sets up event listeners for expense management buttons using lambda expressions
     * Each button triggers its corresponding method when clicked
     */
    private void setupExpenseEvents() {
        addExpense.addActionListener(e -> addExpense());
        deleteExpense.addActionListener(e -> deleteExpense());
        updateExpense.addActionListener(e -> updateExpense());
    }

    /**
     * Adds a new expense to the database
     * Flow: User fills form -> clicks Add -> validates input -> creates Expense object -> calls DAO -> refreshes table
     */
    private void addExpense() {
        // Get form data - same pattern as category
        String amountText = amountField.getText().trim();
        String description = expenseDescriptionArea.getText().trim();
        
        if (amountText.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return;
        }
        
        try {
            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            if (selectedCategory == null) {
                JOptionPane.showMessageDialog(this, "Please select a category");
                return;
            }
            
            PaymentMethod paymentMethod = (PaymentMethod) paymentCombo.getSelectedItem();
            if (paymentMethod == null) {
                JOptionPane.showMessageDialog(this, "Please select a payment method");
                return;
            }
            
            int amount = Integer.parseInt(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0");
                return;
            }
            
            Date selectedDate = (Date) dateSpinner.getValue();
            LocalDateTime expenseDate = selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            
            Expense expense = new Expense(0, selectedCategory.getCategoryid(), paymentMethod, amount);
            expense.setDescription(description);
            expense.setExpense_date(expenseDate);
            expense.setCreated_at(LocalDateTime.now());
            
            int expenseId = mainDAO.createExpense(expense);
            if (expenseId > 0) {
                JOptionPane.showMessageDialog(this, "Expense added successfully!");
                clearExpenseForm();
                loadExpenses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add expense");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount (numbers only)");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding expense: " + e.getMessage());
        }
    }

    /**
     * Deletes the selected expense from the database
     * Flow: User selects row -> clicks Delete -> confirms deletion -> calls DAO delete method -> refreshes table
     */
    private void deleteExpense() {
        int row = expenseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete");
            return;
        }
        
        int id = (int) expenseTable.getValueAt(row, 0);
        String description = (String) expenseTable.getValueAt(row, 4);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete expense: " + description + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Expense expense = new Expense(id, 0, PaymentMethod.CASH, 0);
                if (mainDAO.deleteExpense(expense)) {
                    JOptionPane.showMessageDialog(this, "Expense deleted successfully!");
                    loadExpenses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete expense");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage());
            }
        }
    }

    /**
     * Updates the selected expense with new information
     * Flow: User selects row -> modifies form fields -> clicks Update -> validates input -> calls DAO update method -> refreshes table
     */
    private void updateExpense() {
        int row = expenseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to update");
            return;
        }
        
        String amountText = amountField.getText().trim();
        String description = expenseDescriptionArea.getText().trim();
        
        if (amountText.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return;
        }
        
        try {
            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            if (selectedCategory == null) {
                JOptionPane.showMessageDialog(this, "Please select a category");
                return;
            }
            
            PaymentMethod paymentMethod = (PaymentMethod) paymentCombo.getSelectedItem();
            if (paymentMethod == null) {
                JOptionPane.showMessageDialog(this, "Please select a payment method");
                return;
            }
            
            int amount = Integer.parseInt(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0");
                return;
            }
            
            Date selectedDate = (Date) dateSpinner.getValue();
            LocalDateTime expenseDate = selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            
            int id = (int) expenseTable.getValueAt(row, 0);
            Expense expense = new Expense(id, selectedCategory.getCategoryid(), paymentMethod, amount);
            expense.setDescription(description);
            expense.setExpense_date(expenseDate);
            expense.setCreated_at(LocalDateTime.now());
            
            if (mainDAO.updateExpense(expense)) {
                JOptionPane.showMessageDialog(this, "Expense updated successfully!");
                loadExpenses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update expense");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount (numbers only)");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage());
        }
    }

    /**
     * Loads all categories into the expense category combo box
     * Flow: Fetches categories from DAO -> populates combo box for expense form
     */
    private void loadCategoriesForExpense() {
        try {
            List<Category> categories = mainDAO.getAllCategories();
            categoryCombo.removeAllItems();
            categories.forEach(categoryCombo::addItem);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }

    /**
     * Loads all expenses from database and populates the table
     * Flow: Called on window open and after CRUD operations -> fetches data from DAO -> clears table -> adds rows
     */
    private void loadExpenses() {
        try {
            List<Expense> expenses = mainDAO.getAllExpenses();
            expenseTableModel.setRowCount(0); // Clear existing rows
            
            expenses.forEach(expense -> {
                // Handle null values gracefully
                String paymentMethod = expense.getPaymentMethod() != null ? 
                    expense.getPaymentMethod().toString() : "UNKNOWN";
                String expenseDate = expense.getExpense_date() != null ? 
                    expense.getExpense_date().toString().substring(0, 16) : "N/A";
                
                Object[] row = {
                    expense.getExpense_id(),
                    getCategoryName(expense.getCategory_id()),
                    paymentMethod,
                    expense.getAmount(),
                    expense.getDescription(),
                    expenseDate
                };
                expenseTableModel.addRow(row);
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage());
        }
    }

    /**
     * Loads the selected expense data into the form fields for editing
     * Flow: User selects table row -> this method populates form fields with selected expense data
     */
    private void loadSelectedExpense() {
        int row = expenseTable.getSelectedRow();
        if (row != -1) {
            try {
                // Set category
                String categoryName = (String) expenseTable.getValueAt(row, 1);
                for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                    if (categoryCombo.getItemAt(i).getCategoryname().equals(categoryName)) {
                        categoryCombo.setSelectedIndex(i);
                        break;
                    }
                }

                // Set payment method
                String paymentMethod = (String) expenseTable.getValueAt(row, 2);
                if (!"UNKNOWN".equals(paymentMethod)) {
                    try {
                        paymentCombo.setSelectedItem(PaymentMethod.valueOf(paymentMethod));
                    } catch (IllegalArgumentException e) {
                        paymentCombo.setSelectedIndex(0); // Default to first option
                    }
                }

                // Set amount
                Object amountValue = expenseTable.getValueAt(row, 3);
                if (amountValue != null) {
                    amountField.setText(amountValue.toString());
                }

                // Set description
                Object descValue = expenseTable.getValueAt(row, 4);
                if (descValue != null) {
                    expenseDescriptionArea.setText(descValue.toString());
                }

                // Set date - parse the date string and set it in the spinner
                String dateString = (String) expenseTable.getValueAt(row, 5);
                if (!"N/A".equals(dateString) && dateString != null) {
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(dateString + ":00");
                        Date date = Date.from(dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
                        dateSpinner.setValue(date);
                    } catch (Exception e) {
                        // If parsing fails, keep current date
                        System.err.println("Failed to parse date: " + dateString);
                        dateSpinner.setValue(new Date());
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading selected expense: " + e.getMessage());
            }
        }
    }

    /**
     * Clears the expense form fields
     * Flow: Called after successful operations to reset the form
     */
    private void clearExpenseForm() {
        categoryCombo.setSelectedIndex(0);
        paymentCombo.setSelectedIndex(0);
        amountField.setText("");
        expenseDescriptionArea.setText("");
        dateSpinner.setValue(new Date()); // Reset to current date/time
    }

    /**
     * Helper method to get category name by ID
     * Flow: Searches through categories to find name by ID
     */
    private String getCategoryName(int categoryId) {
        try {
            List<Category> categories = mainDAO.getAllCategories();
            return categories.stream()
                    .filter(cat -> cat.getCategoryid() == categoryId)
                    .findFirst()
                    .map(Category::getCategoryname)
                    .orElse("Unknown");
        } catch (SQLException e) {
            return "Unknown";
        }
    }

    /**
     * Sets up event listeners for main navigation buttons using lambda expressions
     * Each button opens its corresponding management window
     */
    private void setupEventListeners() {
        expense.addActionListener(e -> Expense());
        category.addActionListener(e -> Category());
    }
}
