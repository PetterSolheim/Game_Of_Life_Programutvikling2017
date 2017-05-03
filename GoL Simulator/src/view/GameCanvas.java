package view;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Resizable canvas object with the necessary methods for drawing based on
 * patterns passed as an <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>.
 */
public class GameCanvas extends Canvas {

    private int cellSize = 5;
    private int spaceBetweenCells = 2; // the border.
    private double xOffset = 0;
    private double yOffset = 0;

    private int firstVisibleRow;
    private int lastVisibleRow;
    private int firstVisibleCol;
    private int lastVisibleCol;

    private Color backgroundColor; // also visible as the border.
    private Color livingCellColor;
    private Color deadCellColor;
    private GraphicsContext gc;

    /**
     * GameCanvas no-argument constructor initializes game board colours and
     * graphics context.
     */
    public GameCanvas() {
        gc = this.getGraphicsContext2D();
        backgroundColor = Color.LIGHTGOLDENRODYELLOW;
        deadCellColor = Color.WHITE;
        livingCellColor = Color.BLACK;
    }

    /**
     * Sets the offset for the canvas draw methods.
     *
     * @param xOffset a <code>double</code> specifying the x-axis offset.
     * @param yOffset a <code>double</code> specifying the y-axis offset.
     */
    public void setOffset(double xOffset, double yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    /**
     * Adjust the offset by adding a value to increase the offset with.
     *
     * @param xOffset an <code>int</code> specifying the amount to increase the
     * xOffset.
     * @param yOffset an <code>int</code> specifying the amount to increase the
     * yOffset.
     */
    public void adjustOffset(int xOffset, int yOffset) {
        this.xOffset += xOffset;
        this.yOffset += yOffset;
    }

    /**
     * Gets the x-axis offset.
     *
     * @return a <code>double</code> specifying the x-axis offset.
     */
    public double getXOffset() {
        return xOffset;
    }

    /**
     * Gets the y-axis offset.
     *
     * @return a <code>double</code> specifying the y-axis offset.
     */
    public double getYOffset() {
        return yOffset;
    }

    /**
     * Sets the pixel size of a cell. If cell size is bellow 3 the border will
     * be removed (spaceBetweenCells gets set to 0), as the border colour
     * becomes to hard to differentiate from the dead cells colour at this size.
     *
     * @param cellSize an <code>int</code> specifying the new pixel size of a
     * cell.
     */
    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
        if (cellSize < 3) {
            // cellSize used is actually 1 higher than the value passed to the
            // method once value is low enough that the border is removed. This
            // to make zooming inn and out smoother when the border is removed. 
            spaceBetweenCells = 0;
            this.cellSize++;
        } else {
            spaceBetweenCells = 1;
        }
    }

    /**
     * Gets the pixel size of a cell.
     *
     * @return an <code>int</code> specifying the pixel size of a cell.
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Draws the game board based on an
     * <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code> where 1 symbolises a
     * living cell, and 0 symbolises a dead cell. Only visible parts of the game
     * board are actually drawn.
     *
     * @param board an <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>
     * specifying the state of the games cells.
     */
    public void drawBoard(ArrayList<ArrayList<Byte>> board) {
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        determineVisibleConstraints(board);

        for (int row = firstVisibleRow; row <= lastVisibleRow; row++) {
            for (int col = firstVisibleCol; col <= lastVisibleCol; col++) {
                drawCell(board, row, col);
            }
        }
    }

