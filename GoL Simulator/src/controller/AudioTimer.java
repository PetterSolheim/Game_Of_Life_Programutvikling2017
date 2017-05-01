/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javafx.animation.AnimationTimer;
import model.AudioManager;

/**
 *
 * @author Even
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
    public AudioTimer (AudioSettingsWindowController controller){
        this.controller = controller;
    }

    public void setFps(long timeInNanoSeconds) {
        this.timeBetweenGeneration = timeInNanoSeconds;
    }
    public void setTimeBetweenCellAudioTimer (int cells){
        this.timeBetweenCellAudio = timeBetweenGeneration / cells;
    }
    public long getTimeBetweenCellAudioTimer (){
        return this.timeBetweenCellAudio;
    }
}