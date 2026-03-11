package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.viewmodel.RankingDTO;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class ResultController implements ContextAwareController{
    @FXML private Label endingPhrase;
    @FXML private Label firstRank;
    @FXML private Label secondRank;
    @FXML private Label thirdRank;
    @FXML private Label fourthRank;

    @FXML private Label firstScore;
    @FXML private Label secondScore;
    @FXML private Label thirdScore;
    @FXML private Label fourthScore;

    private List<Label> rankLabels;
    private List<Label> scoreLabels;

    private GameService gameService;

    @Override
    public void setContext(AppContext context){
        this.gameService = context.gameService();

        renderRanking();
        gameService.saveGame();
    }

    @FXML
    private void initialize() {
        rankLabels = List.of(firstRank, secondRank, thirdRank, fourthRank);
        scoreLabels = List.of(firstScore, secondScore, thirdScore, fourthScore);

        // endingPhrase.setText("Results");
    }

    private void renderRanking() {
        System.out.println("|| Game Finished ||");
        RankingDTO ranking = gameService.getCurrentRanking(); // assume

        int n = (ranking == null) ? 0 : ranking.entries().size();

        for (int i = 0; i < rankLabels.size(); i++) {
            if (i < n) {
                var e = ranking.entries().get(i);

                rankLabels.get(i).setVisible(true);
                scoreLabels.get(i).setVisible(true);

                rankLabels.get(i).setText((i+1) + ". " + e.playerName());
                scoreLabels.get(i).setText(String.valueOf(e.totalScore()));

                System.out.println(i + ". " + e.playerName() + ":" + e.totalScore());
            } else {
                rankLabels.get(i).setText("-");
                scoreLabels.get(i).setText("-");
            }
        }
    }

    @FXML
    public void playAgain(){
        ViewManager.switchTo("GameSetup.fxml");
    }

    @FXML
    public void backToHome(){
        ViewManager.switchTo("Menu.fxml");
    }
}