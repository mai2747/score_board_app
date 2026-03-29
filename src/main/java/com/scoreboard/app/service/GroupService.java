package com.scoreboard.app.service;

import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.GroupStatus;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.model.PlayerInGame;
import com.scoreboard.app.repository.GroupRepository;
import com.scoreboard.app.repository.PlayerInGameRepository;
import com.scoreboard.app.repository.PlayerRepository;
import com.scoreboard.app.viewmodel.PlayerTotalScore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GroupService {

    private final PlayerRepository playerRepository;
    private final GroupRepository groupRepository;
    private final PlayerInGameRepository pigRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public GroupService(PlayerRepository playerRepository, GroupRepository groupRepository, PlayerInGameRepository pigRepository) {
        this.playerRepository = playerRepository;
        this.groupRepository = groupRepository;
        this.pigRepository = pigRepository;
    }

    public Group createGroup(List<String> names, boolean isTemporary){
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

    public void updateStatus(Long groupId, GroupStatus status){
        groupRepository.updateStatus(groupId, status);
    }

    public void updateLastPlayedAt(Long groupId){
        String lastPlayedAt = LocalDateTime.now().format(formatter);
        groupRepository.updateLastPlayedAt(groupId, lastPlayedAt);
    }

    public void deleteGroup(Long groupId){
        groupRepository.delete(groupId);
    }

    public Group getGroupById(Long groupID){
        Group group = groupRepository.findById(groupID).orElseThrow();
        List<Player> players = playerRepository.findByGroupId(group.getGroupID());
        group.setPlayers(players);

        return group;
    }

    public String getGroupNameByGroupId(Long groupId){
        return groupRepository.findById(groupId)
                .map(Group::getGroupName)
                .orElse("(Unknown Group)");
    }

    public List<PlayerTotalScore> findPlayerTotalScoreByGameId(Long gameId){
        return pigRepository.findPlayerTotalScoreByGameId(gameId);
    }

    public List<PlayerInGame> findPlayersByGameId(Long gameId){
        return pigRepository.findPlayersByGameId(gameId);
    }

    public Optional<PlayerInGame> findPlayerByPigId(Long pigId) {
        return pigRepository.findById(pigId);
    }

    public List<Group> getAllGroups(){
        List<Group> groups = groupRepository.findAll();

        for (Group group : groups) {
            List<Player> players = playerRepository.findByGroupId(group.getGroupID());
            group.setPlayers(players);
        }

        return groups;
    }
}