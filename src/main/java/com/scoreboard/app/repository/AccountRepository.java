package com.scoreboard.app.repository;

import com.scoreboard.app.model.Account;

import java.util.List;

public interface AccountRepository {
    Account save(Account account);
    void updatePassword(Long accountId,String newPass);
    void deleteAccount(Long accountId);
    Account findByAccountId(Long accountId);
    String getHashedPassword(Long accountId);
    String findSecurityAnsById(Long accountId);
    String getAccountName(Long accountId);
    List<Account> findAll();
}
