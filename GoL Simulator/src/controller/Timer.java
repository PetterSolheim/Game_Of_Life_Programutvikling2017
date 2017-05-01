package controller;

import javafx.animation.AnimationTimer;

/**
 * This class is responsible for triggering the call <code>createNextGeneration</code> that iterates the game to the next generation.
 * @see controller.MainWindowController#createNextGeneration() 
 */
public class Timer extends AnimationTimer {

    private MainWindowController controller;
    /**
     * Determines when the <code>createNextGeneration</Code> is called
     */
    private long nextGeneration;
    /**
     * Determines how often <code>createNextGeneration</code> is called. 
     */
    private long timeBetweenGeneration;
    /**
     * 
     * @param controller 
     */
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
