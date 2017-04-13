/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author aleks
 */
public class BoardDynamic {

    private ArrayList<ArrayList<Byte>> currentBoard;
    private ArrayList<ArrayList<Byte>> originalBoard;
    private ArrayList<ArrayList<Byte>> changedCells;
    private ArrayList<Integer> survivalRules;
    private ArrayList<Integer> birthRules;

    private int generationCount = 0;
    private int livingCells = 0;
    private byte dead = 0;
    private byte alive = 1;
    private boolean dynamic = false;

    public BoardDynamic(int row, int col) {
        originalBoard = generateEmptyArrayList(row, col);
        survivalRules = new ArrayList<Integer>();
        survivalRules.add(2);
        survivalRules.add(3);
        birthRules = new ArrayList<Integer>();
        birthRules.add(3);
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
     * Determin if a board should expand, and if so, expand it.
     *
     * @param board the board that should be checked.
     */
    private void determinAndExpand(ArrayList<ArrayList<Byte>> board) {
        //test northern border of board
        int numberOfLiveCells = 0;
        for (int i = 0; i < board.get(0).size(); i++) {
            numberOfLiveCells += board.get(0).get(i);
        }
        if (numberOfLiveCells > 0) {
            expandNorth(board);
        }

        // test south border of board
        numberOfLiveCells = 0;
        for (int i = 0; i < board.get(board.size() - 1).size(); i++) {
            numberOfLiveCells += board.get(board.size() - 1).get(i);
        }
        if (numberOfLiveCells > 0) {
            expandSouth(board);
        }

        // test east border of board
        numberOfLiveCells = 0;
        for (int i = 0; i < board.size(); i++) {
            numberOfLiveCells += board.get(i).get(0);
        }
        if (numberOfLiveCells > 0) {
            expandEast(board);
        }

        // test west border of board
        numberOfLiveCells = 0;
        for (int i = 0; i < board.size(); i++) {
            numberOfLiveCells += board.get(i).get(board.get(i).size() - 1);
        }
        if (numberOfLiveCells > 0) {
            expandWest(board);
        }
    }

    /**
     * Expand the board with a row of dead cells on the north side of the board.
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

    /**
     * Expand the board with a row of dead cells on the east side of the board.
     *
     * @param board
     */
    private void expandEast(ArrayList<ArrayList<Byte>> board) {
        for(int i = 0; i < board.size(); i++) {
            board.get(i).add(0, dead);
        }
    }

    /**
     * Expand the board with a row of dead cells on the west side of the board.
     *
     * @param board
     */
    private void expandWest(ArrayList<ArrayList<Byte>> board) {
        for(int i = 0; i < board.size(); i++) {
            board.get(i).add(dead);
        }
    }

    public void nextGeneration() {
        if (dynamic) {
            determinAndExpand(currentBoard);
        }

        // make a copy of the board for testing the rules.
        ArrayList<ArrayList<Byte>> testPattern = duplicateBoard(currentBoard);
        // keep track of what cells changed during this generation shift.
        changedCells = generateEmptyArrayList(currentBoard.size(), currentBoard.get(0).size());

        for (int row = 0; row < testPattern.size(); row++) {
            for (int col = 0; col < testPattern.get(0).size(); col++) {
                int nrOfNeighbours = countNeighbours(testPattern, row, col);
                if (testPattern.get(row).get(col) == 1 && !survivalRules.contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, dead);
                } else if (testPattern.get(row).get(col) == 0 && birthRules.contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, alive);
                }
            }
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

    /**
     * Returns true if the rules for the board specify that it should behave
     * dynamically.
     *
     * @return
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * Set weather the board should behave dynamically.
     *
     * @param dynamic
     */
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
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
