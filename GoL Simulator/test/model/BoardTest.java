package model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BoardTest {

    @Test
    public void testNextGeneration() {
        System.out.println("nextGeneration");
        Board instance = new Board();
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
        org.junit.Assert.assertEquals(instance.toString(), "000111000");

        // static board test nr. 2 of 6
        byte[][] board2 = {
            {0, 0, 0, 0},
            {0, 1, 1, 0},
            {0, 1, 1, 0},
            {0, 0, 0, 0}
        };
        instance.setBoard(board2);
        instance.nextGeneration();
        assertEquals(instance.toString(), "0000011001100000");

        // static board test nr. 3 of 6
        byte[][] board3 = {
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0}
        };
        instance.setBoard(board3);
        instance.nextGeneration();
        assertEquals(instance.toString(), "0000000001110000111000000000");

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
        assertEquals(instance.toString(), "00000000000000000000000000000000000");

        // static board test nr. 5 of 6
        byte[][] board5 = {
            {1, 1, 1}
        };
        instance.setBoard(board5);
        instance.nextGeneration();
        assertEquals(instance.toString(), "010");

        // static board test nr. 6 of 6
        byte[][] board6 = {
            {1},
            {1},
            {1}
        };
        instance.setBoard(board6);
        instance.nextGeneration();
        assertEquals(instance.toString(), "010");

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
        assertEquals(instance.toString(), "000000000000111000111000000000000000");

        // dynamic board test nr. 2 of 2
        byte[][] boardDynamic2 = {
            {1},
            {1},
            {1}
        };
        instance.setBoard(boardDynamic2);
        instance.nextGeneration();
        assertEquals(instance.toString(), "000000111000000");
    }

    @Test
    public void testGetBoard() {
        System.out.println("getBoard");
        Board instance = new Board();
        byte[][] startingBoard = {
            {0, 0, 0},
            {1, 1, 1},
            {0, 0, 0}
        };
        instance.setBoard(startingBoard);
        instance.nextGeneration();
        byte[][] expResult = {
            {0, 1, 0},
            {0, 1, 0},
            {0, 1, 0}
        };
        assertArrayEquals(expResult, instance.getBoard());
    }

    @Test
    public void testGetChangedCells() {
        System.out.println("getChangedCells");
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

        // test the method against a living cell.
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
        int expResult = 20;
        for (int i = 0; i < 20; i++) {
            instance.nextGeneration();
        }
        assertEquals(expResult, instance.getGenerationCount());
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
        byte[][] testBoard = {{0, 1, 0}, {0, 1, 0}, {0, 1, 0}};
        instance.setBoard(testBoard);
        assertEquals("010010010", instance.toString());
    }

    // TODO: Must check all values
    @Test
    public void testDeepCopy() {
        System.out.println("deepCopy");
        BoardDynamic instance = new BoardDynamic(2, 2);
        byte[][] instanceBoard = {
            {0, 1, 1},
            {0, 1, 1},
            {0, 0, 0}
        };
        instance.setBoard(instanceBoard);

        for (int i = 0; i < 4; i++) {
            instance.nextGeneration();
        }
        BoardDynamic newBoard = instance.deepCopy();

        // test that values are the same
        assertEquals(instance.getBoard(), newBoard.getBoard());
        assertEquals(instance.getCols(), newBoard.getCols());
        assertEquals(instance.getRows(), newBoard.getRows());
        assertEquals(instance.getGenerationCount(), newBoard.getGenerationCount());
        assertEquals(instance.getLivingCellCount(), newBoard.getLivingCellCount());
        assertEquals(instance.getNumberOfCells(), newBoard.getNumberOfCells());

        // make changes to the new board
        byte[][] board = {
            {0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0}
        };
        newBoard.setBoard(board);
        newBoard.nextGeneration();

        // ensure original boards values are unchanged
        assertEquals(instance.toString(), "011011000");
        assertEquals(instance.getCols(), 3);
        assertEquals(instance.getRows(), 3);
        assertEquals(instance.getGenerationCount(), 4);
        assertEquals(instance.getLivingCellCount(), 4);
        assertEquals(instance.getNumberOfCells(), 9);

    }

    @Test
    public void testGetNumberOfCells() {
        System.out.println("getNumberOfCells");
        Board instance = new Board();
        assertEquals(40000, instance.getNumberOfCells());

        byte[][] testBoard = {
            {1, 0, 1},
            {1, 1, 1},
            {0, 0, 0},
            {1, 0, 1}
        };
        instance.setBoard(testBoard);
        assertEquals(12, instance.getNumberOfCells());
    }

    @Test
    public void testGetLivingCellCount() {
        System.out.println("getLivingCellCount");
        Board instance = new Board();
        assertEquals(0, instance.getLivingCellCount());
    }
}
