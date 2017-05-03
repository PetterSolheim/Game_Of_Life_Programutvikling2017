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
        alert.setHeaderText("Error reading file!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * A simple dialog box for displaying PatternFormatException messages.
     * @param message a <code>String</code> specifying the message to be
     * displayed.
     */
    public static void patternFormatError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error parsing file!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void genericErrorMessage (String headerText, String message){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(headerText);
        a.setContentText(message);
        a.showAndWait();
    }

    
    /**
     * A simple dialog box for displaying input errors.
     * @param message a <code>String</code> specifying the message to be
     * displayed.
     */
    public static void inputError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Displays a confirmation warning box which return true if user selects ok,
     * and false if user selects cancel.
     * @param message a <code>String</code> specifying the text to display with
     * the confirmation warning.
     * @return a <code>boolean</code> specifying user selection where true = 
     * OK and false = cancel.
     */
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

    
    /**
     * A simple dialog box.
     * @param message a <code>String</code> specifying the message to be
     * displayed.
     * @param title a <code>String</code> specifying the title for the dialog
     * box.
     */
    public static void info(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Board Metadata");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
