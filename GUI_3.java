/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.border.EmptyBorder;
import java.util.Map;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.awt.Font.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Dialog;
/**
 *
 * @author maiso
 */
public class GUI_3 {
   
class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public LoginPage() {
        setTitle("Book Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GREEN);
        
        // Logo Panel
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(LIGHT_GREEN);
        try {
            ImageIcon logo = new ImageIcon(System.getProperty("user.dir") + "/src/project_gui_draft2/Logo2.png");
            Image scaledLogo = logo.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoPanel.add(logoLabel);
        } catch (Exception e) {
            System.out.println("Error loading logo: " + e.getMessage());
            // Fallback if logo can't be loaded
            JLabel titleLabel = new JLabel("Book Management System");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(DARK_GREEN);
            logoPanel.add(titleLabel);
        }
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Add padding
        
        // Login Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(LIGHT_GREEN);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Role Selection
        String[] roles = {"Customer", "Admin", "Supplier"};
        roleComboBox = new JComboBox<>(roles);
        addFormField(formPanel, "Select Role:", roleComboBox, gbc, 0);
        
        // Email Field
        emailField = new JTextField(20);
        addFormField(formPanel, "Email:", emailField, gbc, 1);
        
        // Password Field
        passwordField = new JPasswordField(20);
        addFormField(formPanel, "Password:", passwordField, gbc, 2);
        
        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(DARK_GREEN);
        loginButton.setForeground(Color.WHITE); 
        loginButton.setFont(new Font("Arial", Font.BOLD, 12)); 
        loginButton.setFocusPainted(false); 
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(loginButton, gbc);
        
        // Register Link
        JLabel registerLink = new JLabel("Don't have an account? Register here");
        registerLink.setForeground(DARK_GREEN);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        formPanel.add(registerLink, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Add event listeners
        loginButton.addActionListener(e -> handleLogin());
        registerLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openRegistrationPage();
            }
        });
        
