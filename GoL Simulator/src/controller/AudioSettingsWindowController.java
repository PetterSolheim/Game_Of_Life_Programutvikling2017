package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import view.DialogBoxes;

/**
 * FXML Controller class for Audio settings window
 *
 * @author peven
 */
public class AudioSettingsWindowController implements Initializable {

    private AudioManager audioManager;
    private Stage thisStage;
    @FXML
    private ImageView imgPlayPause;
    @FXML
    private Slider volumeSlider;
    @FXML
    private CheckBox generationAudio, cellAudio;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        audioManager = AudioManager.getSingelton();
        volumeSlider.valueProperty().addListener((observable) -> {
            changeVolume();
        });
    }

    public void getAudioFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Formats", "*.wav"));
        File f = fileChooser.showOpenDialog(thisStage); // why can this be null ¯\_(ツ)_/¯ see setThisStage
        if (f != null) {
            try {
                audioManager.loadAudiofile(f.getAbsoluteFile());
            } catch (UnsupportedAudioFileException ex) {
                DialogBoxes.genericErrorMessage("Unsupported Audio File", ex.getMessage());
            }
        }
    }
    @FXML 
    public void playBoardAudio (){
        
    }
    public void setThisStage(Stage stage) {
        this.thisStage = null;
    }

    public void changeVolume() {
        audioManager.volume((float)volumeSlider.getValue());   
    }

    public void togglePlayState() {
            if (audioManager.getActiveSong().isActive()) { // pause song
                showPauseIcon();
                audioManager.playPause();
            } else { // play song
                showPlayIcon();
                audioManager.playPause();
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

    public void resetSong() {
            audioManager.resetSong();
    }
}
