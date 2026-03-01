package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.dto.RankingDTO;
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
    }

    // public void initialize(){ endingPhrase.setText("Ranking should be here"); }

    /*
    private void renderRanking() {
        RankingDTO ranking = gameService.getCurrentRanking(); // assume

        int n = (ranking == null) ? 0 : ranking.entries().size();

        for (int i = 0; i < rankLabels.size(); i++) {
            if (i < n) {
                var e = ranking.entries().get(i);

                rankLabels.get(i).setVisible(true);
                scoreLabels.get(i).setVisible(true);

                rankLabels.get(i).setText(e.playerName());
                scoreLabels.get(i).setText(String.valueOf(e.totalScore()));
            } else {
                // 人数が足りない分は隠す（or "-"表示でもOK）
                rankLabels.get(i).setVisible(false);
                scoreLabels.get(i).setVisible(false);
            }
        }
    }
    */


    @FXML public void backToHome(){
        ViewManager.switchTo("Menu.fxml");
    }

    // TODO: After a completion of this class, add method to refresh IDs in a gameService
}
