/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Board;
import view.ResizableCanvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

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
    private Button btnPlay;
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
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ColorPicker livingCellColorPicker;
    @FXML
    private ColorPicker deadCellColorPicker;
    @FXML
    private ColorPicker backgroundColorPicker;
    @FXML
    private ImageView imgPlayPause;
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
        livingCellColorPicker.setValue(canvas.getLivingCellColor());
        deadCellColorPicker.setValue(canvas.getDeadCellColor());
        backgroundColorPicker.setValue(canvas.getBackgroundColor());
        changeCellSizeAndShow();
        changeFPSAndShow();

        cellSizeSlider.valueProperty().addListener((observable) -> {
            changeCellSizeAndShow();
            canvas.redrawBoard(b);
        });

        fpsSlider.valueProperty().addListener((observable) -> {
            changeFPSAndShow();
        });

        canvas.calculateCanvasSize(b);
        canvas.redrawBoard(b);
    }

    @FXML
    private void reset() {
        stop();
        b.resetBoard();
        canvas.redrawBoard(b);
        displayCellCount();
        displayGeneration();
    }

    private void play() {
        Image imgPause = new Image("/img/pause.png");
        isPaused = false;
        imgPlayPause.setImage(imgPause);
        btnPlay.setText("Pause");
        time.start();
    }

    private void stop() {
        Image imgPlay = new Image("/img/play.png");
        isPaused = true;
        imgPlayPause.setImage(imgPlay);
        btnPlay.setText("Play");
        time.stop();
    }

    @FXML
    private void togglePlayPause() {
        if (isPaused) {
            play();
        } else {
            stop();
        }
    }

    @FXML
    private void changeLivingCellColor() {
        canvas.setLivingCellColor(livingCellColorPicker.getValue());
        canvas.redrawBoard(b);
    }

    @FXML
    private void changeBackgroundColor() {
        canvas.setBackgroundColor(backgroundColorPicker.getValue());
        canvas.redrawBoard(b);
    }

    @FXML
    private void changeDeadCellColor() {
        canvas.setDeadCellColor(deadCellColorPicker.getValue());
        canvas.redrawBoard(b);
    }

    public void changeCellSizeAndShow() {
        canvas.setScaleX(cellSizeSlider.getValue());
        canvas.setScaleY(cellSizeSlider.getValue());
        canvas.redrawBoard(b);
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
        txtShowCellCount.setText(Integer.toString(b.getCellCount()) + " .");
    }

    public void displayGeneration() {
        txtShowGen.setText(Integer.toString(b.getGenerationCount()) + " ");
    }

    public void displayFps() {
        txtShowFps.setText(Integer.toString((int) fpsSlider.getValue()));
    }

    @FXML
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
    public void showStatistics () throws IOException {
        Stage statistics = new Stage ();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StatisticsWindow.fxml"));
        FlowPane root = loader.load();
        
        Scene scene = new Scene (root);
        statistics.setScene(scene);
        statistics.setTitle("Statistics");
        statistics.show();
    }
    @FXML
    public void toggleClickedCell(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            int row = (int) (event.getY() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            int col = (int) (event.getX() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            b.toggleCellState(row, col);
            canvas.drawCell(b, row, col);
        }

    }

    @FXML
    public void dragCanvas(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            scrollPane.setPannable(true);
        } else {
            if (!isPaused) {
                togglePlayPause();
            }
            int row = (int) (event.getY() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            int col = (int) (event.getX() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            b.reviveCell(row, col);
            canvas.drawCell(b, row, col);
        }
    }

    @FXML
    public void dragCanvasEnded() {
        scrollPane.setPannable(false);
    }
}
