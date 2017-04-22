package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.AudioManager;

/**
 * FXML Controller class for Audio settings window
 *
 * @author peven
 */
public class AudioSettingsWindowController implements Initializable {

    private AudioManager audioManager;
    private Stage thisStage;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        audioManager = AudioManager.getSingelton();
    }

    public void loadAudioFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Formats", "*.wav"));
        File f = fileChooser.showOpenDialog(thisStage);
        if(f != null){
            
        }
    }
    public void setThisStage (Stage stage){
        this.thisStage = stage;
    }
}
