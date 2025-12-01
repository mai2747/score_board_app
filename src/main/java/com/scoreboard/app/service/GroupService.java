package com.scoreboard.app.service;

import com.scoreboard.app.model.Game;
import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.Player;

import java.util.ArrayList;
import java.util.List;

public class GroupService {

    public Group createGroup(List<String> names){

        //dummies
        Group newGroup = new Group();
        newGroup.setId();  // setting random id;
        newGroup.setName("Default Group");
        newGroup.setAsTemporaryGroup();
        List<Player> players = new ArrayList<>();

        int playerNum = names.size();
        for(int i = 0; i < playerNum; i++){
            Long dummyPlayerID = 123l;
            Long dummyGroupID = 123l;
            String playerName = names.get(i);
            Player player = new Player(dummyPlayerID, dummyGroupID, playerName);
            players.add(player);
        }
        newGroup.setPlayers(players);

        return newGroup;
    }
}


