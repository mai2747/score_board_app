package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.service.GroupService;
import com.scoreboard.app.view.ViewManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.Collections;
import java.util.List;

public class GameSetupController implements ContextAwareController{
    @FXML private ListView<Player> playerOrderListView;
    @FXML private CheckBox showRankingsCheckBox;

    private ObservableList<Player> players;
    private GameService gameService;

    private ObservableList<String> playerNames;

    @Override
    public void setContext(AppContext context) {
        gameService = context.gameService();
        putGroupInfo(); //testing
    }

    public void putGroupInfo() {
        players = FXCollections.observableArrayList(gameService.getPlayers());

        playerOrderListView.setItems(players);

        playerOrderListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Player item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
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
    private void checkIfShowRankings(){
        gameService.setIsLiveRankingDisplayOn(showRankingsCheckBox.isSelected());
    }

    @FXML
    private void startGame() {
        checkIfShowRankings();

        List<Long> orderedPlayerIds = players.stream()
                .map(Player::getId)
                .toList();

        gameService.startGameWithExistingGroup(orderedPlayerIds);
        ViewManager.switchTo("ScoreInput.fxml");
    }

    @FXML
    public void backToHome(){
        ViewManager.switchTo("Menu.fxml");
    }

}
