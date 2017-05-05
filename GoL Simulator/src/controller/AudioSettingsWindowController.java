package controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.sound.sampled.UnsupportedAudioFileException;
import model.AudioManager;
import model.BoardDynamic;
import model.Statistics;
import view.DialogBoxes;

/**
 * FXML Controller class for Audio settings window. This class contains a
 * ListVIew with an EventHandler that determines which songs are being played by
 * the AudioManager, and most of the tasks regarding audio playback are
 * triggered by this EVentHandler.
 */
public class AudioSettingsWindowController implements Initializable {

    /**
     * A reference to the main board.
     */
    private BoardDynamic mainBoard;
    /**
     * A reference to the AudioManager. Used to call methods controlling audio
     * playback.
     */
    private AudioManager audioManager;
    private Stage thisStage;
    ;
    /**
     * Contains what is displayed in the trackList
     */
    private ObservableList<String> songNames = FXCollections.observableArrayList();
    /**
     * Used to change the icon so the user easily can see the playstate of the
     * music player.
     */
    @FXML
    private ImageView imgPlayPause;
    @FXML
    private Slider volumeSlider;
    /**
     * This ListView controls what songs are being playing in the AudioManager,
     * but the method that calls for change in the AudioManagers playstate is
     * triggered by an EventListener. This means that what is selected is passed
     * to the AudioManager every time a change occurs. Note that a LineListener
     * in the AudioManager forces the ListView to select another song when the
     * active song is finished, triggering the EventListener
     */
    @FXML
    private ListView trackList;

    /**
     * Initializes the controller class and various listeners. Also calls
     * <code>reloadSongList()</code> if an instance of AudioManager exist.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (AudioManager.isCreated()) {
            audioManager = AudioManager.getSingelton();
            ArrayList<String> paths = audioManager.getAllLoadedSongs();
            reloadSongList(paths);
        }
        audioManager = AudioManager.getSingelton();
        audioManager.setController(this);
        volumeSlider.valueProperty().addListener((observable) -> {
            changeVolume();
        });
        trackList.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            playSong();
        });
        Platform.runLater(this::changeVolume);
    }

    /**
     * Sets the next item in the <code>ListView</code> as selected, triggering
     * it's eventListener. Calls <code>resetPlayList</code> if it is at the last
     * index.
     *
     */
    @FXML
    public void playNextSong() {
        if (trackList.getSelectionModel().getSelectedIndex() != trackList.getItems().size() - 1) {
            trackList.getSelectionModel().select(trackList.getSelectionModel().getSelectedIndex() + 1);
        } else {
            resetPlayList();
        }
    }

    /**
     * Resets the trackList, pauses the music and displays the play icon.
     */
    private void resetPlayList() {
        trackList.getSelectionModel().select(0);
        audioManager.getActiveSong().stop();
        //showPlayIcon();
    }

    /**
     * Sets the previous item in trackList as active on double click. On single
     * click it calls resetSong()
     *
     * @see model.AudioManager#resetSong
     */
    @FXML
    private void playPreviousSongOrResetActiveSong() {
        if (audioManager.getActiveSong().getFramePosition() / 10000 < 1.5) {
            if (trackList.getSelectionModel().getSelectedIndex() != 0) {
                trackList.getSelectionModel().select(trackList.getSelectionModel().getSelectedIndex() - 1);
            }
        } else {
            audioManager.resetSong();
        }
    }

    /**
     * Opens a FileChooser and passes the selected items to the AudioManager.
     * This class also calls the method updateSongList which refreshes the
     * trackList. checkIfSongIsLoaded is also called, displaying a dialog box if
     * the song is loaded.
     */
    @FXML
    private void getAudioFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Formats", "*.wav", "*.wave"),
                new FileChooser.ExtensionFilter("Wave / .wav", "*.wav", ".wave"));
        List<File> file = fileChooser.showOpenMultipleDialog(thisStage);
        if (file != null) {
            for (File f : file) {
                if (checkIfSongIsLoaded(f.getName())) {
                    if (checkIfSongIsLoaded(f.getName())) {
                        DialogBoxes.genericErrorMessage("Song " + f.getName() + " is already loaded", "Try a differen song.");
                    }
                } else {
                    audioManager.addAbsolutePath(f);
                    songNames.add(f.getName());
                    updateSongList(f.getName());
                }
            }
        }
    }

    /**
     * Loads previously loaded songs back into the track list by using the
     * absolute paths stored in the AudioManager. This method is called in
     * Initialize if an instance of AudioManager exists
     */
    private void reloadSongList(ArrayList<String> absolutePaths) {
        for (int i = 0; i < absolutePaths.size(); i++) {
            int lastBackSlash = 0;
            String songPath = absolutePaths.get(i);
            char[] chars = songPath.toCharArray();
            for (int o = chars.length - 1; o > 0; o--) {
                if (chars[o] == '\\') {
                    lastBackSlash = o;
                    break;
                }
            }
            String name = (String) songPath.subSequence(lastBackSlash + 1, chars.length);
            songNames.add(name);
        }
        trackList.setItems(songNames);
    }

    /**
     * This method ensures that a song can only be loaded once by checking if
     * the name of the song is in the songNames ArrayList
     */
    private boolean checkIfSongIsLoaded(String songName) {
        for (int i = 0; i < songNames.size(); i++) {
            if (songName.equals(songNames.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is called when the selected item in the trackList changes. It
     * calls loadSong() from AudioManager that starts the audio playback.
     */
    private void playSong() {
        try {
            audioManager.loadSong(trackList.getSelectionModel().getSelectedItem().toString());
        } catch (UnsupportedAudioFileException ex) {
            DialogBoxes.genericErrorMessage("Unsupported Audio File", "Try a different file\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param newSong the name of the song to be displayed in ListView
     * trackList.
     */
    private void updateSongList(String newSong) {
        trackList.setItems(songNames);
        trackList.getSelectionModel().select(newSong);
    }

    /**
     *
     * @param stage
     */
    public void setThisStage(Stage stage) {
        this.thisStage = stage;
    }

    /**
     * Changes the volume based on a private <code>Slider</code>.
     */
    @FXML
    private void changeVolume() {
        audioManager.volume((float) volumeSlider.getValue());
    }

    /**
     * Calls <code>playPauseMusicPlayer</code> that determines whether or not
     * the music is playing.
     *
     * @see model.AudioManager#playPauseMusicPlayer
     */
    @FXML
    private void toggleMusicPlayState() {
        if (audioManager.getActiveSong().isActive()) { // pause song
            audioManager.playPauseMusicPlayer();
        } else { // play song
            audioManager.playPauseMusicPlayer();
        }
    }

    /**
     * Swaps the play icon with the pause icon.
     */
    public void showPauseIcon() {
        Image pause = new Image("/img/pause.png");
        imgPlayPause.setImage(pause);
    }

    /**
     * Swaps the pause icon with the play icon.
     */
    public void showPlayIcon() {
        Image play = new Image("/img/play.png");
        imgPlayPause.setImage(play);
    }
}
