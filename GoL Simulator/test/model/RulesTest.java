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
        Rules result = Rules.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of isDynamic method, of class Rules.
     */
    @Test
    public void testIsDynamic() {
        System.out.println("isDynamic");
        Rules instance = Rules.getInstance();
        boolean expResult = false;
        instance.setDynamic(expResult);
        boolean result = instance.isDynamic();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDynamic method, of class Rules.
     */
    @Test
    public void testSetDynamic() {
        System.out.println("setDynamic");
        Rules instance = Rules.getInstance();
        boolean expResult = true;
        instance.setDynamic(expResult);
        boolean result = instance.isDynamic();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSurviveRules method, of class Rules.
     */
    @Test
    public void testSetSurviveRules_intArr() {
        System.out.println("setSurviveRules");
        int[] input = {1, 2, 3, 3, 3, 4};
        Rules instance = Rules.getInstance();
        instance.setSurviveRules(input);
        ArrayList<Integer> expResult = new ArrayList<>();
        expResult.add(1);
        expResult.add(2);
        expResult.add(3);
        expResult.add(4);
        ArrayList<Integer> result = instance.getSurviveRules();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSurviveRules method, of class Rules.
     */
    @Test
    public void testSetSurviveRules_ArrayList() {
        System.out.println("setSurviveRules");
        ArrayList<Integer> expResult = new ArrayList<>();
        expResult.add(1);
        expResult.add(2);
        expResult.add(3);
        expResult.add(4);
        Rules instance = Rules.getInstance();
        instance.setSurviveRules(expResult);
        ArrayList<Integer> result = instance.getSurviveRules();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSurviveRules method, of class Rules.
     */
    @Test
    public void testGetSurviveRules() {
        System.out.println("getSurviveRules");
        Rules instance = Rules.getInstance();
        ArrayList<Integer> expResult = new ArrayList<>();
        expResult.add(1);
        expResult.add(2);
        expResult.add(3);
        expResult.add(4);
        instance.setSurviveRules(expResult);
        ArrayList<Integer> result = instance.getSurviveRules();
        assertEquals(expResult, result);
    }

    /**
     * Test of setBirthRules method, of class Rules.
     */
    @Test
    public void testSetBirthRules_intArr() {
        System.out.println("setBirthRules");
        int[] input = {1, 2, 2, 2, 4};
        Rules instance = Rules.getInstance();
        instance.setBirthRules(input);
        ArrayList<Integer> expResult = new ArrayList<>();
        expResult.add(1);
        expResult.add(2);
        expResult.add(4);
        ArrayList<Integer> result = instance.getBirthRules();
        assertEquals(expResult, result);
    }

    /**
     * Test of setBirthRules method, of class Rules.
     */
    @Test
    public void testSetBirthRules_ArrayList() {
        System.out.println("setBirthRules");
        Rules instance = Rules.getInstance();
        ArrayList<Integer> expResult = new ArrayList<>();
        expResult.add(1);
        expResult.add(2);
        expResult.add(4);
        instance.setBirthRules(expResult);
        ArrayList<Integer> result = instance.getBirthRules();
        assertEquals(expResult, result);
    }

    /**
     * Test of getBirthRules method, of class Rules.
     */
    @Test
    public void testGetBirthRules() {
        System.out.println("getBirthRules");
        Rules instance = Rules.getInstance();
        ArrayList<Integer> expResult = new ArrayList<>();
        expResult.add(1);
        expResult.add(2);
        instance.setBirthRules(expResult);
        ArrayList<Integer> result = instance.getBirthRules();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxNumberOfCells method, of class Rules.
     */
    @Test
    public void testGetMaxNumberOfCells() {
        System.out.println("getMaxNumberOfCells");
        Rules instance = Rules.getInstance();
        int expResult = 1000000;
        instance.setMaxNumberOfCells(expResult);
        int result = instance.getMaxNumberOfCells();
        assertEquals(expResult, result);
    }

    /**
     * Test of setMaxNumberOfCells method, of class Rules.
     */
    @Test
    public void testSetMaxNumberOfCells() {
        System.out.println("setMaxNumberOfCells");
        Rules instance = Rules.getInstance();
        int expResult = 1000000;
        instance.setMaxNumberOfCells(expResult);
        int result = instance.getMaxNumberOfCells();
        assertEquals(expResult, result);
    }

}
