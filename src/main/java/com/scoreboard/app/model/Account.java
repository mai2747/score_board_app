package com.scoreboard.app.model;

import com.scoreboard.app.util.DateTimeUtils;

import java.time.LocalDateTime;

public class Account {
    private String name;
    private Long accountId;
    private String password;
    private int secretQuestion;
    private String secretAnswer;
    private String createdAt;
    private String lastActivityAt;
    private AccountStatus status;


    public Account(String name) {
        this.name = name;
        accountId = 0L; //dummy
    }

    public Account(String name, Long accountId){
        this.name = name;
        this.accountId = accountId;
    }

    public Account(String name, String password, int secretQuestion, String secretAnswer){
        this.name = name;
        this.password = password;  // TODO: make this secure
        this.secretQuestion = secretQuestion;
        this.secretAnswer = secretAnswer;
        createdAt = lastActivityAt = DateTimeUtils.format(LocalDateTime.now());
        status = AccountStatus.DRAFT;
    }

    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public Long getAccountId() {
        return accountId;
    }

    public void setName(String accountName){ name = accountName; }
    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setSecretQuestion(int secretQuestion) { this.secretQuestion = secretQuestion; }
    public int getSecretQuestion() {
        return secretQuestion;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }
    public String getSecretAnswer() {
        return secretAnswer;
    }

    public String getCreatedAt() { return createdAt; }

    public AccountStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return name;
    }
}
