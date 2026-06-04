package com.scoreboard.app.repository.sqlite;

import com.scoreboard.app.model.Account;
import com.scoreboard.app.repository.AccountRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqliteAccountRepository implements AccountRepository {
    private final Connection conn;

    public SqliteAccountRepository(Connection conn) {
        this.conn = conn;
    }

    public Account save(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }

        if (account.getAccountId() == null) {
            long generatedId = insert(account);
            account.setAccountId(generatedId);
        } else {
            update(account);
        }

        return account;
    }

    @Override
    public void updatePassword(Long accountId, String newPass) {
        String sql = "UPDATE accounts SET password_hash = ? WHERE account_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPass);
            stmt.setLong(2, accountId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Update player failed", e);
        }
    }

    private Long insert(Account account) {
        String sql = "INSERT INTO accounts (account_name, password_hash, security_question_id, security_answer_hash, status, created_at, last_activity_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, account.getName());
            stmt.setString(2, account.getPassword());
            stmt.setInt(3, account.getSecretQuestion());
            stmt.setString(4, account.getSecretAnswer());
            stmt.setString(5, account.getStatus().name());
            stmt.setString(6, account.getCreatedAt());
            stmt.setString(7, account.getCreatedAt());  // last activity time is the same as the date of creation
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new RuntimeException("Failed to get generated id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Insert account failed", e);
        }
    }

    private Account update(Account account) {
        String sql = "UPDATE accounts SET account_name = ? WHERE account_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, account.getName());
            stmt.setLong(2, account.getAccountId());

            stmt.executeUpdate();
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Update account failed", e);
        }
    }

    @Override
    public void deleteAccount(Long accountId) {
        String sql = "DELETE FROM accounts WHERE account_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Delete account failed", e);
        }
    }

    // TODO: better to complete account info?
    @Override
    public Account findByAccountId(Long accountId) {
        String sql = "SELECT account_id, account_name FROM accounts WHERE account_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return toAccount(rs);
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Find account by id failed", e);
        }
    }

    @Override
    public String getHashedPassword(Long accountId) {
        String sql = "SELECT password_hash FROM accounts WHERE account_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, accountId);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getString(1);
                }
            }
            return null;
        }catch(SQLException e){
            throw new RuntimeException("Find password failed", e);
        }
    }

    @Override
    public String findSecurityAnsById(Long accountId) {
        String sql = "SELECT security_answer_hash FROM accounts WHERE account_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, accountId);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getString(1);
                }
            }
            return null;
        }catch(SQLException e){
            throw new RuntimeException("Find security answer failed", e);
        }
    }

    @Override
    public String getAccountName(Long accountId) {
        String sql = "SELECT account_name FROM accounts WHERE account_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, accountId);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getString(1);
                }
            }
            return null;
        }catch(SQLException e){
            throw new RuntimeException("Find password failed", e);
        }
    }

    @Override
    public List<Account> findAll() {
        String sql = "SELECT account_id, account_name FROM accounts";

        List<Account> accounts = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                accounts.add(toAccount(rs));
            }

            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException("Find all accounts failed", e);
        }
    }

    private Account toAccount(ResultSet rs) throws SQLException {
        Account account = new Account(rs.getString("account_name"));
        account.setAccountId(rs.getLong("account_id"));
        return account;
    }
}
