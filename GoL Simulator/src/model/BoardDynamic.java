package model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class representing dynamic game boards using ArrayLists to represent the
 * board. Class is a rewrite of the depricated class Board, and methods work
 * much the same.
 */
public class BoardDynamic {

    private ArrayList<ArrayList<Byte>> currentBoard;
    private ArrayList<ArrayList<Byte>> originalBoard;
    private ArrayList<ArrayList<Byte>> changedCells;
    private Rules rules = Rules.getInstance();

    private int generationCount = 0;
    private int livingCells = 0;
    private byte dead = 0;
    private byte alive = 1;

    /**
     * Default constructor creates an empty starting board of 200x200 cells.
     */
    public BoardDynamic() {
        originalBoard = generateEmptyArrayList(200, 200);
        currentBoard = duplicateBoard(originalBoard);
        changedCells = generateEmptyArrayList(200, 200);
    }

    /**
     * Constructor which creates an empty starting board of a specified size.
     *
     * @param row the number of rows the starting board should have.
     * @param col the number of columns the starting board should have.
     */
    public BoardDynamic(int row, int col) {
        originalBoard = generateEmptyArrayList(row, col);
        currentBoard = duplicateBoard(originalBoard);
        changedCells = generateEmptyArrayList(row, col);
    }

    /**
     * Set a new board.
     *
     * @param newBoard, the new board to use in the game.
     */
    public void setBoard(ArrayList<ArrayList<Byte>> newBoard) {
        originalBoard = duplicateBoard(newBoard);
        currentBoard = duplicateBoard(newBoard);
        changedCells = generateEmptyArrayList(newBoard.size(), newBoard.get(0).size());
    }

    /**
     * 
     * @return the current board.
     */
    public ArrayList<ArrayList<Byte>> getBoard() {
        return currentBoard;
    }

    /**
     *
     * @return a byte[][] array of the cells which changed during the games last
     * generation shift.
     */
    public ArrayList<ArrayList<Byte>> getChangedCells() {
        return changedCells;
    }

    /**
     *
     * @return the number of cells on the board. Both dead, and living.
     */
    public long getNumberOfCells() {
        return currentBoard.size() * currentBoard.get(0).size();
    }

    /**
     * Counts the number of living cells on the board, updating the livingCells
     * variable.
     */
    private void countLivingCells() {
        livingCells = 0;
        for (int row = 0; row < currentBoard.size(); row++) {
            for (int col = 0; col < currentBoard.get(0).size(); col++) {
                if (currentBoard.get(row).get(col) == 1) {
                    livingCells++;
                }
            }
        }
    }

    /**
     *
     * @return the number of living cells.
     */
    public int getLivingCellCount() {
        return livingCells;
    }

    /**
     *
     * @return the current generation count.
     */
    public int getGenerationCount() {
        return generationCount;
    }

    /**
     *
     * @return a deep copy of the board object.
     */
    public BoardDynamic deepCopy() {
        BoardDynamic b = new BoardDynamic();
        b.currentBoard = duplicateBoard(this.currentBoard);
        b.originalBoard = duplicateBoard(this.originalBoard);
        b.generationCount = this.generationCount;
        b.countLivingCells();
        return b;
    }

