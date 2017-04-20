package controller;

import javafx.animation.AnimationTimer;

/**
 * Class for running the game of life animation.
 */
public class Timer extends AnimationTimer {

    private MainWindowController controller;
    private long nextGeneration;
    private long timeBetweenGeneration;
    
    public Timer(MainWindowController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(long currentTime) {
        if (nextGeneration < currentTime) {
            nextGeneration = currentTime + timeBetweenGeneration;
            controller.createNextGeneration();
        }
    }

    /**
     * Sets the desired FPS for the animation.
     *
     * @param timeInNanoSeconds
     */
    public void setFps(long timeInNanoSeconds) {
        this.timeBetweenGeneration = timeInNanoSeconds;
    }

}
