package com.scoreboard.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group {
    private Long id;
    private String name;          // グループ名
    private boolean temporary;    // 一時グループかどうか（Must要件7,8対策）
    private List<Player> players;
    private List<Game> games;

    public void setName(String GroupName){
        name = GroupName;
    }

    public void setId(){
        id = 0l;  // dummy ... sql AUTOINCREMENT will generate the actual ID in later implementation
    }

    public Long getId(){
        return id;
    }

    public List<Long> getPlayerIds(){
        List<Long> playerIds = new ArrayList<>();
        for (Player player: players){
            playerIds.add(player.getId());
        }

        return playerIds;
    }

    public void setAsTemporaryGroup(){
        temporary = true;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}