        // Add key listener for pressing Enter in password field
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
    }
    
    private void addFormField(JPanel panel, String label, JComponent field, 
                            GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
    
    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String roleStr = (String) roleComboBox.getSelectedItem();
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Convert role string to match model expectations
        String role = roleStr.equals("Admin") ? "Administrator" : roleStr;
        
        // Call the controller to authenticate
        User user = UserController.login(email, password);
        
        if (user != null && user.getRole().equalsIgnoreCase(role)) {
            // Successful login, open appropriate dashboard
            openDashboard(user);
        } else {
            // Failed login
            JOptionPane.showMessageDialog(this,
                "Invalid credentials or role mismatch",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openDashboard(User user) {
        // Close login window
        this.dispose();
        
        // Open appropriate dashboard based on user role
        switch (user.getRole().toLowerCase()) {
            case "customer":
                CustomerDashboard dashboard = new CustomerDashboard((Customer)user);
                dashboard.setVisible(true);
                break;
            case "administrator":
                AdminDashboard adminDashboard = new AdminDashboard((Administrator)user);
                adminDashboard.setVisible(true);
                break;
            case "supplier":
                SupplierDashboard supplierDashboard = new SupplierDashboard((Supplier)user);
                supplierDashboard.setVisible(true);
                break;
        }
    }
    
    private void openRegistrationPage() {
        // Open registration page
        RegistrationPage registrationPage = new RegistrationPage();
        registrationPage.setVisible(true);
        this.dispose(); // Close login window
    }
}

class RegistrationPage extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public RegistrationPage() {
        setTitle("Book Management System - Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(LIGHT_GREEN);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add title label
        JLabel titleLabel = new JLabel("Create a New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_GREEN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 30, 5);
        mainPanel.add(titleLabel, gbc);
        
        // Reset insets for form fields
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name Field
        nameField = new JTextField(20);
        addFormField(mainPanel, "Name:", nameField, gbc, 1);
        
        // Email Field
        emailField = new JTextField(20);
        addFormField(mainPanel, "Email:", emailField, gbc, 2);
        
        // Password Fields
        passwordField = new JPasswordField(20);
        addFormField(mainPanel, "Password:", passwordField, gbc, 3);
        
        confirmPasswordField = new JPasswordField(20);
        addFormField(mainPanel, "Confirm Password:", confirmPasswordField, gbc, 4);
        
        // Role Selection (Admin role not available in registration)
        String[] roles = {"Customer", "Supplier"};
        roleComboBox = new JComboBox<>(roles);
        addFormField(mainPanel, "Role:", roleComboBox, gbc, 5);
        
        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(MEDIUM_GREEN);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(registerButton, gbc);
        
        // Login Link
        JLabel loginLink = new JLabel("Already have an account? Login here");
        loginLink.setForeground(DARK_GREEN);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 5, 20, 5);
        mainPanel.add(loginLink, gbc);
        
        add(mainPanel);
        
        // Add event listeners
        registerButton.addActionListener(e -> handleRegistration());
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openLoginPage();
            }
        });
    }
    
    private void addFormField(JPanel panel, String label, JComponent field, 
                            GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }
    
    private void handleRegistration() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());
        String roleStr = (String) roleComboBox.getSelectedItem();
        
        // Validate input
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirmPass)) {
            showError("Passwords do not match");
            return;
        }
        
        if (!isValidEmail(email)) {
        return;
       }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return;
        }
        
        // Convert role string to match model expectations
        String role = roleStr.equals("Admin") ? "Administrator" : roleStr;
        
        // Call controller to register user
        User user = UserController.register(name, email, password, role);
        try{
            if (user != null) {
                // Registration successful
                JOptionPane.showMessageDialog(this, 
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                openLoginPage();
            }
            else {
                // Registration failed
                throw new Exception("Registration failed. Email may already be in use.");
           }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, 
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isValidEmail(String email) {
    try {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Check for common mistakes
        String domain = email.substring(email.indexOf('@') + 1);
        if (domain.equalsIgnoreCase("gmial.com") || domain.equalsIgnoreCase("yahoo.con") || 
            domain.equalsIgnoreCase("hotmial.com")) {
            throw new IllegalArgumentException("Did you mean: " + 
                domain.replace("gmial.com", "gmail.com")
                     .replace("yahoo.con", "yahoo.com")
                     .replace("hotmial.com", "hotmail.com"));
        }
        
        return true;
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this,
            e.getMessage(),
            "Email Validation Error",
            JOptionPane.ERROR_MESSAGE);
        return false;
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Invalid email format: " + e.getMessage(),
            "Email Validation Error",
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
 }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void openLoginPage() {
        LoginPage loginPage = new LoginPage();
        loginPage.setVisible(true);
        this.dispose(); // Close registration window
    }
}
class CustomerDashboard extends JFrame {
    private Customer currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel welcomeLabel;
    private JLabel cartLabel;
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public CustomerDashboard(Customer user) {
        this.currentUser = user;
        
        setTitle("Book Management System - Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Add navigation panel (sidebar)
        NavigationPanel navPanel = new NavigationPanel();
        add(navPanel, BorderLayout.WEST);
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        
        // Create panels for different sections
        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, "welcome");
        contentPanel.add(new BookCatalogPanel(this), "catalog");
        contentPanel.add(new ShoppingCartPanel(this), "cart");
        contentPanel.add(new OrderHistoryPanel(this), "history");
        contentPanel.add(new ReviewsPanel(this), "reviews");
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Show welcome panel initially
        cardLayout.show(contentPanel, "welcome");
    }
    
    // Create header panel with user info and cart summary
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(MEDIUM_GREEN);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Welcome message on the left
        welcomeLabel = new JLabel("Welcome, " + currentUser.getName());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Cart summary on the right
        JPanel cartSummary = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cartSummary.setBackground(MEDIUM_GREEN);
        
        int cartCount = ShoppingCartController.getCartItemCount(currentUser);
        JLabel cartLabel = new JLabel("Cart: " + cartCount + " items");
        cartLabel.setForeground(Color.WHITE);
        cartLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add listener to go to cart when clicked
        cartLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(contentPanel, "cart");
            }
        });
        
        cartSummary.add(cartLabel);
        headerPanel.add(cartSummary, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    // Create welcome panel with user stats
    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(Color.WHITE);
        
        // Welcome header
        JLabel welcomeHeader = new JLabel("Welcome to the Online Book Management System", JLabel.CENTER);
        welcomeHeader.setFont(new Font("Monotype Corsiva", Font.BOLD, 30));
        welcomeHeader.setForeground(DARK_GREEN);
        welcomeHeader.setBorder(new EmptyBorder(30, 0, 30, 0));
        welcomePanel.add(welcomeHeader, BorderLayout.NORTH);
        
        // Content panel with user stats
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(new EmptyBorder(20, 100, 20, 100));
        
        // Get user statistics
        Map<String, Object> stats = ReportController.generateCustomerSpendingReport(currentUser.getUserId());
        
        // Create stat cards
        addStatCard(statsPanel, "Total Orders", stats.getOrDefault("totalOrders", 0).toString(), "Orders placed");
        addStatCard(statsPanel, "Total Spent", "$" + String.format("%.2f", stats.getOrDefault("totalSpent", 0.0)), "Amount spent on books");
        addStatCard(statsPanel, "Favorite Genre", stats.getOrDefault("favoriteGenre", "None").toString(), "Your most purchased category");
        addStatCard(statsPanel, "Account Status", currentUser.isActive() ? "Active" : "Inactive", "Current account status");
        
        welcomePanel.add(statsPanel, BorderLayout.CENTER);
        
        // Quick links panel
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        linksPanel.setBackground(LIGHT_GREEN);
        linksPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        addQuickLink(linksPanel, "Browse Books", "catalog");
        addQuickLink(linksPanel, "View Cart", "cart");
        addQuickLink(linksPanel, "Order History", "history");
        
        welcomePanel.add(linksPanel, BorderLayout.SOUTH);
        
        return welcomePanel;
    }
    
    // Helper method to add a stat card to the panel
    private void addStatCard(JPanel panel, String title, String value, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(LIGHT_GREEN);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MEDIUM_GREEN, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(DARK_GREEN);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        descLabel.setForeground(Color.GRAY);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(LIGHT_GREEN);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        panel.add(card);
    }
    
    // Helper method to add a quick link button
    private void addQuickLink(JPanel panel, String text, String targetCard) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(MEDIUM_GREEN);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        
        button.addActionListener(e -> cardLayout.show(contentPanel, targetCard));
        
        panel.add(button);
    }
    
    // Method to update cart count
    public void updateCartCount() {
//        int cartCount = ShoppingCartController.getCartItemCount(currentUser);
//        cartLabel.setText("Cart: " + cartCount + " items");
        
        getContentPane().add(createHeaderPanel(), BorderLayout.NORTH, 0);
        revalidate();
        repaint();
    }
    
    // Show a specific book's details
    public void showBookDetails(String bookId) {
        BookDetailsPanel detailsPanel = new BookDetailsPanel(this, bookId);
        contentPanel.add(detailsPanel, "bookDetails");
        cardLayout.show(contentPanel, "bookDetails");
    }
    
    // Show order details
    public void showOrderDetails(String orderId) {
        OrderDetailsPanel detailsPanel = new OrderDetailsPanel(this, orderId);
        contentPanel.add(detailsPanel, "orderDetails");
        cardLayout.show(contentPanel, "orderDetails");
    }
    
    // Show review form for a book
    public void showReviewForm(String bookId, String bookTitle) {
        ReviewFormPanel reviewForm = new ReviewFormPanel(this, bookId, bookTitle);
        contentPanel.add(reviewForm, "reviewForm");
        cardLayout.show(contentPanel, "reviewForm");
    }
    
    // Get the current user
    public Customer getCurrentUser() {
        return currentUser;
    }
    
    // Navigation panel (sidebar)
    private class NavigationPanel extends JPanel {
        public NavigationPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(DARK_GREEN);
            setPreferredSize(new Dimension(200, getHeight()));
            setBorder(new EmptyBorder(20, 0, 20, 0));
            
            // Add logo or app name at the top
            JLabel appNameLabel = new JLabel("Book Store");
            appNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            appNameLabel.setForeground(Color.WHITE);
            appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(appNameLabel);
            
            add(Box.createRigidArea(new Dimension(0, 30)));
            
            // Add navigation buttons
            addNavButton("Home", "welcome");
            addNavButton("Book Catalog", "catalog");
            addNavButton("Shopping Cart", "cart");
            addNavButton("Order History", "history");
            addNavButton("My Reviews", "reviews");
            
            add(Box.createVerticalGlue());
            
            // Logout button at the bottom
            JButton logoutButton = new JButton("Logout");
            styleNavButton(logoutButton);
            logoutButton.setBackground(new Color(153, 0, 0)); // Dark red for logout
            
            logoutButton.addActionListener(e -> handleLogout());
            
            add(logoutButton);
            add(Box.createRigidArea(new Dimension(0, 20)));
        }
        
        private void addNavButton(String text, String targetCard) {
            JButton button = new JButton(text);
            styleNavButton(button);
            
             button.addActionListener(e -> {
             // Show the target panel
             cardLayout.show(contentPanel, targetCard);
         
            // If navigating to cart, refresh the cart data
             if ("cart".equals(targetCard)) {
            // Find the ShoppingCartPanel and refresh its data
            Component[] components = contentPanel.getComponents();
            for (Component component : components) {
                if (component instanceof ShoppingCartPanel) {
                    ((ShoppingCartPanel) component).loadCartData();
                    break;
                }
            }
        }
    });
            
            add(button);
            add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        private void styleNavButton(JButton button) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(MEDIUM_GREEN);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 14));
        }
        
        private void handleLogout() {
            int choice = JOptionPane.showConfirmDialog(
                CustomerDashboard.this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                // Save cart items before logging out
                ShoppingCartController.saveCartItems(CustomerDashboard.this.getCurrentUser());
                new LoginPage().setVisible(true);
                CustomerDashboard.this.dispose();
            }
        }
    }
}
class AdminDashboard extends JFrame {
    private Administrator currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public AdminDashboard(Administrator admin) {
        this.currentUser = admin;
        
        setTitle("Book Management System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Create the navigation panel (sidebar)
        NavigationPanel navPanel = new NavigationPanel();
        add(navPanel, BorderLayout.WEST);
        
        // Create the header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create the main content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Create and add the content panels
        JPanel welcomePanel = createWelcomePanel();
        
        contentPanel.add(welcomePanel, "welcome");
        contentPanel.add(new InventoryPanel(this), "inventory");
        contentPanel.add(new SalesReportPanel(this), "sales");
        contentPanel.add(new UserManagementPanel(this), "users");
        contentPanel.add(new OrderManagementPanel(this), "orders");
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Show welcome panel initially
        cardLayout.show(contentPanel, "welcome");
    }
    
    class OrderManagementPanel extends JPanel {
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    // UI Components
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    
    // Admin reference
    private AdminDashboard dashboard;
    private Administrator admin;
    
    /**
     * Constructor
     * @param dashboard reference to the parent AdminDashboard
     */
    public OrderManagementPanel(AdminDashboard dashboard) {
        this.dashboard = dashboard;
        this.admin = dashboard.getCurrentUser();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel with title and filters
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create table panel for order data
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Load initial data
        loadOrderData();
    }
    
    /**
     * Creates the header panel with title, search, and filters
     * @return the configured header panel
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title label
        JLabel titleLabel = new JLabel("Order Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Create search and filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(Color.WHITE);
        
        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusFilterCombo = new JComboBox<>(new String[]{"All", "Pending", "Completed", "Cancelled"});
        statusFilterCombo.setPreferredSize(new Dimension(120, 30));
        
        // Search field
        JLabel searchLabel = new JLabel("Search Order ID:");
        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(150, 30));
        
        // Search button
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(MEDIUM_GREEN);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(MEDIUM_GREEN);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        
        // Add components to filter panel
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilterCombo);
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(refreshButton);
        
        panel.add(filterPanel, BorderLayout.EAST);
        
        // Add listeners
        searchButton.addActionListener(e -> filterOrders());
        refreshButton.addActionListener(e -> loadOrderData());
        statusFilterCombo.addActionListener(e -> filterOrders());
        searchField.addActionListener(e -> filterOrders()); // Search on Enter key
        
        return panel;
    }
    
    /**
     * Creates the table panel with order data
     * @return the configured table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Create table with order data columns
        String[] columns = {"Order ID", "Customer", "Date", "Total Amount", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is editable
            }
        };
        orderTable = new JTable(tableModel);
        
        // Style the table
        orderTable.setRowHeight(35);
        orderTable.setIntercellSpacing(new Dimension(10, 5));
        orderTable.setShowGrid(false);
        orderTable.setBackground(Color.WHITE);
        
        // Set column widths
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Order ID
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Customer
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Date
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Total Amount
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Actions
        
        // Amount column right aligned
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        orderTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        
        // Set custom renderer for Status column
        orderTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        
        // Set custom renderer and editor for Actions column
        orderTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor());
        
        // Set header style
        javax.swing.table.JTableHeader header = orderTable.getTableHeader();
        header.setBackground(LIGHT_GREEN);
        header.setForeground(DARK_GREEN);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Loads order data into the table
     */
    private void loadOrderData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Get all orders from database
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(
                "SELECT o.order_id, u.name, o.order_date, o.total_amount, o.status " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.user_id " +
                "ORDER BY o.order_date DESC"
            );
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            while (rs.next()) {
                String orderId = rs.getString("order_id");
                String customerName = rs.getString("name");
                Date orderDate = rs.getTimestamp("order_date");
                double totalAmount = rs.getDouble("total_amount");
                String status = rs.getString("status");
                
                tableModel.addRow(new Object[]{
                    orderId,
                    customerName,
                    dateFormat.format(orderDate),
                    String.format("$%.2f", totalAmount),
                    status,
                    getActionButtonText(status)
                });
            }
            
            // Show message if no orders found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No orders found in the system.",
                    "No Orders",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading order data: " + e.getMessage(),
                "Data Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Determines the appropriate action button text based on order status
     */
    private String getActionButtonText(String status) {
        switch (status) {
            case "Pending":
                return "Complete Order";
            case "Completed":
                return "View Details";
            case "Cancelled":
                return "View Details";
            default:
                return "View Details";
        }
    }
    
    /**
     * Filters the orders table based on search criteria and filter selections
     */
    private void filterOrders() {
        // Get filter values
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        String searchText = searchField.getText().trim();
        
        // If all filters are default and search is empty, just reload all data
        if ((statusFilter.equals("All") && searchText.isEmpty())) {
            loadOrderData();
            return;
        }
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Construct SQL query with filters
            StringBuilder sqlBuilder = new StringBuilder(
                "SELECT o.order_id, u.name, o.order_date, o.total_amount, o.status " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.user_id " +
                "WHERE 1=1 "
            );
            
            // Add status filter if not "All"
            if (!statusFilter.equals("All")) {
                sqlBuilder.append("AND o.status = '").append(statusFilter).append("' ");
            }
            
            // Add search filter if not empty
            if (!searchText.isEmpty()) {
                sqlBuilder.append("AND o.order_id LIKE '%").append(searchText).append("%' ");
            }
            
            // Add order by
            sqlBuilder.append("ORDER BY o.order_date DESC");
            
            // Execute query
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sqlBuilder.toString());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            while (rs.next()) {
                String orderId = rs.getString("order_id");
                String customerName = rs.getString("name");
                Date orderDate = rs.getTimestamp("order_date");
                double totalAmount = rs.getDouble("total_amount");
                String status = rs.getString("status");
                
                tableModel.addRow(new Object[]{
                    orderId,
                    customerName,
                    dateFormat.format(orderDate),
                    String.format("$%.2f", totalAmount),
                    status,
                    getActionButtonText(status)
                });
            }
            
            // Show message if no orders match the filter
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No orders match the selected filters.",
                    "No Matching Orders",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error filtering order data: " + e.getMessage(),
                "Filter Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Changes an order's status
     * @param orderId ID of the order to change status
     * @param newStatus New status
     */
    private void changeOrderStatus(String orderId, String newStatus) {
        // Confirm with admin
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to change this order status to '" + newStatus + "'?",
            "Confirm Status Change",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Change status
        boolean success = OrderController.updateOrderStatus(orderId, newStatus);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Order status changed successfully to '" + newStatus + "'.",
                "Status Changed",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh data
            loadOrderData();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to change order status. Please try again.",
                "Status Change Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Shows detailed order information
     * @param orderId ID of the order to view
     */
    private void viewOrderDetails(String orderId) {
        // Get order details
        java.util.Map<String, Object> orderDetails = OrderController.getOrderDetails(orderId);
        
        if (orderDetails == null) {
            JOptionPane.showMessageDialog(this,
                "Error loading order details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create and show order details dialog
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), 
                            "Order Details - " + orderId, 
                            Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Order information panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Order Information"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // Add order info fields
        addInfoField(infoPanel, "Order ID:", orderId);
        addInfoField(infoPanel, "Date:", dateFormat.format((Date)orderDetails.get("date")));
        addInfoField(infoPanel, "Total Amount:", String.format("$%.2f", (Double)orderDetails.get("totalAmount")));
        addInfoField(infoPanel, "Status:", (String)orderDetails.get("status"));
        
        // Order items table
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBackground(Color.WHITE);
        itemsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Order Items"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        String[] itemColumns = {"Book Title", "Author", "Quantity", "Price", "Subtotal"};
        DefaultTableModel itemsModel = new DefaultTableModel(itemColumns, 0);
        JTable itemsTable = new JTable(itemsModel);
        
        // Style the items table
        itemsTable.setRowHeight(25);
        itemsTable.setIntercellSpacing(new Dimension(5, 5));
        itemsTable.setBackground(Color.WHITE);
        itemsTable.getTableHeader().setBackground(LIGHT_GREEN);
        
        
        // Add order items to table
        @SuppressWarnings("unchecked")
        java.util.List<java.util.Map<String, Object>> items = 
            (java.util.List<java.util.Map<String, Object>>) orderDetails.get("items");
        
        if (items != null) {
            for (java.util.Map<String, Object> item : items) {
                itemsModel.addRow(new Object[]{
                    item.get("title"),
                    item.get("author"),
                    item.get("quantity"),
                    String.format("$%.2f", (Double)item.get("price")),
                    String.format("$%.2f", (Double)item.get("subtotal"))
                });
            }
        }
        
        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsPanel.add(itemsScroll, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(MEDIUM_GREEN);
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        // Add change status buttons if not completed or cancelled
        String status = (String)orderDetails.get("status");
        if ("Pending".equals(status)) {
            JButton completeButton = new JButton("Mark as Completed");
            completeButton.setBackground(MEDIUM_GREEN);
            completeButton.setForeground(Color.WHITE);
            completeButton.addActionListener(e -> {
                changeOrderStatus(orderId, "Completed");
                dialog.dispose();
            });
            buttonPanel.add(completeButton);
            
            JButton cancelButton = new JButton("Cancel Order");
            cancelButton.setBackground(new Color(153, 0, 0)); // Dark red
            cancelButton.setForeground(Color.WHITE);
            cancelButton.addActionListener(e -> {
                changeOrderStatus(orderId, "Cancelled");
                dialog.dispose();
            });
            buttonPanel.add(cancelButton);
        }
        
        // Assemble the dialog
        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(itemsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Helper method to add an info field to a panel
     */
    private void addInfoField(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(labelComp);
        panel.add(valueComp);
    }
    
    /**
     * Custom renderer for the Status column
     */
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
            
            String status = (String) value;
            if ("Completed".equals(status)) {
                setForeground(new Color(0, 128, 0)); // Dark green for completed
            } else if ("Pending".equals(status)) {
                setForeground(new Color(184, 134, 11)); // Dark goldenrod for pending
            } else if ("Cancelled".equals(status)) {
                setForeground(new Color(204, 0, 0)); // Dark red for cancelled
            } else {
                setForeground(Color.BLACK);
            }
            
            return c;
        }
    }
    
    /**
     * Custom renderer for the action buttons in the table
     */
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBorderPainted(false);
            setFocusPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            setText((value == null) ? "" : value.toString());
            
            // Set color based on action
            if ("Complete Order".equals(value)) {
                setBackground(MEDIUM_GREEN);
            } else {
                setBackground(new Color(70, 130, 180)); // Steel blue for view details
            }
            setForeground(Color.WHITE);
            
            return this;
        }
    }
    
    /**
     * Custom editor for the action buttons in the table
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            
            // Add button action
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            
            // Set color based on action
            if ("Complete Order".equals(label)) {
                button.setBackground(MEDIUM_GREEN);
            } else {
                button.setBackground(new Color(70, 130, 180)); // Steel blue for view details
            }
            button.setForeground(Color.WHITE);
            
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get order ID from the first column
                int row = orderTable.getEditingRow();
                String orderId = (String) orderTable.getValueAt(row, 0);
                
                // Perform action based on button label
                if ("Complete Order".equals(label)) {
                    changeOrderStatus(orderId, "Completed");
                } else {
                    viewOrderDetails(orderId);
                }
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
   }
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MEDIUM_GREEN);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Admin welcome label
        JLabel welcomeLabel = new JLabel("Admin Dashboard - " + currentUser.getName());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        panel.add(welcomeLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Welcome header
        JLabel headerLabel = new JLabel("Welcome to the Admin Dashboard", JLabel.CENTER);
        headerLabel.setFont(new Font("Monotype Corsiva", Font.BOLD, 30));
        headerLabel.setForeground(DARK_GREEN);
        headerLabel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        // Main content - dashboard cards
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        // Add statistic cards
        addStatCard(cardsPanel, "Total Books", getTotalBooks(), "Books in inventory");
        addStatCard(cardsPanel, "Low Stock Items", getLowStockCount(), "Books with stock < 10");
        addStatCard(cardsPanel, "Total Users", getTotalUsers(), "Registered users");
        addStatCard(cardsPanel, "Monthly Revenue", getMonthlyRevenue(), "Revenue this month");
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        actionsPanel.setBackground(LIGHT_GREEN);
        actionsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        addQuickLink(actionsPanel, "Manage Inventory", "inventory");
        addQuickLink(actionsPanel, "View Sales Reports", "sales");
        addQuickLink(actionsPanel, "Manage Users", "users");
        
        // Add all components to main panel
        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(cardsPanel, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Helper method to add a statistics card
    private void addStatCard(JPanel panel, String title, String value, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(LIGHT_GREEN);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MEDIUM_GREEN, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(DARK_GREEN);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        descLabel.setForeground(Color.GRAY);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(LIGHT_GREEN);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        panel.add(card);
    }
    
    // Helper method to add a quick action button
    private void addQuickLink(JPanel panel, String text, String targetCard) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(MEDIUM_GREEN);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        
        button.addActionListener(e -> cardLayout.show(contentPanel, targetCard));
        
        panel.add(button);
    }
    
    // Get count of books in inventory
    private String getTotalBooks() {
        try {
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books");
            
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "0";
    }
    
    // Get count of books with low stock
    private String getLowStockCount() {
        try {
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books WHERE stock < 10");
            
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "0";
    }
    
    // Get count of registered users
    private String getTotalUsers() {
        try {
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "0";
    }
    
    // Get current month's revenue
    private String getMonthlyRevenue() {
        try {
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(
                "SELECT SUM(total_amount) FROM orders " +
                "WHERE MONTH(order_date) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(order_date) = YEAR(CURRENT_DATE()) " +
                "AND status = 'Completed'"
            );
            
            java.sql.ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double revenue = rs.getDouble(1);
                return String.format("$%.2f", revenue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "$0.00";
    }
    
    public Administrator getCurrentUser() {
        return currentUser;
    }
    
    // Navigation panel (sidebar)
    private class NavigationPanel extends JPanel {
        public NavigationPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(DARK_GREEN);
            setPreferredSize(new Dimension(200, getHeight()));
            setBorder(new EmptyBorder(20, 0, 20, 0));
            
            // Add logo or app name at the top
            JLabel appNameLabel = new JLabel("Book Store");
            appNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            appNameLabel.setForeground(Color.WHITE);
            appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(appNameLabel);
            
            add(Box.createRigidArea(new Dimension(0, 30)));
            
            // Add navigation buttons
            addNavButton("Dashboard", "welcome");
            addNavButton("Inventory Management", "inventory");
            addNavButton("Sales Reports", "sales");
            addNavButton("User Management", "users");
            addNavButton("Manage Orders", "orders");
            
            add(Box.createVerticalGlue());
            
            // Logout button at the bottom
            JButton logoutButton = new JButton("Logout");
            styleNavButton(logoutButton);
            logoutButton.setBackground(new Color(153, 0, 0)); // Dark red for logout
            
            logoutButton.addActionListener(e -> handleLogout());
            
            add(logoutButton);
            add(Box.createRigidArea(new Dimension(0, 20)));
        }
        
        private void addNavButton(String text, String targetCard) {
            JButton button = new JButton(text);
            styleNavButton(button);
            
            button.addActionListener(e -> cardLayout.show(contentPanel, targetCard));
            
            add(button);
            add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        private void styleNavButton(JButton button) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(MEDIUM_GREEN);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 14));
        }
        
        private void handleLogout() {
            int choice = JOptionPane.showConfirmDialog(
                AdminDashboard.this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                new LoginPage().setVisible(true);
                AdminDashboard.this.dispose();
            }
        }
    }
}
class SupplierDashboard extends JFrame {
    private Supplier currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public SupplierDashboard(Supplier supplier) {
        this.currentUser = supplier;
        
        setTitle("Book Management System - Supplier Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Create the navigation panel (sidebar)
        NavigationPanel navPanel = new NavigationPanel();
        add(navPanel, BorderLayout.WEST);
        
        // Create the header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create the main content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Create and add the content panels
        JPanel welcomePanel = createWelcomePanel();
        
        contentPanel.add(welcomePanel, "welcome");
        contentPanel.add(new AddBooksPanel(this), "addBooks");
        contentPanel.add(new StockUpdatePanel(this), "stockUpdate");
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Show welcome panel initially
        cardLayout.show(contentPanel, "welcome");
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MEDIUM_GREEN);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Supplier welcome label
        JLabel welcomeLabel = new JLabel("Supplier Dashboard - " + currentUser.getName());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        panel.add(welcomeLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Welcome header
        JLabel headerLabel = new JLabel("Welcome to the Supplier Dashboard", JLabel.CENTER);
        headerLabel.setFont(new Font("Monotype Corsiva", Font.BOLD, 30));
        headerLabel.setForeground(DARK_GREEN);
        headerLabel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        // Main content - dashboard cards
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        // Get contribution stats
        Map<String, Object> stats = ReportController.generateSupplierContributionReport(
            currentUser.getUserId());
        
        // Add statistic cards
        addStatCard(cardsPanel, "Books Added", 
            stats.getOrDefault("totalBooks", 0).toString(), 
            "Total books contributed");
            
        addStatCard(cardsPanel, "Stock Added", 
            stats.getOrDefault("totalStockAdded", 0).toString(), 
            "Total inventory added");
            
        // Get top genre if available
        String topGenre = "None";
        int topGenreCount = 0;
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> genreCounts = 
            (Map<String, Integer>) stats.getOrDefault("genreCounts", new java.util.HashMap<>());
            
        for (Map.Entry<String, Integer> entry : genreCounts.entrySet()) {
            if (entry.getValue() > topGenreCount) {
                topGenre = entry.getKey();
                topGenreCount = entry.getValue();
            }
        }
        
        addStatCard(cardsPanel, "Top Genre", 
            topGenre + " (" + topGenreCount + ")", 
            "Your most added category");
            
        addStatCard(cardsPanel, "Account Status",
            currentUser.isActive() ? "Active" : "Inactive",
            "Current account status");
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        actionsPanel.setBackground(LIGHT_GREEN);
        actionsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        addQuickLink(actionsPanel, "Add New Books", "addBooks");
        addQuickLink(actionsPanel, "Update Stock", "stockUpdate");
        
        // Add all components to main panel
        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(cardsPanel, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Helper method to add a statistics card
    private void addStatCard(JPanel panel, String title, String value, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(LIGHT_GREEN);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MEDIUM_GREEN, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(DARK_GREEN);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        descLabel.setForeground(Color.GRAY);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(LIGHT_GREEN);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        panel.add(card);
    }
    
    // Helper method to add a quick action button
    private void addQuickLink(JPanel panel, String text, String targetCard) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(MEDIUM_GREEN);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        
        button.addActionListener(e -> cardLayout.show(contentPanel, targetCard));
        
        panel.add(button);
    }
    
    public Supplier getCurrentUser() {
        return currentUser;
    }
    
    // Navigation panel (sidebar)
    private class NavigationPanel extends JPanel {
        public NavigationPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(DARK_GREEN);
            setPreferredSize(new Dimension(200, getHeight()));
            setBorder(new EmptyBorder(20, 0, 20, 0));
            
            // Add logo or app name at the top
            JLabel appNameLabel = new JLabel("Book Store");
            appNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            appNameLabel.setForeground(Color.WHITE);
            appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(appNameLabel);
            
            add(Box.createRigidArea(new Dimension(0, 30)));
            
            // Add navigation buttons
            addNavButton("Dashboard", "welcome");
            addNavButton("Add Books", "addBooks");
            addNavButton("Update Stock", "stockUpdate");
            
            add(Box.createVerticalGlue());
            
            // Logout button at the bottom
            JButton logoutButton = new JButton("Logout");
            styleNavButton(logoutButton);
            logoutButton.setBackground(new Color(153, 0, 0)); // Dark red for logout
            
            logoutButton.addActionListener(e -> handleLogout());
            
            add(logoutButton);
            add(Box.createRigidArea(new Dimension(0, 20)));
        }
        
        private void addNavButton(String text, String targetCard) {
            JButton button = new JButton(text);
            styleNavButton(button);
            
            button.addActionListener(e -> cardLayout.show(contentPanel, targetCard));
            
            add(button);
            add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        private void styleNavButton(JButton button) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(MEDIUM_GREEN);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 14));
        }
        
        private void handleLogout() {
            int choice = JOptionPane.showConfirmDialog(
                SupplierDashboard.this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                new LoginPage().setVisible(true);
                SupplierDashboard.this.dispose();
            }
        }
    }
}
class ShoppingCartPanel extends JPanel {
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JButton checkoutButton;
    private CustomerDashboard dashboard;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public ShoppingCartPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initializeComponents();
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create cart table
        createCartTable();
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create footer panel with total and checkout button
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    private void initializeComponents() {
        // Initialize checkoutButton
        checkoutButton = new JButton("Proceed to Checkout");
        checkoutButton.setBackground(MEDIUM_GREEN);
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setEnabled(false);
        checkoutButton.addActionListener(e -> proceedToCheckout());

        // Initialize totalLabel
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(DARK_GREEN);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Shopping Cart");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void createCartTable() {
        // Column names
        String[] columns = {"Title", "Author", "Price", "Quantity", "Subtotal", "Actions", "BookID"};
        
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 5; // Only Quantity and Actions columns are editable
            }
            
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 3) { // Quantity column
                    try {
                        int quantity = Integer.parseInt(aValue.toString());
                        
                        if (quantity <= 0) {
                            JOptionPane.showMessageDialog(ShoppingCartPanel.this,
                                "Quantity must be greater than zero.",
                                "Invalid Quantity",
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Get current quantity and book ID
                        int currentQuantity = Integer.parseInt(getValueAt(row, 3).toString());
                        String bookId = (String) getValueAt(row, 6);
                        
                        // Only update if quantity changed
                        if (quantity != currentQuantity) {
                            // Update quantity in cart
                            ShoppingCartController.updateCartQuantity(
                                dashboard.getCurrentUser(), bookId, quantity);
                            
                            // Update the display
                            super.setValueAt(quantity, row, 3);
                            
                            // Get price (remove $ and convert)
                            double price = Double.parseDouble(
                                getValueAt(row, 2).toString().substring(1));
                            
                            // Calculate and update subtotal
                            double subtotal = price * quantity;
                            super.setValueAt(String.format("$%.2f", subtotal), row, 4);
                            
                            // Update total
                            updateTotal();
                            
                            // Update cart count in header
                            dashboard.updateCartCount();
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(ShoppingCartPanel.this,
                            "Please enter a valid number for quantity.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    super.setValueAt(aValue, row, column);
                }
            }
        };
        
        // Add columns to model
        for (String column : columns) {
            tableModel.addColumn(column);
        }
        
        cartTable = new JTable(tableModel);
        
        // Style the table
        cartTable.setRowHeight(30);
        cartTable.setIntercellSpacing(new Dimension(10, 5));
        cartTable.setShowGrid(false);
        cartTable.setBackground(Color.WHITE);
        
        // Set up column widths
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(250); // Title
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Author
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Price
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Quantity
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Subtotal
        cartTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Actions
        
        // Hide the BookID column
        cartTable.getColumnModel().getColumn(6).setMinWidth(0);
        cartTable.getColumnModel().getColumn(6).setMaxWidth(0);
        cartTable.getColumnModel().getColumn(6).setWidth(0);
        
        // Set up cell renderers and editors
        cartTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()));
        cartTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("Remove"));
        cartTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor());
        
        // Set header style
        JTableHeader header = cartTable.getTableHeader();
        header.setBackground(LIGHT_GREEN);
        header.setForeground(DARK_GREEN);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Load cart data
        loadCartData();
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Total label
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(DARK_GREEN);
        
        // Clear cart button
        JButton clearButton = new JButton("Clear Cart");
        clearButton.setBackground(new Color(153, 0, 0)); // Dark red
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        
        clearButton.addActionListener(e -> {
            if (tableModel.getRowCount() > 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to clear your cart?",
                    "Confirm Clear Cart",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    ShoppingCartController.clearCart(dashboard.getCurrentUser());
                    loadCartData();
                    dashboard.updateCartCount();
                }
            }
        });
        
        // Checkout button
        checkoutButton = new JButton("Proceed to Checkout");
        checkoutButton.setBackground(MEDIUM_GREEN);
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setEnabled(false); // Disable until items in cart
        
        checkoutButton.addActionListener(e -> proceedToCheckout());
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(clearButton);
        buttonPanel.add(checkoutButton);
        
        panel.add(totalLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    public void loadCartData() {
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get current user's cart
        Customer customer = dashboard.getCurrentUser();
        ShoppingCart cart = customer.getCart();
         System.out.println("Loading cart data. Items in cart: " + cart.getItems().size());
        if (cart.isEmpty()) {
            // Show empty cart message
            checkoutButton.setEnabled(false);
            totalLabel.setText("Total: $0.00");
            tableModel.addRow(new Object[]{
                "Your cart is empty", "", "", "", "", "", ""
            });
            
            // Make the empty message span across columns
            cartTable.setEnabled(false);
            return;
        }
        
        cartTable.setEnabled(true);
        checkoutButton.setEnabled(true);
        
        // Load cart items
        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            int quantity = item.getQuantity();
            double price = book.getPrice();
            double subtotal = item.getSubtotal();
            System.out.println("Adding to table: " + book.getTitle() + ", qty: " + quantity);
            tableModel.addRow(new Object[]{
                book.getTitle(),
                book.getAuthor(),
                String.format("$%.2f", price),
                quantity,
                String.format("$%.2f", subtotal),
                "Remove",
                book.getBookId()
            });
        }
        
        // Update total
        updateTotal();
    }
    
    private void updateTotal() {
        double total = ShoppingCartController.getCartTotal(dashboard.getCurrentUser());
        totalLabel.setText(String.format("Total: $%.2f", total));
    }
    
    private void proceedToCheckout() {
        // Validate cart first
        String validationError = ShoppingCartController.validateCartForCheckout(dashboard.getCurrentUser());
        if (!validationError.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                validationError,
                "Checkout Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show checkout dialog
        CheckoutDialog dialog = new CheckoutDialog(dashboard);
        dialog.setVisible(true);
        
        // Reload cart if order was placed
        if (dialog.isOrderPlaced()) {
            loadCartData();
            dashboard.updateCartCount();
        }
    }
    
    /**
     * Custom renderer for the action button in the table
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
            setBackground(new Color(153, 0, 0)); // Dark red
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    /**
     * Custom editor for the action button in the table
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(153, 0, 0)); // Dark red
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            
            // Add button action
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Remove" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get the book ID from the hidden column
                int row = cartTable.getSelectedRow();
                String bookId = (String) cartTable.getValueAt(row, 6);
                
                // Remove from cart
                ShoppingCartController.removeFromCart(dashboard.getCurrentUser(), bookId);
                
                // Reload cart data
                loadCartData();
                
                // Update cart count in header
                dashboard.updateCartCount();
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    /**
     * Dialog for checkout process
     */
    private class CheckoutDialog extends JDialog {
        private JComboBox<String> paymentMethodCombo;
        private JTextField addressField;
        private boolean orderPlaced = false;
        
        public CheckoutDialog(CustomerDashboard parent) {
            super(parent, "Checkout", true);
            setSize(500, 300);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            
            // Header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(MEDIUM_GREEN);
            headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            JLabel titleLabel = new JLabel("Complete Your Order");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel);
            
            // Form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Payment method
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(new JLabel("Payment Method:"), gbc);
            
            String[] paymentMethods = {"Credit Card", "PayPal", "Bank Transfer"};
            paymentMethodCombo = new JComboBox<>(paymentMethods);
            gbc.gridx = 1;
            formPanel.add(paymentMethodCombo, gbc);
            
            // Add payment logo label
            JLabel paymentLogoLabel = new JLabel();
            try {
                ImageIcon logo = new ImageIcon(System.getProperty("user.dir") + "/src/project_gui_draft2/Logo3.png");
                Image scaledLogo = logo.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                paymentLogoLabel = new JLabel(new ImageIcon(scaledLogo));
            } catch (Exception e) {
                System.out.println("Error loading payment logo: " + e.getMessage());
                // Fallback if logo can't be loaded
                paymentLogoLabel = new JLabel("PAYMENT");
                paymentLogoLabel.setFont(new Font("Arial", Font.BOLD, 14));
                paymentLogoLabel.setForeground(MEDIUM_GREEN);
            }
            

            // Add to the form panel
            gbc.gridx = -1; // Position it to the right of the combo box
            formPanel.add(paymentLogoLabel, gbc);
            
            // Shipping address
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(new JLabel("Shipping Address:"), gbc);
            
            addressField = new JTextField(25);
            gbc.gridx = 1;
            formPanel.add(addressField, gbc);
            
            // Order summary
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(20, 5, 5, 5);
            
            double total = ShoppingCartController.getCartTotal(parent.getCurrentUser());
            JLabel summaryLabel = new JLabel(String.format(
                "Order Total: $%.2f", total));
            summaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
            formPanel.add(summaryLabel, gbc);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(Color.LIGHT_GRAY);
            cancelButton.addActionListener(e -> dispose());
            
            JButton placeOrderButton = new JButton("Place Order");
            placeOrderButton.setBackground(MEDIUM_GREEN);
            placeOrderButton.setForeground(Color.WHITE);
            
            placeOrderButton.addActionListener(e -> {
               try{
                   if (addressField.getText().trim().isEmpty()) {
                    throw new Exception("Please enter a shipping address.");
                   }
                }
               catch (Exception p){
                JOptionPane.showMessageDialog(this,
                        p.getMessage(),
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
               }
                String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
                
               try {
               // Create the order
               System.out.println("Creating order with payment method: " + paymentMethod);
               Order order = OrderController.createOrder(
               parent.getCurrentUser(), paymentMethod);
        
               if (order != null) {
                  JOptionPane.showMessageDialog(this,
                  "Your order has been placed successfully!\n" +
                  "Order ID: " + order.getOrderId(),
                  "Order Confirmation",
                  JOptionPane.INFORMATION_MESSAGE);
            
                  orderPlaced = true;
                  dispose();
                } else {
                       JOptionPane.showMessageDialog(this,
                       "There was an error processing your order. Please try again.",
                       "Order Error",
                       JOptionPane.ERROR_MESSAGE);
                    }
              } catch (Exception ex) {
                   ex.printStackTrace();
                   JOptionPane.showMessageDialog(this,
                   "Error during checkout: " + ex.getMessage(),
                   "Checkout Error",
                   JOptionPane.ERROR_MESSAGE);
                }
            });
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(placeOrderButton);
            
            // Add panels to dialog
            add(headerPanel, BorderLayout.NORTH);
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        public boolean isOrderPlaced() {
            return orderPlaced;
        }
    }
}
class BookDetailsPanel extends JPanel {
    private CustomerDashboard dashboard;
    private String bookId;
    private Book book;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public BookDetailsPanel(CustomerDashboard dashboard, String bookId) {
        this.dashboard = dashboard;
        this.bookId = bookId;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Load book details from database
        loadBookDetails();
        
        if (book != null) {
            // Create header panel with back button and title
            JPanel headerPanel = createHeaderPanel();
            add(headerPanel, BorderLayout.NORTH);
            
            // Create main content panel
            JPanel contentPanel = createContentPanel();
            add(contentPanel, BorderLayout.CENTER);
            
            // Create reviews panel
            JPanel reviewsPanel = createReviewsPanel();
            add(reviewsPanel, BorderLayout.SOUTH);
        } else {
            // Show error message if book not found
            JLabel errorLabel = new JLabel("Book not found!", JLabel.CENTER);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 18));
            errorLabel.setForeground(Color.RED);
            add(errorLabel, BorderLayout.CENTER);
        }
    }
    
    private void loadBookDetails() {
        try {
            book = BookController.getBookById(bookId);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading book details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Back button
        JButton backButton = new JButton("← Back to Catalog");
        backButton.setBackground(MEDIUM_GREEN);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> dashboard.getContentPane().remove(this));
        
        backButton.addActionListener(e -> {
        // Switch to catalog panel
        dashboard.getContentPane().remove(this);
        dashboard.getContentPane().revalidate();
        dashboard.getContentPane().repaint();
        dashboard.cardLayout.show(dashboard.contentPanel, "catalog");
        });
        
        // Book title
        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_GREEN);
        
        panel.add(backButton, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        
        // Reset grid height for other components
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Author
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(new JLabel("Author:"), gbc);
        
        gbc.gridx = 2;
        JLabel authorLabel = new JLabel(book.getAuthor());
        authorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(authorLabel, gbc);
        
        // Genre
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Genre:"), gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel(book.getGenre()), gbc);
        
        // ISBN
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(new JLabel("ISBN:"), gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel(book.getIsbn()), gbc);
        
        // Price
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(new JLabel("Price:"), gbc);
        
        gbc.gridx = 2;
        JLabel priceLabel = new JLabel(String.format("$%.2f", book.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 100, 0));
        panel.add(priceLabel, gbc);
        
        // Availability
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(new JLabel("Availability:"), gbc);
        
        gbc.gridx = 2;
        String availabilityText = book.getStock() > 0 ? 
            "In Stock (" + book.getStock() + " available)" : "Out of Stock";
        JLabel availabilityLabel = new JLabel(availabilityText);
        availabilityLabel.setForeground(book.getStock() > 0 ? new Color(0, 100, 0) : Color.RED);
        panel.add(availabilityLabel, gbc);
        
        // Average Rating
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(new JLabel("Rating:"), gbc);
        
        gbc.gridx = 2;
        double rating = book.getAverageRating();
        JLabel ratingLabel = new JLabel(String.format("%.1f ★", rating));
        panel.add(ratingLabel, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 5, 10, 5);
        JLabel descHeader = new JLabel("Description");
        descHeader.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(descHeader, gbc);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 5, 20, 5);
        JTextArea descArea = new JTextArea(book.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(panel.getBackground());
        descArea.setRows(5);
        panel.add(descArea, gbc);
        
        // Add to cart button
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setPreferredSize(new Dimension(400, 50));   
        
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        quantitySpinner.setPreferredSize(new Dimension(60, 30));
        buttonPanel.add(new JLabel("Quantity:"));
        buttonPanel.add(quantitySpinner);
        
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.setBackground(new Color(0, 102, 51));
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.setVisible(true);
        addToCartButton.setPreferredSize(new Dimension(150, 40));
        addToCartButton.setEnabled(book.getStock() > 0);
        

        
        addToCartButton.addActionListener(e -> {
            int quantity = (int) quantitySpinner.getValue();
            
            if (quantity > book.getStock()) {
                JOptionPane.showMessageDialog(this,
                    "Sorry, only " + book.getStock() + " copies are available.",
                    "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Customer customer = dashboard.getCurrentUser();
            
            try {
            // Add debug output
            System.out.println("Adding to cart: Book ID=" + bookId + ", Quantity=" + quantity);
        
            // Call the controller method
            ShoppingCartController.addToCart(customer, bookId, quantity);
        
            JOptionPane.showMessageDialog(BookDetailsPanel.this,
            book.getTitle() + " has been added to your cart.",
            "Added to Cart",
            JOptionPane.INFORMATION_MESSAGE);
        
            dashboard.updateCartCount();
            } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
            "Error adding to cart: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
            dashboard.updateCartCount();
        }
      });
        

    // Create a panel for both components(quantity spinner and add to cart) with BoxLayout
    JPanel actionPanel = new JPanel();
    actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
    actionPanel.setBackground(Color.WHITE);
    JLabel quantityLabel = new JLabel("Quantity:");
    actionPanel.add(quantityLabel);

    quantitySpinner.setPreferredSize(new Dimension(80, 30));
    actionPanel.add(quantitySpinner);

    // Add some spacing
    actionPanel.add(Box.createRigidArea(new Dimension(20, 0)));

    // Add button
    actionPanel.add(addToCartButton);
    

    // Add the combined panel to the main panel
    gbc.gridx = 0;
    gbc.gridy = 8;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    panel.add(actionPanel, gbc);


    return panel;
    }
    
    private JPanel createReviewsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            new EmptyBorder(20, 0, 0, 0)
        ));
        
        // Reviews header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel reviewsHeader = new JLabel("Customer Reviews");
        reviewsHeader.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(reviewsHeader, BorderLayout.WEST);
        
        JButton writeReviewButton = new JButton("Write a Review");
        writeReviewButton.setBackground(MEDIUM_GREEN);
        writeReviewButton.setForeground(Color.WHITE);
        writeReviewButton.setFocusPainted(false);
        
        writeReviewButton.addActionListener(e -> {
            dashboard.showReviewForm(bookId, book.getTitle());
        });
        
        headerPanel.add(writeReviewButton, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Reviews content
        JPanel reviewsContent = new JPanel();
        reviewsContent.setLayout(new BoxLayout(reviewsContent, BoxLayout.Y_AXIS));
        reviewsContent.setBackground(Color.WHITE);
        
        List<Review> reviews = ReviewController.getBookReviews(bookId);
        
        if (reviews.isEmpty()) {
            JLabel noReviewsLabel = new JLabel("No reviews yet. Be the first to review this book!");
            noReviewsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            reviewsContent.add(noReviewsLabel);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Review review : reviews) {
                JPanel reviewPanel = new JPanel(new BorderLayout(10, 5));
                reviewPanel.setBackground(LIGHT_GREEN);
                reviewPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    new EmptyBorder(10, 10, 10, 10)
                ));
                
                // Review header (user and rating)
                JPanel reviewHeader = new JPanel(new BorderLayout());
                reviewHeader.setBackground(LIGHT_GREEN);
                
                JLabel userLabel = new JLabel(review.getReviewerName());
                userLabel.setFont(new Font("Arial", Font.BOLD, 14));
                reviewHeader.add(userLabel, BorderLayout.WEST);
                
                JLabel ratingLabel = new JLabel(String.format("%d ★", review.getRating()));
                ratingLabel.setFont(new Font("Arial", Font.BOLD, 14));
                reviewHeader.add(ratingLabel, BorderLayout.EAST);
                
                // Review content
                JTextArea commentArea = new JTextArea(review.getComment());
                commentArea.setEditable(false);
                commentArea.setLineWrap(true);
                commentArea.setWrapStyleWord(true);
                commentArea.setBackground(LIGHT_GREEN);
                commentArea.setBorder(null);
                
                // Review footer (date)
                JLabel dateLabel = new JLabel("Posted on " + sdf.format(review.getDate()));
                dateLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                dateLabel.setForeground(Color.GRAY);
                
                reviewPanel.add(reviewHeader, BorderLayout.NORTH);
                reviewPanel.add(commentArea, BorderLayout.CENTER);
                reviewPanel.add(dateLabel, BorderLayout.SOUTH);
                
                // Add margin between reviews
                reviewsContent.add(reviewPanel);
                reviewsContent.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(reviewsContent);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        scrollPane.setPreferredSize(new Dimension(-1, 100));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER); 
        return panel;
    }
    
}
class OrderHistoryPanel extends JPanel {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private CustomerDashboard dashboard;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public OrderHistoryPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create order table
        createOrderTable();
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Order History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void createOrderTable() {
        // Column names
        String[] columns = {"Order ID", "Date", "Total", "Status", "Actions", "OrderID"};
        
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only Actions column is editable
            }
        };
        
        // Add columns to model
        for (String column : columns) {
            tableModel.addColumn(column);
        }
        
        orderTable = new JTable(tableModel);
        
        // Style the table
        orderTable.setRowHeight(30);
        orderTable.setIntercellSpacing(new Dimension(10, 5));
        orderTable.setShowGrid(false);
        orderTable.setBackground(Color.WHITE);
        
        // Set up column widths
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Order ID
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Date
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Total
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Actions
        
        // Hide the OrderID column (used for reference)
        orderTable.getColumnModel().getColumn(5).setMinWidth(0);
        orderTable.getColumnModel().getColumn(5).setMaxWidth(0);
        orderTable.getColumnModel().getColumn(5).setWidth(0);
        
        // Status cell renderer (color based on status)
        orderTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                String status = value.toString();
                if (status.equals("Completed")) {
                    c.setForeground(new Color(0, 128, 0)); // Dark green
                } else if (status.equals("Pending")) {
                    c.setForeground(new Color(184, 134, 11)); // Dark goldenrod
                } else if (status.equals("Cancelled")) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
        
        // Set up the action button renderer and editor
        orderTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("View Details"));
        orderTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor());
        
        // Set header style
        JTableHeader header = orderTable.getTableHeader();
        header.setBackground(LIGHT_GREEN);
        header.setForeground(DARK_GREEN);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Load order data
        loadOrderData();
    }
    
    public void loadOrderData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get current user's orders
        Customer customer = dashboard.getCurrentUser();
        List<Order> orders = OrderController.getOrderHistory(customer.getUserId());
        
        if (orders.isEmpty()) {
            // Show empty message
            tableModel.addRow(new Object[]{
                "No orders found", "", "", "", "", ""
            });
            
            // Make the empty message span across columns
            orderTable.setEnabled(false);
            return;
        }
        
        orderTable.setEnabled(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        // Load orders
        for (Order order : orders) {
            tableModel.addRow(new Object[]{
                order.getOrderId(),
                sdf.format(order.getOrderDate()),
                String.format("$%.2f", order.getTotalAmount()),
                order.getStatus(),
                "View Details",
                order.getOrderId() // Hidden column for reference
            });
        }
    }
    
    /**
     * Custom renderer for the action button in the table
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
            setBackground(MEDIUM_GREEN);
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    /**
     * Custom editor for the action button in the table
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(MEDIUM_GREEN);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            
            // Add button action
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "View Details" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get the order ID from the hidden column
                int row = orderTable.getSelectedRow();
                String orderId = (String) orderTable.getValueAt(row, 5);
                
                // Show order details
                dashboard.showOrderDetails(orderId);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
class OrderDetailsPanel extends JPanel {
    private CustomerDashboard dashboard;
    private String orderId;
    private Map<String, Object> orderDetails;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public OrderDetailsPanel(CustomerDashboard dashboard, String orderId) {
        this.dashboard = dashboard;
        this.orderId = orderId;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Load order details
        loadOrderDetails();
        
        if (orderDetails != null) {
            // Create header panel
            JPanel headerPanel = createHeaderPanel();
            add(headerPanel, BorderLayout.NORTH);
            
            // Create main content panel
            JPanel contentPanel = createContentPanel();
            add(contentPanel, BorderLayout.CENTER);
        } else {
            // Show error message if order not found
            JLabel errorLabel = new JLabel("Order not found!", JLabel.CENTER);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 18));
            errorLabel.setForeground(Color.RED);
            add(errorLabel, BorderLayout.CENTER);
        }
    }
    
    private void loadOrderDetails() {
        try {
            orderDetails = OrderController.getOrderDetails(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading order details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Back button
        JButton backButton = new JButton("← Back to Orders");
        backButton.setBackground(MEDIUM_GREEN);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> {dashboard.cardLayout.show(dashboard.contentPanel, "history");
        });
        
        // Order title
        JLabel titleLabel = new JLabel("Order #" + orderId);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_GREEN);
        
        panel.add(backButton, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Color.WHITE);
        
        // Order summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Order date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JPanel datePanel = createInfoPanel("Order Date", 
            sdf.format(orderDetails.get("date")));
        summaryPanel.add(datePanel);
        
        // Order status
        String status = (String) orderDetails.get("status");
        JPanel statusPanel = createInfoPanel("Status", status);
        // Set status color
        Component[] components = statusPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getText().equals(status)) {
                    if (status.equals("Completed")) {
                        label.setForeground(new Color(0, 128, 0)); // Dark green
                    } else if (status.equals("Pending")) {
                        label.setForeground(new Color(184, 134, 11)); // Dark goldenrod
                    } else if (status.equals("Cancelled")) {
                        label.setForeground(Color.RED);
                    }
                    break;
                }
            }
        }
        summaryPanel.add(statusPanel);
        
        // Total amount
        Double totalAmount = (Double) orderDetails.get("totalAmount");
        JPanel totalPanel = createInfoPanel("Total Amount", 
            String.format("$%.2f", totalAmount));
        summaryPanel.add(totalPanel);
        
        // Order items table
        JPanel itemsPanel = new JPanel(new BorderLayout(0, 10));
        itemsPanel.setBackground(Color.WHITE);
        
        JLabel itemsTitle = new JLabel("Order Items");
        itemsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        itemsPanel.add(itemsTitle, BorderLayout.NORTH);
        
        // Create table for order items
        JTable itemsTable = createItemsTable();
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setBackground(Color.WHITE);
        itemsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Payment information
        JPanel paymentPanel = new JPanel(new BorderLayout(0, 10));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel paymentTitle = new JLabel("Payment Information");
        paymentTitle.setFont(new Font("Arial", Font.BOLD, 16));
        paymentPanel.add(paymentTitle, BorderLayout.NORTH);
        
        // Payment details
        JPanel paymentDetails = new JPanel(new GridLayout(2, 1, 0, 10));
        paymentDetails.setBackground(Color.WHITE);
        
        String paymentMethod = orderDetails.containsKey("paymentMethod") ? 
            (String) orderDetails.get("paymentMethod") : "Not available";
        JPanel methodPanel = createInfoPanel("Payment Method", paymentMethod);
        paymentDetails.add(methodPanel);
        
        String paymentDate = orderDetails.containsKey("paymentDate") ? 
            sdf.format(orderDetails.get("paymentDate")) : "Not available";
        JPanel payDatePanel = createInfoPanel("Payment Date", paymentDate);
        paymentDetails.add(payDatePanel);
        
        paymentPanel.add(paymentDetails, BorderLayout.CENTER);
        
        // Add all sections to main panel
        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(itemsPanel, BorderLayout.CENTER);
        panel.add(paymentPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createInfoPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GREEN);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(valueLabel);
        
        return panel;
    }
    
    private JTable createItemsTable() {
        // Create table model
        String[] columns = {"Book Title", "Author", "Quantity", "Price", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel();
        
        // Add columns
        for (String column : columns) {
            model.addColumn(column);
        }
        
        // Add order items
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderDetails.get("items");
        
        if (items != null&&!items.isEmpty()) {
            for (Map<String, Object> item : items) {
                String title = (String) item.get("title");
                String author = (String) item.get("author");
                int quantity = (Integer) item.get("quantity");
                double price = (Double) item.get("price");
                double subtotal = (Double) item.get("subtotal");
                
                model.addRow(new Object[]{
                    title,
                    author,
                    quantity,
                    String.format("$%.2f", price),
                    String.format("$%.2f", subtotal)
                });
            }
        }else {
        System.out.println("No items found in order details or items list is null");
        // Add debug row to show there's an issue with the data
        model.addRow(new Object[]{"No items found", "", "", "", ""});
       }
        
        // Create table
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setIntercellSpacing(new Dimension(10, 5));
        table.setShowGrid(false);
        table.setBackground(Color.WHITE);
        table.setEnabled(false); // Make table non-editable
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(250); // Title
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Author
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Quantity
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Price
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Subtotal
        
        // Set header style
        table.getTableHeader().setBackground(LIGHT_GREEN);
        table.getTableHeader().setForeground(DARK_GREEN);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        return table;
    }
}
class ReviewsPanel extends JPanel {
    private JTable reviewsTable;
    private DefaultTableModel tableModel;
    private CustomerDashboard dashboard;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public ReviewsPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create reviews table
        createReviewsTable();
        JScrollPane scrollPane = new JScrollPane(reviewsTable);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("My Reviews");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void createReviewsTable() {
        // Column names
        String[] columns = {"Book Title", "Rating", "Review", "Date", "Actions", "ReviewID"};
        
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only Actions column is editable
            }
        };
        
        // Add columns to model
        for (String column : columns) {
            tableModel.addColumn(column);
        }
        
        reviewsTable = new JTable(tableModel);
        
        // Style the table
        reviewsTable.setRowHeight(30);
        reviewsTable.setIntercellSpacing(new Dimension(10, 5));
        reviewsTable.setShowGrid(false);
        reviewsTable.setBackground(Color.WHITE);
        
        // Set up column widths
        reviewsTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Book Title
        reviewsTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Rating
        reviewsTable.getColumnModel().getColumn(2).setPreferredWidth(300); // Review
        reviewsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Date
        reviewsTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Actions
        
        // Hide the ReviewID column
        reviewsTable.getColumnModel().getColumn(5).setMinWidth(0);
        reviewsTable.getColumnModel().getColumn(5).setMaxWidth(0);
        reviewsTable.getColumnModel().getColumn(5).setWidth(0);
        
        // Configure Review column to wrap text
        DefaultTableCellRenderer reviewRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JTextArea textArea = new JTextArea((String) value);
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                textArea.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                textArea.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                
                return textArea;
            }
        };
        reviewsTable.getColumnModel().getColumn(2).setCellRenderer(reviewRenderer);
        
        // Set up the action button renderer and editor
        reviewsTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("Edit"));
        reviewsTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor());
        
        // Set header style
        JTableHeader header = reviewsTable.getTableHeader();
        header.setBackground(LIGHT_GREEN);
        header.setForeground(DARK_GREEN);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Load reviews data
        loadReviewsData();
    }
    
    public void loadReviewsData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get current user's reviews
        Customer customer = dashboard.getCurrentUser();
        List<Review> reviews = ReviewController.getCustomerReviews(customer.getUserId());
        
        if (reviews.isEmpty()) {
            // Show empty message
            tableModel.addRow(new Object[]{
                "You haven't written any reviews yet.", "", "", "", "", ""
            });
            
            // Make the empty message span across columns
            reviewsTable.setEnabled(false);
            return;
        }
        
        reviewsTable.setEnabled(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        // Load reviews
        for (Review review : reviews) {
            String bookTitle = review.getBookTitle();
            String rating = String.format("%d ★", review.getRating());
            String comment = review.getComment();
            String date = sdf.format(review.getDate());
            
            tableModel.addRow(new Object[]{
                bookTitle,
                rating,
                comment,
                date,
                "Edit",
                review.getReviewId() // Hidden column for reference
            });
        }
    }
    
    /**
     * Custom renderer for the action button in the table
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
            setBackground(MEDIUM_GREEN);
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    /**
     * Custom editor for the action button in the table
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(MEDIUM_GREEN);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            
            // Add button action
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Edit" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get the review ID from the hidden column
                int row = reviewsTable.getSelectedRow();
                String reviewId = (String) reviewsTable.getValueAt(row, 5);
                
                // Open edit dialog
                editReview(reviewId);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    /**
     * Open dialog to edit a review
     */
    private void editReview(String reviewId) {
        try {
            // Load review from database
            Review review = Review.loadFromDatabase(reviewId);
            if (review == null) {
                JOptionPane.showMessageDialog(this,
                    "Error loading review details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create dialog
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), 
                           "Edit Review", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            // Create form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Book title (non-editable)
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(new JLabel("Book:"), gbc);
            
            JTextField bookField = new JTextField(review.getBookTitle());
            bookField.setEditable(false);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            formPanel.add(bookField, gbc);
            
            // Rating selector
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0.0;
            formPanel.add(new JLabel("Rating:"), gbc);
            
            Integer[] ratings = {1, 2, 3, 4, 5};
            JComboBox<Integer> ratingBox = new JComboBox<>(ratings);
            ratingBox.setSelectedItem(review.getRating());
            gbc.gridx = 1;
            formPanel.add(ratingBox, gbc);
            
            // Review text
            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(new JLabel("Review:"), gbc);
            
            JTextArea commentArea = new JTextArea(review.getComment());
            commentArea.setLineWrap(true);
            commentArea.setWrapStyleWord(true);
            commentArea.setRows(8);
            JScrollPane scrollPane = new JScrollPane(commentArea);
            
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            formPanel.add(scrollPane, gbc);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton deleteButton = new JButton("Delete Review");
            deleteButton.setBackground(new Color(153, 0, 0)); // Dark red
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            
            JButton saveButton = new JButton("Save Changes");
            saveButton.setBackground(MEDIUM_GREEN);
            saveButton.setForeground(Color.WHITE);
            saveButton.setFocusPainted(false);
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(Color.LIGHT_GRAY);
            cancelButton.setFocusPainted(false);
            
            buttonPanel.add(deleteButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            
            // Add panels to dialog
            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            // Add button actions
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to delete this review?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = ReviewController.deleteReview(reviewId);
                    if (success) {
                        loadReviewsData(); // Reload reviews
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Error deleting review.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            saveButton.addActionListener(e -> {
                // Get values
                int rating = (Integer) ratingBox.getSelectedItem();
                String comment = commentArea.getText().trim();
                
                // Validate
                if (comment.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please enter a review comment.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update review
                boolean success = ReviewController.updateReview(reviewId, rating, comment);
                if (success) {
                    loadReviewsData(); // Reload reviews
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Error updating review.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            dialog.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error editing review: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
class ReviewFormPanel extends JPanel {
    private CustomerDashboard dashboard;
    private String bookId;
    private String bookTitle;
    private JComboBox<Integer> ratingCombo;
    private JTextArea commentArea;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public ReviewFormPanel(CustomerDashboard dashboard, String bookId, String bookTitle) {
        this.dashboard = dashboard;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Back button
        JButton backButton = new JButton("← Back");
        backButton.setBackground(MEDIUM_GREEN);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> { dashboard.showBookDetails(bookId);
        });
        
        // Review title
        JLabel titleLabel = new JLabel("Write a Review for: " + bookTitle);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        
        panel.add(backButton, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Rating selector
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel ratingLabel = new JLabel("Rating:");
        ratingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(ratingLabel, gbc);
        
        Integer[] ratings = {1, 2, 3, 4, 5};
        ratingCombo = new JComboBox<>(ratings);
        ratingCombo.setSelectedItem(5); // Default to 5 stars
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(ratingCombo, gbc);
        
        // Rating explanation
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        
        JPanel ratingExplanation = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ratingExplanation.setBackground(Color.WHITE);
        ratingExplanation.add(new JLabel("1★ = Poor, 2★ = Fair, 3★ = Good, 4★ = Very Good, 5★ = Excellent"));
        
        panel.add(ratingExplanation, gbc);
        
        // Comment area
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JLabel commentLabel = new JLabel("Your Review:");
        commentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(commentLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        
        commentArea = new JTextArea();
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setRows(10);
        commentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(commentArea);
        panel.add(scrollPane, gbc);
        
        // Review guidelines
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        
        JTextArea guidelinesArea = new JTextArea(
            "Guidelines for writing reviews:\n" +
            "- Focus on your experience with the book\n" +
            "- Be specific about what you liked or disliked\n" +
            "- Be respectful and constructive\n" +
            "- Avoid spoilers or offensive content"
        );
        guidelinesArea.setEditable(false);
        guidelinesArea.setBackground(LIGHT_GREEN);
        guidelinesArea.setFont(new Font("Arial", Font.ITALIC, 12));
        guidelinesArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        guidelinesArea.setLineWrap(true);
        guidelinesArea.setWrapStyleWord(true);
        
        panel.add(guidelinesArea, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dashboard.showBookDetails(bookId));
        
        JButton submitButton = new JButton("Submit Review");
        submitButton.setBackground(MEDIUM_GREEN);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> submitReview());
        
        panel.add(cancelButton);
        panel.add(submitButton);
        
        return panel;
    }
    
    private void submitReview() {
        // Validate input
        String comment = commentArea.getText().trim();
        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your review.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get rating
        int rating = (Integer) ratingCombo.getSelectedItem();
        
        // Submit review
        Customer customer = dashboard.getCurrentUser();
        boolean success = ReviewController.addReview(
            customer.getUserId(), bookId, rating, comment);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Your review has been submitted. Thank you!",
                "Review Submitted",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Go back to book details
            dashboard.showBookDetails(bookId);
        } else {
            JOptionPane.showMessageDialog(this,
                "There was an error submitting your review. Please try again.",
                "Submission Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
class InventoryPanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private AdminDashboard dashboard;
    private JTextField searchField;
    private JComboBox<String> searchCategoryCombo;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public InventoryPanel(AdminDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel with title, search, and action buttons
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create inventory table
        createInventoryTable();
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title and actions on the left
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Inventory Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        titlePanel.add(titleLabel);
        
        // Add book button
        JButton addBookButton = new JButton("Add New Book");
        addBookButton.setBackground(MEDIUM_GREEN);
        addBookButton.setForeground(Color.WHITE);
        addBookButton.setFocusPainted(false);
        addBookButton.addActionListener(e -> showAddBookDialog());
        addBookButton.setVisible(false);
        titlePanel.add(addBookButton);
        
        // Import CSV button
        JButton importButton = new JButton("Import CSV");
        importButton.setVisible(false);
        importButton.setBackground(MEDIUM_GREEN);
        importButton.setForeground(Color.WHITE);
        importButton.setFocusPainted(false);
        importButton.addActionListener(e -> importBooksFromCSV());
        titlePanel.add(importButton);
        
        // Search panel on the right
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        
        String[] categories = {"Title", "Author", "Genre", "ISBN", "All"};
        searchCategoryCombo = new JComboBox<>(categories);
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(MEDIUM_GREEN);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchCategoryCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add search action
        searchButton.addActionListener(e -> searchBooks());
        searchField.addActionListener(e -> searchBooks()); // Search on Enter key
        
        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void createInventoryTable() {
        // Column names
        String[] columns = {"ID", "Title", "Author", "Genre", "ISBN", "Price", "Stock", "Actions"};
        
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column is editable
            }
        };
        
        // Add columns to model
        for (String column : columns) {
            tableModel.addColumn(column);
        }
        
        bookTable = new JTable(tableModel);
        
        // Style the table
        bookTable.setRowHeight(30);
        bookTable.setIntercellSpacing(new Dimension(10, 5));
        bookTable.setShowGrid(false);
        bookTable.setBackground(Color.WHITE);
        
        // Set up column widths
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Genre
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(100); // ISBN
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Price
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(60);  // Stock
        bookTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Actions
        
        // Custom renderer for stock column to highlight low stock
        bookTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                int stock = Integer.parseInt(value.toString());
                if (stock < 5) {
                    c.setForeground(Color.RED);
                } else if (stock < 10) {
                    c.setForeground(new Color(255, 165, 0)); // Orange
                } else {
                    c.setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
        
        // Set up the action button renderer and editor
        bookTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        bookTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor());
        
        // Set header style
        JTableHeader header = bookTable.getTableHeader();
        header.setBackground(LIGHT_GREEN);
        header.setForeground(DARK_GREEN);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Load book data
        loadInventoryData();
    }
    
    public void loadInventoryData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load books from database
        try {
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(
                "SELECT book_id, title, author, genre, isbn, price, stock FROM books ORDER BY title");
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getString("isbn"),
                    String.format("$%.2f", rs.getDouble("price")),
                    rs.getInt("stock"),
                    "Edit/Delete"
                });
            }
            
            // Show message if no books found
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{
                    "", "No books found in inventory", "", "", "", "", "", ""
                });
                bookTable.setEnabled(false);
            } else {
                bookTable.setEnabled(true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading inventory data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadInventoryData(); // Load all books if search is empty
            return;
        }
        
        String category = (String) searchCategoryCombo.getSelectedItem();
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Construct SQL based on search category
            String sql;
            java.sql.PreparedStatement stmt;
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            
            if (category.equals("All")) {
                sql = "SELECT book_id, title, author, genre, isbn, price, stock " +
                      "FROM books " +
                      "WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? " +
                      "OR LOWER(genre) LIKE ? OR isbn LIKE ? " +
                      "ORDER BY title";
                stmt = conn.prepareStatement(sql);
                
                String searchPattern = "%" + searchTerm.toLowerCase() + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
            } else {
                sql = "SELECT book_id, title, author, genre, isbn, price, stock " +
                      "FROM books " +
                      "WHERE LOWER(" + category.toLowerCase() + ") LIKE ? " +
                      "ORDER BY title";
                stmt = conn.prepareStatement(sql);
                
                stmt.setString(1, "%" + searchTerm.toLowerCase() + "%");
            }
            
            java.sql.ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getString("isbn"),
                    String.format("$%.2f", rs.getDouble("price")),
                    rs.getInt("stock"),
                    "Edit/Delete"
                });
            }
            
            // Show message if no results
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{
                    "", "No books found matching your search.", "", "", "", "", "", ""
                });
                bookTable.setEnabled(false);
            } else {
                bookTable.setEnabled(true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error searching inventory: " + e.getMessage(),
                "Search Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddBookDialog() {
        // Create dialog
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), 
                           "Add new Book", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        
        JTextField titleField = new JTextField(30);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Author:"), gbc);
        
        JTextField authorField = new JTextField(30);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(authorField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Genre:"), gbc);
        
        String[] genres = {"Fiction", "Non-Fiction", "Science", "Programming", 
                          "History", "Biography", "Business", "Children", "Other"};
        JComboBox<String> genreCombo = new JComboBox<>(genres);
        gbc.gridx = 1;
        formPanel.add(genreCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("ISBN:"), gbc);
        
        JTextField isbnField = new JTextField(30);
        gbc.gridx = 1;
        formPanel.add(isbnField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Price:"), gbc);
        
        JTextField priceField = new JTextField(30);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Stock:"), gbc);
        
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        gbc.gridx = 1;
        formPanel.add(stockSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Description:"), gbc);
        
        JTextArea descArea = new JTextArea(5, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        
        gbc.gridx = 1;
        gbc.gridheight = 3;
        formPanel.add(descScroll, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setFocusPainted(false);
        
        JButton saveButton = new JButton("Save Book");
        saveButton.setBackground(MEDIUM_GREEN);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add button actions
        cancelButton.addActionListener(e -> dialog.dispose());
        
        saveButton.addActionListener(e -> {
            // Validate input
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String genre = (String) genreCombo.getSelectedItem();
            String isbn = isbnField.getText().trim();
            String priceStr = priceField.getText().trim();
            int stock = (int) stockSpinner.getValue();
            String description = descArea.getText().trim();
            
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all required fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate price format
            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) {
                    throw new NumberFormatException("Price must be positive");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter a valid price (e.g., 29.99).",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Add book using the controller
            boolean success = BookController.addBook(
                title, author, genre, isbn, price, stock, "", description);
            
            if (success) {
                dialog.dispose();
                loadInventoryData(); // Reload inventory
                JOptionPane.showMessageDialog(this,
                    "Book added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Error adding book. ISBN may already exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void showEditBookDialog(String bookId) {
        try {
            // Load book from database
            Book book = BookController.getBookById(bookId);
            if (book == null) {
                JOptionPane.showMessageDialog(this,
                    "Error loading book details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create dialog
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), 
                           "Edit Book", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setSize(500, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            // Create form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 5, 8, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Form fields
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(new JLabel("Title:"), gbc);
            
            JTextField titleField = new JTextField(book.getTitle(), 30);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            formPanel.add(titleField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0.0;
            formPanel.add(new JLabel("Author:"), gbc);
            
            JTextField authorField = new JTextField(book.getAuthor(), 30);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            formPanel.add(authorField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0.0;
            formPanel.add(new JLabel("Genre:"), gbc);
            
            String[] genres = {"Fiction", "Non-Fiction", "Science", "Programming", 
                              "History", "Biography", "Business", "Children", "Other"};
            JComboBox<String> genreCombo = new JComboBox<>(genres);
            genreCombo.setSelectedItem(book.getGenre());
            gbc.gridx = 1;
            formPanel.add(genreCombo, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(new JLabel("ISBN:"), gbc);
            
            JTextField isbnField = new JTextField(book.getIsbn(), 30);
            gbc.gridx = 1;
            formPanel.add(isbnField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 4;
            formPanel.add(new JLabel("Price:"), gbc);
            
            JTextField priceField = new JTextField(String.valueOf(book.getPrice()), 30);
            gbc.gridx = 1;
            formPanel.add(priceField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 5;
            formPanel.add(new JLabel("Stock:"), gbc);
            
            JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(book.getStock(), 0, 1000, 1));
            gbc.gridx = 1;
            formPanel.add(stockSpinner, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 6;
            formPanel.add(new JLabel("Description:"), gbc);
            
            JTextArea descArea = new JTextArea(book.getDescription(), 5, 30);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            JScrollPane descScroll = new JScrollPane(descArea);
            
            gbc.gridx = 1;
            gbc.gridheight = 3;
            formPanel.add(descScroll, gbc);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(Color.LIGHT_GRAY);
            cancelButton.setFocusPainted(false);
            
            JButton deleteButton = new JButton("Delete Book");
            deleteButton.setBackground(new Color(153, 0, 0)); // Dark red
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            
            JButton saveButton = new JButton("Save Changes");
            saveButton.setBackground(MEDIUM_GREEN);
            saveButton.setForeground(Color.WHITE);
            saveButton.setFocusPainted(false);
            
            buttonPanel.add(deleteButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            
            // Add panels to dialog
            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            // Add button actions
            cancelButton.addActionListener(e -> dialog.dispose());
            
            deleteButton.addActionListener(e -> {
             int confirm = JOptionPane.showConfirmDialog(dialog,
             "Are you sure you want to delete this book?\nThis action cannot be undone.",
             "Confirm Delete",
             JOptionPane.YES_NO_OPTION,
             JOptionPane.WARNING_MESSAGE);
    
             if (confirm == JOptionPane.YES_OPTION) {
             boolean success = BookController.deleteBook(bookId);
                  if (success) {
                     dialog.dispose();
                     loadInventoryData(); // Reload inventory
                     JOptionPane.showMessageDialog(this,
                     "Book deleted successfully.",
                      "Success",
                      JOptionPane.INFORMATION_MESSAGE);
                    } 
                  else {
                     JOptionPane.showMessageDialog(dialog,
                     "Error deleting book. The book may be part of existing orders and cannot be deleted.",
                     "Error",
                     JOptionPane.ERROR_MESSAGE);
                    }
            }
            });
            
            saveButton.addActionListener(e -> {
                // Validate input
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String genre = (String) genreCombo.getSelectedItem();
                String isbn = isbnField.getText().trim();
                String priceStr = priceField.getText().trim();
                int stock = (int) stockSpinner.getValue();
                String description = descArea.getText().trim();
                
                if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || priceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please fill in all required fields.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate price format
                double price;
                try {
                    price = Double.parseDouble(priceStr);
                    if (price < 0) {
                        throw new NumberFormatException("Price must be positive");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid price (e.g., 29.99).",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update book using the controller
                boolean success = BookController.updateBook(
                    bookId, title, author, genre, isbn, price, stock, description);
                
                if (success) {
                    dialog.dispose();
                    loadInventoryData(); // Reload inventory
                    JOptionPane.showMessageDialog(this,
                        "Book updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Error updating book.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            dialog.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error editing book: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void importBooksFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Import books from CSV
            int count = BookController.importBooksFromCSV(file, "");
            
            if (count > 0) {
                loadInventoryData(); // Reload inventory
                JOptionPane.showMessageDialog(this,
                    count + " books imported successfully.",
                    "Import Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No books were imported. Please check the CSV format.",
                    "Import Failed",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    /**
     * Custom renderer for the action button in the table
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Edit/Delete");
            setOpaque(true);
            setBackground(MEDIUM_GREEN);
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    /**
     * Custom editor for the action button in the table
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(MEDIUM_GREEN);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            
            // Add button action
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Edit/Delete" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get the book ID from the first column
                int row = bookTable.getSelectedRow();
                String bookId = (String) bookTable.getValueAt(row, 0);
                
                // Show edit dialog
                showEditBookDialog(bookId);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
class AddBooksPanel extends JPanel {
    private SupplierDashboard dashboard;
    private JTextField titleField;
    private JTextField authorField;
    private JComboBox<String> genreCombo;
    private JTextField isbnField;
    private JTextField priceField;
    private JSpinner stockSpinner;
    private JTextArea descriptionArea;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public AddBooksPanel(SupplierDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Add New Book");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Book information form
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel, gbc);
        
        titleField = new JTextField(30);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(titleField, gbc);
        
        // Author
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(authorLabel, gbc);
        
        authorField = new JTextField(30);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(authorField, gbc);
        
        // Genre
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel genreLabel = new JLabel("Genre:");
        genreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(genreLabel, gbc);
        
        String[] genres = {"Fiction", "Non-Fiction", "Science", "Programming", 
                          "History", "Biography", "Business", "Children", "Other"};
        genreCombo = new JComboBox<>(genres);
        gbc.gridx = 1;
        panel.add(genreCombo, gbc);
        
        // ISBN
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel isbnLabel = new JLabel("ISBN:");
        isbnLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(isbnLabel, gbc);
        
        isbnField = new JTextField(30);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);
        
        // ISBN info
        gbc.gridx = 1;
        gbc.gridy = 4;
        JLabel isbnInfoLabel = new JLabel("Format: 13 digits without dashes (e.g., 9781234567890)");
        isbnInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        isbnInfoLabel.setForeground(Color.GRAY);
        panel.add(isbnInfoLabel, gbc);
        
        // Price
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(priceLabel, gbc);
        
        priceField = new JTextField(30);
        gbc.gridx = 1;
        panel.add(priceField, gbc);
        
        // Price info
        gbc.gridx = 1;
        gbc.gridy = 6;
        JLabel priceInfoLabel = new JLabel("Format: Use decimal point (e.g., 29.99)");
        priceInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        priceInfoLabel.setForeground(Color.GRAY);
        panel.add(priceInfoLabel, gbc);
        
        // Stock
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel stockLabel = new JLabel("Initial Stock:");
        stockLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(stockLabel, gbc);
        
        stockSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        gbc.gridx = 1;
        panel.add(stockSpinner, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(descLabel, gbc);
        
        descriptionArea = new JTextArea(8, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        
        gbc.gridx = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(descScroll, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton importButton = new JButton("Import from CSV");
        importButton.setBackground(new Color(70, 130, 180)); // Steel blue
        importButton.setForeground(Color.WHITE);
        importButton.setFocusPainted(false);
        importButton.addActionListener(e -> importBooksFromCSV());
        
        JButton clearButton = new JButton("Clear Form");
        clearButton.setBackground(Color.LIGHT_GRAY);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearForm());
        
        JButton addButton = new JButton("Add Book");
        addButton.setBackground(MEDIUM_GREEN);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addBook());
        
        panel.add(importButton);
        panel.add(clearButton);
        panel.add(addButton);
        
        return panel;
    }
    
    private void addBook() {
        // Validate input
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre = (String) genreCombo.getSelectedItem();
        String isbn = isbnField.getText().trim();
        String priceStr = priceField.getText().trim();
        int stock = (int) stockSpinner.getValue();
        String description = descriptionArea.getText().trim();
        
        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate ISBN format
        try{
            if (!isbn.matches("\\d{13}")) {
                throw new Exception("ISBN must be exactly 13 digits without dashes.");
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        // Validate price format
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                throw new NumberFormatException("Price must be positive");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid price (e.g., 29.99).",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Add book using the controller
        boolean success = BookController.addBook(
            title, author, genre, isbn, price, stock, 
            dashboard.getCurrentUser().getUserId(), description);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Book added successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error adding book. ISBN may already exist.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        genreCombo.setSelectedIndex(0);
        isbnField.setText("");
        priceField.setText("");
        stockSpinner.setValue(1);
        descriptionArea.setText("");
        
        // Set focus back to title field
        titleField.requestFocus();
    }
    
    private void importBooksFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Show CSV format info dialog
            JOptionPane.showMessageDialog(this,
                "CSV File Format:\n" +
                "Title,Author,Genre,ISBN,Price,Stock,Description\n\n" +
                "Example:\n" +
                "Java Programming,John Doe,Programming,9781234567890,29.99,10,A comprehensive guide to Java\n\n" +
                "First line should be column headers.",
                "CSV Format Information",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Import books from CSV
            int count = BookController.importBooksFromCSV(
                file, dashboard.getCurrentUser().getUserId());
            
            if (count > 0) {
                JOptionPane.showMessageDialog(this,
                    count + " books imported successfully.",
                    "Import Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No books were imported. Please check the CSV format.",
                    "Import Failed",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
class StockUpdatePanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private SupplierDashboard dashboard;
    private JTextField searchField;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public StockUpdatePanel(SupplierDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create book table
        createBookTable();
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title on the left
        JLabel titleLabel = new JLabel("Update Stock Levels");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        
        // Search panel on the right
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(MEDIUM_GREEN);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        searchPanel.add(new JLabel("Search Title:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add search action
        searchButton.addActionListener(e -> searchBooks());
        searchField.addActionListener(e -> searchBooks()); // Search on Enter key
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void createBookTable() {
        // Column names
        String[] columns = {"Book ID", "Title", "Author", "Current Stock", "Add Stock", "Actions"};
        
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; // Only Add Stock and Actions columns are editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return Integer.class; // For spinner in Add Stock column
                }
                return super.getColumnClass(columnIndex);
            }
        };
        
        // Add columns to model
        for (String column : columns) {
            tableModel.addColumn(column);
        }
        
        bookTable = new JTable(tableModel);
        
        // Style the table
        bookTable.setRowHeight(30);
        bookTable.setIntercellSpacing(new Dimension(10, 5));
        bookTable.setShowGrid(false);
        bookTable.setBackground(Color.WHITE);
        
        // Set up column widths
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Book ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Current Stock
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Add Stock
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Actions
        
        // Hide the Book ID column
        bookTable.getColumnModel().getColumn(0).setMinWidth(0);
        bookTable.getColumnModel().getColumn(0).setMaxWidth(0);
        bookTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Custom renderer for stock column to highlight low stock
        bookTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                int stock = Integer.parseInt(value.toString());
                if (stock < 5) {
                    c.setForeground(Color.RED);
                } else if (stock < 10) {
                    c.setForeground(new Color(255, 165, 0)); // Orange
                } else {
                    c.setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
        
        // Add spinner to Add Stock column
        bookTable.getColumnModel().getColumn(4).setCellRenderer(new SpinnerRenderer());
        bookTable.getColumnModel().getColumn(4).setCellEditor(new SpinnerEditor());
        
        // Set up the action button renderer and editor
        bookTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        bookTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor());
        
        // Set header style
        JTableHeader header = bookTable.getTableHeader();
        header.setBackground(LIGHT_GREEN);
        header.setForeground(DARK_GREEN);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Load book data
        loadBookData();
    }
    
    public void loadBookData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get supplier ID
        String supplierId = dashboard.getCurrentUser().getUserId();
        
        // Load books from database
        try {
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(
                "SELECT book_id, title, author, stock FROM books WHERE supplier_id = ? ORDER BY title");
            stmt.setString(1, supplierId);
            java.sql.ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("stock"),
                    0, // Default add stock value
                    "Update"
                });
            }
            
            // Show message if no books found
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{
                    "", "You haven't added any books yet.", "", "", 0, ""
                });
                bookTable.setEnabled(false);
            } else {
                bookTable.setEnabled(true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading book data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadBookData(); // Load all books if search is empty
            return;
        }
        
        // Get supplier ID
        String supplierId = dashboard.getCurrentUser().getUserId();
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(
                "SELECT book_id, title, author, stock FROM books " +
                "WHERE supplier_id = ? AND LOWER(title) LIKE ? ORDER BY title");
            stmt.setString(1, supplierId);
            stmt.setString(2, "%" + searchTerm.toLowerCase() + "%");
            
            java.sql.ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("stock"),
                    0, // Default add stock value
                    "Update"
                });
            }
            
            // Show message if no results
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{
                    "", "No books found matching your search.", "", "", 0, ""
                });
                bookTable.setEnabled(false);
            } else {
                bookTable.setEnabled(true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error searching books: " + e.getMessage(),
                "Search Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStock(int row) {
        String bookId = (String) tableModel.getValueAt(row, 0);
        int additionalStock = (Integer) tableModel.getValueAt(row, 4);
        
        if (additionalStock <= 0) {
            JOptionPane.showMessageDialog(this,
                "Please enter a positive number for additional stock.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update stock using controller
        boolean success = BookController.updateBookStock(
            bookId, dashboard.getCurrentUser().getUserId(), additionalStock);
        
        if (success) {
            // Update table with new stock level
            int currentStock = (Integer) tableModel.getValueAt(row, 3);
            tableModel.setValueAt(currentStock + additionalStock, row, 3);
            tableModel.setValueAt(0, row, 4); // Reset additional stock field
            
            JOptionPane.showMessageDialog(this,
                "Stock updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Error updating stock. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Custom renderer for the spinner in the Add Stock column
     */
    private class SpinnerRenderer extends DefaultTableCellRenderer {
        private JSpinner spinner;
        
        public SpinnerRenderer() {
            spinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
            spinner.setBorder(BorderFactory.createEmptyBorder());
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            spinner.setValue(value);
            return spinner;
        }
    }
    
    /**
     * Custom editor for the spinner in the Add Stock column
     */
    private class SpinnerEditor extends DefaultCellEditor {
        private JSpinner spinner;
        private JTextField textField;
        
        public SpinnerEditor() {
            super(new JTextField());
            spinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
            textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            spinner.setValue(value);
            return spinner;
        }
        
        @Override
        public boolean stopCellEditing() {
            try {
                spinner.commitEdit();
            } catch (java.text.ParseException e) {
                return false;
            }
            return super.stopCellEditing();
        }
        
        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }
    
    /**
     * Custom renderer for the action button in the table
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Update");
            setOpaque(true);
            setBackground(MEDIUM_GREEN);
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    /**
     * Custom editor for the action button in the table
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(MEDIUM_GREEN);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            
            // Add button action
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Update" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Update stock
                int row = bookTable.getSelectedRow();
                updateStock(row);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
class BookCatalogPanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCategoryCombo;
    private CustomerDashboard dashboard;
    
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    public BookCatalogPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel with title and search
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create book table
        createBookTable();
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
        
        // Load initial book data
        loadBookData();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title label
        JLabel titleLabel = new JLabel("Book Catalog");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        
        String[] categories = {"Title", "Author", "Genre", "ISBN", "All"};
        searchCategoryCombo = new JComboBox<>(categories);
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(MEDIUM_GREEN);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchCategoryCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add search action
        searchButton.addActionListener(e -> searchBooks());
        searchField.addActionListener(e -> searchBooks()); // Search on Enter key
        
        panel.add(searchPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void createBookTable() {
        // Column names
        String[] columns = {"Title", "Author", "Genre", "Price", "Rating", "Actions", "BookID"};
        
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5; // Only Actions column is editable
            }
        };
        
        // Add columns to model
        for (String column : columns) {
            tableModel.addColumn(column);
        }
        
        bookTable = new JTable(tableModel);
        
        // Style the table
        bookTable.setRowHeight(30);
        bookTable.setIntercellSpacing(new Dimension(10, 5));
        bookTable.setShowGrid(false);
        bookTable.setBackground(Color.WHITE);
        
        // Set up column widths
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(250); // Title
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Author
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Genre
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Price
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Rating
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Actions
        
        // Hide the BookID column
        bookTable.getColumnModel().getColumn(6).setMinWidth(0);
        bookTable.getColumnModel().getColumn(6).setMaxWidth(0);
        bookTable.getColumnModel().getColumn(6).setWidth(0);
        
        // Set up the action button renderer and editor
        bookTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("View Details"));
        bookTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(dashboard));
        
        // Set header style
        JTableHeader header = bookTable.getTableHeader();
        header.setBackground(LIGHT_GREEN);
        header.setForeground(DARK_GREEN);
        header.setFont(new Font("Arial", Font.BOLD, 14));
    }
    
    private void loadBookData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load books from database
        try {
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            java.sql.Statement stmt = conn.createStatement();
            
            String sql = "SELECT b.book_id, b.title, b.author, b.genre, b.price, " +
                         "COALESCE(AVG(r.rating), 0) as rating " +
                         "FROM books b " +
                         "LEFT JOIN reviews r ON b.book_id = r.book_id " +
                         "WHERE b.stock > 0 " +
                         "GROUP BY b.book_id, b.title, b.author, b.genre, b.price " +
                         "ORDER BY b.title";
            
            java.sql.ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String bookId = rs.getString("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String genre = rs.getString("genre");
                double price = rs.getDouble("price");
                double rating = rs.getDouble("rating");
                
                // Format rating as stars
                String ratingStr = String.format("%.1f ★", rating);
                
                // Format price as currency
                String priceStr = String.format("$%.2f", price);
                
                // Add row to table
                tableModel.addRow(new Object[]{
                    title, author, genre, priceStr, ratingStr, "View Details", bookId
                });
            }
            
        } catch (Exception e) {
            System.err.println("Error loading book data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading book catalog. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadBookData(); // Load all books if search is empty
            return;
        }
        
        String category = (String) searchCategoryCombo.getSelectedItem();
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Construct SQL based on search category
            String sql;
            java.sql.PreparedStatement stmt;
            java.sql.Connection conn = DatabaseConnector.getInstance().getConnection();
            
            if (category.equals("All")) {
                sql = "SELECT b.book_id, b.title, b.author, b.genre, b.price, " +
                      "COALESCE(AVG(r.rating), 0) as rating " +
                      "FROM books b " +
                      "LEFT JOIN reviews r ON b.book_id = r.book_id " +
                      "WHERE b.stock > 0 AND " +
                      "(LOWER(b.title) LIKE ? OR LOWER(b.author) LIKE ? OR LOWER(b.genre) LIKE ?) " +
                      "GROUP BY b.book_id, b.title, b.author, b.genre, b.price " +
                      "ORDER BY b.title";
                stmt = conn.prepareStatement(sql);
                
                String searchPattern = "%" + searchTerm.toLowerCase() + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
            } else {
                sql = "SELECT b.book_id, b.title, b.author, b.genre, b.price, " +
                      "COALESCE(AVG(r.rating), 0) as rating " +
                      "FROM books b " +
                      "LEFT JOIN reviews r ON b.book_id = r.book_id " +
                      "WHERE b.stock > 0 AND " +
                      "LOWER(b." + category.toLowerCase() + ") LIKE ? " +
                      "GROUP BY b.book_id, b.title, b.author, b.genre, b.price " +
                      "ORDER BY b.title";
                stmt = conn.prepareStatement(sql);
                
                stmt.setString(1, "%" + searchTerm.toLowerCase() + "%");
            }
            
            java.sql.ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String bookId = rs.getString("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String genre = rs.getString("genre");
                double price = rs.getDouble("price");
                double rating = rs.getDouble("rating");
                
                // Format rating as stars
                String ratingStr = String.format("%.1f ★", rating);
                
                // Format price as currency
                String priceStr = String.format("$%.2f", price);
                
                // Add row to table
                tableModel.addRow(new Object[]{
                    title, author, genre, priceStr, ratingStr, "View Details", bookId
                });
            }
            
            // Show message if no results
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No books found matching your search.",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Reload all books
                loadBookData();
            }
            
        } catch (Exception e) {
            System.err.println("Error searching books: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error searching the book catalog. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Custom renderer for the action button in the table
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
            setBackground(MEDIUM_GREEN);
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    /**
     * Custom editor for the action button in the table
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private CustomerDashboard dashboard;
        
        public ButtonEditor(CustomerDashboard dashboard) {
            super(new JCheckBox());
            this.dashboard = dashboard;
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(MEDIUM_GREEN);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            
            // Add button action
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "View Details" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get the book ID from the hidden column
                int row = bookTable.getSelectedRow();
                String bookId = (String) bookTable.getValueAt(row, 6);
                
                // Show book details
                dashboard.showBookDetails(bookId);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
class SalesReportPanel extends JPanel {
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    // UI Components
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JLabel totalRevenueLabel;
    private JLabel totalOrdersLabel;
    private JLabel avgOrderValueLabel;
    private JLabel bestSellingLabel;
    
    // Date selection
    private JComboBox<String> periodSelector;
    private JTextField startDateField;
    private JTextField endDateField;
    
    // Admin reference
    private AdminDashboard dashboard;
    private Administrator admin;
    
    /**
     * Constructor
     * @param dashboard reference to the parent AdminDashboard
     */
    public SalesReportPanel(AdminDashboard dashboard) {
        this.dashboard = dashboard;
        this.admin = dashboard.getCurrentUser();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel with title and filters
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel (split for table and stats)
        JSplitPane splitPane = createMainContentPanel();
        add(splitPane, BorderLayout.CENTER);
        
        // Load initial data (default: current month)
        loadReportData(getStartOfMonth(), new Date());
    }
    
    /**
     * Creates the header panel with title, date filters, and actions
     * @return the configured header panel
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title and period selection on the left
        JPanel leftPanel = new JPanel(new BorderLayout(15, 0));
        leftPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Sales Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        leftPanel.add(titleLabel, BorderLayout.WEST);
        
        // Period selector
        JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        periodPanel.setBackground(Color.WHITE);
        
        // Predefined periods
        periodSelector = new JComboBox<>(new String[]{
            "Today", "Last 7 Days", "This Month", "Last Month", "Last 3 Months", "This Year", "Custom"
        });
        periodSelector.setPreferredSize(new Dimension(120, 30));
        periodPanel.add(new JLabel("Period:"));
        periodPanel.add(periodSelector);
        
        // Date fields for custom period
        startDateField = new JTextField(10);
        startDateField.setToolTipText("Format: YYYY-MM-DD");
        startDateField.setVisible(false);
        
        endDateField = new JTextField(10);
        endDateField.setToolTipText("Format: YYYY-MM-DD");
        endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        endDateField.setVisible(false);
        
        periodPanel.add(new JLabel("From:"));
        periodPanel.add(startDateField);
        periodPanel.add(new JLabel("To:"));
        periodPanel.add(endDateField);
        
        // Apply button for custom period
        JButton applyButton = new JButton("Apply");
        applyButton.setBackground(MEDIUM_GREEN);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setVisible(false);
        periodPanel.add(applyButton);
        
        leftPanel.add(periodPanel, BorderLayout.CENTER);
        
        // Actions panel on the right
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(MEDIUM_GREEN);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        
        JButton exportButton = new JButton("Export Report");
        exportButton.setBackground(MEDIUM_GREEN);
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        
        actionsPanel.add(refreshButton);
        actionsPanel.add(exportButton);
        
        // Add panels to header
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(actionsPanel, BorderLayout.EAST);
        
        // Add listeners
        periodSelector.addActionListener(e -> handlePeriodSelection());
        applyButton.addActionListener(e -> applyCustomDateRange());
        refreshButton.addActionListener(e -> refreshReport());
        exportButton.addActionListener(e -> exportReport());
        
        return panel;
    }
    
    /**
     * Creates the main content panel with sales table and statistics
     * @return the configured JSplitPane
     */
    private JSplitPane createMainContentPanel() {
        // Create a split pane with table on left and stats on right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setBackground(Color.WHITE);
        splitPane.setDividerSize(8);
        
        // Create table panel (left)
        JPanel tablePanel = createTablePanel();
        
        // Create statistics panel (right)
        JPanel statsPanel = createStatsPanel();
        
        // Add to split pane
        splitPane.setLeftComponent(tablePanel);
        splitPane.setRightComponent(statsPanel);
        
        return splitPane;
    }
    
    /**
     * Creates the sales table panel
     * @return the configured table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MEDIUM_GREEN),
                "Sales Data"
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Create table with sales data
        String[] columns = {"Date", "Orders", "Books Sold", "Revenue", "Top Selling Book"};
        tableModel = new DefaultTableModel(columns, 0);
        salesTable = new JTable(tableModel);
        
        // Style the table
        salesTable.setRowHeight(30);
        salesTable.setIntercellSpacing(new Dimension(10, 5));
        salesTable.setShowGrid(false);
        salesTable.setBackground(Color.WHITE);
        
        // Set column widths and renderers
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(70);  // Orders
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Books Sold
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Revenue
        salesTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Top Book
        
        // Revenue column renderer (right-aligned)
        salesTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.RIGHT);
            }
        });
        
        // Set header style
        JTableHeader header = salesTable.getTableHeader();
        header.setBackground(LIGHT_GREEN);
        header.setForeground(DARK_GREEN);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        
        panel.add(new JScrollPane(salesTable), BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the statistics panel
     * @return the configured statistics panel
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MEDIUM_GREEN),
                "Summary Statistics"
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Create grid for stats cards
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        statsGrid.setBackground(Color.WHITE);
        
        // Create stat cards
        JPanel revenueCard = createStatCard("Total Revenue", "$0.00");
        totalRevenueLabel = (JLabel) revenueCard.getComponent(2);
        
        JPanel ordersCard = createStatCard("Total Orders", "0");
        totalOrdersLabel = (JLabel) ordersCard.getComponent(2);
        
        JPanel avgOrderCard = createStatCard("Avg. Order Value", "$0.00");
        avgOrderValueLabel = (JLabel) avgOrderCard.getComponent(2);
        
        JPanel bestSellingCard = createStatCard("Best Selling", "None");
        bestSellingLabel = (JLabel) bestSellingCard.getComponent(2);
        
        // Add cards to grid
        statsGrid.add(revenueCard);
        statsGrid.add(ordersCard);
        statsGrid.add(avgOrderCard);
        statsGrid.add(bestSellingCard);
        
        // Add alternative visualization (text-based summary)
        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setBackground(panel.getBackground());
        summaryArea.setFont(new Font("Arial", Font.PLAIN, 14));
        summaryArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        summaryArea.setText(
            "Sales Summary:\n\n" +
            "This panel shows summary statistics for the selected time period. " +
            "You can change the time period using the dropdown at the top of the screen.\n\n" +
            "The table shows daily or monthly sales data, depending on the selected period. " +
            "Use the Export button to save this report as a PDF file."
        );
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(summaryArea, BorderLayout.CENTER);
        
        panel.add(statsGrid, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates a statistics card
     * @param title The card title
     * @param value The initial value
     * @return The configured JPanel for the stat card
     */
    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(LIGHT_GREEN);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(DARK_GREEN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);
        
        return card;
    }
    
    /**
     * Handles the period selection from the dropdown
     */
    private void handlePeriodSelection() {
        String selectedPeriod = (String) periodSelector.getSelectedItem();
        
        // Show/hide custom date fields based on selection
        boolean isCustom = "Custom".equals(selectedPeriod);
        startDateField.setVisible(isCustom);
        endDateField.setVisible(isCustom);
        Component applyButton = ((Container)periodSelector.getParent()).getComponent(6); // The Apply button
        applyButton.setVisible(isCustom);
        
        if (!isCustom) {
            // Determine date range based on selection
            Date startDate = null;
            Date endDate = new Date(); // End date is always today
            
            Calendar cal = Calendar.getInstance();
            
            switch (selectedPeriod) {
                case "Today":
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    startDate = cal.getTime();
                    break;
                case "Last 7 Days":
                    cal.add(Calendar.DAY_OF_MONTH, -6);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    startDate = cal.getTime();
                    break;
                case "This Month":
                    startDate = getStartOfMonth();
                    break;
                case "Last Month":
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.MONTH, -1);
                    startDate = cal.getTime();
                    
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    endDate = cal.getTime();
                    break;
                case "Last 3 Months":
                    cal.add(Calendar.MONTH, -2);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    startDate = cal.getTime();
                    break;
                case "This Year":
                    cal.set(Calendar.DAY_OF_YEAR, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    startDate = cal.getTime();
                    break;
            }
            
            loadReportData(startDate, endDate);
        }
    }
    
    /**
     * Applies the custom date range selected by the user
     */
    private void applyCustomDateRange() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        
        try {
            startDate = dateFormat.parse(startDateField.getText());
            endDate = dateFormat.parse(endDateField.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid dates in format YYYY-MM-DD.",
                "Date Format Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (startDate.after(endDate)) {
            JOptionPane.showMessageDialog(this,
                "Start date cannot be after end date.",
                "Date Selection Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        loadReportData(startDate, endDate);
    }
    
    /**
     * Refreshes the current report
     */
    private void refreshReport() {
        handlePeriodSelection();
    }
    
    /**
     * Exports the current report to a file
     */
    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Sales Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        fileChooser.setSelectedFile(new File("Sales_Report.pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Ensure file has .pdf extension
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }
            
            // Generate report using the Report class
            try {
                // Get date range from current data
                Date startDate = getStartDateFromSelection();
                Date endDate = getEndDateFromSelection();
                
                // Generate report using administrator
                Report report = admin.generateSalesReport(startDate, endDate);
                
                // Export the report
                boolean success = ReportController.exportReportToFile(report, file);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Report exported successfully to: " + file.getAbsolutePath(),
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to export report.",
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Gets the start date based on current selection
     */
    private Date getStartDateFromSelection() {
        String selectedPeriod = (String) periodSelector.getSelectedItem();
        
        if ("Custom".equals(selectedPeriod)) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(startDateField.getText());
            } catch (ParseException e) {
                return new Date(); // Fallback to today if parsing fails
            }
        } else {
            // Reuse the logic from handlePeriodSelection
            Calendar cal = Calendar.getInstance();
            
            switch (selectedPeriod) {
                case "Today":
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    return cal.getTime();
                case "Last 7 Days":
                    cal.add(Calendar.DAY_OF_MONTH, -6);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    return cal.getTime();
                case "This Month":
                    return getStartOfMonth();
                case "Last Month":
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.MONTH, -1);
                    return cal.getTime();
                case "Last 3 Months":
                    cal.add(Calendar.MONTH, -2);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    return cal.getTime();
                case "This Year":
                    cal.set(Calendar.DAY_OF_YEAR, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    return cal.getTime();
                default:
                    return new Date(); // Fallback
            }
        }
    }
    
    /**
     * Gets the end date based on current selection
     */
    private Date getEndDateFromSelection() {
        String selectedPeriod = (String) periodSelector.getSelectedItem();
        
        if ("Custom".equals(selectedPeriod)) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(endDateField.getText());
            } catch (ParseException e) {
                return new Date(); // Fallback to today if parsing fails
            }
        } else if ("Last Month".equals(selectedPeriod)) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            return cal.getTime();
        } else {
            return new Date(); // Default to current date for all other options
        }
    }
    
    /**
     * Loads report data for the given date range
     * @param startDate Start date for the report
     * @param endDate End date for the report
     */
    private void loadReportData(Date startDate, Date endDate) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Load data for the table
            OrderController.populateSalesTable(salesTable, startDate, endDate);
            
            // Update summary statistics
            updateSummaryStatistics(startDate, endDate);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading report data: " + e.getMessage(),
                "Data Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Updates the summary statistics
     * @param startDate Start date for the statistics
     * @param endDate End date for the statistics
     */
    private void updateSummaryStatistics(Date startDate, Date endDate) {
        try {
            // Get total revenue
            double totalRevenue = OrderController.getTotalRevenue(startDate, endDate);
            totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
            
            // Get total orders
            int totalOrders = OrderController.getTotalOrders(startDate, endDate);
            totalOrdersLabel.setText(String.valueOf(totalOrders));
            
            // Calculate average order value
            double avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;
            avgOrderValueLabel.setText(String.format("$%.2f", avgOrderValue));
            
            // Get best selling book
            String[] popularBooks = ReportController.getMostPopularBooks(startDate, endDate, 1);
            if (popularBooks.length > 0 && popularBooks[0] != null) {
                bestSellingLabel.setText(popularBooks[0]);
            } else {
                bestSellingLabel.setText("None");
            }
            
            // Update summary area with date range
            // First get the text area component
            JPanel mainPanel = (JPanel) getComponents()[0];
            JPanel contentPanel = (JPanel) mainPanel.getComponents()[1];
            BorderLayout layout = (BorderLayout)contentPanel.getLayout();
            JPanel southPanel = (JPanel) layout.getLayoutComponent(BorderLayout.SOUTH);
            JTextArea summaryArea = (JTextArea) southPanel.getComponent(0);

            // Now update the text area with date range information
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            summaryArea.setText(
             "Sales Summary for: " + dateFormat.format(startDate) + " to " + dateFormat.format(endDate) + "\n\n" +
             "Total Revenue: $" + String.format("%.2f", totalRevenue) + "\n" +
             "Total Orders: " + totalOrders + "\n" +
             "Average Order Value: $" + String.format("%.2f", avgOrderValue) + "\n" +
             "Best Selling Book: " + (popularBooks.length > 0 && popularBooks[0] != null ? popularBooks[0] : "None") + "\n\n" +
             "Use the Export button to save this report as a PDF file."
            );
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the start of the current month
     * @return Date object for the first day of the current month
     */
    private Date getStartOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
}

class UserManagementPanel extends JPanel {
    // Color scheme
    private static final Color DARK_GREEN = new Color(0, 51, 25);
    private static final Color MEDIUM_GREEN = new Color(0, 102, 51);
    private static final Color LIGHT_GREEN = new Color(230, 255, 230);
    
    // UI Components
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> roleFilterCombo;
    private JComboBox<String> statusFilterCombo;
    
    // Admin reference
    private AdminDashboard dashboard;
    private Administrator admin;
    
    /**
     * Constructor
     * @param dashboard reference to the parent AdminDashboard
     */
    public UserManagementPanel(AdminDashboard dashboard) {
        this.dashboard = dashboard;
        this.admin = dashboard.getCurrentUser();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel with title and filters
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create table panel for user data
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Load initial data
        loadUserData();
    }
    
    /**
     * Creates the header panel with title, search, and filters
     * @return the configured header panel
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title label
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_GREEN);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Create search and filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(Color.WHITE);
        
        // Role filter
        JLabel roleLabel = new JLabel("Role:");
        roleFilterCombo = new JComboBox<>(new String[]{"All", "Customer", "Supplier", "Administrator"});
        roleFilterCombo.setPreferredSize(new Dimension(120, 30));
        
        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusFilterCombo = new JComboBox<>(new String[]{"All", "Active", "Blocked"});
        statusFilterCombo.setPreferredSize(new Dimension(120, 30));
        
        // Search field
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        
        // Search button
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(MEDIUM_GREEN);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(MEDIUM_GREEN);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        
        // Add components to filter panel
        filterPanel.add(roleLabel);
        filterPanel.add(roleFilterCombo);
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilterCombo);
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(refreshButton);
        
        panel.add(filterPanel, BorderLayout.EAST);
        
        // Add listeners
        searchButton.addActionListener(e -> filterUsers());
        refreshButton.addActionListener(e -> loadUserData());
        roleFilterCombo.addActionListener(e -> filterUsers());
        statusFilterCombo.addActionListener(e -> filterUsers());
        searchField.addActionListener(e -> filterUsers()); // Search on Enter key
        
        return panel;
    }
    
    /**
     * Creates the table panel with user data
     * @return the configured table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Create table with user data columns
        String[] columns = {"User ID", "Name", "Email", "Role", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is editable
            }
        };
        userTable = new JTable(tableModel);
        
        // Style the table
        userTable.setRowHeight(35);
        userTable.setIntercellSpacing(new Dimension(10, 5));
        userTable.setShowGrid(false);
        userTable.setBackground(Color.WHITE);
        
        // Set column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // User ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        userTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Email
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Role
        userTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Status
        userTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Actions
        
        // Set custom renderer for Status column
        userTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        
        // Set custom renderer and editor for Actions column
        userTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        userTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor());
        
        // Set header style
        JTableHeader header = userTable.getTableHeader();
        header.setBackground(LIGHT_GREEN);
        header.setForeground(DARK_GREEN);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Loads user data into the table
     */
    private void loadUserData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Get all users
            List<User> users = admin.getAllUsers();
            
            // Add users to table
            for (User user : users) {
                // Don't show the current admin
                if (user.getUserId().equals(admin.getUserId())) {
                    continue;
                }
                
                // Add row with user data
                tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.isActive() ? "Active" : "Blocked",
                    user.isActive() ? "Block" : "Unblock"
                });
            }
            
            // Show message if no users found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No users found in the system.",
                    "No Users",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading user data: " + e.getMessage(),
                "Data Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Filters the users table based on search criteria and filter selections
     */
    private void filterUsers() {
        // Get filter values
        String roleFilter = (String) roleFilterCombo.getSelectedItem();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        String searchText = searchField.getText().toLowerCase();
        
        // If all filters are default and search is empty, just reload all data
        if ((roleFilter.equals("All") && statusFilter.equals("All") && searchText.isEmpty())) {
            loadUserData();
            return;
        }
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Get all users
            List<User> users = admin.getAllUsers();
            
            // Filter and add matching users to table
            for (User user : users) {
                // Don't show the current admin
                if (user.getUserId().equals(admin.getUserId())) {
                    continue;
                }
                
                // Apply role filter
                if (!roleFilter.equals("All") && !user.getRole().equals(roleFilter)) {
                    continue;
                }
                
                // Apply status filter
                if (statusFilter.equals("Active") && !user.isActive()) {
                    continue;
                } else if (statusFilter.equals("Blocked") && user.isActive()) {
                    continue;
                }
                
                // Apply search filter (case insensitive)
                if (!searchText.isEmpty()) {
                    boolean matches = 
                        user.getName().toLowerCase().contains(searchText) ||
                        user.getEmail().toLowerCase().contains(searchText) ||
                        user.getUserId().toLowerCase().contains(searchText);
                    
                    if (!matches) {
                        continue;
                    }
                }
                
                // User passed all filters, add to table
                tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.isActive() ? "Active" : "Blocked",
                    user.isActive() ? "Block" : "Unblock"
                });
            }
            
            // Show message if no users match the filter
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No users match the selected filters.",
                    "No Matching Users",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error filtering user data: " + e.getMessage(),
                "Filter Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Shows the user details dialog
     * @param userId ID of the user to show details for
     */
    private void showUserDetails(String userId) {
        // Get user details
        String userDetails = UserController.getUserDetails(userId);
        
        // Show dialog with user details
        JOptionPane.showMessageDialog(this,
            userDetails,
            "User Details",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Changes a user's status (block/unblock)
     * @param userId ID of the user to change status
     * @param newStatus true to activate, false to block
     */
    private void changeUserStatus(String userId, boolean newStatus) {
        // Confirm with admin
        String action = newStatus ? "unblock" : "block";
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to " + action + " this user?",
            "Confirm " + (action.substring(0, 1).toUpperCase() + action.substring(1)),
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Change status
        boolean success = UserController.changeUserStatus(userId, newStatus);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "User has been " + (newStatus ? "unblocked" : "blocked") + " successfully.",
                "Status Changed",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh data
            loadUserData();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to " + action + " user. Please try again.",
                "Status Change Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Custom renderer for the Status column
     */
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
            
            String status = (String) value;
            if (status.equals("Active")) {
                setForeground(new Color(0, 128, 0)); // Dark green for active
            } else {
                setForeground(new Color(204, 0, 0)); // Dark red for blocked
            }
            
            return c;
        }
    }
    
    /**
     * Custom renderer for the action buttons in the table
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBorderPainted(false);
            setFocusPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            setText((value == null) ? "" : value.toString());
            
            // Set color based on action (red for Block, green for Unblock)
            if ("Block".equals(value)) {
                setBackground(new Color(204, 0, 0)); // Dark red
            } else {
                setBackground(new Color(0, 128, 0)); // Dark green
            }
            setForeground(Color.WHITE);
            
            return this;
        }
    }
    
    /**
     * Custom editor for the action buttons in the table
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            
            // Add button action
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            
            // Set color based on action
            if ("Block".equals(label)) {
                button.setBackground(new Color(204, 0, 0)); // Dark red
            } else {
                button.setBackground(new Color(0, 128, 0)); // Dark green
            }
            button.setForeground(Color.WHITE);
            
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get user ID from the first column
                int row = userTable.getEditingRow();
                String userId = (String) userTable.getValueAt(row, 0);
                
                // Perform action based on button label
                if ("Block".equals(label)) {
                    changeUserStatus(userId, false); // Block the user
                } else if ("Unblock".equals(label)) {
                    changeUserStatus(userId, true); // Unblock the user
                } else {
                    // Should not happen
                    System.err.println("Unknown action: " + label);
                }
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    /**
     * Inner class for the user edit dialog
     */
    private class UserEditDialog extends JDialog {
        private JTextField nameField;
        private JTextField emailField;
        private JPasswordField passwordField;
        private JComboBox<String> roleCombo;
        private JCheckBox activeCheckbox;
        private User user;
        
        public UserEditDialog(Frame owner, User user) {
            super(owner, "Edit User", true);
            this.user = user;
            
            // Set up dialog
            setSize(400, 300);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());
            
            // Create form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Name field
            formPanel.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            nameField = new JTextField(user.getName(), 20);
            formPanel.add(nameField, gbc);
            
            // Email field
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0.0;
            formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            emailField = new JTextField(user.getEmail(), 20);
            formPanel.add(emailField, gbc);
            
            // Password field
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0.0;
            formPanel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            passwordField = new JPasswordField(20);
            passwordField.setText(user.getPassword());
            formPanel.add(passwordField, gbc);
            
            // Role combo (disabled - can't change roles)
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 0.0;
            formPanel.add(new JLabel("Role:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            roleCombo = new JComboBox<>(new String[]{"Customer", "Supplier", "Administrator"});
            roleCombo.setSelectedItem(user.getRole());
            roleCombo.setEnabled(false); // Don't allow role changes
            formPanel.add(roleCombo, gbc);
            
            // Active checkbox
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.weightx = 0.0;
            formPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            activeCheckbox = new JCheckBox("Active");
            activeCheckbox.setSelected(user.isActive());
            formPanel.add(activeCheckbox, gbc);
            
            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelButton = new JButton("Cancel");
            JButton saveButton = new JButton("Save Changes");
            
            cancelButton.addActionListener(e -> dispose());
            saveButton.addActionListener(e -> {
                saveChanges();
                dispose();
            });
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            
            // Add panels to dialog
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void saveChanges() {
            // Get values from form
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            boolean active = activeCheckbox.isSelected();
            
            // Update user object
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setActive(active);
            
            // Save changes
            boolean success = user.save();
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "User updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the user table
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update user. Please try again.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 }
}