package View;

import Controller.CreateBillController;
import Model.Book;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class BillView extends Application {
    private Connection conn;

    private double totalPrice = 0;
    private int billNumber;
    private Label totalLabel;
    private Label billNumberLabel;

    TableView<Book> bookTableView;
    final ObservableList<Book> books;
    final CreateBillController createBillController;
    private static String librarianUsername;

    public BillView() {
        books = FXCollections.observableArrayList();
        createBillController = new CreateBillController();
    }

    public static void setLibrarianUsername(String username) {
        librarianUsername = username;
    }

    public static void main(String[] args) {
        setLibrarianUsername("Librarian2");
        launch(args);
    }

    @Override
    public void start(Stage billStage) {
        System.out.println("BillView started with librarianUsername: " + librarianUsername);

        billStage.setTitle("Create bill");

        double rectangleWidth = 400;
        double rectangleHeight = 600;

        Rectangle background = new Rectangle(rectangleWidth, rectangleHeight);
        background.setFill(Color.LIGHTBLUE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label isbnLabel = new Label("Isbn: ");
        ComboBox<String> isbnComboBox = new ComboBox<>();
        List<String> isbns = createBillController.getAllISBNsOrderedByStock();
        isbnComboBox.setItems(FXCollections.observableArrayList(isbns));

        grid.add(isbnLabel, 0, 0);
        grid.add(isbnComboBox, 1, 0);

        Label quantityLabel = new Label("Quantity: ");
        TextField quantityField = new TextField("0");
        quantityField.setDisable(true);
        grid.add(quantityLabel, 0, 6);
        grid.add(quantityField, 1, 6);

        totalLabel = new Label("Total Price: $" + totalPrice);
        totalLabel.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");
        grid.add(totalLabel, 0, 8, 2, 1);

        billNumberLabel = new Label("Bill Number " + billNumber);
        billNumberLabel.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");
        grid.add(billNumberLabel, 0, 9, 2, 1);

        bookTableView = new TableView<>();
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Book, Double> totalPriceColumn = new TableColumn<>("Total Price");
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        bookTableView.getColumns().addAll(Arrays.asList(titleColumn, quantityColumn, totalPriceColumn));
        bookTableView.setItems(books);

        grid.add(bookTableView, 0, 11, 3, 1);

        Button loadBookButton = new Button("Load Book Info");
        loadBookButton.setOnAction(e -> {
            String isbn = isbnComboBox.getValue();
            Book book = createBillController.getBookByISBN(isbn);

            if (book != null) {
                quantityField.setDisable(false);
                quantityField.setText("0");
            } else {
                showAlert("ISBN not found", "The entered ISBN does not exist in the book data.");
            }
        });

        Button addBookButton = new Button("Add Book");
        addBookButton.setOnAction(e -> {
            String isbn = isbnComboBox.getValue();
            Book book = createBillController.getBookByISBN(isbn);

            if (book != null) {
                String quantityText = quantityField.getText();
                if (!quantityText.matches("\\d+")) {
                    showAlert("Invalid Quantity", "Please enter a valid numeric quantity.");
                    return;
                }
                int quantity = Integer.parseInt(quantityText);
                if (quantity > book.getStock()) {
                    showAlert("Insufficient Stock", "The available stock is less than the requested quantity.");
                    return;
                }
                double bookTotalPrice = book.getSellingPrice() * quantity;
                totalPrice += bookTotalPrice;
                totalLabel.setText("Total Price: $" + totalPrice);
                billNumberLabel.setText("Bill Number: " + billNumber);
                books.add(new Book(book.getTitle(), quantity, bookTotalPrice));

                createBillController.updateBookStock(isbn, (book.getStock() - quantity));

                isbnComboBox.setValue(null);
                quantityField.clear();
                quantityField.setDisable(true);
            } else {
                showAlert("ISBN not found", "The entered ISBN does not exist in the book data.");
            }
        });

        Button submitButton = new Button("Create Bill");
        submitButton.setOnAction(e -> {
            processAddedBooks();
            books.clear();
            totalPrice = 0;
            totalLabel.setText("Total Price: $" + totalPrice);
        });

        loadBookButton.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15;");
        addBookButton.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15;");
        submitButton.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15;");

        grid.add(loadBookButton, 2, 0);
        grid.add(addBookButton, 2, 7);
        grid.add(submitButton, 1, 10);

        Pane root = new Pane(background, grid);

        Scene scene = new Scene(root, rectangleWidth, rectangleHeight);
        billStage.setScene(scene);
        billStage.show();
    }

    public void processAddedBooks() {
        for (Book book : books) {
            String title = book.getTitle();
            int quantity = book.getStock();
            double totalPrice = book.getSellingPrice();

            billNumber++;
            createBillController.saveBillToDatabase(librarianUsername, title, quantity, totalPrice);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
