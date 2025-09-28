package com.blogspot.blog.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedAccessException() {
        super("You are not authorized to perform this operation");
    }
}
