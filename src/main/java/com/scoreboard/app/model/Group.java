package com.scoreboard.app.model;

import com.scoreboard.app.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Group {
    private Long groupId;
    private Long accountId;
    private String name;
    private boolean isTemporary;
    private GroupStatus status;
    private String createdAt;
    private List<Player> players;

    // Constructor for creating new Group
    public Group(Long accountId, List<Player> players, boolean isTemporary){
        this.accountId = accountId;
        this.players = players;
        this.isTemporary = isTemporary;
        status = GroupStatus.DRAFT;
        createdAt = DateTimeUtils.format(LocalDateTime.now());
        name = "Group [" + createdAt + "]" ;  // Default name
    }

    // Constructor for obtaining from DB
    public Group(Long groupId, String name, boolean isTemporary, String createdAt) {
        this.groupId = groupId;
        this.name = name;
        this.isTemporary = isTemporary;
        this.createdAt = createdAt;
        this.players = new ArrayList<>();
    }

    public Long getAccountId(){ return accountId; }

    public void setGroupName(String groupName){
        name = groupName;
    }

    public String getGroupName(){
        return name;
    }

    public void setGroupId(Long groupID) { // Set by repository
        this.groupId = groupID;
    }

    public Long getGroupId(){
        return groupId;
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