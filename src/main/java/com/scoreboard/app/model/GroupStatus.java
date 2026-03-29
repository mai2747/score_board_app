package com.scoreboard.app.model;

public enum GroupStatus {
    DRAFT,
    ACTIVE;

    public boolean isDraft(){
        return this == DRAFT;
    }
}
