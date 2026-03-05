/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_gui_draft2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Report class - represents a system report
 * Corresponds to the Reports table in the database
 */
public class Report {
    private String reportId;
    private String reportType;
    private String generatedBy;
    private Date date;
    private String title;
    private Map<String, Object> dataContent;
    
    // Constructor for creating a new report
    public Report(String reportType, String generatedBy, String title) {
        this.reportId = "REP" + System.currentTimeMillis();
        this.reportType = reportType;
        this.generatedBy = generatedBy;
        this.date = new Date();
        this.title = title;
        this.dataContent = new HashMap<>();
    }
    
    // Constructor for loading from database
    private Report(String reportId, String reportType, String generatedBy, Date date, String title) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.generatedBy = generatedBy;
        this.date = date;
        this.title = title;
        this.dataContent = new HashMap<>();
    }
    
    // Getters
    public String getReportId() { return reportId; }
    public String getReportType() { return reportType; }
    public String getGeneratedBy() { return generatedBy; }
    public Date getDate() { return date; }
    public String getTitle() { return title; }
    public Map<String, Object> getDataContent() { return dataContent; }
    
    // Add data to the report
    public void addData(String key, Object value) {
        dataContent.put(key, value);
    }
    
    // Convert data content to JSON string for storage
    private String dataToJson() {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : dataContent.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":");
            
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else if (entry.getValue() instanceof Map) {
                // Handle nested maps
                json.append("{");
                Map<?, ?> nestedMap = (Map<?, ?>) entry.getValue();
                boolean firstNested = true;
                
                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    if (!firstNested) {
                        json.append(",");
                    }
                    firstNested = false;
                    
                    json.append("\"").append(nestedEntry.getKey()).append("\":");
                    
                    if (nestedEntry.getValue() instanceof String) {
                        json.append("\"").append(nestedEntry.getValue()).append("\"");
                    } else {
                        json.append(nestedEntry.getValue());
                    }
                }
                json.append("}");
            } else {
                json.append(entry.getValue());
            }
        }
        json.append("}");
        
        return json.toString();
    }
    
    // Database operations
    public boolean save() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            // Check if report exists
            String checkSql = "SELECT COUNT(*) FROM reports WHERE report_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, reportId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count > 0) {
                // Update existing report
                String updateSql = "UPDATE reports SET title = ?, data_content = ? WHERE report_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, title);
                updateStmt.setString(2, dataToJson());
                updateStmt.setString(3, reportId);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert new report
                String insertSql = "INSERT INTO reports (report_id, report_type, generated_by, date, title, data_content) " +
                                  "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, reportId);
                insertStmt.setString(2, reportType);
                insertStmt.setString(3, generatedBy);
                insertStmt.setTimestamp(4, new Timestamp(date.getTime()));
                insertStmt.setString(5, title);
                insertStmt.setString(6, dataToJson());
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Static methods for database operations
    public static Report loadFromDatabase(String reportId) {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT * FROM reports WHERE report_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, reportId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Report report = new Report(
                    rs.getString("report_id"),
                    rs.getString("report_type"),
                    rs.getString("generated_by"),
                    rs.getTimestamp("date"),
                    rs.getString("title")
                );
                
                // Parse JSON data content
                // This is a simplified approach - in a real app, use a JSON library like Gson or Jackson
                // report.parseDataFromJson(rs.getString("data_content"));
                
                return report;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get generator's name
    public String getGeneratorName() {
        Connection conn = DatabaseConnector.getInstance().getConnection();
        try {
            String sql = "SELECT name FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, generatedBy);
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
    
    // Generate a formatted report text
    public String generateFormattedReport() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder report = new StringBuilder();
        
        report.append("========== ").append(title).append(" ==========\n");
        report.append("Report Type: ").append(reportType).append("\n");
        report.append("Generated By: ").append(getGeneratorName()).append("\n");
        report.append("Date: ").append(sdf.format(date)).append("\n");
        report.append("=======================================\n\n");
        
        // Add report data
        for (Map.Entry<String, Object> entry : dataContent.entrySet()) {
            report.append(formatKey(entry.getKey())).append(": ");
            
            if (entry.getValue() instanceof Map) {
                report.append("\n");
                Map<?, ?> nestedMap = (Map<?, ?>) entry.getValue();
                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    report.append("  - ").append(nestedEntry.getKey()).append(": ")
                          .append(nestedEntry.getValue()).append("\n");
                }
            } else {
                report.append(entry.getValue()).append("\n");
            }
        }
        
        return report.toString();
    }
    
    // Format key for display (camelCase to Title Case)
    private String formatKey(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        result.append(Character.toUpperCase(key.charAt(0)));
        
        for (int i = 1; i < key.length(); i++) {
            char c = key.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append(' ').append(c);
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
}
