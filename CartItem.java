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
 * CartItem class - represents an item in a shopping cart
 * This is a transient class (not stored in database)
 */
public class CartItem {
    private Book book;
    private int quantity;
    
    public CartItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }
    
    // Getters and setters
    public Book getBook() { return book; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getSubtotal() {
        return book.getPrice() * quantity;
    }
}