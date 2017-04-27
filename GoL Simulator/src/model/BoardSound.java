/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.concurrent.Delayed;

/**
 *
 * @author Even
 */
public class BoardSound extends BoardDynamic implements Runnable {

    private boolean cellAudio;
    private boolean generationAudio;
    private boolean isActive;
    private boolean createNextGeneration;
    private boolean playCellAudio;
    private int nrOfCells;
    private AudioManager audioManager = AudioManager.getSingelton();

    public BoardSound(BoardDynamic b) {
        cellAudio = false;
        this.currentBoard = duplicateBoard(b.getBoard());
        this.originalBoard = duplicateBoard(originalBoard);
        cellAudio = false;
        generationAudio = false;
        setNumbeOfCells();
    }

    @Override
    public void run() {
        while (isActive) {
            if (createNextGeneration) {
                nextGeneration();
            }
        }
    }

    public enum CellState {
        DEAD, ALIVE
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
                if (!isActive) {
                    return;
                }
                int nrOfNeighbours = countNeighbours(testPattern, row, col);
                if (testPattern.get(row).get(col) == 1) {
                    if (!rules.getSurviveRules().contains(nrOfNeighbours)) {
                        if (cellAudio) {
                            while (!playCellAudio) {
                                System.out.println("Waiting ");
                            }
                            audioManager.cellAudio(CellState.DEAD);
                            currentBoard.get(row).set(col, DEAD);
                            livingCells--;
                            playCellAudio = false;
                        }
                    } else {
                        if (cellAudio) {
                            audioManager.cellAudio(CellState.ALIVE);
                        }
                    }
                    currentBoard.get(row).set(col, DEAD);
                    livingCells--;
                } else if (testPattern.get(row).get(col) == 0) {
                    if (rules.getBirthRules().contains(nrOfNeighbours)) {
                        if (cellAudio) {
                            audioManager.cellAudio(CellState.ALIVE);
                        }
                        currentBoard.get(row).set(col, ALLIVE);
                        livingCells++;
                    } else {
                        if (cellAudio) {
                            while(!controller.pl){
                            
                            }
                            audioManager.cellAudio(CellState.DEAD);
                            playCellAudio = false;
                        }
                    }
                }
            }
        }
        if (generationAudio) {
            audioManager.generationAudio();
        }
    }

    public void setGenerationAudio(boolean generationAudio) {
        this.generationAudio = generationAudio;
    }

    public void setCellAudio(boolean cellAudio) {
        this.cellAudio = cellAudio;
    }

    public void setCreateNextGeneration(boolean createNextGeneration) {
        this.createNextGeneration = createNextGeneration;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setPlayCellAudio(boolean playCellAudio) {
        this.playCellAudio = playCellAudio;
        System.out.println("Play cell audio changed in board");
    }

    private void setNumbeOfCells() {
        int cells = 0;
        for (int row = 0; row < currentBoard.size(); row++) {
            cells += currentBoard.get(row).size();
        }
        this.nrOfCells = cells;
        System.out.println("Number of cells " + nrOfCells);
    }

    public int getNrOfCells() {
        return this.nrOfCells;
    }
}
