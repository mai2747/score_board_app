package com.scoreboard.app.repository.memory;

import com.scoreboard.app.model.Group;
import com.scoreboard.app.repository.GroupRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryGroupRepository implements GroupRepository {
    private final AtomicLong seq = new AtomicLong(1);
    private final Map<Long, Group> store = new ConcurrentHashMap<>();

    @Override
    public Group save(Group group) {
        if (group == null) throw new IllegalArgumentException("group is null");

        if (group.getGroupID() == null) {
            group.setGroupID(seq.getAndIncrement()); // AUTOINCREMENT
        }
        store.put(group.getGroupID(), group);
        return group;
    }

    @Override
    public Optional<Group> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}