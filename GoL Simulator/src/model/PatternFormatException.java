/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author aleks
 */
public class PatternFormatException extends Exception {
    public PatternFormatException(){}
    
    public PatternFormatException(String message) {
        super(message);
    }
}
