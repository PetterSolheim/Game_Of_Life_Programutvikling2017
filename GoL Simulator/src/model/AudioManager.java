package model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    static AudioManager instance;

    static Synthesizer synt;
    static Sequencer sequencer;
    private Clip activeSong; // High level interface that uses L
    private boolean generationSound;
    private boolean cellSound;

    public void setGenerationSound(boolean generationSound) {
        this.generationSound = generationSound;
    }

    public void setCellSound(boolean cellSound) {
        this.cellSound = cellSound;
    }

    private AudioManager() {
        instance = this;
        try {
            activeSong = AudioSystem.getClip();
            synt = MidiSystem.getSynthesizer();
            sequencer = MidiSystem.getSequencer();
        } catch (MidiUnavailableException exception) {
            DialogBoxes.genericErrorMessage("No midi devices available", exception.getMessage());
        } catch (LineUnavailableException ex) {
            DialogBoxes.genericErrorMessage("No lines available for playback", ex.getMessage());
        }
    }

    public static AudioManager getSingelton() {
        if (instance != null) {
            return instance;
        }
        return new AudioManager();
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

    public void loadAudiofile(File f) throws UnsupportedAudioFileException {
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(f);
            setActiveSong(inputStream);
        } catch (LineUnavailableException ex) {
            DialogBoxes.genericErrorMessage("No lines available", ex.getMessage());
        } catch (IOException ex) {
            DialogBoxes.genericErrorMessage("Failed to load audio file", ex.getMessage());
        } catch (IllegalStateException ex) {
            DialogBoxes.genericErrorMessage("This audio file is already loaded", "Try a different audio file.");
        }
        playPause();
    }

    public void resetSong() {
        activeSong.setFramePosition(0);
    }

    private void setActiveSong(AudioInputStream song) throws LineUnavailableException, IOException {
        activeSong.open(song);
    }

    public Clip getActiveSong() {
        return activeSong;
    }

    public void playPause() {
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
        synt.close();
    }

}
