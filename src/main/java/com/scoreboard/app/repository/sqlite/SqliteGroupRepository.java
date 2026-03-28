package com.scoreboard.app.repository.sqlite;

import com.scoreboard.app.model.Group;
import com.scoreboard.app.repository.GroupRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteGroupRepository implements GroupRepository {
    private final Connection conn;

    public SqliteGroupRepository(Connection conn){
        this.conn = conn;
    }

    @Override
    public Group save(Group group) {
        if(group == null){
            throw new IllegalArgumentException("group is null");
        }

        if(group.getGroupID() == null){
            long generatedId = insert(group);
            group.setGroupID(generatedId);
        }else{
            update(group);
        }

        return group;
    }

    private long insert(Group group){
        String sql = "INSERT INTO groups (name, is_temporary, created_at) VALUES (?, ?, ?)";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, group.getGroupName());
            stmt.setInt(2, group.isTemporary() ? 1 : 0);
            stmt.setString(3, group.getCreatedTime());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new RuntimeException("Failed to get generated id");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Insert group failed", e);
        }
    }

    private void update(Group group){
        String sql = "UPDATE groups SET name = ? WHERE group_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, group.getGroupName());
            stmt.setLong(2, group.getGroupID());

            stmt.executeUpdate();

        }catch(SQLException e){
            throw new RuntimeException("Update group failed", e);
        }
    }

    @Override
    public Optional<Group> findById(Long id) {
        String sql = "SELECT group_id, name, is_temporary, created_at FROM groups WHERE group_id = ?";

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);


            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(new Group(
                            rs.getLong("group_id"),
                            rs.getString("name"),
                            rs.getBoolean("is_temporary"),
                            rs.getString("created_at")
                    ));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find group by id failed", e);
        }
    }

    @Override
    public List<Group> findAll() {
        String sql = "SELECT group_id, name, is_temporary, created_at FROM groups";

        List<Group> groups = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Group group = new Group(
                        rs.getLong("group_id"),
                        rs.getString("name"),
                        rs.getBoolean("is_temporary"),
                        rs.getString("created_at")
                );

                groups.add(group);
            }

            return groups;

        } catch (SQLException e) {
            throw new RuntimeException("Find all groups failed", e);
        }
    }
}
