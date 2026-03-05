/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;

/**
 *
 * @author maiso
 */
import java.util.ArrayList;
import java.util.List;

/**
 * ShoppingCart class - represents a customer's shopping cart
 * This is a transient class (not stored in database)
 */
public class ShoppingCart {
    private List<CartItem> items;
    
    public ShoppingCart() {
        this.items = new ArrayList<>();
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public void addItem(Book book, int quantity) {
        // Check if item already exists in cart
        for (CartItem item : items) {
            if (item.getBook().getBookId().equals(book.getBookId())) {
                // Update quantity
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        
        // Add new item
        items.add(new CartItem(book, quantity));
    }
    
    public void removeItem(String bookId) {
        items.removeIf(item -> item.getBook().getBookId().equals(bookId));
    }
    
    public void updateQuantity(String bookId, int quantity) {
        for (CartItem item : items) {
            if (item.getBook().getBookId().equals(bookId)) {
                if (quantity <= 0) {
                    removeItem(bookId);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }
    
    public double calculateTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
    
    public void clear() {
        items.clear();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int getTotalItems() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }
}
