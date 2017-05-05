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
    private ArrayList<ArrayList<Byte>> currentBoard;

    /**
     * Used to represent cells which have changed during the last generation
     * shift.
     */
    private ArrayList<ArrayList<Byte>> changedCells;

    /**
     * Used to store the game board as it was before the game started. Allows
     * for resetting the game board.
     */
    private ArrayList<ArrayList<Byte>> originalBoard;

    /**
     * Used when generating the next generation.
     */
    private ArrayList<ArrayList<Byte>> nextGeneration;

    private int generationCount = 0;

    private int livingCells = 0;
    private final byte DEAD = 0;
    private final byte ALLIVE = 1;
    private final byte CHANGED = 1;

    private Rules rules = Rules.getInstance();

    /**
     * Specifies if board has expanded.
     */
    private boolean expandedNorth, expandedWest, boardExpanded;

    /**
     * Board metadata
     */
    private String boardAuthor = "";
    private String boardName = "";
    private String boardComment = "";

    /**
     * Used for threading
     */
    private int numWorkers = Runtime.getRuntime().availableProcessors();
    private ArrayList<Thread> workers = new ArrayList<Thread>();

    /**
     * Following ArrayLists are used to calculate the sum of the indexes to all
     * living cells.
     */
    private ArrayList<Integer> rows = new ArrayList<Integer>();
    private ArrayList<Integer> cols = new ArrayList<Integer>();

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
     * Board constructor. Accepts an
     * <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code> representing a board
     * which is used as the starting board for the DynamicBoard object.
     *
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
     * @param row an <code>int</code> specifying the number of rows for the
     * starting board.
     * @param col an <code>int</code> specifying the number of columns for the
     * starting board.
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
     * Set the metadata for the board. Three Strings expected. Pass an empty
     * string if no value is to be set for one of the three pieces of metadata.
     *
     * @param author a <code>String</code> specifying the author of the board.
     * @param name a <code>String</code> specifying the name of the board.
     * @param comment a <code>String</code> specifying the comments for the
     * board.
     */
    public void setMetadata(String author, String name, String comment) {
        boardAuthor = author;
        boardName = name;
        boardComment = comment;
    }

    /**
     * Gets the name of the boards author.
     *
     * @return a <code>String</code> specifying the boards author.
     */
    public String getAuthor() {
        return boardAuthor;
    }

    /**
     * Gets the name of the board.
     *
     * @return a <code>String</code> specifying the boards name.
     */
    public String getName() {
        return boardName;
    }

    /**
     * Gets the boards comments.
     *
     * @return a <code>String</code> specifying the boards comments.
     */
    public String getComment() {
        return boardComment;
    }

    /**
     * Gets the the number of rows on the current board.
     *
     * @return an <code>int</code> specifying the number of rows on the current
     * board.
     */
    public int getRows() {
        return currentBoard.size();
    }

    /**
     * Gets the number of columns on the current board.
     *
     * @return an <code>int</code> specifying the number of columns on the
     * current board.
     */
    public int getCols() {
        return currentBoard.get(0).size();
    }

    /**
     * Sets a new game board.
     *
     * @param newBoard an <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>the
     * representing the new game board.
     */
    public void setBoard(ArrayList<ArrayList<Byte>> newBoard) {
        originalBoard = duplicateBoard(newBoard);
        currentBoard = duplicateBoard(newBoard);
        countLivingCells();
    }

    /**
     * Sets a new game board.
     *
     * @param newBoard a <code>byte[][]</code> representing the new game board.
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

    /**
     * Shifts all the living cells on the board in a given direction.
     *
     * @param xAxis an <code>int</code> specifying the number of rows to shift
     * the board.
     * @param yAxis an <code>int</code> specifying the number of cols to shift
     * the board.
     */
    public void moveBoardWithArrowKeys(int xAxis, int yAxis) {
        ArrayList<ArrayList<Byte>> newBoard = createEmptyBoard(currentBoard.size(), currentBoard.get(0).size());
        for (int row = 0; row < currentBoard.size(); row++) {
            for (int col = 0; col < currentBoard.get(row).size(); col++) {
                if (currentBoard.get(row).get(col) == 1) {
                    //check if new coordinates is within the bounds of the board
                    if ((row + yAxis < currentBoard.size()) && (row + yAxis > - 1) && (col + xAxis > - 1) && (col + xAxis < currentBoard.get(row).size())) {
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
    public int getCellCount() {
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
        b.changedCells = duplicateBoard(this.changedCells);
        b.originalBoard = duplicateBoard(this.originalBoard);
        b.generationCount = this.generationCount;
        b.countLivingCells();
        return b;
    }
    
    /**
     * Used in <code>Statistics</code> to generate a similarity measure. 
     * @return <code>float</code> the sum of the index to all living cells.
     */
    public float getIndexSum() {
        float sum = 0;
        for (int i = 0; i < rows.size(); i++) {
            float row, col;
            row = rows.get(i);
            col = cols.get(i);
            if (i % 2 == 0) {
                    sum += (row + col) * row;
            } else {
                sum += (row + col)  * col; 
            }
        }
        rows.clear();
        cols.clear();
        return sum;
    }

    /**
     * Runs the non-threaded method nextGeneration() a specified number of
     * times, and prints to the console the time it took to complete.
     *
     * @param iterations an <code>int</code> specifying the number of iterations
     * for the test.
     */
    public void nextGenerationPrintPerformance(int iterations) {
        long totaltime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            nextGeneration();
            totaltime += System.currentTimeMillis() - start;
        }
        System.out.println("Single Thread: time to perform " + iterations + " iterations: " + totaltime);
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

        expandedNorth = false;
        expandedWest = false;
        boardExpanded = false;

        if (rules.isDynamic() && getCellCount() < rules.getMaxNumberOfCells()) {
            expandBoardIfNeeded();
        }

        // a copy of the board is used to test the rules, while changes are
        // applied to the actual board.
        nextGeneration = duplicateBoard(currentBoard);

        // iterate through the board cells, count number of neighbours for each
        // cell, and apply changes based on the ruleset.
        for (int row = 0; row < nextGeneration.size(); row++) {
            for (int col = 0; col < nextGeneration.get(0).size(); col++) {
                int nrOfNeighbours = countNeighbours(currentBoard, row, col);

                if (currentBoard.get(row).get(col) == 1 && !rules.getSurviveRules().contains(nrOfNeighbours)) {
                    nextGeneration.get(row).set(col, DEAD);
                    changedCells.get(row).set(col, CHANGED);
                    livingCells--;
                } else if (currentBoard.get(row).get(col) == 1 && rules.getSurviveRules().contains(nrOfNeighbours)) {
                    addToIndexSum2(row + 1, col + 1);
                } else if (currentBoard.get(row).get(col) == 0 && rules.getBirthRules().contains(nrOfNeighbours)) {
                    addToIndexSum2(row + 1, col + 1);
                    nextGeneration.get(row).set(col, ALLIVE);
                    changedCells.get(row).set(col, CHANGED);
                    livingCells++;
                }
            }
        }

        currentBoard = nextGeneration;
        generationCount++;
    }

    /**
     * Runs the threaded method nextGenerationConcurrent() a specified number of
     * times, and prints to the console the time it took to complete.
     *
     * @param iterations an <code>int</code> specifying the number of iterations
     * for the test.
     */
    public void nextGenerationConcurrentPrintPerformance(int iterations) {
        long totaltime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            nextGenerationConcurrent();
            totaltime += System.currentTimeMillis() - start;
        }
        System.out.println("Multi-Threaded: Time to perform " + iterations + " iterations: " + totaltime);
    }

    /**
     * Iterates the current board to its next generation, playing by the rules
     * defined in the Rules class object. This method uses threads to improve
     * performance.
     *
     * @see model.Rules
     */
    public void nextGenerationConcurrent() {

        // reset values
        changedCells = createEmptyBoard(currentBoard.size(), currentBoard.get(0).size());
        expandedNorth = false;
        expandedWest = false;
        boardExpanded = false;

        // determin if board should expand
        if (rules.isDynamic() && getCellCount() < rules.getMaxNumberOfCells()) {
            expandBoardIfNeeded();
        }

        nextGeneration = duplicateBoard(currentBoard);

        // create threads and assign them their task.
        createNextGenerationWorkers();

        // if a thread gets interupted during execution, roll back changes made 
        // during this generational shift.
        try {
            runNextGenerationWorkers();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        currentBoard = nextGeneration;
        workers.clear(); // clear workers
        generationCount++;
    }

    /**
     * Creates a number of workers equal to the number of threads on the current
     * system.
     */
    private void createNextGenerationWorkers() {
        for (int i = 0; i < numWorkers; i++) {
            int workerNr = i;
            workers.add(new Thread(() -> {
                partialNextGeneration(workerNr);
            }));
        }
    }

    /**
     * Starts the threads, and then waits for them to all finish.
     *
     * @throws InterruptedException
     */
    private void runNextGenerationWorkers() throws InterruptedException {
        for (Thread t : workers) {
            t.start();
        }

        for (Thread t : workers) {

            t.join();
        }
    }

    /**
     * Assigns a worker its section of the game board for processing. its
     * section is determined by its worker number, and the total number of
     * workers.
     *
     * @param workerNr an <code>int</code> specifying a worker id number.
     */
    private void partialNextGeneration(int workerNr) {
        int startCol = (nextGeneration.get(0).size() / numWorkers) * workerNr;
        int endCol;
        if (workerNr + 1 == numWorkers) {
            endCol = nextGeneration.get(0).size() - 1;
        } else {
            endCol = ((nextGeneration.get(0).size() / numWorkers) * (workerNr + 1)) - 1;
        }

        for (int row = 0; row < nextGeneration.size(); row++) {
            for (int col = startCol; col <= endCol; col++) {
                int nrOfNeighbours = countNeighbours(currentBoard, row, col);
                if (currentBoard.get(row).get(col) == 1 && !rules.getSurviveRules().contains(nrOfNeighbours)) {
                    nextGeneration.get(row).set(col, DEAD);
                    changedCells.get(row).set(col, CHANGED);
                    addToLivingCells(-1);
                } else if (currentBoard.get(row).get(col) == 0 && rules.getBirthRules().contains(nrOfNeighbours)) {
                    nextGeneration.get(row).set(col, ALLIVE);
                    changedCells.get(row).set(col, CHANGED);
                    addToLivingCells(+1);
                }
            }
        }
    }

    /**
     *
     * Allows for adjusting the number of living cells. Pass positive values to
     * increase, negative to decrease.
     *
     * @param i an <code>int</code> specifying the amount to increase or deduct
     * the value by.
     *
     */
    private synchronized void addToLivingCells(int i) {
        livingCells += i;
    }

    private synchronized void addToIndexSum2(int row, int col) {
        rows.add(row);
        cols.add(col);
    }

    /**
     * Checks the current board to see if it should be expanded. Requirement for
     * expansion is if a living cell is touching one of the current boards
     * borders.
     */
    private void expandBoardIfNeeded() {
        if (shouldExpandNorth()) {
            expandNorth();
            expandedNorth = true;
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
            expandedWest = true;
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
     * Check if the board expanded on the north side. Can be used to determine
     * if one should alter the offset used when drawing the board.
     *
     * @return a <code>boolean</code> specifying if the board expanded north.
     */
    public boolean expandedNorth() {
        return expandedNorth;
    }

    /**
     * Check if the board expanded on the west side. Can be used to determine if
     * one should alter the offset used when drawing the board.
     *
     * @return a <code>boolean</code> specifying if the board expanded west.
     */
    public boolean expandedWest() {
        return expandedWest;
    }

    /**
     * Check if the board expanded on any of its sides.
     *
     * @return a <code>boolean</code> specifying weather or not the board
     * expanded.
     */
    public boolean didExpand() {
        return boardExpanded;
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
     * cell on the left most column of the game board.
     *
     * @return a <code>boolean</code> specifying weather the board meets the
     * requirements for expansion.
     */
    private boolean shouldExpandWest() {
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
     * Expand the board with a column of dead cells on the left most side of the
     * board.
     */
    private void expandWest() {
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
    private boolean shouldExpandEast() {
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
    private void expandEast() {
        for (int i = 0; i < currentBoard.size(); i++) {
            currentBoard.get(i).add(DEAD);
        }
    }

    /**
     * Counts the number of living neighbour cells for a specified cell.
     *
     * @param board the game board containing the cell to have its neighbours
     * counted.
     * @param row the row location of the cell to have its neighbours counted.
     * @param col the column location of the cell to have its neighbours
     * counted.
     * @return an <code>int</code> specifying the number of living neighbours.
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
     * Toggles the state of a specified cell in the current board. Live cell
     * becomes dead, dead cell becomes alive.
     *
     * @param row an <code>int</code> specifying the row position of the cell to
     * toggle.
     * @param col an <code>int</code> specifying the column position of the cell
     * to toggle.
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
     * @param row an <code>int</code> specifying the row position of the cell to
     * make alive.
     * @param col an <code>int</code> specifying the column position of the cell
     * to make alive.
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
        generationCount = 0;
        countLivingCells();
    }

    /**
     * Replaces the current board with a new, empty board, of the same
     * dimensions, effectively clearing it.
     */
    public void clearBoard() {
        generationCount = 0;
        livingCells = 0;
        currentBoard = createEmptyBoard(originalBoard.size(), originalBoard.get(0).size());
        originalBoard = createEmptyBoard(currentBoard.size(), currentBoard.get(0).size());
    }

    /**
     * Stores the current board in the originalBoard variable.
     */
    public void preserveBoard() {
        originalBoard = duplicateBoard(currentBoard);
    }

    /**
     * Creates a copy of the passed game board ArrayList
     * <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>.
     *
     * @param original the <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>
     * you want copied.
     * @return a <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code> copy.
     */
    private ArrayList<ArrayList<Byte>> duplicateBoard(ArrayList<ArrayList<Byte>> original) {
        ArrayList<ArrayList<Byte>> boardCopy = new ArrayList<>();
        for (int row = 0; row < original.size(); row++) {
            boardCopy.add(row, new ArrayList<Byte>());
            for (int col = 0; col < original.get(0).size(); col++) {
                boardCopy.get(row).add(original.get(row).get(col));
            }
        }
        return boardCopy;
    }

    /**
     * Creates an <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>. where all
     * values are set to 0.
     *
     * @param row an <code>int</code> specifying the number of rows the list should have.
     * @param col an <code>int</code> specifying the number of columns the list should have.
     * @return the finished <code>ArrayList&lt;ArrayList&lt;Byte&gt;&gt;</code>.
     */
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

    /**
     * Each row of the game board is concatenated into a single string.
     * Primarily meant for testing purposes.
     *
     * @return a <code>String</code> representing the current board state.
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
