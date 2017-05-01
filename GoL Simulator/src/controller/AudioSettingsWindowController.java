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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.sound.sampled.UnsupportedAudioFileException;
import model.AudioManager;
import model.BoardDynamic;
import model.BoardSound;
import model.Statistics;
import view.DialogBoxes;

/**
 * FXML Controller class for Audio settings window.
 * This class contains a ListVIew with an EventHandler that determines which songs are being played by the AudioManager, 
 * and most of the tasks regarding audio playback are triggered by this EVentHandler.
 */
public class AudioSettingsWindowController implements Initializable {

    private MainWindowController mainWindowController;
    /**
     * A reference to the main board.
     */
    private BoardDynamic mainBoard;
    private BoardSound board;
    /**
     * A reference to the AudioManager. Used to call methods controlling audio
     * playback.
     */
    private AudioManager audioManager;
    private AudioTimer timer;
    private Stage thisStage;
    private boolean isPlaying;
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
    private Slider volumeSlider, audioBoardSlider;
    @FXML
    private CheckBox generationAudio, cellAudio;
    @FXML
    private Button btnPlayBoard;
    /**
     * This ListView controls what songs are being playing in the
     * AudioManager, but the method that calls for change in the AudioManagers
     * playstate is triggered by an EventListener. This means that what is
     * selected is passed to the AudioManager every time a change occurs. Note
     * that a LineListener in the AudioManager forces the ListView to select
     * another song when the active song is finished, triggering the EventListener
     */
    @FXML
    private ListView trackList;
    @FXML
    private TextField txtGenerations;

    /**
     * Initializes the controller class and various listeners. Also calls
     * <code>reloadSongList()</code> if an instance of AudioManager exist.
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
        timer = new AudioTimer(this);
        volumeSlider.valueProperty().addListener((observable) -> {
            changeVolume();
        });
        audioBoardSlider.valueProperty().addListener(observalbe -> {
            setFpsAndCellTimer();
        });
        generationAudio.selectedProperty().addListener((observable) -> {
            toggleGenerationAudio();
        });
        cellAudio.selectedProperty().addListener((observable) -> {
            toggleCellAudio();
        });
        trackList.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            playSong();
        });
        Platform.runLater(this::setFpsAndCellTimer);
        Platform.runLater(this::changeVolume);
    }

    /**
     * Sets the next item in the <code>trackList</code> as active, triggering
     * it's eventListener. Calls <code>resetPlayList</code> if it is at the last
     * index.
     *
     * @see #resetPlayList
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
        showPlayIcon();
        trackList.getSelectionModel().select(0);
        audioManager.playPauseMusicPlayer();
    }

    /**
     * Sets the previous item in <code>trackList</code> as active on double
     * click. On single click it calls <code>resetSong()</code>
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
     *
     */
    public void resetBoard() {
        board.resetBoard();
    }

    private void toggleGenerationAudio() {
        if (generationAudio.isSelected()) {
            board.setGenerationAudio(true);
        } else {
            board.setGenerationAudio(false);
        }
    }

    private void toggleCellAudio() {
        if (cellAudio.isSelected()) {
            board.setCellAudio(true);
        } else {
            board.setCellAudio(false);
        }
    }

    /**
     *
     */
    public void createNextGeneration() {
        Thread newThread = new Thread(board);
        newThread.run();
        timer.stop();
    }

    /**
     *
     * @param board
     */
    public void initializeBoardSound(BoardDynamic board) {
        BoardSound b = new BoardSound(board);
        this.board = b;
        timer.stop();
    }

    /**
     *
     * @param audioLength
     */
    public void setBoardAudioLength(long audioLength) {
        board.setAudioLength(audioLength);
    }

    /**
     * Opens a <code>FileChooser</code> and passes the selected items to the
     * AudioManager. <br>
     * This class also checks if a song is already loaded.
     *
     * @see model.AudioManager#addAbsolutePath
     */
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
     * absolute paths stored in the AudioManager
     *
     * @param absolutePaths
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
     *
     * @param songName
     * @return
     */
    public boolean checkIfSongIsLoaded(String songName) {
        for (int i = 0; i < songNames.size(); i++) {
            if (songName.equals(songNames.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    private void playSong() {
        try {
            audioManager.loadSongFromAbsolutePath(trackList.getSelectionModel().getSelectedItem().toString());
            showPauseIcon();
        } catch (UnsupportedAudioFileException ex) {
            DialogBoxes.genericErrorMessage("Unsupported Audio File", "Try a different file\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param newSong
     */
    public void updateSongList(String newSong) {
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
    public void toggleMusicPlayState() {
        if (audioManager.getActiveSong().isActive()) { // pause song
            showPlayIcon();
            audioManager.playPauseMusicPlayer();
        } else { // play song
            showPlayIcon();
            audioManager.playPauseMusicPlayer();
        }
    }

    /**
     *
     * @param isPlaying
     */
    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     *
     */
    public void toggleAudioBoardPlayState() {
        if (isPlaying) { // pause
            isPlaying = !isPlaying;
            btnPlayBoard.setText("Play");
            timer.stop();
        } else { // play
            isPlaying = !isPlaying;
            btnPlayBoard.setText("Pause");
            timer.start();
        }
    }

    /**
     *
     */
    public void findSong() {
        Statistics statistics = new Statistics(mainBoard, 20);
        statistics.AudioStatistics();
        //audioManager.generateAudioSequence();
    }

    /**
     * Swaps the play icon with the pause icon.
     */
    private void showPauseIcon() {
        Image pause = new Image("/img/pause.png");
        imgPlayPause.setImage(pause);
    }

    /**
     * Swaps the pause icon with the play icon.
     */
    private void showPlayIcon() {
        Image play = new Image("/img/play.png");
        imgPlayPause.setImage(play);
    }

    private void setFpsAndCellTimer() {
        long newTimer = (long) (1000000000 / audioBoardSlider.getValue());
        timer.setFps(newTimer);
        timer.setTimeBetweenCellAudioTimer(board.getNrOfCells());
        board.setAudioLength(timer.getTimeBetweenCellAudioTimer());
    }

    /**
     *
     * @param isActive
     */
    public void setBoardIsActive(boolean isActive) {
        board.setIsActive(isActive);
    }

    /**
     *
     * @param mainWindowController
     */
    public void setMainWindowConttroller(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    /**
     *
     * @param mainBoard
     */
    public void setMainBoard(BoardDynamic mainBoard) {
        this.mainBoard = mainBoard;
    }

    /**
     *
     * @return
     */
    public BoardDynamic getMainBoard() {
        return mainBoard;
    }
}
