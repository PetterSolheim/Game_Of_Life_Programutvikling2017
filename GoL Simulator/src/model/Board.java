package model;

/**
 * This class contains the game board and its mechanics, such as moving the game
 * board to the next generation. This class has been deprecated and replaced by
 * the {@link model.BoardDynamic} class.
 *
 * @deprecated use {@link model.BoardDynamic} instead
 * @see model.BoardDynamic
 */
@Deprecated
public class Board {

    /**
     * Represents the game board in its current game state. 1 represents live
     * cells, 0 represents dead cells.
     */
    private byte[][] currentBoard;

    /**
     * Used to represent cells which have changed during the last generation
     * shift.
     */
    private byte[][] changedCells;

    /**
     * Used to store the game board as it was before the game starts. Allows for
     * resetting the game board.
     */
    private byte[][] originalBoard;

    private int generationCount = 0;
    private int livingCells = 0;
    private int indexSum = 0;
    private Rules rules = Rules.getInstance();

    /**
     * Board no-argument constructor initializes a game board consisting of 200
     * x 200 dead cells.
     */
    public Board() {
        currentBoard = new byte[200][200];
        originalBoard = duplicateBoard(currentBoard);
        changedCells = new byte[200][200];
    }

    /**
     * Allows one to define the starting size of the game board.
     *
     * @param row an <code>int</code> specifying the number of rows for the
     * starting board.
     * @param col an <code>int</code> specifying the number of columns for the
     * starting board.
     * @throws IllegalArgumentException in the case that either the number of
     * rows of columns are defined to be bellow 1.
     */
    public Board(int row, int col) {
        if (row < 1 || col < 1) {
            throw new IllegalArgumentException("Number of rows and columns must"
                    + "be higher than 0!");
        }
        currentBoard = new byte[row][col];
        originalBoard = duplicateBoard(currentBoard);
        changedCells = new byte[row][col];
    }

    /**
     * Constructor accepts a <code>byte[][]</code> representing the starting
     * state of the board.
     *
     * @param board a <code>byte[][]</code> specifying the starting board.
     */
    public Board(byte[][] board) {
        this.currentBoard = duplicateBoard(board);
    }

    /**
     * Sets a new game board.
     *
     * @param newBoard a <code>byte[][]</code> specifying the new game board.
     */
    public void setBoard(byte[][] newBoard) {
        originalBoard = duplicateBoard(newBoard);
        currentBoard = duplicateBoard(newBoard);
        countLivingCells();
    }

    /**
     * Gets the current board.
     *
     * @return a <code>byte[][]</code> representing the current game board.
     */
    public byte[][] getBoard() {
        return currentBoard;
    }

    /**
     * Gets a list of all the cells which changed their state during the last
     * generation shift.
     *
     * @return a <code>byte[][]</code> representing the cells which have changed
     * where 1 means changed and 0 means no change.
     */
    public byte[][] getChangedCells() {
        return changedCells;
    }

    /**
     * Updates the livingCells variable to reflect the number of living cells on
     * the current board.
     */
    private void countLivingCells() {
        livingCells = 0;
        for (int row = 0; row < currentBoard.length; row++) {
            for (int col = 0; col < currentBoard[0].length; col++) {
                if (currentBoard[row][col] == 1) {
                    livingCells++;
                }
            }
        }
    }

    public int getIndexSum() {
        int sum = indexSum;
        indexSum = 0;
        return sum;
    }

    /**
     * Gets the number of cells on the current board, both living and dead.
     *
     * @return an <code>int</code> specifying the number of cells, both living
     * and dead, on the current board.
     */
    public int getCellCount() {
        return currentBoard.length * currentBoard[0].length;
    }

    /**
     * Gets the number of living cells on the current board.
     *
     * @return an <code>int</code> specifying the number of living cells on the
     * current board.
     */
    public int getLivingCellCount() {
        return livingCells;
    }

    /**
     * Gets the current generation count for the game.
     *
     * @return an <code>int</code> specifying the current generation count.
     */
    public int getGenerationCount() {
        return generationCount;
    }

    /**
     * Gets a deep copy of this board object.
     *
     * @return a deep copy of the <code>Board</code> object.
     */
    public Board deepCopy() {
        Board b = new Board();
        b.currentBoard = duplicateBoard(this.currentBoard);
        b.changedCells = duplicateBoard(this.currentBoard);
        b.originalBoard = duplicateBoard(this.originalBoard);
        b.changedCells = duplicateBoard(this.changedCells);
        b.livingCells = this.livingCells;
        b.generationCount = this.generationCount;
        return b;
    }

