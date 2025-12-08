package com.scoreboard.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResultController {
    @FXML private Label endingPhrase;

    public void initialize(){
        endingPhrase.setText("Ranking should be here");
    }
}
