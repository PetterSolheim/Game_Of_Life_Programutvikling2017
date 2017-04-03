/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import model.Board;

/**
 *
 * @author aleks
 */
public class GameRulesWindowController implements Initializable {

    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;
    @FXML
    private CheckBox s1, s2, s3, s4, s5, s6, s7, s8;
    @FXML
    private CheckBox b1, b2, b3, b4, b5, b6, b7, b8;

    Stage stage;
    Board board;

    @FXML
    private void cancel() {
        stage.close();
    }

    @FXML
    private void save() {
        CheckBox[] survivalCheckBoxes = {s1, s2, s3, s4, s5, s6, s7, s8};
        CheckBox[] birthCheckBoxes = {b1, b2, b3, b4, b5, b6, b7, b8};
        ArrayList<Integer> survivalRules = new ArrayList<Integer>();
        ArrayList<Integer> birthRules = new ArrayList<Integer>();

        for (int i = 0; i < 8; i++) {
            if (survivalCheckBoxes[i].isSelected()) {
                System.out.println("Found: s"+(i+1));
                survivalRules.add(i+1);
            }
            
            if (birthCheckBoxes[i].isSelected()) {
                System.out.println("Found: b"+(i+1));
                birthRules.add(i+1);
            }
        }

        board.setSurviveRules(survivalRules);
        board.setBirthRules(birthRules);
        stage.close();
    }

    /**
     * Stores a reference to the windows stage for easy access.
     */
    private void defineStage() {
        stage = (Stage) btnSave.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // make stage available
        Platform.runLater(this::defineStage);
    }

    /**
     * Method for passing the Board object to the settings window so that the
     * settings window can populate its fields.
     *
     * @param board
     */
    public void initData(Board inputBoard) {
        board = inputBoard;
        // create an array of the checkboxes to allow easy iteration of their
        // values.
        CheckBox[] survivalCheckBoxes = {s1, s2, s3, s4, s5, s6, s7, s8};
        CheckBox[] birthCheckBoxes = {b1, b2, b3, b4, b5, b6, b7, b8};
        
        // load and display current survival rules
        for (int i = 0; i < board.getSurviveRules().size(); i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getSurviveRules().get(i) == j + 1) {
                    survivalCheckBoxes[j].setSelected(true);
                }
            }
        }

        // load and display current birth rules
        for (int i = 0; i < board.getBirthRules().size(); i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getBirthRules().get(i) == j + 1) {
                    birthCheckBoxes[j].setSelected(true);
                }
            }
        }
    }

}
