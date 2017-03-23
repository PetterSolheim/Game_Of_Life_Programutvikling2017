/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.scene.control.Alert;

/**
 * A class containing simple dialog boxes that can be called statically.
 * @author aleks
 */
public class DialogBoxes {

    public static void ioException(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("File not found.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void patternFormatException(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error reading file");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
