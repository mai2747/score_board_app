package com.scoreboard.app.repository;

import com.scoreboard.app.model.Group;
import java.util.Optional;

public interface GroupRepository {
    Group save(Group group);                    // insert or update
    Optional<Group> findById(Long id);
}