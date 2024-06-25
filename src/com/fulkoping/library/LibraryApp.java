package com.fulkoping.library;

import com.fulkoping.library.dao.BooksDAO;
import com.fulkoping.library.dao.LoansDAO;
import com.fulkoping.library.dao.UsersDAO;
import com.fulkoping.library.model.Books;
import com.fulkoping.library.model.Loans;
import com.fulkoping.library.model.Users;
import com.fulkoping.library.gui.Login;
import com.fulkoping.library.gui.Register;
import com.fulkoping.library.utils.ActivityLog;
import com.fulkoping.library.utils.Hashing;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryApp extends Application {

    private static UsersDAO usersDAO = new UsersDAO();
    private static BooksDAO booksDAO;

    static {
        booksDAO = new BooksDAO();
    }

    private static LoansDAO loansDAO = new LoansDAO();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fulköpings Bibliotek");
        primaryStage.setScene(getHomePageScene(primaryStage));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static Scene getHomePageScene(Stage primaryStage) {
        Label welcomeLabel = new Label("Välkommen till Fulköpings bibliotek!");
        Button loginButton = new Button("Logga in");
        Button registerButton = new Button("Registrera");

        loginButton.setOnAction(e -> primaryStage.setScene(Login.getLoginScene(primaryStage)));
        registerButton.setOnAction(e -> primaryStage.setScene(Register.getRegisterScene(primaryStage)));

        VBox vbox = new VBox(10, welcomeLabel, loginButton, registerButton);
        return getStyledScene(vbox);
    }

    public static Scene getUserMenuScene(Stage primaryStage, Users user) {
        Label userLabel = new Label("Välkommen " + user.getUsername());
        Label menuLabel = new Label("Användarmeny:");
        TextField searchField = new TextField();
        searchField.setPromptText("Sök efter böcker...");
        Button searchButton = new Button("Sök");
        ListView<String> searchResults = new ListView<>();
        Button loanBookButton = new Button("Låna bok");
        Button returnBookButton = new Button("Lämna tillbaka bok");
        Button showLoansButton = new Button("Visa mina lån");
        Button logoutButton = new Button("Logga ut");

        searchButton.setOnAction(e -> {
            String keyword = searchField.getText();
            searchBooks(keyword, searchResults);
        });

        loanBookButton.setOnAction(e -> {
            String selectedItem = searchResults.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int bookId = Integer.parseInt(selectedItem.split(":")[0]);
                loanBook(bookId, user, primaryStage);
            } else {
                showAlert(Alert.AlertType.WARNING, primaryStage, "Vänligen välj en bok att låna.", null);
            }
        });

        returnBookButton.setOnAction(e -> returnBook(primaryStage, user));
        showLoansButton.setOnAction(e -> showLoans(user, primaryStage));
        logoutButton.setOnAction(e -> primaryStage.setScene(getHomePageScene(primaryStage)));

        VBox vbox = new VBox(10, userLabel, menuLabel, searchField, searchButton, searchResults, loanBookButton, returnBookButton, showLoansButton, logoutButton);
        return getStyledScene(vbox);
    }

    public static void showAlert(AlertType alertType, Stage owner, String message, Runnable onClose) {
        Alert alert = new Alert(alertType);
        alert.initOwner(owner);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait().ifPresent(response -> {
            if (onClose != null) {
                onClose.run();
            }
        });
    }

    public static Scene getStyledScene(VBox vbox) {
        Scene scene = new Scene(vbox, 800, 600);
        scene.getStylesheets().add(LibraryApp.class.getResource("/com/fulkoping/library/resources/styles.css").toExternalForm());
        return scene;
    }

    private static void searchBooks(String keyword, ListView<String> searchResults) {
        try {
            List<Books> books = booksDAO.searchBooks(keyword);
            searchResults.getItems().clear();
            for (Books book : books) {
                searchResults.getItems().add(book.getId() + ": " + book.getTitle() + " av " + book.getAuthor() + " - " + (book.isAvailable() ? "Tillgänglig" : "Utlånad"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loanBook(int bookId, Users users, Stage primaryStage) {
        try {
            loansDAO.loanBook(users.getId(), bookId);
            showAlert(AlertType.INFORMATION, primaryStage, "Boken har lagts till i dina lån.", () -> showLoans(users, primaryStage));
            try (Connection conn = Database.getConnection()) {
                ActivityLog.log(conn, String.valueOf(users.getId()), "Bok lånad: " + bookId);
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, primaryStage, "Misslyckades att låna bok: " + e.getMessage(), null);
        }
    }

    private static void returnBook(Stage primaryStage, Users users) {
        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Bok-ID att lämna tillbaka...");
        Button confirmReturnButton = new Button("Bekräfta återlämning");

        confirmReturnButton.setOnAction(e -> {
            String bookIdText = bookIdField.getText();
            if (!bookIdText.isEmpty()) {
                int bookId = Integer.parseInt(bookIdText);
                try {
                    loansDAO.returnBook(users.getId(), bookId);
                    showAlert(AlertType.INFORMATION, primaryStage, "Boken har lämnats tillbaka.", () -> showLoans(users, primaryStage));
                    try (Connection conn = Database.getConnection()) {
                        ActivityLog.log(conn, String.valueOf(users.getId()), "Bok återlämnad: " + bookId);
                    }
                } catch (SQLException ex) {
                    showAlert(AlertType.ERROR, primaryStage, "Misslyckades att lämna tillbaka bok: " + ex.getMessage(), null);
                }
            }
        });

        VBox vbox = new VBox(10, bookIdField, confirmReturnButton);
        Scene returnBookScene = getStyledScene(vbox);
        primaryStage.setScene(returnBookScene);
    }

    private static void showLoans(Users users, Stage primaryStage) {
        ListView<String> loansList = new ListView<>();
        Button returnBookButton = new Button("Återlämna vald bok");

        try {
            List<Loans> loans = loansDAO.getUserLoans(users.getId());
            loansList.getItems().clear();
            for (Loans loan : loans) {
                loansList.getItems().add("Lån-ID: " + loan.getId() + ", Bok-ID: " + loan.getBookId() +
                        ", Lånedatum: " + loan.getLoanDate() +
                        ", Returdatum: " + (loan.getEndDate() != null ? loan.getEndDate() : "Ej återlämnad"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        returnBookButton.setOnAction(e -> {
            String selectedItem = loansList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int loanId = Integer.parseInt(selectedItem.split(",")[0].split(":")[1].trim());
                int bookId = Integer.parseInt(selectedItem.split(",")[1].split(":")[1].trim());
                try {
                    loansDAO.returnBook(users.getId(), bookId);
                    loansList.getItems().remove(selectedItem);
                    showAlert(AlertType.INFORMATION, primaryStage, "Boken har lämnats tillbaka.", null);
                    try (Connection conn = Database.getConnection()) {
                        ActivityLog.log(conn, String.valueOf(users.getId()), "Bok återlämnad: " + bookId);
                    }
                } catch (SQLException ex) {
                    showAlert(AlertType.ERROR, primaryStage, "Misslyckades att lämna tillbaka bok: " + ex.getMessage(), null);
                }
            } else {
                showAlert(AlertType.WARNING, primaryStage, "Vänligen välj en bok att återlämna.", null);
            }
        });

        VBox vbox = new VBox(10, loansList, returnBookButton);
        Scene loansScene = getStyledScene(vbox);
        primaryStage.setScene(loansScene);
    }
}
