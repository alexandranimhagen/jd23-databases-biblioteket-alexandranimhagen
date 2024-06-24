package com.fulkoping.library;

import com.fulkoping.library.dao.BooksDAO;
import com.fulkoping.library.dao.LoansDAO;
import com.fulkoping.library.dao.UsersDAO;
import com.fulkoping.library.gui.GUI;
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
import java.util.Scanner;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Application;

public class LibraryApp extends Application {

    private static UsersDAO usersDAO = new UsersDAO();
    private static BooksDAO booksDAO = new BooksDAO();
    private static LoansDAO loansDAO = new LoansDAO();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fulköpings Bibliotek");
        primaryStage.setScene(getHomePageScene(primaryStage));
        primaryStage.show();
    }

    public static Scene getHomePageScene(Stage primaryStage) {
        Label welcomeLabel = new Label("Välkommen till Fulköpings bibliotek!");
        Button loginButton = new Button("Logga in");
        Button registerButton = new Button("Registrera");

        loginButton.setOnAction(e -> primaryStage.setScene(Login.getLoginScene(primaryStage)));
        registerButton.setOnAction(e -> primaryStage.setScene(Register.getRegisterScene(primaryStage)));

        VBox vbox = new VBox(10, welcomeLabel, loginButton, registerButton);
        return new Scene(vbox, 300, 200);
    }

    public static Scene getUserMenuScene(Stage primaryStage, Users user) {
        Label menuLabel = new Label("Användarmeny:");
        Button searchBooksButton = new Button("Sök efter böcker");
        Button loanBookButton = new Button("Låna bok");
        Button returnBookButton = new Button("Lämna tillbaka bok");
        Button showLoansButton = new Button("Visa mina lån");
        Button logoutButton = new Button("Logga ut");

        searchBooksButton.setOnAction(e -> searchBooks(new Scanner(System.in)));
        loanBookButton.setOnAction(e -> loanBook(new Scanner(System.in), user));
        returnBookButton.setOnAction(e -> returnBook(new Scanner(System.in), user));
        showLoansButton.setOnAction(e -> showLoans(user));
        logoutButton.setOnAction(e -> primaryStage.setScene(getHomePageScene(primaryStage)));

        VBox vbox = new VBox(10, menuLabel, searchBooksButton, loanBookButton, returnBookButton, showLoansButton, logoutButton);
        return new Scene(vbox, 300, 250);
    }

    private static void searchBooks(Scanner scanner) {
        System.out.print("Sökord: ");
        String keyword = scanner.nextLine();

        try {
            List<Books> books = booksDAO.searchBooks(keyword);
            for (Books book : books) {
                System.out.println(book.getId() + ": " + book.getTitle() + " av " + book.getAuthor() + " - " + (book.isAvailable() ? "Tillgänglig" : "Utlånad"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loanBook(Scanner scanner, Users users) {
        System.out.print("Bok-ID att låna: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        try {
            loansDAO.loanBook(users.getId(), bookId);
            System.out.println("Boken har lånats.");
            try (Connection conn = Database.getConnection()) {
                ActivityLog.log(conn, String.valueOf(users.getId()), "Bok lånad: " + bookId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void returnBook(Scanner scanner, Users users) {
        System.out.print("Bok-ID att lämna tillbaka: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        try {
            loansDAO.returnBook(users.getId(), bookId);
            System.out.println("Boken har lämnats tillbaka.");
            try (Connection conn = Database.getConnection()) {
                ActivityLog.log(conn, String.valueOf(users.getId()), "Bok återlämnad: " + bookId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showLoans(Users users) {
        try {
            List<Loans> loans = loansDAO.getUserLoans(users.getId());
            for (Loans loan : loans) {
                System.out.println("Lån-ID: " + loan.getId() + ", Bok-ID: " + loan.getBookId() +
                        ", Lånedatum: " + loan.getLoanDate() +
                        ", Returdatum: " + (loan.getEndDate() != null ? loan.getEndDate() : "Ej återlämnad"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
