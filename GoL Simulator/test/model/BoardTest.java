/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aleks
 */
public class BoardTest {
    
    public BoardTest() {
    }

    @Test
    public void testNextGeneration() {
        Board b = Board.getInstance();
        
        // test pattern 1
        byte[][] testBoard = {
            {0,0,0,0},
            {0,1,1,0},
            {0,1,1,0},
            {0,0,0,0}
        };
        b.setBoard(testBoard);
        b.nextGeneration();
        org.junit.Assert.assertEquals(b.toString(),"0000011001100000");
        
        // test pattern 2
        byte[][] testBoard2 = {
            {0,0,0},
            {1,1,1},
            {1,1,1},
            {0,0,0}
        };
        b.setBoard(testBoard2);
        b.nextGeneration();
        org.junit.Assert.assertEquals(b.toString(),"010101101010");
        
        // test pattern 3
        byte[][] testBoard3 = {
            {0,0,0,0,0,0},
            {0,1,1,1,1,0},
            {0,1,1,1,1,0},
            {0,1,1,1,1,0},
            {0,1,1,1,1,0},
            {0,0,0,0,0,0}
        };
        b.setBoard(testBoard3);
        b.nextGeneration();
        org.junit.Assert.assertEquals(b.toString(),"001100010010100001100001010010001100");
        
        // test pattern 4: test an empty board
        byte[][] testBoard4 = {
            {0,0,0,0},
            {0,0,0,0},
            {0,0,0,0},
            {0,0,0,0}
        };
        b.setBoard(testBoard4);
        b.nextGeneration();
        org.junit.Assert.assertEquals(b.toString(),"0000000000000000");
        
        // test pattern 5: test a full board. This test will only work using
        // a static board of 4x4. Dynamic boards will behave differently.
        byte[][] testBoard5 = {
            {1,1,1,1},
            {1,1,1,1},
            {1,1,1,1},
            {1,1,1,1}
        };
        b.setBoard(testBoard5);
        b.nextGeneration();
        org.junit.Assert.assertEquals(b.toString(), "1001000000001001");
    }
}
