package com.scoreboard.app.controller;


import com.scoreboard.app.AppContext;
import com.scoreboard.app.Exception.ValidationException;
import com.scoreboard.app.model.PlayerInGame;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;


public class ScoreInputController implements ContextAwareController{

    @FXML private Label playerNameLabel;
    @FXML private TextField scoreField;
    @FXML private Label errorLabel;
    @FXML private Button submitButton;

    @FXML private Label prevPlayerLabel;
    @FXML private TextField prevScoreField;
    @FXML private Label infoLabel;
    @FXML private Button editScoreButton;

    @FXML private Pane prevScorePane;

    private GameService gameService;

    @Override
    public void setContext(AppContext context){
        this.gameService = context.gameService();

        updatePlayerDisplay();
    }

    public void onSceneReady(){
        updatePlayerDisplay();
    }

    @FXML
    private void initialize() {
        // バリデーションはserviceに任せるべき？
        var empty = scoreField.textProperty().isEmpty();
        submitButton.disableProperty().bind(empty);

        var tip = new javafx.scene.control.Tooltip("Please input your score");
        submitButton.tooltipProperty().bind(
                javafx.beans.binding.Bindings.when(empty).then(tip).otherwise((javafx.scene.control.Tooltip) null)
        );

        prevScorePane.setVisible(false);
    }

    private void updatePlayerDisplay(){
        String name = gameService.getPlayerName(gameService.getCurrentPlayer());
        System.out.println("|| Current player: " + name + " ||");
        playerNameLabel.setText(name);

        if(gameService.getPrevPlayer() != null){
            String prevPlayerName = gameService.getPlayerName(gameService.getPrevPlayer());
            prevPlayerLabel.setText(prevPlayerName);
        }
    }

    @FXML private void submitScore(ActionEvent event){
        String scoreInField = scoreField.getText();

        errorLabel.setText("");
        errorLabel.setVisible(false);
        infoLabel.setText("");
        infoLabel.setVisible(false);

        // Submit score and advance turn to the next player
        try {
            gameService.submitScore(scoreInField);
        } catch (ValidationException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
            return;
        }

        updatePlayerDisplay();
        prevScorePane.setVisible(true);
        prevScoreField.setText(scoreInField);
        scoreField.clear();
    }

    @FXML private void editScore(){
        String prevScoreInField = prevScoreField.getText();
        int prevScore = gameService.getPrevScore().getScore();

        infoLabel.setText("");
        infoLabel.setVisible(false);

        try {
            gameService.editPrevScore(prevScoreInField);
            prevScoreField.setText(Integer.toString(gameService.getPrevScore().getScore()));
            infoLabel.setText("Score updated:" + prevScore + " -> " + prevScoreInField);
        } catch (ValidationException e) {
            infoLabel.setText(e.getMessage());
            infoLabel.setVisible(true);
        }
    }

    @FXML private void endGame(){
        if (!scoreField.getText().isBlank()){
            errorLabel.setText("Please submit your score");
            errorLabel.setVisible(true);
        }else {
            ViewManager.switchTo("Result.fxml");
        }
    }


    public void setPlayerName(String name) {
        playerNameLabel.setText(name);
    }

    public void setGameService(GameService gameService){
        this.gameService = gameService;
    }
}
