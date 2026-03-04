package com.scoreboard.app.repository.memory;

import com.scoreboard.app.model.Game;
import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.repository.GameRepository;
import com.scoreboard.app.repository.GroupRepository;
import com.scoreboard.app.repository.PlayerRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryGameRepository implements GameRepository {
    private final AtomicLong seq = new AtomicLong(1);
    private final Map<Long, Game> store = new ConcurrentHashMap<>();

    private final PlayerRepository playerRepository;
    private final GroupRepository groupRepository;

    public InMemoryGameRepository(PlayerRepository playerRepository, GroupRepository groupRepository) {
        this.playerRepository = playerRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public Long reserveId(){
        return seq.getAndIncrement();
    }

    @Override
    public Game save(Game game) {
        if (game == null) throw new IllegalArgumentException("game is null");

        Group group = game.getGroup();
        if (group == null) throw new IllegalArgumentException("game.group is null");

        // Insert/update players in advance to fill their IDs
        if (group.getPlayers() != null) {
            for (Player p : group.getPlayers()) {
                if (p == null) continue;
                playerRepository.save(p);
            }
        }

        // Insert/update group to fill ID
        if (group.getGroupID() == null) {
            groupRepository.save(group);
        } else {
            groupRepository.save(group);
        }

        // Insert/update game
        if (game.getGameID() == null) {
            game.setGameID(seq.getAndIncrement());
        }
        store.put(game.getGameID(), game);
        return game;
    }

    // Debug method
    public int getStoredDataNum(){
        return store.size();
    }

    @Override
    public Optional<Game> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}