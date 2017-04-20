package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import model.Rules;

/**
 * FXML controller for the game rules window.
 */
public class GameRulesWindowController implements Initializable {

    @FXML
    private CheckBox s0, s1, s2, s3, s4, s5, s6, s7, s8;
    @FXML
    private CheckBox b0, b1, b2, b3, b4, b5, b6, b7, b8;
    @FXML
    private RadioButton rbtnStatic, rbtnDynamic;

    private Rules rules = Rules.getInstance();
    private Stage stage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(this::defineStage); // makes the stage available.
        Platform.runLater(this::loadRules); // set checkbox values based on current rules.
    }

    /**
     * Closes the window without applying any changes.
     */
    @FXML
    private void cancel() {
        stage.close();
    }

    /**
     * Sets "Static" as selected option for radio buttons.
     */
    @FXML
    private void rbtnStaticClicked() {
        rbtnStatic.setSelected(true);
        rbtnDynamic.setSelected(false);
    }

    /**
     * Sets "Dynamic" as selected option for radio buttons.
     */
    @FXML
    private void rbtnDynamicClicked() {
        rbtnDynamic.setSelected(true);
        rbtnStatic.setSelected(false);
    }

    /**
     * Runs through the settings on the screen, and applies them to the games
     * rules.
     */
    @FXML
    private void save() {
        // place all checkboxes in an array for easy iteration.
        CheckBox[] survivalCheckBoxes = {s0, s1, s2, s3, s4, s5, s6, s7, s8};
        CheckBox[] birthCheckBoxes = {b0, b1, b2, b3, b4, b5, b6, b7, b8};
        // arraylist for storing the corresponding values of the checkboxes.
        ArrayList<Integer> survivalRules = new ArrayList<Integer>();
        ArrayList<Integer> birthRules = new ArrayList<Integer>();

        // determin which survivalCheckBoxes are checked, and store the value
        // they represent in an arraylist.
        for (int i = 0; i <= 8; i++) {
            if (survivalCheckBoxes[i].isSelected()) {
                survivalRules.add(i);
            }

            // determin which birthCheckBoxes are checked, and store the value
            // they represent in an arraylist.
            if (birthCheckBoxes[i].isSelected()) {
                birthRules.add(i);
            }
        }
        rules.setSurviveRules(survivalRules);
        rules.setBirthRules(birthRules);

        // set the new rules, and close the window.
        if (rbtnStatic.isSelected()) {
            rules.setDynamic(false);
        } else {
            rules.setDynamic(true);
        }

        stage.close();
    }

    /**
     * Creates a reference to the windows Stage in the stage variable, allowing
     * easy access to the windows Stage in the rest of the class.
     */
    private void defineStage() {
        stage = (Stage) s0.getScene().getWindow();
    }

    /**
     * Loads the rules from the Rules class object, and sets the state of the
     * various JavaFX nodes to reflect those rules.
     */
    private void loadRules() {
        // create an array of the checkboxes to allow easy iteration of their
        // values.
        CheckBox[] survivalCheckBoxes = {s0, s1, s2, s3, s4, s5, s6, s7, s8};
        CheckBox[] birthCheckBoxes = {b0, b1, b2, b3, b4, b5, b6, b7, b8};

        // load and display current survival rules
        for (int i = 0; i < rules.getSurviveRules().size(); i++) {
            for (int j = 0; j <= 8; j++) {
                if (rules.getSurviveRules().get(i) == j) {
                    survivalCheckBoxes[j].setSelected(true);
                }
            }
        }

        // load and display current birth rules
        for (int i = 0; i < rules.getBirthRules().size(); i++) {
            for (int j = 0; j <= 8; j++) {
                if (rules.getBirthRules().get(i) == j) {
                    birthCheckBoxes[j].setSelected(true);
                }
            }
        }

        // load and display dynamic rules
        if (rules.isDynamic()) {
            rbtnStatic.setSelected(false);
            rbtnDynamic.setSelected(true);
        } else {
            rbtnStatic.setSelected(true);
            rbtnDynamic.setSelected(false);
        }
    }

}
