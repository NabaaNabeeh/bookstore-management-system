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

/**
 * The User superclass - parent class for all users in the system
 * Corresponds to the User table in the database
 */
public abstract class User {
    // Database fields - match columns in User table
    protected String userId;
    protected String name;
    protected String email;
    protected String password;
    protected String role;
    protected boolean isActive;
    
    // Constructor
    public User(String userId, String name, String email, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = true;
    }
    
    // Empty constructor for loading from database
    protected User() {
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    // Database CRUD operations
    
    // Save user to database
    public boolean save() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Check if user exists (update vs insert)
            String checkSql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count > 0) {
                // Update existing user
                String updateSql = "UPDATE users SET name = ?, email = ?, password = ?, " +
                                  "role = ?, is_active = ? WHERE user_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, name);
                updateStmt.setString(2, email);
                updateStmt.setString(3, password);
                updateStmt.setString(4, role);
                updateStmt.setBoolean(5, isActive);
                updateStmt.setString(6, userId);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert new user
                String insertSql = "INSERT INTO users (user_id, name, email, password, role, is_active) " +
                                  "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, userId);
                insertStmt.setString(2, name);
                insertStmt.setString(3, email);
                insertStmt.setString(4, password);
                insertStmt.setString(5, role);
                insertStmt.setBoolean(6, isActive);
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete user from database
    public boolean delete() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Authentication method
    public static User authenticate(String email, String password) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND is_active = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Create appropriate user object based on role
                String role = rs.getString("role");
                String userId = rs.getString("user_id");
                String name = rs.getString("name");
                
                switch (role.toLowerCase()) {
                    case "customer":
                        return Customer.loadFromDatabase(userId);
                    case "administrator":
                        return Administrator.loadFromDatabase(userId);
                    case "supplier":
                        return Supplier.loadFromDatabase(userId);
                    default:
                        return null;
                }
            } else {
                return null; // Authentication failed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Common methods for all users
    public void logout() {
        // Just a placeholder - actual logout handled by GUI
        System.out.println("User " + name + " logged out");
    }
    
    public boolean updateProfile(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        return save(); // Save changes to database
    }
    
    // Abstract methods to be implemented by subclasses
    public abstract String getDetails();
    
    // Method to create a new user - Factory method
    public static User createUser(String name, String email, String password, String role) {
        // Generate a unique ID
        String userId = "USER" + System.currentTimeMillis();
        
        User user = null;
        switch (role.toLowerCase()) {
            case "customer":
                user = new Customer(userId, name, email, password);
                break;
            case "administrator":
                user = new Administrator(userId, name, email, password);
                break;
            case "supplier":
                user = new Supplier(userId, name, email, password);
                break;
        }
        
        if (user != null) {
            user.save(); // Save to database
        }
        
        return user;
    }
}
