package model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>
 * Game rules are stored and altered through this class. Class is a singleton,
 * and the constructor is therefore private. A reference to the object can be
 * acquired using the static {@link #getInstance() } method. The following rules
 * are currently supported;</p>
 * <ul>
 * <li><b>survival rules</b>, defines which living cells should survive.</li>
 * <li><b>birth rules</b>, defines which dead cells should become alive.</li>
 * <li><b>dynamic</b>, defines if the board should behave dynamically.</li>
 * <li><b>max number of cells:</b>, defines the upper limit to how many cells a
 * dynamic board can have.</li>
 * </ul>
 */
public class Rules {

    private ArrayList<Integer> survivalRules;
    private ArrayList<Integer> birthRules;
    private boolean dynamic = false;
    private int maxNumberOfCells = 3000000;
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
     * @return a reference to the Rules object.
     */
    public static Rules getInstance() {
        if (rules == null) {
            rules = new Rules();
        }
        return rules;
    }

    /**
     * Specifies if the rules are currently set to dynamic board behaviour.
     *
     * @return a <code>boolean</code> value indicating if the board should
     * behave dynamically.
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * Set the border behaviour for the game. True makes game board dynamic.
     *
     * @param dynamic a<code>boolean</code> specifying if the game rules should
     * be dynamic or not.
     */
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    /**
     * Sets the survival values for the game rules. New values can be passed as
     * either a number of <code>ints</code>, or an <code>int[]</code>. If
     * duplicate values are passed, they will be removed, i.e. 2,3,3,4 will be
     * stored as 2,3,4.
     *
     * Existing rules are discarded before new rules are applied.
     *
     * @param input one or more <code>int</code> values, or a <code>int[]</code>
     * specifying the new values.
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
     * Takes an <code>ArrayList&lt;Integer&gt;</code> and uses it as the new
     * definition for survival rules.
     *
     * Existing rules are discarded before new rules are applied.
     *
     * @param input an <code>ArrayList&lt;Integer&gt;</code> containing the new
     * rule values.
     */
    public void setSurviveRules(ArrayList<Integer> input) {
        survivalRules = input;
    }

    /**
     * Acquires an <code>ArrayList&lt;Integer&gt;</code> which define the number
     * of live neighbours a live cell must have to survive.
     *
     * @return an <code>&lt;ArrayList&lt;Integer&gt;&gt;</code> specifying the
     * new values.
     */
    public ArrayList<Integer> getSurviveRules() {
        return survivalRules;
    }

    /**
     * Sets the birth values for the game rules. New values can be passed as
     * either a number of <code>ints</code>, or an <code>int[]</code>. If
     * duplicate values are passed, they will be removed, i.e. 2,3,3,4 will be
     * stored as 2,3,4.
     *
     * Existing rules are discarded before new rules are applied.
     *
     * @param input one or more <code>int</code> values, or a <code>int[]</code>
     * specifying the new values.
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
     * Takes an <code>ArrayList&lt;Integer&gt;</code> and uses it as the new
     * definition for survival rules.
     *
     * Existing rules are discarded before new rules are applied.
     *
     * @param input an <code>ArrayList&lt;Integer&gt;</code> containing the new
     * rule values.
     */
    public void setBirthRules(ArrayList<Integer> input) {
        birthRules = input;
    }

    /**
     * Acquires an <code>ArrayList&lt;Integer&gt;</code> which define the number
     * of live neighbours a dead cell must have to be born.
     *
     * @return an <code>ArrayList&lt;Integer&gt;</code> specifying the values
     * for a dead cell to be born.
     */
    public ArrayList<Integer> getBirthRules() {
        return birthRules;
    }

    /**
     * Gets the max number of cells for a board.
     *
     * @return a <code>long</code> specifying the max number of cells.
     */
    public int getMaxNumberOfCells() {
        return maxNumberOfCells;
    }

    /**
     * Sets the max number of cells for a board.
     *
     * @param newValue a <code>long</code> specifying the max number of cells.
     */
    public void setMaxNumberOfCells(int newValue) {
        maxNumberOfCells = newValue;
    }

}
