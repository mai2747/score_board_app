package com.scoreboard.app.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ScoreInputController {

    @FXML
    private Label playerNameLabel;

    public void setPlayerName(String name) {
        playerNameLabel.setText(name);
    }
}
