package com.fulkoping.library.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import com.fulkoping.library.LibraryApp;

public class GUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fulköpings Bibliotek");
        primaryStage.setScene(LibraryApp.getHomePageScene(primaryStage));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
