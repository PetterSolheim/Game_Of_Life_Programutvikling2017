package model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for the game board. Contains the actual board and the methods for
 * iterating to the next generation. Rules are located in the Rules class.
 */
public class Board {

    private static Board instance;
    private Rules rules;
    private byte[][] currentBoard;
    private byte[][] changedCells;
    private byte[][] originalBoard;
    private int generationCount = 0;
    private int cellCount = 0;

    /**
     * As this is a singleton, the constructor is private. Default start board
     * is a blank board of 50 rows and 50 columns.
     */
    public Board() {
        currentBoard = new byte[200][200];
        originalBoard = duplicateBoard(currentBoard);
        rules = Rules.getInstance(); // rules class is a singleton
    }
    
    public Board(int row, int col) {
        currentBoard = new byte[row][col];
        originalBoard = duplicateBoard(currentBoard);
        rules = Rules.getInstance(); // rules class is a singleton.
    }

    /**
     * Method for acquiring a reference to the Board object.
     *
     * @return A reference to the Board object.
     */
    /*public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }*/

    /**
     * Gets the current Board.
     *
     * @return byte[][] array.
     */
    public byte[][] getBoard() {
        return currentBoard;
    }

    /**
     * Gets a byte[][] array representing which cells were changed during the
     * last generation iteration.
     * @return byte[][] array.
     */
    public byte[][] getBoardChanges() {
        return changedCells;
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

    public void nextGeneration() {
        // a copy of the board is used to test the rules, while changes are
        // applied to the actual board.
        byte[][] neighbourCount = duplicateBoard(currentBoard);
        changedCells = new byte[currentBoard.length][currentBoard[0].length];

        // iterates through the board cells, count number of neighbours for each
        // cell, and apply changes based on the ruleset.
        for (int row = 0; row < neighbourCount.length; row++) {
            for (int col = 0; col < neighbourCount[0].length; col++) {
                int neighbours = countNeighbours(neighbourCount, row, col);

                if (neighbourCount[row][col] == 1 && (!rules.getSurvivalRules().contains(neighbours))) {
                    currentBoard[row][col] = 0;
                    changedCells[row][col] = 1;
                } else if (neighbourCount[row][col] == 0 && rules.getBirthRules().contains(neighbours)) {
                    currentBoard[row][col] = 1;
                    changedCells[row][col] = 1;
                }
            }
        }
        generationCount++;
    }

    /**
     * Counts the number of neighbours for a given cell. Method is private, and
     * used by the nextGeneration() method.
     *
     * @param cell The game board.
     * @param x The first index of the cell to have neighbours counted.
     * @param y The second index of the cell to have neighbours counted.
     * @return The number of neighbours.
     */
    private int countNeighbours(byte[][] cell, int x, int y) {
        int neighbours = 0;
        int rowLastIndex = cell.length - 1;
        int colLastIndex = cell[0].length - 1;

        if (y + 1 <= colLastIndex) {
            neighbours += cell[x][y + 1];
        }

        if (x - 1 >= 0 && y + 1 <= colLastIndex) {
            neighbours += cell[x - 1][y + 1];
        }

        if (x - 1 >= 0) {
            neighbours += cell[x - 1][y];
        }

        if (x - 1 >= 0 && y - 1 >= 0) {
            neighbours += cell[x - 1][y - 1];
        }

        if (y - 1 >= 0) {
            neighbours += cell[x][y - 1];
        }

        if (x + 1 <= rowLastIndex && y - 1 >= 0) {
            neighbours += cell[x + 1][y - 1];
        }

        if (x + 1 <= rowLastIndex) {
            neighbours += cell[x + 1][y];
        }

        if (x + 1 <= rowLastIndex && y + 1 <= colLastIndex) {
            neighbours += cell[x + 1][y + 1];
        }

        return neighbours;
    }

    public void toggleCellState(int row, int col) {
        if (currentBoard[row][col] == 1) {
            currentBoard[row][col] = 0;
        } else {
            currentBoard[row][col] = 1;
        }
    }

    public void settCellStateAlive(int row, int col) {
        currentBoard[row][col] = 1;
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
     * Reverts the current board back to its original state.
     */
    public void resetBoard() {
        currentBoard = duplicateBoard(originalBoard);
        generationCount = 0;

    }

    @Override
    public String toString() {
        StringBuilder tempReturnValue = new StringBuilder();
        for (int row = 0; row < currentBoard.length; row++) {
            for (int col = 0; col < currentBoard[row].length; col++) {
                tempReturnValue.append(currentBoard[row][col]);
            }
        }

        String finalReturnValue = tempReturnValue.toString();
        return finalReturnValue;
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
