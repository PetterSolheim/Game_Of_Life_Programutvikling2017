package model;

import java.util.ArrayList;

public class BoardDynamic {

    private ArrayList<ArrayList<Byte>> currentBoard;
    private ArrayList<ArrayList<Byte>> originalBoard;
    private ArrayList<ArrayList<Byte>> changedCells;
    private Rules rules = Rules.getInstance();

    private int generationCount = 0;
    private int livingCells = 0;
    private byte dead = 0;
    private byte alive = 1;
    private byte changed = 1;

    public BoardDynamic() {
        originalBoard = generateEmptyArrayList(200, 200);
        currentBoard = duplicateBoard(originalBoard);
        changedCells = generateEmptyArrayList(200, 200);
    }

    public BoardDynamic(int row, int col) {
        originalBoard = generateEmptyArrayList(row, col);
        currentBoard = duplicateBoard(originalBoard);
        changedCells = generateEmptyArrayList(row, col);
    }

    public void setBoard(ArrayList<ArrayList<Byte>> newBoard) {
        originalBoard = duplicateBoard(newBoard);
        currentBoard = duplicateBoard(newBoard);
        changedCells = generateEmptyArrayList(newBoard.size(), newBoard.get(0).size());
    }

    public ArrayList<ArrayList<Byte>> getBoard() {
        return currentBoard;
    }

    public ArrayList<ArrayList<Byte>> getChangedCells() {
        return changedCells;
    }

    public long getNumberOfCells() {
        return currentBoard.size() * currentBoard.get(0).size();
    }

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

    public int getLivingCellCount() {
        return livingCells;
    }

    public int getGenerationCount() {
        return generationCount;
    }

    public BoardDynamic deepCopy() {
        BoardDynamic b = new BoardDynamic();
        b.currentBoard = duplicateBoard(this.currentBoard);
        b.originalBoard = duplicateBoard(this.originalBoard);
        b.generationCount = this.generationCount;
        b.countLivingCells();
        return b;
    }

    /**
     * Iterates the current board to its next generation, playing by the rules
     * defined in the Rules class object.
     *
     * @see model.Rules
     */
    public void nextGeneration() {
        // keep track of what cells changed during this generation shift.
        changedCells = generateEmptyArrayList(currentBoard.size(), currentBoard.get(0).size());
        
        if (rules.isDynamic()) {
            expandBoardIfNeeded();
        }

        // make a copy of the board for testing the rules.
        ArrayList<ArrayList<Byte>> testPattern = duplicateBoard(currentBoard);

        for (int row = 0; row < testPattern.size(); row++) {
            for (int col = 0; col < testPattern.get(0).size(); col++) {
                int nrOfNeighbours = countNeighbours(testPattern, row, col);
                
                if (testPattern.get(row).get(col) == 1 && !rules.getSurviveRules().contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, dead);
                    changedCells.get(row).set(col, changed);
                    livingCells--;
                } else if (testPattern.get(row).get(col) == 0 && rules.getBirthRules().contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, alive);
                    changedCells.get(row).set(col, changed);
                    livingCells++;
                }
            }
        }
        generationCount++;
    }

    private void expandBoardIfNeeded() {
        boolean boardExpanded = false;
        if (shouldExpandNorth()) {
            expandNorth();
        }

        if (shouldExpandEast()) {
            expandEast();
        }

        if (shouldExpandSouth()) {
            expandSouth();
        }

        if (shouldExpandWest()) {
            expandWest();
        }
    }

    private boolean shouldExpandNorth() {
        int numberOfLiveCells = 0;
        for (int i = 0; i < currentBoard.get(0).size(); i++) {
            numberOfLiveCells += currentBoard.get(0).get(i);
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void expandNorth() {
        currentBoard.add(0, new ArrayList<>());
        for (int i = 0; i < currentBoard.get(1).size(); i++) {
            currentBoard.get(0).add(dead);
        }
    }

    private boolean shouldExpandEast() {
        int numberOfLiveCells = 0;
        for (int i = 0; i < currentBoard.size(); i++) {
            numberOfLiveCells += currentBoard.get(i).get(0);
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void expandEast() {
        for (int i = 0; i < currentBoard.size(); i++) {
            currentBoard.get(i).add(0, dead);
        }
    }

    private boolean shouldExpandSouth() {
        int numberOfLiveCells = 0;
        for (int i = 0; i < currentBoard.get(currentBoard.size() - 1).size(); i++) {
            numberOfLiveCells += currentBoard.get(currentBoard.size() - 1).get(i);
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void expandSouth() {
        currentBoard.add(new ArrayList<>());
        for (int i = 0; i < currentBoard.get(0).size(); i++) {
            currentBoard.get(currentBoard.size() - 1).add(dead);
        }
    }

    private boolean shouldExpandWest() {
        int numberOfLiveCells = 0;
        for (int i = 0; i < currentBoard.size(); i++) {
            numberOfLiveCells += currentBoard.get(i).get(currentBoard.get(i).size() - 1);
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void expandWest() {
        for (int i = 0; i < currentBoard.size(); i++) {
            currentBoard.get(i).add(dead);
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
    
    public void toggleCellState(int row, int col) {
        if (currentBoard.get(row).get(col) == 1) {
            livingCells--;
            currentBoard.get(row).set(col, dead);
        } else {
            currentBoard.get(row).set(col, alive);
            livingCells++;
        }
    }
    
    public void setCellStateAlive(int row, int col) {
        if (currentBoard.get(row).get(col) != 1) {
            currentBoard.get(row).set(col, alive);
            livingCells++;
        }
    }

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
