package com.scoreboard.app;

import com.scoreboard.app.repository.*;
import com.scoreboard.app.repository.memory.InMemoryGameRepository;
import com.scoreboard.app.repository.memory.InMemoryGroupRepository;
import com.scoreboard.app.repository.memory.InMemoryPlayerRepository;
import com.scoreboard.app.repository.memory.InMemoryScoreRepository;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.service.GroupService;
import com.scoreboard.app.service.ScoreService;

public final class AppContext {
    private final PlayerRepository playerRepository;
    private final GroupRepository groupRepository;
    private final GameRepository gameRepository;
    private final ScoreRepository scoreRepository;

    private final ScoreService scoreService;
    private final GroupService groupService;
    private final GameService gameService;

    public AppContext() {
        // アプリ全体で共有したいものをここでnew
        this.scoreRepository = new InMemoryScoreRepository();
        this.playerRepository = new InMemoryPlayerRepository();
        this.groupRepository = new InMemoryGroupRepository(playerRepository);  // Should it contain playerRepository?
        this.gameRepository = new InMemoryGameRepository(playerRepository, groupRepository);

        this.scoreService = new ScoreService(scoreRepository);
        this.groupService = new GroupService(playerRepository, groupRepository);
        this.gameService = new GameService(scoreService, groupService, gameRepository);
    }

    public GameService gameService() { return gameService; }
    public ScoreService scoreService() { return scoreService; }
    public GroupService groupService() { return groupService; }

    public PlayerRepository playerRepository() { return playerRepository; }
    public GroupRepository groupRepository() { return groupRepository; }
    public GameRepository gameRepository() { return gameRepository; }
    public ScoreRepository scoreRepository() { return scoreRepository; }
}
