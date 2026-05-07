package com.scoreboard.app.service;

import com.scoreboard.app.model.Account;
import com.scoreboard.app.repository.AccountRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class AuthService {
    AccountRepository accountRepository;
    private static final int BCRYPT_COST_FACTOR = 12;

    public AuthService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public Account createGuestAccount(){
        return new Account("TempAccount");
        // save to DB or just in memory?
    }

    public void createAccount(Account newAccount){
        accountRepository.save(newAccount);
    }

    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_COST_FACTOR));
    }

    public boolean verifyPassword(Long accountId, String plainPassword) {
        if (accountId == null || plainPassword == null) {
            return false;
        }
        try {
            String hashedPassword = accountRepository.getHashedPassword(accountId);
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getAccountName(Long accountId){
        return accountRepository.getAccountName(accountId);
    }

    public String hashSecurityAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Security answer cannot be null or empty");
        }
        String normalized = answer.trim().toLowerCase();
        return BCrypt.hashpw(normalized, BCrypt.gensalt(BCRYPT_COST_FACTOR));
    }

    public boolean verifySecurityAnswer(String inputAnswer, String hashedAnswer) {
        if (inputAnswer == null || hashedAnswer == null) {
            return false;
        }
        String normalized = inputAnswer.trim().toLowerCase();
        try {
            return BCrypt.checkpw(normalized, hashedAnswer);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<Account> getAllAccount(){
        return accountRepository.findAll();
    }
}
