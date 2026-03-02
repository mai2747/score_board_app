package com.scoreboard.app.repository.memory;

import com.scoreboard.app.model.Player;
import com.scoreboard.app.repository.PlayerRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryPlayerRepository implements PlayerRepository {
    private final AtomicLong seq = new AtomicLong(1);
    private final Map<Long, Player> store = new ConcurrentHashMap<>();

    @Override
    public Player save(Player player) {
        if (player == null) throw new IllegalArgumentException("player is null");

        if (player.getId() == null) {
            player.setId(seq.getAndIncrement()); // AUTOINCREMENT
        }
        store.put(player.getId(), player);
        return player;
    }

    @Override
    public Optional<Player> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}