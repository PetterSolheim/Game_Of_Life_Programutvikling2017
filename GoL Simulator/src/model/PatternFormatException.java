package model;

/**
 * Used by FileImporter.java to throw exceptions when encountering patterns
 * in files such as RLE that do not match the FileImporters expectations.
 */
public class PatternFormatException extends Exception {
    public PatternFormatException(){}
    
    public PatternFormatException(String message) {
        super(message);
    }
}
