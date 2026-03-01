package com.scoreboard.app;

import com.scoreboard.app.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Instantiate controller and repo
        AppContext context = new AppContext();

        // Pass main stage to ViewManager
        ViewManager.setStage(stage);
        ViewManager.setContext(context);
        ViewManager.switchTo("GroupSetup.fxml");

        stage.setTitle("Scrabble Score Calculator");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // JavaFX アプリ起動
    }
}