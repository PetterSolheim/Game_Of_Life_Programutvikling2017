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
 * Controller for the game rules window. 
 * @author aleks
 */
public class GameRulesWindowController implements Initializable {

    @FXML
    private CheckBox s0, s1, s2, s3, s4, s5, s6, s7, s8;
    @FXML
    private CheckBox b0, b1, b2, b3, b4, b5, b6, b7, b8;

    private Stage stage;
    private Board board;

    /**
     * Closes the window without applying any changes.
     */
    @FXML
    private void cancel() {
        stage.close();
    }

    /**
     * Uses the state of the CheckBoxes to apply the given rules.
     */
    @FXML
    private void save() {
        // place all checkboxes in an array for easy iteration.
        CheckBox[] survivalCheckBoxes = {s0, s1, s2, s3, s4, s5, s6, s7, s8};
        CheckBox[] birthCheckBoxes = {b0, b1, b2, b3, b4, b5, b6, b7, b8};
        // arraylist for storing the corresponding values of the checkboxes.
        ArrayList<Integer> survivalRules = new ArrayList<Integer>();
        ArrayList<Integer> birthRules = new ArrayList<Integer>();

        for (int i = 0; i <= 8; i++) {
            // determin which survivalCheckBoxes are checked, and store the value
            // they represent in an arraylist.
            if (survivalCheckBoxes[i].isSelected()) {
                survivalRules.add(i);
            }

            // determin which birthCheckBoxes are checked, and store the value
            // they represent in an arraylist.
            if (birthCheckBoxes[i].isSelected()) {
                birthRules.add(i);
            }
        }

        // set the new rules, and close the window.
        board.setSurviveRules(survivalRules);
        board.setBirthRules(birthRules);
        stage.close();
    }

    /**
     * Stores a reference to the windows stage for easy access.
     */
    private void defineStage() {
        stage = (Stage) s0.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(this::defineStage); // makes the stage available.
    }

    /**
     * Used to pass the Board that the GameRulesWindow will reflect. The method
     * uses this Board to determine the state of the windows CheckBoxes.
     * @param b 
     */
    public void initData(Board b) {
        board = b;
        
        // create an array of the checkboxes to allow easy iteration of their
        // values.
        CheckBox[] survivalCheckBoxes = {s0, s1, s2, s3, s4, s5, s6, s7, s8};
        CheckBox[] birthCheckBoxes = {b0, b1, b2, b3, b4, b5, b6, b7, b8};

        // load and display current survival rules
        for (int i = 0; i < board.getSurviveRules().size(); i++) {
            for (int j = 0; j <= 8; j++) {
                if (board.getSurviveRules().get(i) == j) {
                    survivalCheckBoxes[j].setSelected(true);
                }
            }
        }

        // load and display current birth rules
        for (int i = 0; i < board.getBirthRules().size(); i++) {
            for (int j = 0; j <= 8; j++) {
                if (board.getBirthRules().get(i) == j) {
                    birthCheckBoxes[j].setSelected(true);
                }
            }
        }
    }

}
