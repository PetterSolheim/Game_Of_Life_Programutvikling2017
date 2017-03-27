/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
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
    private TextField txtB;
    @FXML
    private TextField txtS;
    @FXML
    private VBox vBox;

    Stage stage;
    Board board;

    @FXML
    private void cancel() {
        stage.close();
    }

    @FXML
    private void save() {
        // apply new birth rules
        try {
            String[] bStringArray = txtB.getText().split("");
            int[] bIntArray = new int[bStringArray.length];
            for (int i = 0; i < bStringArray.length; i++) {
                bIntArray[i] = Integer.parseInt(bStringArray[i]);
            }
            board.setBirthRules(bIntArray);

            // apply new survival rules
            String[] sStringArray = txtS.getText().split("");
            int[] sIntArray = new int[sStringArray.length];
            for (int i = 0; i < sStringArray.length; i++) {
                sIntArray[i] = Integer.parseInt(sStringArray[i]);
            }
            board.setSurviveRules(sIntArray);
            stage.close();
        } catch (NumberFormatException e) {
            System.err.println("not a number!");
        }
    }

    /**
     * Stores a reference to the windows stage for easy access.
     */
    private void defineStage() {
        stage = (Stage) vBox.getScene().getWindow();
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
    public void initData(Board board) {
        this.board = board;
        // load and display current rules
        StringBuilder birthString = new StringBuilder();
        for (int value : board.getBirthRules()) {
            birthString.append(value);
        }
        txtB.setText(birthString.toString());

        StringBuilder survivalString = new StringBuilder();
        for (int value : board.getSurviveRules()) {
            survivalString.append(value);
        }
        txtS.setText(survivalString.toString());
    }

}
