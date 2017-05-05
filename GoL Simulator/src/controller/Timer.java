package controller;

import javafx.animation.AnimationTimer;

/**
 * This class is responsible for triggering the call to <code>createNextGeneration</code> that iterates the game to the next generation.
 * @see controller.MainWindowController#createNextGeneration() 
 * @see model.BoardDynamic#nextGeneration()
 */
public class Timer extends AnimationTimer {

    private MainWindowController controller;
    /**
     * Determines when the createNextGeneration is called
     */
    private long nextGeneration = 0;
    /**
     * Determines how often createNextGeneration is called. 
     */
    private long timeBetweenGeneration;
    /**
     * Timer constructor. Takes a reference to the main controller so it can call
     * <code>createNextGeneration</code>
     * @param controller <code>MainWindowController</code>
     */
    public Timer(MainWindowController controller) {
        this.controller = controller;
    }

    /**
     * This method is called every frame and checks whether or not it is time to call <code>createNextGeneration</code>
     * @param currentTime 
     */
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
    public void setFps(double desiredFramesPerSecond) {
        long timeBetweenGenerations = (long) (1000000000 / desiredFramesPerSecond);
        this.timeBetweenGeneration = timeBetweenGenerations;
    }
    /*
     * Used for testing
    */
    public long getTimeBetweenGenerations (){
        return this.timeBetweenGeneration;
    }
    /*
     * Used for testing
     */
    public long getNextGeneration (){
        return this.nextGeneration;
    }
}
