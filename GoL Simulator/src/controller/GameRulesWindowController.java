/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Board;

/**
 *
 * @author aleks
 */
public class GameRulesWindowController implements Initializable {

    @FXML
    private javafx.scene.control.Button btnCancel;
    @FXML
    private javafx.scene.control.Button btnSave;
    @FXML
    private javafx.scene.control.TextField txtMin;
    @FXML
    private javafx.scene.control.TextField txtMax;
    @FXML
    private javafx.scene.control.TextField txtBirth;
    Board myBoard = Board.getInstance();

    @FXML
    public void cancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();

    }

    @FXML
    public void save() {
        try {
            int min = Integer.parseInt(txtMin.getText());
            int max = Integer.parseInt(txtMax.getText());
            int birth = Integer.parseInt(txtBirth.getText());

            myBoard.setRules(min, max, birth);
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid input");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String min = String.valueOf(myBoard.getMinToSurvive());
        String max = String.valueOf(myBoard.getMaxToSurvive());
        String birth = String.valueOf(myBoard.getBirth());

        txtMin.setText(min);
        txtMax.setText(max);
        txtBirth.setText(birth);
    }

}
