package com.scoreboard.app.repository.memory;

import com.scoreboard.app.model.Group;
import com.scoreboard.app.repository.GroupRepository;
import com.scoreboard.app.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryGroupRepository implements GroupRepository {
    private final AtomicLong seq = new AtomicLong(1);
    private final Map<Long, Group> store = new ConcurrentHashMap<>();

    private final PlayerRepository playerRepository;

    public InMemoryGroupRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Group save(Group group) {
        if (group == null) throw new IllegalArgumentException("Group is null");

        if (group.getGroupID() == null) {
            group.setGroupID(seq.getAndIncrement()); // AUTOINCREMENT
        }
        store.put(group.getGroupID(), group);
        return group;
    }

    @Override
    public Group findById(Long id) {
        Group group = store.get(id);
        if (group == null) {
            throw new IllegalArgumentException("Group not found: " + id);
        }
        return group;
    }

    public List<Group> findAll(){
        return new ArrayList<>(store.values());
    }
}