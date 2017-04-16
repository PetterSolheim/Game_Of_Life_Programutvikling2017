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

    byte dead = 0;
    byte alive = 1;
    Rules rules = Rules.getInstance();

    public BoardDynamicTest() {

    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        BoardDynamic instance = new BoardDynamic(4, 2);
        String expResult = "00000000";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    // simple setBoard
    @Test
    public void testSetBoard() {
        System.out.println("setBoard");
        byte dead = 0;
        BoardDynamic instance = new BoardDynamic(2, 2);
        String expResult = "0000";
        String result = instance.toString();
        assertEquals(expResult, result);

        ArrayList<ArrayList<Byte>> input = new ArrayList<ArrayList<Byte>>();
        for (int i = 0; i < 4; i++) {
            input.add(new ArrayList<Byte>());
            for (int j = 0; j < 2; j++) {
                input.get(i).add(dead);
            }
        }
        instance.setBoard(input);
        expResult = "00000000";
        result = instance.toString();
        assertEquals(expResult, result);
    }

    @Test
    // TODO: bad test, should rewrite
    public void testGetChangedCells() {
        System.out.println("getChangedCells");

        byte dead = 0;
        byte alive = 1;
        BoardDynamic instance = new BoardDynamic(2, 2);

        ArrayList<ArrayList<Byte>> newBoard = new ArrayList<>();
        newBoard.add(new ArrayList<>());
        newBoard.add(new ArrayList<>());
        newBoard.get(0).add(dead);
        newBoard.get(0).add(dead);
        newBoard.get(1).add(alive);
        newBoard.get(1).add(alive);
        instance.setBoard(newBoard);

        ArrayList<ArrayList<Byte>> expResult = new ArrayList<>();
        expResult.add(new ArrayList<>());
        expResult.add(new ArrayList<>());
        expResult.get(0).add(dead);
        expResult.get(0).add(dead);
        expResult.get(1).add(dead);
        expResult.get(1).add(dead);

        ArrayList<ArrayList<Byte>> result = instance.getChangedCells();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetCurrentBoard() {
        System.out.println("getCurrentBoard");
        byte dead = 0;
        byte alive = 1;
        BoardDynamic instance = new BoardDynamic(2, 2);

        ArrayList<ArrayList<Byte>> expResult = new ArrayList<>();
        expResult.add(new ArrayList<>());
        expResult.add(new ArrayList<>());
        expResult.get(0).add(dead);
        expResult.get(0).add(dead);
        expResult.get(1).add(alive);
        expResult.get(1).add(alive);

        instance.setBoard(expResult);
        ArrayList<ArrayList<Byte>> result = instance.getBoard();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetOriginalBoard() {
        System.out.println("getOriginalBoard");
        byte dead = 0;
        byte alive = 1;
        BoardDynamic instance = new BoardDynamic(2, 2);

        ArrayList<ArrayList<Byte>> expResult = new ArrayList<>();
        expResult.add(new ArrayList<>());
        expResult.add(new ArrayList<>());
        expResult.get(0).add(dead);
        expResult.get(0).add(dead);
        expResult.get(1).add(alive);
        expResult.get(1).add(alive);

        instance.setBoard(expResult);
        ArrayList<ArrayList<Byte>> result = instance.getOriginalBoard();

        assertEquals(expResult, result);
    }

    @Test
    public void testNextGeneration() {
        System.out.println("testNextGeneration");

        // create starting point
        BoardDynamic instance = new BoardDynamic(3, 3);
        ArrayList<ArrayList<Byte>> startBoard = new ArrayList<>();
        startBoard.add(new ArrayList<>());
        startBoard.add(new ArrayList<>());
        startBoard.add(new ArrayList<>());
        startBoard.get(0).add(dead);
        startBoard.get(0).add(alive);
        startBoard.get(0).add(dead);
        startBoard.get(1).add(dead);
        startBoard.get(1).add(alive);
        startBoard.get(1).add(dead);
        startBoard.get(2).add(dead);
        startBoard.get(2).add(alive);
        startBoard.get(2).add(dead);
        instance.setBoard(startBoard);
        instance.nextGeneration();

        // create expected result
        ArrayList<ArrayList<Byte>> expResult = new ArrayList<>();
        expResult.add(new ArrayList<>());
        expResult.add(new ArrayList<>());
        expResult.add(new ArrayList<>());
        expResult.get(0).add(dead);
        expResult.get(0).add(dead);
        expResult.get(0).add(dead);
        expResult.get(1).add(alive);
        expResult.get(1).add(alive);
        expResult.get(1).add(alive);
        expResult.get(2).add(dead);
        expResult.get(2).add(dead);
        expResult.get(2).add(dead);

        assertEquals(expResult, instance.getBoard());

        // run test with dynamic board
        rules.setDynamic(true);
        instance.nextGeneration();
        instance.nextGeneration();
        instance.nextGeneration();
        instance.nextGeneration();

        // create expected result
        ArrayList<ArrayList<Byte>> expResultDynamic = new ArrayList<>();
        expResultDynamic.add(new ArrayList<>());
        expResultDynamic.add(new ArrayList<>());
        expResultDynamic.add(new ArrayList<>());
        expResultDynamic.add(new ArrayList<>());
        expResultDynamic.add(new ArrayList<>());
        expResultDynamic.get(0).add(dead);
        expResultDynamic.get(0).add(dead);
        expResultDynamic.get(0).add(dead);
        expResultDynamic.get(0).add(dead);
        expResultDynamic.get(0).add(dead);
        expResultDynamic.get(1).add(dead);
        expResultDynamic.get(1).add(dead);
        expResultDynamic.get(1).add(dead);
        expResultDynamic.get(1).add(dead);
        expResultDynamic.get(1).add(dead);
        expResultDynamic.get(2).add(dead);
        expResultDynamic.get(2).add(alive);
        expResultDynamic.get(2).add(alive);
        expResultDynamic.get(2).add(alive);
        expResultDynamic.get(2).add(dead);
        expResultDynamic.get(3).add(dead);
        expResultDynamic.get(3).add(dead);
        expResultDynamic.get(3).add(dead);
        expResultDynamic.get(3).add(dead);
        expResultDynamic.get(3).add(dead);
        expResultDynamic.get(4).add(dead);
        expResultDynamic.get(4).add(dead);
        expResultDynamic.get(4).add(dead);
        expResultDynamic.get(4).add(dead);
        expResultDynamic.get(4).add(dead);

        assertEquals(expResultDynamic, instance.getBoard());

    }

}
