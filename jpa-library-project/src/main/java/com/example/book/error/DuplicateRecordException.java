package com.example.book.error;

// Custom exception class for handling duplicate records
public class DuplicateRecordException extends RuntimeException {

    // Default constructor
    public DuplicateRecordException() {
        super();
    }

    // Constructor with custom message
    public DuplicateRecordException(String message) {
        super(message);
    }
}
