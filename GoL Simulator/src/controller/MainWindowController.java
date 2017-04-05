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
import javafx.scene.layout.AnchorPane;
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
    private AnchorPane canvasAnchor;
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
        Platform.runLater(this::prepareCanvas);

        board = new Board(200, 200);
        time = new Timer(this);
        isPaused = true;
        livingCellColorPicker.setValue(canvas.getLivingCellColor());
        deadCellColorPicker.setValue(canvas.getDeadCellColor());
        backgroundColorPicker.setValue(canvas.getBackgroundColor());
        fpsSlider.setValue(4.0);

        fpsSlider.valueProperty().addListener((observable) -> {
            setFps();
        });

        cellSizeSlider.valueProperty().addListener((observable) -> {
            canvas.setCellSize((int) cellSizeSlider.getValue());
            canvas.draw(board);
        });

        canvasAnchor.heightProperty().addListener((observable) -> {
            prepareCanvas();

        });

        canvasAnchor.widthProperty().addListener((observable) -> {
            prepareCanvas();
        });

        canvas.setCellSize((int) cellSizeSlider.getValue());
        setFps();
        displayLivingCellCount();
        displayGenerationCount();

    }

    private void prepareCanvas() {
        canvas.resizeCanvas(canvasAnchor.getHeight(), canvasAnchor.getWidth());
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
        displayLivingCellCount();
        displayGenerationCount();
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
                canvas.draw(board);
                displayLivingCellCount();

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
                canvas.draw(board);
                displayLivingCellCount();
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

    private void setFps() {
        long newTimer = (long) (1000000000 / fpsSlider.getValue());
        time.setFps(newTimer);
    }

    private void displayLivingCellCount() {
        txtShowCellCount.setText(Integer.toString(board.getLivingCellCount()) + " .");
    }

    private void displayGenerationCount() {
        txtShowGen.setText(Integer.toString(board.getGenerationCount()) + " ");
    }

    @FXML
    public void createNextGeneration() {
        board.nextGeneration();
        canvas.drawChanges(board);
        displayLivingCellCount();
        displayGenerationCount();
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
            int row = (int) ((event.getY() - canvas.getYOffsett()) / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            int col = (int) ((event.getX() - canvas.getXOffsett()) / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));

            if (row < board.getBoard().length && col < board.getBoard()[0].length) {
                board.toggleCellState(row, col);
                canvas.drawCell(board, row, col);
            }
        }
    }

    /**
     * Determines which mouse button is dragging, and calls the relevant method.
     *
     * @param event
     */
    @FXML
    private void dragCanvas(MouseEvent event
    ) {
        if (event.getButton() == MouseButton.SECONDARY) {
        } else {
            if (!isPaused) {
                togglePlayPause();
            }
            int row = (int) ((event.getY() - canvas.getYOffsett()) / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            int col = (int) ((event.getX() - canvas.getXOffsett()) / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));

            // ensure that the drag event was within the canvas.
            if (row < board.getBoard().length && col < board.getBoard()[0].length) {
                board.setCellStateAlive(row, col);
                canvas.drawCell(board, row, col);
            }
        }
    }

    @FXML
    private void dragCanvasEnded() {
        //anchorPane.setPannable(false);
        board.getLivingCellCount();
        displayLivingCellCount();
    }

    private void defineStage() {
        stage = (Stage) canvasAnchor.getScene().getWindow();
    }
}
