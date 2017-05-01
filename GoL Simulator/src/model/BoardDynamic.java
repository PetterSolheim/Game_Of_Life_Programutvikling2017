package model;

import java.util.ArrayList;

/**
 * This class contains the game board and its mechanics, such as moving a game
 * board to its next generation. This class replaces the now deprecated
 * {@link model.Board} class.
 *
 */
public class BoardDynamic {

    /**
     * Represents the game board in its current game state. 1 represents live
     * cells, 0 represents dead cells.
     */
    protected ArrayList<ArrayList<Byte>> currentBoard;

    /**
     * Used to represent cells which have changed during the last generation
     * shift.
     */
    private ArrayList<ArrayList<Byte>> changedCells;

    /**
     * Used to store the game board as it was before the game started. Allows
     * for resetting the game board.
     */
    protected ArrayList<ArrayList<Byte>> originalBoard;

    /**
     * Used to reset the generation count when the board is reset.
     */
    private int oldGenerationCount;
    
    private int generationCount = 0;

    protected int livingCells = 0;
    protected final byte DEAD = 0;
    protected final byte ALLIVE = 1;
    protected final byte CHANGED = 1;
    private float indexSum;

    private Rules rules = Rules.getInstance();
    private String boardAuthor = "";
    private String boardName = "";
    private String boardComment = "";
    


    /**
     * Board no-argument constructor initializes a game board consisting of 200
     * x 200 dead cells.
     */
    public BoardDynamic() {
        originalBoard = createEmptyBoard(200, 200);
        currentBoard = duplicateBoard(originalBoard);
        changedCells = createEmptyBoard(200, 200);
    }

    /**
     * Board constructor. Accepts an <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>
     * representing a board which is used as the starting board for the
     * DynamicBoard object.
     * @param board an <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>
     * representing the starting board.
     */
    public BoardDynamic(ArrayList<ArrayList<Byte>> board) {
        originalBoard = duplicateBoard(board);
        currentBoard = duplicateBoard(board);
        changedCells = createEmptyBoard(board.size(), board.get(0).size());
    }

    /**
     * Board constructor. Allows one to define the starting size of the game
     * board.
     *
     * @param row the number of rows for the starting board.
     * @param col the number of columns for the starting board.
     * @throws IllegalArgumentException in the case that either the number of
     * rows of columns are defined to be bellow 1.
     */
    public BoardDynamic(int row, int col) {
        if (row < 1 || col < 1) {
            throw new IllegalArgumentException("Number of rows and columns must"
                    + "be higher than 0!");
        }
        originalBoard = createEmptyBoard(row, col);
        currentBoard = duplicateBoard(originalBoard);
        changedCells = createEmptyBoard(row, col);
    }
    
    /**
     * Set the metadata for the board. Three Strings expected.
     * @param author a <code>String</code> specifying the author of the board.
     * @param name a <code>String</code> specifying the name of the board.
     * @param comment a <code>String</code> specifying the comments for the board.
     */
    public void setMetadata(String author, String name, String comment) {
        boardAuthor = author;
        boardName = name;
        boardComment = comment;
    }
    
    /**
     * Gets the name of the boards author.
     * @return a <code>String</code> specifying the boards author.
     */
    public String getAuthor() {
        return boardAuthor;
    }
    
    /**
     * Gets the name of the board.
     * @return a <code>String</code> specifying the boards name.
     */
    public String getName() {
        return boardName;
    }
    
    /**
     * Gets the boards comments.
     * @return a <code>String</code> specifying the boards comments.
     */
    public String getComment() {
        return boardComment;
    }
    
    /**
     * Gets the the number of rows on the current board.
     * @return an <code>int</code> specifying the number of rows on the current
     * board.
     */
    public int getRows() {
        return currentBoard.size();
    }
    
    /**
     * Gets the number of columns on the current board.
     * @return an <code>int</code> specifying the number of columns on the
     * current board.
     */
    public int getCols() {
        return currentBoard.get(0).size();
    }

    /**
     * Sets a new game board.
     *
     * @param newBoard the new game board.
     */
    public void setBoard(ArrayList<ArrayList<Byte>> newBoard) {
        originalBoard = duplicateBoard(newBoard);
        currentBoard = duplicateBoard(newBoard);
        countLivingCells();
    }

