package com.scoreboard.app;

import com.scoreboard.app.model.Account;
import com.scoreboard.app.repository.*;
//import com.scoreboard.app.repository.memory.InMemoryGameRepository;
//import com.scoreboard.app.repository.memory.InMemoryGroupRepository;
//import com.scoreboard.app.repository.memory.InMemoryPlayerRepository;
//import com.scoreboard.app.repository.memory.InMemoryScoreRepository;
import com.scoreboard.app.repository.sqlite.*;
import com.scoreboard.app.service.*;
import com.sun.tools.javac.Main;

import java.sql.Connection;

public final class AppContext {
    private final PlayerRepository playerRepository;
    private final GroupRepository groupRepository;
    private final GameRepository gameRepository;
    private final ScoreRepository scoreRepository;
    private final PlayerInGameRepository pigRepository;

    private final ScoreService scoreService;
    private final GroupService groupService;
    private final GameService gameService;
    private final MaintenanceService maintenanceService;
    private final GameQueryService gameQueryService;
    private final GamePlayService gamePlayService;

    private Account loggedInAccount;
    private Long groupId;
    private Long gameId;
    private GamePlayContext gameContext;


    public AppContext(Connection conn) {
        this.scoreRepository = new SqliteScoreRepository(conn);
        this.playerRepository = new SqlitePlayerRepository(conn);
        this.groupRepository = new SqliteGroupRepository(conn);
        this.gameRepository = new SqliteGameRepository(conn);
        this.pigRepository = new SqlitePlayerInGameRepository(conn);

        this.scoreService = new ScoreService(scoreRepository);
        this.groupService = new GroupService(playerRepository, groupRepository, pigRepository);
        this.maintenanceService = new MaintenanceService(groupService, gameRepository);
        this.gameQueryService = new GameQueryService(groupService, gameRepository);
        this.gamePlayService = new GamePlayService(groupService, scoreService, gameQueryService);
        this.gameService = new GameService(scoreService, groupService, gameRepository, maintenanceService, gameQueryService, gamePlayService);
    }

    public Account requireAccount() {
        if (loggedInAccount == null) {
            throw new IllegalStateException("No logged-in account");
        }
        return loggedInAccount;
    }

    public Long requireAccountId() {
        return requireAccount().getAccountId();
    }

    public void login(Account account) {
        this.loggedInAccount = account;
    }

    public void logout() {
        this.loggedInAccount = null;
    }

    public GameService gameService() { return gameService; }
    public ScoreService scoreService() { return scoreService; }
    public GroupService groupService() { return groupService; }

    public void setSelectedGroupId(Long groupId){ this.groupId = groupId; }
    public Long getSelectedGroupId() {return groupId; }

    public void setSelectedGameId(Long gameId){ this.gameId = gameId; }
    public Long getSelectedGameId() {return gameId; }

    public void setGamePlayContext(GamePlayContext gameContext){ this.gameContext = gameContext; }
    public GamePlayContext getGameContext() { return gameContext; }
}
