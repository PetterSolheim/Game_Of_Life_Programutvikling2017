package view;

import javafx.scene.control.Alert;

/**
 * A class containing simple dialog boxes that can be called statically. Allows
 * one to display dialog boxes with fewer lines of code.
 */
public class DialogBoxes {

    /**
     * A simple dialog box for displaying IOException messages.
     * @param message The message to display in the dialog box. 
     */
    public static void ioException(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("File not found.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * A simple dialog box for displaying PatternFormatException messages.
     * @param message The message to display in the dialog box.
     */
    public static void patternFormatException(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error reading file");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
