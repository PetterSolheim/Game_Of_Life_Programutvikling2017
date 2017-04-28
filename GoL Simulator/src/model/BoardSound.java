/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import view.DialogBoxes;

/**
 *
 * @author Even
 */
public class BoardSound extends BoardDynamic implements Runnable {

    private boolean cellAudio;
    private boolean generationAudio;
    private boolean isActive;
    private boolean createNextGeneration;
    private int nrOfCells;
    private Synthesizer synthesizer;
    static MidiChannel midiChannels[];
    static Instrument instruments[];
    private long audioLength;

    public BoardSound(BoardDynamic b) {
        isActive = true;
        try {
            synthesizer = MidiSystem.getSynthesizer();
            midiChannels = synthesizer.getChannels();
            synthesizer.open();
            instruments = synthesizer.getAvailableInstruments();
        } catch (MidiUnavailableException exception) {
            DialogBoxes.genericErrorMessage("No midi devices available", "Representing the board with sound is not supported on this device.\n" + exception.getMessage());
        }
        cellAudio = false;
        this.currentBoard = duplicateBoard(b.getBoard());
        this.originalBoard = duplicateBoard(originalBoard);
        cellAudio = false;
        generationAudio = false;
        setNumbeOfCells();
    }

    @Override
    public void run() {
        nextGeneration();
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
                try {
                    if (!isActive) {
                        return;
                    }
                    int nrOfNeighbours = countNeighbours(testPattern, row, col);
                    if (testPattern.get(row).get(col) == 1) {
                        if (!rules.getSurviveRules().contains(nrOfNeighbours)) {
                            if (cellAudio) {
                                cellAudio(CellState.DEAD);
                                currentBoard.get(row).set(col, DEAD);
                                livingCells--;
                            }
                        } else {
                            if (cellAudio) {
                                cellAudio(CellState.ALIVE);
                            }
                        }
                        currentBoard.get(row).set(col, DEAD);
                        livingCells--;
                    } else if (testPattern.get(row).get(col) == 0) {
                        if (rules.getBirthRules().contains(nrOfNeighbours)) {
                            if (cellAudio) {
                                cellAudio(CellState.ALIVE);
                            }
                            currentBoard.get(row).set(col, ALLIVE);
                            livingCells++;
                        } else {
                            if (cellAudio) {
                                cellAudio(CellState.DEAD);
                            }
                        }
                    }
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BoardSound.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println(row);
        }
        if (generationAudio) {
            generationAudio();
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

    public void setAudioLength(long audioLength) {
        this.audioLength = audioLength / 1000000;
    }

    private void setNumbeOfCells() {
        int cells = 0;
        for (int row = 0; row < currentBoard.size(); row++) {
            cells += currentBoard.get(row).size();
        }
        this.nrOfCells = cells;
        System.out.println("Number of cells " + nrOfCells);
    }

    public void cellAudio(BoardSound.CellState state) {
        switch (state) {
            case DEAD:
                for (MidiChannel m : midiChannels) {
                    if (m != null) { // channel is open
                        try {
                            m.noteOn(60, 50);
                            Thread.sleep(audioLength);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AudioManager.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            m.noteOff(60);
                        }
                        return;
                    }
                }
            case ALIVE:
                for (MidiChannel m : midiChannels) {
                    if (m != null) { // channel is open
                        try {
                            m.noteOn(60, 200);
                            m.noteOn(72, 200);
                            m.noteOn(76, 200);
                            Thread.sleep(audioLength);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AudioManager.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            m.noteOff(60);
                            m.noteOff(72);
                            m.noteOff(76);
                        }
                        return;
                    }
                }
        }
    }

    public void generationAudio() {

    }

    public int getNrOfCells() {
        return this.nrOfCells;
    }
}
