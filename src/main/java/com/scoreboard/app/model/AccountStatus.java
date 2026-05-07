package com.scoreboard.app.model;

public enum AccountStatus {
    DRAFT,
    ACTIVE;

    public boolean isDraft(){
        return this == DRAFT;
    }
}
