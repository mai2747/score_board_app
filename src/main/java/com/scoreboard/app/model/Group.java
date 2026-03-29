package com.scoreboard.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Group {
    private Long groupID;
    private String name;          // グループ名
    private boolean isTemporary;
    private GroupStatus status;
    private String createdAt;
    private List<Player> players;
    private List<Game> games;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Group(List<Player> players, boolean isTemporary){
        this.players = players;
        this.isTemporary = isTemporary;
        status = GroupStatus.DRAFT;
        createdAt = LocalDateTime.now().format(formatter);
        name = "Group [" + createdAt + "]" ;  // Default name
    }

    public Group(Long groupID, String name, boolean isTemporary, String createdAt) {
        this.groupID = groupID;
        this.name = name;
        this.isTemporary = isTemporary;
        this.createdAt = createdAt;
        this.players = new ArrayList<>();
        this.games = new ArrayList<>();
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

    public boolean isTemporary(){
        return isTemporary;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setStatus(GroupStatus status){ this.status = status; }

    public GroupStatus getStatus() { return status; }

    public String getCreatedTime(){ return createdAt; }
}