package com.example.book.error;

// Custom exception class for file storage errors
public class FileStorageException extends RuntimeException {

    // Default constructor
    public FileStorageException() {
        super();
    }

    // Constructor with custom message and cause
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with custom message
    public FileStorageException(String message) {
        super(message);
    }
}
