package com.scoreboard.app.repository;

import com.scoreboard.app.model.SecurityQuestion;

import java.util.List;

public interface SecurityQuestionRepository {
    List<SecurityQuestion> findAll();
}
