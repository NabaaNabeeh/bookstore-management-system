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
 * UserController - manages interactions between User models and the GUI
 */
public class UserController {
    
    /**
     * Authenticates a user based on email and password
     * @param email User's email
     * @param password User's password
     * @return User object if authentication succeeds, null otherwise
     */
    public static User login(String email, String password) {
        User user = User.authenticate(email, password);
    
        // If user is a customer, load their saved cart items
        if (user != null && user instanceof Customer) {
            ShoppingCartController.loadCartItems((Customer)user);
    }
    
    return user;
    }
    
    /**
     * Registers a new user
     * @param name User's name
     * @param email User's email
     * @param password User's password
     * @param role User's role (Customer, Supplier)
     * @return User object if registration succeeds, null otherwise
     */
    public static User register(String name, String email, String password, String role) {
        // Check if email already exists
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                return null; // Email already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
        return User.createUser(name, email, password, role);
    }
    
    /**
     * Populates a JTable with user data for admin view
     * @param table JTable to populate
     */
    public static void populateUserTable(JTable table) {
        Administrator admin = new Administrator("", "", "", ""); // Temporary admin to access methods
        java.util.List<User> users = admin.getAllUsers();
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear table
        
        for (User user : users) {
            model.addRow(new Object[] {
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive() ? "Active" : "Blocked",
                "Edit/Block"
            });
        }
    }
    
    /**
     * Changes a user's status (block/unblock)
     * @param userId ID of the user to change
     * @param active true to activate, false to block
     * @return true if operation succeeds, false otherwise
     */
    public static boolean changeUserStatus(String userId, boolean active) {
        Administrator admin = new Administrator("", "", "", ""); // Temporary admin
        if (active) {
            return admin.unblockUser(userId);
        } else {
            return admin.blockUser(userId);
        }
    }
    
    /**
     * Updates a user's profile information
     * @param user User to update
     * @param name New name
     * @param email New email
     * @param password New password
     * @return true if update succeeds, false otherwise
     */
    public static boolean updateUserProfile(User user, String name, String email, String password) {
        return user.updateProfile(name, email, password);
    }
    
    /**
     * Gets user details for display
     * @param userId ID of the user to get details for
     * @return String containing formatted user details
     */
    public static String getUserDetails(String userId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
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
                    return user.getDetails();
                }
            }
            return "User not found";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error retrieving user details: " + e.getMessage();
        }
    }
}
