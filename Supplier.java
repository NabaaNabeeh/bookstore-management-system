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
 * Supplier subclass - represents a user who adds books to the inventory
 * Corresponds to the Supplier table in the database (extends User data)
 */
public class Supplier extends User {
    
    public Supplier(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Supplier");
    }
    
    // Empty constructor for loading from database
    private Supplier() {
        super();
        this.role = "Supplier";
    }
    
    // Factory method to load supplier from database
    public static Supplier loadFromDatabase(String userId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Load basic user data
            String userSql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setString(1, userId);
            ResultSet userRs = userStmt.executeQuery();
            
            if (userRs.next()) {
                Supplier supplier = new Supplier();
                supplier.userId = userRs.getString("user_id");
                supplier.name = userRs.getString("name");
                supplier.email = userRs.getString("email");
                supplier.password = userRs.getString("password");
                supplier.isActive = userRs.getBoolean("is_active");
                
                // Load supplier-specific data if needed
                
                return supplier;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Book operations
    public boolean addNewBook(String title, String author, String genre, String isbn, double price, int stock, String description) {
        Book book = new Book(title, author, genre, isbn, price, stock, this.userId, description);
        return book.save();
    }
    
    public boolean updateBookStock(String bookId, int additionalStock) {
        Book book = Book.loadFromDatabase(bookId);
        if (book == null) {
            return false;
        }
        
        // Check if supplier owns this book
        if (!book.getSupplierId().equals(this.userId)) {
            return false;
        }
        
        int newStock = book.getStock() + additionalStock;
        book.setStock(newStock);
        
        if (book.save()) {
            // Record the inventory update
            InventoryUpdate update = new InventoryUpdate(this.userId, bookId, additionalStock);
            return update.save();
        }
        
        return false;
    }
    
    // Get books contributed by this supplier
    public List<Book> getContributedBooks() {
        List<Book> books = new ArrayList<>();
        Connection conn = DatabaseConnector.getInstance().getConnection();
        
        try {
            String sql = "SELECT book_id FROM books WHERE supplier_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String bookId = rs.getString("book_id");
                Book book = Book.loadFromDatabase(bookId);
                if (book != null) {
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return books;
    }
    
    // Get inventory updates by this supplier
    public List<InventoryUpdate> getStockUpdates() {
        List<InventoryUpdate> updates = new ArrayList<>();
        Connection conn = DatabaseConnector.getInstance().getConnection();
        
        try {
            String sql = "SELECT inventory_id FROM inventory_updates WHERE supplier_id = ? ORDER BY date_added DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String inventoryId = rs.getString("inventory_id");
                InventoryUpdate update = InventoryUpdate.loadFromDatabase(inventoryId);
                if (update != null) {
                    updates.add(update);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return updates;
    }
    
    // Get contribution statistics
    public Map<String, Object> getContributionStats() {
        Map<String, Object> stats = new HashMap<>();
        Connection conn = DatabaseConnector.getInstance().getConnection();
        
        try {
            // Total books contributed
            String booksSql = "SELECT COUNT(*) FROM books WHERE supplier_id = ?";
            PreparedStatement booksStmt = conn.prepareStatement(booksSql);
            booksStmt.setString(1, userId);
            ResultSet booksRs = booksStmt.executeQuery();
            
            if (booksRs.next()) {
                stats.put("totalBooks", booksRs.getInt(1));
            }
            
            // Total stock added
            String stockSql = "SELECT SUM(stock_added) FROM inventory_updates WHERE supplier_id = ?";
            PreparedStatement stockStmt = conn.prepareStatement(stockSql);
            stockStmt.setString(1, userId);
            ResultSet stockRs = stockStmt.executeQuery();
            
            if (stockRs.next()) {
                stats.put("totalStockAdded", stockRs.getInt(1));
            }
            
            // Books by genre
            String genreSql = 
                "SELECT genre, COUNT(*) as count " +
                "FROM books " +
                "WHERE supplier_id = ? " +
                "GROUP BY genre";
            PreparedStatement genreStmt = conn.prepareStatement(genreSql);
            genreStmt.setString(1, userId);
            ResultSet genreRs = genreStmt.executeQuery();
            
            Map<String, Integer> genreCounts = new HashMap<>();
            while (genreRs.next()) {
                genreCounts.put(genreRs.getString("genre"), genreRs.getInt("count"));
            }
            stats.put("genreCounts", genreCounts);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    @Override
    public String getDetails() {
        Map<String, Object> stats = getContributionStats();
        
        return "Supplier: " + getName() + 
               "\nEmail: " + getEmail() + 
               "\nBooks Contributed: " + stats.getOrDefault("totalBooks", 0) + 
               "\nTotal Stock Added: " + stats.getOrDefault("totalStockAdded", 0);
    }
}