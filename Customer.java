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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Customer subclass - represents a user who can browse and purchase books
 * Corresponds to the Customer table in the database (extends User data)
 */
public class Customer extends User {
    // Customer-specific attributes
    private ShoppingCart cart;
    
    public Customer(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Customer");
        this.cart = new ShoppingCart();
    }
    
    // Empty constructor for loading from database
    private Customer() {
        super();
        this.role = "Customer";
        this.cart = new ShoppingCart();
    }
    
    // Factory method to load customer from database
    public static Customer loadFromDatabase(String userId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Load basic user data
            String userSql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setString(1, userId);
            ResultSet userRs = userStmt.executeQuery();
            
            if (userRs.next()) {
                Customer customer = new Customer();
                customer.userId = userRs.getString("user_id");
                customer.name = userRs.getString("name");
                customer.email = userRs.getString("email");
                customer.password = userRs.getString("password");
                customer.isActive = userRs.getBoolean("is_active");
                
                // Load customer-specific data if needed
                // Example: String customerSql = "SELECT * FROM customers WHERE user_id = ?";
                
                return customer;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Database operations for customer-specific data
    @Override
    public boolean save() {
        // First save the base user data
        if (!super.save()) {
            return false;
        }
        
        // Then save customer-specific data
        // This would insert/update the customers table if you have one
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Check if customer record exists
            String checkSql = "SELECT COUNT(*) FROM customers WHERE user_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count > 0) {
                // Update customer data
                String updateSql = "UPDATE customers SET other_field = ? WHERE user_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                // Set customer-specific fields
                updateStmt.setString(1, "some value");
                updateStmt.setString(2, userId);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert new customer data
                String insertSql = "INSERT INTO customers (user_id, other_field) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, userId);
                insertStmt.setString(2, "some value");
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Shopping cart operations
    public ShoppingCart getCart() {
        return cart;
    }
    
    public void addToCart(String bookId, int quantity) {
    if (quantity <= 0) {
        return; // Invalid quantity
    }
    
    Book book = Book.loadFromDatabase(bookId);
    if (book != null) {
        // Check if we already have this book in cart
        for (CartItem item : cart.getItems()) {
            if (item.getBook().getBookId().equals(bookId)) {
                // Update quantity of existing item
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        
        // If not found, add new item
        cart.addItem(book, quantity);
    }
  }
    
    public void removeFromCart(String bookId) {
        cart.removeItem(bookId);
    }
    
    public Order checkout(String paymentMethod) {
        if (cart.isEmpty()) {
            System.out.println("Cannot checkout: cart is empty");
            return null;
        }
        
        // Create a new order
        Order order = new Order(this.userId, cart.calculateTotal(), "Pending");
        System.out.println("Created new order: " + order.getOrderId() + " for $" + cart.calculateTotal());
        if (!order.save()) {
        System.out.println("Failed to save order");
        return null;
        }
        // Add cart items to order
        
        for (CartItem item : cart.getItems()) {
        Book book = item.getBook();
        int quantity = item.getQuantity();
        System.out.println("Adding to order: " + book.getTitle() + ", qty: " + quantity);
        
        // Check stock availability
        if (book.getStock() < quantity) {
            System.out.println("Insufficient stock for: " + book.getTitle());
            return null;
        }
        
        order.addItem(book.getBookId(), quantity);
       }
        
        // Save the order to database
        if (order.save()) {
            System.out.println("Order saved successfully");
            // Create payment record
            Payment payment = new Payment(order.getOrderId(), this.userId, 
                                         paymentMethod, cart.calculateTotal());
            boolean paymentSaved = payment.save();
            System.out.println("Payment saved: " + paymentSaved);
            
            // Update book stock
             for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            boolean stockUpdated = book.reduceStock(item.getQuantity());
            System.out.println("Stock updated for " + book.getTitle() + ": " + stockUpdated);
           }
            
            // Clear the cart
            cart.clear();
            return order;
        }
        else {
        System.out.println("Failed to save order");
        return null;
    }
    }
    
    // Order operations
    public List<Order> getOrderHistory() {
        List<Order> orders = new ArrayList<>();
        Connection conn = DatabaseConnector.getInstance().getConnection();
        
        try {
            String sql = "SELECT order_id FROM orders WHERE user_id = ? ORDER BY order_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String orderId = rs.getString("order_id");
                Order order = Order.loadFromDatabase(orderId);
                if (order != null) {
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    // Review operations
    public void addReview(String bookId, int rating, String comment) {
        Review review = new Review(this.userId, bookId, rating, comment);
        review.save();
    }
    
    public List<Review> getReviews() {
        List<Review> reviews = new ArrayList<>();
        Connection conn = DatabaseConnector.getInstance().getConnection();
        
        try {
            String sql = "SELECT review_id FROM reviews WHERE user_id = ? ORDER BY date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
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
    
    // Statistics generation
    public Map<String, Object> generateStatistics() {
        Map<String, Object> stats = new HashMap<>();
        Connection conn = DatabaseConnector.getInstance().getConnection();
        
        try {
            // Total spent
            String spentSql = "SELECT SUM(total_amount) FROM orders WHERE user_id = ? AND status = 'Completed'";
            PreparedStatement spentStmt = conn.prepareStatement(spentSql);
            spentStmt.setString(1, userId);
            ResultSet spentRs = spentStmt.executeQuery();
            
            if (spentRs.next()) {
                stats.put("totalSpent", spentRs.getDouble(1));
            }
            
            // Total orders
            String ordersSql = "SELECT COUNT(*) FROM orders WHERE user_id = ?";
            PreparedStatement ordersStmt = conn.prepareStatement(ordersSql);
            ordersStmt.setString(1, userId);
            ResultSet ordersRs = ordersStmt.executeQuery();
            
            if (ordersRs.next()) {
                stats.put("totalOrders", ordersRs.getInt(1));
            }
            
            // Favorite genre (most purchased)
            String genreSql = 
                "SELECT b.genre, COUNT(*) as count " +
                "FROM order_items oi " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "JOIN books b ON oi.book_id = b.book_id " +
                "WHERE o.user_id = ? " +
                "GROUP BY b.genre " +
                "ORDER BY count DESC " +
                "LIMIT 1";
                
            PreparedStatement genreStmt = conn.prepareStatement(genreSql);
            genreStmt.setString(1, userId);
            ResultSet genreRs = genreStmt.executeQuery();
            
            if (genreRs.next()) {
                stats.put("favoriteGenre", genreRs.getString("genre"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    @Override
    public String getDetails() {
        Map<String, Object> stats = generateStatistics();
        
        return "Customer: " + getName() + 
               "\nEmail: " + getEmail() + 
               "\nTotal Orders: " + stats.getOrDefault("totalOrders", 0) + 
               "\nTotal Spent: $" + String.format("%.2f", stats.getOrDefault("totalSpent", 0.0)) +
               "\nFavorite Genre: " + stats.getOrDefault("favoriteGenre", "None");
    }
}
