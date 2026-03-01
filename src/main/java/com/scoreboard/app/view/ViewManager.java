package com.scoreboard.app.view;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.controller.ContextAwareController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {

    private static Stage mainStage;
    private static AppContext context;

    private ViewManager() {}

    public static void setStage(Stage stage) { mainStage = stage;}
    public static void setContext(AppContext c) { context = c; }

//    public static void switchTo(String fxmlName) {
//
//        try {
//            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("/fxml/" + fxmlName));
//            Parent root = loader.load();
//            Scene scene = new Scene(root);
//            mainStage.setScene(scene);
//            mainStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
            e.printStackTrace();
            return null;
        }
    }
}

