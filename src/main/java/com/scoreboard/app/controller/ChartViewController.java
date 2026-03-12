package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Score;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartViewController implements ContextAwareController{
    @FXML private LineChart<Number,Number> scoreChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    private GameService gameService;

    @Override
    public void setContext(AppContext context) {
        gameService = context.gameService();

        setLineChart();
    }

    private void setLineChart() {
        scoreChart.getData().clear();
        scoreChart.setAnimated(false);
        scoreChart.setCreateSymbols(true);

        configureAxes();

        List<Score> scores = gameService.scoreService.getScores();
        if (scores == null || scores.isEmpty()) {
            return;
        }

        Map<Long, XYChart.Series<Number, Number>> seriesMap = new LinkedHashMap<>();
        Map<Long, Integer> cumulativeScoreMap = new LinkedHashMap<>();
        Map<Long, Integer> turnCountMap = new LinkedHashMap<>();

        for (Score score : scores) {
            Long playerId = score.getPlayerId();

            XYChart.Series<Number, Number> series = seriesMap.computeIfAbsent(playerId, id -> {
                XYChart.Series<Number, Number> s = new XYChart.Series<>();
                s.setName(gameService.getPlayerNameByID(id));
                s.getData().add(new XYChart.Data<>(0, 0));
                cumulativeScoreMap.put(id, 0);
                turnCountMap.put(id, 0);
                return s;
            });

            int newCumulative = cumulativeScoreMap.get(playerId) + score.getScore();
            int newTurnCount = turnCountMap.get(playerId) + 1;

            cumulativeScoreMap.put(playerId, newCumulative);
            turnCountMap.put(playerId, newTurnCount);

            series.getData().add(new XYChart.Data<>(newTurnCount, newCumulative));
        }

        scoreChart.getData().addAll(seriesMap.values());

        javafx.application.Platform.runLater(this::applySeriesColors);
    }

    private void configureAxes() {
        xAxis.setLabel("Turn");
        yAxis.setLabel("Total Score");

        xAxis.setAutoRanging(true);
        xAxis.setForceZeroInRange(true);
        xAxis.setTickUnit(1);
        xAxis.setMinorTickVisible(false);
        xAxis.setMinorTickCount(0);
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

    @FXML
    public void backToRankings(){
        ViewManager.switchTo("Result.fxml");
    }

    @FXML
    public void backToHome(){
        ViewManager.switchTo("Menu.fxml");
    }
}
