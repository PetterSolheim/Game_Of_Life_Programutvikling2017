/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *  Class to encapsulate floatboards.
 * @author Even
 */
public class FloatBoard {
    
    private int generation;
    private float floatBoard;
    
    public FloatBoard(int generation, float floatBoard){
        this.generation = generation;
        this.floatBoard = floatBoard;
    }
    
    public int getGeneration (){
        return this.generation;
    }
    public float getFloatBoard (){
        return this.floatBoard;
    }
}
