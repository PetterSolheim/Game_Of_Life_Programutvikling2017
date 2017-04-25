/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author Even
 */
public class BoardSound extends BoardDynamic {

    private boolean cellAudio;
    private boolean generationAudio;

    public BoardSound(BoardDynamic b) {
        this.currentBoard = duplicateBoard(b.getBoard());
    }

    @Override
    public void nextGeneration() {
        // a copy of the board is used to test the rules, while changes are
        // applied to the actual board.
        ArrayList<ArrayList<Byte>> testPattern = duplicateBoard(currentBoard);

        // iterate through the board cells, count number of neighbours for each
        // cell, and apply changes based on the ruleset.
        for (int row = 0; row < testPattern.size(); row++) {
            for (int col = 0; col < testPattern.get(0).size(); col++) {
                int nrOfNeighbours = countNeighbours(testPattern, row, col);
                if (cellAudio) {
                    // make nosie
                }
                if (testPattern.get(row).get(col) == 1 && !rules.getSurviveRules().contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, DEAD);
                    livingCells--;
                } else if (testPattern.get(row).get(col) == 0 && rules.getBirthRules().contains(nrOfNeighbours)) {
                    currentBoard.get(row).set(col, ALLIVE);
                    livingCells++;
                }
            }
        }
        if (generationAudio) {
            // make noise
        }
    }
}
