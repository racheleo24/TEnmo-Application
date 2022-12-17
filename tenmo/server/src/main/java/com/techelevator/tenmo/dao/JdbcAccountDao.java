package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{
    JdbcTemplate jdbcTemplate;
    // Error messages
    public static final int SUCCESS = 0;
    public static final int OVERDRAFT = 1;
    public static final int SAME_ACCOUNT = 2;
    public static final int ZERO_AMOUNT = 3;
    public static final int MISSING_ACCOUNT = 4;
    public static final int DATABASE_ERROR = 5;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Account createAccount(Account account) {
        String sql = "INSERT INTO account (user_id, balance) " +
                "VALUES (?,?) RETURNING account_id";
        int id = jdbcTemplate.queryForObject(sql, Integer.class, account.getUserId(), account.getBalance());
        return getAccountByAccountId(id);
    }

    @Override
    public Account getAccountByAccountId(int id) {
        Account account=null;
        String sql = "SELECT account_id, user_id, balance " +
                "FROM account " +
                "WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            account=mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public Account getAccountByUserId(int id) {
        Account account=null;
        String sql = "SELECT account_id, user_id, balance " +
                "FROM account " +
                "WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            account=mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance " +
                     "FROM account " +
                     "ORDER BY account_id ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()){
            accounts.add(mapRowToAccount(results));
        }
        return accounts;
    }

    @Override
    public int requestTransferMoney(Transfer transfer){
        BigDecimal transferAmount = transfer.getAmount();
        int moneySenderId = transfer.getMoneySenderId();
        int moneyRecipientId = transfer.getMoneyRecipientId();

        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return JdbcAccountDao.ZERO_AMOUNT;
        }
        if (moneySenderId == moneyRecipientId) {
            return JdbcAccountDao.SAME_ACCOUNT;
        }
        if (getAccountByUserId(moneySenderId) == null) {
            return JdbcAccountDao.MISSING_ACCOUNT;
        }
        return JdbcAccountDao.SUCCESS;

    }
    @Override
    public int transferMoney(Transfer transfer) {
        // pulling out values
        BigDecimal transferAmount = transfer.getAmount();
        int originId = transfer.getMoneySenderId();
        int destinationId = transfer.getMoneyRecipientId();
        Account originAccount = getAccountByUserId(originId);
        Account destinationAccount = getAccountByUserId(destinationId);

        // exception handling
        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return JdbcAccountDao.ZERO_AMOUNT;
        }
        if (transferAmount.compareTo(originAccount.getBalance()) > 0) {
            return JdbcAccountDao.OVERDRAFT;
        }
        if (originId == destinationId) {
            return JdbcAccountDao.SAME_ACCOUNT;
        }
        if (getAccountByUserId(destinationId) == null) {
            return JdbcAccountDao.MISSING_ACCOUNT;
        }

        // updating account balances on Java end
        originAccount.subtractAmount(transferAmount);
        destinationAccount.addAmount(transferAmount);

        // sending transaction to DB
        jdbcTemplate.execute("START TRANSACTION");
        try {
            updateAccount(originAccount);
            updateAccount(destinationAccount);
            jdbcTemplate.execute("COMMIT");
            return JdbcAccountDao.SUCCESS;
        } catch (Exception e){
            jdbcTemplate.execute("ROLLBACK");
            return JdbcAccountDao.DATABASE_ERROR;
        }
    }

    @Override
    public boolean updateAccount(Account account) {
        String sql = "UPDATE account " +
                     "SET user_id = ?, balance = ? " +
                     "WHERE account_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, account.getUserId(), account.getBalance(), account.getAccountId());
        return rowsUpdated == 1;
    }


    private Account mapRowToAccount(SqlRowSet results){
        int accountId = results.getInt("account_id");
        int userId = results.getInt("user_id");
        BigDecimal balance = results.getBigDecimal("balance");
        return new Account(accountId,userId,balance);
    }

    /*
    Methods not used:
    @Override
    public boolean deleteAccount(int id) {
        String sql = "DELETE FROM account " +
                     "WHERE account_id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);
        return rowsDeleted == 1;
    }

     */
}