    /**
     * Sets a new game board.
     *
     * @param newBoard the new game board.
     */
    public void setBoard(byte[][] newBoard) {
        originalBoard = createEmptyBoard(newBoard.length, newBoard[0].length);

        for (int row = 0; row < newBoard.length; row++) {
            for (int col = 0; col < newBoard[0].length; col++) {
                originalBoard.get(row).set(col, newBoard[row][col]);
            }
        }
        currentBoard = duplicateBoard(originalBoard);
        countLivingCells();
    }

    /**
     * Gets the current board.
     *
     * @return a <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>
     * representing the current game board.
     */
    public ArrayList<ArrayList<Byte>> getBoard() {
        return currentBoard;
    }

    /**
     * Gets a list of all the cells which changed their state during the last
     * generation shift.
     *
     * @return a <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>
     * representing the cells which have changed where 1 means changed and 0
     * means no change.
     */
    public ArrayList<ArrayList<Byte>> getChangedCells() {
        return changedCells;
    }

    /**
     * Updates the livingCells variable to reflect the number of living cells on
     * the current board.
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

    public void moveBoardWithArrowKeys(int xAxis, int yAxis) {
        ArrayList<ArrayList<Byte>> newBoard = createEmptyBoard(currentBoard.size(), currentBoard.get(0).size());
        for (int row = 0; row < currentBoard.size(); row++) {
            for (int col = 0; col < currentBoard.get(row).size(); col++) {
                if (currentBoard.get(row).get(col) == 1) {
                    //check if new coordinates is within the bounds of the board
                    if ((row + yAxis < currentBoard.size()) && (row + yAxis > - 1) && (col + xAxis >  - 1) && (col + xAxis < currentBoard.get(row).size())) {
                        newBoard.get(row + yAxis).set(col + xAxis, ALLIVE);
                    } else {
                        return;
                    }
                }
            }
        }
        currentBoard = duplicateBoard(newBoard);
    }

    /**
     * Gets the number of cells on the current board, both living and dead.
     *
     * @return an <code>int</code> specifying the number of cells, both living
     * and dead, on the current board.
     */
    public int getNumberOfCells() {
        return currentBoard.size() * currentBoard.get(0).size();
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
     * @return a deep copy of the <code>BoardDynamic</code> object.
     */
    public BoardDynamic deepCopy() {
        BoardDynamic b = new BoardDynamic();
        b.currentBoard = duplicateBoard(this.currentBoard);
        b.originalBoard = duplicateBoard(this.originalBoard);
        b.generationCount = this.generationCount;
        b.countLivingCells();
        b.indexSum = 0;
        return b;
    }

    public float getIndexSum() {
        float sum = indexSum;
        indexSum = 0;
        return sum;
    }

    /**
     * Iterates the current board to its next generation, playing by the rules
     * defined in the Rules class object.
     *
     * @see model.Rules
     */
    public void nextGeneration() {
        // reset list of changed cells.
        changedCells = createEmptyBoard(currentBoard.size(), currentBoard.get(0).size());

        if (rules.isDynamic() && getNumberOfCells() < rules.getMaxNumberOfCells()) {
            expandBoardIfNeeded();
        }

        // a copy of the board is used to test the rules, while changes are
        // applied to the actual board.
        ArrayList<ArrayList<Byte>> testPattern = duplicateBoard(currentBoard);

        // iterate through the board cells, count number of neighbours for each
        // cell, and apply changes based on the ruleset.
        for (int row = 0; row < testPattern.size(); row++) {
            for (int col = 0; col < testPattern.get(0).size(); col++) {
                int nrOfNeighbours = countNeighbours(testPattern, row, col);

                if (testPattern.get(row).get(col) == 1 && !rules.getSurviveRules().contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, DEAD);
                    changedCells.get(row).set(col, CHANGED);
                    livingCells--;
                } else if (testPattern.get(row).get(col) == 1 && rules.getSurviveRules().contains(nrOfNeighbours)) {
                    indexSum += ((row * 0.2) + (col * 0.7));
                }
                else if (testPattern.get(row).get(col) == 0 && rules.getBirthRules().contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, ALLIVE);
                    changedCells.get(row).set(col, CHANGED);
                    livingCells++;
                    indexSum += ((row * 0.2) + (col * 0.7));
                }
            }
            
            
        }
        generationCount++;
    }

    /**
     * Checks the current board to see if it should be expanded. Requirement for
     * expansion is if a living cells is touching one of the current boards
     * borders.
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
            changedCells = createEmptyBoard(currentBoard.size(), currentBoard.get(0).size());
            for (int row = 0; row < changedCells.size(); row++) {
                for (int col = 0; col < changedCells.get(0).size(); col++) {
                    changedCells.get(row).set(col, CHANGED);
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
        for (int i = 0; i < currentBoard.get(0).size(); i++) {
            numberOfLiveCells += currentBoard.get(0).get(i);
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
        currentBoard.add(0, new ArrayList<>());
        for (int i = 0; i < currentBoard.get(1).size(); i++) {
            currentBoard.get(0).add(DEAD);
        }
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
        for (int i = 0; i < currentBoard.size(); i++) {
            numberOfLiveCells += currentBoard.get(i).get(0);
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
        for (int i = 0; i < currentBoard.size(); i++) {
            currentBoard.get(i).add(0, DEAD);
        }
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
        for (int i = 0; i < currentBoard.get(currentBoard.size() - 1).size(); i++) {
            numberOfLiveCells += currentBoard.get(currentBoard.size() - 1).get(i);
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
        currentBoard.add(new ArrayList<>());
        for (int i = 0; i < currentBoard.get(0).size(); i++) {
            currentBoard.get(currentBoard.size() - 1).add(DEAD);
        }
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
        for (int i = 0; i < currentBoard.size(); i++) {
            numberOfLiveCells += currentBoard.get(i).get(currentBoard.get(i).size() - 1);
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
        for (int i = 0; i < currentBoard.size(); i++) {
            currentBoard.get(i).add(DEAD);
        }
    }

    /**
     * Counts the number of living neighbour cells for a specified cell.
     *
     * @param board the game board containing the cell to have neighbours its
     * counted.
     * @param row the row location of the cell to have its neighbours counted.
     * @param col the column location of the cell to have its neighbours
     * counted.
     * @return an <code>int</code> specifying the number of living neighbours.
     */
    protected int countNeighbours(ArrayList<ArrayList<Byte>> board, int row, int col) {
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
     * Toggles the state of a specified cell in the current board. Live cell
     * becomes dead, dead cell becomes alive.
     *
     * @param row the row position of the cell to toggle.
     * @param col the column position of the cell to toggle.
     */
    public void toggleCellState(int row, int col) {
        if (currentBoard.get(row).get(col) == 1) {
            livingCells--;
            currentBoard.get(row).set(col, DEAD);
        } else {
            currentBoard.get(row).set(col, ALLIVE);
            livingCells++;
        }
    }

    /**
     * Sets the state of a specified cell on the current board to alive.
     *
     * @param row the row position of the cell to make alive.
     * @param col the column position of the cell to make alive.
     */
    public void setCellStateAlive(int row, int col) {
        if (currentBoard.get(row).get(col) != 1) {
            currentBoard.get(row).set(col, ALLIVE);
            livingCells++;
        }
    }

    /**
     * Reverts the current board back to its starting state. Also resets the
     * generation count and living cell count.
     */
    public void resetBoard() {
        currentBoard = duplicateBoard(originalBoard);
        generationCount = oldGenerationCount;
        countLivingCells();
    }
    /**
     * Creates an empty board with dimensions of the previously active board. Also sets the
     * generation count and living cell count to 0.
     */
    public void deleteBoard (){
        generationCount = 0;
        livingCells = 0;
        currentBoard = createEmptyBoard(originalBoard.size(), originalBoard.get(0).size());
        originalBoard = createEmptyBoard(currentBoard.size(), currentBoard.get(0).size());
    }
    /**
     * Preserves the active board in the originalBoard variable
     * so that the user can reset their board.
     * This method is called if the user presses the play button while the timer is paused.
     */
    public void preserveBoard (){
        oldGenerationCount = generationCount;
        originalBoard = duplicateBoard(currentBoard);
    }
    /**
     * Gets a copy of the passed game board ArrayList.
     *
     * @param original the board that you want to copy.
     * @return a <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code> copy.
     */
    protected ArrayList<ArrayList<Byte>> duplicateBoard(ArrayList<ArrayList<Byte>> original) {
        ArrayList<ArrayList<Byte>> boardCopy = new ArrayList<>();
        for (int row = 0; row < original.size(); row++) {
            boardCopy.add(row, new ArrayList<Byte>());
            for (int col = 0; col < original.get(0).size(); col++) {
                boardCopy.get(row).add(original.get(row).get(col));
            }
        }
        return boardCopy;
    }

    private ArrayList<ArrayList<Byte>> createEmptyBoard(int row, int col) {
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
