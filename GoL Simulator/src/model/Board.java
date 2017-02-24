package model;

/**
 * Class for the game board. Contains the actual board and the methods for
 * iterating to the next generation, along with game rules for the board. The
 * class is a Singleton.
 *
 */
public class Board {

    private static Board instance;
    private byte[][] currentBoard;
    private byte[][] originalBoard;
    private int generationCount = 0;
    private int cellCount = 0;
    private int minToSurvive = 2;
    private int maxToSurvive = 3;
    private int birth = 3;

    /**
     * As this is a singleton, the constructor is private. Default start board
     * is a blank board of 50 rows and 50 columns.
     */
    private Board() {
        currentBoard = new byte[100][100];
        originalBoard = duplicateBoard(currentBoard);
    }

    /**
     * Method for acquiring a reference to the Board object.
     *
     * @return A reference to the Board object.
     */
    public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }

    /**
     * Gets the current Board.
     *
     * @return The game board.
     */
    public byte[][] getBoard() {
        return currentBoard;
    }
    
    public int getHeight() {
        return currentBoard.length;
    }
    
    public int getWidth() {
        return currentBoard[0].length;
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

    /**
     * Allows new rules to be set. Default is 23/3.
     *
     * @param min The minimum number of live neighbours for current cell to
     * survive.
     * @param max The maximum number of live neighbours for the current cell to
     * survive.
     * @param birth The number of live neighbours a dead cell must have for it
     * to become a live cell.
     */
    public void setRules(int min, int max, int birth) {
        if (min >= 0 && min <= 8 && max >= 0 && max <= 8 && birth >= 0 && birth <= 8) {
            minToSurvive = min;
            maxToSurvive = max;
            this.birth = birth;
        } else {
            throw new IllegalArgumentException("Values entered must be no lower"
                    + " than 0, and no higher than 8!");

        }
    }

    /**
     * Iterates the board to the next generation.
     */
    public void nextGeneration() {
        // a copy of the board is used to test the rules, while changes are
        // applied to the actual board.
        byte[][] testPattern = duplicateBoard(currentBoard);


        // iterates through the board cells, count number of neighbours for each
        // cell, and apply changes based on the ruleset.
        for (int i = 0; i < testPattern.length; i++) {
            for (int j = 0; j < testPattern[0].length; j++) {
                int neighbours = countNeighbours(testPattern, i, j);

                if (neighbours < minToSurvive || neighbours > maxToSurvive) {
                    currentBoard[i][j] = 0;
                }

                if (neighbours == birth) {
                    currentBoard[i][j] = 1;
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
        int xLastIndex = cell.length - 1;
        int yLastIndex = cell[0].length - 1;
        
        if (y+1 <= yLastIndex) {
            neighbours += cell[x][y+1];
        }

        if (x-1 >= 0 && y+1 <= yLastIndex) {
            neighbours += cell[x-1][y+1];
        }

        if (x-1 >= 0) {
            neighbours += cell[x-1][y];
        }

        if (x-1 >= 0 && y-1 >= 0) {
            neighbours += cell[x-1][y-1];
        }

        if (y-1 >= 0) {
            neighbours += cell[x][y-1];
        }

        if (x+1 <= xLastIndex && y-1 >= 0) {
            neighbours += cell[x+1][y-1];
        }

        if (x+1 <= xLastIndex) {
            neighbours += cell[x+1][y];
        }

        if (x+1 <= xLastIndex && y+1 <= yLastIndex) {
            neighbours += cell[x+1][y+1];
        }

        return neighbours;
    }
    
    /**
     * Method which toggles the state of a specified cell (if it is alive, it
     * becomes dead, if it is dead, it becomes alive).
     * @param x
     * @param y 
     */
    public void toggleCellState(int x, int y) {
        if(currentBoard[y][x] == 1)
            currentBoard[y][x] = 0;
        else
            currentBoard[y][x] = 1;
    }

    /**
     *
     * @return The lowest number of live neighbours needed for a cell to
     * survive.
     */
    public int getMinToSurvive() {
        return minToSurvive;
    }

    /**
     *
     * @return The highest number of live neighbours before a cell dies.
     */
    public int getMaxToSurvive() {
        return maxToSurvive;
    }

    /**
     *
     * @return The number of live neighbours a dead cell needs to become alive.
     */
    public int getBirth() {
        return birth;
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
