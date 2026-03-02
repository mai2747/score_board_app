package com.scoreboard.app.service;

import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.model.PlayerInGame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GroupService {

    public Group createGroup(List<String> names){
        System.out.println("Creating new group");
        //dummies
        Group newGroup = new Group();
        newGroup.setName("Default Group");
        newGroup.setAsTemporaryGroup();
        List<Player> players = new ArrayList<>();

        int playerNum = names.size();
        for(int i = 0; i < playerNum; i++){
            String playerName = names.get(i);
            Player player = new Player(playerName);
            players.add(player);
        }
        newGroup.setPlayers(players);

        return newGroup;
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


