
package controller;

import javafx.animation.AnimationTimer;
import model.AudioManager;

/**
 *
 */
public class AudioTimer extends AnimationTimer {
    
    private AudioSettingsWindowController controller;
    private long nextGeneration;
    private long timeBetweenGeneration;
    private long cellAudioTimer;
    private long timeBetweenCellAudio;

    @Override
    public void handle(long currentTime) {
        if (nextGeneration < currentTime) {
            nextGeneration = currentTime + timeBetweenGeneration;
            controller.createNextGeneration();
        }
        if(cellAudioTimer < currentTime){
            cellAudioTimer = currentTime + timeBetweenCellAudio;
        }
    } 

    /**
     *
     * @param controller
     */
    public AudioTimer (AudioSettingsWindowController controller){
        this.controller = controller;
    }

    /**
     *
     * @param timeInNanoSeconds
     */
    public void setFps(long timeInNanoSeconds) {
        this.timeBetweenGeneration = timeInNanoSeconds;
    }

    /**
     *
     * @param cells
     */
    public void setTimeBetweenCellAudioTimer (int cells){
        this.timeBetweenCellAudio = timeBetweenGeneration / cells;
    }

    /**
     *
     * @return
     */
    public long getTimeBetweenCellAudioTimer (){
        return this.timeBetweenCellAudio;
    }
}
