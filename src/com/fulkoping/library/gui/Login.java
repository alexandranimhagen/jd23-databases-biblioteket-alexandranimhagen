package com.fulkoping.library.gui;

import com.fulkoping.library.LibraryApp;
import com.fulkoping.library.dao.UsersDAO;
import com.fulkoping.library.model.Users;
import com.fulkoping.library.utils.Hashing;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

        loginButton.setOnAction(e -> handleLogin(primaryStage, userField.getText(), passField.getText()));
        registerButton.setOnAction(e -> primaryStage.setScene(Register.getRegisterScene(primaryStage)));
        backButton.setOnAction(e -> primaryStage.setScene(LibraryApp.getHomePageScene(primaryStage)));

        VBox vbox = new VBox(10, userLabel, userField, passLabel, passField, loginButton, registerButton, backButton);
        return LibraryApp.getStyledScene(vbox);
    }

    private static void handleLogin(Stage primaryStage, String username, String password) {
        try {
            Users user = usersDAO.getUserByUsername(username);
            if (user != null && Hashing.verify(password, user.getPassword())) {
                System.out.println("Inloggning lyckades!");
                primaryStage.setScene(LibraryApp.getUserMenuScene(primaryStage, user));
            } else {
                System.out.println("Fel användarnamn eller lösenord.");
                LibraryApp.showAlert(Alert.AlertType.ERROR, primaryStage, "Fel användarnamn eller lösenord.", null);
            }
        } catch (Exception e) {
            System.out.println("Inloggning misslyckades: " + e.getMessage());
            LibraryApp.showAlert(Alert.AlertType.ERROR, primaryStage, "Inloggning misslyckades: " + e.getMessage(), null);
        }
    }
}
