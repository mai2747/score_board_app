package com.scoreboard.app.repository;

public interface AccountScopedRepository {
    void setCurrentAccountId(Long accountId);
}