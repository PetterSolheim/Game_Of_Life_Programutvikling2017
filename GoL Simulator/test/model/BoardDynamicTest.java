/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aleks
 */
public class BoardDynamicTest {

    public BoardDynamicTest() {

    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testSetBoard_ArrayList() {
        System.out.println("setBoard");
        // create test board
        byte alive = 1;
        byte dead = 0;
        ArrayList<ArrayList<Byte>> testBoard = new ArrayList<>();
        testBoard.add(0, new ArrayList<Byte>());
        testBoard.add(1, new ArrayList<Byte>());
        testBoard.add(2, new ArrayList<Byte>());
        testBoard.get(0).add(dead);
        testBoard.get(0).add(dead);
        testBoard.get(0).add(dead);
        testBoard.get(1).add(alive);
        testBoard.get(1).add(alive);
        testBoard.get(1).add(alive);
        testBoard.get(2).add(dead);
        testBoard.get(2).add(dead);
        testBoard.get(2).add(dead);

        // set the board
        BoardDynamic instance = new BoardDynamic();
        instance.setBoard(testBoard);

        // test equality
        assertEquals("000111000", instance.toString());
    }

    @Test
    public void testSetBoard_byteArrArr() {
        System.out.println("setBoard");
        byte[][] newBoard = {
            {1, 1, 1},
            {0, 0, 0},
            {1, 1, 1}
        };
        BoardDynamic instance = new BoardDynamic();
        instance.setBoard(newBoard);
        assertEquals("111000111", instance.toString());
    }

    @Test
    public void testGetBoard() {
        System.out.println("getBoard");

        // create test board
        byte alive = 1;
        byte dead = 0;
        ArrayList<ArrayList<Byte>> expResult = new ArrayList<>();
        expResult.add(0, new ArrayList<Byte>());
        expResult.add(1, new ArrayList<Byte>());
        expResult.add(2, new ArrayList<Byte>());
        expResult.get(0).add(dead);
        expResult.get(0).add(dead);
        expResult.get(0).add(dead);
        expResult.get(1).add(alive);
        expResult.get(1).add(alive);
        expResult.get(1).add(alive);
        expResult.get(2).add(dead);
        expResult.get(2).add(dead);
        expResult.get(2).add(dead);
        BoardDynamic instance = new BoardDynamic();
        instance.setBoard(expResult);
        assertEquals(expResult, instance.getBoard());
    }

    @Test
    public void testGetChangedCells() {
        System.out.println("getChangedCells");
        // create and set test board
        BoardDynamic instance = new BoardDynamic();
        byte[][] testBoard = {{0, 1, 0}, {0, 1, 0}, {0, 1, 0}};
        instance.setBoard(testBoard);
        instance.nextGeneration();

        // create expected result
        byte alive = 1;
        byte dead = 0;
        ArrayList<ArrayList<Byte>> expResult = new ArrayList<>();
        expResult.add(0, new ArrayList<Byte>());
        expResult.add(1, new ArrayList<Byte>());
        expResult.add(2, new ArrayList<Byte>());
        expResult.get(0).add(dead);
        expResult.get(0).add(alive);
        expResult.get(0).add(dead);
        expResult.get(1).add(alive);
        expResult.get(1).add(dead);
        expResult.get(1).add(alive);
        expResult.get(2).add(dead);
        expResult.get(2).add(alive);
        expResult.get(2).add(dead);
        assertEquals(expResult, instance.getChangedCells());
    }

    @Test
    public void testGetNumberOfCells() {
        System.out.println("getNumberOfCells");

        // test using board of 200 x 200
        BoardDynamic instance = new BoardDynamic(200, 200);
        assertEquals(40000, instance.getNumberOfCells());

        // test using board of 2 x 2
        instance = new BoardDynamic(2, 2);
        assertEquals(4, instance.getNumberOfCells());
    }

    @Test
    public void testGetLivingCellCount() {
        System.out.println("getLivingCellCount");
        // create test board
        byte alive = 1;
        byte dead = 0;
        ArrayList<ArrayList<Byte>> testBoard = new ArrayList<>();
        testBoard.add(0, new ArrayList<Byte>());
        testBoard.add(1, new ArrayList<Byte>());
        testBoard.add(2, new ArrayList<Byte>());
        testBoard.get(0).add(dead);
        testBoard.get(0).add(alive);
        testBoard.get(0).add(dead);
        testBoard.get(1).add(alive);
        testBoard.get(1).add(dead);
        testBoard.get(1).add(alive);
        testBoard.get(2).add(dead);
        testBoard.get(2).add(alive);
        testBoard.get(2).add(dead);
        BoardDynamic instance = new BoardDynamic();
        instance.setBoard(testBoard);

        assertEquals(4, instance.getLivingCellCount());
    }

    @Test
    public void testGetGenerationCount() {
        System.out.println("getGenerationCount");

        // new board, game not started
        BoardDynamic instance = new BoardDynamic();
        assertEquals(0, instance.getGenerationCount());

        // game run for 20 generation shifts
        for (int i = 0; i < 20; i++) {
            instance.nextGeneration();
        }
        assertEquals(20, instance.getGenerationCount());
    }

    @Test
    public void testDeepCopy() {
        System.out.println("deepCopy");
        BoardDynamic instance = new BoardDynamic();
        BoardDynamic expResult = null;
        BoardDynamic result = instance.deepCopy();
        assertEquals(expResult, result);
        fail("TODO: complete test code.");
    }

    @Test
    public void testNextGeneration() {
        System.out.println("nextGeneration");
        BoardDynamic instance = new BoardDynamic();
        Rules rules = Rules.getInstance();
        rules.setDynamic(false);

        // static board test nr. 1 of 6
        byte[][] board = {
            {0, 1, 0},
            {0, 1, 0},
            {0, 1, 0}
        };
        instance.setBoard(board);
        instance.nextGeneration();
        org.junit.Assert.assertEquals("000111000", instance.toString());

        // static board test nr. 2 of 6
        byte[][] board2 = {
            {0, 0, 0, 0},
            {0, 1, 1, 0},
            {0, 1, 1, 0},
            {0, 0, 0, 0}
        };
        instance.setBoard(board2);
        instance.nextGeneration();
        assertEquals("0000011001100000", instance.toString());

        // static board test nr. 3 of 6
        byte[][] board3 = {
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0}
        };
        instance.setBoard(board3);
        instance.nextGeneration();
        assertEquals("0000000001110000111000000000", instance.toString());

        // static board test nr. 4 of 6
        byte[][] board4 = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
        };
        instance.setBoard(board4);
        instance.nextGeneration();
        assertEquals("00000000000000000000000000000000000", instance.toString());

