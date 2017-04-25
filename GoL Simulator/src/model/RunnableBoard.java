package model;

import java.util.ArrayList;

/**
 *
 * @author peven
 */
public class RunnableBoard implements Runnable {

    private final byte DEAD = 0;
    private final byte ALLIVE = 1;
    private final byte CHANGED = 1;
    private ArrayList<ArrayList<Byte>> testPattern;
    private ArrayList<ArrayList<Byte>> currentBoard;
    private ArrayList<ArrayList<Byte>> changedCells;
    private BoardDynamic b;
    private Rules rules = Rules.getInstance();
    private int startCol;
    private int endCol;

    @Override
    public void run() {
        System.err.println("Inni run\n");

        for (int row = 0; row < testPattern.size(); row++) {
            //System.out.println("Inni rad" + row);
            //System.out.println("Inni col ");
            for (int col = startCol; col < endCol; col++) {
                //System.out.print(col);
                int nrOfNeighbours = countNeighbours(testPattern, row, col);

                if (testPattern.get(row).get(col) == 1 && !rules.getSurviveRules().contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, DEAD);
                    changedCells.get(row).set(col, CHANGED);
                } else if (testPattern.get(row).get(col) == 0 && rules.getBirthRules().contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, ALLIVE);
                    changedCells.get(row).set(col, CHANGED);
                }
            }
            //System.out.println();
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

    public RunnableBoard(int startCol, int endCol, ArrayList<ArrayList<Byte>> testPattern, ArrayList<ArrayList<Byte>> currentBoard, ArrayList<ArrayList<Byte>> changedCells, BoardDynamic b) {
        this.startCol = startCol;
        this.endCol = endCol;
        this.testPattern = testPattern;
        this.currentBoard = currentBoard;
        this.changedCells = changedCells;

    }
}
