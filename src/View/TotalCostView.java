package View;

import Controller.TotalCostController;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TotalCostView extends Application {
    private TotalCostController totalCostController;
    private TableView<CostItem> tableView;
    private ComboBox<String> timeframeComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        totalCostController = new TotalCostController(); // Instantiate controller

        tableView = createTableView();
        timeframeComboBox = createTimeframeComboBox();

        Button calculateButton = new Button("Calculate Total Cost");
        calculateButton.setOnAction(event -> {
            String selectedTimeframe = timeframeComboBox.getValue();
            double totalAdminSalary = totalCostController.calculateSalaryByRoleAndTimeframe("Administrator", selectedTimeframe);
            double totalManagerSalary = totalCostController.calculateSalaryByRoleAndTimeframe("Manager", selectedTimeframe);
            double totalLibrarianSalary = totalCostController.calculateSalaryByRoleAndTimeframe("Librarian", selectedTimeframe);
            double totalBookCostWithTax = totalCostController.calculateTotalBookCostWithTaxByTimeframe(selectedTimeframe);

            double totalEmployeeCost = totalAdminSalary + totalManagerSalary + totalLibrarianSalary;
            double totalCost = totalEmployeeCost + totalBookCostWithTax;

            // Update table with new data
            ObservableList<CostItem> data = FXCollections.observableArrayList(
                    new CostItem("Total Administrator Costs", totalAdminSalary),
                    new CostItem("Total Manager Costs", totalManagerSalary),
                    new CostItem("Total Librarian Costs", totalLibrarianSalary),
                    new CostItem("Total Book Costs (with 20% tax)", totalBookCostWithTax),
                    new CostItem("Total Employee Costs", totalEmployeeCost),
                    new CostItem("Total Overall Cost", totalCost)
            );
            tableView.setItems(data);
        });

        VBox root = new VBox(10, timeframeComboBox, calculateButton, tableView);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Total Cost Calculation");
        stage.setScene(scene);
        stage.show();
    }

    private TableView<CostItem> createTableView() {
        TableView<CostItem> tableView = new TableView<>();

        TableColumn<CostItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<CostItem, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        tableView.getColumns().addAll(descriptionColumn, amountColumn);

        return tableView;
    }

    private ComboBox<String> createTimeframeComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Daily", "Weekly", "Monthly", "Yearly");
        comboBox.setValue("Daily"); // Default value
        return comboBox;
    }

    public static class CostItem {
        private final SimpleDoubleProperty amount;
        private final String description;

        public CostItem(String description, double amount) {
            this.description = description;
            this.amount = new SimpleDoubleProperty(amount);
        }

        public double getAmount() {
            return amount.get();
        }

        public String getDescription() {
            return description;
        }
    }
}
