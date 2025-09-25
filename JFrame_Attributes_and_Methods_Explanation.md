# JFrame Attributes and setExecute Methods - Complete Explanation

## JFrame Attributes in Your Application

### 1. **setTitle(String title)**
```java
setTitle("Expense Tracker");
```
**WHAT IT DOES:**
- Sets the text displayed in the window's title bar
- Shows at the top of the window frame

**WHERE IT HAPPENS:**
- Called in `initializeComponents()` method
- Executes when JFrame is created

**WHY IT'S NEEDED:**
- User identification: Users know which application they're using
- Window management: Helps users distinguish between multiple windows
- Professional appearance: Makes application look complete

### 2. **setVisible(boolean visible)**
```java
setVisible(true);
```
**WHAT IT DOES:**
- Makes the window visible or invisible on screen
- Controls whether user can see and interact with the window

**WHERE IT HAPPENS:**
- Called in `initializeComponents()` method
- Also called when creating new windows (Category, Expense)

**WHY IT'S NEEDED:**
- Window display: Without this, window exists but is invisible
- User interaction: Users can't interact with invisible windows
- Window management: Can hide/show windows as needed

### 3. **setSize(int width, int height)**
```java
setSize(1920, 1200);
```
**WHAT IT DOES:**
- Sets the window dimensions in pixels
- Controls how big the window appears on screen

**WHERE IT HAPPENS:**
- Called in `initializeComponents()` method
- Different sizes for different windows (main: 1920x1200, category: 900x600)

**WHY IT'S NEEDED:**
- Screen space: Ensures window fits on user's screen
- Content display: Provides enough space for all components
- User experience: Appropriate size for the content being displayed

### 4. **setDefaultCloseOperation(int operation)**
```java
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
```
**WHAT IT DOES:**
- Controls what happens when user clicks the X button
- Determines application behavior on window close

**WHERE IT HAPPENS:**
- Called in `initializeComponents()` for main window
- Called in `Category()` and `Expense()` methods for sub-windows

**WHY IT'S NEEDED:**
- Application lifecycle: Controls when application exits
- Window management: Different behavior for main vs sub-windows
- User experience: Prevents accidental application closure

**DIFFERENCE BETWEEN OPTIONS:**
- `JFrame.EXIT_ON_CLOSE`: Closes entire application (main window)
- `JFrame.DISPOSE_ON_CLOSE`: Closes only current window (sub-windows)

### 5. **setResizable(boolean resizable)**
```java
setResizable(false);
```
**WHAT IT DOES:**
- Controls whether user can resize the window by dragging edges
- Prevents or allows window resizing

**WHERE IT HAPPENS:**
- Called in `Category()` and `Expense()` methods
- Not set for main window (allows resizing)

**WHY IT'S NEEDED:**
- Layout consistency: Prevents breaking of fixed layouts
- User experience: Ensures components display correctly
- Design control: Maintains intended window appearance

### 6. **setLocationRelativeTo(Component parent)**
```java
setLocationRelativeTo(null);
```
**WHAT IT DOES:**
- Centers the window on screen
- Positions window relative to parent component

**WHERE IT HAPPENS:**
- Called in `Category()` and `Expense()` methods
- Not used for main window (appears at default location)

**WHY IT'S NEEDED:**
- User experience: Windows appear in center of screen
- Professional appearance: Better than random positioning
- Accessibility: Easier for users to find windows

## setExecute Methods - Database Operations

### 1. **executeUpdate() Method**
```java
int rows = stmt.executeUpdate();
```
**WHAT IT DOES:**
- Executes INSERT, UPDATE, or DELETE SQL statements
- Returns number of rows affected by the operation

**WHERE IT HAPPENS:**
- `createCategory()`: INSERT new category
- `updateCategory()`: UPDATE existing category
- `deleteCategory()`: DELETE category
- `createExpense()`: INSERT new expense
- `updateExpense()`: UPDATE existing expense
- `deleteExpense()`: DELETE expense

**WHY IT'S NEEDED:**
- Data modification: Actually changes data in database
- Success verification: Returns number of affected rows
- Error handling: Can detect if operation failed

**WHAT HAPPENS IN BACKGROUND:**
- SQL statement sent to MySQL server
- MySQL processes the statement
- MySQL returns count of affected rows
- Connection remains open for result retrieval

### 2. **executeQuery() Method**
```java
ResultSet rs = stmt.executeQuery();
```
**WHAT IT DOES:**
- Executes SELECT SQL statements
- Returns ResultSet containing query results

**WHERE IT HAPPENS:**
- `getAllCategories()`: SELECT all categories
- `getAllExpenses()`: SELECT all expenses

**WHY IT'S NEEDED:**
- Data retrieval: Gets data from database
- Result processing: Provides data for GUI display
- Multiple rows: Handles queries that return many rows

**WHAT HAPPENS IN BACKGROUND:**
- SQL SELECT statement sent to MySQL server
- MySQL executes query and returns results
- ResultSet created with all matching rows
- Data can be processed row by row

### 3. **getGeneratedKeys() Method**
```java
ResultSet rs = stmt.getGeneratedKeys();
```
**WHAT IT DOES:**
- Retrieves auto-generated keys from INSERT operations
- Gets the ID that was automatically created by database

**WHERE IT HAPPENS:**
- `createCategory()`: Get new category_id
- `createExpense()`: Get new expense_id

**WHY IT'S NEEDED:**
- ID retrieval: Get the auto-generated primary key
- Foreign key relationships: Need ID for other operations
- GUI updates: Display the new record with its ID

**WHAT HAPPENS IN BACKGROUND:**
- MySQL returns the auto-generated key
- ResultSet contains the new ID
- ID extracted and returned to calling method
- Used for future UPDATE/DELETE operations

## Flow Summary

### **JFrame Attributes Flow:**
1. **Window Creation** → `new JFrame()`
2. **Title Setting** → `setTitle()`
3. **Size Setting** → `setSize()`
4. **Close Behavior** → `setDefaultCloseOperation()`
5. **Resize Control** → `setResizable()`
6. **Positioning** → `setLocationRelativeTo()`
7. **Visibility** → `setVisible(true)`

### **setExecute Methods Flow:**
1. **Connection** → `DatabaseConnection.getDBConnection()`
2. **Statement Preparation** → `conn.prepareStatement()`
3. **Parameter Setting** → `stmt.setString()`, `stmt.setInt()`
4. **Execution** → `stmt.executeUpdate()` or `stmt.executeQuery()`
5. **Result Processing** → Handle returned data
6. **Connection Cleanup** → Automatic with try-with-resources

### **Key Differences:**
- **executeUpdate()**: For data changes (INSERT/UPDATE/DELETE)
- **executeQuery()**: For data retrieval (SELECT)
- **getGeneratedKeys()**: For getting auto-generated IDs
- **JFrame attributes**: Control window appearance and behavior
- **setExecute methods**: Control database operations and data flow

