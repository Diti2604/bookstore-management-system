package View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainDashboard extends Application {

    private Connection conn;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Attempt to establish a database connection
        try {
            // Step 1: Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Step 2: Establish Connection
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = "root";
            String password = "HarveySpectre9";
            conn = DriverManager.getConnection(url, user, password);

            System.out.println("Connected to the database!");

            // Launch your JavaFX GUI
            launchDashboard(primaryStage);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC Driver not found.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Connection failed. Check output console.");
        }
    }

    public void launchDashboard(Stage primaryStage) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);

        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        Rectangle headerRectangle = new Rectangle(screenWidth, 100, Color.BLACK);

        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.TOP_CENTER);

        Text headerText = new Text("HOMEPAGE");
        headerText.setFont(Font.font("Times New Roman", 86));
        headerText.setFill(Color.WHITE);

        stackPane.getChildren().addAll(headerRectangle, headerText);

        HBox hbox = new HBox();
        hbox.setSpacing(12);
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.setAlignment(Pos.TOP_CENTER);

        TextField searchBar = new TextField();
        searchBar.setMinHeight(38);
        searchBar.setPrefWidth(400);
        searchBar.setStyle("-fx-border-color:green;-fx-border-radius:6px;");
        searchBar.setPromptText("Search book");

        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("Sort", "Title", "Category", "Authors");
        filterComboBox.setValue("Sort");
        filterComboBox.setStyle("-fx-font-size: 14pt; -fx-background-color: transparent; -fx-border-color: green; -fx-border-radius: 10; -fx-border-width: 2;");

        Image secondImage = new Image("/Assets/down-arrow.png");
        ImageView addImageView = new ImageView(secondImage);
        addImageView.setFitHeight(30);
        addImageView.setFitWidth(30);
        addImageView.setPreserveRatio(true);
        Button addButton = new Button("", addImageView);
        addButton.setStyle("-fx-background-color:transparent;-fx-border-color:green;-fx-border-radius:10;-fx-border-width:2;");

        ComboBox<Button> actionComboBox = new ComboBox<>();
        actionComboBox.getItems().addAll(
                createComboBoxButton("Create Bill"),
                createComboBoxButton("Add Book"),
                createComboBoxButton("Check Librarian Performance"),
                createComboBoxButton("Book Statistics"),
                createComboBoxButton("Manage Employees"),
                createComboBoxButton("Total Cost")
        );
        actionComboBox.setStyle("-fx-font-size: 14pt; -fx-background-color: transparent; -fx-border-color: green; -fx-border-radius: 10; -fx-border-width: 2;");

        VBox bookContainer = new VBox();
        bookContainer.setAlignment(Pos.TOP_CENTER);
        bookContainer.setSpacing(40);
        bookContainer.setPadding(new Insets(10, 40, 10, 140));

        List<VBox> bookBoxes = fetchBooksFromDatabase();
        for (VBox bookVBox : bookBoxes) {
            bookContainer.getChildren().add(bookVBox);
        }

        int booksPerRow = 4;
        for (int i = 0; i < bookBoxes.size(); i += booksPerRow) {
            StackPane row = new StackPane(); // Use StackPane for centering
            HBox booksHBox = new HBox(); // Use HBox for horizontal arrangement
            booksHBox.setAlignment(Pos.CENTER); // Center books in the row
            booksHBox.setSpacing(40);  // Adjust the spacing as needed

            int booksInThisRow = Math.min(booksPerRow, bookBoxes.size() - i);
            for (int j = i; j < i + booksInThisRow; j++) {
                VBox bookVBox = bookBoxes.get(j);
                booksHBox.getChildren().add(bookVBox);
            }

            row.getChildren().add(booksHBox);
            bookContainer.getChildren().add(row);

            if (i + booksInThisRow < bookBoxes.size()) {
                Separator separator = new Separator();
                separator.setOrientation(Orientation.HORIZONTAL);
                bookContainer.getChildren().add(separator);
            }
        }

        bookContainer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(bookContainer);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-border-color: green; -fx-border-radius: 10; -fx-border-width: 2;-fx-font-weight:bold");
        backButton.setPrefHeight(38);
        backButton.setMaxHeight(38);
        backButton.setMinHeight(38);

        hbox.getChildren().addAll(backButton, searchBar, filterComboBox, actionComboBox);

        vbox.getChildren().addAll(stackPane, hbox, scrollPane);

        Scene librarianScene = new Scene(vbox, 800, 600);
        primaryStage.setScene(librarianScene);
        primaryStage.setTitle("Librarian View");

        primaryStage.setMaximized(true);
        Platform.runLater(primaryStage::show);
    }

    private Button createComboBoxButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: green; -fx-border-radius: 10; -fx-border-width: 2; -fx-font-weight:bold");
        button.setPrefHeight(38);
        button.setMaxHeight(38);
        button.setMinHeight(38);
        return button;
    }

    private List<VBox> fetchBooksFromDatabase() {
        List<VBox> bookBoxes = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");

            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("title");
                String category = rs.getString("category");
                Double sellingPrice = rs.getDouble("selling_price");
                String author = rs.getString("author");
                int stock = rs.getInt("stock");

                // Example: Load cover image from database blob (assuming it's stored as blob)
                Blob coverImageBlob = rs.getBlob("cover_image");
                byte[] imageData = coverImageBlob.getBytes(1, (int) coverImageBlob.length());
                InputStream inputStream = new ByteArrayInputStream(imageData);
                Image coverImage = new Image(inputStream);

                // For demo purposes, create a basic VBox for each book
                VBox bookVBox = new VBox();
                bookVBox.setSpacing(5);
                bookVBox.setPadding(new Insets(10));
                bookVBox.setAlignment(Pos.CENTER);

                Label isbnLabel = new Label("ISBN: " + isbn);
                Label titleLabel = new Label("Title: " + title);
                Label categoryLabel = new Label("Category: " + category);
                Label authorLabel = new Label("Author: " + author);

                // Display cover image
                ImageView imageView = new ImageView(coverImage);
                imageView.setFitWidth(150);
                imageView.setFitHeight(200);

                bookVBox.getChildren().addAll(isbnLabel, titleLabel, categoryLabel, authorLabel, imageView);

                // Add bookVBox to the list of book boxes
                bookBoxes.add(bookVBox);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookBoxes;
    }
}