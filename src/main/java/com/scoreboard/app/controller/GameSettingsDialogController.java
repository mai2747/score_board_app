package com.scoreboard.app.controller;

import com.scoreboard.app.model.GameSettings;
import com.scoreboard.app.model.TimerSettings;
import com.scoreboard.app.service.GameService;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;

public class GameSettingsDialogController {

    @FXML
    private CheckBox showRankingsCheckBox;
    @FXML
    private CheckBox useTimerCheckBox;
    @FXML
    private HBox timerOptionsBox;
    @FXML
    private Spinner<Integer> minutesSpinner;
    @FXML
    private Spinner<Integer> secondsSpinner;
    @FXML
    private Label errorLabel;

    private GameService gameService;

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    @FXML
    private void initialize() {
        minutesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 3)
        );
        secondsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0)
        );

        timerOptionsBox.setVisible(false);
        timerOptionsBox.setManaged(false);
        errorLabel.setVisible(false);
    }

    public void loadCurrentSettings() {
        GameSettings settings = gameService.getCurrentGame().getSettings();

        showRankingsCheckBox.setSelected(settings.isLiveRankingEnabled());

        boolean timerEnabled = settings.isTimerEnabled();
        useTimerCheckBox.setSelected(timerEnabled);
        timerOptionsBox.setVisible(timerEnabled);
        timerOptionsBox.setManaged(timerEnabled);

        if (timerEnabled) {
            int totalSeconds = settings.getTimerSettings().getSeconds();
            minutesSpinner.getValueFactory().setValue(totalSeconds / 60);
            secondsSpinner.getValueFactory().setValue(totalSeconds % 60);
        } else {
            minutesSpinner.getValueFactory().setValue(0);
            secondsSpinner.getValueFactory().setValue(0);
        }
    }

    @FXML
    private void toggleTimerOptions() {
        boolean enabled = useTimerCheckBox.isSelected();
        timerOptionsBox.setVisible(enabled);
        timerOptionsBox.setManaged(enabled);
    }

    public GameSettings buildGameSettings() {
        TimerSettings timerSettings;

        if (useTimerCheckBox.isSelected()) {
            int totalSeconds = minutesSpinner.getValue() * 60 + secondsSpinner.getValue();

            if (totalSeconds <= 0) {
                throw new IllegalStateException("Timer must be greater than 0.");
            }

            timerSettings = TimerSettings.ofSeconds(totalSeconds);
        } else {
            timerSettings = TimerSettings.off();
        }

        return new GameSettings(
                showRankingsCheckBox.isSelected(),
                timerSettings
        );
    }
}