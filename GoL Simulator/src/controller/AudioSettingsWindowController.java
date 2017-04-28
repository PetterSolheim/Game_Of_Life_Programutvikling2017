package controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.UnsupportedAudioFileException;
import model.AudioManager;
import model.BoardDynamic;
import model.BoardSound;
import view.DialogBoxes;

/**
 * FXML Controller class for Audio settings window
 *
 * @author peven
 */
public class AudioSettingsWindowController implements Initializable {

    private BoardSound board;
    private AudioManager audioManager;
    private AudioTimer timer;
    private Stage thisStage;
    private boolean isPlaying;
    private ObservableList<String> songNames = FXCollections.observableArrayList();
    @FXML
    private ImageView imgPlayPause;
    @FXML
    private Slider volumeSlider, audioBoardSlider;
    @FXML
    private CheckBox generationAudio, cellAudio;
    @FXML
    private Button btnPlayBoard;
    @FXML
    private ListView songList;

    /**
     * Initializes the controller class.
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
        songList.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            playSong();
        });
        Platform.runLater(this::setFpsAndCellTimer);
        Platform.runLater(this::changeVolume);
    }

    public void playNextSong() {
        if (songList.getSelectionModel().getSelectedIndex() != songList.getItems().size() - 1) {
            songList.getSelectionModel().select(songList.getSelectionModel().getSelectedIndex() + 1);
        }
    }

    public void playPreviousSongOrResetActiveSong() {
        if (audioManager.getActiveSong().getFramePosition() / 10000 < 1) {
            if (songList.getSelectionModel().getSelectedIndex() != 0) {
                songList.getSelectionModel().select(songList.getSelectionModel().getSelectedIndex() - 1);
            }
        } else {
            audioManager.resetSong();
        }
    }

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

    public void createNextGeneration() {
        Thread newThread = new Thread(board);
        newThread.run();
        timer.stop();
    }

    public void initializeBoardSound(BoardDynamic board) {
        BoardSound b = new BoardSound(board);
        this.board = b;
        timer.stop();
    }

    public AudioManager getAudioManager() {
        return this.audioManager;
    }

    public void setBoardAudioLength(long audioLength) {
        board.setAudioLength(audioLength);
    }

    public void getAudioFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Formats", "*.wav", "*.wave"),
                new FileChooser.ExtensionFilter("Wave / .wav", "*.wav", ".wave"));
        File f = fileChooser.showOpenDialog(thisStage);
        if (f != null) {
            if (checkIfSongIsLoaded(f.getName())) {
                DialogBoxes.genericErrorMessage("Song is already loaded", "Try a differen song.");
            } else {
                audioManager.addAbsolutePath(f);
                songNames.add(f.getName());
                updateSongList(f.getName());
            }
        }
    }

    private void reloadSongList(ArrayList<String> absolutePaths) {
        System.out.println("controller.AudioSettingsWindowController.reloadSongList()");
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
        songList.setItems(songNames);
    }

    public boolean checkIfSongIsLoaded(String songName) {
        for (int i = 0; i < songNames.size(); i++) {
            if (songName.equals(songNames.get(i))) {
                return true;
            }
        }
        return false;
    }

    private void playSong() {
        try {
            audioManager.loadSongFromAbsolutePath(songList.getSelectionModel().getSelectedItem().toString());
        } catch (UnsupportedAudioFileException ex) {
            DialogBoxes.genericErrorMessage("Unsupported Audio File", "Try a different file\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void updateSongList(String newSong) {
        songList.setItems(songNames);
        songList.getSelectionModel().select(newSong);
    }

    public void setThisStage(Stage stage) {
        this.thisStage = stage;
    }

    public void changeVolume() {
        audioManager.volume((float) volumeSlider.getValue());
    }

    public void toggleMusicPlayState() {
        if (audioManager.getActiveSong().isActive()) { // pause song
            showPauseIcon();
            audioManager.playPauseMusicPlayer();
        } else { // play song
            showPlayIcon();
            audioManager.playPauseMusicPlayer();
        }
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

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

    private void showPauseIcon() {
        Image pause = new Image("/img/pause.png");
        imgPlayPause.setImage(pause);
    }

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

    public void setBoardIsActive(boolean isActive) {
        board.setIsActive(isActive);
    }
}
