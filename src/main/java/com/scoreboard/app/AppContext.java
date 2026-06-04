package com.scoreboard.app;

import com.scoreboard.app.controller.PasswordInputController;
import com.scoreboard.app.model.Account;
import com.scoreboard.app.repository.*;
//import com.scoreboard.app.repository.memory.InMemoryGameRepository;
//import com.scoreboard.app.repository.memory.InMemoryGroupRepository;
//import com.scoreboard.app.repository.memory.InMemoryPlayerRepository;
//import com.scoreboard.app.repository.memory.InMemoryScoreRepository;
import com.scoreboard.app.repository.sqlite.*;
import com.scoreboard.app.service.*;
import com.sun.tools.javac.Main;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

public final class AppContext {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PasswordInputController.class);

    private final PlayerRepository playerRepository;
    private final GroupRepository groupRepository;
    private final GameRepository gameRepository;
    private final ScoreRepository scoreRepository;
    private final PlayerInGameRepository pigRepository;
    private final AccountRepository accountRepository;  // ← 追加
    private final SecurityQuestionRepository securityQuestionRepository;

    private final ScoreService scoreService;
    private final GroupService groupService;
    private final GameService gameService;
    private final MaintenanceService maintenanceService;
    private final GameQueryService gameQueryService;
    private final GamePlayService gamePlayService;
    private final AuthService authService;

    private Long groupId;
    private Long gameId;
    private GamePlayContext gameContext;

    public AppContext(Connection conn) {
        this.scoreRepository = new SqliteScoreRepository(conn);
        this.playerRepository = new SqlitePlayerRepository(conn);
        this.groupRepository = new SqliteGroupRepository(conn);
        this.gameRepository = new SqliteGameRepository(conn);
        this.pigRepository = new SqlitePlayerInGameRepository(conn);
        this.accountRepository = new SqliteAccountRepository(conn);
        this.securityQuestionRepository = new SqliteSecurityQuestionRepository(conn);

        List<AccountScopedRepository> scopedRepositories = List.of(
                scoreRepository,
                playerRepository,
                groupRepository,
                gameRepository,
                pigRepository
        );

        this.authService = new AuthService(accountRepository, securityQuestionRepository, scopedRepositories);
        this.scoreService = new ScoreService(scoreRepository);
        this.groupService = new GroupService(playerRepository, groupRepository, pigRepository);
        this.maintenanceService = new MaintenanceService(groupService, gameRepository);
        this.gameQueryService = new GameQueryService(groupService, gameRepository);
        this.gamePlayService = new GamePlayService(groupService, scoreService, gameQueryService);
        this.gameService = new GameService(scoreService, groupService, gameRepository, maintenanceService, gameQueryService, gamePlayService);
    }


    public GameService gameService() { return gameService; }
    public ScoreService scoreService() { return scoreService; }
    public GroupService groupService() { return groupService; }
    public AuthService authService() { return authService; }

    public void setSelectedGroupId(Long groupId){ this.groupId = groupId; }
    public Long getSelectedGroupId() {return groupId; }

    public void setSelectedGameId(Long gameId){ this.gameId = gameId; }
    public Long getSelectedGameId() {return gameId; }

    public void setGamePlayContext(GamePlayContext gameContext){ this.gameContext = gameContext; }
    public GamePlayContext getGameContext() { return gameContext; }
}
