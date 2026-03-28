package com.scoreboard.app.model;

// This entity is alternative of DB, which records the base information of each player.
// Thus, not recording order in games. Player name can be edited by users.

public class Player {
    private Long playerID;
    private Long groupId;     // 所属グループ
    private String name;      // 表示名

    public Player(String name){
        this.name = name;
    }

    public Player(Long playerId, Long groupId, String name){
        this.playerID = playerId;
        this.groupId = groupId;
        this.name = name;
    }

    public Long getId() { return playerID; }
    public void setId(Long id) { this.playerID = id; } // Should be used by repository（package-private）

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

}
