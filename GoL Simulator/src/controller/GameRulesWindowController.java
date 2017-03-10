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
import model.Rules;

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

    Rules rules = Rules.getInstance();

    @FXML
    public void cancel() {

        stage.close();
    }

    @FXML
    public void save() {
        // apply new birth rules
        String[] bStringArray = txtB.getText().split("");
        int[] bIntArray = new int[bStringArray.length];
        for (int i = 0; i < bStringArray.length; i++) {
            bIntArray[i] = Integer.parseInt(bStringArray[i]);
        }
        rules.setBirthRules(bIntArray);

        // apply new survival rules
        String[] sStringArray = txtS.getText().split("");
        int[] sIntArray = new int[sStringArray.length];
        for (int i = 0; i < sStringArray.length; i++) {
            sIntArray[i] = Integer.parseInt(sStringArray[i]);
        }
        rules.setSurviveRules(sIntArray);

        stage.close();
    }

    private void defineStage() {
        stage = (Stage) vBox.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // make stage available
        Platform.runLater(this::defineStage);

        // load and display current rules
        StringBuilder birthString = new StringBuilder();
        for (int value : rules.getBirthRules()) {
            birthString.append(value);
        }
        txtB.setText(birthString.toString());

        StringBuilder survivalString = new StringBuilder();
        for (int value : rules.getSurvivalRules()) {
            survivalString.append(value);
        }
        txtS.setText(survivalString.toString());

    }

}
