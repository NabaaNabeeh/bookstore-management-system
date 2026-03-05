/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Payment class - represents a payment for an order
 * Corresponds to the Payments table in the database
 */
public class Payment {
    private String paymentId;
    private String orderId;
    private String userId;
    private String paymentMethod;
    private double amount;
    private Date date;
    
    // Constructor for creating a new payment
    public Payment(String orderId, String userId, String paymentMethod, double amount) {
        this.paymentId = "PMT" + System.currentTimeMillis();
        this.orderId = orderId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.date = new Date();
    }
    
    // Constructor for loading from database
    private Payment(String paymentId, String orderId, String userId, 
                   String paymentMethod, double amount, Date date) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.date = date;
    }
    
    // Getters
    public String getPaymentId() { return paymentId; }
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    
    // Database operations
    public boolean save() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Check if payment exists
            String checkSql = "SELECT COUNT(*) FROM payments WHERE payment_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, paymentId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count > 0) {
                // Update existing payment
                String updateSql = "UPDATE payments SET payment_method = ?, amount = ? WHERE payment_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, paymentMethod);
                updateStmt.setDouble(2, amount);
                updateStmt.setString(3, paymentId);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert new payment
                String insertSql = "INSERT INTO payments (payment_id, order_id, user_id, payment_method, amount, date) " +
                                  "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, paymentId);
                insertStmt.setString(2, orderId);
                insertStmt.setString(3, userId);
                insertStmt.setString(4, paymentMethod);
                insertStmt.setDouble(5, amount);
                insertStmt.setTimestamp(6, new Timestamp(date.getTime()));
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Static methods for database operations
    public static Payment loadFromDatabase(String paymentId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT * FROM payments WHERE payment_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Payment(
                    rs.getString("payment_id"),
                    rs.getString("order_id"),
                    rs.getString("user_id"),
                    rs.getString("payment_method"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("date")
                );
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
        return "Payment #" + paymentId + " - $" + String.format("%.2f", amount) + 
               " via " + paymentMethod + " on " + sdf.format(date);
    }
}
