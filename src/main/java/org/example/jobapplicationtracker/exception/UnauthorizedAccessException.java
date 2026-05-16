package org.example.jobapplicationtracker.exception;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException() {
        super("You are not authorized to access this resource");
    }
}
