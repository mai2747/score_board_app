package com.scoreboard.app.service;

public class GameService {

    public String startNewGame() {

        // 1. グループ作成（2人固定）
        Group group = groupService.createDefaultGroup();

        // 2. Game オブジェクト作成
        Game game = new Game(group);

        // 3. DB保存
        gameRepository.save(game);

        // 4. 最初のプレイヤー名を返す
        return group.getPlayers().get(0).getName();
    }
}
