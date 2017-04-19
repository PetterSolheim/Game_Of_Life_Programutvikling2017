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
import javafx.scene.Cursor;
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
import view.GameCanvas;
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
    private GameCanvas canvas;
    @FXML
    private Button btnPlay;
    @FXML
    private Slider cellSizeSlider;
    @FXML
    private Slider fpsSlider;
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
    private boolean isPaused = true;
    private Stage stage;
    private double previousXOffset;
    private double previousYOffset;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(this::defineStage); // allows easy referal to the stage.
        Platform.runLater(this::resizeCanvas); // ensures the parent node is ready before resizing the canvas.

        board = new Board(10, 10);
        time = new Timer(this);

        // set the default value of the color pickers.
        livingCellColorPicker.setValue(canvas.getLivingCellColor());
        deadCellColorPicker.setValue(canvas.getDeadCellColor());
        backgroundColorPicker.setValue(canvas.getBackgroundColor());

        // prepare the FPS slider and add a listener.
        fpsSlider.setValue(4.0);
        setFps();
        fpsSlider.valueProperty().addListener((observable) -> {
            setFps();
        });

        // prepare the cellSizeSlider.
        cellSizeSlider.valueProperty().addListener((observable) -> {
            canvas.setCellSize((int) cellSizeSlider.getValue());
            canvas.drawBoard(board);
        });

        canvas.setCellSize((int) cellSizeSlider.getValue());

        // prepare the canvas, and add a listener to its parent node so that
        // the canvas will resize to fill the available space.
        canvasAnchor.heightProperty().addListener((observable) -> {
            resizeCanvas();

        });
        canvasAnchor.widthProperty().addListener((observable) -> {
            resizeCanvas();
        });

        // update the labels for the living cell count and generation count.
        updateLivingCellCountLabel();
        updateGenerationCountLabel();

    }

    /**
     * Resizes the canvas so that it fills the available space in the canvases
     * parent node.
     */
    private void resizeCanvas() {
        canvas.resizeCanvas(canvasAnchor.getHeight(), canvasAnchor.getWidth());
        canvas.drawBoard(board);
    }

    @FXML
    private void newBoard() {

    }

    /**
     * Sets the board back to its original state, and reset counters.
     */
    @FXML
    private void reset() {
        pause();
        board.resetBoard();
        canvas.drawBoard(board);
        updateLivingCellCountLabel();
        updateGenerationCountLabel();
    }

    /**
     * Toggles the play/pause state of the animation.
     */
    @FXML
    private void togglePlayPause() {
        if (isPaused) {
            play();
        } else {
            pause();
        }
    }

    /**
     * Starts the animation. The play buttons text gets changed to "Pause" and
     * the icon gets switched to a pause icon.
     */
    private void play() {
        Image imgPause = new Image("/img/pause.png");
        isPaused = false;
        imgPlayPause.setImage(imgPause);
        btnPlay.setText("Pause");
        time.start();
    }

    /**
     * Pauses the animation. THe pause button text gets changed to "Play" and
     * the icon gets switched to a play icon.
     */
    private void pause() {
        Image imgPlay = new Image("/img/play.png");
        isPaused = true;
        imgPlayPause.setImage(imgPlay);
        btnPlay.setText("Play");
        time.stop();
    }

    /**
     * Centres the board on the canvas.
     */
    @FXML
    private void centreBoardOnCanvas() {
        double boardWidthCenter = (board.getBoard()[0].length * (canvas.getCellSize() + canvas.getSpaceBetweenCells()) / 2);
        double boardHeightCenter = (board.getBoard().length * (canvas.getCellSize() + canvas.getSpaceBetweenCells()) / 2);
        double canvasWidthCenter = (canvas.getWidth() / 2);
        double canvasHeightCenter = (canvas.getHeight() / 2);

        double xOffset = canvasWidthCenter - boardWidthCenter;
        double yOffset = canvasHeightCenter - boardHeightCenter;

        canvas.setOffset(xOffset, yOffset);
        canvas.drawBoard(board);
    }

    /**
     * Creates a file chooser for use with methods that open files from disk.
     *
     * @return a FileChooser object.
     */
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

    /**
     * Displays a FileChooser, and lets the user select a compatible pattern
     * file which is then parsed and drawn to the canvas, replacing any existing
     * board.
     */
    @FXML
    private void openFromDisk() {
        FileImporter fileImporter = new FileImporter();
        FileChooser fileChooser = createFileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null && file.exists()) {
            try {
                board.setBoard(fileImporter.readGameBoardFromDisk(file));
                centreBoardOnCanvas();
                canvas.drawBoard(board);
                updateLivingCellCountLabel();

            } catch (FileNotFoundException e) {
                DialogBoxes.ioException("No file found at: " + e.getMessage());
            } catch (IOException e) {
                DialogBoxes.ioException("There was a problem reading the file: " + e.getMessage());
            } catch (PatternFormatException e) {
                DialogBoxes.patternFormatException("There was an error parsing the file: " + e.getMessage());
            }
        }
    }

    /**
     * Displays a dialogue box requesting an URL. URL is used to download a
     * compatible GoL pattern file. Pattern is then parsed and drawn to board,
     * replacing any existing board.
     */
    @FXML
    private void openFromUrl() {
        FileImporter fileImporter = new FileImporter();
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Open URL");
        inputDialog.setContentText("Please enter URL for the file you wish to open");

        Optional<String> url = inputDialog.showAndWait();
        if (url.isPresent()) {
            try {
                board.setBoard(fileImporter.readGameBoardFromUrl(url.get()));
                canvas.drawBoard(board);
                updateLivingCellCountLabel();
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

    /**
     * Changes the color of living cells based on the current value of the
     * livingCellColorPicker.
     */
    @FXML
    private void changeLivingCellColor() {
        canvas.setLivingCellColor(livingCellColorPicker.getValue());
        canvas.drawBoard(board);
    }

    /**
     * Changes the color of the background (and the border as they share color)
     * based on the current value of the backgroundColorPicker.
     */
    @FXML
    private void changeBackgroundColor() {
        canvas.setBackgroundColor(backgroundColorPicker.getValue());
        canvas.drawBoard(board);
    }

    /**
     * Changes the color of dead cells based on the current value of the
     * deadCellColorPicker.
     */
    @FXML
    private void changeDeadCellColor() {
        canvas.setDeadCellColor(deadCellColorPicker.getValue());
        canvas.drawBoard(board);
    }

    /**
     * TODO: change method to accept value, rather than collect value.
     */
    private void setFps() {
        long newTimer = (long) (1000000000 / fpsSlider.getValue());
        time.setFps(newTimer);
    }

    /**
     * Updates the label which displays the number of living cells on the
     * current board.
     */
    private void updateLivingCellCountLabel() {
        txtShowCellCount.setText(Integer.toString(board.getLivingCellCount()) + " .");
    }

    /**
     * Updates the label which displays the current generation count.
     */
    private void updateGenerationCountLabel() {
        txtShowGen.setText(Integer.toString(board.getGenerationCount()) + " ");
    }

    /**
     * Iterates the board to the next generation, draws the new board on canvas,
     * and updates the GUIs labels for living cell count, and generation count.
     */
    @FXML
    public void createNextGeneration() {
        board.nextGeneration();
        canvas.drawBoardChanges(board);
        updateLivingCellCountLabel();
        updateGenerationCountLabel();
    }

    /**
     * Shuts down the application.
     */
    @FXML
    private void quit() {
        Platform.exit();
    }

    /**
     * Displays the game rules window where the user can alter the rules which
     * the game plays by.
     * 
     * Game rules window has Modality.APPLICATION_MODAL to prevent changes to
     * the Board object while game rules window is open.
     */
    @FXML
    private void showGameRulesWindow() throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/GameRulesWindow.fxml"));
            loader.load();
            Parent parent = loader.getRoot();
            Scene Scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(Scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            DialogBoxes.ioException("There was an error displaying the game rules window!");
        }
    }

    public void showStatistics() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StatisticsWindow.fxml"));
        FlowPane root = loader.load();

        StatisticsWindowController controller = loader.getController();
        Board copy = board.deepCopy();
        controller.setBoard(copy);
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
     * @param event the mouse event which triggered the method.
     */
    @FXML
    private void canvasClickEvent(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && !event.isDragDetect()) { // single click registered
            int row = (int) ((event.getY() - canvas.getYOffset()) / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
            int col = (int) ((event.getX() - canvas.getXOffset()) / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));

            if (isWithinBoard(row, col)) {
                board.toggleCellState(row, col);
                canvas.drawCell(board, row, col);
                board.getLivingCellCount();
                updateLivingCellCountLabel();
            }
        } else {
            prepareForCanvasMovement(event); // sets initial values needed to calculate offset while draging.
        }
    }

    /**
     * Determines which mouse button is being dragged on the canvas, and calls
     * the appropriate method. Left click drag results in drawing on the canvas
     * while right click drag results in moving the canvas.
     *
     * @param event the mouse event which triggered the method.
     */
    @FXML
    private void canvasDragEvent(MouseEvent event) {
        // movement of canvas
        if (event.getButton() == MouseButton.SECONDARY) {
            moveCanvas(event);
        } else {
            dragDraw(event);
        }
    }

    /**
     * Define starting values for moving the canvas offset through mouse
     * dragging.
     *
     * @param event the mouse event that triggered the drag.
     */
    private void prepareForCanvasMovement(MouseEvent event) {
        canvas.getScene().setCursor(Cursor.MOVE); 
        previousXOffset = event.getX();
        previousYOffset = event.getY();
    }

    /**
     * Moves the visible area of the board in the canvas 
     * based on the direction of the mouse movement. 
     *
     * @param event the mouse event that triggered the drag.
     */
    private void moveCanvas(MouseEvent event) {
        double newXOffset = canvas.getXOffset() + (event.getX() - previousXOffset);
        double newYOffset = canvas.getYOffset() + (event.getY() - previousYOffset);
        canvas.setOffset(newXOffset, newYOffset);
        canvas.drawBoard(board);
        previousXOffset = event.getX();
        previousYOffset = event.getY();
    }

    /**
     * Allows for dragged drawing on the canvas.
     *
     * @param event the mouse event which triggered the event.
     */
    private void dragDraw(MouseEvent event) {
        if (!isPaused) {
            togglePlayPause();
        }
        // calculate which cell is being clicked
        int row = (int) ((event.getY() - canvas.getYOffset()) / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));
        int col = (int) ((event.getX() - canvas.getXOffset()) / (canvas.getCellSize() + canvas.getSpaceBetweenCells()));

        // ensure that the drag event was within the actual board.
        if (isWithinBoard(row, col)) {
            board.setCellStateAlive(row, col);
            canvas.drawCell(board, row, col);
            board.getLivingCellCount();
            updateLivingCellCountLabel();
        }
    }

    /**
     * Determine if a cell exists on the current board. Use this method to avoid
     * ArrayIndexOutOfBounds exceptions.
     *
     * @param row y-axis location of desired cell.
     * @param col x-axis location of desired cell.
     * @return true or false depending on if the given values are within the
     * current board.
     */
    private boolean isWithinBoard(int row, int col) {
        if (row < board.getBoard().length && row >= 0 && col < board.getBoard()[0].length && col >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the cursor to default state at the end of a canvas drag.
     */
    @FXML
    private void canvasDragEnded() {
        canvas.getScene().setCursor(Cursor.DEFAULT);

    }

    /**
     * Creates a stage variable which allows for easy referral to the Stage of
     * this controllers window.
     */
    private void defineStage() {
        stage = (Stage) canvasAnchor.getScene().getWindow();
    }
}
