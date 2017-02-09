/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import model.Board;
import view.ResizableCanvas;

/**
 * FXML Controller class for the main window. The main window consists of two
 * toolbars, one at the top and one at the bottom. In the middle is the canvas
 * which is the visual representation of the cells/Board.
 *
 * @author peven
 */
public class MainWindowController implements Initializable {

    @FXML
    private ResizableCanvas canvas;

    private Board b;
    private Timer time;
    private boolean isPaused;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        b = Board.getInstance();
        time = new Timer (this);
        isPaused = true;
        canvas.draw(b);
    }

    @FXML
    private void play() {
        if (isPaused) {
            isPaused = false;
            time.start();
        } else {
            isPaused = true;
            time.stop();
        }
    }
    public void initiateNextGeneration (){
        b.nextGeneration();
        canvas.draw(b);
    }
    private void setIsPaused() {
        isPaused = !isPaused;
    }

    private boolean getIsPaused() {
        return isPaused;
    }
}
