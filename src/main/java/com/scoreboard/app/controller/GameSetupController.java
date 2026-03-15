package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.GameSettings;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.model.TimerSettings;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.service.GroupService;
import com.scoreboard.app.view.ViewManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

public class GameSetupController implements ContextAwareController{
    @FXML private ListView<Player> playerOrderListView;
    @FXML private CheckBox showRankingsCheckBox;

    @FXML private CheckBox useTimerCheckBox;
    @FXML private HBox timerOptionsBox;
    @FXML private Spinner<Integer> minutesSpinner;
    @FXML private Spinner<Integer> secondsSpinner;

    private ObservableList<Player> players;
    private GameService gameService;

    private ObservableList<String> playerNames;

    @Override
    public void setContext(AppContext context) {
        gameService = context.gameService();
        putGroupInfo(); //testing
    }

    @FXML
    public void initialize() {
        minutesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 3)
        );
        secondsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5)
        );

        timerOptionsBox.setVisible(false);
        timerOptionsBox.setManaged(false);
    }

    public void putGroupInfo() {
        players = FXCollections.observableArrayList(gameService.getPlayers());

        playerOrderListView.setItems(players);

        playerOrderListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Player item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : getIndex()+1 + ". " + item.getName());
            }
        });
    }

    @FXML
    private void toggleTimerOptions() {
        boolean enabled = useTimerCheckBox.isSelected();
        timerOptionsBox.setVisible(enabled);
        timerOptionsBox.setManaged(enabled);
    }

    @FXML
    private void moveUp() {
        int index = playerOrderListView.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            Collections.swap(players, index, index - 1);
            playerOrderListView.getSelectionModel().select(index - 1);
        }
    }

    @FXML
    private void moveDown() {
        int index = playerOrderListView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < players.size() - 1) {
            Collections.swap(players, index, index + 1);
            playerOrderListView.getSelectionModel().select(index + 1);
        }
    }

    @FXML
    private void startGame() {
        List<Long> orderedPlayerIds = players.stream()
                .map(Player::getId)
                .toList();

        TimerSettings timerSettings;

        if (useTimerCheckBox.isSelected()) {
            int totalSeconds = minutesSpinner.getValue() * 60 + secondsSpinner.getValue();
            if (totalSeconds <= 0) {
                // showValidationError("Please set at least 1 second.");
                return;
            }
            timerSettings = TimerSettings.ofSeconds(totalSeconds);
        } else {
            timerSettings = TimerSettings.off();
        }

        GameSettings gameSettings = new GameSettings(showRankingsCheckBox.isSelected(), timerSettings);

        gameService.startGameWithExistingGroup(orderedPlayerIds, gameSettings);
        ViewManager.switchTo("ScoreInput.fxml");
    }

    @FXML
    public void backToHome(){
        ViewManager.switchTo("Menu.fxml");
    }

}
