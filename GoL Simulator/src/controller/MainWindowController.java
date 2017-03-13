/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import view.ResizableCanvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

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
    private Board board;
    private Timer time;
    private boolean isPaused;
    private Stage stage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(this::defineStage); // allows easy referal to the windows stage

        board = new Board(700, 700);
        time = new Timer(this);
        isPaused = true;
        livingCellColorPicker.setValue(canvas.getLivingCellColor());
        deadCellColorPicker.setValue(canvas.getDeadCellColor());
        backgroundColorPicker.setValue(canvas.getBackgroundColor());
        setCanvasScale();
        setFps();

        cellSizeSlider.valueProperty().addListener((observable) -> {
            setCanvasScale();
            canvas.draw(board);
        });

        fpsSlider.valueProperty().addListener((observable) -> {
            setFps();
        });

        canvas.calculateCanvasSize(board);
        canvas.draw(board);
    }

    @FXML
    private void newBoard() {
        
    }

    @FXML
    private void reset() {
        stop();
        board.resetBoard();
        canvas.draw(board);
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

    @FXML
    private void openFromDisk() {
        FileChooser fileChooser = new FileChooser();
        FileImporter fileImporter = new FileImporter();

        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Formats", "*.rle", "*.lif", "*.life", "*.cells"),
                new FileChooser.ExtensionFilter("RLE", "*.rle"),
                new FileChooser.ExtensionFilter("Life 1.05/1.06", "*.lif", "*.life"),
                new FileChooser.ExtensionFilter("Plaintext", "*.cells"));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null && file.exists()) {
            try {
                board = fileImporter.readGameBoardFromDisk(file);
                canvas.calculateCanvasSize(board);
                canvas.draw(board);
            } catch (IOException e) {
                System.err.println("File not found: " + e);
            }
        }
    }

    @FXML
    private void openFromUrl() {
        FileImporter fileImporter = new FileImporter();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Open URL");
        dialog.setContentText("Please enter URL for the file you wish to open");

        Optional<String> url = dialog.showAndWait();
        if (url.isPresent()) {
            try {
                board = fileImporter.readGameBoardFromUrl(url.get());
                canvas.calculateCanvasSize(board);
                canvas.draw(board);
            } catch (IOException e) {
                System.err.println("File not found: " + e);
            }
        }
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
        canvas.draw(board);
    }

    @FXML
    private void changeBackgroundColor() {
        canvas.setBackgroundColor(backgroundColorPicker.getValue());
        canvas.draw(board);
    }

    @FXML
    private void changeDeadCellColor() {
        canvas.setDeadCellColor(deadCellColorPicker.getValue());
        canvas.draw(board);
    }

    public void setCanvasScale() {
        canvas.setScaleX(cellSizeSlider.getValue());
        canvas.setScaleY(cellSizeSlider.getValue());
        canvas.draw(board);
    }

    private void setFps() {
        long newTimer = (long) ((1 / fpsSlider.getValue()) * 1000000000);
        time.setFps(newTimer);
    }

    private void displayCellCount() {
        txtShowCellCount.setText(Integer.toString(board.getCellCount()) + " .");
    }

    private void displayGeneration() {
        txtShowGen.setText(Integer.toString(board.getGenerationCount()) + " ");
    }

    private void displayFps() {
        txtShowFps.setText(Integer.toString((int) fpsSlider.getValue()));
    }

    @FXML
    public void createNextGeneration() {
        board.nextGeneration();
        canvas.drawChanges(board);
        displayCellCount();
        displayGeneration();
    }

    @FXML
    private void quit() {
        Platform.exit();
    }

    @FXML
    private void showGameRulesWindow() throws Exception {
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
    private void toggleClickedCell(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && !event.isDragDetect()) {
            int row = (int) (event.getY() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            int col = (int) (event.getX() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            board.toggleCellState(row, col);
            canvas.drawCell(board, row, col);
        }

    }

    @FXML
    private void dragCanvas(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            scrollPane.setPannable(true);
        } else {
            if (!isPaused) {
                togglePlayPause();
            }
            int row = (int) (event.getY() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            int col = (int) (event.getX() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            board.settCellStateAlive(row, col);
            canvas.drawCell(board, row, col);
        }
    }

    @FXML
    private void dragCanvasEnded() {
        scrollPane.setPannable(false);
    }

    private void defineStage() {
        stage = (Stage) scrollPane.getScene().getWindow();
    }
}
