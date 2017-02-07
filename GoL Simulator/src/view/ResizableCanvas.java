package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Board;
import model.TestBoard;

/**
 * Provides same functionality as a regular canvas, but allows for fluid resizing.
 * To draw on the canvas use the draw method and initialized GraphicsContext2D. 
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
     * Inherits from canvas constructor and initialize a local GraphicsContext2D.
     * @param width Determines width of canvas
     * @param height Determines height of canvas
     */
    public ResizableCanvas(double width, double height) {
        super(width, height);
        gc = this.getGraphicsContext2D();
        backGroundColor = Color.WHITE;
        deadCellColor = Color.ORANGE;
        livingCellColor = Color.RED;
        cellSize = 10;
        spaceBetweenCells = 5;
    }

    /**
     * Default constructor. Needed by FXML loader to initialize canvas properly. Don't use this.
     */
    public ResizableCanvas (){
        super(1000d,1000d);
        gc = this.getGraphicsContext2D();
        System.out.println("Default constructor called.");
        gc = this.getGraphicsContext2D();
        backGroundColor = Color.WHITE;
        deadCellColor = Color.ORANGE;
        livingCellColor = Color.RED;
        cellSize = 10;
        spaceBetweenCells = 5;
    }
    /**
     * Provides the ability to draw a grid of cells, both living and dead.
     * @param b Board class containing a two dimensional byte array.
     */
    public void draw(TestBoard b) {
        float xPos = -spaceBetweenCells - cellSize;
        float yPos = -spaceBetweenCells - cellSize;
        for (int row = 0; row < b.getBoard().length; row++) {
            xPos = -spaceBetweenCells - cellSize;
            gc.fillText(Integer.toString(row), xPos, yPos);
            yPos += spaceBetweenCells + cellSize;
            for (int col = 0; col < b.getBoard()[0].length; col++) {
                xPos += spaceBetweenCells + cellSize;
                if (b.getBoard()[row][col] == 1) {
                    gc.setFill(livingCellColor);
                    gc.fillRect(xPos, yPos, cellSize, cellSize);
                } else {
                    gc.setFill(deadCellColor);
                    gc.fillRect(xPos, yPos, cellSize, cellSize);
                }
            }
        }
    }

    public void testD (){
        gc.fillRect(5, 5, 10, 10);
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
