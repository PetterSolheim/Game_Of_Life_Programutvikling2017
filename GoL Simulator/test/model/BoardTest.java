/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author aleks
 */
public class BoardTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testNextGeneration() {
        System.out.println("nextGeneration");
        Board instance = new Board();

        // board test nr. 1 of 4
        byte[][] board = {
            {0, 1, 0},
            {0, 1, 0},
            {0, 1, 0}
        };
        instance.setBoard(board);
        instance.nextGeneration();
        org.junit.Assert.assertEquals(instance.toString(), "000111000");

        // board test nr. 2 of 4
        byte[][] board2 = {
            {0, 0, 0, 0},
            {0, 1, 1, 0},
            {0, 1, 1, 0},
            {0, 0, 0, 0}
        };
        instance.setBoard(board2);
        instance.nextGeneration();
        assertEquals(instance.toString(), "0000011001100000");

        // board test nr. 3 of 4
        byte[][] board3 = {
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0}
        };
        instance.setBoard(board3);
        instance.nextGeneration();
        assertEquals(instance.toString(), "0000000001110000111000000000");

        // board test nr. 4 of 4
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
        byte[][] result = instance.getChangedCells();
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
        Board instance = new Board();
        byte[][] testBoard = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        instance.setBoard(testBoard);
        instance.toggleCellState(0, 0);
        assertEquals(instance.toString(), "000000000");

    }

    @Test
    public void testSetCellStateAlive() {
        System.out.println("settCellStateAlive");
        Board instance = new Board();

        // test the method against a living cell (i.e. cell state should not change).
        byte[][] testBoard = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        instance.setBoard(testBoard);
        instance.setCellStateAlive(0, 0);
        assertEquals(instance.toString(), "100000000");

        // test the method against a dead cell.
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

    @Test
    public void testDeepCopy() {
        System.out.println("deepCopy");
        Board orig = new Board();

        byte[][] origBoardArray = {
            {0, 1, 0},
            {0, 1, 0},
            {0, 1, 0}
        };
        orig.setBoard(origBoardArray);

        // create a deep copy of the board
        Board deepCopy = orig.deepCopy();
        
        // change the board of the new board.
        byte[][] newBoardArray = {
            {1, 0, 1},
            {1, 0, 1},
            {1, 0, 1}
        };
        deepCopy.setBoard(newBoardArray);

        // check that the new board has changed compared to the old one.
        assertArrayEquals(deepCopy.getBoard(), newBoardArray);

        // check that the original Board object has been left untouched by
        // changes made to the new, deep copy.
        assertArrayEquals(orig.getBoard(), origBoardArray);
    }

}
