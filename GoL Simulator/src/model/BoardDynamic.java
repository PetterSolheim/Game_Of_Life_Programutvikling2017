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
    private Rules rules;

    private int generationCount = 0;
    private int livingCells = 0;
    private byte dead = 0;
    private byte alive = 1;

    public BoardDynamic(int row, int col) {
        originalBoard = generateEmptyArrayList(row, col);
        rules = Rules.getInstance();
    }

    public void setBoard(ArrayList<ArrayList<Byte>> input) {
        originalBoard = duplicateBoard(input);
        currentBoard = duplicateBoard(input);
        changedCells = generateEmptyArrayList(input.size(), input.get(0).size());
    }

    public ArrayList<ArrayList<Byte>> getChangedCells() {
        return changedCells;
    }

    public ArrayList<ArrayList<Byte>> getCurrentBoard() {
        return currentBoard;
    }

    public ArrayList<ArrayList<Byte>> getOriginalBoard() {
        return originalBoard;
    }

    private ArrayList<ArrayList<Byte>> duplicateBoard(ArrayList<ArrayList<Byte>> input) {
        ArrayList<ArrayList<Byte>> boardCopy = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            boardCopy.add(new ArrayList<Byte>());
            for (int j = 0; j < input.get(0).size(); j++) {
                boardCopy.get(i).add(input.get(i).get(j));
            }
        }

        return boardCopy;
    }

    /**
     * Generates a 2D (an ArrayList of ArrayList of Bytes) ArrayList of Bytes
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
     * Method for testing if the given board needs to expand by adding one row
     * to the top of the board. Requirement for this to happen is if there is a
     * live cell in the top row. If all cells in top row are dead, return false.
     *
     * @param board
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
     * @param board
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
     * @param board
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
     * Expand the board with a row of dead cells on the left most side of the
     * board.
     *
     * @param board
     */
    private void expandEast(ArrayList<ArrayList<Byte>> board) {
        for (int i = 0; i < board.size(); i++) {
            board.get(i).add(0, dead);
        }
    }

    /**
     * TODO: FINISH
     * Method for testing if the given board needs to expand by adding one row
     * to the bottom. Requirement for this to happen is if there is currently
     * a live cell in the left most column. If not, return false.
     *
     * @param board
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
     * @param board
     */
    private void expandSouth(ArrayList<ArrayList<Byte>> board) {
        board.add(new ArrayList<>());
        for (int i = 0; i < board.get(0).size(); i++) {
            board.get(board.size() - 1).add(dead);
        }
    }

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
     * @param board
     */
    private void expandWest(ArrayList<ArrayList<Byte>> board) {
        for (int i = 0; i < board.size(); i++) {
            board.get(i).add(dead);
        }
    }

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

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < originalBoard.size(); i++) {
            for (int j = 0; j < originalBoard.get(i).size(); j++) {
                returnString.append(originalBoard.get(i).get(j));
            }
        }
        return returnString.toString();
    }
}
