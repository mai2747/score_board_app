package com.scoreboard.app.repository;

import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.GroupStatus;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    Group save(Group group);                    // insert or update
    void delete(Long groupId);
    Optional<Group> findById(Long groupId);
    List<Group> findAll();

    String getGroupNameByGameId(Long gameId);

    void rename(Long groupId, String name);
    void updateStatus(Long groupId, GroupStatus status);
    void updateLastPlayedAt(Long groupId, String lastPlayedAt);
}