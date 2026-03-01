package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.service.GameService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResultController implements ContextAwareController{
    @FXML private Label endingPhrase;

    private GameService gameService;

    @Override
    public void setContext(AppContext context){
        this.gameService = context.gameService();
    }

    public void initialize(){
        endingPhrase.setText("Ranking should be here");
    }
}
