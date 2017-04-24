package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;
import view.GameCanvas;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import view.DialogBoxes;

/**
 * FXML Controller class for the main window.
 *
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
    private BoardDynamic board;
    private Timer time;
    private boolean isPaused = true;
    private Stage stage;
    private Stage mainStage;
    private double previousXOffset;
    private double previousYOffset;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(this::defineStage); // allows easy referal to the stage.
        Platform.runLater(this::resizeCanvas); // ensures the parent node is ready before resizing the canvas.
        Platform.runLater(this::setArrowKeyEventListener); // Eventhandler for arrowkeys after stage is loaded
        board = new BoardDynamic(20, 20);
        time = new Timer(this); // used for animation timing.

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
            int oldValue = canvas.getCellSize() + canvas.getSpaceBetweenCells();
            canvas.setCellSize((int) cellSizeSlider.getValue());
            int newValue = canvas.getCellSize() + canvas.getSpaceBetweenCells();
            int yOffsetAdjust = ((oldValue - newValue) * board.getRows()) / 2;
            int xOffsetAdjust = ((oldValue - newValue) * board.getCols()) / 2;
            canvas.adjustOffset(xOffsetAdjust, yOffsetAdjust);

            canvas.drawBoard(board.getBoard());

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
        Platform.runLater(this::centreBoardOnCanvas);
    }

    /**
     * Resizes the canvas so that it fills the available space in the canvases
     * parent node.
     */
    private void resizeCanvas() {
        canvas.setCanvasSize(canvasAnchor.getHeight(), canvasAnchor.getWidth());
        canvas.drawBoard(board.getBoard());
    }

    /**
     * Creates a new blank board of a user defined size. User is presented with
     * a dialog box asking for board size.
     */
    @FXML
    private void newBoard() {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("New Board");
        GridPane grid = new GridPane();

        Label lblRow = new Label("Rows: ");
        grid.add(lblRow, 0, 0);
        TextField txtRow = new TextField();
        txtRow.setPromptText("Enter number of rows");
        grid.add(txtRow, 1, 0);

        Label lblCol = new Label("Columns: ");
        grid.add(lblCol, 0, 1);
        TextField txtCol = new TextField();
        txtCol.setPromptText("Enter number of columns");
        grid.add(txtCol, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                ArrayList<String> values = new ArrayList<>();
                values.add(txtRow.getText());
                values.add(txtCol.getText());
                return values;
            } else {
                return null;
            }
        });

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        Optional<ArrayList<String>> result = dialog.showAndWait();

        result.ifPresent(consumer -> {
            try {
                int row = Integer.parseInt(consumer.get(0));
                int col = Integer.parseInt(consumer.get(1));
                BoardDynamic newBoard = new BoardDynamic(row, col);
                board = newBoard;
                centreBoardOnCanvas();
                canvas.drawBoard(board.getBoard());
            } catch (IllegalArgumentException e) {
                DialogBoxes.ioException("Entered value is not a number!");
            }
        });
    }

    /**
     * Sets the board back to its original state, and reset counters.
     */
    @FXML
    private void reset() {
        pause();
        board.resetBoard();
        canvas.drawBoard(board.getBoard());
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

    private void setArrowKeyEventListener() {
        stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                KeyCode k = ke.getCode();
                if (k == KeyCode.LEFT || k == KeyCode.RIGHT || k == KeyCode.DOWN || k == KeyCode.UP) {

                    ke.consume(); // <-- stops passing the event to next node
                }
                switch (k) {
                    case LEFT:
                        board.moveBoardWithArrowKeys(-1, 0);
                        break;
                    case RIGHT:
                        board.moveBoardWithArrowKeys(1, 0);
                        break;
                    case UP:
                        board.moveBoardWithArrowKeys(0, -1);
                        break;
                    case DOWN:
                        board.moveBoardWithArrowKeys(0, 1);
                        break;
                    default:
                        break;
                }
                canvas.drawBoard(board.getBoard());
            }
        });
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
        double boardWidthCenter = (board.getBoard().get(0).size() * (canvas.getCellSize() + canvas.getSpaceBetweenCells()) / 2);
        double boardHeightCenter = (board.getBoard().size() * (canvas.getCellSize() + canvas.getSpaceBetweenCells()) / 2);
        double canvasWidthCenter = (canvas.getWidth() / 2);
        double canvasHeightCenter = (canvas.getHeight() / 2);

        double xOffset = canvasWidthCenter - boardWidthCenter;
        double yOffset = canvasHeightCenter - boardHeightCenter;

        canvas.setOffset(xOffset, yOffset);
        canvas.drawBoard(board.getBoard());
    }

    /**
     * Creates a file chooser window for use with methods that open files from
     * disk.
     *
     * @return a <code>FileChooser</code> object.
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
                canvas.drawBoard(board.getBoard());
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
                centreBoardOnCanvas();
                canvas.drawBoard(board.getBoard());
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
     * Changes the colour of living cells based on the current value of the
     * livingCellColorPicker.
     */
    @FXML
    private void changeLivingCellColor() {
        canvas.setLivingCellColor(livingCellColorPicker.getValue());
        canvas.drawBoard(board.getBoard());
    }

    /**
     * Changes the colour of the background (and the border as they share
     * colour) based on the current value of the backgroundColorPicker.
     */
    @FXML
    private void changeBackgroundColor() {
        canvas.setBackgroundColor(backgroundColorPicker.getValue());
        canvas.drawBoard(board.getBoard());
    }

    /**
     * Changes the colour of dead cells based on the current value of the
     * deadCellColorPicker.
     */
    @FXML
    private void changeDeadCellColor() {
        canvas.setDeadCellColor(deadCellColorPicker.getValue());
        canvas.drawBoard(board.getBoard());
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
        txtShowCellCount.setText(Integer.toString(board.getLivingCellCount()) + "");
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

        // only draw cells that changed during last generational shift.
        for (int row = 0; row < board.getChangedCells().size(); row++) {
            for (int col = 0; col < board.getChangedCells().get(0).size(); col++) {
                // cells that have changed are symbolised by the number 1.
                if (board.getChangedCells().get(row).get(col) == 1) {
                    canvas.drawCell(board.getBoard(), row, col);
                }
            }
        }
        updateLivingCellCountLabel();
        updateGenerationCountLabel();
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
        Stage statistics = new Stage();

        statistics.setWidth(800);

        statistics.setHeight(800);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StatisticsWindow.fxml"));

        FlowPane root = loader.load();

        StatisticsWindowController controller = loader.getController();

        controller.setBoard(board.deepCopy());

        Scene scene = new Scene(root);

        statistics.setScene(scene);

        statistics.setTitle("Statistics");

        statistics.show();
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
                canvas.drawCell(board.getBoard(), row, col);
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
     * Moves the visible area of the board in the canvas based on the direction
     * of the mouse movement.
     *
     * @param event the mouse event that triggered the drag.
     */
    private void moveCanvas(MouseEvent event) {
        double newXOffset = canvas.getXOffset() + (event.getX() - previousXOffset);
        double newYOffset = canvas.getYOffset() + (event.getY() - previousYOffset);
        canvas.setOffset(newXOffset, newYOffset);
        canvas.drawBoard(board.getBoard());
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
            canvas.drawCell(board.getBoard(), row, col);
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
        if (row < board.getBoard().size() && row >= 0 && col < board.getBoard().get(0).size() && col >= 0) {
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
     * Shuts down the application.
     */
    @FXML
    private void quit() {
        Platform.exit();
    }

    public void setMainStage(Stage s) {
        this.mainStage = s;
    }

    /**
     * Creates a stage variable which allows for easy referral to the Stage of
     * this controllers window.
     */
    private void defineStage() {
        stage = (Stage) canvasAnchor.getScene().getWindow();
    }
}
