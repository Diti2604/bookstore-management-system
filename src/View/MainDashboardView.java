package View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
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
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainDashboardView extends Application {
    private String librarianUsername;
    private Connection conn;
    private String userRole;
    private VBox bookContainer = new VBox();
    private List<VBox> originalBooks;

    public MainDashboardView(String userRole, String librarianUsername) {
        this.userRole = userRole;
        this.librarianUsername = librarianUsername;
    }

    public MainDashboardView() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = System.getenv("root");
            String password = System.getenv("DitiHost2604");
            conn = DriverManager.getConnection(url, "root","DitiHost2604");
            System.out.println("Connected to the database!");

            originalBooks = fetchBooksFromDatabase("", "Title");
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
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);

        Rectangle headerRectangle = new Rectangle(screenBounds.getWidth(), 100, Color.BLACK);

        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.TOP_CENTER);

        Text headerText = new Text("Welcome, " + userRole);
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
        filterComboBox.getItems().addAll("Sort", "Title", "Category", "Author");
        filterComboBox.setValue("Sort");
        filterComboBox.setStyle("-fx-font-size: 14pt; -fx-background-color: transparent; -fx-border-color: green; -fx-border-radius: 10; -fx-border-width: 2;");

        searchBar.setOnAction(event -> {
            String searchText = searchBar.getText().trim();
            String sortBy = filterComboBox.getValue();
            List<VBox> filteredBooks = fetchBooksFromDatabase(searchText, sortBy);
            updateBookContainer(filteredBooks, sortBy);
        });

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

        actionComboBox.setOnAction(event -> {
            Button selectedButton = actionComboBox.getValue();
            if (selectedButton != null) {
                Stage newStage = new Stage();
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.initOwner(primaryStage);

                switch (selectedButton.getText()) {
                    case "Create Bill":
                        System.out.println("Creating BillView with librarianUsername: " + librarianUsername);
                        BillView billView = new BillView();
                        billView.setLibrarianUsername(librarianUsername);
                        billView.start(newStage);
                        break;
                    case "Add Book":
                        System.out.println("Opening Add Book Section");
                        AddBookView addBookView = new AddBookView();
                        addBookView.start(newStage);
                        break;
                    case "Check Librarian Performance":
                        System.out.println("Opening Check Librarian Performance Section");
                        CheckLibrarianPerformanceView checkLibrarianPerformanceView = new CheckLibrarianPerformanceView();
                        checkLibrarianPerformanceView.start(newStage);
                        break;
                    case "Book Statistics":
                        System.out.println("Opening Book Statistics Section");
                        BookStatisticsView bookStatisticsView = new BookStatisticsView();
                        bookStatisticsView.start(newStage);
                        break;
                    case "Manage Employees":
                        System.out.println("Opening Manage Employees Section");
                        ManageEmployeesView manageEmployeesView = new ManageEmployeesView();
                        manageEmployeesView.start(newStage);
                        break;
                    case "Total Cost":
                        TotalCostView totalCostView = new TotalCostView();
                        totalCostView.start(newStage);
                        break;
                }
            }
        });

        bookContainer = new VBox();
        bookContainer.setAlignment(Pos.TOP_CENTER);
        bookContainer.setSpacing(40);
        bookContainer.setPadding(new Insets(10, 40, 10, 140));
        actionComboBox.setCellFactory(param -> new ListCell<Button>() {
            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getText());
                    setDisable(!isButtonEnabled(item));
                    setStyle("-fx-opacity: " + (isButtonEnabled(item) ? "1.0;" : "0.5;") +
                            "-fx-text-fill: " + (isButtonEnabled(item) ? "black;" : "grey;"));
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });
        List<VBox> bookBoxes = fetchBooksFromDatabase("", "Title");

        int booksPerRow = 6;
        for (int i = 0; i < bookBoxes.size(); i += booksPerRow) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER);
            row.setSpacing(20);

            int booksInThisRow = Math.min(booksPerRow, bookBoxes.size() - i);
            for (int j = i; j < i + booksInThisRow; j++) {
                VBox bookVBox = bookBoxes.get(j);
                row.getChildren().add(bookVBox);
            }

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
        backButton.setStyle("-fx-background-color: transparent; -fx-border-color: green; -fx-border-radius: 10;");
        backButton.setPrefHeight(38);
        backButton.setMaxHeight(38);
        backButton.setMinHeight(38);

        backButton.setOnAction(event -> {
            updateBookContainer(originalBooks, "Title");
        });
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: transparent; -fx-border-color: green; -fx-border-radius: 10;");
        refreshButton.setPrefHeight(38);
        refreshButton.setMaxHeight(38);
        refreshButton.setMinHeight(38);

        refreshButton.setOnAction(event -> {
            originalBooks = fetchBooksFromDatabase("", "Title");
            updateBookContainer(originalBooks, "Title");
        });

        hbox.getChildren().addAll(backButton ,refreshButton, searchBar, filterComboBox, actionComboBox);

        vbox.getChildren().addAll(stackPane, hbox, scrollPane);

        Scene scene = new Scene(vbox, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setTitle(userRole + " View");

        primaryStage.setFullScreen(true);
        primaryStage.show();

        searchBar.setOnKeyReleased(event -> {
            String searchText = searchBar.getText().trim();
            String sortBy = filterComboBox.getValue();
            List<VBox> filteredBooks = fetchBooksFromDatabase(searchText, sortBy);
            updateBookContainer(filteredBooks, sortBy);
        });
    }

    private Button createComboBoxButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: green; -fx-border-radius: 10; -fx-border-width: 2; -fx-font-weight:bold");
        button.setPrefHeight(38);
        button.setMaxHeight(38);
        button.setMinHeight(38);
        return button;
    }

    private List<VBox> fetchBooksFromDatabase(String searchQuery, String sortBy) {
        List<VBox> bookBoxes = new ArrayList<>();

        try {
            String query = "SELECT * FROM books";

            if (!searchQuery.isEmpty()) {
                query += " WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";
            }

            switch (sortBy) {
                case "Title":
                    query += " ORDER BY title";
                    break;
                case "Author":
                    query += " ORDER BY author";
                    break;
                case "Category":
                    query += " ORDER BY category";
                    break;
                default:
                    break;
            }

            PreparedStatement stmt = conn.prepareStatement(query);

            if (!searchQuery.isEmpty()) {
                stmt.setString(1, "%" + searchQuery + "%");
                stmt.setString(2, "%" + searchQuery + "%");
                stmt.setString(3, "%" + searchQuery + "%");
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("title");
                String category = rs.getString("category");
                Double sellingPrice = rs.getDouble("selling_price");
                String author = rs.getString("author");
                int stock = rs.getInt("stock");
                String coverImageUrl = rs.getString("cover_image_path");

                if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                    Image coverImage = new Image(coverImageUrl);

                    VBox bookVBox = new VBox();
                    bookVBox.setSpacing(5);
                    bookVBox.setPadding(new Insets(10));
                    bookVBox.setAlignment(Pos.CENTER);

                    ImageView imageView = new ImageView(coverImage);
                    imageView.setFitWidth(150);
                    imageView.setFitHeight(200);

                    Label titleLabel = new Label("Title: " + title);
                    Label isbnLabel = new Label("ISBN: " + isbn);
                    Label categoryLabel = new Label("Category: " + category);
                    Label authorLabel = new Label("Author: " + author);
                    Label priceLabel = new Label("Price: $" + sellingPrice);
                    Label stockLabel = new Label("Stock: " + stock);

                    VBox infoBox = new VBox(titleLabel, isbnLabel, categoryLabel, authorLabel, priceLabel, stockLabel);
                    infoBox.setAlignment(Pos.CENTER);
                    infoBox.setSpacing(5);

                    bookVBox.getChildren().addAll(imageView, infoBox);

                    bookBoxes.add(bookVBox);
                }
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookBoxes;
    }

    private void updateBookContainer(List<VBox> filteredBooks, String sortBy) {
        Platform.runLater(() -> {
            bookContainer.getChildren().clear();
            if (filteredBooks.isEmpty()) {
                Label noResultsLabel = new Label("No results found.");
                noResultsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                noResultsLabel.setTextFill(Color.RED);
                bookContainer.getChildren().add(noResultsLabel);
            } else {
                int booksPerRow = 6;
                for (int i = 0; i < filteredBooks.size(); i += booksPerRow) {
                    HBox row = new HBox();
                    row.setAlignment(Pos.CENTER);
                    row.setSpacing(20);

                    int booksInThisRow = Math.min(booksPerRow, filteredBooks.size() - i);
                    for (int j = i; j < i + booksInThisRow; j++) {
                        VBox bookVBox = filteredBooks.get(j);
                        row.getChildren().add(bookVBox);
                    }

                    bookContainer.getChildren().add(row);

                    if (i + booksInThisRow < filteredBooks.size()) {
                        Separator separator = new Separator();
                        separator.setOrientation(Orientation.HORIZONTAL);
                        bookContainer.getChildren().add(separator);
                    }
                }
            }
        });
    }

    private boolean isButtonEnabled(Button button) {
        return !button.isDisabled();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Database connection closed.");
        }
    }
}
