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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * OrderController - manages interactions between Order models and the GUI
 */
public class OrderController {
    
    /**
     * Creates a new order from the shopping cart
     * @param customer Customer placing the order
     * @param paymentMethod Method of payment
     * @return Order object if creation succeeds, null otherwise
     */
    public static Order createOrder(Customer customer, String paymentMethod) {
    try {
        System.out.println("Creating order for customer: " + customer.getName());
        System.out.println("Items in cart: " + customer.getCart().getItems().size());
        
        // Check if cart is empty
        if (customer.getCart().isEmpty()) {
            System.out.println("Cart is empty, cannot create order");
            return null;
        }
        
        // Call the customer checkout method
        return customer.checkout(paymentMethod);
    } catch (Exception e) {
        System.err.println("Error in createOrder: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
   }
    
    /**
     * Gets the order history for a customer
     * @param userId ID of the customer
     * @return List of orders
     */
    public static List<Order> getOrderHistory(String userId) {
        Customer customer = new Customer(userId, "", "", "");
        return customer.getOrderHistory();
    }
    
    /**
     * Updates an order's status
     * @param orderId ID of the order to update
     * @param status New status
     * @return true if update succeeds, false otherwise
     */
    public static boolean updateOrderStatus(String orderId, String status) {
        Order order = Order.loadFromDatabase(orderId);
        if (order == null) {
            return false;
        }
        
        order.setStatus(status);
        return true;
    }
    
    /**
     * Gets detailed information about an order
     * @param orderId ID of the order to retrieve
     * @return Map containing order details if found, null otherwise
     */
    public static Map<String, Object> getOrderDetails(String orderId) {
        Order order = Order.loadFromDatabase(orderId);
        if (order == null) {
            return null;
        }
        
        Map<String, Object> details = new HashMap<>();
        details.put("orderId", order.getOrderId());
        details.put("date", order.getOrderDate());
        details.put("status", order.getStatus());
        details.put("totalAmount", order.getTotalAmount());
        
        List<Map<String, Object>> items = new ArrayList<>();
        List<OrderItem> orderItems = order.getItems();
        for (OrderItem item : order.getItems()) {
            Map<String, Object> itemMap = new HashMap<>();
            Book book = item.getBook();
            itemMap.put("title", item.getBook().getTitle());
            itemMap.put("author", item.getBook().getAuthor());
            itemMap.put("price", item.getBook().getPrice());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("subtotal", item.getSubtotal());
            items.add(itemMap);
        }
        details.put("items", items);
        
        Payment payment = order.getPayment();
        if (payment != null) {
            details.put("paymentMethod", payment.getPaymentMethod());
            details.put("paymentDate", payment.getDate());
        }
        
        return details;
    }
    
    /**
     * Populates a JTable with order history for a customer
     * @param table JTable to populate
     * @param userId ID of the customer
     */
    public static void populateOrderHistoryTable(JTable table, String userId) {
        List<Order> orders = getOrderHistory(userId);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear table
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Order order : orders) {
            model.addRow(new Object[] {
                order.getOrderId(),
                sdf.format(order.getOrderDate()),
                String.format("$%.2f", order.getTotalAmount()),
                order.getStatus(),
                "View Details"
            });
        }
    }
    
    /**
     * Populates a JTable with sales data for admin view
     * @param table JTable to populate
     * @param startDate Start date for sales period
     * @param endDate End date for sales period
     */
    public static void populateSalesTable(JTable table, Date startDate, Date endDate) {
    Connection conn = DatabaseConnector.getInstance().getConnection();
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.setRowCount(0); // Clear table
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    try {
        String sql = 
            "SELECT o.order_date, COUNT(oi.order_item_id) as books_sold, " +
            "SUM(o.total_amount) as revenue, " +
            "(SELECT b2.title FROM books b2 " +
            "JOIN order_items oi2 ON b2.book_id = oi2.book_id " +
            "JOIN orders o2 ON oi2.order_id = o2.order_id " +
            "WHERE o2.order_date BETWEEN ? AND ? " +
            "GROUP BY b2.title ORDER BY COUNT(oi2.order_item_id) DESC LIMIT 1) as top_book " +
            "FROM orders o " +
            "JOIN order_items oi ON o.order_id = oi.order_id " +
            "WHERE o.order_date BETWEEN ? AND ? " + 
            // Remove "AND o.status = 'Completed'" to be consistent with other methods
            "GROUP BY o.order_date " +
            "ORDER BY o.order_date DESC";
            
        PreparedStatement stmt = conn.prepareStatement(sql);
        java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
        java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
        
        stmt.setDate(1, sqlStartDate);
        stmt.setDate(2, sqlEndDate);
        stmt.setDate(3, sqlStartDate);
        stmt.setDate(4, sqlEndDate);
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            model.addRow(new Object[] {
                sdf.format(rs.getDate("order_date")),
                rs.getInt("books_sold"),
                String.format("$%.2f", rs.getDouble("revenue")),
                rs.getString("top_book")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
   }
    
    /**
     * Gets the total revenue for a time period
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue
     */
    public static double getTotalRevenue(Date startDate, Date endDate) {
    Connection conn = DatabaseConnector.getInstance().getConnection();
    try {
        // Remove the "status = 'Completed'" filter to be consistent with order count
        String sql = "SELECT SUM(total_amount) FROM orders WHERE order_date BETWEEN ? AND ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, new java.sql.Date(startDate.getTime()));
        stmt.setDate(2, new java.sql.Date(endDate.getTime()));
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0.0;
   }
    
    /**
     * Gets total orders for a time period
     * @param startDate Start date
     * @param endDate End date
     * @return Total number of orders
     */
    public static int getTotalOrders(Date startDate, Date endDate) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT COUNT(*) FROM orders WHERE order_date BETWEEN ? AND ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
