/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * ShoppingCartController - manages interactions between ShoppingCart model and the GUI
 */
public class ShoppingCartController {
    
    /**
     * Adds a book to the customer's cart
     * @param customer Customer whose cart to modify
     * @param bookId ID of the book to add
     * @param quantity Quantity to add
     */
    // In ShoppingCartController.java, update the addToCart method
   public static void addToCart(Customer customer, String bookId, int quantity) {
    if (customer == null || bookId == null || bookId.isEmpty() || quantity <= 0) {
        System.out.println("Invalid parameters: customer=" + customer + ", bookId=" + bookId + ", quantity=" + quantity);
        return;
    }

    try {
        // Load the book first to ensure it exists
        Book book = Book.loadFromDatabase(bookId);
        if (book == null) {
            System.out.println("Book not found with ID: " + bookId);
            return;
        }
        
        // Now add to cart
        customer.addToCart(bookId, quantity);
        System.out.println("Successfully added to cart: " + book.getTitle() + ", quantity: " + quantity);
    } catch (Exception e) {
        System.err.println("Error in addToCart: " + e.getMessage());
        e.printStackTrace();
    }
 }
    
    /**
     * Removes a book from the customer's cart
     * @param customer Customer whose cart to modify
     * @param bookId ID of the book to remove
     */
    public static void removeFromCart(Customer customer, String bookId) {
        customer.removeFromCart(bookId);
    }
    
    /**
     * Updates the quantity of a book in the customer's cart
     * @param customer Customer whose cart to modify
     * @param bookId ID of the book to update
     * @param quantity New quantity
     */
    public static void updateCartQuantity(Customer customer, String bookId, int quantity) {
        customer.getCart().updateQuantity(bookId, quantity);
    }
    
    /**
     * Gets the total price of all items in the cart
     * @param customer Customer whose cart to calculate
     * @return Total price
     */
    public static double getCartTotal(Customer customer) {
        return customer.getCart().calculateTotal();
    }
    
    /**
     * Gets the total number of items in the cart
     * @param customer Customer whose cart to check
     * @return Total number of items
     */
    public static int getCartItemCount(Customer customer) {
        return customer.getCart().getTotalItems();
    }
    
    /**
     * Checks if the cart is empty
     * @param customer Customer whose cart to check
     * @return true if cart is empty, false otherwise
     */
    public static boolean isCartEmpty(Customer customer) {
        return customer.getCart().isEmpty();
    }
    
    /**
     * Clears all items from the cart
     * @param customer Customer whose cart to clear
     */
    public static void clearCart(Customer customer) {
        customer.getCart().clear();
    }
    
    /**
     * Checks if there is sufficient stock for all items in the cart
     * @param customer Customer whose cart to check
     * @return true if sufficient stock, false otherwise
     */
    public static boolean checkStockAvailability(Customer customer) {
        ShoppingCart cart = customer.getCart();
        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            if (book.getStock() < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Populates a JTable with the contents of a customer's cart
     * @param table JTable to populate
     * @param customer Customer whose cart to display
     */
    public static void populateCartTable(JTable table, Customer customer) {
        ShoppingCart cart = customer.getCart();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear table
        
        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            model.addRow(new Object[] {
                book.getTitle(),
                String.format("$%.2f", book.getPrice()),
                item.getQuantity(),
                String.format("$%.2f", item.getSubtotal()),
                "Remove",
                book.getBookId() // Hidden column for ID
            });
        }
    }
    
    /**
     * Validates cart contents before checkout
     * @param customer Customer whose cart to validate
     * @return Error message if validation fails, empty string if validation succeeds
     */
    public static String validateCartForCheckout(Customer customer) {
        ShoppingCart cart = customer.getCart();
        
        // Check if cart is empty
        if (cart.isEmpty()) {
            return "Your cart is empty. Please add items before checkout.";
        }
        
        // Check stock availability
        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            if (book.getStock() < item.getQuantity()) {
                return "Insufficient stock for '" + book.getTitle() + "'. Available: " + book.getStock();
            }
        }
        
        return ""; // Validation succeeded
    }
    
    public static boolean saveCartItems(Customer customer) {
    Connection conn = DatabaseConnector.getInstance().getConnection();
    try {
        // First delete any existing saved cart items for this user
        String deleteSql = "DELETE FROM saved_cart_items WHERE user_id = ?";
        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
        deleteStmt.setString(1, customer.getUserId());
        deleteStmt.executeUpdate();
        
        // Now insert current cart items
        String insertSql = "INSERT INTO saved_cart_items (user_id, book_id, quantity) VALUES (?, ?, ?)";
        PreparedStatement insertStmt = conn.prepareStatement(insertSql);
        
        for (CartItem item : customer.getCart().getItems()) {
            insertStmt.setString(1, customer.getUserId());
            insertStmt.setString(2, item.getBook().getBookId());
            insertStmt.setInt(3, item.getQuantity());
            insertStmt.executeUpdate();
        }
        
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
    
    
    }
  
    public static void loadCartItems(Customer customer) {
    Connection conn = DatabaseConnector.getInstance().getConnection();
    try {
        String sql = "SELECT book_id, quantity FROM saved_cart_items WHERE user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, customer.getUserId());
        ResultSet rs = stmt.executeQuery();
        
        // Clear the current cart first
        customer.getCart().clear();
        
        // Add each saved item to the cart
        while (rs.next()) {
            String bookId = rs.getString("book_id");
            int quantity = rs.getInt("quantity");
            
            Book book = Book.loadFromDatabase(bookId);
            if (book != null && book.getStock() >= quantity) {
                customer.addToCart(bookId, quantity);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
  }
}