    /**
     * Iterates the current board to its next generation, playing by the rules
     * defined in the Rules class object.
     *
     * @see model.Rules
     */
    public void nextGeneration() {
        // reset list of changed cells.
        changedCells = new byte[currentBoard.length][currentBoard[0].length];

        if (rules.isDynamic()) {
            expandBoardIfNeeded();
        }

        // a copy of the board is used to test the rules, while changes are
        // applied to the actual board.GE
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
                    indexSum += (row + col);

                } else if (testPattern[row][col] == 0 && rules.getBirthRules().contains(neighbours)) {
                    currentBoard[row][col] = 1;
                    changedCells[row][col] = 1;
                    livingCells++;
                }
            }
        }
        int i = getIndexSum();
        generationCount++;
    }

    /**
     * Checks the current board to see if it should be expanded. Requirement for
     * expansion is if a living cells is touching the edge of the game board.
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
     * Check the game board to see if it meets requirements for expansion on the
     * top of the board. Requirement for this is that there is currently a live
     * cell in the top row.
     *
     * @return a <code>boolean</code> specifying weather the board meets the
     * requirements for expansion.
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
     * Expand the board with a row of dead cells at the top of the board.
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

    /**
     * Check the game board to see if it meets requirements for expansion on the
     * right side of the board. Requirement for this is that there is a living
     * cell on the right most column of the game board.
     *
     * @return a <code>boolean</code> specifying weather the board meets the
     * requirements for expansion.
     */
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

    /**
     * Expand the board with a column of dead cells on the right most side of
     * the board.
     */
    private void expandEast() {
        byte[][] newBoard = new byte[currentBoard.length][currentBoard[0].length + 1];
        for (int row = 0; row < currentBoard.length; row++) {
            for (int col = 0; col < currentBoard[0].length; col++) {
                newBoard[row][col] = currentBoard[row][col];
            }
        }
        currentBoard = newBoard;
    }

    /**
     * Check the game board to see if it meets requirements for expansion at the
     * bottom of the board. Requirement for this is that there is a living cell
     * in the bottom row of the board.
     *
     * @return a <code>boolean</code> specifying weather the board meets the
     * requirements for expansion.
     */
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

    /**
     * Expand the board with a row of dead cells at the bottom of the board.
     */
    private void expandSouth() {
        byte[][] newBoard = new byte[currentBoard.length + 1][currentBoard[0].length];
        for (int row = 0; row < currentBoard.length; row++) {
            for (int col = 0; col < currentBoard[0].length; col++) {
                newBoard[row][col] = currentBoard[row][col];
            }
        }
        currentBoard = newBoard;
    }

    /**
     * Check the game board to see if it meets requirements for expansion on the
     * left most column of the board. Requirement for this is that there is a
     * living cell in right most column.
     *
     * @return a <code>boolean</code> specifying weather the board meets the
     * requirements for expansion.
     */
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

    /**
     * Expand the board with a column of dead cells on the right side of the
     * board.
     */
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
     * Counts the number of living neighbour for a specified cell.
     *
     * @param board a <code>byte[][]</code> representing the board to have
     * neighbours counted.
     * @param row an <code>int</code> specifying the row location of the cell to
     * have its neighbours counted.
     * @param col an <code>int</code> specifying the column location of the cell
     * to have its neighbours counted.
     * @return an <code>int</code> specifying the number of living neighbours
     * the specified cell has.
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
     * Toggles the state of a specified cell in the current board. Live cell
     * becomes dead, dead cell becomes alive.
     *
     * @param row an <code>int</code> specifying the row position of the cell to
     * toggle.
     * @param col an <code>int</code> specifying the column position of the cell
     * to toggle.
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
     * Sets the state of a specified cell on the current board to alive.
     *
     * @param row an <code>int</code> specifying the row position of the cell to
     * make alive.
     * @param col an <code>int</code> specifying the column position of the cell
     * to make alive.
     */
    public void setCellStateAlive(int row, int col) {
        if (currentBoard[row][col] != 1) {
            currentBoard[row][col] = 1;
            livingCells++;
        }
    }

    /**
     * Reverts the current board back to its starting state. Also resets the
     * generation count and living cell count.
     */
    public void resetBoard() {
        currentBoard = duplicateBoard(originalBoard);
        generationCount = 0;
        countLivingCells();
    }

    /**
     * Creates a copy of the passed game board array.
     *
     * @param original a <code>byte[][]</code> that you want copied.
     * @return a <code>byte[][]</code> copy.
     */
    private byte[][] duplicateBoard(byte[][] original) {
        byte[][] boardCopy = new byte[original.length][original[0].length];

        for (int row = 0; row < original.length; row++) {
            for (int col = 0; col < original[0].length; col++) {
                boardCopy[row][col] = original[row][col];
            }
        }
        return boardCopy;
    }

    /**
     * Each row of the game board is concatenated into a single string.
     * Primarily meant for testing purposes.
     *
     * @return a <code>String</code> representing the current board state.
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
