package model;

import controller.AudioSettingsWindowController;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import view.DialogBoxes;

/**
 * This class is responsible for handling everything related to music playback.
 * A reference of type Clip is created in the constructor to ensure that only
 * one clip is played at any given time. <br>A LineListener is also attached to this
 * clip, but it is important to note that it calls <code>playNextSong</code> in
 * AudioSettingsController once the clip finishes it's audio playback. <br>This is
 * done to ensure that the GUI always stay in sync with the audio.
 */
public class AudioManager {

    private AudioSettingsWindowController controller;
    static AudioManager instance;
    private Clip activeSong;
    private ArrayList<String> absolutePathForLoadedSongs = new ArrayList<String>();
    private AudioInputStream inputStream;

    /**
     * AudiomManager no-argument constructor. Because this is a singelton the
     * constructor stores a reference to this class.
     */
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

    /**
     * This method is used in AudioSettingsController to load in previously
     * loaded songs to the tracklist.
     *
     * @return <code>boolean</code> specifying if an instance is created.
     */
    public static boolean isCreated() {
        if (instance == null) {
            return false;
        }
        return true;
    }

    /**
     * Static method which ensures that only one AudioManager can ever exists
     * within the application. Returns a new AudioManager if no AudioManager
     * exists.
     *
     * @return <code>AudioManager</code>
     */
    public static AudioManager getSingelton() {
        if (instance != null) {
            return instance;
        }
        return new AudioManager();
    }

    /**
     * Calls the <code>playNextSong</code>
     *
     * @see controller.AudioSettingsController#.playNextSong()
     */
    private void playNextSong() {
        controller.playNextSong();
    }

    /**
     * Sets the AudioSettingsController so <code>playNextSong</code> can be
     * called.
     *
     * @param controller
     */
    public void setController(AudioSettingsWindowController controller) {
        this.controller = controller;
    }

    /**
     * Determines the new volume, by adjusting the gain, based on user input.
     *
     * @param volume <code>float</code> desired volume.
     */
    public void volume(float volume) {
        if (activeSong.isOpen()) {
            FloatControl activeSongGain = (FloatControl) activeSong.getControl(FloatControl.Type.MASTER_GAIN);
            activeSongGain.setValue(convertGainToVolume(activeSongGain.getMinimum(), activeSongGain.getMaximum(), volume));
        }
    }

    /**
     * Helper method used to convert the gain level of <code>Clip</code> to
     * match the desired volume.
     *
     * @param minGain <code>float</code> minimum possible gain of the Clip
     * object.
     * @param maxGain<code>float</code> maximum possible gain of the Clip
     * object.
     * @param desiredVolume <code>float</code> desired volume.
     * @return
     */
    private float convertGainToVolume(float minGain, float maxGain, float desiredVolume) {
        Float range = maxGain - minGain;
        float gainUnitperVolumeUnit = range / 100;
        float newGainValue = minGain + (gainUnitperVolumeUnit * desiredVolume);
        return newGainValue;
    }

    /**
     *
     * @param f
     */
    public void addAbsolutePath(File f) {
        absolutePathForLoadedSongs.add(f.getAbsolutePath());
    }

    /**
     *
     * @return <code>ArrayList&lt;ArrayList&lt;String&gt;&gt;</code> absolute
     * file path for previously loaded songs.
     */
    public ArrayList<String> getAllLoadedSongs() {
        return absolutePathForLoadedSongs;
    }

    /**
     *
     * @param songName <code>String</code> name of the song
     * @throws UnsupportedAudioFileException if the file is corrupted or not
     * supported
     */
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

    /**
     * sets the frame position on <code>activeSong</code> back to 0;
     */
    public void resetSong() {
        activeSong.setFramePosition(0);
    }

    /**
     * Loads the audio file to <code>activeSong</code> and opens the line if it
     * is the first time audio playback occurs.
     *
     * @param song <code>AudioInputStream</code> stream from the audio file.
     */
    private void setActiveSong(AudioInputStream song) {
        try {
            activeSong.open(song);
        } catch (LineUnavailableException ex) {
            DialogBoxes.genericErrorMessage("No lines available", ex.getMessage());
        } catch (IOException ex) {
            DialogBoxes.genericErrorMessage("Failed to load audio file", "Try again\n" + ex.getMessage());
        }
    }

    /**
     *
     * @param data
     */
    public void generateAudioSequence(ArrayList<ArrayList> data) {

    }

    /**
     * Get a reference to <code>activeSong</code>
     *
     * @return <code>Clip</code>a reference to <code>activeSong</code>
     */
    public Clip getActiveSong() {
        return activeSong;
    }

    /**
     * Toggles the playstate of <code>activeSong</code>
     */
    public void playPauseMusicPlayer() {
        if (activeSong.isActive()) {
            activeSong.stop();
        } else {
            activeSong.start();
        }
    }

    /**
     * Closes the line used by <code>activeSong</code> freeing up system
     * resources and the line itself.
     */
    public void closeLines() {
        activeSong.close();
    }
}
