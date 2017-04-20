package controller;

import com.sun.javafx.binding.BindingHelperObserver;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import model.Board;
import model.Statistics;
import view.ResizableCanvas;

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
    private VBox leftCanvasGroup;
    @FXML
    private VBox rightCanvasGroup;
    @FXML
    private HBox findSimilarIteration;
    @FXML
    private ResizableCanvas leftCanvas;
    @FXML
    private ResizableCanvas rightCanvas;
    @FXML
    private VBox detailedInformation;
    @FXML
    private ComboBox<String> findSimilarComboBox;
    @FXML
    private ComboBox<String> leftCanvasComboBox;
    @FXML
    private ComboBox<String> rightCanvasComboBox;
    private Board b;
    private Statistics s;
    private boolean extendStatistics;

    public void initialize() {
        root.getChildren().remove(chartWrapper);
        root.getChildren().remove(detailedInformation);
        extendStatistics = false;
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

    public void showSelection(ComboBox selection, ResizableCanvas c) {
        Object o = selection.getValue();
        int i = Integer.parseInt(o.toString());
        c.draw(s.getSelectedIteration(i));
    }

    public void refreshCanvas() {
        Object o = leftCanvasComboBox.getValue();
        int i = Integer.parseInt(o.toString());
        leftCanvas.draw(s.getSelectedIteration(i));
        o = rightCanvasComboBox.getValue();
        i = Integer.parseInt(o.toString());
        rightCanvas.draw(s.getSelectedIteration(i));
    }

    public void populateComboBoxes() {
        //findSimilarComboBox = new ComboBox<String>();
        //leftCanvasComboBox = new ComboBox<String>();
        //rightCanvasComboBox = new ComboBox<String>();
        int i = s.getFirstGeneration();
        while (i < s.getIterations() + s.getFirstGeneration()) {
            String o = Integer.toString(i);
            leftCanvasComboBox.getItems().add(o);
            rightCanvasComboBox.getItems().add(o);
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
                -> System.out.println(leftCanvasComboBox.getValue())
        );
        leftCanvasComboBox.setValue(Integer.toString(s.getFirstGeneration()));
        rightCanvasComboBox.setValue(Integer.toString(s.getLastGeneration()));

    }

    @FXML

    public void getStatistics() {
        s = new Statistics(b, Integer.parseInt(txtIterations.getText()));
        XYChart.Series[] series = s.getStatistics();
        for (XYChart.Series se : series) {
            chart.getData().add(se);

        }
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
            extendStatistics = true;
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

    public void setBoard(Board b) {
        this.b = b;
    }
}
