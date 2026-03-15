package com.scoreboard.app.model;

public class GameSettings {
    private boolean liveRankingEnabled;
    private TimerSettings timerSettings;

    public GameSettings(boolean liveRankingEnabled, TimerSettings timerSettings) {
        this.liveRankingEnabled = liveRankingEnabled;
        this.timerSettings = timerSettings;
    }

    public boolean isLiveRankingEnabled() {
        return liveRankingEnabled;
    }

    public boolean isTimerEnabled(){ return timerSettings.isEnabled(); }

    public void setLiveRankingEnabled(boolean liveRankingEnabled) {
        this.liveRankingEnabled = liveRankingEnabled;
    }

    public TimerSettings getTimerSettings() {
        return timerSettings;
    }

    public void setTimerSettings(TimerSettings timerSettings) {
        if (timerSettings == null) {
            throw new IllegalArgumentException("timerSettings must not be null");
        }
        this.timerSettings = timerSettings;
    }
}
