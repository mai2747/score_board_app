package com.scoreboard.app.service;

import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.model.PlayerInGame;
import com.scoreboard.app.repository.GroupRepository;
import com.scoreboard.app.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GroupService {

    private PlayerRepository playerRepository;
    private GroupRepository groupRepository;

    public GroupService(PlayerRepository playerRepository, GroupRepository groupRepository) {
        this.playerRepository = playerRepository;
        this.groupRepository = groupRepository;
    }

    public Group createGroup(List<String> names, boolean isTemporary){
        System.out.println("Creating new group");
        List<Player> players = new ArrayList<>();

        int playerNum = names.size();
        for(int i = 0; i < playerNum; i++){
            String playerName = names.get(i);
            Player p = new Player(playerName);
            p.setId(playerRepository.reserveId());
            players.add(p);
        }

        return new Group(players, isTemporary);
    }

    public List<PlayerInGame> makePlayerList(Group currentGroup){
        List<PlayerInGame> playersInGame = new ArrayList<>();

        int order = 1;
        for (Long playerId : currentGroup.getPlayerIDs()) {  // ← group内のプレイヤーID一覧
            PlayerInGame pig = new PlayerInGame(0l, playerId, order++);
            playersInGame.add(pig);
        }
        return playersInGame;
    }

    public List<PlayerInGame> getOrderedPlayers(List<PlayerInGame> playersInGame){
        return playersInGame.stream().sorted(Comparator.comparingInt(PlayerInGame::getTurnOrder)).toList();
    }
}


