package com.scoreboard.app.repository;

import com.scoreboard.app.model.SecQuestion;

import java.util.List;

public interface SecurityQuestionRepository {
    List<SecQuestion> findAll();
}
