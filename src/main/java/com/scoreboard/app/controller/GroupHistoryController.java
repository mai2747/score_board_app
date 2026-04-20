package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Game;
import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import com.scoreboard.app.viewmodel.PlayerWinRateDTO;
import com.scoreboard.app.viewmodel.RankingDTO;
import com.scoreboard.app.viewmodel.RankingEntryDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GroupHistoryController implements ContextAwareController{
    @FXML private ListView<Player> playerNameListView;
    @FXML private ListView<RankingDTO>  pastRankingsListView;
    @FXML private Label groupNameLabel;
    @FXML private Label lastPlayedTimeLabel;
    @FXML private Label bestPlayerLabel;
    @FXML private Label selectedPlayerStatsLabel;
    @FXML private HBox playerStatsHBox;
    @FXML private Button viewChartButton;
    @FXML private Button deleteGameButton;

    private Map<Long, PlayerWinRateDTO> playerWinRateMap;
    private PlayerWinRateDTO bestWinRatePlayer;

    GameService gameService;
    AppContext context;
    Group group;

    @Override
    public void setContext(AppContext context) {
        this.gameService = context.gameService();
        this.context = context;

        playerStatsHBox.setVisible(false);
        playerStatsHBox.setManaged(false);

        Long groupId = context.getSelectedGroupId();
        group = gameService.getGroup(groupId);

        refreshHistoryDisplay();
    }

    private void refreshHistoryDisplay(){
        this.playerWinRateMap = gameService.getPlayerWinRatesByGroupId(group.getGroupId());
        this.bestWinRatePlayer = gameService.findBestWinRatePlayer(playerWinRateMap);

        displayGroupInfo(group);
        displayRankings(group);
        displayBestWinRate();
    }

    @FXML
    public void initialize(){
        deleteGameButton.disableProperty().bind(
                pastRankingsListView.getSelectionModel().selectedItemProperty().isNull()
        );
        viewChartButton.disableProperty().bind(
                pastRankingsListView.getSelectionModel().selectedItemProperty().isNull()
        );
        viewChartButton.setTooltip(new Tooltip("Select a game to view chart"));

        playerNameListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;

            playerStatsHBox.setVisible(true);
            playerStatsHBox.setManaged(true);

            PlayerWinRateDTO stats = playerWinRateMap.get(newValue.getId());
            if (stats != null) {
                selectedPlayerStatsLabel.setText(String.format(
                        "%s: %.1f%% (%d/%d wins)",
                        stats.playerName(),
                        stats.winRate() * 100,
                        stats.wins(),
                        stats.gamesPlayed()
                ));
            } else {
                selectedPlayerStatsLabel.setText(newValue.getName() + ": No finished games");
            }
        });
    }

    private void displayGroupInfo(Group group){
        groupNameLabel.setText(group.getGroupName());
        lastPlayedTimeLabel.setText("Created date: " + group.getCreatedTime());

        ObservableList<Player> players = FXCollections.observableArrayList(group.getPlayers());

        playerNameListView.setItems(players);  // should be String?
    }

    private void displayRankings(Group group){
        List<Game> recentGames = gameService.findRecentFinishedGamesByGroupId(group.getGroupId());

        ObservableList<RankingDTO> rankingItems = FXCollections.observableArrayList();

        for (Game game : recentGames) {
            RankingDTO ranking = gameService.calculateRanking(game.getGameId());
            rankingItems.add(ranking);
        }

        pastRankingsListView.setItems(rankingItems);

        pastRankingsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(RankingDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                List<RankingEntryDTO> entries = item.entries();

                if (entries.isEmpty() || entries.size() == 1) {
                    setText("No ranking data");
                    return;
                }

                if (entries.size() == 2) {
                    setText(formatEntry(entries.get(0)) + "   " + formatEntry(entries.get(1)));
                    return;
                }

                String firstLine = formatEntry(entries.get(0));
                String secondLine = entries.subList(1, entries.size()).stream()
                        .map(this::formatEntry)
                        .reduce((a, b) -> a + "   " + b)
                        .orElse("");

                setText(firstLine + "\n" + secondLine);
            }

            private String formatEntry(RankingEntryDTO entry) {
                return ordinal(entry.rank()) + ". " + entry.playerName() + " " + entry.totalScore();
            }
        });
    }

    private void displayBestWinRate() {
        if (bestWinRatePlayer == null) {
            bestPlayerLabel.setText("No finished games");
            return;
        }

        bestPlayerLabel.setText(String.format(
                "Best Performer: \n %s (%.1f%%, %d/%d wins)",
                bestWinRatePlayer.playerName(),
                bestWinRatePlayer.winRate() * 100,
                bestWinRatePlayer.wins(),
                bestWinRatePlayer.gamesPlayed()
        ));
    }

    @FXML
    void deleteSelectedGame(){
        RankingDTO selected = pastRankingsListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String rankingText = formatRankingForDialog(selected);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Delete Game?");
        alert.setContentText(
                "The following game result will be deleted:\n\n" +
                        rankingText +
                        "\n\nThis game status will be deleted completely."
        );

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        gameService.deleteGameByGameId(selected.gameId());
        refreshHistoryDisplay();
    }

    private String formatRankingForDialog(RankingDTO ranking) {
        if (ranking == null || ranking.entries() == null || ranking.entries().isEmpty()) {
            return "No ranking data";
        }

        StringBuilder sb = new StringBuilder();
        for (RankingEntryDTO entry : ranking.entries()) {
            sb.append(ordinal(entry.rank()))
                    .append(". ")
                    .append(entry.playerName())
                    .append(" ")
                    .append(entry.totalScore())
                    .append("\n");
        }
        return sb.toString().trim();
    }

    private String ordinal(int rank) {
        return switch (rank) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> rank + "th";
        };
    }

    @FXML void playerStatsTransition(){
        ViewManager.switchTo("PlayerStatsView.fxml");
    }

    @FXML void gameChartViewTransition(){
        RankingDTO selected = pastRankingsListView.getSelectionModel().getSelectedItem();

        if (selected == null) return;

        context.setSelectedGameId(selected.gameId());
        ViewManager.switchTo("ChartView.fxml");
    }

    @FXML void backToGroupHistoryList(){
        context.setSelectedGroupId(null);
        ViewManager.switchTo("HistoryGroupSelect.fxml");
    }
}
