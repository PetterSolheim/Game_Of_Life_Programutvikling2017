package gol.simulator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.TestBoard;
import view.ResizableCanvas;

public class GoLSimulator extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainWindow.fxml"));
        //Group root = new Group();
        Scene scene = new Scene(root, 1000, 1000);
        //ResizableCanvas canvas = new ResizableCanvas(1000, 1000);
        //TestBoard t = new TestBoard();        
        //canvas.draw(t);
        //root.getChildren().add(canvas);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
