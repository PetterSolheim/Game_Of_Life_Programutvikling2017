/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aleks
 */
public class RulesTest {
    
    public RulesTest() {
    }

    /**
     * Test of getInstance method, of class Rules.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        Rules expResult = null;
        Rules result = Rules.getInstance();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isDynamic method, of class Rules.
     */
    @Test
    public void testIsDynamic() {
        System.out.println("isDynamic");
        Rules instance = null;
        boolean expResult = false;
        boolean result = instance.isDynamic();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDynamic method, of class Rules.
     */
    @Test
    public void testSetDynamic() {
        System.out.println("setDynamic");
        boolean dynamic = false;
        Rules instance = null;
        instance.setDynamic(dynamic);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSurviveRules method, of class Rules.
     */
    @Test
    public void testSetSurviveRules_intArr() {
        System.out.println("setSurviveRules");
        int[] input = null;
        Rules instance = null;
        instance.setSurviveRules(input);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSurviveRules method, of class Rules.
     */
    @Test
    public void testSetSurviveRules_ArrayList() {
        System.out.println("setSurviveRules");
        ArrayList<Integer> input = null;
        Rules instance = null;
        instance.setSurviveRules(input);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSurviveRules method, of class Rules.
     */
    @Test
    public void testGetSurviveRules() {
        System.out.println("getSurviveRules");
        Rules instance = null;
        ArrayList<Integer> expResult = null;
        ArrayList<Integer> result = instance.getSurviveRules();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBirthRules method, of class Rules.
     */
    @Test
    public void testSetBirthRules_intArr() {
        System.out.println("setBirthRules");
        int[] input = null;
        Rules instance = null;
        instance.setBirthRules(input);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBirthRules method, of class Rules.
     */
    @Test
    public void testSetBirthRules_ArrayList() {
        System.out.println("setBirthRules");
        ArrayList<Integer> input = null;
        Rules instance = null;
        instance.setBirthRules(input);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBirthRules method, of class Rules.
     */
    @Test
    public void testGetBirthRules() {
        System.out.println("getBirthRules");
        Rules instance = null;
        ArrayList<Integer> expResult = null;
        ArrayList<Integer> result = instance.getBirthRules();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaxNumberOfCells method, of class Rules.
     */
    @Test
    public void testGetMaxNumberOfCells() {
        System.out.println("getMaxNumberOfCells");
        Rules instance = null;
        int expResult = 0;
        int result = instance.getMaxNumberOfCells();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMaxNumberOfCells method, of class Rules.
     */
    @Test
    public void testSetMaxNumberOfCells() {
        System.out.println("setMaxNumberOfCells");
        int newValue = 0;
        Rules instance = null;
        instance.setMaxNumberOfCells(newValue);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
