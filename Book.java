/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Book class - represents a book in the system
 * Corresponds to the Books table in the database
 */
public class Book {
    private String bookId;
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private double price;
    private int stock;
    private String supplierId;
    private String description;
    
    // Constructor for new book creation
    public Book(String title, String author, String genre, String isbn,double price, int stock, String supplierId, String description) {
        this.bookId = "BOOK" + System.currentTimeMillis();
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.price = price;
        this.stock = stock;
        this.supplierId = supplierId;
        this.description = description;
    }
    
    // Constructor for loading from database
    private Book(String bookId, String title, String author, String genre, String isbn,
                double price, int stock, String supplierId, String description) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.price = price;
        this.stock = stock;
        this.supplierId = supplierId;
        this.description = description;
    }
    
    // Getters and Setters
    public String getBookId() { return bookId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public String getSupplierId() { return supplierId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // Database operations
    public boolean save() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Check if book exists
            String checkSql = "SELECT COUNT(*) FROM books WHERE book_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count > 0) {
                // Update existing book
                String updateSql = "UPDATE books SET title = ?, author = ?, genre = ?, isbn = ?, " +
                                  "price = ?, stock = ?, description = ? WHERE book_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, title);
                updateStmt.setString(2, author);
                updateStmt.setString(3, genre);
                updateStmt.setString(4, isbn);
                updateStmt.setDouble(5, price);
                updateStmt.setInt(6, stock);
                updateStmt.setString(7, description);
                updateStmt.setString(8, bookId);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert new book
                String insertSql = "INSERT INTO books (book_id, title, author, genre, isbn, " +
                                  "price, stock, supplier_id, description) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, bookId);
                insertStmt.setString(2, title);
                insertStmt.setString(3, author);
                insertStmt.setString(4, genre);
                insertStmt.setString(5, isbn);
                insertStmt.setDouble(6, price);
                insertStmt.setInt(7, stock);
                insertStmt.setString(8, supplierId);
                insertStmt.setString(9, description);
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete() {
    Connection conn = DatabaseConnector.getInstance().getConnection();
    try {
        String sql = "DELETE FROM books WHERE book_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, bookId);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
  }
    
    // Static methods for database operations
    public static Book loadFromDatabase(String bookId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT * FROM books WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Book(
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getString("isbn"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("supplier_id"),
                    rs.getString("description")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static List<Book> searchBooks(String criteria, String searchTerm) {
        List<Book> results = new ArrayList<>();
        Connection conn = DatabaseConnector.getInstance().getConnection();
        
        try {
            String sql = "";
            switch (criteria.toLowerCase()) {
                case "title":
                    sql = "SELECT * FROM books WHERE LOWER(title) LIKE ?";
                    break;
                case "author":
                    sql = "SELECT * FROM books WHERE LOWER(author) LIKE ?";
                    break;
                case "genre":
                    sql = "SELECT * FROM books WHERE LOWER(genre) LIKE ?";
                    break;
                case "isbn":
                    sql = "SELECT * FROM books WHERE isbn = ?";
                    break;
                default:
                    // Search in multiple fields
                    sql = "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? " +
                          "OR LOWER(genre) LIKE ? OR isbn LIKE ?";
                    break;
            }
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            if (criteria.equalsIgnoreCase("isbn")) {
                stmt.setString(1, searchTerm); // Exact match for ISBN
            } else if (criteria.equalsIgnoreCase("title") || 
                      criteria.equalsIgnoreCase("author") || 
                      criteria.equalsIgnoreCase("genre")) {
                stmt.setString(1, "%" + searchTerm.toLowerCase() + "%"); // Partial match
            } else {
                // Set all parameters for multi-field search
                String term = "%" + searchTerm.toLowerCase() + "%";
                stmt.setString(1, term);
                stmt.setString(2, term);
                stmt.setString(3, term);
                stmt.setString(4, term);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                results.add(new Book(
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getString("isbn"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("supplier_id"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return results;
    }
    
    // Book specific methods
    public boolean reduceStock(int quantity) {
        if (stock >= quantity) {
            stock -= quantity;
            return save();
        }
        return false;
    }
    
    public boolean isInStock() {
        return stock > 0;
    }
    
    public double getAverageRating() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT AVG(rating) FROM reviews WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    public List<Review> getReviews() {
        List<Review> reviews = new ArrayList<>();
        Connection conn = DatabaseConnector.getInstance().getConnection();
        
        try {
            String sql = "SELECT review_id FROM reviews WHERE book_id = ? ORDER BY date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String reviewId = rs.getString("review_id");
                Review review = Review.loadFromDatabase(reviewId);
                if (review != null) {
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reviews;
    }
    
    @Override
    public String toString() {
        return title + " by " + author + " (" + genre + ") - $" + price;
    }
}
