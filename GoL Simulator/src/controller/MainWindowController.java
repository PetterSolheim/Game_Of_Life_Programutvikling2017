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
import model.TestBoard;
import view.ResizableCanvas;

/**
 * FXML Controller class for the main window. The main window consists of two
 * toolbars, one at the top and one at the bottom. In the middle is the canvas
 * which is the visual representation of the cells/Board.
 *
 * @author peven
 */
public class MainWindowController extends AnimationTimer implements Initializable {

    @FXML
    private ResizableCanvas canvas;

    private TimeKeeper time;

    private Board b;
    private boolean isPaused;
    private long nextGeneration;
    private float timeBetweenGenerations;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        b = Board.getInstance();
        isPaused = true;
        canvas.draw(b);
    }

    @FXML
    protected void play() {
        if (isPaused) {
            nextGeneration = System.nanoTime();
            start();
        } else {
            stop();
        }
    }

    @Override
    public void handle(long currentTime) {
        if (currentTime > nextGeneration) {
            nextGeneration += timeBetweenGenerations;
            b.nextGeneration();
            canvas.draw(b);
        }
    }

    private void setIsPaused() {
        isPaused = !isPaused;
    }

    private boolean getIsPaused() {
        return isPaused;
    }
}
