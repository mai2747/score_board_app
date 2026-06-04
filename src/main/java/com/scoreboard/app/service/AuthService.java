package com.scoreboard.app.service;

import com.scoreboard.app.model.Account;
import com.scoreboard.app.model.SecQuestion;
import com.scoreboard.app.repository.AccountRepository;
import com.scoreboard.app.repository.AccountScopedRepository;
import com.scoreboard.app.repository.SecurityQuestionRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AuthService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthService.class);

    AccountRepository accountRepository;
    SecurityQuestionRepository securityQuestionRepository;
    private final List<AccountScopedRepository> scopedRepositories;
    private static final int BCRYPT_COST_FACTOR = 12;

    Account loggedinAccount;
    Account pendingAccount;
    private Boolean guestMode;


    public AuthService(AccountRepository accountRepository, SecurityQuestionRepository securityQuestionRepository, List<AccountScopedRepository> scopedRepositories){
        this.accountRepository = accountRepository;
        this.securityQuestionRepository = securityQuestionRepository;
        this.scopedRepositories = scopedRepositories;
    }

    public Account createGuestAccount(){
        return new Account("TempAccount");
        // save to DB or just in memory?
    }

    public void createAccount(Account newAccount){
        accountRepository.save(newAccount);

        // once account has been saved

    }

    public void updatePassword(String newPassword){
        String newPass = hashPassword(newPassword);
        Account account = (pendingAccount == null) ? loggedinAccount : pendingAccount;
        accountRepository.updatePassword(account.getAccountId(), newPass);
    }

    public Account requireAccount() {
        if (loggedinAccount == null) {
            throw new IllegalStateException("No logged-in account");
        }
        return loggedinAccount;
    }

    public Long requireAccountId() {
        return requireAccount().getAccountId();
    }

    public void completePendingLogin() {
        if (pendingAccount == null) {
            throw new IllegalStateException("No pending account to complete login");
        }
        login(pendingAccount);
        clearPendingAccount();
    }

    public void setPendingAccount(Account account){
        pendingAccount = account;
    }
    public Account getPendingAccount(){ return pendingAccount; }
    public void clearPendingAccount() {
        this.pendingAccount = null;
    }

    public void login(Account account) {
        this.loggedinAccount = account;

        Long currentAccountId = requireAccountId();
        setCurrentAccountIdForRepositories(currentAccountId);

        logger.info("--Logged in as {}--", account.getName());
    }

    public void logout() {
        logger.info("--{} has logged out--", requireAccount().getName());
        setCurrentAccountIdForRepositories(null);
        this.loggedinAccount = null;
    }

    private void setCurrentAccountIdForRepositories(Long accountId) {
        scopedRepositories.forEach(r -> r.setCurrentAccountId(accountId));
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

    public boolean checkSecretAns(String inputAns){
        Account account = (pendingAccount == null) ? loggedinAccount : pendingAccount;
        String secAns = accountRepository.findSecurityAnsById(account.getAccountId());

        return verifySecurityAnswer(inputAns, secAns);
    }

    public String getCurrentAccountName(){
        Account account = (pendingAccount == null) ? loggedinAccount : pendingAccount;
        return account.getName();
    }

    public String getAccountNameById(Long accountId){
        return accountRepository.getAccountName(accountId);
    }

    public List<SecQuestion> getSecurityQuestions() {
        return securityQuestionRepository.findAll();
    }

    public List<Account> getAllAccount(){
        return accountRepository.findAll();
    }

    public void setGuestMode(boolean isGuest){ this.guestMode = isGuest; }
    public boolean isGuestMode(){ return guestMode;}
}
