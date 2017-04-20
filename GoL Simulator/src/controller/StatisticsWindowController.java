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

public class StatisticsWindowController {

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
    @FXML
    private GameCanvas leftCanvas;
    @FXML
    private GameCanvas rightCanvas;
    @FXML
    private VBox detailedInformation;
    @FXML
    private ComboBox<String> findSimilarComboBox;
    @FXML
    private ComboBox<String> leftCanvasComboBox;
    @FXML
    private ComboBox<String> rightCanvasComboBox;

    private BoardDynamic b;
    private Statistics s;

    public void initialize() {
        root.getChildren().remove(chartWrapper);
        root.getChildren().remove(detailedInformation);
        root.widthProperty().addListener((BindingHelperObserver, oldValue, newValue) -> changeWidth());
    }

    public void changeWidth() {
        if (s != null) {
            double d = root.getWidth();
            leftCanvas.setWidth(d / 2);
            rightCanvas.setWidth(d / 2);
            chart.setPrefWidth(d);
            refreshCanvas();
        }
    }

    public void showSelection(ComboBox selection, GameCanvas c) {
        Object o = selection.getValue();
        int i = Integer.parseInt(o.toString());
        c.drawBoard(s.getSelectedIteration(i).getBoard());
    }

    public void refreshCanvas() {
        Object o = leftCanvasComboBox.getValue();
        int i = Integer.parseInt(o.toString());
        leftCanvas.drawBoard(s.getSelectedIteration(i).getBoard());
        o = rightCanvasComboBox.getValue();
        i = Integer.parseInt(o.toString());
        rightCanvas.drawBoard(s.getSelectedIteration(i).getBoard());
    }

    public void populateComboBoxes() {
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
        Tooltip findSimilarTooltip = new Tooltip();
        findSimilarTooltip.setText("Select a generation to find the most similar generation");
        findSimilarComboBox.setTooltip(findSimilarTooltip);
        Tooltip canvasTooltip = new Tooltip();
        canvasTooltip.setText("Select a generation to display it below");
        leftCanvasComboBox.setTooltip(canvasTooltip);
        rightCanvasComboBox.setTooltip(canvasTooltip);
        leftCanvasComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> showSelection(leftCanvasComboBox, leftCanvas)
        );
        rightCanvasComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> showSelection(rightCanvasComboBox, rightCanvas)
        );
        findSimilarComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> setSimilarCanvas()
        );
        leftCanvasComboBox.setValue(Integer.toString(s.getFirstGeneration()));
        rightCanvasComboBox.setValue(Integer.toString(s.getLastGeneration()));

    }

    public void getStatistics() {
        s = new Statistics(b, Integer.parseInt(txtIterations.getText()));
        XYChart.Series[] series = s.getStatistics();
        for (XYChart.Series se : series) {
            chart.getData().add(se);

        }
    }

    public void setSimilarCanvas() {
        Object o = findSimilarComboBox.getValue();
        int i = Integer.parseInt(o.toString());
        leftCanvasComboBox.setValue(findSimilarComboBox.getValue());
        rightCanvasComboBox.setValue(Integer.toString(s.findMostSimilar(i)));
    }

    public void defineCanvas() {
        leftCanvas.widthProperty().set(root.getWidth() / 2);
        rightCanvas.widthProperty().set(root.getWidth() / 2);
        leftCanvas.heightProperty().set(300);
        rightCanvas.heightProperty().set(300);

    }

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
            s.setIterations(Integer.parseInt(txtIterations.getText()));
            getStatistics();
            populateComboBoxes();
        }
    }

    private void shuffleSceneGraph() {
        root.getChildren().add(chartWrapper);
        root.getChildren().add(detailedInformation);
        root.getChildren().remove(dialogWrapper);
        root.getChildren().add(dialogWrapper);
    }

    private void defineChart() {
        xAxis.setTickUnit(1);
        xAxis.setLowerBound(b.getGenerationCount());
        xAxis.setUpperBound(b.getGenerationCount() + Integer.parseInt(txtIterations.getText()));
        xAxis.setAutoRanging(false);
        /**
         * Alternative way to access axis with stack variables instead
         * NumberAxis x = (NumberAxis)chart.getXAxis();
         */
    }

    public void setBoard(BoardDynamic b) {
        this.b = b;
    }
}
