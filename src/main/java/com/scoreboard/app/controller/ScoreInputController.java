package com.scoreboard.app.controller;


import com.scoreboard.app.model.PlayerInGame;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ScoreInputController {

    @FXML private Label playerNameLabel;
    @FXML private TextField scoreField;

    private GameService gameService;

    public void onSceneReady(){
        updatePlayerDisplay();
    }

    private void updatePlayerDisplay(){
        PlayerInGame pig = gameService.getCurrentPlayer();
        // Player player = gameService.getPlayerById(pig.getPlayerId()); // or pig.getPlayerName
        // playerNameLabel.setText(player.getName());
    }

    @FXML private void submitScore(ActionEvent event){
        // Error handling: Score is null/negative/non-digit

        int points = Integer.parseInt(scoreField.getText());
        PlayerInGame currentPlayer = gameService.getCurrentPlayer();

        // Submit score and advance turn to the next player
        gameService.submitScore(currentPlayer.getPlayerId(), points);

        updatePlayerDisplay();
        scoreField.clear();
    }

    @FXML private void endGame(){
        // Tell GameService and transition into the result scene (skipping penalty scene for now)
        ViewManager.switchTo("result.fxml");
    }


    public void setPlayerName(String name) {
        playerNameLabel.setText(name);
    }

    public void setGameService(GameService gameService){
        this.gameService = gameService;
    }
}
