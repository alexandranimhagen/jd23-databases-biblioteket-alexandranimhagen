package com.fulkoping.library.gui;

import com.fulkoping.library.LibraryApp;
import javafx.stage.Stage;

public class GUI extends LibraryApp {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fulköpings Bibliotek");
        primaryStage.setScene(LibraryApp.getHomePageScene(primaryStage));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void launch(String[] args) {
    }
}
