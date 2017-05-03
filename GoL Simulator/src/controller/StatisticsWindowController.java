package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.BoardDynamic;
import model.Statistics;
import view.GameCanvas;

/**
 * FXML controller class used to pull data from a <code>Statistics</code> object
 * and display it to the user
 */
public class StatisticsWindowController {

    /**
     * The chart used to present data
     */
    @FXML
    private LineChart chart;
    /**
     * root node
     */
    @FXML
    private FlowPane root;
    /**
     * xAxis to the chart
     */
    @FXML
    private NumberAxis xAxis;
    /**
     * Input that allows the user to select amount of data to generate
     */
    @FXML
    private TextField txtIterations;
    @FXML
    private HBox chartWrapper;
    @FXML
    private VBox dialogWrapper;
    @FXML
    private GameCanvas leftCanvas;
    @FXML
    private GameCanvas rightCanvas;
    @FXML
    private VBox detailedInformation;
    /**
     * Changing this combobox fires an event calling
     */
    @FXML
    private ComboBox<String> findSimilarComboBox;
    @FXML
    private ComboBox<String> leftCanvasComboBox;
    @FXML
    private ComboBox<String> rightCanvasComboBox;

    private BoardDynamic b;
    private Statistics s;

    /**
     * Initialized the FXML fields and sets a Listener on the width of the root
     * node, calling changeWidth() if the width changes. It also adds Listeners
     * to the combo boxes.
     */
    public void initialize() {
        root.getChildren().remove(chartWrapper);
        root.getChildren().remove(detailedInformation);
        root.widthProperty().addListener((BindingHelperObserver, oldValue, newValue) -> changeWidth());
        leftCanvasComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> showSelection(leftCanvasComboBox, leftCanvas)
        );
        rightCanvasComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> showSelection(rightCanvasComboBox, rightCanvas)
        );
        findSimilarComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> setSimilarCanvas()
        );
    }

    /*
     * This method is called every time the width property of the root node changes
     */
    private void changeWidth() {
        if (s != null) {
            double d = root.getWidth();
            leftCanvas.setWidth(d / 2);
            rightCanvas.setWidth(d / 2);
            chart.setPrefWidth(d);
            setInitialCanvasGeneration();
        }
    }

    /**
     * This method is called every time the value changes in the two ComboBoxes
     * and changes the canvas based on the value of the ComboBox
     */
    private void showSelection(ComboBox selection, GameCanvas c) {
        Object o = selection.getValue();
        int i = Integer.parseInt(o.toString());
        c.drawBoard(s.getSelectedGeneration(i).getBoard());
    }

    /*
     * This method sets the generations that should be displayed the first time showInformation() is called
     * Default is to show the first generation on the left canvas and the last generation on the right canvas
     */
    private void setInitialCanvasGeneration() {
        Object o = leftCanvasComboBox.getValue();
        int i = Integer.parseInt(o.toString());
        leftCanvas.drawBoard(s.getSelectedGeneration(i).getBoard());
        o = rightCanvasComboBox.getValue();
        i = Integer.parseInt(o.toString());
        rightCanvas.drawBoard(s.getSelectedGeneration(i).getBoard());
    }

    /*
     * This method populates the ComboBoxes with the generations used in the Statistics class
     */
    private void populateComboBoxes() {
        int i = s.getFirstGeneration();
        while (i < s.getIterations() + s.getFirstGeneration()) {
            String o = Integer.toString(i);
            leftCanvasComboBox.getItems().add(o);
            rightCanvasComboBox.getItems().add(o);
            if (i == s.getFirstGeneration()) {
                i++;
                continue;
            }
            findSimilarComboBox.getItems().add(o);
            i++;
        }
        leftCanvasComboBox.setValue(Integer.toString(s.getFirstGeneration()));
        rightCanvasComboBox.setValue(Integer.toString(s.getLastGeneration()));
    }

    /*
     * This method starts the process of creating  the data used in the XYChart
     * by calling getStatistics()
     */
    public void getStatistics() {
        s = new Statistics(b, Integer.parseInt(txtIterations.getText()));
        XYChart.Series[] series = s.getStatistics();
        for (XYChart.Series se : series) {
            chart.getData().add(se);
        }
    }

    /*
     * This method is called every time the value findSimilarComboBox changes
     * and display the most similar generation for the selected generation in 
     * the  right canvas
     */
    private void setSimilarCanvas() {
        Object o = findSimilarComboBox.getValue();
        int i = Integer.parseInt(o.toString());
        leftCanvasComboBox.setValue(findSimilarComboBox.getValue());
        rightCanvasComboBox.setValue(Integer.toString(s.findMostSimilar(i)));
    }

    /*
     * This displays the canvas' after user input.
     */
    private void defineCanvas() {
        leftCanvas.widthProperty().set(root.getWidth() / 2);
        rightCanvas.widthProperty().set(root.getWidth() / 2);
        leftCanvas.heightProperty().set(300);
        rightCanvas.heightProperty().set(300);
    }

    /**
     * This class drives the entire controller. Various method are called after
     * user input to help define the layout and show the generated data.
     */
    @FXML
    public void showInformation() {
        if (s == null) {
            shuffleSceneGraph();
            defineChart();
            defineCanvas();
            getStatistics();
            populateComboBoxes();
        } else {
            chart.getData().clear();
            xAxis.setUpperBound(b.getGenerationCount() + Integer.parseInt(txtIterations.getText()));
            s.setIterationsAndLastGeneration(Integer.parseInt(txtIterations.getText()));
            getStatistics();
            populateComboBoxes();
        }
    }

    /*
     * This method is called after the user has requested the generation of data
     * and shuffles the most relevant scene elements to the top of the fxml
     * document.
     */
    private void shuffleSceneGraph() {
        root.getChildren().add(chartWrapper);
        root.getChildren().add(detailedInformation);
        root.getChildren().remove(dialogWrapper);
        root.getChildren().add(dialogWrapper);
    }

    /**
     * This method defines how the chart should look
     */
    private void defineChart() {
        chart.setPrefWidth(root.getWidth());
        xAxis.setTickUnit(1);
        xAxis.setLowerBound(b.getGenerationCount());
        xAxis.setUpperBound(b.getGenerationCount() + Integer.parseInt(txtIterations.getText()));
        xAxis.setAutoRanging(false);
        /**
         * Alternative way to access axis with stack variables instead
         * NumberAxis x = (NumberAxis)chart.getXAxis();
         */
    }

    /**
     * Used in MainWindowController to store a reference of the active board
     * that is later used to generate data.
     */
    public void setBoard(BoardDynamic b) {
        this.b = b;
    }
}
