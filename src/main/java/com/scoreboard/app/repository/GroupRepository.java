package com.scoreboard.app.repository;

import com.scoreboard.app.model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    Group save(Group group);                    // insert or update
    Group findById(Long id);
    List<Group> findAll();
}