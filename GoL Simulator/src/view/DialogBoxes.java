package view;

import javafx.scene.control.Alert;

/**
 * A class containing simple dialog boxes that can be called statically. Allows
 * dialog boxes to be easily reused throughout the program.
 */
public class DialogBoxes {

    /**
     * A simple dialog box for displaying IOException messages.
     * @param message a <code>String</code> specifying the message to be
     * displayed.
     */
    public static void ioException(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("File not found.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * A simple dialog box for displaying PatternFormatException messages.
     * @param message a <code>String</code> specifying the message to be
     * displayed.
     */
    public static void patternFormatException(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error reading file");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
