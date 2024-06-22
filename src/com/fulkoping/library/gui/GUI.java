package com.fulkoping.library.gui;

import com.fulkoping.library.LibraryApp;
import javafx.application.Application;
import javafx.stage.Stage;

public class GUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fulköpings Bibliotek");
        primaryStage.setScene(LibraryApp.getHomePageScene(primaryStage));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void launch(String[] args) {
    }
}
