package View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TotalCostView extends Application {
    //DO IT SO THAT IT SHOWS HOW MUCH YOU PAY THE EMPLOYEES, HOW MUCH THE BOOKS COST ETC ETC
    @Override
    public void start(Stage stage) throws Exception {
        double totalCost = 2700.0;


        Label totalCostLabel = new Label("The Total Cost is: $" + totalCost);


        totalCostLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: blue;");


        StackPane root = new StackPane();
        root.getChildren().add(totalCostLabel);


        root.setStyle("-fx-border-color: black; -fx-border-width: 4px; -fx-border-radius: 8px;");


        Scene scene = new Scene(root, 600, 400);


        stage.setTitle("Total Cost View");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
