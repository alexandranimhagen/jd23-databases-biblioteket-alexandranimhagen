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

public class Register {
    private static UsersDAO usersDAO = new UsersDAO();

    public static Scene getRegisterScene(Stage primaryStage) {
        Label userLabel = new Label("Användarnamn:");
        TextField userField = new TextField();
        Label passLabel = new Label("Lösenord:");
        PasswordField passField = new PasswordField();
        Label emailLabel = new Label("E-post:");
        TextField emailField = new TextField();
        Button registerButton = new Button("Registrera");
        Button backButton = new Button("Tillbaka");

        registerButton.setOnAction(e -> handleRegister(userField.getText(), passField.getText(), emailField.getText()));
        backButton.setOnAction(e -> primaryStage.setScene(LibraryApp.getHomePageScene(primaryStage)));

        VBox vbox = new VBox(10, userLabel, userField, passLabel, passField, emailLabel, emailField, registerButton, backButton);
        return new Scene(vbox, 300, 250);
    }

    private static void handleRegister(String username, String password, String email) {
        try {
            String hashedPassword = Hashing.encrypt(password);
            Users user = new Users(0, username, hashedPassword, email, null);
            usersDAO.addUser(user);
            System.out.println("Registrering lyckades!");
        } catch (Exception e) {
            System.out.println("Registrering misslyckades: " + e.getMessage());
        }
    }
}
