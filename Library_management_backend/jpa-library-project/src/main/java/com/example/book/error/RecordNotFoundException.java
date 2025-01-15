package com.example.book.error;

// Custom exception class for handling record not found errors
public class RecordNotFoundException extends RuntimeException {

    // Default constructor
    public RecordNotFoundException() {
        super();
    }

    // Constructor with custom message
    public RecordNotFoundException(String message) {
        super(message);
    }
}
