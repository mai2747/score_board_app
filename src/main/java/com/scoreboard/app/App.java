package com.scoreboard.app;

import com.scoreboard.app.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import com.scoreboard.app.db.DatabaseManager;
import com.scoreboard.app.db.DatabaseInitialiser;

public class App extends Application {
    private Connection conn;
    private AppContext context;

    @Override
    public void start(Stage stage) {

        try {
            conn = DatabaseManager.getConnection();
            DatabaseInitialiser.initialise(conn);

            // Instantiate controller and repo
            context = new AppContext(conn);

            context.gameService().pauseRemainingInProgressGames();

            // Pass main stage to ViewManager
            ViewManager.setStage(stage);
            ViewManager.setContext(context);
            ViewManager.switchTo("GroupSetup.fxml");

//            stage.setTitle("Scrabble Score Calculator");
//            stage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    @Override
    public void stop() throws Exception {
        if (context != null) {
            context.gameService().pauseCurrentGameIfInProgress();
        }

        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    public static void main(String[] args) {
        launch(args); // JavaFX アプリ起動
    }
}