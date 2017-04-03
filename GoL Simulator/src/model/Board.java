package model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for the game board. Contains the actual board and the methods for
 * iterating to the next generation. Rules are located in the Rules class.
 */
public class Board {
    private byte[][] currentBoard;
    private byte[][] changedCells;
    private byte[][] originalBoard;
    private int generationCount = 0;
    private int livingCells = 0;
    private ArrayList<Integer> survivalRules;
    private ArrayList<Integer> birthRules;

    public Board() {
        currentBoard = new byte[200][200];
        originalBoard = duplicateBoard(currentBoard);

        // apply Conway Rules (23/3) by default
        survivalRules = new ArrayList<Integer>();
        survivalRules.add(2);
        survivalRules.add(3);
        birthRules = new ArrayList<Integer>();
        birthRules.add(3);
    }

    public Board(int row, int col) {
        currentBoard = new byte[row][col];
        originalBoard = duplicateBoard(currentBoard);

        // apply Conway Rules (23/3) by default
        survivalRules = new ArrayList<Integer>();
        survivalRules.add(2);
        survivalRules.add(3);
        birthRules = new ArrayList<Integer>();
        birthRules.add(3);
    }

    /**
     * Returns the number of cells on the board.
     *
     * @return int
     */
    public long numberOfCells() {
        return currentBoard.length * currentBoard[0].length;
    }

    /**
     * Creates a deep copy of the board.
     *
     * @return board
     */
    public Board deepCopy() {
        Board b = new Board();
        b.currentBoard = duplicateBoard(this.currentBoard);
        b.originalBoard = duplicateBoard(this.originalBoard);
        
        ArrayList<Integer> survivalRulesCopy = new ArrayList<Integer>();
        for(int i = 0; i < survivalRules.size(); i++) {
            survivalRulesCopy.add(survivalRules.get(i));
        }
        b.setSurviveRules(survivalRulesCopy);
        
        ArrayList<Integer> birthRulesCopy = new ArrayList<Integer>();
        for(int i = 0; i < birthRules.size(); i++) {
            birthRulesCopy.add(birthRules.get(i));
        }
        b.setBirthRules(birthRulesCopy);
        
        b.generationCount = this.generationCount;
        b.countLivingCells();
        return b;
    }

    /**
     * Updates the board cellCount variable to reflect the current number of
     * living cells.
     */
    private void countLivingCells() {
        livingCells = 0;
        for (int col = 0; col < currentBoard.length; col++) {
            for (int row = 0; row < currentBoard.length; row++) {
                if (currentBoard[row][col] == 1) {
                    livingCells++;
                }
            }
        }
    }

    /**
     * Sets the survival values for the game rules. New values can be passed as
     * either a number of integers, or an array of integers. If duplicate values
     * are passed, they will be removed, i.e. 2,3,3,4 will be stored as 2,3,4.
     *
     * @param input
     */
    public void setSurviveRules(int... input) {
        Arrays.sort(input);
        ArrayList<Integer> inputWithoutDuplicates = new ArrayList<Integer>();

        for (int i = 0; i < input.length; i++) {
            if (i == 0) {
                inputWithoutDuplicates.add(input[i]);
            } else if (input[i] != input[i - 1]) {
                inputWithoutDuplicates.add(input[i]);
            }
        }
        survivalRules = inputWithoutDuplicates;
    }
    
    public void setSurviveRules(ArrayList<Integer> input) {
        survivalRules = input;
    }

    /**
     * Sets the birth values for the game rules. New values can be passed as
     * either a number of integers, or an array of integers. If duplicate values
     * are passed, they will be removed, i.e. 2,3,3,4 will be stored as 2,3,4.
     *
     * @param input
     */
    public void setBirthRules(int... input) {
        Arrays.sort(input);
        ArrayList<Integer> inputWithoutDuplicates = new ArrayList<Integer>();

        for (int i = 0; i < input.length; i++) {
            if (i == 0) {
                inputWithoutDuplicates.add(input[i]);
            } else if (input[i] != input[i - 1]) {
                inputWithoutDuplicates.add(input[i]);
            }
        }
        birthRules = inputWithoutDuplicates;
    }
    
    public void setBirthRules(ArrayList<Integer> input) {
        birthRules = input;
    }

    /**
     * Acquires an ArrayList of integer values which define the number of live
     * neighbours a dead cell must have to be born.
     *
     * @return an ArrayList of Integers.
     */
    public ArrayList<Integer> getBirthRules() {
        return birthRules;
    }

    /**
     * Acquires an ArrayList of integer values which define the number of live
     * neighbours a live cell must have to survive.
     *
     * @return an ArrayList of Integers.
     */
    public ArrayList<Integer> getSurviveRules() {
        return survivalRules;
    }

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
     *
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
        countLivingCells();
    }

    /**
     * Iterates the board to the next generation.
     */
    public void nextGeneration() {
        // a copy of the board is used to test the rules, while changes are
        // applied to the actual board.
        byte[][] testPattern = duplicateBoard(currentBoard);
        changedCells = new byte[currentBoard.length][currentBoard[0].length];

        // iterates through the board cells, count number of neighbours for each
        // cell, and apply changes based on the ruleset.
        for (int row = 0; row < testPattern.length; row++) {
            for (int col = 0; col < testPattern[0].length; col++) {
                int neighbours = countNeighbours(testPattern, row, col);

                if (testPattern[row][col] == 1 && (!survivalRules.contains(neighbours))) {
                    currentBoard[row][col] = 0;
                    changedCells[row][col] = 1;
                    livingCells--;
                } else if (testPattern[row][col] == 0 && birthRules.contains(neighbours)) {
                    currentBoard[row][col] = 1;
                    changedCells[row][col] = 1;
                    livingCells++;
                }
            }
        }
        System.out.println(livingCells);
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

    /**
     * Toggles the state of a give cell. Live cell becomes dead, dead cell
     * becomes alive.
     *
     * @param row y position of the cell to toggle.
     * @param col x position of the cell to toggle.
     */
    public void toggleCellState(int row, int col) {
        if (currentBoard[row][col] == 1) {
            livingCells--;
            currentBoard[row][col] = 0;
        } else {
            currentBoard[row][col] = 1;
            livingCells++;
        }
    }

    /**
     * Makes a spesific cell alive.
     *
     * @param row y position of the cell to make alive.
     * @param col x position of the cell to make alive.
     */
    public void setCellStateAlive(int row, int col) {
        if (currentBoard[row][col] != 1) {
            currentBoard[row][col] = 1;
            livingCells++;
        }
    }

    /**
     *
     * @return The current generation count.
     */
    public int getGenerationCount() {
        return generationCount;
    }

    /**
     * Return the number of living cells on the current board.
     * @return 
     */
    public int getLivingCellCount() {
        return livingCells;
    }

    /**
     * Reverts the current board back to its original state.
     */
    public void resetBoard() {
        currentBoard = duplicateBoard(originalBoard);
        generationCount = 0;
        countLivingCells();
    }

    /**
     * A string representation of the board. Exists soley for use with JUnit.
     *
     * @return
     */
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
     * A simple method for copying a 2D byte array.
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
