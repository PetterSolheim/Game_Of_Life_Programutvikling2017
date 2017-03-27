package controller;

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
    public void getStatistics (){
        shuffleSceneGraph();
        defineGraph();
        XYChart.Series livingCells = new XYChart.Series();
        livingCells.getData().add(new XYChart.Data(b.getGenerationCount(), b.getLivingCells()));
        livingCells.setName("Living Cells");
        XYChart.Series popluationChange = new XYChart.Series();
        popluationChange.getData().add(new XYChart.Data(b.getGenerationCount(), 0));
        popluationChange.setName("Population Change");
        int iterations = Integer.parseInt(txtIterations.getText());
        do{
            int prevPopulation = b.getLivingCells();
            b.nextGeneration();
            livingCells.getData().add(getLivingCells());
            popluationChange.getData().add(getPopulationChange(prevPopulation));
        }
        while (b.getGenerationCount() < iterations);
        chart.getData().add(livingCells);
        chart.getData().add(popluationChange);
    }
    private XYChart.Data getLivingCells (){
        return new XYChart.Data(b.getGenerationCount(), b.getLivingCells());
    }
    private void shuffleSceneGraph (){
        root.getChildren().add(chartWrapper);
        root.getChildren().remove(dialogWrapper);
        root.getChildren().add(dialogWrapper);
    }
    private void defineGraph (){
        //xAxis.setTickUnit(1);
        xAxis.setLowerBound(b.getGenerationCount());
        xAxis.setUpperBound(b.getGenerationCount() + Integer.parseInt(txtIterations.getText()));
        xAxis.setAutoRanging(false);
        /** -- Alternative way to access axis using stack variables
        NumberAxis x = (NumberAxis)chart.getXAxis();
        **/
    }
    private XYChart.Data getPopulationChange (int prevPopulation){   
        int diff = b.getLivingCells() - prevPopulation;
        XYChart.Data populationChange = new XYChart.Data(b.getGenerationCount(),diff);
        return populationChange;
    }
    private XYChart.Data getSimilarityMeasure (){
        float a = 0.5f, b = 3.0f, y = 0.25f;
        XYChart.Data similarityMeasure = new XYChart.Data();
        return similarityMeasure;
    }
    public void setBoard (Board b){
        this.b= b;
    }
}
