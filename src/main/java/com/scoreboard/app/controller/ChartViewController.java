package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Score;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import com.scoreboard.app.viewmodel.ScoreRow;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartViewController implements ContextAwareController{
    @FXML private LineChart<Number,Number> scoreChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Button backToRankingsButton;
    @FXML private RadioButton chartViewRadioButton;
    @FXML private RadioButton tableViewRadioButton;

    @FXML private TableView<ScoreRow> scoreTable;
    @FXML private TableColumn<ScoreRow, String> playerColumn;
    @FXML private TableColumn<ScoreRow, Integer> turnColumn;
    @FXML private TableColumn<ScoreRow, Integer> scoreColumn;
    @FXML private TableColumn<ScoreRow, Integer> totalColumn;

    private GameService gameService;
    AppContext context;

    @Override
    public void setContext(AppContext context) {
        gameService = context.gameService();
        this.context = context;

        if(context.getSelectedGameId() != null && context.getSelectedGroupId() != null){
            gameService.setNameByPlayerId(context.getSelectedGameId(), context.getSelectedGroupId());
            backToRankingsButton.setText("Back to History");
        }else{
            backToRankingsButton.setText("Back to Rankings");
        }

        generateChartAndTable();
    }

    @FXML
    public void initialize(){
        ToggleGroup toggleGroup = new ToggleGroup();
        chartViewRadioButton.setToggleGroup(toggleGroup);
        tableViewRadioButton.setToggleGroup(toggleGroup);

        chartViewRadioButton.setSelected(true);

        scoreChart.visibleProperty().bind(chartViewRadioButton.selectedProperty());
        scoreChart.managedProperty().bind(chartViewRadioButton.selectedProperty());

        scoreTable.visibleProperty().bind(tableViewRadioButton.selectedProperty());
        scoreTable.managedProperty().bind(tableViewRadioButton.selectedProperty());

        playerColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPlayerName()));

        turnColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTurn()).asObject());

        scoreColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getScore()).asObject());

        totalColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTotal()).asObject());

    }

    private void generateChartAndTable(){
        scoreChart.getData().clear();
        scoreChart.setAnimated(false);
        scoreChart.setCreateSymbols(true);

        Long targetGameId;
        if (context.getSelectedGameId() != null && context.getSelectedGroupId() != null) {
            targetGameId = context.getSelectedGameId();
        } else {
            targetGameId = gameService.getCurrentGame().getGameId();
        }

        List<Score> scores = gameService.getScores(targetGameId);
        if (scores == null || scores.isEmpty()) {
            return;
        }

        setLineChart(scores);
        setTable(scores);
    }

    private void setLineChart(List<Score> scores) {
        Map<Long, XYChart.Series<Number, Number>> seriesMap = new LinkedHashMap<>();
        Map<Long, Integer> cumulativeScoreMap = new LinkedHashMap<>();
        Map<Long, Integer> turnCountMap = new LinkedHashMap<>();

        int maxScore = 0;
        int maxTurn = 0;

        for (Score score : scores) {
            Long pigId = score.getPlayerInGameId();

            XYChart.Series<Number, Number> series = seriesMap.computeIfAbsent(pigId, id -> {
                XYChart.Series<Number, Number> s = new XYChart.Series<>();
                s.setName(gameService.getPlayerNameByPigId(id));
                s.getData().add(new XYChart.Data<>(0, 0));
                cumulativeScoreMap.put(id, 0);
                turnCountMap.put(id, 0);
                return s;
            });

            int newCumulative = cumulativeScoreMap.get(pigId) + score.getScore();
            int newTurnCount = turnCountMap.get(pigId) + 1;

            maxScore = Math.max(maxScore, newCumulative);
            maxTurn = Math.max(maxTurn, newTurnCount);

            cumulativeScoreMap.put(pigId, newCumulative);
            turnCountMap.put(pigId, newTurnCount);

            series.getData().add(new XYChart.Data<>(newTurnCount, newCumulative));
        }

        configureAxes(maxTurn, maxScore);

        scoreChart.getData().addAll(seriesMap.values());
        javafx.application.Platform.runLater(this::applySeriesColors);
    }

    private void configureAxes(int maxTurn, int maxScore) {
        xAxis.setLabel("Turn");
        yAxis.setLabel("Total Score");

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(maxTurn + 1);
        xAxis.setTickUnit(1);
        xAxis.setForceZeroInRange(true);

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(maxScore * 1.1);
        yAxis.setTickUnit(1);
    }

    private void applySeriesColors() {
        for (int i = 0; i < scoreChart.getData().size(); i++) {
            XYChart.Series<Number, Number> series = scoreChart.getData().get(i);

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

    private void setTable(List<Score> scores) {
        Map<Long, Integer> cumulativeScoreMap = new LinkedHashMap<>();
        Map<Long, Integer> turnCountMap = new LinkedHashMap<>();

        javafx.collections.ObservableList<ScoreRow> tableData =
                javafx.collections.FXCollections.observableArrayList();

        for (Score score : scores) {
            Long pigId = score.getPlayerInGameId();

            cumulativeScoreMap.putIfAbsent(pigId, 0);
            turnCountMap.putIfAbsent(pigId, 0);

            int newTotal = cumulativeScoreMap.get(pigId) + score.getScore();
            int newTurn = turnCountMap.get(pigId) + 1;

            cumulativeScoreMap.put(pigId, newTotal);
            turnCountMap.put(pigId, newTurn);

            String playerName = gameService.getPlayerNameByPigId(pigId);

            tableData.add(new ScoreRow(
                    playerName,
                    newTurn,
                    score.getScore(),
                    newTotal
            ));
        }
        scoreTable.setItems(tableData);
    }

    @FXML
    public void backToRankings(){
        if(context.getSelectedGameId() != null && context.getSelectedGroupId() != null){
            context.setSelectedGameId(null);
            ViewManager.switchTo("GroupHistory.fxml");
        }else {
            ViewManager.switchTo("Result.fxml");
        }
    }

    @FXML
    public void backToHome(){
        ViewManager.switchTo("Menu.fxml");
    }
}
