/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;

/**
 *
 * @author maiso
 */
/**
 * OrderItem class - represents an item in an order
 * Corresponds to the OrderItems table in the database
 */
public class OrderItem {
    private String orderItemId;
    private String orderId;
    private Book book;
    private int quantity;
    private double subtotal;
    
    public OrderItem(String orderItemId, String orderId, Book book, int quantity, double subtotal) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.book = book;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
    
    // Getters
    public String getOrderItemId() { return orderItemId; }
    public String getOrderId() { return orderId; }
    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return subtotal; }
}
