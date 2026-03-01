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


public class ScoreInputController implements ContextAwareController{

    @FXML private Label playerNameLabel;
    @FXML private TextField scoreField;
    @FXML private Label errorLabel;
    @FXML private Button submitButton;

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
    }


    // TODO: Replace current player from PlayerInGame object to DTO
    private void updatePlayerDisplay(){
        String name = gameService.getCurrentPlayerName();
        System.out.println("|| Displayed current player: " + name + " ||");
        playerNameLabel.setText(name);
    }

    @FXML private void submitScore(ActionEvent event){
        String scoreInField = scoreField.getText();

        errorLabel.setText("");
        errorLabel.setVisible(false);

        // Submit score and advance turn to the next player
        try {
            gameService.submitScore(scoreInField);
        } catch (ValidationException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
            return;
        }

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
