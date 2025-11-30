package com.scoreboard.app.controller;

import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class MainController {

    @FXML
    private Button startButton;

    @FXML
    private void handleStartButton(ActionEvent event) {
        System.out.println("Start button pressed!");

        String firstPlayer = gameService.StartNewGame();

        ScoreInputController controller =
                (ScoreInputController) ViewManager.switchTo("scoreInput.fxml");

        // Set first Player name to ScoreInputController
        controller.setPlayerName(firstPlayerName);

        // ? ViewManager.switchTo("scoreInput.fxml");
    }
}
