package com.scoreboard.app.model;

// This entity is alternative of DB, which records the base information of each player.
// Thus, not recording order in games. Player name can be edited by users.

public class Player {
    private Long playerID;
    private Long groupId;     // 所属グループ
    private String name;      // 表示名

    public Player(Long id, Long groupId, String name){
        this.playerID = id;
        this.groupId = groupId;
        this.name = name;
    }

    public Long getId(){
        return playerID;
    }

    public String getName() {
        return name;
    }
}
