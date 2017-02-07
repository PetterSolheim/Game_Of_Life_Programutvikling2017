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
import javafx.scene.canvas.Canvas;
import model.TestBoard;
import view.ResizableCanvas;

/**
 * FXML Controller class for the main window.
 * The main window consists of two toolbars, one at the top and one at the bottom. 
 * In the middle is the canvas which is the visual representation of the cells/Board.
 * @author peven
 */
public class MainWindowController implements Initializable {

    @FXML
    private ResizableCanvas canvas;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Controller Main initialize.");
        TestBoard b = new TestBoard();
        canvas.draw(b);
        
        
    }    
    
}
