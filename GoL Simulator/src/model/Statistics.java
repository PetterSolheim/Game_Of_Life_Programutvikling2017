package model;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.chart.XYChart;

/**
 * This class is used to generate various statistics
 *
 */
public class Statistics {

    /**
     * A deep copy of the active board.
     */
    private BoardDynamic b;
    /**
     * The amount of iteration the data generation is going to run.
     */
    private int iterations;
    /**
     * The generation counter of the board at the start of initialization
     */
    private int firstGeneration;
    /**
     * The generation when data generation ends.
     */
    private int lastGeneration;
    /**
     * Field used to simply store data so the controller can access them later.
     */
    private float averageDeadCells, averageLivingCells;
    /**
     * Used to store a generations floating point representation and the corresponding generation.
     */
    private HashMap<Integer, Float> floatBoards;
    /**
     * Used to store a generations total amount of living cells and the corresponding generation number.
     */
    private HashMap<Integer, Integer> livingCellsPerGeneration;
    /**
     * Used to store an entire <code>BoardDynamic</code> and the corresponding generation number.
     */
    private HashMap<Integer, BoardDynamic> generations;

    /**
     * Statistics constructor. Needs a board to generate data from and the amount of iterations to run the data generation.
     * The constructor will create a deep copy of the board to ensure that the main window isn't affected by the data generation.
     * @param b
     * @param iterations
     */
    public Statistics(BoardDynamic b, int iterations) {
        this.b = b.deepCopy();
        firstGeneration = b.getGenerationCount();
        this.iterations = iterations;
        livingCellsPerGeneration = new HashMap<Integer, Integer>();
        floatBoards = new HashMap<Integer, Float>();
        generations = new HashMap<Integer, BoardDynamic>();
        setLastGeneration();
    }
    /**
     * Sets the final generations that this instance should generate statistics for.
     */
    private void setLastGeneration() {
        this.lastGeneration = firstGeneration + iterations - 1;
    }

    /**
     * Returns the last generation that this instance generates statistics for.
     * @return <code>int</code> generation
     */
    public int getLastGeneration() {
        return this.lastGeneration;
    }

    /**
     * Sets the amount of iterations to generate data for, and determines the last generation of that various loops run.
     * @param i  <code>int</code>
     */
    public void setIterationsAndLastGeneration(int i) {
        this.iterations = i;
        setLastGeneration();
    }

    /**
     * returns the amount of iterations the user wished to generate statistics for.
     * @return <code>int</code> iterations
     */
    public int getIterations() {
        return this.iterations;
    }

    /**
     * returna the generation of the board that was used to crate the instance.
     * @return <code>int</code> the generation
     */
    public int getFirstGeneration() {
        return this.firstGeneration;
    }

    /**
     *
     */
    public void AudioStatistics() {
        ArrayList<Integer> cellsPerGeneratin = new ArrayList<Integer>();
        ArrayList<Integer> populationChange = new ArrayList<Integer>();
        populationChange.add(0);
        int totalCells = b.getNumberOfCells();
        averageDeadCells = totalCells - b.getLivingCellCount();
        averageLivingCells = b.getLivingCellCount();
        int prevCellCount = 0;
        while (b.getGenerationCount() <= lastGeneration) {
            cellsPerGeneratin.add(b.getLivingCellCount());
            prevCellCount = b.getLivingCellCount();
            b.nextGeneration();
            averageDeadCells += (totalCells - b.getLivingCellCount());
            averageLivingCells += b.getLivingCellCount();
            populationChange.add(getPopulationChange(prevCellCount));
        }
        int i = 0;

        averageDeadCells = averageDeadCells / iterations;
        averageLivingCells = averageLivingCells / iterations;
        System.out.println(averageDeadCells + " " + averageLivingCells);
        System.out.println(b.getNumberOfCells());
        ArrayList<ArrayList> data = new ArrayList<>();

    }

