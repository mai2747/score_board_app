package com.scoreboard.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Group {
    private Long groupID;
    private String name;          // グループ名
    private boolean isTemporary;    // 一時グループかどうか（Must要件7,8対策）
    private List<Player> players;
    private List<Game> games;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Group(List<Player> players, boolean isTemporary){
        this.players = players;
        this.isTemporary = isTemporary;
        name = "Group [" + LocalDateTime.now().format(formatter) + "]" ;  // Default name
    }

    public void setGroupName(String groupName){
        name = groupName;
    }

    public String getGroupName(){
        return name;
    }

    public void setGroupID(Long groupID) { // Set by repository
        this.groupID = groupID;
    }

    public Long getGroupID(){
        return groupID;
    }

    public List<Long> getPlayerIDs(){
        List<Long> playerIds = new ArrayList<>();
        for (Player player: players){
            playerIds.add(player.getId());
        }
        return playerIds;
    }

    public void setAsTemporaryGroup(){
        isTemporary = true;
    }

    public boolean isTemporary(){
        return isTemporary;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }
}