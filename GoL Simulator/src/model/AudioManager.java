package model;

import controller.AudioSettingsWindowController;
import controller.AudioTimer;
import controller.Timer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import view.DialogBoxes;

/**
 * This class is responsible for handling everything related to audio.
 *
 */
public class AudioManager {

    private AudioSettingsWindowController controller;
    static AudioManager instance;
    private Clip activeSong;
    private ArrayList<String> absolutePathForLoadedSongs = new ArrayList<String>();
    private AudioInputStream inputStream;

    private AudioManager() {
        instance = this;
        try {
            activeSong = AudioSystem.getClip();
            activeSong.addLineListener(listener -> {
                //plays the next song in the queue if the current song is finished
                if (activeSong.getFramePosition() == activeSong.getFrameLength()) {
                    playNextSong();
                }
            });
        } catch (LineUnavailableException ex) {
            DialogBoxes.genericErrorMessage("No lines available for audio playback", ex.getMessage());
        }
    }
    public static boolean isCreated (){
        if(instance == null){
            return false;
        }
        return true;
    }
    public static AudioManager getSingelton() {
        if (instance != null) {
            return instance;
        }
        return new AudioManager();
    }

    private void playNextSong() {
        controller.playNextSong();
    }

    public void setController(AudioSettingsWindowController controller) {
        this.controller = controller;
    }

    public void volume(float volume) {
        if (activeSong.isOpen()) {
            FloatControl activeSongGain = (FloatControl) activeSong.getControl(FloatControl.Type.MASTER_GAIN);
            activeSongGain.setValue(convertGainToVolume(activeSongGain.getMinimum(), activeSongGain.getMaximum(), volume));
        }
    }

    private float convertGainToVolume(float minGain, float maxGain, float desiredVolume) {
        Float range = maxGain - minGain;
        float gainUnitperVolumeUnit = range / 100;
        float newGainValue = minGain + (gainUnitperVolumeUnit * desiredVolume);
        return newGainValue;
    }

    public void addAbsolutePath(File f) {
        absolutePathForLoadedSongs.add(f.getAbsolutePath());
    }
    public ArrayList<String> getAllLoadedSongs (){
        return absolutePathForLoadedSongs;
    }
    public void loadSongFromAbsolutePath(String songName) throws UnsupportedAudioFileException {
        for (int i = 0; i < absolutePathForLoadedSongs.size(); i++) {
            if (absolutePathForLoadedSongs.get(i).contains(songName)) {
                try {
                    File file = new File(absolutePathForLoadedSongs.get(i));
                    inputStream = AudioSystem.getAudioInputStream(file);
                    setActiveSong(inputStream);
                    playPauseMusicPlayer();
                } catch (IOException ex) {
                    DialogBoxes.genericErrorMessage("Failed to load audio file", "Try again\n" + ex.getMessage());
                }
            }
        }
    }

    public void resetSong() {
        activeSong.setFramePosition(0);
    }

    private void setActiveSong(AudioInputStream song) {
        try {
            if (activeSong.isOpen()) {
                activeSong.close();
            }
            activeSong.open(song);
        } catch (LineUnavailableException ex) {
            DialogBoxes.genericErrorMessage("No lines available", ex.getMessage());
        } catch (IOException ex) {
            DialogBoxes.genericErrorMessage("Failed to load audio file", "Try again\n" + ex.getMessage());
        }
    }
    public void generateAudioSequence (ArrayList<ArrayList> data){
        
    }
    public Clip getActiveSong() {
        return activeSong;
    }

    public void playPauseMusicPlayer() {
        if (activeSong.isActive()) {
            activeSong.stop();
        } else {
            activeSong.start();
            if (activeSong.getFramePosition() == activeSong.getFrameLength()) {
                resetSong();
                activeSong.start();
            }
        }
    }

    public void closeLines() {
        activeSong.close();
    }
}
