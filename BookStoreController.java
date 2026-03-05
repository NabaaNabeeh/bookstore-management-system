/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;


import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import project_gui_draft2.GUI_3.LoginPage;


/**
 * BookStoreController - main controller class for the application
 * Handles system initialization and application startup
 */
public class BookStoreController {
    
    // Configuration properties
    private static Properties config = new Properties();
    
    /**
     * Initializes the system, including database setup
     * @return true if initialization succeeds, false otherwise
     */
    public static boolean initializeSystem() {
        // Load configuration
        try {
            File configFile = new File("config.properties");
            if (configFile.exists()) {
                config.load(new FileInputStream(configFile));
            } else {
                // Use defaults if no config file
                setDefaultConfig();
            }
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            // Continue with defaults
            setDefaultConfig();
        }
        
        // Initialize database
        try {
            // Get database connection to verify it works
            DatabaseConnector.getInstance().getConnection();
            
            // Create database tables
            createDatabaseTables();
            
            // Insert sample data if this is a fresh installation
            if (Boolean.parseBoolean(config.getProperty("insert_sample_data", "true"))) {
                insertSampleData();
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Error initializing system: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sets default configuration properties
     */
    private static void setDefaultConfig() {
        config.setProperty("insert_sample_data", "true");
        config.setProperty("theme", "light");
        config.setProperty("currency", "USD");
    }
    
    /**
     * Creates database tables
     */
    private static void createDatabaseTables() {
        java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Create Users table
            String createUsersSql = 
                "CREATE TABLE IF NOT EXISTS users (" +
                "user_id VARCHAR(50) PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "password VARCHAR(100) NOT NULL, " +
                "role VARCHAR(20) NOT NULL, " +
                "is_active BOOLEAN DEFAULT TRUE" +
                ")";
            conn.createStatement().executeUpdate(createUsersSql);
            
            // Create Books table
            String createBooksSql = 
                "CREATE TABLE IF NOT EXISTS books (" +
                "book_id VARCHAR(50) PRIMARY KEY, " +
                "title VARCHAR(200) NOT NULL, " +
                "author VARCHAR(100) NOT NULL, " +
                "genre VARCHAR(50) NOT NULL, " +
                "isbn VARCHAR(20) UNIQUE NOT NULL, " +
                "price DECIMAL(10, 2) NOT NULL, " +
                "stock INT NOT NULL, " +
                "supplier_id VARCHAR(50) NOT NULL, " +
                "description TEXT, " +
                "FOREIGN KEY (supplier_id) REFERENCES users(user_id)" +
                ")";
            conn.createStatement().executeUpdate(createBooksSql);
            
            // create saved cart items 
            String createSavedCartSql = 
            "CREATE TABLE IF NOT EXISTS saved_cart_items (" +
            "user_id VARCHAR(50) NOT NULL, " +
            "book_id VARCHAR(50) NOT NULL, " +
            "quantity INT NOT NULL, " +
            "PRIMARY KEY (user_id, book_id), " +
            "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
            "FOREIGN KEY (book_id) REFERENCES books(book_id)" +
            ")";
conn.createStatement().executeUpdate(createSavedCartSql);
            // Create Orders table
            String createOrdersSql = 
                "CREATE TABLE IF NOT EXISTS orders (" +
                "order_id VARCHAR(50) PRIMARY KEY, " +
                "user_id VARCHAR(50) NOT NULL, " +
                "total_amount DECIMAL(10, 2) NOT NULL, " +
                "order_date TIMESTAMP NOT NULL, " +
                "status VARCHAR(20) NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ")";
            conn.createStatement().executeUpdate(createOrdersSql);
            
            // Create OrderItems table
            String createOrderItemsSql = 
                "CREATE TABLE IF NOT EXISTS order_items (" +
                "order_item_id VARCHAR(50) PRIMARY KEY, " +
                "order_id VARCHAR(50) NOT NULL, " +
                "book_id VARCHAR(50) NOT NULL, " +
                "quantity INT NOT NULL, " +
                "subtotal DECIMAL(10, 2) NOT NULL, " +
                "FOREIGN KEY (order_id) REFERENCES orders(order_id), " +
                "FOREIGN KEY (book_id) REFERENCES books(book_id)" +
                ")";
            conn.createStatement().executeUpdate(createOrderItemsSql);
            
            // Create Reviews table
            String createReviewsSql = 
                "CREATE TABLE IF NOT EXISTS reviews (" +
                "review_id VARCHAR(50) PRIMARY KEY, " +
                "user_id VARCHAR(50) NOT NULL, " +
                "book_id VARCHAR(50) NOT NULL, " +
                "rating INT NOT NULL, " +
                "comment TEXT, " +
                "date TIMESTAMP NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                "FOREIGN KEY (book_id) REFERENCES books(book_id)" +
                ")";
            conn.createStatement().executeUpdate(createReviewsSql);
            
            // Create Payments table
            String createPaymentsSql = 
                "CREATE TABLE IF NOT EXISTS payments (" +
                "payment_id VARCHAR(50) PRIMARY KEY, " +
                "order_id VARCHAR(50) NOT NULL, " +
                "user_id VARCHAR(50) NOT NULL, " +
                "payment_method VARCHAR(50) NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "date TIMESTAMP NOT NULL, " +
                "FOREIGN KEY (order_id) REFERENCES orders(order_id), " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ")";
            conn.createStatement().executeUpdate(createPaymentsSql);
            
            // Create Reports table
            String createReportsSql = 
                "CREATE TABLE IF NOT EXISTS reports (" +
                "report_id VARCHAR(50) PRIMARY KEY, " +
                "report_type VARCHAR(50) NOT NULL, " +
                "generated_by VARCHAR(50) NOT NULL, " +
                "date TIMESTAMP NOT NULL, " +
                "title VARCHAR(200) NOT NULL, " +
                "data_content TEXT NOT NULL, " +
                "FOREIGN KEY (generated_by) REFERENCES users(user_id)" +
                ")";
            conn.createStatement().executeUpdate(createReportsSql);
            
            // Create InventoryUpdates table
            String createInventoryUpdatesSql = 
                "CREATE TABLE IF NOT EXISTS inventory_updates (" +
                "inventory_id VARCHAR(50) PRIMARY KEY, " +
                "supplier_id VARCHAR(50) NOT NULL, " +
                "book_id VARCHAR(50) NOT NULL, " +
                "stock_added INT NOT NULL, " +
                "date_added TIMESTAMP NOT NULL, " +
                "FOREIGN KEY (supplier_id) REFERENCES users(user_id), " +
                "FOREIGN KEY (book_id) REFERENCES books(book_id)" +
                ")";
            conn.createStatement().executeUpdate(createInventoryUpdatesSql);
            
            System.out.println("Database tables created successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error creating database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inserts sample data into the database for testing
     */
    private static void insertSampleData() {
        java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // First check if we already have sample data
            String checkUsersSql = "SELECT COUNT(*) FROM users";
            java.sql.ResultSet rs = conn.createStatement().executeQuery(checkUsersSql);
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("Sample data already exists.");
                return;
            }
            
            // Insert sample users
            String insertAdminSql = 
                "INSERT INTO users (user_id, name, email, password, role, is_active) " +
                "VALUES ('ADMIN1', 'Admin User', 'admin@test.com', 'admin123', 'Administrator', TRUE)";
            conn.createStatement().executeUpdate(insertAdminSql);
            
            String insertSupplierSql = 
                "INSERT INTO users (user_id, name, email, password, role, is_active) " +
                "VALUES ('SUPP1', 'Supplier User', 'supplier@test.com', 'supplier123', 'Supplier', TRUE)";
            conn.createStatement().executeUpdate(insertSupplierSql);
            
            String insertCustomerSql = 
                "INSERT INTO users (user_id, name, email, password, role, is_active) " +
                "VALUES ('CUST1', 'Customer User', 'customer@test.com', 'customer123', 'Customer', TRUE)";
            conn.createStatement().executeUpdate(insertCustomerSql);
            
            // Insert sample books
            String insertBookSql = 
                "INSERT INTO books (book_id, title, author, genre, isbn, price, stock, supplier_id, description) " +
                "VALUES ('BOOK1', 'Java Programming', 'John Doe', 'Programming', '1234567890', 29.99, 50, 'SUPP1', " +
                "'A comprehensive guide to Java programming.')";
            conn.createStatement().executeUpdate(insertBookSql);
            
            insertBookSql = 
                "INSERT INTO books (book_id, title, author, genre, isbn, price, stock, supplier_id, description) " +
                "VALUES ('BOOK2', 'Python Basics', 'Jane Smith', 'Programming', '0987654321', 24.99, 30, 'SUPP1', " +
                "'Learn Python programming from scratch.')";
            conn.createStatement().executeUpdate(insertBookSql);
            
            insertBookSql = 
                "INSERT INTO books (book_id, title, author, genre, isbn, price, stock, supplier_id, description) " +
                "VALUES ('BOOK3', 'Web Development', 'Bob Wilson', 'Web', '1122334455', 34.99, 25, 'SUPP1', " +
                "'Modern web development techniques explained.')";
            conn.createStatement().executeUpdate(insertBookSql);
            
            System.out.println("Sample data inserted successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Starts the application
     */
    public static void startApplication() {
        // Set look and feel
        try {
        // Set cross-platform Java L&F (also called "Metal")
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }
        
        // Initialize system
        if (!initializeSystem()) {
            JOptionPane.showMessageDialog(null,
                "Error initializing system. Please check database connection and try again.",
                "Initialization Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Create and show the login page
        SwingUtilities.invokeLater(() -> {
            GUI_3 gui3 = new GUI_3();
            GUI_3.LoginPage loginPage = gui3.new LoginPage();
            loginPage.setVisible(true);
        });
    }
    
    /**
     * Cleans up resources when the application closes
     */
    public static void shutdownApplication() {
        // Close database connection
        DatabaseConnector.getInstance().closeConnection();
        System.out.println("Application shutdown complete");
    }
    
    /**
     * Main entry point for the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        startApplication();
        
        // Add shutdown hook to clean up resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdownApplication();
        }));
    }
}