package com.scoreboard.app.model;

public enum GameStatus {
    IN_PROGRESS,
    PAUSED,
    FINISHED,
    CANCELLED;

    public boolean isFinished() {
        return this == FINISHED;
    }

    public boolean isPaused(){
        return this == PAUSED;
    }

    public boolean isInProgress() { return this == IN_PROGRESS; }
}
