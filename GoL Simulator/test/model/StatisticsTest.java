package model;

import javafx.scene.chart.XYChart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class StatisticsTest {

    private Statistics instance;

    public StatisticsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        BoardDynamic b = new BoardDynamic(4, 4);
        instance = new Statistics(b, 3);
    }

    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of getLastGeneration method, of class Statistics.
     */
    @org.junit.Test
    public void testGetLastGeneration() {
        System.out.println("getLastGeneration");
        //arrange
        int expected = 2;
        //act
        int result = instance.getLastGeneration();
        //assert
        assertEquals(expected, result);
        assertTrue(expected == result);
    }

    /**
     * Test of setIterationsAndLastGeneration method, of class Statistics.
     */
    @org.junit.Test
    public void testSetIterationsAndLastGeneration() {
        System.out.println("setIterations");
        //arrange
        int iterations = 3; 
        //act
        int expectedIteration = 6;
        int expectedGeneration = 5;
        instance.setIterationsAndLastGeneration(iterations);
        //assert
        assertEquals(expectedIteration, instance.getIterations());
        assertEquals(expectedGeneration, instance.getLastGeneration());
    }

    /**
     * Test of getIterations method, of class Statistics.
     */
    @org.junit.Test
    public void testGetIterations() {
        System.out.println("getIterations");
        //arrange
        int iterations = 4; 
        //act
        int expected = 7;
        instance.setIterationsAndLastGeneration(iterations);
        //assert
        assertEquals(expected, instance.getIterations());
    }

    /**
     * Test of getFirstGeneration method, of class Statistics.
     */
    @org.junit.Test
    public void testGetFirstGeneration() {
        System.out.println("getFirstGeneration");
        //arrange
        int expected = 0;
        //act
        int actual = instance.getFirstGeneration();
        //assert
        assertEquals(expected, actual);
    }

    /**
     * Test of getStatistics method, of class Statistics.
     */
    @org.junit.Test
    public void testGetStatistics() {
        System.out.println("getStatistics");
        //arrange
        XYChart.Series[] result = new XYChart.Series[3];
        //act
        result = instance.getStatistics();
        //assert
        assertTrue(result.length == 3);
        assertEquals("Living Cells", result[0].getName());
        assertEquals("Population Change", result[1].getName());
        assertEquals("Similarity Measure", result[2].getName());
    }

    /**
     * Test of getSelectedGeneration method, of class Statistics.
     */
    @org.junit.Test
    public void testGetSelectedGeneration() {
        System.out.println("getSelectedIteration");
        //arrange
        int desiredGeneration = 1;
        instance.getStatistics();
        //act
        BoardDynamic result = instance.getSelectedGeneration(desiredGeneration);
        //assert
        assertNotNull(result);
        assertEquals("0000010000100000", result.toString());
    }

    /**
     * Test of AudioStatistics method, of class Statistics.
     */
    @Test
    public void testAudioStatistics() {
        System.out.println("AudioStatistics");
        //arrange

        //act
        //assert
    }

    /**
     * Test of getSimilairtyFromFloat method, of class Statistics.
     */
    @Test
    public void testGetSimilairtyFromFloat() {
        System.out.println("getSimilairtyFromFloat");
        //arrange
        instance.getStatistics();
        //act
        //assert
    }

    /**
     * Test of findMostSimilar method, of class Statistics.
     */
    @Test
    public void testFindMostSimilar() {
        System.out.println("findMostSimilar");
        //arrange

        //act
        //assert
    }

}
