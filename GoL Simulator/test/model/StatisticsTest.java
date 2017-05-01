/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.scene.chart.XYChart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author peven
 */
public class StatisticsTest {
    
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
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getLastGeneration method, of class Statistics.
     */
    @org.junit.Test
    public void testGetLastGeneration() {
        System.out.println("getLastGeneration");
        Statistics instance = null;
        int expResult = 0;
        int result = instance.getLastGeneration();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIterationsAndLastGeneration method, of class Statistics.
     */
    @org.junit.Test
    public void testSetIterations() {
        System.out.println("setIterations");
        int i = 0;
        Statistics instance = null;
        instance.setIterationsAndLastGeneration(i);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIterations method, of class Statistics.
     */
    @org.junit.Test
    public void testGetIterations() {
        System.out.println("getIterations");
        Statistics instance = null;
        int expResult = 0;
        int result = instance.getIterations();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFirstGeneration method, of class Statistics.
     */
    @org.junit.Test
    public void testGetFirstGeneration() {
        System.out.println("getFirstGeneration");
        Statistics instance = null;
        int expResult = 0;
        int result = instance.getFirstGeneration();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStatistics method, of class Statistics.
     */
    @org.junit.Test
    public void testGetStatistics() {
        System.out.println("getStatistics");
        Statistics instance = null;
        XYChart.Series[] expResult = null;
        XYChart.Series[] result = instance.getStatistics();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSelectedIteration method, of class Statistics.
     */
    @org.junit.Test
    public void testGetSelectedIteration() {
        System.out.println("getSelectedIteration");
        int generation = 0;
        Statistics instance = null;
        BoardDynamic expResult = null;
        BoardDynamic result = instance.getSelectedIteration(generation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
