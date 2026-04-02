package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import com.scoreboard.app.viewmodel.PlayerGameStatDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerStatsViewController implements ContextAwareController{
    @FXML private LineChart<Number, Number> statsChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private RadioButton scoreRadioButton;
    @FXML private RadioButton rankingRadioButton;
    @FXML private VBox playerCheckBoxContainer;

    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private Map<Long, List<PlayerGameStatDTO>> playerStatsMap;
    private int maxPlayers;

    GameService gameService;


    @Override
    public void setContext(AppContext context) {
        gameService = context.gameService();

        Group group = gameService.getGroup(context.getSelectedGroupId());
        maxPlayers = group.getPlayers().size();

        configureAxes();
        createPlayerCheckBoxes(group.getPlayers());

        playerStatsMap = gameService.getPlayerStatsByGroupId(group.getGroupId());

        redrawChart();
    }

    @FXML
    public void initialize() {
        ToggleGroup toggleGroup = new ToggleGroup();
        scoreRadioButton.setToggleGroup(toggleGroup);
        rankingRadioButton.setToggleGroup(toggleGroup);

        scoreRadioButton.setSelected(true);

        toggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                redrawChart();
            }
        });
    }

    private void redrawChart() {
        if (playerStatsMap == null) return;

        statsChart.getData().clear();

        boolean showScore = scoreRadioButton.isSelected();

        yAxis.setLabel(showScore ? "Total Score" : "Rank");
        updateYAxis(showScore);

        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.isSelected()) continue;

            Long playerId = (Long) checkBox.getUserData();
            List<PlayerGameStatDTO> stats = playerStatsMap.get(playerId);

            if (stats == null || stats.isEmpty()) continue;

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(stats.get(0).playerName());

            for (PlayerGameStatDTO stat : stats) {
                Number value = showScore ? stat.totalScore() : stat.rank();
                series.getData().add(new XYChart.Data<>(stat.gameIndex(), value));
            }

            statsChart.getData().add(series);
        }
        Platform.runLater(this::applySeriesColors);
    }


    private void createPlayerCheckBoxes(List<Player> players) {
        playerCheckBoxContainer.getChildren().clear();
        checkBoxes.clear();

        for (Player player : players) {
            CheckBox checkBox = new CheckBox(player.getName());
            checkBox.setUserData(player.getId());
            checkBox.setSelected(true);
            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> redrawChart());

            checkBoxes.add(checkBox);
            playerCheckBoxContainer.getChildren().add(checkBox);
        }
    }

    private void configureAxes() {
        xAxis.setLabel("Game");
        yAxis.setLabel("Total Score");

        xAxis.setAutoRanging(true);
        xAxis.setTickUnit(1);
        xAxis.setMinorTickVisible(false);
        xAxis.setMinorTickCount(0);

        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                int maxPlayers = 4;
                return String.valueOf(maxPlayers + 1 - object.intValue());
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        });
    }

    private void updateYAxis(boolean showScore) {
        if (showScore) {
            yAxis.setLabel("Total Score");
            yAxis.setTickLabelFormatter(null);
            yAxis.setForceZeroInRange(false);
        } else {
            yAxis.setLabel("Rank");

            yAxis.setTickLabelFormatter(new StringConverter<Number>() {
                @Override
                public String toString(Number object) {
                    return String.valueOf(maxPlayers + 1 - object.intValue());
                }

                @Override
                public Number fromString(String string) {
                    return maxPlayers + 1 - Integer.parseInt(string);
                }
            });
            yAxis.setForceZeroInRange(false);
        }
    }


    private void applySeriesColors() {
        for (int i = 0; i < statsChart.getData().size(); i++) {
            XYChart.Series<Number, Number> series = statsChart.getData().get(i);

            String styleClass = switch (i % 4) {
                case 0 -> "player-line-1";
                case 1 -> "player-line-2";
                case 2 -> "player-line-3";
                default -> "player-line-4";
            };

            if (series.getNode() != null) {
                series.getNode().getStyleClass().add(styleClass);
            }

            for (XYChart.Data<Number, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    data.getNode().getStyleClass().add(styleClass);
                }
            }
        }
    }

    @FXML void backToGroupHistory(){
        ViewManager.switchTo("GroupHistory.fxml");
    }
}
