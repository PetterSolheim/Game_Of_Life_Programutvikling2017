package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Board;

/**
 * Resizable canvas object with the necessary methods for drawing, and changing
 * the apperance of the game board.
 *
 */
public class ResizableCanvas extends Canvas {

    private int cellSize = 5;
    private int spaceBetweenCells = 2; // the border.
    private double xOffset = 0;
    private double yOffset = 0;
    private Color backgroundColor; // also visible as the border.
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
     * Set the offset for the canvas drawing methods. Allows for moving around
     * on the canvas.
     *
     * @param xOffset x-axis offset.
     * @param yOffset y-axis offset.
     */
    public void setOffset(double xOffset, double yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    /**
     *
     * @return the x-axis offset of the canvas.
     */
    public double getXOffset() {
        return xOffset;
    }

    /**
     *
     * @return the y-axis offset of the canvas.
     */
    public double getYOffset() {
        return yOffset;
    }

    /**
     *
     * @param cellSize sets the pixels size of a cell.
     */
    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    /**
     *
     * @return the pixel size of the cell.
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Draws the entire board to canvas.
     *
     * @param b Board class containing a two dimensional byte array.
     */
    public void drawBoard(Board b) {
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        for (int row = 0; row < b.getBoard().length; row++) {
            for (int col = 0; col < b.getBoard()[0].length; col++) {
                drawCell(b, row, col);
            }
        }
    }

    /**
     * Takes a board object and draws only the cells which have changed during
     * the last nexGeneration() method call.
     *
     * @param b the Board object.
     */
    public void drawBoardChanges(Board b) {
        for (int row = 0; row < b.getBoardChanges().length; row++) {
            for (int col = 0; col < b.getBoardChanges()[0].length; col++) {
                // cells that have changed are symbolised by the number 1.
                if (b.getBoardChanges()[row][col] == 1) {
                    drawCell(b, row, col);
                }
            }
        }
    }

    /**
     * Draws the state of a single cell if, and only if, that cell is within the
     * visible area of the canvas.
     *
     * @param b the board object.
     * @param row the y position of the cell to be drawn.
     * @param col the x position of the cell to be drawn.
     */
    public void drawCell(Board b, int row, int col) {
        // calculate the position of the given cell. Use of Math.floor() to
        // avoid pixelbleed due to decimal values.
        double xPosition = Math.floor(xOffset + (col * (cellSize + spaceBetweenCells)));
        double yPosition = Math.floor(yOffset + (row * (cellSize + spaceBetweenCells)));

        // determin if the given cells position is within the size of the canvas.
        // If it is, drawBoard that cell. If not, do nothing.
        if (xPosition < this.getWidth() && yPosition < this.getHeight()) {
            if (b.getBoard()[row][col] == 1) {
                gc.setFill(livingCellColor);
            } else {
                gc.setFill(deadCellColor);
            }
            gc.fillRect(xPosition, yPosition, cellSize, cellSize);
        }
    }

    /**
     * Resizes the canvas object.
     *
     * @param height the new height for the canvas.
     * @param width the new width for the canvas.
     */
    public void resizeCanvas(double height, double width) {
        this.heightProperty().setValue(height);
        this.widthProperty().setValue(width);
    }

    /**
     *
     * @return the pixel size of the border.
     */
    public int getSpaceBetweenCells() {
        return spaceBetweenCells;
    }

    /**
     *
     * @return the color of living cells.
     */
    public Color getLivingCellColor() {
        return livingCellColor;
    }

    /**
     * Sets the color of living cells.
     *
     * @param livingCellColor
     */
    public void setLivingCellColor(Color livingCellColor) {
        this.livingCellColor = livingCellColor;
    }

    /**
     *
     * @return the color of dead cells.
     */
    public Color getDeadCellColor() {
        return deadCellColor;
    }

    /**
     * Sets the color of dead cells.
     *
     * @param deadCellColor
     */
    public void setDeadCellColor(Color deadCellColor) {
        this.deadCellColor = deadCellColor;
    }

    /**
     *
     * @return the background color of the board, which is also the color of the
     * border.
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the background color, which is also the color of the border.
     *
     * @param backgroundColor
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Allows the canvas object to be resized.
     *
     * @return
     */
    @Override
    public boolean isResizable() {
        return true;
    }
}
