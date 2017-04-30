package model;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.chart.XYChart;

/**
 *
 * @author peven
 */
public class Statistics {

    private BoardDynamic b;
    private int iterations;
    private int firstGeneration;
    private int lastGeneration;
    private float averageDeadCells, averageLivingCells;
    private HashMap<Integer, Float> floatBoards;
    private HashMap<Integer, Integer> livingCellsPerGeneration;
    private HashMap<Integer, BoardDynamic> generations;

    public Statistics(BoardDynamic b, int iterations) {
        this.b = b.deepCopy();
        firstGeneration = b.getGenerationCount();
        this.iterations = iterations;
        livingCellsPerGeneration = new HashMap<Integer, Integer>();
        floatBoards = new HashMap<Integer, Float>();
        generations = new HashMap<Integer, BoardDynamic>();
        setLastGeneration();
    }

    private void setLastGeneration() {
        this.lastGeneration = firstGeneration + iterations - 1;
    }

    public int getLastGeneration() {
        return this.lastGeneration;
    }

    public void setIterationsAndLastGeneration(int i) {
        this.iterations = i;
        setLastGeneration();
    }

    public int getIterations() {
        return this.iterations;
    }

    public int getFirstGeneration() {
        return this.firstGeneration;
    }

    public ArrayList<ArrayList> generateAudioSequence() {
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
        averageDeadCells = averageDeadCells / iterations;
        averageLivingCells = averageLivingCells / iterations;
        System.out.println(averageDeadCells + " " + averageLivingCells);
        ArrayList<ArrayList> data = new ArrayList<>();
        data.add(cellsPerGeneratin);
        data.add(populationChange);
        return data;
    }

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
        while (b.getGenerationCount() < lastGeneration) {
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
        return new XYChart.Data(b.getGenerationCount(), b.getLivingCellCount());
    }

    private int getPopulationChange(int prevPopulation) {
        int diff = b.getLivingCellCount() - prevPopulation;
        return diff;
    }

    public void getAverage() {
        for (int i = 0; i < iterations; i++) {

        }
    }

    private float convertBoardToFloat(int currentGeneration) {
        float a = 0.5f, be = 0.95f, y = 0.25f;
        float af = a * b.getLivingCellCount();
        float bf = be * (b.getLivingCellCount() - livingCellsPerGeneration.get(currentGeneration - 1));
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
        while (i <= lastGeneration) {
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

    public int findMostSimilar(int generation) {
        float generationToCompare = floatBoards.get(generation);
        float highestfloat = 0f;
        int mostSimilarGeneration = 0;
        int i = this.firstGeneration + 1;
        while (i < this.lastGeneration) {
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

    public BoardDynamic getSelectedIteration(int generation) {
        return generations.get(generation);
    }
}
