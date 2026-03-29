package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Game;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import com.scoreboard.app.viewmodel.PlayerTotalScore;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class PausedGameController implements ContextAwareController{
    GameService gameService;
    private Long pausedGameId;

    @FXML private Label groupNameLabel;

    @FXML private Label player1;
    @FXML private Label player2;
    @FXML private Label player3;
    @FXML private Label player4;

    @FXML private Label score1;
    @FXML private Label score2;
    @FXML private Label score3;
    @FXML private Label score4;

    @Override
    public void setContext(AppContext context) {
        gameService = context.gameService();
        displayPausedGame();
    }

    private void displayPausedGame() {
        Game pausedGame = gameService.getPausedGame();

        if (pausedGame == null) {
            groupNameLabel.setText("No paused game");
            return;
        }

        pausedGameId = pausedGame.getGameId();

        List<PlayerTotalScore> pts = gameService.makePlayerTotalScores(pausedGameId);
        String groupName = gameService.getGroupNameByGameId(pausedGameId);
        groupNameLabel.setText(groupName);

        setPlayerLabels(pts);
        setScoreLabels(pts);
    }

    private void setPlayerLabels(List<PlayerTotalScore> pts) {
        Label[] playerLabels = {player1, player2, player3, player4};

        for (int i = 0; i < pts.size() && i < playerLabels.length; i++) {
            playerLabels[i].setText(pts.get(i).playerName());
        }
    }

    private void setScoreLabels(List<PlayerTotalScore> pts) {
        Label[] scoreLabels = {score1, score2, score3, score4};

        for (int i = 0; i < pts.size() && i < scoreLabels.length; i++) {
            scoreLabels[i].setText(String.valueOf(pts.get(i).totalScore()));
        }
    }

    @FXML
    public void resumeGame(){
        gameService.prepareAndResumeGame(pausedGameId);
        ViewManager.switchTo("ScoreInput.fxml");
    }

    @FXML
    public void backToHome(){
        ViewManager.switchTo("Menu.fxml");
    }
}
