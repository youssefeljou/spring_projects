package com.example.book.error;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

// Response structure for errors
@Setter
@Getter
public class ErrorResponse {
    
    private Boolean success; // Indicates whether the operation was successful or not
    private String message; // Error message
    private LocalDateTime dateTime; // Timestamp when the error occurred
    private List<String> details; // Detailed list of error messages
    
    // Default constructor
    public ErrorResponse() {
        super();
    }

    // Constructor with message and details
    public ErrorResponse(String message, List<String> details) {
        super();
        this.message = message;
        this.details = details;
        this.success = Boolean.FALSE; // By default, set success to false
        this.dateTime = LocalDateTime.now(); // Current timestamp
    }

   
}