        // static board test nr. 5 of 6
        byte[][] board5 = {
            {1, 1, 1}
        };
        instance.setBoard(board5);
        instance.nextGeneration();
        assertEquals("010", instance.toString());

        // static board test nr. 6 of 6
        byte[][] board6 = {
            {1},
            {1},
            {1}
        };
        instance.setBoard(board6);
        instance.nextGeneration();
        assertEquals("010", instance.toString());

        // dynamic board test nr. 1 of 2
        rules.setDynamic(true);
        byte[][] boardDynamic1 = {
            {0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0},
            {1, 0, 0, 0, 0},
            {1, 0, 0, 0, 0},
            {1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };
        instance.setBoard(boardDynamic1);
        instance.nextGeneration();
        assertEquals("000000000000111000111000000000000000", instance.toString());

        // dynamic board test nr. 2 of 2
        byte[][] boardDynamic2 = {
            {1},
            {1},
            {1}
        };
        instance.setBoard(boardDynamic2);
        instance.nextGeneration();
        assertEquals("000000111000000", instance.toString());
    }

    @Test
    public void testToggleCellState() {
        System.out.println("toggleCellState");
        BoardDynamic instance = new BoardDynamic();
        byte[][] testBoard = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        instance.setBoard(testBoard);
        instance.toggleCellState(0, 0);
        assertEquals("000000000", instance.toString());
    }

    @Test
    public void testSetCellStateAlive() {
        System.out.println("setCellStateAlive");
        Board instance = new Board();

        // test the method against a living cell.
        byte[][] testBoard = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        instance.setBoard(testBoard);
        instance.setCellStateAlive(0, 0);
        assertEquals("100000000", instance.toString());

        // test the method against a dead cell.
        byte[][] testBoard2 = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        instance.setBoard(testBoard2);
        instance.setCellStateAlive(0, 0);
        assertEquals("100000000", instance.toString());
    }

    @Test
    public void testResetBoard() {
        System.out.println("resetBoard");
        BoardDynamic instance = new BoardDynamic();
        byte[][] testBoard = {{0, 1, 0}, {0, 1, 0}, {0, 1, 0}};
        instance.setBoard(testBoard);
        instance.nextGeneration();
        instance.resetBoard();
        assertEquals("010010010", instance.toString());
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        BoardDynamic instance = new BoardDynamic();
        byte[][] testBoard = {
            {1, 0, 1},
            {1, 0, 1},
            {1, 0, 1}
        };
        instance.setBoard(testBoard);
        assertEquals("101101101", instance.toString());
    }

}
