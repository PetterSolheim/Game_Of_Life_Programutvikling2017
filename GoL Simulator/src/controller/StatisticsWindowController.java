package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Board;

public class StatisticsWindowController {
    
    private Board b;
    private int firstGeneration;
    private int iterations;
    @FXML
    private LineChart chart;
    @FXML
    private FlowPane root;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private TextField txtIterations;
    @FXML
    private HBox chartWrapper;
    @FXML
    private VBox dialogWrapper;
    
    public void initialize() {
        root.getChildren().remove(chartWrapper);
    }
    
    @FXML
    public void getStatistics() {
        iterations = Integer.parseInt(txtIterations.getText());
        firstGeneration = b.getGenerationCount();
        HashMap<Integer, Float> floatBoards = new HashMap<Integer, Float>();
        shuffleSceneGraph();
        defineGraph();
        XYChart.Series livingCells = new XYChart.Series();
        livingCells.getData().add(new XYChart.Data(b.getGenerationCount(), b.getLivingCells()));
        livingCells.setName("Living Cells");
        XYChart.Series popluationChange = new XYChart.Series();
        popluationChange.getData().add(new XYChart.Data(b.getGenerationCount(), 0));
        popluationChange.setName("Population Change");
        
        XYChart.Series similiarityMeasure = new XYChart.Series();
        similiarityMeasure.setName("similarity Measure");
        while (b.getGenerationCount() < iterations) {
            int prevPopulation = b.getLivingCells();
            floatBoards.put(b.getGenerationCount(), convertBoardToFloat());
            b.nextGeneration();
            livingCells.getData().add(getLivingCells());
            popluationChange.getData().add(getPopulationChange(prevPopulation));
        }
        int generationCount = firstGeneration;
        while (generationCount < iterations) {
            similiarityMeasure.getData().add(getSimilarityMeasure(generationCount, floatBoards));
            generationCount++;
        }
        chart.getData().add(livingCells);
        chart.getData().add(popluationChange);
        chart.getData().add(similiarityMeasure);
    }
    
    private XYChart.Data getLivingCells() {
        return new XYChart.Data(b.getGenerationCount(), b.getLivingCells());
    }
    
    private void shuffleSceneGraph() {
        root.getChildren().add(chartWrapper);
        root.getChildren().remove(dialogWrapper);
        root.getChildren().add(dialogWrapper);
    }
    
    private void defineGraph() {
        //xAxis.setTickUnit(1);
        xAxis.setLowerBound(b.getGenerationCount());
        xAxis.setUpperBound(b.getGenerationCount() + Integer.parseInt(txtIterations.getText()));
        xAxis.setAutoRanging(false);
        /**
         * Alternative way to access axis using stack variables NumberAxis x =
         * (NumberAxis)chart.getXAxis();
         */
    }
    
    private XYChart.Data getPopulationChange(int prevPopulation) {
        int diff = b.getLivingCells() - prevPopulation;
        XYChart.Data populationChange = new XYChart.Data(b.getGenerationCount(), diff);
        return populationChange;
    }
    
    private float convertBoardToFloat() {
        float a = 0.5f, be = 3.0f, y = 0.25f;
        float board = (a * b.getLivingCells()) + (be * b.getLivingCells()) + (y * b.getIndexSum());
        return board;
    }
    
    private XYChart.Data getSimilarityMeasure(int generation, HashMap<Integer, Float> floatBoards) {
        XYChart.Data similarityMeasure = new XYChart.Data();
        float highestfloat = 0;
        int i = firstGeneration;
        float floatConstat = floatBoards.get(generation).floatValue();
        while (i < iterations) {
            if (i == generation) {
                // Do nothing
            } else {
                float newFloat = Float.min(floatConstat, floatBoards.get(i).floatValue()) / Float.max(floatConstat, floatBoards.get(i).floatValue());
                if (newFloat > highestfloat) {
                    highestfloat = newFloat;
                }
            }
            i++;
        }
        System.out.println(highestfloat);
        int similarity = (int)Math.floor((double)highestfloat * 100);
        System.out.println(similarity);
        similarityMeasure.setYValue(similarity);
        similarityMeasure.setXValue(generation);
        return similarityMeasure;
    }
    
    public void setBoard(Board b) {
        this.b = b;
    }
}
