package com.scoreboard.app.view;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.controller.ContextAwareController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ViewManager {
    private static final Logger logger = LoggerFactory.getLogger(ViewManager.class);

    private static Stage mainStage;
    private static AppContext context;

    private ViewManager() {}

    public static void setStage(Stage stage) { mainStage = stage;}
    public static void setContext(AppContext c) { context = c; }

    public static Object switchTo(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ViewManager.class.getResource("/fxml/" + fxmlName)
            );
            Parent root = loader.load();

            Object controller = loader.getController();

            if(controller instanceof ContextAwareController aware){
                aware.setContext(context);
            }

            Scene scene = new Scene(root);
            mainStage.setScene(scene);
            mainStage.show();

            return controller;
        } catch (IOException e) {
            logger.error("Could not switch to view: {}", fxmlName, e);
            return null;
        }
    }
}

