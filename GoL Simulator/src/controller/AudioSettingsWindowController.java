package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML
    private ImageView imgPlayPause;
    @FXML
    private Slider volumeSlider, audioBoardSlider;
    @FXML
    private CheckBox generationAudio, cellAudio;
    @FXML
    private Button btnPlayBoard;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        audioManager = AudioManager.getSingelton();
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
        Platform.runLater(this::setFpsAndCellTimer);
    }

    public void resetBoard() {
        board.resetBoard();
    }

    private void toggleGenerationAudio() {
        if(generationAudio.isSelected()){
            board.setGenerationAudio(true);
        } else {
            board.setGenerationAudio(false);
        }
    }

    private void toggleCellAudio() {
        if(cellAudio.isSelected()){
            board.setCellAudio(true);
        }else {
            board.setCellAudio(false);
        }
    }

    public void createNextGeneration() {
        Thread newThread = new Thread(board);
        newThread.run();
        
    }
    
    public void initializeBoardSound(BoardDynamic board) {
        BoardSound b = new BoardSound(board);
        this.board = b;
        timer.stop();
    }

    public AudioManager getAudioManager() {
        return this.audioManager;
    }
    public void setBoardAudioLength (long audioLength){
        board.setAudioLength(audioLength);
    }
    public void getAudioFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Formats", "*.wav"));
        File f = fileChooser.showOpenDialog(thisStage); // why can thisStage be null ¯\_(ツ)_/¯ see setThisStage
        if (f != null) {
            try {
                audioManager.loadAudiofile(f.getAbsoluteFile());
            } catch (UnsupportedAudioFileException ex) {
                DialogBoxes.genericErrorMessage("Unsupported Audio File", ex.getMessage());
            }
        }
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
    public void setIsPlaying (boolean isPlaying){
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

    public void resetSong() {
        audioManager.resetSong();
    }
    public void setBoardIsActive (boolean isActive){
        board.setIsActive(isActive);
    }
}
