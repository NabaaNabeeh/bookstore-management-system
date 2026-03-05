/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;

/**
 *
 * @author maiso
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Order class - represents a customer order
 * Corresponds to the Orders table in the database
 */
public class Order {
    private String orderId;
    private String userId;
    private double totalAmount;
    private Date orderDate;
    private String status;
    
    // Constructor for creating a new order
    public Order(String userId, double totalAmount, String status) {
        this.orderId = "ORD" + System.currentTimeMillis();
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderDate = new Date();
        this.status = status;
    }
    
    // Constructor for loading from database
    private Order(String orderId, String userId, double totalAmount, Date orderDate, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
    }
    
    // Getters
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public double getTotalAmount() { return totalAmount; }
    public Date getOrderDate() { return orderDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status;
        // Update in database
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setString(2, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Database operations
    public boolean save() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Check if order exists
            String checkSql = "SELECT COUNT(*) FROM orders WHERE order_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, orderId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count > 0) {
                // Update existing order
                String updateSql = "UPDATE orders SET total_amount = ?, status = ? WHERE order_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setDouble(1, totalAmount);
                updateStmt.setString(2, status);
                updateStmt.setString(3, orderId);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert new order
                String insertSql = "INSERT INTO orders (order_id, user_id, total_amount, order_date, status) " +
                                  "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, orderId);
                insertStmt.setString(2, userId);
                insertStmt.setDouble(3, totalAmount);
                insertStmt.setTimestamp(4, new Timestamp(orderDate.getTime()));
                insertStmt.setString(5, status);
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Static methods for database operations
    public static Order loadFromDatabase(String orderId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT * FROM orders WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Order(
                    rs.getString("order_id"),
                    rs.getString("user_id"),
                    rs.getDouble("total_amount"),
                    rs.getTimestamp("order_date"),
                    rs.getString("status")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Order specific methods
    public void addItem(String bookId, int quantity) {
        Book book = Book.loadFromDatabase(bookId);
        if (book == null) {
            System.out.println("Cannot add item: book not found with ID " + bookId);
            return;
        }
        
        double subtotal = book.getPrice() * quantity;
        String orderItemId = "ITEM" + System.currentTimeMillis();
        System.out.println("Adding item to order: " + book.getTitle() + ", qty: " + quantity);
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "INSERT INTO order_items (order_item_id, order_id, book_id, quantity, subtotal) " +
                         "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "ITEM" + System.currentTimeMillis());
            stmt.setString(2, orderId);
            stmt.setString(3, bookId);
            stmt.setInt(4, quantity);
            stmt.setDouble(5, subtotal);
            int result = stmt.executeUpdate();
            System.out.println("Insert result: " + (result > 0 ? "Success" : "Failed"));
        } catch (SQLException e) {
            System.err.println("Error adding order item: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public List<OrderItem> getItems() {
        List<OrderItem> items = new ArrayList<>();
        Connection conn = DatabaseConnector.getInstance().getConnection();
        
        try {
            String sql = "SELECT * FROM order_items WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String orderItemId = rs.getString("order_item_id");
                String bookId = rs.getString("book_id");
                int quantity = rs.getInt("quantity");
                double subtotal = rs.getDouble("subtotal");
                
                Book book = Book.loadFromDatabase(bookId);
                items.add(new OrderItem(orderItemId, orderId, book, quantity, subtotal));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    public Payment getPayment() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT payment_id FROM payments WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String paymentId = rs.getString("payment_id");
                return Payment.loadFromDatabase(paymentId);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "Order #" + orderId + " (" + sdf.format(orderDate) + ") - $" + 
               String.format("%.2f", totalAmount) + " - " + status;
    }
}