package com.scoreboard.app;

import com.scoreboard.app.repository.InMemoryScoreRepository;
import com.scoreboard.app.repository.ScoreRepository;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.service.GroupService;
import com.scoreboard.app.service.ScoreService;

public final class AppContext {
    private final ScoreRepository scoreRepository;
    private final ScoreService scoreService;
    private final GroupService groupService;
    private final GameService gameService;

    public AppContext() {
        // アプリ全体で共有したいものをここでnew
        this.scoreRepository = new InMemoryScoreRepository();
        this.scoreService = new ScoreService(scoreRepository);
        this.groupService = new GroupService();
        this.gameService = new GameService(scoreService, groupService);
    }

    public GameService gameService() { return gameService; }
    public ScoreService scoreService() { return scoreService; }
    public GroupService groupService() { return groupService; }
    public ScoreRepository scoreRepository() { return scoreRepository; }
}
