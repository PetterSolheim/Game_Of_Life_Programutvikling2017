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
        if (nextGeneration < currentTime ) {
            nextGeneration = currentTime + 100000000; // 9 
            controller.initiateNextGeneration();
            System.out.println();
            //stop ();
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
