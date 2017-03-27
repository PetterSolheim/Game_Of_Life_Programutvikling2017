/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author aleks
 */
public class BoardTest {

    @Test
    public void testNextGeneration() {
        System.out.println("nextGeneration");
        Board instance = new Board();

        byte[][] board = {
            {0, 1, 0},
            {0, 1, 0},
            {0, 1, 0}
        };
        instance.setBoard(board);
        instance.nextGeneration();
        org.junit.Assert.assertEquals(instance.toString(), "000111000");

        byte[][] board2 = {
            {0, 0, 0, 0},
            {0, 1, 1, 0},
            {0, 1, 1, 0},
            {0, 0, 0, 0}
        };
        instance.setBoard(board2);
        instance.nextGeneration();
        assertEquals(instance.toString(), "0000011001100000");

        byte[][] board3 = {
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0}
        };
        instance.setBoard(board3);
        instance.nextGeneration();
        assertEquals(instance.toString(), "0000000001110000111000000000");

        byte[][] board4 = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
        };
        instance.setBoard(board4);
        instance.nextGeneration();
        assertEquals(instance.toString(), "00000000000000000000000000000000000");

    }

    @Test
    public void testSetSurviveRules() {
        System.out.println("setSurviveRules");
        int[] input = {2, 2, 1, 3};
        Board instance = new Board();
        instance.setSurviveRules(input);
        ArrayList<Integer> expResult = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
        assertEquals(expResult, instance.getSurviveRules());
    }

    @Test
    public void testSetBirthRules() {
        System.out.println("setBirthRules");
        int[] input = {3, 2, 2};
        Board instance = new Board();
        instance.setBirthRules(input);
        ArrayList<Integer> expResult = new ArrayList<Integer>(Arrays.asList(2, 3));
        assertEquals(expResult, instance.getBirthRules());
    }

    @Test
    public void testGetBirthRules() {
        System.out.println("getBirthRules");
        Board instance = new Board();
        ArrayList<Integer> expResult = new ArrayList<Integer>(Arrays.asList(3));
        ArrayList<Integer> result = instance.getBirthRules();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetSurviveRules() {
        System.out.println("getSurvivalRules");
        Board instance = new Board();
        ArrayList<Integer> expResult = new ArrayList<Integer>(Arrays.asList(2, 3));
        ArrayList<Integer> result = instance.getSurviveRules();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetBoard() {
        System.out.println("getBoard");
        Board instance = new Board();
        byte[][] expResult = {{0, 1, 0}, {0, 0, 0}, {0, 1, 0}};
        instance.setBoard(expResult);
        byte[][] result = instance.getBoard();
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetBoardChanges() {
        System.out.println("getBoardChanges");
        Board instance = new Board();
        byte[][] testBoard = {{0, 1, 0}, {0, 1, 0}, {0, 1, 0}};
        instance.setBoard(testBoard);
        instance.nextGeneration();
        byte[][] expResult = {{0, 1, 0}, {1, 0, 1}, {0, 1, 0}};
        byte[][] result = instance.getBoardChanges();
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testSetBoard() {
        System.out.println("setBoard");
        Board instance = new Board();
        byte[][] expResult = {{0, 1, 0}, {0, 0, 0}, {0, 1, 0}};
        instance.setBoard(expResult);
        byte[][] result = instance.getBoard();
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testToggleCellState() {
        System.out.println("toggleCellState");
        int row = 0;
        int col = 0;
        Board instance = new Board();
        byte[][] testBoard = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        instance.setBoard(testBoard);
        instance.toggleCellState(row, col);
        assertEquals(instance.toString(), "000000000");

    }

    @Test
    public void testSetCellStateAlive() {
        System.out.println("settCellStateAlive");
        Board instance = new Board();
        byte[][] testBoard = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        instance.setBoard(testBoard);
        instance.setCellStateAlive(0, 0);
        assertEquals(instance.toString(), "100000000");

        byte[][] testBoard2 = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        instance.setBoard(testBoard2);
        instance.setCellStateAlive(0, 0);
        assertEquals(instance.toString(), "100000000");
    }

    @Test
    public void testGetGenerationCount() {
        System.out.println("getGenerationCount");
        Board instance = new Board();
        int expResult = 2;
        instance.nextGeneration();
        instance.nextGeneration();
        int result = instance.getGenerationCount();
        assertEquals(expResult, result);
    }

    @Test
    public void testResetBoard() {
        System.out.println("resetBoard");
        Board instance = new Board();
        byte[][] testBoard = {{0, 1, 0}, {0, 1, 0}, {0, 1, 0}};
        instance.setBoard(testBoard);
        instance.nextGeneration();
        instance.resetBoard();
        assertArrayEquals(testBoard, instance.getBoard());
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        Board instance = new Board();
        String expResult = "010010010";
        byte[][] testBoard = {{0, 1, 0}, {0, 1, 0}, {0, 1, 0}};
        instance.setBoard(testBoard);
        String result = instance.toString();
        assertEquals(expResult, result);
    }
}
