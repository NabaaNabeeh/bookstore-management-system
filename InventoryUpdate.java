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
 * InventoryUpdate class - represents a stock update by a supplier
 * Corresponds to the InventoryUpdates table in the database
 */
public class InventoryUpdate {
    private String inventoryId;
    private String supplierId;
    private String bookId;
    private int stockAdded;
    private Date dateAdded;
    
    // Constructor for creating a new update
    public InventoryUpdate(String supplierId, String bookId, int stockAdded) {
        this.inventoryId = "INV" + System.currentTimeMillis();
        this.supplierId = supplierId;
        this.bookId = bookId;
        this.stockAdded = stockAdded;
        this.dateAdded = new Date();
    }
    
    // Constructor for loading from database
    private InventoryUpdate(String inventoryId, String supplierId, String bookId, 
                          int stockAdded, Date dateAdded) {
        this.inventoryId = inventoryId;
        this.supplierId = supplierId;
        this.bookId = bookId;
        this.stockAdded = stockAdded;
        this.dateAdded = dateAdded;
    }
    
    // Getters
    public String getInventoryId() { return inventoryId; }
    public String getSupplierId() { return supplierId; }
    public String getBookId() { return bookId; }
    public int getStockAdded() { return stockAdded; }
    public Date getDateAdded() { return dateAdded; }
    
    // Database operations
    public boolean save() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Check if update exists
            String checkSql = "SELECT COUNT(*) FROM inventory_updates WHERE inventory_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, inventoryId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count > 0) {
                // Update existing record
                String updateSql = "UPDATE inventory_updates SET stock_added = ? WHERE inventory_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, stockAdded);
                updateStmt.setString(2, inventoryId);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert new record
                String insertSql = "INSERT INTO inventory_updates (inventory_id, supplier_id, book_id, stock_added, date_added) " +
                                  "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, inventoryId);
                insertStmt.setString(2, supplierId);
                insertStmt.setString(3, bookId);
                insertStmt.setInt(4, stockAdded);
                insertStmt.setTimestamp(5, new Timestamp(dateAdded.getTime()));
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Static methods for database operations
    public static InventoryUpdate loadFromDatabase(String inventoryId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT * FROM inventory_updates WHERE inventory_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, inventoryId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new InventoryUpdate(
                    rs.getString("inventory_id"),
                    rs.getString("supplier_id"),
                    rs.getString("book_id"),
                    rs.getInt("stock_added"),
                    rs.getTimestamp("date_added")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get book title
    public String getBookTitle() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT title FROM books WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("title");
            }
            return "Unknown Book";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown Book";
        }
    }
    
    // Get supplier name
    public String getSupplierName() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT name FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, supplierId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("name");
            }
            return "Unknown Supplier";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown Supplier";
        }
    }
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "Update #" + inventoryId + " - Added " + stockAdded + " units of " + 
               getBookTitle() + " on " + sdf.format(dateAdded);
    }
}
