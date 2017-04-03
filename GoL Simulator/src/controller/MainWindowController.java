/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;
import view.ResizableCanvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import view.DialogBoxes;

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
        Platform.runLater(this::defineStage); // allows easy referal to the stage

        board = new Board(200, 200);
        time = new Timer(this);
        isPaused = true;
        livingCellColorPicker.setValue(canvas.getLivingCellColor());
        deadCellColorPicker.setValue(canvas.getDeadCellColor());
        backgroundColorPicker.setValue(canvas.getBackgroundColor());
        setFps();

        fpsSlider.valueProperty().addListener((observable) -> {
            setFps();
        });

        canvas.resizeCanvas(board);
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
        FileImporter fileImporter = new FileImporter();
        FileChooser fileChooser = createFileChooser();
        File file = fileChooser.showOpenDialog(stage);

        Board tmpBoard = new Board();
        if (file != null && file.exists()) {
            try {
                tmpBoard = fileImporter.readGameBoardFromDisk(file);
                board = tmpBoard;
                canvas.resizeCanvas(board);
                canvas.draw(board);
            } catch (FileNotFoundException e) {
                DialogBoxes.ioException("No file found at: " + e.getMessage());
            } catch (IOException e) {
                DialogBoxes.ioException("There was a problem reading the file: " + e.getMessage());
            } catch (PatternFormatException e) {
                DialogBoxes.patternFormatException("There was an error parsing the file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void addFromDisk() {

    }

    @FXML
    private void addFromUrl() {

    }

    @FXML
    private void showAboutWindow() {

    }

    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Formats", "*.rle"/*, "*.lif", "*.life", "*.cells"*/),
                new FileChooser.ExtensionFilter("RLE", "*.rle"));
        //new FileChooser.ExtensionFilter("Life 1.05/1.06", "*.lif", "*.life"),
        //new FileChooser.ExtensionFilter("Plaintext", "*.cells")),
        return fileChooser;
    }

    @FXML
    private void openFromUrl() {
        FileImporter fileImporter = new FileImporter();
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Open URL");
        inputDialog.setContentText("Please enter URL for the file you wish to open");

        Optional<String> url = inputDialog.showAndWait();
        if (url.isPresent()) {
            try {
                board = fileImporter.readGameBoardFromUrl(url.get());
                canvas.resizeCanvas(board);
                canvas.draw(board);
            } catch (MalformedURLException e) {
                DialogBoxes.ioException("Given String is not a valid URL: " + e.getMessage());
            } catch (FileNotFoundException e) {
                DialogBoxes.ioException("File not found at: " + e.getMessage());
            } catch (IOException e) {
                DialogBoxes.ioException("File not found at: " + e.getMessage());
            } catch (PatternFormatException e) {
                DialogBoxes.patternFormatException("There was an error parsing the file: " + e.getMessage());
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

    @FXML
    private void lowerScale() {
        if (canvas.getScaleX() >= 0.45) {
            if (canvas.getScaleX() < 0.5) {
                canvas.adjustScale(-0.08, -0.08);
            } else if (canvas.getScaleX() < 0.6) {
                canvas.adjustScale(-0.13, -0.13);
            } else {
                canvas.adjustScale(-0.2, -0.2);
            }
        }
    }

    @FXML
    private void higherScale() {
        if (canvas.getScaleX() < 0.4) {
            canvas.adjustScale(0.05, 0.05);
        } else if (canvas.getScaleX() < 0.5) {
            canvas.adjustScale(0.09, 0.09);
        } else {
            canvas.adjustScale(0.12, 0.12);
        }
    }

    private void setFps() {
        long newTimer = (long) ((1 / fpsSlider.getValue()) * 1000000000);
        time.setFps(newTimer);
    }

    private void displayCellCount() {
        txtShowCellCount.setText(Integer.toString(board.getLivingCells()) + " .");
    }

    private void displayGeneration() {
        txtShowGen.setText(Integer.toString(board.getGenerationCount()) + " ");
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
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/GameRulesWindow.fxml"));
        loader.load();
        Parent parent = loader.getRoot();
        Scene Scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(Scene);

        GameRulesWindowController controller = loader.getController();
        controller.initData(board);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void showStatistics() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StatisticsWindow.fxml"));
        FlowPane root = loader.load();

        StatisticsWindowController controller = loader.getController();
        controller.setBoard(board.deepCopy());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Statistics");
        stage.show();
    }

    /**
     * Toggles the state of a clicked cell. A live cell becomes dead, and a dead
     * cell becomes alive.
     *
     * @param event
     */
    @FXML
    private void toggleClickedCell(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && !event.isDragDetect()) {
            int row = (int) (event.getY() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            int col = (int) (event.getX() / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            board.toggleCellState(row, col);
            canvas.drawCell(board, row, col);
        }
    }

    /**
     * Determines which mouse button is dragging, and calls the relevant method.
     *
     * @param event
     */
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

            // ensure that the drag event was within the canvas.
            if (row < board.getBoard().length && col < board.getBoard()[0].length) {
                board.setCellStateAlive(row, col);
                canvas.drawCell(board, row, col);
            }
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
