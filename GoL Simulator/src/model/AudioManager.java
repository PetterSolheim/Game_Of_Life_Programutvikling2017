package model;

import controller.AudioTimer;
import controller.Timer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    static AudioManager instance;

    static Synthesizer synthesizer;
    static MidiChannel midiChannels[];
    static Instrument instruments[];
    static Sequencer sequencer;
    private Clip activeSong;
    private long audioDuration;
    public bool playCellAudio;

    public void setAudioDuration(long audioDuration) {
        this.audioDuration = audioDuration;
    }
    ExecutorService executorService = Executors.newCachedThreadPool();
    
    private AudioManager() {
        instance = this;

        try {
            activeSong = AudioSystem.getClip();
            synthesizer = MidiSystem.getSynthesizer();
            sequencer = MidiSystem.getSequencer();
            midiChannels = synthesizer.getChannels();
            instruments = synthesizer.getAvailableInstruments();
            synthesizer.open();
            synthesizer.loadInstrument(instruments[22]); // not working?
        } catch (MidiUnavailableException exception) {
            DialogBoxes.genericErrorMessage("No midi devices available", "Representing the board with sound is not supported on this device.\n" + exception.getMessage());
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

    public void cellAudio(BoardSound.CellState state) {
        switch (state) {
            case DEAD:
                for (MidiChannel m : midiChannels) {
                    if (m != null) { // channel is open
                        Runnable note = () -> { // plays a single note, c4.
                            try {
                                m.noteOn(60, 50);
                                Thread.sleep(audioDuration);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(AudioManager.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                m.noteOff(60);
                            }
                        };
                        executorService.submit(note);
                        return;
                    }
                }
                break;
            case ALIVE:
                for (MidiChannel m : midiChannels) {
                    if (m != null) { // channel is open
                        Runnable chord = () -> { // plays a c major chord
                            try {
                                m.noteOn(60, 200);
                                m.noteOn(72, 200);
                                m.noteOn(76, 200);
                                Thread.sleep(audioDuration);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(AudioManager.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                m.noteOff(60);
                                m.noteOff(72);
                                m.noteOff(76);
                            }
                        };
                        executorService.submit(chord);
                        return;
                    }
                }
                break;
        }
    }

    public void generationAudio() {
        System.out.println("Generation audo called");
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
            DialogBoxes.genericErrorMessage("This audio file is already loaded", "It is possible that the file is already loaded \n Try a different audio file.");
        }
        playPauseMusicPlayer();
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
        synthesizer.close();
    }

}
