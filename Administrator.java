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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Administrator subclass - represents a user who manages the bookstore
 * Corresponds to Administrator table in the database (extends User data)
 */
public class Administrator extends User {
    
    public Administrator(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Administrator");
    }
    
    // Empty constructor for loading from database
    private Administrator() {
        super();
        this.role = "Administrator";
    }
    
    // Factory method to load administrator from database
    public static Administrator loadFromDatabase(String userId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Load basic user data
            String userSql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setString(1, userId);
            ResultSet userRs = userStmt.executeQuery();
            
            if (userRs.next()) {
                Administrator admin = new Administrator();
                admin.userId = userRs.getString("user_id");
                admin.name = userRs.getString("name");
                admin.email = userRs.getString("email");
                admin.password = userRs.getString("password");
                admin.isActive = userRs.getBoolean("is_active");
                
                // Load admin-specific data if needed
                
                return admin;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Book management methods
    public boolean addBook(String title, String author, String genre, String isbn,double price, int stock, String supplierId, String description) {
        Book book = new Book(title, author, genre, isbn, price, stock, supplierId, description);
        return book.save();
    }
    
    public boolean updateBook(String bookId, String title, String author, String genre, 
                            String isbn, double price, int stock, String description) {
        Book book = Book.loadFromDatabase(bookId);
        if (book == null) {
            return false;
        }
        
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setStock(stock);
        book.setDescription(description);
        
        return book.save();
    }
    
    public boolean deleteBook(String bookId) {
    Book book = Book.loadFromDatabase(bookId);
    if (book == null) {
        return false;
    }
    
    // Check if book is referenced in order_items
    Connection conn = DatabaseConnector.getInstance().getConnection();
    try {
        String checkOrderSql = "SELECT COUNT(*) FROM order_items WHERE book_id = ?";
        PreparedStatement checkOrderStmt = conn.prepareStatement(checkOrderSql);
        checkOrderStmt.setString(1, bookId);
        ResultSet orderRs = checkOrderStmt.executeQuery();
        orderRs.next();
        if (orderRs.getInt(1) > 0) {
            // Book is part of orders - can't delete
            return false;
        }
        
        // Delete related reviews first
        String deleteReviewsSql = "DELETE FROM reviews WHERE book_id = ?";
        PreparedStatement deleteReviewsStmt = conn.prepareStatement(deleteReviewsSql);
        deleteReviewsStmt.setString(1, bookId);
        deleteReviewsStmt.executeUpdate();
        
        // Delete inventory updates
        String deleteUpdatesSql = "DELETE FROM inventory_updates WHERE book_id = ?";
        PreparedStatement deleteUpdatesStmt = conn.prepareStatement(deleteUpdatesSql);
        deleteUpdatesStmt.setString(1, bookId);
        deleteUpdatesStmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
    
    return book.delete();
  }
    
    // User management methods
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Connection conn = DatabaseConnector.getInstance().getConnection();
        
        try {
            String sql = "SELECT user_id, role FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String role = rs.getString("role");
                
                User user = null;
                switch (role.toLowerCase()) {
                    case "customer":
                        user = Customer.loadFromDatabase(userId);
                        break;
                    case "administrator":
                        user = Administrator.loadFromDatabase(userId);
                        break;
                    case "supplier":
                        user = Supplier.loadFromDatabase(userId);
                        break;
                }
                
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    public boolean blockUser(String userId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "UPDATE users SET is_active = false WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean unblockUser(String userId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "UPDATE users SET is_active = true WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Report generation methods
    public Report generateSalesReport(Date startDate, Date endDate) {
        // Create a report object
        Report report = new Report("Sales", this.userId, "Sales Report");
        
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Total revenue
            String revenueSql = 
                "SELECT SUM(total_amount) FROM orders " +
                "WHERE order_date BETWEEN ? AND ? AND status = 'Completed'";
            PreparedStatement revenueStmt = conn.prepareStatement(revenueSql);
            revenueStmt.setDate(1, new java.sql.Date(startDate.getTime()));
            revenueStmt.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet revenueRs = revenueStmt.executeQuery();
            
            if (revenueRs.next()) {
                report.addData("totalRevenue", revenueRs.getDouble(1));
            }
            
            // Total books sold
            String booksSql = 
                "SELECT SUM(quantity) FROM order_items oi " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "WHERE o.order_date BETWEEN ? AND ? AND o.status = 'Completed'";
            PreparedStatement booksStmt = conn.prepareStatement(booksSql);
            booksStmt.setDate(1, new java.sql.Date(startDate.getTime()));
            booksStmt.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet booksRs = booksStmt.executeQuery();
            
            if (booksRs.next()) {
                report.addData("totalBooksSold", booksRs.getInt(1));
            }
            
            // Best-selling book
            String bestBookSql = 
                "SELECT b.book_id, b.title, SUM(oi.quantity) as total " +
                "FROM order_items oi " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "JOIN books b ON oi.book_id = b.book_id " +
                "WHERE o.order_date BETWEEN ? AND ? AND o.status = 'Completed' " +
                "GROUP BY b.book_id, b.title " +
                "ORDER BY total DESC " +
                "LIMIT 1";
            PreparedStatement bestBookStmt = conn.prepareStatement(bestBookSql);
            bestBookStmt.setDate(1, new java.sql.Date(startDate.getTime()));
            bestBookStmt.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet bestBookRs = bestBookStmt.executeQuery();
            
            if (bestBookRs.next()) {
                report.addData("bestSellingBook", bestBookRs.getString("title"));
                report.addData("bestSellingBookQuantity", bestBookRs.getInt("total"));
            }
            
            // Save the report
            report.save();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return report;
    }
    
    public Report generateInventoryReport() {
        // Create a report object
        Report report = new Report("Inventory", this.userId, "Inventory Status Report");
        
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Total books
            String totalSql = "SELECT COUNT(*) FROM books";
            PreparedStatement totalStmt = conn.prepareStatement(totalSql);
            ResultSet totalRs = totalStmt.executeQuery();
            
            if (totalRs.next()) {
                report.addData("totalBooks", totalRs.getInt(1));
            }
            
            // Low stock books
            String lowStockSql = "SELECT COUNT(*) FROM books WHERE stock < 10";
            PreparedStatement lowStockStmt = conn.prepareStatement(lowStockSql);
            ResultSet lowStockRs = lowStockStmt.executeQuery();
            
            if (lowStockRs.next()) {
                report.addData("lowStockBooks", lowStockRs.getInt(1));
            }
            
            // Total inventory value
            String valueSql = "SELECT SUM(price * stock) FROM books";
            PreparedStatement valueStmt = conn.prepareStatement(valueSql);
            ResultSet valueRs = valueStmt.executeQuery();
            
            if (valueRs.next()) {
                report.addData("inventoryValue", valueRs.getDouble(1));
            }
            
            // Books by genre
            String genreSql = "SELECT genre, COUNT(*) as count FROM books GROUP BY genre";
            PreparedStatement genreStmt = conn.prepareStatement(genreSql);
            ResultSet genreRs = genreStmt.executeQuery();
            
            Map<String, Integer> genreCounts = new HashMap<>();
            while (genreRs.next()) {
                genreCounts.put(genreRs.getString("genre"), genreRs.getInt("count"));
            }
            report.addData("genreCounts", genreCounts);
            
            // Save the report
            report.save();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return report;
    }
    
    @Override
    public String getDetails() {
        return "Administrator: " + getName() + 
               "\nEmail: " + getEmail();
    }
}
