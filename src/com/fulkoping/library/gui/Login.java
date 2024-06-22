package com.fulkoping.library.gui;

import com.fulkoping.library.LibraryApp;
import com.fulkoping.library.dao.UsersDAO;
import com.fulkoping.library.model.Users;
import com.fulkoping.library.utils.Hashing;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Login {
    private static UsersDAO usersDAO = new UsersDAO();

    public static Scene getLoginScene(Stage primaryStage) {
        Label userLabel = new Label("Användarnamn:");
        TextField userField = new TextField();
        Label passLabel = new Label("Lösenord:");
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Logga in");
        Button registerButton = new Button("Registrera");
        Button backButton = new Button("Tillbaka");

        loginButton.setOnAction(e -> handleLogin(userField.getText(), passField.getgit add .
                Text()));
        registerButton.setOnAction(e -> primaryStage.setScene(Register.getRegisterScene(primaryStage)));
        backButton.setOnAction(e -> primaryStage.setScene(LibraryApp.getHomePageScene(primaryStage)));

        VBox vbox = new VBox(10, userLabel, userField, passLabel, passField, loginButton, registerButton, backButton);
        return new Scene(vbox, 300, 250);
    }

    private static void handleLogin(String username, String password) {
        try {
            Users user = usersDAO.getUserByUsername(username);
            if (user != null) {
                if (Hashing.verify(password, user.getPassword())) {
                    System.out.println("Inloggning lyckades!");
                } else {
                    System.out.println("Fel användarnamn eller lösenord.");
                }
            } else {
                System.out.println("Fel användarnamn eller lösenord.");
            }
        } catch (Exception e) {
            System.out.println("Inloggning misslyckades: " + e.getMessage());
        }
    }
}
