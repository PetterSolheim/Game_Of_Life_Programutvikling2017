package model;

/**
 *
 * @author peven
 */
public class TestBoard {
    
    public byte[][] cellGrid;

    public TestBoard (){
        cellGrid = new byte[10][10];
        cellGrid[2][2] = 1;
        cellGrid[2][3] = 1;
        cellGrid[2][4] = 1;
        cellGrid[3][2] = 1;
    }
    
    public byte[][] getBoard (){
        return this.cellGrid;
    }
}
