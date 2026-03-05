/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * BookController - manages interactions between Book models and the GUI
 */
public class BookController {
    
    /**
     * Adds a new book to the system
     * @param title Book title
     * @param author Book author
     * @param genre Book genre
     * @param isbn Book ISBN
     * @param price Book price
     * @param stock Initial stock
     * @param supplierId ID of supplier (or empty if added by admin)
     * @param description Book description
     * @return true if operation succeeds, false otherwise
     */
    public static boolean addBook(String title, String author, String genre, String isbn, 
                                 double price, int stock, String supplierId, String description) {
        // Validate input
        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || isbn.isEmpty()) {
            return false;
        }
        
        // Check if ISBN already exists
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT COUNT(*) FROM books WHERE isbn = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                return false; // ISBN already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        if (supplierId.isEmpty()) {
            // Added by admin
            Administrator admin = new Administrator("", "", "", ""); // Temporary admin
            return admin.addBook(title, author, genre, isbn, price, stock, supplierId, description);
        } else {
            // Added by supplier
            Supplier supplier = new Supplier(supplierId, "", "", ""); // Temporary supplier
            return supplier.addNewBook(title, author, genre, isbn, price, stock, description);
        }
    }
    
    /**
     * Updates an existing book
     * @param bookId ID of the book to update
     * @param title New title
     * @param author New author
     * @param genre New genre
     * @param isbn New ISBN
     * @param price New price
     * @param stock New stock
     * @param description New description
     * @return true if update succeeds, false otherwise
     */
    public static boolean updateBook(String bookId, String title, String author, String genre, 
                                    String isbn, double price, int stock, String description) {
        // Validate input
        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || isbn.isEmpty()) {
            return false;
        }
        
        Administrator admin = new Administrator("", "", "", ""); // Temporary admin
        return admin.updateBook(bookId, title, author, genre, isbn, price, stock, description);
    }
    
    /**
     * Deletes a book from the system
     * @param bookId ID of the book to delete
     * @return true if deletion succeeds, false otherwise
     */
    public static boolean deleteBook(String bookId) {
        Administrator admin = new Administrator("", "", "", ""); // Temporary admin
        return admin.deleteBook(bookId);
    }
    
    /**
     * Updates a book's stock level
     * @param bookId ID of the book to update
     * @param supplierId ID of the supplier updating the stock
     * @param additionalStock Amount of stock to add
     * @return true if update succeeds, false otherwise
     */
    public static boolean updateBookStock(String bookId, String supplierId, int additionalStock) {
        if (additionalStock <= 0) {
            return false;
        }
        
        Supplier supplier = new Supplier(supplierId, "", "", ""); // Temporary supplier
        return supplier.updateBookStock(bookId, additionalStock);
    }
    
    /**
     * Searches for books based on criteria
     * @param criteria Search criteria (title, author, genre, isbn)
     * @param searchTerm Term to search for
     * @return List of matching books
     */
    public static List<Book> searchBooks(String criteria, String searchTerm) {
        return Book.searchBooks(criteria, searchTerm);
    }
    
    /**
     * Gets a book by ID
     * @param bookId ID of the book to retrieve
     * @return Book object if found, null otherwise
     */
    public static Book getBookById(String bookId) {
        return Book.loadFromDatabase(bookId);
    }
    
    /**
     * Populates a JTable with book data for inventory view
     * @param table JTable to populate
     */
    public static void populateBookTable(JTable table) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear table
        
        try {
            String sql = "SELECT * FROM books ORDER BY title";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    "Edit/Delete"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Populates a JTable with book data for supplier view
     * @param table JTable to populate
     * @param supplierId ID of the supplier whose books to show
     */
    public static void populateSupplierBookTable(JTable table, String supplierId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear table
        
        try {
            String sql = "SELECT * FROM books WHERE supplier_id = ? ORDER BY title";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, supplierId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("stock"),
                    0, // For new stock input
                    "Update"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Populates a JTable with book data for customer catalog view
     * @param table JTable to populate
     */
    public static void populateBookCatalog(JTable table) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear table
        
        try {
            String sql = "SELECT b.book_id, b.title, b.author, b.price, b.stock, " +
                         "COALESCE(AVG(r.rating), 0) as avg_rating " +
                         "FROM books b " +
                         "LEFT JOIN reviews r ON b.book_id = r.book_id " +
                         "WHERE b.stock > 0 " +
                         "GROUP BY b.book_id, b.title, b.author, b.price, b.stock " +
                         "ORDER BY b.title";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"),
                    String.format("%.1f★", rs.getDouble("avg_rating")),
                    "View Details",
                    rs.getString("book_id") // Hidden column for ID
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Imports books from a CSV file
     * @param file CSV file to import
     * @param supplierId ID of the supplier importing the books
     * @return Number of books successfully imported
     */
    public static int importBooksFromCSV(File file, String supplierId) {
        int importedCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip header
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length < 7) {
                    continue; // Skip invalid lines
                }
                
                try {
                    String title = values[0].trim();
                    String author = values[1].trim();
                    String genre = values[2].trim();
                    String isbn = values[3].trim();
                    double price = Double.parseDouble(values[4].trim());
                    int stock = Integer.parseInt(values[5].trim());
                    String description = values[6].trim();
                    
                    if (addBook(title, author, genre, isbn, price, stock, supplierId, description)) {
                        importedCount++;
                    }
                } catch (NumberFormatException e) {
                    // Skip lines with invalid numbers
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return importedCount;
    }
}
