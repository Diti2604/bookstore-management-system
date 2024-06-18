package View;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

public class AddBookView extends Application {

    //STOCK COUNT WILL HAVE TO BE INCREMENTED WHEN A BOOK IS ADDED, IT SHOULDNT BE PUT THERE AS A TEXTFIELD
    //WAITING FOR INPUT
    private ImageView imageView;
    private TextField bookUrlField;
    private TextField bookNameField;
    private TextField categoryField;
    private TextField isbnField;
    private TextField authorField;
    private TextField sellingPriceField;
    private Button submitButton;

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

        Label categoryLabel = new Label("Category: ");
        categoryField = new TextField();
        grid.add(categoryLabel, 0, 2);
        grid.add(categoryField, 1, 2);

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

        Button submitButton = new Button("Create");
        submitButton.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15;");
        grid.add(submitButton, 1, 10);

        this.submitButton = submitButton;


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
    }

    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Book Image");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }

    public TextField getBookUrlField() {
        return bookUrlField;
    }

    public TextField getBookNameField() {
        return bookNameField;
    }

    public TextField getCategoryField() {
        return categoryField;
    }

    public TextField getIsbnField() {
        return isbnField;
    }

    public TextField getAuthorField() {
        return authorField;
    }

    public TextField getSellingPriceField() {
        return sellingPriceField;
    }


    public Button getSubmitButton() {
        return submitButton;
    }
}
