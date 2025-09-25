package com.model;

/**
 * PaymentMethod enum representing the ways expenses can be paid
 * 
 * FLOW DETAILED EXPLANATION:
 * 1. GUI form -> user selects payment method from dropdown -> enum value selected
 * 2. Expense object creation -> PaymentMethod enum stored in Expense object
 * 3. Database operations -> enum converted to string for database storage
 * 4. Database retrieval -> string converted back to enum for GUI display
 * 
 * WHY THIS DESIGN:
 * - Type safety: Prevents invalid payment method values
 * - Enum benefits: Compile-time checking, IDE autocomplete, refactoring support
 * - Database compatibility: Stored as string in database, converted as needed
 * - GUI integration: Dropdowns can display enum values directly
 */
public enum PaymentMethod {
    /**
     * Cash payment method
     * 
     * INVOCATION FLOW:
     * 1. GUI dropdown -> user selects "CASH" option
     * 2. Enum value -> stored in Expense object
     * 3. Database storage -> converted to "CASH" string
     * 4. Database retrieval -> "CASH" string converted back to enum
     * 
     * WHY CASH:
     * - Common payment method: Many expenses paid with cash
     * - Simple tracking: No need for bank account details
     * - User-friendly: Easy to understand and select
     */
    CASH,
    
    /**
     * Bank account payment method
     * 
     * INVOCATION FLOW:
     * 1. GUI dropdown -> user selects "BANK_ACCOUNT" option
     * 2. Enum value -> stored in Expense object
     * 3. Database storage -> converted to "BANK_ACCOUNT" string
     * 4. Database retrieval -> "BANK_ACCOUNT" string converted back to enum
     * 
     * WHY BANK_ACCOUNT:
     * - Digital payments: Credit cards, debit cards, online payments
     * - Tracking: Can be linked to bank statements
     * - Reporting: Useful for financial analysis and budgeting
     */
    BANK_ACCOUNT
}
