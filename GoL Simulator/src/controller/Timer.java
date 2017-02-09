/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javafx.animation.AnimationTimer;

/**
 * This class determines when draw in ResizableCanvas and nextGeneration in Board is called from MainWindowController
 * @author peven
 */
public class Timer extends AnimationTimer{
    
    private MainWindowController controller;
    private long nextGeneration;
    private long timeBetweenGeneration;

    
    @Override
    public void handle(long currentTime) {
        timeBetweenGeneration = 100l;
        System.out.println("Before if: " + currentTime + " " + nextGeneration);
        if (nextGeneration < currentTime) {
            System.out.println("After if:  " + currentTime + " " + nextGeneration);
            nextGeneration = currentTime + timeBetweenGeneration;
            //nextGeneration = 0;
            controller.initiateNextGeneration();
            System.out.println();
        }
    }
    public void setNextGenerationTimer (long time){
        this.nextGeneration = time;
    }
    public Timer (MainWindowController controller){
        this.controller = controller;
        this.timeBetweenGeneration = 2;
    }
}
