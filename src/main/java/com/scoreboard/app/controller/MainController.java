package com.scoreboard.app.controller;

import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import com.scoreboard.app.service.GameService;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private Button startButton;
    @FXML private TextField firstPlayerName;
    @FXML private TextField secondPlayerName;

    // Further implementation: "Create Group" -> Edit settings -> "Start Game"
    // This method respond to "Create Group"
    @FXML
    private void handleStartButton(ActionEvent event) {
        System.out.println("Start button pressed!");

        List<String> playerNames = new ArrayList<>();
        if (!firstPlayerName.getText().isBlank())  playerNames.add(firstPlayerName.getText());
        if (!secondPlayerName.getText().isBlank()) playerNames.add(secondPlayerName.getText());
        // if (!thirdPlayerName.getText().isBlank())  playerNames.add(thirdPlayerName.getText());
        // if (!fourthPlayerName.getText().isBlank()) playerNames.add(fourthPlayerName.getText());

        // Pass information to GameService
        GameService gameService = new GameService();
        gameService.createNewGroup(playerNames);

        ScoreInputController controller =
                (ScoreInputController) ViewManager.switchTo("scoreInput.fxml");
        // ?or ViewManager.switchTo("scoreInput.fxml");
    }
}
