package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.repository.InMemoryScoreRepository;
import com.scoreboard.app.repository.ScoreRepository;
import com.scoreboard.app.service.GroupService;
import com.scoreboard.app.service.ScoreService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import com.scoreboard.app.service.GameService;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class GroupSetupController implements ContextAwareController{

    @FXML private Button startButton;
    @FXML private TextField firstPlayerName;
    @FXML private TextField secondPlayerName;

    private GameService gameService;

    @Override
    public void setContext(AppContext context){
        this.gameService = context.gameService();
    }

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

        System.out.println("First Player..." + firstPlayerName.getText());
        System.out.println("Second Player.." + secondPlayerName.getText());
        System.out.println();

        gameService.startGameWithNewGroup(playerNames);

        ViewManager.switchTo("ScoreInput.fxml");
    }
}
