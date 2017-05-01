package view;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

    public static void genericErrorMessage (String headerText, String message){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(headerText);
        a.setContentText(message);
        a.showAndWait();
    }

    
    public static void inputError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }
}
