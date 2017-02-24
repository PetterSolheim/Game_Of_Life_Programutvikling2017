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
    private int spaceBetweenCells = 2;
    private Color backGroundColor;
    private Color livingCellColor;
    private Color deadCellColor;
    private GraphicsContext gc;


    public ResizableCanvas() {
        gc = this.getGraphicsContext2D();
        backGroundColor = Color.LIGHTGOLDENRODYELLOW;
        deadCellColor = Color.WHITE;
        livingCellColor = Color.BLACK;
    }

    /**
     * Provides the ability to draw a grid of cells, both living and dead.
     *
     * @param b Board class containing a two dimensional byte array.
     */
    public void draw(Board b) {
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

    public void calculateNewDimensions(Board b) {
        this.heightProperty().setValue((b.getHeight()) * (cellSize + spaceBetweenCells));
        this.widthProperty().setValue((b.getWidth()) * (cellSize + spaceBetweenCells));
    }

    public void clearCanvas() {
        gc.setFill(backGroundColor);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    /**
     *
     * @param b
     */
    public void redrawBoard(Board b) {
        clearCanvas();
        draw(b);
    }

    /**
     *
     * @param newCellSize set the width and height of all cells.
     */
    public void setCellSize(int newCellSize) {
        this.cellSize = newCellSize;
        //this.spaceBetweenCells = newCellSize / 2;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getSpaceBetweenCells() {
        return spaceBetweenCells;
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
     * @param newCellColor set/change the color of all living cells.
     */
    public void setLivingCellColor(Color newCellColor) {
        this.livingCellColor = newCellColor;
    }
    public Color getLivingCellColor() {
        return this.livingCellColor;
    }

    public Color getDeadCellColor() {
        return this.deadCellColor;
    }

    public Color getBackgroundColor() {
        return this.backGroundColor;
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
