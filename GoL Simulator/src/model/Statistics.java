package model;

import java.util.HashMap;
import javafx.scene.chart.XYChart;

/**
 *
 * @author peven
 */
public class Statistics {

    private Board b;
    private int iterations;
    private int firstGeneration;
    private int lastGeneration;
    private HashMap<Integer, Float> floatBoards;
    private HashMap<Integer, Integer> livingCellsPerGeneration;
    private HashMap<Integer, Board> generations;

    public Statistics(Board b, int iterations) {
        this.b = b;
        firstGeneration = b.getGenerationCount();
        this.iterations = iterations;
        livingCellsPerGeneration = new HashMap<Integer, Integer>();
        floatBoards = new HashMap<Integer, Float>();
        generations = new HashMap<Integer, Board>();
        setLastGeneration();
    }
    private void setLastGeneration (){
        this.lastGeneration = firstGeneration + iterations;
    }
    public int getLastGeneration (){
        return this.lastGeneration;
    }
    public void setIterations(int i) {
        this.iterations = i;
        setLastGeneration();
    }
    public int getIterations (){
        return this.iterations;
    }
    public int getFirstGeneration (){
        return this.firstGeneration;
    }
    public XYChart.Series[] getStatistics() {
        //Define series to be returned
        XYChart.Series livingCells = new XYChart.Series();
        livingCells.getData().add(new XYChart.Data(b.getGenerationCount(), b.getLivingCells()));
        livingCells.setName("Living Cells");

        XYChart.Series popluationChange = new XYChart.Series();
        popluationChange.getData().add(new XYChart.Data(b.getGenerationCount(), 0));
        popluationChange.setName("Population Change");

        XYChart.Series similiarityMeasure = new XYChart.Series();
        similiarityMeasure.setName("Similarity Measure");
        
        //Needs initial generation data.
        livingCellsPerGeneration.put(b.getGenerationCount(), b.getLivingCells());
        generations.put(b.getGenerationCount(), new Board(b.getBoard()));
        //Populate living Cells and populationChange
        while (b.getGenerationCount() < lastGeneration) {
            int prevPopulation = b.getLivingCells();
            b.nextGeneration();
            generations.put(b.getGenerationCount(), new Board(b.getBoard()));
            livingCellsPerGeneration.put(b.getGenerationCount(), b.getLivingCells());
            livingCells.getData().add(getLivingCells());
            popluationChange.getData().add(getPopulationChange(prevPopulation));
            floatBoards.put(b.getGenerationCount(), convertBoardToFloat(b.getGenerationCount()));
        }
        
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

    private XYChart.Data getLivingCells() {
        return new XYChart.Data(b.getGenerationCount(), b.getLivingCells());
    }

    private XYChart.Data getPopulationChange(int prevPopulation) {
        int diff = b.getLivingCells() - prevPopulation;
        XYChart.Data populationChange = new XYChart.Data(b.getGenerationCount(), diff);
        return populationChange;
    }

    private float convertBoardToFloat(int currentGeneration) {
        float a = 0.5f, be = 0.95f, y = 0.25f;
        float af = a * b.getLivingCells();
        float bf = be * (b.getLivingCells() - livingCellsPerGeneration.get(currentGeneration - 1));
        System.out.println("bf" + bf);
        float yg = y * b.getIndexSum();
        float board = af + bf + yg;
        return board;
    }

    private XYChart.Data getSimilarityMeasure(int generation, HashMap<Integer, Float> floatBoards) {
        XYChart.Data similarityMeasure = new XYChart.Data();
        float highestfloat = 0;
        int i = firstGeneration + 1; // First generation can not have a similarity measure because floatboards use current generation - 1 as part of the calculation.
        float floatConstat = floatBoards.get(generation);
        while (i < iterations) {
            if (i != generation) {
                float newFloat = Float.min(floatConstat, floatBoards.get(i).floatValue()) / Float.max(floatConstat, floatBoards.get(i).floatValue());
                if (newFloat > highestfloat) {
                    highestfloat = newFloat;
                }
            }
            i++;
        }
        System.out.println("Floaty " + highestfloat);
        int similarity = (int) Math.floor((double) highestfloat * 100);
        System.out.println("Mr. Int" + similarity);
        similarityMeasure.setYValue(similarity);
        similarityMeasure.setXValue(generation);
        return similarityMeasure;
    }
    public Board getSelectedIteration(int generation){
        return generations.get(generation);
    }
}