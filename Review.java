package project_gui_draft2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Review class - represents a customer review of a book
 * Corresponds to the Reviews table in the database
 */
public class Review {
    private String reviewId;
    private String userId;
    private String bookId;
    private int rating;
    private String comment;
    private Date date;
    
    // Constructor for creating a new review
    public Review(String userId, String bookId, int rating, String comment) {
        this.reviewId = "REV" + System.currentTimeMillis();
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.comment = comment;
        this.date = new Date();
    }
    
    // Constructor for loading from database
    private Review(String reviewId, String userId, String bookId, int rating, String comment, Date date) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }
    
    // Getters and Setters
    public String getReviewId() { return reviewId; }
    public String getUserId() { return userId; }
    public String getBookId() { return bookId; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { 
        this.rating = rating;
        save();
    }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { 
        this.comment = comment;
        save();
    }
    
    public Date getDate() { return date; }
    
    // Database operations
    public boolean save() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Check if review exists
            String checkSql = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, reviewId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count > 0) {
                // Update existing review
                String updateSql = "UPDATE reviews SET rating = ?, comment = ? WHERE review_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, rating);
                updateStmt.setString(2, comment);
                updateStmt.setString(3, reviewId);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert new review
                String insertSql = "INSERT INTO reviews (review_id, user_id, book_id, rating, comment, date) " +
                                   "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, reviewId);
                insertStmt.setString(2, userId);
                insertStmt.setString(3, bookId);
                insertStmt.setInt(4, rating);
                insertStmt.setString(5, comment);
                insertStmt.setTimestamp(6, new Timestamp(date.getTime()));
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "DELETE FROM reviews WHERE review_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, reviewId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Static methods for database operations
    public static Review loadFromDatabase(String reviewId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT * FROM reviews WHERE review_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, reviewId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Review(
                    rs.getString("review_id"),
                    rs.getString("user_id"),
                    rs.getString("book_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("date")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Return the username of the reviewer
    public String getReviewerName() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT name FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("name");
            }
            return "Unknown User";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown User";
        }
    }
    
    // Get book title
    public String getBookTitle() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT title FROM books WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("title");
            }
            return "Unknown Book";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown Book";
        }
    }
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return rating + "★ " + comment + " (" + sdf.format(date) + ")";
    }
}