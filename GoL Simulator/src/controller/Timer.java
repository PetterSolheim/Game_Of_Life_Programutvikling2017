package controller;

import javafx.animation.AnimationTimer;

/**
 * This class determines when draw in ResizableCanvas and nextGeneration in Board is called from MainWindowController
 * 
 */
public class Timer extends AnimationTimer{
    
    private MainWindowController controller;
    private long nextGeneration;
    private long timeBetweenGeneration;

    
    @Override
    public void handle(long currentTime) {
        if (nextGeneration < currentTime ) {
            nextGeneration = currentTime + timeBetweenGeneration;
            controller.createNextGeneration();
        }
    }
    public void setFps (long timeInNanoSeconds){
        this.timeBetweenGeneration = timeInNanoSeconds;
    }
    public Timer (MainWindowController controller){
        this.controller = controller;
    }
}