    /**
     * Draws board on canvas where board is passed as a <code>byte[][]</code>
     * array.
     *
     * @param board the board to be drawn to canvas.
     * @deprecated due to the fact that the Board class has been deprecated in
     * favor of the BoardDynamic class. Use BoardDynamic instead as the
     * GameCanvas draw methods for BoardDynamic are much more optimized.
     */
    @Deprecated
    public void drawBoard(byte[][] board) {
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                double xPosition = Math.floor(xOffset + (col * (cellSize + spaceBetweenCells)));
                double yPosition = Math.floor(yOffset + (row * (cellSize + spaceBetweenCells)));
                if (board[row][col] == 1) {
                    gc.setFill(livingCellColor);
                } else {
                    gc.setFill(deadCellColor);
                }
                gc.fillRect(xPosition, yPosition, cellSize, cellSize);
            }
        }
    }

    /**
     * Takes two <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>. One
     * representing the board in its current state, and the other representing
     * which of that boards cells you wish to draw. 1 represents cells you wish
     * drawn, 0 cells that are not to be drawn. This allows faster draw
     * operations, as one doesn't need to redraw cells that have not changed
     * their state.
     *
     * @param toDraw the cells you want drawn.
     * @param board the pattern containing the actual cells.
     */
    public void drawSpecificCells(ArrayList<ArrayList<Byte>> toDraw, ArrayList<ArrayList<Byte>> board) {
        determineVisibleConstraints(board);
        for (int row = firstVisibleRow; row <= lastVisibleRow; row++) {
            for (int col = firstVisibleCol; col <= lastVisibleCol; col++) {
                // cells that have changed are symbolised by the number 1.
                if (toDraw.get(row).get(col) == 1) {
                    drawCell(board, row, col);
                }
            }
        }
    }

    private void determineVisibleConstraints(ArrayList<ArrayList<Byte>> board) {
        // determine the given boards first visible row
        firstVisibleRow = (int) ((yOffset * -1) / (cellSize + spaceBetweenCells));
        if (firstVisibleRow < 0) {
            firstVisibleRow = 0;
        }

        // determine the given boards last visible row
        lastVisibleRow = (int) ((this.getHeight() - yOffset) / (cellSize + spaceBetweenCells) + 2);
        if (lastVisibleRow >= board.size()) {
            lastVisibleRow = board.size() - 1;
        } else if (lastVisibleRow < 0) {
            lastVisibleRow = 0;
        }

        // determine the given boards first visible column
        firstVisibleCol = (int) ((xOffset * -1) / (cellSize + spaceBetweenCells));
        if (firstVisibleCol < 0) {
            firstVisibleCol = 0;
        }

        // determine the given boards last visible column
        lastVisibleCol = (int) ((this.getWidth() - xOffset) / (cellSize + spaceBetweenCells) + 2);
        if (lastVisibleCol >= board.get(0).size()) {
            lastVisibleCol = board.get(0).size() - 1;
        } else if (lastVisibleCol < 0) {
            lastVisibleCol = 0;
        }
    }

    /**
     * Draws the state of a single cell if, and only if, that cell is actually
     * within the visible area of the game board.
     *
     * @param b <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code> representing
     * the game board.
     * @param row an <code>int</code> specifying the row location of the cell to
     * draw.
     * @param col an <code>int</code> specifying the column location of the cell
     * to draw.
     */
    public void drawCell(ArrayList<ArrayList<Byte>> b, int row, int col) {
        // calculate the position of the given cell. Use of Math.floor() to
        // avoid pixelbleed due to decimal values.
        double xPosition = Math.floor(xOffset + (col * (cellSize + spaceBetweenCells)));
        double yPosition = Math.floor(yOffset + (row * (cellSize + spaceBetweenCells)));

        if (b.get(row).get(col) == 1) {
            gc.setFill(livingCellColor);
        } else {
            gc.setFill(deadCellColor);
        }
        gc.fillRect(xPosition, yPosition, cellSize, cellSize);
    }

    /**
     * Sets new size for the canvas.
     *
     * @param height a <code>double</code> specifying the new height of the
     * canvas.
     * @param width a <code>double</code> specifying the new width of the
     * canvas.
     */
    public void setCanvasSize(double height, double width) {
        this.heightProperty().setValue(height);
        this.widthProperty().setValue(width);
    }

    /**
     * Gets the current pixel spacing between cells.
     *
     * @return an <code>int</code> specifying the space between pixels.
     */
    public int getSpaceBetweenCells() {
        return spaceBetweenCells;
    }

    /**
     * Gets the colour of living cells.
     *
     * @return a <code>Color</code> specifying the colour of living cells.
     */
    public Color getLivingCellColor() {
        return livingCellColor;
    }

    /**
     * Sets the colour of living cells.
     *
     * @param livingCellColor a <code>Color</code> specifying the new colour.
     */
    public void setLivingCellColor(Color livingCellColor) {
        this.livingCellColor = livingCellColor;
    }

    /**
     * Gets the colour of dead cells.
     *
     * @return a <code>Color</code> specifying the colour of dead cells.
     */
    public Color getDeadCellColor() {
        return deadCellColor;
    }

    /**
     * Sets the colour of dead cells.
     *
     * @param deadCellColor a <code>Color</code> specifying the new colour.
     */
    public void setDeadCellColor(Color deadCellColor) {
        this.deadCellColor = deadCellColor;
    }

    /**
     * Gets the background colour of the board, which also acts as the border
     * colour.
     *
     * @return a <code>Color</code> specifying the colour of the background and
     * border.
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the background colour, which which also acts as the border colour.
     *
     * @param backgroundColor a <code>Color</code> specifying the new colour.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Overrides the super classes (Canvas) isResizable state, changing it from
     * false, to true. This allows canvas objects based on this class to be
     * resizable.
     *
     * @return true, indicating that objects based on this class can be resized.
     */
    @Override
    public boolean isResizable() {
        return true;
    }
}