    /**
     *  This method is responsible for generating all the data for the <code>XYChart</code>. It is structured in way that prevents 
     * iteration of the board more than once and will therefore look dirty, but it doesn't mind because it is secretly very naughty.
     * 
     * <strong>It uses many helper methods to perform it's computation. Some of these are:</strong>
     * 
     * <code>getLivingCells()</code>, 
     * <code>getPopulationChange()</code>, 
     * <code>getLivingCells()</code>, 
     * <code>convertBoardToFloat()</code>, 
     * <code>getLivingCells()</code>, 
     * <code>getSimilairtyMeasure()</code>, 
     * 
     * @return <code>XYChart.Series[]</code> an array with 3 series containing all the data necessary for populating an <code>XYChart</code>
     */
    public XYChart.Series[] getStatistics() {
        //Define series to be returned
        XYChart.Series livingCells = new XYChart.Series();
        livingCells.getData().add(new XYChart.Data(b.getGenerationCount(), b.getLivingCellCount()));
        livingCells.setName("Living Cells");

        XYChart.Series popluationChange = new XYChart.Series();
        popluationChange.getData().add(new XYChart.Data(b.getGenerationCount(), 0));
        popluationChange.setName("Population Change");

        XYChart.Series similiarityMeasure = new XYChart.Series();
        similiarityMeasure.setName("Similarity Measure");

        //Needs initial generation data.
        livingCellsPerGeneration.put(b.getGenerationCount(), b.getLivingCellCount());
        generations.put(b.getGenerationCount(), new BoardDynamic(b.getBoard()));
        averageLivingCells = b.getLivingCellCount();
        averageDeadCells = b.getNumberOfCells() - b.getLivingCellCount();
        //This loop populates living Cells and populationChange series
        while (b.getGenerationCount() <= lastGeneration) {
            int prevPopulation = b.getLivingCellCount();
            b.nextGeneration();
            averageLivingCells += b.getLivingCellCount();
            averageDeadCells += (b.getNumberOfCells() - b.getLivingCellCount());
            generations.put(b.getGenerationCount(), new BoardDynamic(b.getBoard()));
            livingCellsPerGeneration.put(b.getGenerationCount(), b.getLivingCellCount());
            livingCells.getData().add(getLivingCells());
            popluationChange.getData().add(new XYChart.Data(b.getGenerationCount(), getPopulationChange(prevPopulation)));
            floatBoards.put(b.getGenerationCount(), convertBoardToFloat(b.getGenerationCount()));
        }
        averageLivingCells = averageLivingCells / iterations;
        averageDeadCells = averageDeadCells / iterations;
        System.out.println("Average living: " + averageLivingCells + " average dead" + averageDeadCells);
        //Populate similarity measure
        int generationCount = firstGeneration + 1;
        while (generationCount <= lastGeneration) {
            similiarityMeasure.getData().add(getSimilarityMeasure(generationCount, floatBoards));
            generationCount++;
        }

        //Return series.
        XYChart.Series[] series = new XYChart.Series[3];
        series[0] = livingCells;
        series[1] = popluationChange;
        series[2] = similiarityMeasure;
        return series;
    }
/**
 * returns the generation count and living cells of the current generation.
 * @return <code>XYChart.Data</code> 
 */
    private XYChart.Data getLivingCells() {
        return new XYChart.Data(b.getGenerationCount(), b.getLivingCellCount());
    }
    /**
     * Returns the poplutaion difference between the current generation and the previous one.
     * @param prevPopulation <code>int</code> containt the total population of living cells to the previous generation.
     * @return <int> population difference.
     */
    private int getPopulationChange(int prevPopulation) {
        int diff = b.getLivingCellCount() - prevPopulation;
        return diff;
    }

    /**
     *
     * @param currentGeneration <code>int</code> the generation that is going to be converted
     * @return <code>BoardDynamic</code> as a <code>float</code> 
     */
    private float convertBoardToFloat(int currentGeneration) {
        float a = 0.5f, be = 0.85f, y = 0.25f;
        float af = a * b.getLivingCellCount();
        float bf = be * (b.getLivingCellCount() - livingCellsPerGeneration.get(currentGeneration - 1));
        System.out.println("IndexSum: " + b.getIndexSum());
        float yg = y * b.getIndexSum();
        float board = af + bf + yg;
        System.out.println("floatBoard: " + board);
        return board;
    }

    /**
     * Finds the highest similarity for a generation and converts that number to an <code>int</code>.
     *
     * @param generation
     * @param floatBoards <code>ArrayList&lt;ArrayList&lt;String&gt;&gt;</code> containing the geometric value of all the boards that shall be compared. 
     * @return <code>int</code> the most similar generation
     */
    private XYChart.Data getSimilarityMeasure(int generation, HashMap<Integer, Float> floatBoards) {
        XYChart.Data similarityMeasure = new XYChart.Data();
        float highestfloat = 0;
        int i = firstGeneration + 1; // First generation can not have a similarity measure because floatboards use current generation - 1 as part of the calculation.
        float floatConstat = floatBoards.get(generation);
        while (i <= lastGeneration) {
            if (i != generation) {
                float newFloat = Float.min(floatConstat, floatBoards.get(i).floatValue()) / Float.max(floatConstat, floatBoards.get(i).floatValue());
                if (newFloat > highestfloat) {
                    highestfloat = newFloat;
                }
            }
            i++;
        }
        int similarity = getSimilairtyFromFloat(highestfloat);
        similarityMeasure.setYValue(similarity);
        similarityMeasure.setXValue(generation);
        return similarityMeasure;
    }

    /**
     * Converts a float to integer
     * @param highestFloat <code>float</code> float to be converted
     * @return <code>int</code> similarity value 
     */
    public int getSimilairtyFromFloat (float highestFloat){
        int similairty = (int) Math.floor((double) highestFloat * 100);
        return similairty;
    }
    /**
     * finds the most similar generation of a specific generation.
     *
     * @param generation <code>int</code> the generation that is to be compared
     * @return <code>int</code> the most similar generation
     */
    public int findMostSimilar(int generation) {
        float generationToCompare = floatBoards.get(generation);
        float highestfloat = 0f;
        int mostSimilarGeneration = 0;
        int i = this.firstGeneration + 1;
        while (i <= this.lastGeneration) {
            if (i != generation) {
                float newFloat = Float.min(generationToCompare, floatBoards.get(i).floatValue()) / Float.max(generationToCompare, floatBoards.get(i).floatValue());
                if (newFloat > highestfloat) {
                    mostSimilarGeneration = i;
                    highestfloat = newFloat;
                }
            }
            i++;
        }
        return mostSimilarGeneration;
    }

    /**
     * Returns a reference to the selected board
     *
     * @param generation <code>int</code> generation of the desired board
     * @return <code>BoardDynamic</code> reference to the desired board.
     */
    public BoardDynamic getSelectedIteration(int generation) {
        return generations.get(generation);
    }
}
