package model;

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
    private Rules rules = Rules.getInstance();

    /**
     * Default constructor creates an empty starting board of 200x200 cells.
     */
    public Board() {
        currentBoard = new byte[200][200];
        originalBoard = duplicateBoard(currentBoard);
    }

    /**
     * Constructor which creates an empty starting board of a specified size.
     *
     * @param row the number of rows the starting board should have.
     * @param col the number of columns the starting board should have.
     */
    public Board(int row, int col) {
        currentBoard = new byte[row][col];
        originalBoard = duplicateBoard(currentBoard);
    }

    /**
     * Set a new board.
     *
     * @param newBoard, the new board to use in the game.
     */
    public void setBoard(byte[][] newBoard) {
        originalBoard = duplicateBoard(newBoard);
        currentBoard = duplicateBoard(newBoard);
        countLivingCells();
    }

    /**
     *
     * @return the current board.
     */
    public byte[][] getBoard() {
        return currentBoard;
    }

    /**
     *
     * @return a byte[][] array of the cells which changed during the games last
     * generation shift.
     */
    public byte[][] getChangedCells() {
        return changedCells;
    }

    /**
     *
     * @return the number of cells on the board. Both dead, and living.
     */
    public long getNumberOfCells() {
        return currentBoard.length * currentBoard[0].length;
    }

    /**
     * Counts the number of living cells on the board, updating the livingCells
     * variable.
     */
    private void countLivingCells() {
        livingCells = 0;
        for (int row = 0; row < currentBoard.length; row++) {
            for (int col = 0; col < currentBoard.length; col++) {
                if (currentBoard[row][col] == 1) {
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
    public Board deepCopy() {
        Board b = new Board();
        b.currentBoard = duplicateBoard(this.currentBoard);
        b.originalBoard = duplicateBoard(this.originalBoard);
        b.generationCount = this.generationCount;
        b.countLivingCells();
        return b;
    }

    /**
     * Iterates the current board to its next generation, playing by the rules
     * defined in the Rules class object.
     */
    public void nextGeneration() {
        // reset list of changed cells.
        changedCells = new byte[currentBoard.length][currentBoard[0].length];

        if (rules.isDynamic()) {
            expandBoardIfNeeded();
        }

        // a copy of the board is used to test the rules, while changes are
        // applied to the actual board.
        byte[][] testPattern = duplicateBoard(currentBoard);

        // iterate through the board cells, count number of neighbours for each
        // cell, and apply changes based on the ruleset.
        for (int row = 0; row < testPattern.length; row++) {
            for (int col = 0; col < testPattern[0].length; col++) {
                int neighbours = countNeighbours(testPattern, row, col);

                if (testPattern[row][col] == 1 && (!rules.getSurviveRules().contains(neighbours))) {
                    currentBoard[row][col] = 0;
                    changedCells[row][col] = 1;
                    livingCells--;
                } else if (testPattern[row][col] == 0 && rules.getBirthRules().contains(neighbours)) {
                    currentBoard[row][col] = 1;
                    changedCells[row][col] = 1;
                    livingCells++;
                }
            }
        }
        generationCount++;
    }

    /**
     * Checks the current board to see if it should be expanded. If so, expand
     * it.
     */
    private void expandBoardIfNeeded() {
        boolean boardExpanded = false;
        if (shouldExpandNorth()) {
            expandNorth();
            boardExpanded = true;
        }
        if (shouldExpandEast()) {
            expandEast();
            boardExpanded = true;
        }
        if (shouldExpandSouth()) {
            expandSouth();
            boardExpanded = true;
        }
        if (shouldExpandWest()) {
            expandWest();
            boardExpanded = true;
        }
        if (boardExpanded) {
            // if board expanded, all cells will have shifted, and thereby
            // changed.
            changedCells = new byte[currentBoard.length][currentBoard[0].length];
            for (int row = 0; row < changedCells.length; row++) {
                for (int col = 0; col < changedCells[0].length; col++) {
                    changedCells[row][col] = 1;
                }
            }
        }
    }

    /**
     * Determine if game board should expand by one row on the top. Requirement
     * for this is that there is currently a live cell in the top row.
     *
     * @param board, the board to check.
     * @return weather the board meets the requirements for expansion.
     */
    private boolean shouldExpandNorth() {
        int numberOfLiveCells = 0;
        for (int col = 0; col < currentBoard[0].length; col++) {
            numberOfLiveCells += currentBoard[0][col];
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
     * @param currentBoard, the board to apply the row.
     */
    private void expandNorth() {
        byte[][] newBoard = new byte[currentBoard.length + 1][currentBoard[0].length];
        for (int row = 0; row < currentBoard.length; row++) {
            for (int col = 0; col < currentBoard[0].length; col++) {
                newBoard[row + 1][col] = currentBoard[row][col];
            }
        }
        currentBoard = newBoard;
    }

    private boolean shouldExpandEast() {
        int numberOfLiveCells = 0;
        for (int row = 0; row < currentBoard.length; row++) {
            numberOfLiveCells += currentBoard[row][currentBoard[0].length - 1];
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void expandEast() {
        byte[][] newBoard = new byte[currentBoard.length][currentBoard[0].length + 1];
        for (int row = 0; row < currentBoard.length; row++) {
            for (int col = 0; col < currentBoard[0].length; col++) {
                newBoard[row][col] = currentBoard[row][col];
            }
        }
        currentBoard = newBoard;
    }

    private boolean shouldExpandSouth() {
        int numberOfLiveCells = 0;
        for (int col = 0; col < currentBoard[0].length; col++) {
            numberOfLiveCells += currentBoard[currentBoard.length - 1][col];
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void expandSouth() {
        byte[][] newBoard = new byte[currentBoard.length + 1][currentBoard[0].length];
        for (int row = 0; row < currentBoard.length; row++) {
            for (int col = 0; col < currentBoard[0].length; col++) {
                newBoard[row][col] = currentBoard[row][col];
            }
        }
        currentBoard = newBoard;
    }

    private boolean shouldExpandWest() {
        int numberOfLiveCells = 0;
        for (int row = 0; row < currentBoard.length; row++) {
            numberOfLiveCells += currentBoard[row][0];
        }
        if (numberOfLiveCells > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void expandWest() {
        byte[][] newBoard = new byte[currentBoard.length][currentBoard[0].length + 1];
        for (int row = 0; row < currentBoard.length; row++) {
            for (int col = 0; col < currentBoard[0].length; col++) {
                newBoard[row][col + 1] = currentBoard[row][col];
            }
        }
        currentBoard = newBoard;
    }

    /**
     * Counts the number of living neighbours for a given cell.
     *
     * @param board, the game board.
     * @param row the row location of the cell to have neighbours counted.
     * @param col the column location of the cell to have neighbours counted.
     * @return the number of neighbours.
     */
    private int countNeighbours(byte[][] board, int row, int col) {
        int neighbours = 0;
        int rowLastIndex = board.length - 1;
        int colLastIndex = board[0].length - 1;

        if (col + 1 <= colLastIndex) {
            neighbours += board[row][col + 1];
        }

        if (row - 1 >= 0 && col + 1 <= colLastIndex) {
            neighbours += board[row - 1][col + 1];
        }

        if (row - 1 >= 0) {
            neighbours += board[row - 1][col];
        }

        if (row - 1 >= 0 && col - 1 >= 0) {
            neighbours += board[row - 1][col - 1];
        }

        if (col - 1 >= 0) {
            neighbours += board[row][col - 1];
        }

        if (row + 1 <= rowLastIndex && col - 1 >= 0) {
            neighbours += board[row + 1][col - 1];
        }

        if (row + 1 <= rowLastIndex) {
            neighbours += board[row + 1][col];
        }

        if (row + 1 <= rowLastIndex && col + 1 <= colLastIndex) {
            neighbours += board[row + 1][col + 1];
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
        if (currentBoard[row][col] == 1) {
            livingCells--;
            currentBoard[row][col] = 0;
        } else {
            currentBoard[row][col] = 1;
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
        if (currentBoard[row][col] != 1) {
            currentBoard[row][col] = 1;
            livingCells++;
        }
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
     * A simple method for copying a 2D byte array.
     *
     * @param original The board that you want to copy.
     * @return The copy of the board.
     */
    private byte[][] duplicateBoard(byte[][] original) {
        byte[][] boardCopy = new byte[original.length][original[0].length];

        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, boardCopy[i], 0, original[0].length);
        }
        return boardCopy;
    }

    /**
     *
     * @return A string representation o the current board.
     */
    @Override
    public String toString() {
        StringBuilder returnValue = new StringBuilder();
        for (int row = 0; row < currentBoard.length; row++) {
            for (int col = 0; col < currentBoard[row].length; col++) {
                returnValue.append(currentBoard[row][col]);
            }
        }
        return returnValue.toString();
    }
}
