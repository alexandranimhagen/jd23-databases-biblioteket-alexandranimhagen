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
import com.fulkoping.library.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class LibraryApp {

    private static UsersDAO usersDAO = new UsersDAO();
    private static BooksDAO booksDAO = new BooksDAO();
    private static LoansDAO loansDAO = new LoansDAO();

    public abstract void start(Stage primaryStage);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Välkommen till Fulköpings bibliotek!");

        while (true) {
            System.out.println("1. Logga in");
            System.out.println("2. Registrera");
            System.out.println("3. Avsluta");
            System.out.print("Välj ett alternativ: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                login(scanner);
            } else if (choice == 2) {
                register(scanner);
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Ogiltigt alternativ.");
            }
        }
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

    private static void login(Scanner scanner) {
        System.out.print("Användarnamn: ");
        String username = scanner.nextLine();
        System.out.print("Lösenord: ");
        String password = scanner.nextLine();

        try {
            Users users = usersDAO.getUserByUsername(username);
            if (users != null && users.getPassword().equals(password)) {
                System.out.println("Inloggning lyckades!");
                try (Connection conn = Database.getConnection()) {
                    ActivityLog.log(conn, String.valueOf(users.getId()), "Användare inloggad");
                }
                userMenu(scanner, users);
            } else {
                System.out.println("Fel användarnamn eller lösenord.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void register(Scanner scanner) {
        System.out.print("Användarnamn: ");
        String username = scanner.nextLine();
        System.out.print("Lösenord: ");
        String password = scanner.nextLine();
        System.out.print("Namn: ");
        String name = scanner.nextLine();
        System.out.print("E-post: ");
        String email = scanner.nextLine();

        Users users = new Users(0, username, password, email, null);
        try {
            usersDAO.addUser(users);
            System.out.println("Registrering lyckades!");
            try (Connection conn = Database.getConnection()) {
                ActivityLog.log(conn, String.valueOf(users.getId()), "Användare registrerad");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void userMenu(Scanner scanner, Users users) {
        while (true) {
            System.out.println("1. Sök efter böcker");
            System.out.println("2. Låna bok");
            System.out.println("3. Lämna tillbaka bok");
            System.out.println("4. Visa mina lån");
            System.out.println("5. Logga ut");
            System.out.print("Välj ett alternativ: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                searchBooks(scanner);
            } else if (choice == 2) {
                loanBook(scanner, users);
            } else if (choice == 3) {
                returnBook(scanner, users);
            } else if (choice == 4) {
                showLoans(users);
            } else if (choice == 5) {
                break;
            } else {
                System.out.println("Ogiltigt alternativ.");
            }
        }
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
