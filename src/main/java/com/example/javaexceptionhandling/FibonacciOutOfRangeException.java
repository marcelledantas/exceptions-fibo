package com.example.javaexceptionhandling;

public class FibonacciOutOfRangeException extends Exception {
    public FibonacciOutOfRangeException(String message) {
        super(message);
    }
}
