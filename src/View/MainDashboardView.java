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

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainDashboardView extends Application {
    private String librarianUsername;
    private static Connection conn;
    private String userRole;
    private VBox bookContainer = new VBox();
    private List<VBox> originalBooks;

    public MainDashboardView(String userRole, String librarianUsername) {
        this.userRole = userRole;
        this.librarianUsername = librarianUsername;
    }

    public MainDashboardView() {
    }
    public MainDashboardView(Connection conn) {
        this.conn = conn; // Ensure conn is initialized properly
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            conn = DriverManager.getConnection(url, "root", "IndritFerati2604!");
            System.out.println("Connected to the database!");


            originalBooks = fetchBooksFromDatabase("", "Title");
            launchDashboard(primaryStage);

        } catch (ClassNotFoundException e) {
            showErrorDialog("Database Driver Error", "MySQL JDBC Driver not found.");
        } catch (SQLException e) {
            showErrorDialog("Database Connection Error", "Failed to connect to the database.");
        }
    }

    private static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        filterComboBox.getItems().addAll("Sort", "Title", "Category", "Author", "Price");
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
        actionComboBox.setId("actionComboBox");
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

                String buttonText = selectedButton.getText();

                if ("Create Bill".equals(buttonText)) {
                    System.out.println("Creating BillView with librarianUsername: " + librarianUsername);
                    BillView billView = new BillView();
                    BillView.setLibrarianUsername(librarianUsername);
                    billView.start(newStage);
                } else if ("Add Book".equals(buttonText)) {
                    System.out.println("Opening Add Book Section");
                    AddBookView addBookView = new AddBookView();
                    addBookView.start(newStage);
                } else if ("Check Librarian Performance".equals(buttonText)) {
                    System.out.println("Opening Check Librarian Performance Section");
                    CheckLibrarianPerformanceView checkLibrarianPerformanceView = new CheckLibrarianPerformanceView();
                    checkLibrarianPerformanceView.start(newStage);
                } else if ("Book Statistics".equals(buttonText)) {
                    System.out.println("Opening Book Statistics Section");
                    BookStatisticsView bookStatisticsView = new BookStatisticsView();
                    bookStatisticsView.start(newStage);
                } else if ("Manage Employees".equals(buttonText)) {
                    System.out.println("Opening Manage Employees Section");
                    ManageEmployeesView manageEmployeesView = new ManageEmployeesView();
                    manageEmployeesView.start(newStage);
                } else if ("Total Cost".equals(buttonText)) {
                    System.out.println("Opening Total Cost Section");
                    TotalCostView totalCostView = new TotalCostView();
                    totalCostView.start(newStage);
                } else {
                    System.out.println("Unrecognized action: " + buttonText);
                }
            }
        });


        bookContainer = new VBox();
        bookContainer.setAlignment(Pos.TOP_CENTER);
        bookContainer.setSpacing(40);
        bookContainer.setPadding(new Insets(10, 40, 10, 140));

        actionComboBox.setCellFactory(param -> new ListCell<Button>() {
            @Override
            public void updateItem(Button item, boolean empty) {
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
        int booksPerRow = 4;
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

        hbox.getChildren().addAll(backButton, refreshButton, searchBar, filterComboBox, actionComboBox);

        vbox.getChildren().addAll(stackPane, hbox, scrollPane);

        searchBar.setOnAction(event -> {
            int price = Integer.parseInt(searchBar.getText().trim());
            int bookCount = countBooksByPrice(price, "");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Book Count");
            alert.setHeaderText(null);
            alert.setContentText("Number of books with price <= " + price + ": " + bookCount);
            alert.showAndWait();
        });

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
        button.setId(text);
        return button;
    }
    public int countBooksByPrice(int price, String sortBy) {
        int count = 0;
        try {
            String query = "SELECT COUNT(*) AS book_count FROM books WHERE selling_price <= ?";
            if (sortBy.equals("Category")) {
                query += " ORDER BY category";
            } else if (sortBy.equals("Author")) {
                query += " ORDER BY author";
            } else {
                query += " ORDER BY title";
            }
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, price);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("book_count");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            showErrorDialog("Database Error", "Failed to fetch book count from the database.");
        }
        return count;
    }

    public int getAvgPrice() {
        int avgPrice = 0;
        try {
            System.out.println("Calling getAvgPrice()...");
            String query = "SELECT AVG(selling_price) AS avg_price FROM books";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                avgPrice = rs.getInt("avg_price");
            }
            System.out.println("Avg price is: " + avgPrice);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            showErrorDialog("Database Error", "Failed to fetch average price from the database.");
        }
        return avgPrice;
    }



    private void performSearch(TextField searchBar, ComboBox<String> filterComboBox) {
        String searchText = searchBar.getText().trim();
        String sortBy = filterComboBox.getValue();
        List<VBox> filteredBooks = fetchBooksFromDatabase(searchText, sortBy);
        displayBooks(filteredBooks);
    }

    private void displayBooks(List<VBox> bookBoxes) {
        Platform.runLater(() -> {
            bookContainer.getChildren().clear();

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
        });
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

    private List<VBox> fetchBooksFromDatabase(String searchQuery, String sortBy) {
        List<VBox> bookBoxes = new ArrayList<>();
        boolean isNumericSearch = searchQuery.matches("\\d+(\\.\\d+)?");

        try {
            String query = "SELECT * FROM books";

            if (!searchQuery.isEmpty()) {
                if (isNumericSearch) {
                    query += " WHERE selling_price <= ?";
                } else {
                    query += " WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";
                }
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
                case "Price":
                    query += " ORDER BY selling_price";
                    break;
                default:
                    break;
            }

            PreparedStatement stmt = conn.prepareStatement(query);

            if (!searchQuery.isEmpty()) {
                if (isNumericSearch) {
                    stmt.setDouble(1, Double.parseDouble(searchQuery));
                } else {
                    stmt.setString(1, "%" + searchQuery + "%");
                    stmt.setString(2, "%" + searchQuery + "%");
                    stmt.setString(3, "%" + searchQuery + "%");
                }
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                VBox bookBox = createBookBox(
                        rs.getString("ISBN"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getDouble("selling_price"),
                        rs.getString("author"),
                        rs.getInt("stock"),
                        rs.getString("cover_image_path")
                );

                bookBoxes.add(bookBox);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            showErrorDialog("Database Error", "Failed to fetch books from database.");
        }

        return bookBoxes;
    }

    private VBox createBookBox(String isbn, String title, String category,
                               Double sellingPrice, String author,
                               int stock, String coverImagePath) {
        VBox bookVBox = new VBox();
        bookVBox.setSpacing(5);
        bookVBox.setPadding(new Insets(10));
        bookVBox.setAlignment(Pos.CENTER);

        // Image View Setup
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        // Load Image with Robust Error Handling
        try {
            // Use absolute file path for development
            File imageFile = new File("src/Assets/" + coverImagePath);

            if (imageFile.exists()) {
                Image coverImage = new Image(new FileInputStream(imageFile));
                imageView.setImage(coverImage);
            } else {
                System.out.println("Image not found: " + imageFile.getAbsolutePath());
                // Optional: Set a default image
                // imageView.setImage(new Image(getClass().getResourceAsStream("/default-book-cover.jpg")));
            }
        } catch (Exception e) {
            System.out.println("Error loading image for book: " + title);
        }

        // Book Information Labels
        Label titleLabel = new Label("Title: " + title);
        Label isbnLabel = new Label("ISBN: " + isbn);
        Label categoryLabel = new Label("Category: " + category);
        Label authorLabel = new Label("Author: " + author);
        Label priceLabel = new Label("Price: $" + String.format("%.2f", sellingPrice));
        Label stockLabel = new Label("Stock: " + stock);

        // Style the labels
        Font infoFont = Font.font(12);
        titleLabel.setFont(Font.font(14));
        titleLabel.setStyle("-fx-font-weight: bold;");

        VBox infoBox = new VBox(titleLabel, isbnLabel, categoryLabel,
                authorLabel, priceLabel, stockLabel);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setSpacing(5);

        bookVBox.getChildren().addAll(imageView, infoBox);

        return bookVBox;
    }

    // Cleanup database connection
    @Override
    public void stop() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        }
    }
}