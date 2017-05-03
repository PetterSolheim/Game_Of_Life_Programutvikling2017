package model;

/**
 * Used by FileImporter.java to throw exceptions when encountering patterns
 * that do not match its expectations.
 */
public class PatternFormatException extends Exception {
    public PatternFormatException(){}
    
    public PatternFormatException(String message) {
        super(message);
    }
}
