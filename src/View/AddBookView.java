package View;

import Controller.AddBookController;
import Model.Book;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AddBookView extends Application {

    private ImageView imageView;
    private TextField bookUrlField;
    private TextField bookNameField;
    private TextField categoryField;
    private TextField isbnField;
    private TextField authorField;
    private TextField sellingPriceField;
    private Button submitButton;
    private Button chooseImageButton;
    private AddBookController addBookController;
    private String imageUrl;
    private ComboBox<String> categoryComboBox;

    private Button searchButton;

    private static final String ISBN_REGEX = "\\d{4}-\\d{2}-\\d{4}";
    private static final String AUTHOR_REGEX = "^[a-zA-Z\\s]+$"; // Only letters and spaces

    List<String> bookCategories = Arrays.asList(
            "Fantasy", "Science Fiction", "Mystery", "Thriller", "Romance",
            "Biography", "Memoir", "History", "Self-help", "Cookbooks",
            "Young Adult", "Children's Books", "Poetry", "Drama/Plays",
            "Horror", "Travel", "Graphic Novels/Comics", "Science"
    );

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage addBookStage) {
        addBookStage.setTitle("Add Book");

        double rectangleWidth = 400;
        double rectangleHeight = 600;

        Rectangle background = new Rectangle(rectangleWidth, rectangleHeight);
        background.setFill(Color.LIGHTBLUE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label pictureBookLabel = new Label("Book URL Picture: ");
        bookUrlField = new TextField();
        bookUrlField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String pictureBookUrl = bookUrlField.getText();
                if (!pictureBookUrl.isEmpty()) {
                    imageUrl = pictureBookUrl;
                    Image image = new Image(pictureBookUrl);
                    imageView.setImage(image);
                }
            }
        });
        grid.add(pictureBookLabel, 0, 0);
        grid.add(bookUrlField, 1, 0);

        Label bookNameLabel = new Label("Book Name: ");
        bookNameField = new TextField();
        grid.add(bookNameLabel, 0, 1);
        grid.add(bookNameField, 1, 1);

        searchButton = new Button("Search");
        searchButton.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15;");
        searchButton.setOnAction(e -> handleSearch());
        grid.add(searchButton, 2, 1);

        Label categoryLabel = new Label("Category: ");
        categoryComboBox = new ComboBox<>(FXCollections.observableArrayList(bookCategories));
        categoryComboBox.setPromptText("Select Category");
        grid.add(categoryLabel, 0, 2);
        grid.add(categoryComboBox, 1, 2);

        Label isbnLabel = new Label("ISBN: ");
        isbnField = new TextField();
        grid.add(isbnLabel, 0, 3);
        grid.add(isbnField, 1, 3);

        Label authorLabel = new Label("Author: ");
        authorField = new TextField();
        grid.add(authorLabel, 0, 4);
        grid.add(authorField, 1, 4);

        Label sellingPriceLabel = new Label("Selling Price: ");
        sellingPriceField = new TextField();
        grid.add(sellingPriceLabel, 0, 5);
        grid.add(sellingPriceField, 1, 5);

        imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(225);
        grid.add(imageView, 1, 8);

        chooseImageButton = new Button("Choose Image");
        chooseImageButton.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15;");
        chooseImageButton.setOnAction(e -> chooseImage());
        grid.add(chooseImageButton, 1, 9);

        submitButton = new Button("Create");
        submitButton.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15;");
        grid.add(submitButton, 1, 10);
        submitButton.setOnAction(e -> handleSubmit());

        Font timesNewRomanBold = Font.font("Times New Roman", FontWeight.BOLD, Font.getDefault().getSize());
        pictureBookLabel.setFont(timesNewRomanBold);
        bookNameLabel.setFont(timesNewRomanBold);
        categoryLabel.setFont(timesNewRomanBold);
        isbnLabel.setFont(timesNewRomanBold);
        authorLabel.setFont(timesNewRomanBold);
        sellingPriceLabel.setFont(timesNewRomanBold);

        Pane root = new Pane(background, grid);
        root.setPrefSize(rectangleWidth, rectangleHeight);

        Scene scene = new Scene(root, rectangleWidth, rectangleHeight);
        addBookStage.setScene(scene);
        addBookStage.show();

        addBookController = new AddBookController();
    }

    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Book Image");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageUrl = file.toURI().toString();
            Image image = new Image(imageUrl);
            imageView.setImage(image);
        }
    }

    private void handleSubmit() {
        System.out.println("Submit button clicked.");

        String url = imageUrl != null ? imageUrl : bookUrlField.getText();
        String name = bookNameField.getText();
        String category = categoryComboBox.getValue();
        String isbn = isbnField.getText();
        String author = authorField.getText();
        double sellingPrice = 0.0;
        try {
            sellingPrice = Double.parseDouble(sellingPriceField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid selling price format.");
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Selling Price must be a number.");
            return;
        }

        if (!validateISBN(isbn)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "ISBN format is incorrect. Expected format: XXXX-XX-XXXX.");
            return;
        }

        if (!validateAuthor(author)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Author name must contain only letters and spaces.");
            return;
        }

        Book book = new Book(url, name, category, isbn, author, sellingPrice);

        boolean bookExists = addBookController.bookExistsByName(name);

        addBookController.saveBook(book);

        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Book Saved", "Book saved successfully and fields cleared.");
    }

    private boolean validateISBN(String isbn) {
        return isbn.matches(ISBN_REGEX);
    }

    private boolean validateAuthor(String author) {
        return author.matches(AUTHOR_REGEX);
    }

    private void clearFields() {
        bookUrlField.clear();
        bookNameField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        isbnField.clear();
        authorField.clear();
        sellingPriceField.clear();
        imageView.setImage(null);
        imageUrl = null;
    }

    private void handleSearch() {
        String title = bookNameField.getText();
        if (!title.isEmpty()) {
            Book existingBook = addBookController.getBookByName(title);
            if (existingBook != null) {
                bookUrlField.setText(existingBook.getUrl());
                categoryComboBox.setValue(existingBook.getCategory());
//                isbnField.setText(existingBook.getISBN());
                authorField.setText(existingBook.getAuthor());
                sellingPriceField.setText(String.valueOf(existingBook.getSellingPrice()));

                String imageUrl = existingBook.getUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Image image = new Image(imageUrl);
                    imageView.setImage(image);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Book Not Found", "Book with title '" + title + "' not found in the database.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "Empty Field", "Please enter a book title to search.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
