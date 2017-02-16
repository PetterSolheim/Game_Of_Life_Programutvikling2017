package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Board;

/**
 * Provides same functionality as a regular canvas, but allows for fluid
 * resizing. To draw on the canvas use the draw method and initialized
 * GraphicsContext2D.
 *
 * @author peven
 */
public class ResizableCanvas extends Canvas {

    private int cellSize;
    private Color backGroundColor;
    private Color livingCellColor;
    private Color deadCellColor;
    private GraphicsContext gc;
    private int spaceBetweenCells;

    /**
     * Default constructor. Needed by FXML loader to initialize canvas properly.
     * Don't use this.
     */
    public ResizableCanvas() {
        super(800d, 800d);
        gc = this.getGraphicsContext2D();
        gc = this.getGraphicsContext2D();
        backGroundColor = Color.GREY;
        deadCellColor = Color.WHITE;
        livingCellColor = Color.BLACK;
        cellSize = 10;
        spaceBetweenCells = 2;
    }

    /**
     * Provides the ability to draw a grid of cells, both living and dead.
     *
     * @param b Board class containing a two dimensional byte array.
     */
    public void draw(Board b) {
        this.heightProperty().setValue((b.getHeight() + 2) * (cellSize + spaceBetweenCells));
        this.widthProperty().setValue((b.getWidth() + 2) * (cellSize + spaceBetweenCells));

        for (int row = 0; row < b.getBoard().length; row++) {
            for (int col = 0; col < b.getBoard()[0].length; col++) {
                if (b.getBoard()[row][col] == 1) {
                    gc.setFill(livingCellColor);
                } else {
                    gc.setFill(deadCellColor);
                }
                gc.fillRect((col * (cellSize + spaceBetweenCells)), (row * (cellSize + spaceBetweenCells)), cellSize, cellSize);
            }
        }

    }

    public int cellClickedRow(double x) {
        return (int) (x / (cellSize + spaceBetweenCells));
    }

    public int cellClickedCol(double y) {
        return (int) (y / (cellSize + spaceBetweenCells));
    }

    /**
     *
     * @param newCellSize sets the width and height of all cells.
     */
    public void setCellSize(int newCellSize) {
        this.cellSize = newCellSize;
    }

    /**
     *
     * @param newBackgroundColor set/change background color behind all cells.
     */
    public void setBackgroundColor(Color newBackgroundColor) {
        this.backGroundColor = newBackgroundColor;
    }

    /**
     *
     * @param newCellColor sets/change the color of all living cells.
     */
    public void setLivingCellColor(Color newCellColor) {
        this.setLivingCellColor(newCellColor);
    }

    /**
     *
     * @param newCellColor set/change color of all dead cells
     */
    public void setDeadCellColor(Color newCellColor) {
        this.deadCellColor = newCellColor;
    }

    @Override
    public boolean isResizable() {
        return true;
    }
}
