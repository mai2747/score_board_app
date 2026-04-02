package com.scoreboard.app.controller;


import com.scoreboard.app.AppContext;
import com.scoreboard.app.Exception.ValidationException;
import com.scoreboard.app.model.GameSettings;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import com.scoreboard.app.viewmodel.RankingEntryDTO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class ScoreInputController implements ContextAwareController{

    @FXML private MenuItem resetTimerButton;
    @FXML private Label playerNameLabel;
    @FXML private Label turnNumberLabel;
    @FXML private TextField scoreField;
    @FXML private Label errorLabel;
    @FXML private Button submitButton;

    @FXML private Label firstRanked;
    @FXML private Label secondRanked;
    @FXML private Label thirdRanked;
    @FXML private Label fourthRanked;

    @FXML private Label prevPlayerLabel;
    @FXML private TextField prevScoreField;
    @FXML private Label infoLabel;

    @FXML private Pane prevScorePane;
    @FXML private Pane currentRankingPane;

    @FXML Pane timerPane;
    @FXML Label timerLabel;
    @FXML ProgressBar timerBar;
    @FXML Button timerToggleButton;
    private Timeline timerTimeline;
    private int remainingSeconds;
    private int totalSeconds;
    private int pauseRequests = 0;
    private boolean wasRunningBeforePause = false;

    private GameService gameService;
    private GameSettings gameSettings;

    @Override
    public void setContext(AppContext context){
        this.gameService = context.gameService();

        gameSettings = gameService.getCurrentGame().getSettings();
        if(gameSettings.isLiveRankingEnabled()) currentRankingPane.setVisible(true);
        if(gameSettings.isTimerEnabled()){
            timerPane.setVisible(true);
            totalSeconds = gameSettings.getTimerSettings().getSeconds();
            startTimer(totalSeconds);
        }

        updatePlayerDisplay();
        refreshOptionalSettings();
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

        resetTimerButton.setVisible(false);
        prevScorePane.setVisible(false);
    }

    @FXML private void submitScore(ActionEvent event){
        String scoreInField = scoreField.getText();

        errorLabel.setText("");
        errorLabel.setVisible(false);

        try {
            gameService.submitScore(scoreInField);
        } catch (ValidationException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
            return;
        }

        refreshLabels(scoreInField);
        refreshOptionalSettings();
    }

    private void updatePlayerDisplay(){
        Pair<String, Integer> nameAndRound = gameService.createCurrentTurnInfo();

        String name = nameAndRound.getKey();
        int round = nameAndRound.getValue();
        System.out.println("|| Current player: " + name + " ||");

        playerNameLabel.setText("Round " + round + " / " + name + " 's Turn");
        prevPlayerLabel.setText(gameService.getPrevPlayerName());
    }

    private void updateRankingDisplay(){
        if(!currentRankingPane.isVisible()) return;

        List<RankingEntryDTO> rankings = gameService.getCurrentRanking()
                .stream()
                .sorted(Comparator.comparingInt(RankingEntryDTO::rank))
                .toList();

        List<Label> labels = List.of(firstRanked, secondRanked, thirdRanked, fourthRanked);

        for (int i = 0; i < labels.size(); i++) {
            if (i < rankings.size()) {
                RankingEntryDTO entry = rankings.get(i);
                labels.get(i).setText((i + 1) + ". " + entry.playerName() + " : " + entry.totalScore());
            } else {
                labels.get(i).setText("");
            }
        }
    }

    private void refreshLabels(String scoreInField){
        infoLabel.setText("");
        infoLabel.setVisible(false);
        updatePlayerDisplay();
        prevScorePane.setVisible(true);
        prevScoreField.setText(scoreInField);
        scoreField.clear();
    }

    private void refreshOptionalSettings(){
        if(gameSettings.isLiveRankingEnabled()) updateRankingDisplay();
        if(gameSettings.isTimerEnabled()) restartTimer();
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

        if(gameService.getCurrentGame().getSettings().isLiveRankingEnabled()) updateRankingDisplay();
    }

    @FXML private void endGame(){
        if (!scoreField.getText().isBlank()){
            errorLabel.setText("Please submit your score");
            errorLabel.setVisible(true);
        }else if(gameService.getCurrentScores().isEmpty()){
            errorLabel.setText("The game hasn't started yet");
            errorLabel.setVisible(true);
        }else{
            gameService.finishGame();
            ViewManager.switchTo("Result.fxml");
        }
    }

    public void startTimer(int seconds) {
        remainingSeconds = seconds;

        updateTimer();

        timerTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> updateTimer())
        );

        timerTimeline.setCycleCount(seconds);
        timerTimeline.play();
    }

    @FXML
    private void restartTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();  // stop?
        }
        startTimer(totalSeconds);  // change to remainingSeconds ?
    }

    private void updateTimer() {
        int min = remainingSeconds / 60;
        int sec = remainingSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", min, sec));
        timerBar.setProgress(remainingSeconds / (double) totalSeconds);

        if (remainingSeconds < 0) {
            remainingSeconds = 0;
        }

        if (remainingSeconds == 0) {
            timerTimeline.stop();
            // Display for prompt users to input score
        }

        remainingSeconds--;
    }

    public void applyUpdatedSettingsToScreen(){
        currentRankingPane.setVisible(gameSettings.isLiveRankingEnabled());

        boolean timerEnabled = gameSettings.isTimerEnabled();

        timerPane.setVisible(timerEnabled);
        resetTimerButton.setVisible(timerEnabled);

        if(timerEnabled) {
            totalSeconds = gameSettings.getTimerSettings().getSeconds();
        }

        refreshOptionalSettings();
    }

    @FXML
    public void toggleTimer(){
        if (timerTimeline != null) {
            boolean running = timerTimeline.getStatus() == Animation.Status.RUNNING;

            if (running) {
                requestPauseTimer();
            } else {
                releasePausedTimer();
            }
        }
    }

    @FXML
    private void requestPauseTimer() {
        if (timerTimeline == null) return;

        if (pauseRequests == 0 &&
                timerTimeline.getStatus() == Animation.Status.RUNNING) {
            wasRunningBeforePause = true;
            timerTimeline.pause();
            timerToggleButton.setText("Resume");
        }

        pauseRequests++;
    }

    @FXML
    private void releasePausedTimer() {
        if (timerTimeline == null) return;
        if (pauseRequests > 0) pauseRequests--;

        if (pauseRequests == 0 && wasRunningBeforePause) {
            timerTimeline.play();
            timerToggleButton.setText("Pause");
            wasRunningBeforePause = false;
        }
    }

    @FXML
    public void openGameSettingsDialog(){
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/GameSettingsDialog.fxml")
            );

            DialogPane dialogPane = loader.load();
            GameSettingsDialogController controller = loader.getController();
            controller.setGameService(gameService);
            controller.loadCurrentSettings();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Game Settings");
            dialog.setDialogPane(dialogPane);

            requestPauseTimer();
            controller.setDialog(dialog);

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    GameSettings newSettings = controller.buildGameSettings();
                    gameService.updateGameSettings(newSettings);
                    gameSettings = gameService.getCurrentGame().getSettings();
                    applyUpdatedSettingsToScreen();
                } catch (IllegalStateException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Settings");
                    alert.setHeaderText("Could not apply settings.");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open settings.");
            alert.setContentText("Failed to load the settings dialog.");
            alert.showAndWait();
        }finally {
            releasePausedTimer();
        }
    }

    @FXML
    public void saveAndReturnHome(){
        gameService.pauseGame();
        toggleTimer();

        ViewManager.switchTo("Menu.fxml");
    }

    @FXML
    public void quitGame(){
        if(!gameService.getCurrentScores().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Leave Game?");
            alert.setContentText("Current game status will not be saved.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        gameService.cancelGame();
        ViewManager.switchTo("Menu.fxml");
    }

}
