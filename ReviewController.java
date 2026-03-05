/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * ReviewController - manages interactions between Review models and the GUI
 */
public class ReviewController {
    
    /**
     * Adds a new review for a book
     * @param userId ID of the customer writing the review
     * @param bookId ID of the book being reviewed
     * @param rating Rating (1-5)
     * @param comment Review comment
     * @return true if addition succeeds, false otherwise
     */
    public static boolean addReview(String userId, String bookId, int rating, String comment) {
        // Validate input
        if (rating < 1 || rating > 5 || comment.isEmpty()) {
            return false;
        }
        
        // Check if user has purchased the book
        if (!hasUserPurchasedBook(userId, bookId)) {
            return false; // User can only review books they've purchased
        }
        
        Customer customer = new Customer(userId, "", "", "");
        customer.addReview(bookId, rating, comment);
        return true;
    }
    
    /**
     * Checks if a user has purchased a specific book
     * @param userId ID of the user
     * @param bookId ID of the book
     * @return true if user has purchased the book, false otherwise
     */
    private static boolean hasUserPurchasedBook(String userId, String bookId) {
        List<Order> orders = OrderController.getOrderHistory(userId);
        
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                if (item.getBook().getBookId().equals(bookId)) {
                    return true;
                }
            }
        }
        
        return false; // Book not found in user's order history
    }
    
    /**
     * Updates an existing review
     * @param reviewId ID of the review to update
     * @param rating New rating
     * @param comment New comment
     * @return true if update succeeds, false otherwise
     */
    public static boolean updateReview(String reviewId, int rating, String comment) {
        if (rating < 1 || rating > 5 || comment.isEmpty()) {
            return false;
        }
        
        Review review = Review.loadFromDatabase(reviewId);
        if (review == null) {
            return false;
        }
        
        review.setRating(rating);
        review.setComment(comment);
        return true;
    }
    
    /**
     * Deletes a review
     * @param reviewId ID of the review to delete
     * @return true if deletion succeeds, false otherwise
     */
    public static boolean deleteReview(String reviewId) {
        Review review = Review.loadFromDatabase(reviewId);
        if (review == null) {
            return false;
        }
        
        return review.delete();
    }
    
    /**
     * Gets all reviews for a book
     * @param bookId ID of the book
     * @return List of reviews
     */
    public static List<Review> getBookReviews(String bookId) {
        Book book = Book.loadFromDatabase(bookId);
        if (book == null) {
            return java.util.Collections.emptyList();
        }
        
        return book.getReviews();
    }
    
    /**
     * Gets all reviews by a customer
     * @param userId ID of the customer
     * @return List of reviews
     */
    public static List<Review> getCustomerReviews(String userId) {
        Customer customer = new Customer(userId, "", "", "");
        return customer.getReviews();
    }
    
    /**
     * Gets the average rating for a book
     * @param bookId ID of the book
     * @return Average rating
     */
    public static double getAverageRating(String bookId) {
        Book book = Book.loadFromDatabase(bookId);
        if (book == null) {
            return 0.0;
        }
        
        return book.getAverageRating();
    }
    
    /**
     * Populates a JTable with reviews for a book
     * @param table JTable to populate
     * @param bookId ID of the book
     */
    public static void populateBookReviewsTable(JTable table, String bookId) {
        List<Review> reviews = getBookReviews(bookId);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear table
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Review review : reviews) {
            model.addRow(new Object[] {
                review.getReviewerName(),
                String.format("%d★", review.getRating()),
                review.getComment(),
                sdf.format(review.getDate())
            });
        }
    }
    
    /**
     * Populates a JTable with reviews by a customer
     * @param table JTable to populate
     * @param userId ID of the customer
     */
    public static void populateCustomerReviewsTable(JTable table, String userId) {
        List<Review> reviews = getCustomerReviews(userId);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear table
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Review review : reviews) {
            model.addRow(new Object[] {
                review.getBookTitle(),
                String.format("%d★", review.getRating()),
                review.getComment(),
                sdf.format(review.getDate()),
                "Edit",
                review.getReviewId() // Hidden column for ID
            });
        }
    }
}