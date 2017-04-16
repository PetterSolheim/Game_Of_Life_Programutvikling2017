package model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>Game rules are stored and altered through this class. Class is a singleton,
 * and the constructor is therefore private. A reference to the object can be
 * acquired using the static getInstance() method. The following rules are
 * currently supported;</p>
 * <ul>
 * <li><b>survivalRules</b>, defines which living cells should survive.</li>
 * <li><b>birthRules</b>, defines which dead cells should become alive.</li>
 * <li><b>dynamic</b>, defines if the board should behave dynamically.</li>
 * </ul>
 * 
 * @author aleks
 */
public class Rules {

    private ArrayList<Integer> survivalRules;
    private ArrayList<Integer> birthRules;
    private boolean dynamic = false;
    private long maxNumberOfCells = 16000000L;
    private static Rules rules;

    /**
     * Class is a singleton. Constructor is therefore private. Constructor sets
     * Conway rules (S23/B3) as default values.
     */
    private Rules() {
        survivalRules = new ArrayList<Integer>();
        survivalRules.add(2);
        survivalRules.add(3);
        birthRules = new ArrayList<Integer>();
        birthRules.add(3);
    }

    /**
     * Rules is a singleton. Constructor is therefore private. Use this method
     * to aquire a reference to the Rules singleton object.
     *
     * @return reference to the Rules object.
     */
    public static Rules getInstance() {
        if (rules == null) {
            rules = new Rules();
        }
        return rules;
    }

    /**
     *
     * @return a boolean value indicating if the board should behave
     * dynamically.
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * Specify if the board should behave dynamically. If so, board size will
     * increase to acomadate boards that grow.
     *
     * @param dynamic
     */
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    /**
     * Sets the survival values for the game rules. New values can be passed as
     * either a number of integers, or an array of integers. If duplicate values
     * are passed, they will be removed, i.e. 2,3,3,4 will be stored as 2,3,4.
     *
     * Existing rules are discarded before new rules are applied.
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
     * Takes an ArrayList of integers and uses it as the new definition for
     * survival rules.
     *
     * @param input
     */
    public void setSurviveRules(ArrayList<Integer> input) {
        survivalRules = input;
    }

    /**
     * Acquires an ArrayList of integer values which define the number of live
     * neighbours a live cell must have to survive.
     *
     * @return an ArrayList of Integers.
     */
    public ArrayList<Integer> getSurviveRules() {
        return survivalRules;
    }

    /**
     * Sets the birth values for the game rules. New values can be passed as
     * either a number of integers, or an array of integers. If duplicate values
     * are passed, they will be removed, i.e. 2,3,3,4 will be stored as 2,3,4.
     *
     * Existing rules are discarded before new rules are applied.
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
     * Takes an ArrayList of integers and uses it as the new definition for
     * survival rules.
     *
     * @param input
     */
    public void setBirthRules(ArrayList<Integer> input) {
        birthRules = input;
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

}
