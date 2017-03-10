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

    private final int cellSize = 4;
    private final int spaceBetweenCells = 1;
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
     * Provides the ability to draw a grid of cells, both living and dead.
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

    public void drawChanges(Board b) {
        for (int row = 0; row < b.getBoard().length; row++) {
            for (int col = 0; col < b.getBoard()[0].length; col++) {
                if (b.getBoardChanges()[row][col] != 0) {
                    drawCell(b, row, col);
                }
            }
        }
    }

    public void drawCell(Board b, int row, int col) {
        if (b.getBoard()[row][col] == 1) {
            gc.setFill(livingCellColor);
        } else {
            gc.setFill(deadCellColor);
        }
        gc.fillRect((col * (cellSize + spaceBetweenCells)), (row * (cellSize + spaceBetweenCells)), cellSize, cellSize);
    }

    public void calculateCanvasSize(Board b) {
        this.heightProperty().setValue((b.getRowCount()) * (cellSize + spaceBetweenCells));
        this.widthProperty().setValue((b.getColumnCount()) * (cellSize + spaceBetweenCells));
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
        this.backgroundColor = newBackgroundColor;
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
        return this.backgroundColor;
    }

    /**
     *
     * @param newCellColor set/change color of all dead cells
     */
    public void setDeadCellColor(Color newCellColor) {
        this.deadCellColor = newCellColor;
    }

    /** 
     * Allows the board to be resizable.
     * @return 
     */
    @Override
    public boolean isResizable() {
        return true;
    }
}
