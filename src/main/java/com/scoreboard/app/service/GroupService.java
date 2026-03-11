package com.scoreboard.app.service;

import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.model.PlayerInGame;
import com.scoreboard.app.repository.GroupRepository;
import com.scoreboard.app.repository.PlayerRepository;

import java.util.*;

public class GroupService {

    private PlayerRepository playerRepository;
    private GroupRepository groupRepository;

    public GroupService(PlayerRepository playerRepository, GroupRepository groupRepository) {
        this.playerRepository = playerRepository;
        this.groupRepository = groupRepository;
    }

    public Group createGroup(List<String> names, boolean isTemporary){
        System.out.println("Creating new group");
        List<Player> players = new ArrayList<>();

        int playerNum = names.size();
        for(int i = 0; i < playerNum; i++){
            String playerName = names.get(i);
            Player p = new Player(playerName);
            p.setId(playerRepository.reserveId());
            players.add(p);
            //playerRepository.save(p);
        }

        return new Group(players, isTemporary);
    }

    public List<PlayerInGame> makePlayerList(Group currentGroup, List<Long> orderedPlayerIDs) {
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

            playersInGame.add(new PlayerInGame(currentGroup.getGroupID(), playerID, order++));
        }

        return playersInGame;
    }

    public List<PlayerInGame> getOrderedPlayers(List<PlayerInGame> playersInGame){
        return playersInGame.stream().sorted(Comparator.comparingInt(PlayerInGame::getTurnOrder)).toList();
    }

    public Group getGroupByID(Long groupID){
        return groupRepository.findById(groupID);
    }

    public List<Group> getAllGroup(){
        return groupRepository.findAll();
    }
}