    /**
     * Iterates the currentBoard to its next generation using the rules defined
     * in the Rules class object.
     *
     * TODO: changedCells is not currently being used.
     */
    public void nextGeneration() {
        if (rules.isDynamic()) {
            if (shouldExpandNorth(currentBoard)) {
                expandNorth(currentBoard);
            }

            if (shouldExpandEast(currentBoard)) {
                expandEast(currentBoard);
            }

            if (shouldExpandSouth(currentBoard)) {
                expandSouth(currentBoard);
            }

            if (shouldExpandWest(currentBoard)) {
                expandWest(currentBoard);
            }
        }

        // make a copy of the board for testing the rules.
        ArrayList<ArrayList<Byte>> testPattern = duplicateBoard(currentBoard);
        // keep track of what cells changed during this generation shift.
        changedCells = generateEmptyArrayList(currentBoard.size(), currentBoard.get(0).size());

        for (int row = 0; row < testPattern.size(); row++) {
            for (int col = 0; col < testPattern.get(0).size(); col++) {
                int nrOfNeighbours = countNeighbours(testPattern, row, col);
                if (testPattern.get(row).get(col) == 1 && !rules.getSurviveRules().contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, dead);
                } else if (testPattern.get(row).get(col) == 0 && rules.getBirthRules().contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, alive);
                }
            }
        }

    }

    /**
     * Determine if game board should expand by one row on the top. 
     * Requirement for this is that there is currently a live cell in the top 
     * row.
     * 
     * @param board, the board to check.
     * @return weather the board meets the requirements for expansion.
     */
    private boolean shouldExpandNorth(ArrayList<ArrayList<Byte>> board) {
        int numberOfLiveCells = 0;
        for (int i = 0; i < board.get(0).size(); i++) {
            numberOfLiveCells += board.get(0).get(i);
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Expand the board with a row of dead cells on the top of the board.
     *
     * @param board, the board to apply the row.
     */
    private void expandNorth(ArrayList<ArrayList<Byte>> board) {
        board.add(0, new ArrayList<>());
        for (int i = 0; i < board.get(1).size(); i++) {
            board.get(0).add(dead);
        }
    }

    /**
     * Method for testing if the given board needs to expand by adding one row
     * to its left side. Requirement for this to happen is if there is currently
     * a live cell in the left most column. If not, return false.
     *
     * @param board, the board to check.
     * @return weather the board meets the requirements for expansion.
     */
    private boolean shouldExpandEast(ArrayList<ArrayList<Byte>> board) {
        int numberOfLiveCells = 0;
        for (int i = 0; i < board.size(); i++) {
            numberOfLiveCells += board.get(i).get(0);
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Expand the board with a column of dead cells on the left most side of the
     * board.
     *
     * @param board, the board to apply the column.
     */
    private void expandEast(ArrayList<ArrayList<Byte>> board) {
        for (int i = 0; i < board.size(); i++) {
            board.get(i).add(0, dead);
        }
    }

    /**
     * Method for testing if the given board needs to expand by
     * adding one row to the bottom. Requirement for this to happen is if there
     * is currently a live cell in the left most column. If not, return false.
     *
     * @param board, the board to check.
     * @return weather the board meets the requirements for expansion.
     */
    private boolean shouldExpandSouth(ArrayList<ArrayList<Byte>> board) {
        int numberOfLiveCells = 0;
        for (int i = 0; i < board.get(board.size() - 1).size(); i++) {
            numberOfLiveCells += board.get(board.size() - 1).get(i);
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Expand the board with a row of dead cells on the south side of the board.
     *
     * @param board, the board to apply the row.
     */
    private void expandSouth(ArrayList<ArrayList<Byte>> board) {
        board.add(new ArrayList<>());
        for (int i = 0; i < board.get(0).size(); i++) {
            board.get(board.size() - 1).add(dead);
        }
    }

    /**
     * Method for testing if the given board needs to expand by adding one
     * column to the right most side of the board. Requirement for this to
     * happen is if there is currently a live cell in the right most column.
     * If not, return false.
     * 
     * @param board, the board to check.
     * @return weather the board meets the requirements for expansion.
     */
    private boolean shouldExpandWest(ArrayList<ArrayList<Byte>> board) {
        int numberOfLiveCells = 0;
        for (int i = 0; i < board.size(); i++) {
            numberOfLiveCells += board.get(i).get(board.get(i).size() - 1);
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Expand the board with a row of dead cells on the west side of the board.
     *
     * @param board, the board to apply the column.
     */
    private void expandWest(ArrayList<ArrayList<Byte>> board) {
        for (int i = 0; i < board.size(); i++) {
            board.get(i).add(dead);
        }
    }

    /**
     * Counts the number of living neighbours for a given cell.
     *
     * @param board, the game board.
     * @param row the row location of the cell to have neighbours counted.
     * @param col the column location of the cell to have neighbours counted.
     * @return the number of neighbours.
     */
    private int countNeighbours(ArrayList<ArrayList<Byte>> board, int row, int col) {
        int neighbours = 0;
        int rowLastIndex = board.size() - 1;
        int colLastIndex = board.get(0).size() - 1;

        if (col + 1 <= colLastIndex) {
            neighbours += board.get(row).get(col + 1);
        }

        if (row - 1 >= 0 && col + 1 <= colLastIndex) {
            neighbours += board.get(row - 1).get(col + 1);
        }

        if (row - 1 >= 0) {
            neighbours += board.get(row - 1).get(col);
        }

        if (row - 1 >= 0 && col - 1 >= 0) {
            neighbours += board.get(row - 1).get(col - 1);
        }

        if (col - 1 >= 0) {
            neighbours += board.get(row).get(col - 1);
        }

        if (row + 1 <= rowLastIndex && col - 1 >= 0) {
            neighbours += board.get(row + 1).get(col - 1);
        }

        if (row + 1 <= rowLastIndex) {
            neighbours += board.get(row + 1).get(col);
        }

        if (row + 1 <= rowLastIndex && col + 1 <= colLastIndex) {
            neighbours += board.get(row + 1).get(col + 1);
        }

        return neighbours;
    }

    /**
     * Toggles the state of a given cell. Live cell becomes dead, dead cell
     * becomes alive.
     *
     * @param row the row position of the cell to toggle.
     * @param col the column position of the cell to toggle.
     */
    public void toggleCellState(int row, int col) {
        if (currentBoard.get(row).get(col) == 1) {
            livingCells--;
            currentBoard.get(row).set(col, dead);
        } else {
            currentBoard.get(row).set(col, alive);
            livingCells++;
        }
    }

    /**
     * Makes a spesified cell alive.
     *
     * @param row the row position of the cell to make alive.
     * @param col the column position of the cell to make alive.
     */
    public void setCellStateAlive(int row, int col) {
        if (currentBoard.get(row).get(col) != 1) {
            currentBoard.get(row).set(col, alive);
            livingCells++;
        }
    }

    /**
     * A simple method or copying a 2D ArrayList of Bytes.
     * @param original The 2D ArrayList of Bytes to copy.
     * @return The copy of the board.
     */
    private ArrayList<ArrayList<Byte>> duplicateBoard(ArrayList<ArrayList<Byte>> original) {
        ArrayList<ArrayList<Byte>> boardCopy = new ArrayList<>();

        for (int i = 0; i < original.size(); i++) {
            boardCopy.add(new ArrayList<Byte>());
            for (int j = 0; j < original.get(0).size(); j++) {
                boardCopy.get(i).add(original.get(i).get(j));
            }
        }

        return boardCopy;
    }

    /**
     * Generates a 2D ArrayList (an ArrayList of ArrayList) of Bytes
     * representing the game board. All Bytes are set to 0.
     *
     * @param row the number of rows the 2D ArrayList should have.
     * @param col the number of columns the 2D ArrayList should have.
     * @return the ArrayList.
     */
    private ArrayList<ArrayList<Byte>> generateEmptyArrayList(int row, int col) {
        ArrayList<ArrayList<Byte>> emptyBoard = new ArrayList<>();
        byte dead = 0;
        for (int i = 0; i < row; i++) {
            emptyBoard.add(new ArrayList<Byte>());
            for (int j = 0; j < col; j++) {
                emptyBoard.get(i).add(dead);
            }
        }
        return emptyBoard;
    }

    /**
     * 
     * @return a string representation of the current board.
     */
    @Override
    public String toString() {
        StringBuilder returnValue = new StringBuilder();
        for (int i = 0; i < currentBoard.size(); i++) {
            for (int j = 0; j < currentBoard.get(i).size(); j++) {
                returnValue.append(currentBoard.get(i).get(j));
            }
        }
        return returnValue.toString();
    }
}
