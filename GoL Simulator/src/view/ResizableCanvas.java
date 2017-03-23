package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Board;

/**
 * Provides same functionality as a regular canvas, but allows for fluid
 * resizing.
 *
 * @author peven
 */
public class ResizableCanvas extends Canvas {

    private int cellSize = 1;
    private int spaceBetweenCells = 1;
    private Color backgroundColor;
    private Color livingCellColor;
    private Color deadCellColor;
    private GraphicsContext gc;

    public ResizableCanvas() {
        gc = this.getGraphicsContext2D();
        backgroundColor = Color.LIGHTGOLDENRODYELLOW;
        deadCellColor = Color.WHITE;
        livingCellColor = Color.BLACK;
    }

    /**
     * Draws the entire board to canvas.
     *
     * @param b Board class containing a two dimensional byte array.
     */
    public void draw(Board b) {
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());
        for (int row = 0; row < b.getBoard().length; row++) {
            for (int col = 0; col < b.getBoard()[0].length; col++) {
                drawCell(b, row, col);
            }
        }
    }

    /**
     * Resizes the canvas based on the size of the board, and draws the board on
     * the new canvas.
     *
     * @param b a board object.
     */
    public void redraw(Board b) {
        resizeCanvas(b);
        draw(b);
    }

    /**
     * Draws only the changes since the last generation to canvas.
     *
     * @param b a board object.
     */
    public void drawChanges(Board b) {
        for (int row = 0; row < b.getBoard().length; row++) {
            for (int col = 0; col < b.getBoard()[0].length; col++) {
                if (b.getBoardChanges()[row][col] != 0) {
                    drawCell(b, row, col);
                }
            }
        }
    }

    /**
     * Draws a single cell.
     *
     * @param b the board object.
     * @param row the y position of the cell to be drawn.
     * @param col the x position of the cell to be drawn.
     */
    public void drawCell(Board b, int row, int col) {
        if (b.getBoard()[row][col] == 1) {
            gc.setFill(livingCellColor);
        } else {
            gc.setFill(deadCellColor);
        }
        gc.fillRect((col * (cellSize + spaceBetweenCells)), (row * (cellSize + spaceBetweenCells)), cellSize, cellSize);
    }

    /**
     * Resizes the canvas object. New size is based on the required size to
     * accomadate the current board.
     *
     * @param b
     */
    public void resizeCanvas(Board b) {
        this.heightProperty().setValue((b.getBoard().length) * (cellSize + spaceBetweenCells));
        this.widthProperty().setValue((b.getBoard()[0].length) * (cellSize + spaceBetweenCells));
    }

    /**
     * Returns the pixel size of a cell.
     *
     * @return int value of pixel size.
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Return the pixel size of the border between cells.
     *
     * @return int value of pixel size.
     */
    public int getSpaceBetweenCells() {
        return spaceBetweenCells;
    }

    /**
     * Returns the color of living cells.
     *
     * @return color.
     */
    public Color getLivingCellColor() {
        return this.livingCellColor;
    }

    /**
     * Sets the color of a living cell.
     *
     * @param newCellColor the new color.
     */
    public void setLivingCellColor(Color newCellColor) {
        this.livingCellColor = newCellColor;
    }

    /**
     * Returns the color of a dead cell.
     *
     * @return color.
     */
    public Color getDeadCellColor() {
        return this.deadCellColor;
    }

    /**
     * Sets the color of a dead cell.
     *
     * @param newCellColor the new color.
     */
    public void setDeadCellColor(Color newCellColor) {
        this.deadCellColor = newCellColor;
    }

    /**
     * Returns the color of the background (visible as a border).
     *
     * @return color.
     */
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    /**
     * Sets the background color for the canvas. The background color is visible
     * as the border between cells.
     *
     * @param newBackgroundColor the new color.
     */
    public void setBackgroundColor(Color newBackgroundColor) {
        this.backgroundColor = newBackgroundColor;
    }

    public void adjustScale(double x, double y) {
        setScaleX(getScaleX() + x);
        setScaleY(getScaleY() + y);
    }

    /**
     * Allows the board to be resizable.
     *
     * @return
     */
    @Override
    public boolean isResizable() {
        return true;
    }
}
