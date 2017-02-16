/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javafx.stage.Modality;
import javafx.stage.Stage;
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

    @FXML private ResizableCanvas canvas;
    @FXML private Slider cellSizeSlider;
    @FXML private Slider fpsSlider;
    @FXML private Text showCellSize;
    @FXML private Text showFps;
    @FXML private MenuBar menuBar;
    @FXML private AnchorPane rootNode;
    private Board b;
    private Timer time;
    private boolean isPaused;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        b = Board.getInstance();
        time = new Timer(this);
        isPaused = true;
        changeCellSizeAndShow();
        cellSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                    changeCellSizeAndShow();
                    canvas.redrawBoard(b);
            }
        });
        
        changeFPSAndShow();
        fpsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                    changeFPSAndShow();
            }
        });
        canvas.draw(b);
    }

    @FXML private void reset() {
        b.resetBoard();
        canvas.redrawBoard(b);
        time.stop();
    }

    @FXML private void play() {
        if (isPaused) {
            isPaused = false;
            time.start();
        } else {
            isPaused = true;
            time.stop();
        }
    }
    public void changeCellSizeAndShow() {
        canvas.setCellSize((int)cellSizeSlider.getValue());
        showCellSize.setText(Integer.toString((int)cellSizeSlider.getValue()));
    }
    public void displayCellSize (){
        showCellSize.setText(Integer.toString((int)cellSizeSlider.getValue()));
    }
    public void changeFPSAndShow() {
        long newTimer = (long)((1 / fpsSlider.getValue()) * 1000000000);
        time.setNextGenerationTimer(newTimer);
        showFps.setText(Double.toString(fpsSlider.getValue()));
    }
    public void displayFps (){
        showFps.setText(Double.toString(fpsSlider.getValue()));
    }
    public void createNextGeneration() {
        b.nextGeneration();
        canvas.draw(b);
    }
    @FXML private void quit (){
        Platform.exit();
    }
    public void showGameRulesWindow() throws Exception {
        Stage settings = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/view/GameRulesWindow.fxml"));
        Scene scene = new Scene(root);

        settings.setResizable(true);
        settings.initModality(Modality.APPLICATION_MODAL);
        settings.setScene(scene);
        settings.setTitle("Settings");
        settings.show();
    }
    
    @FXML public void clickedCell(MouseEvent event) {
        int row = canvas.cellClickedRow(event.getX());
        int col = canvas.cellClickedCol(event.getY());
        b.toggleCellState(row, col);
        canvas.draw(b);
    }

    public void defineSliders(Slider s){
        
    }
}
