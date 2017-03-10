/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author aleks
 */
public class Rules {

    private static Rules instance;
    private ArrayList<Integer> survivalRules;
    private ArrayList<Integer> birthRules;

    /**
     * Rules constructor is a Singleton as only one sett of rules can exist at
     * any time. Use getInstance() method to aquire object. Conway Rules (23/3)
     * set as default.
     */
    private Rules() {
        // apply Conway Rules (23/3) by default
        survivalRules = new ArrayList<Integer>();
        survivalRules.add(2);
        survivalRules.add(3);
        birthRules = new ArrayList<Integer>();
        birthRules.add(3);
    }

    /**
     * Method for acquiring a reference to the Rules object.
     *
     * @return a reference to the Rules object.
     */
    public static Rules getInstance() {
        if (instance == null) {
            instance = new Rules();
        }
        return instance;
    }

    /**
     * Sets the survival values for the game rules. New values can be passed as
     * either a number of integers, or an array of integers. If duplicate values
     * are passed, they will be removed, i.e. 2,3,3,4 will be stored as 2,3,4.
     *
     * @param input
     */
    public void setSurviveRules(int... input) {
        Arrays.sort(input);
        ArrayList<Integer> inputWithoutDuplicates = new ArrayList<Integer>();

        for (int i = 0; i < input.length; i++) {
            if (i == 0) {
                inputWithoutDuplicates.add(input[i]);
            } else if (input[i] != input[i - 1]) {
                inputWithoutDuplicates.add(input[i]);
            }
        }
        survivalRules = inputWithoutDuplicates;
    }

    /**
     * Sets the birth values for the game rules. New values can be passed as
     * either a number of integers, or an array of integers. If duplicate values
     * are passed, they will be removed, i.e. 2,3,3,4 will be stored as 2,3,4.
     *
     * @param input
     */
    public void setBirthRules(int... input) {
        Arrays.sort(input);
        ArrayList<Integer> inputWithoutDuplicates = new ArrayList<Integer>();

        for (int i = 0; i < input.length; i++) {
            if (i == 0) {
                inputWithoutDuplicates.add(input[i]);
            } else if (input[i] != input[i - 1]) {
                inputWithoutDuplicates.add(input[i]);
            }
        }
        birthRules = inputWithoutDuplicates;
    }

    /**
     * Acquires an ArrayList of integer values which define the number of live
     * neighbours a dead cell must have to be born.
     *
     * @return an ArrayList of Integers.
     */
    public ArrayList<Integer> getBirthRules() {
        return birthRules;
    }

    /**
     * Acquires an ArrayList of integer values which define the number of live
     * neighbours a live cell must have to survive.
     *
     * @return an ArrayList of Integers.
     */
    public ArrayList<Integer> getSurvivalRules() {
        return survivalRules;
    }
}
