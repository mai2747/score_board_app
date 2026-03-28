package com.scoreboard.app.service;

import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.model.PlayerInGame;
import com.scoreboard.app.repository.GroupRepository;
import com.scoreboard.app.repository.PlayerInGameRepository;
import com.scoreboard.app.repository.PlayerRepository;

import java.util.*;

public class GroupService {

    private final PlayerRepository playerRepository;
    private final GroupRepository groupRepository;
    private final PlayerInGameRepository pigRepository;

    public GroupService(PlayerRepository playerRepository, GroupRepository groupRepository, PlayerInGameRepository pigRepository) {
        this.playerRepository = playerRepository;
        this.groupRepository = groupRepository;
        this.pigRepository = pigRepository;
    }

    public Group createGroup(List<String> names, boolean isTemporary){
        System.out.println("Creating new group");
        List<Player> players = new ArrayList<>();

        for (String playerName : names) {
            players.add(new Player(playerName));
        }

        return new Group(players, isTemporary);
    }

    public void saveGroup(Group group) {
        groupRepository.save(group);  // Generate group ID and save it in player entities

        for (Player player : group.getPlayers()) {
            player.setGroupId(group.getGroupID());
            playerRepository.save(player);
        }
    }

    public List<PlayerInGame> registerPlayersInGame(Group currentGroup, Long gameId, List<Long> orderedPlayerIDs) {
        List<PlayerInGame> playersInGame = new ArrayList<>();

        List<Long> groupPlayerIds = currentGroup.getPlayers().stream()
                .map(Player::getId)
                .toList();

        int order = 1;
        for (Long playerID : orderedPlayerIDs) {
            if (!groupPlayerIds.contains(playerID)) {
                throw new IllegalArgumentException(
                        "Player does not belong to current group. playerID=" + playerID
                );
            }
            // TODO: save&getId then create new object??
            PlayerInGame pig = new PlayerInGame(null, gameId, playerID, order++);
            pigRepository.save(pig);
            playersInGame.add(pig);
        }

        return playersInGame;
    }

    public Group getGroupById(Long groupID){
        return groupRepository.findById(groupID).orElseThrow();
    }

    public List<Group> getAllGroups(){
        return groupRepository.findAll();
    }
}