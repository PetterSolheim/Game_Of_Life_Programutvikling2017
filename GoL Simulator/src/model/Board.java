package model;

//import controller.TimeKeeper;

/**
 * Class for the game board. Contains the actual board and the methods for
 * iterating to the next generation, along with game rules for the board. The
 * class is a Singleton.
 *
 */
public class Board {

    private static Board instance;
    private byte[][] currentBoard;
    private byte[][] originalBoard;
    private int generationCount = 1;
    private int cellCount;
    private int minToSurvive = 2;
    private int maxToSurvive = 3;
    private int birth = 3;

    /**
     * As this is a singleton, the constructor is private.
     */
    private Board() {
        currentBoard = new byte[][] {
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 1, 1, 0, 0},
        {0, 0, 1, 1, 1, 0, 0},
        {0, 0, 1, 1, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 1, 1, 0, 0},
        {0, 0, 1, 1, 1, 0, 0},
        {0, 0, 1, 1, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0}
    };
    }

    /**
     * Method for acquiring a reference to the Board object.
     *
     * @return A reference to the Board object.
     */
    public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }

    /**
     * Gets the current Board.
     *
     * @return The game board.
     */
    public byte[][] getBoard() {
        return currentBoard;
    }

    /**
     * Sets a new board.
     *
     * @param newBoard The new board to use in the game.
     */
    public void setBoard(byte[][] newBoard) {
        originalBoard = duplicateBoard(newBoard);
        currentBoard = duplicateBoard(newBoard);
    }

    /**
     * Allows new rules to be set. Default is 23/3.
     *
     * @param min The minimum number of live neighbours for current cell to
     * survive.
     * @param max The maximum number of live neighbours for the current cell to
     * survive.
     * @param birth The number of live neighbours a dead cell must have for it
     * to become a live cell.
     */
    public void setRules(int min, int max, int birth) {
        if (min >= 0 && min <= 8 && max >= 0 && max <= 8 && birth >= 0 && birth <= 8) {
            minToSurvive = min;
            maxToSurvive = max;
            this.birth = birth;
        }
        else {
            System.out.println("Error registered");
            throw new IllegalArgumentException("Values entered must be no lower"
                    + " than 0, and no higher than 8!");
            
        }
    }

    /**
     * Iterates the board to the next generation.
     */
    public void nextGeneration() {
        // a copy of the board is used to test the rules, while changes are
        // applied to the actual board.
        byte[][] testPattern = duplicateBoard(currentBoard);

        setCellCount(0); // reset the cell count

        // iterates through the board cells, count number of neighbours for each
        // cell, and apply changes based on the ruleset.
        for (int i = 0; i < testPattern.length; i++) {
            for (int j = 0; j < testPattern[0].length; j++) {
                int neighbours = countNeighbours(testPattern, i, j);

                if (neighbours < minToSurvive || neighbours > maxToSurvive) {
                    currentBoard[i][j] = 0;
                }

                if (neighbours == birth) {
                    currentBoard[i][j] = 1;
                }
            }
        }
        increaseGenerationCount();

    }

    /**
     * Counts the number of neighbours for a given cell. Method is private, and
     * used by the nextGeneration() method. Try-catch statements used to handle
     * out of bounds exceptions.
     *
     * @param cell The game board.
     * @param x The x axis of the cell.
     * @param y The y axis of the cell.
     * @return The number of neighbours.
     */
    private int countNeighbours(byte[][] cell, int x, int y) {
        int neighbours = 0;

        try {
            if (cell[x][y + 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x - 1][y + 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x - 1][y] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x - 1][y - 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x][y - 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x + 1][y - 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x + 1][y] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x + 1][y + 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        return neighbours;
    }

    /**
     *
     * @return The lowest number of live neighbours needed for a cell to
     * survive.
     */
    public int getMinToSurvive() {
        return minToSurvive;
    }

    /**
     *
     * @return The highest number of live neighbours before a cell dies.
     */
    public int getMaxToSurvive() {
        return maxToSurvive;
    }

    /**
     *
     * @return The number of live neighbours a dead cell needs to become alive.
     */
    public int getBirth() {
        return birth;
    }

    /**
     * Private method used by nextGeneration to keep count of the number of live
     * cells.
     *
     * @param newValue Set the current cell count
     */
    private void setCellCount(int newValue) {
        if (newValue >= 0) {
            cellCount = newValue;
        }
    }

    /**
     *
     * @return The number of live cells on the current board.
     */
    public int getCellCount() {
        return cellCount;
    }

    /**
     *
     * @return The current generation count.
     */
    public int getGenerationCount() {
        return generationCount;
    }

    /**
     * Increases the generation count by 1. Private method used by
     * nextGeneration() method.
     */
    private void increaseGenerationCount() {
        generationCount++;
    }

    /**
     * Reverts the current board back to its original state.
     */
    public void resetBoard() {
        currentBoard = duplicateBoard(originalBoard);
        generationCount = 1;

    }

    /**
     * Creates a copy of a 2 dimensional byte array.
     *
     * @param original The board that you want to copy.
     * @return A reference to the new copy of the array.
     */
    private byte[][] duplicateBoard(byte[][] original) {
        byte[][] boardCopy = new byte[original.length][original[0].length];

        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, boardCopy[i], 0, original[0].length);
        }
        return boardCopy;
    }
}
