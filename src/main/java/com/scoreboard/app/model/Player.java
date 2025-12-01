package com.scoreboard.app.model;

public class Player {
    private Long id;
    private Long groupId;     // 所属グループ
    private String name;      // 表示名

    public Player(Long id, Long groupId, String name){
        this.id = id;
        this.groupId = groupId;
        this.name = name;
    }

    public Long getId(){
        return id;
    }
}
