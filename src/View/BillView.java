package View;

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

import java.io.*;
import java.util.Scanner;

public class BillView extends Application {
    private static final String FILE_PATH = "billData.txt";
    private static final String PRINTABLE_BILL_PATH = "printableBills";
    private static final String BOOK_DATA_PATH = "Bookstore_Software_Project/Bookstore/src/bookData.txt";
    private static final String BILL_NUMBER_FILE = "Bookstore_Software_Project/billNumber.txt";

    private double totalPrice = 0;
    private int billNumber;
    private Label totalLabel;
    private Label billNumberLabel;

    private TableView<Book> bookTableView;
    private ObservableList<Book> books;

    public BillView() throws IOException {
        loadBillNumber();
        books = FXCollections.observableArrayList();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage billStage) {
        billStage.setTitle("Create bill");

        double rectangleWidth = 400;
        double rectangleHeight = 600;

        Rectangle background = new Rectangle(rectangleWidth, rectangleHeight);
        background.setFill(Color.LIGHTBLUE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label titleLabel = new Label("Title: ");
        TextField titleField = new TextField();
        grid.add(titleLabel, 0, 0);
        grid.add(titleField, 1, 0);

        Label quantityLabel = new Label("Quantity: ");
        TextField quantityField = new TextField();
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
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Book, Double> totalPriceColumn = new TableColumn<>("Total Price");
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        bookTableView.getColumns().addAll(titleColumn, quantityColumn, totalPriceColumn);
        bookTableView.setItems(books);

        grid.add(bookTableView, 0, 11, 3, 1);

        Button loadBookButton = new Button("Load Book Info");
        loadBookButton.setOnAction(e -> {
            String title = titleField.getText();
            Book book = loadBookData(title);

            if (book != null) {
                quantityField.setText(String.valueOf(book.getStock()));
            } else {
                showAlert("Title not found", "The entered title does not exist in the book data.");
            }
        });

        Button addBookButton = new Button("Add Book");
        addBookButton.setOnAction(e -> {
            String title = titleField.getText();
            Book book = loadBookData(title);

            if (book != null) {
                int quantity = Integer.parseInt(quantityField.getText());

                double bookTotalPrice = book.getSellingPrice() * quantity;
                totalPrice += bookTotalPrice;

                totalLabel.setText("Total Price: $" + totalPrice);

                billNumberLabel.setText("Bill Number: " + billNumber);

                books.add(new Book(title, quantity, bookTotalPrice));

                titleField.clear();
                quantityField.clear();
            } else {
                showAlert("Title not found", "The entered title does not exist in the book data.");
            }
        });

        Button submitButton = new Button("Create Bill");
        submitButton.setOnAction(e -> {
            processAddedBooks();
            books.clear();
            totalPrice = 0;
            totalLabel.setText("Total Price: $" + totalPrice);
            saveBillNumber();
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

    private void processAddedBooks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Book book : books) {
                String title = book.getTitle();
                int quantity = book.getStock();
                double totalPrice = book.getSellingPrice();

                String bookInfo = "Title: " + title + ", Quantity: " + quantity + ", Total Price: $" + totalPrice;
                billNumber++;

                writer.write(bookInfo);
                writer.newLine();

                File printableBillDir = new File(PRINTABLE_BILL_PATH);
                if (!printableBillDir.exists()) {
                    if (!printableBillDir.mkdirs()) {
                        System.err.println("Failed to create printableBills directory.");
                        return;
                    }
                }

                String printableBillPath = PRINTABLE_BILL_PATH + "/Bill_" + (billNumber - 1) + ".txt";
                try (BufferedWriter billWriter = new BufferedWriter(new FileWriter(printableBillPath))) {
                    billWriter.write("Bill Number: " + (billNumber - 1));
                    billWriter.newLine();
                    billWriter.write(bookInfo);
                    billWriter.newLine();
                    billWriter.write("Total Price: $" + totalPrice);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private Book loadBookData(String title) {
        try (Scanner scanner = new Scanner(new File(BOOK_DATA_PATH))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length == 8) {
                    String bookTitle = parts[1].trim();
                    if (bookTitle.equalsIgnoreCase(title.trim())) {
                        String category = parts[2].trim();
                        String isbn = parts[3].trim();
                        String author = parts[4].trim();
                        double price = Double.parseDouble(parts[5].trim());
                        int quantity = Integer.parseInt(parts[6].trim());

                        return new Book(title, category, isbn, author, price, quantity);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadBillNumber() {
        try (Scanner scanner = new Scanner(new File(BILL_NUMBER_FILE))) {
            if (scanner.hasNextInt()) {
                billNumber = scanner.nextInt();
            }
        } catch (FileNotFoundException e) {

            billNumber = 1;
        }
    }

    private void saveBillNumber() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BILL_NUMBER_FILE))) {
            writer.write(Integer.toString(billNumber));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
