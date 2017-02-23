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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
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

    @FXML
    private ResizableCanvas canvas;
    @FXML
    private Slider cellSizeSlider;
    @FXML
    private Slider fpsSlider;
    @FXML
    private Text txtShowCellSize;
    @FXML
    private Text txtShowFps;
    @FXML
    private Text txtShowGen;
    @FXML
    private Text txtShowCellCount;
    //@FXML
    //private MenuBar menuBar;
    //@FXML
    //private AnchorPane rootNode;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ColorPicker livingCellColorPicker;
    @FXML
    private ColorPicker deadCellColorPicker;
    @FXML
    private ColorPicker backgroundColorPicker;
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
        changeFPSAndShow();
        livingCellColorPicker.getStyleClass().add("button");
        deadCellColorPicker.getStyleClass().add("button");
        backgroundColorPicker.getStyleClass().add("button");
        livingCellColorPicker.setPrefWidth(30);
        deadCellColorPicker.setPrefWidth(30);
        backgroundColorPicker.setPrefWidth(30);
        
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
        displayGeneration();
        displayCellCount();
    }

    @FXML
    private void reset() {
        b.resetBoard();
        canvas.redrawBoard(b);
        time.stop();
        isPaused = true;
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

    @FXML
    private void changeLivingCellColor() {
        canvas.setLivingCellColor(livingCellColorPicker.getValue());
        canvas.redrawBoard(b);
    }

    @FXML
    private void changeBackground() {
        canvas.setBackgroundColor(backgroundColorPicker.getValue());
        canvas.redrawBoard(b);
    }
    @FXML
    private void changeDeadCellColor(){
        canvas.setDeadCellColor(deadCellColorPicker.getValue());
        canvas.redrawBoard(b);
    }
    public void changeCellSizeAndShow() {
        canvas.setCellSize((int) cellSizeSlider.getValue());
        canvas.calculateNewDimensions(b);
        displayCellSize();
    }

    public void displayCellSize() {
        txtShowCellSize.setText(Integer.toString((int) cellSizeSlider.getValue()));
    }

    public void changeFPSAndShow() {
        long newTimer = (long) ((1 / fpsSlider.getValue()) * 1000000000);
        time.setNextGenerationTimer(newTimer);
        displayFps();
    }
    public void displayCellCount() {
        txtShowCellCount.setText(Integer.toString(b.getCellCount()));
    }
    public void displayGeneration() {
        txtShowGen.setText(Integer.toString(b.getGenerationCount()) + " ");
    }
    public void displayFps() {
        txtShowFps.setText(Double.toString(fpsSlider.getValue()));
    }

    public void createNextGeneration() {
        b.nextGeneration();
        canvas.draw(b);
        displayCellCount();
        displayGeneration();
    }

    @FXML
    private void quit() {
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

    @FXML
    public void toggleClickedCell(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            int row = (int) (event.getX() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            int col = (int) (event.getY() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            b.toggleCellState(row, col);
            canvas.draw(b);
        }

    }

    @FXML
    public void dragCanvas(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            scrollPane.setPannable(true);
        }
    }

    @FXML
    public void dragCanvasEnded() {
        scrollPane.setPannable(false);
    }
}
