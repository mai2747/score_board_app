package com.scoreboard.app.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {

    private static Stage mainStage;

    public static void setStage(Stage stage) {
        mainStage = stage;
    }

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

            Scene scene = new Scene(root);
            mainStage.setScene(scene);
            mainStage.show();

            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

