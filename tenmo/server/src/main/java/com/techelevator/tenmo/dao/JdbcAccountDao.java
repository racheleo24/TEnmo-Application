package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{
    JdbcTemplate jdbcTemplate;

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
                     "FROM account";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()){
            accounts.add(mapRowToAccount(results));
        }
        return accounts;
    }

    @Override
    public boolean updateAccount(Account account) {
        String sql = "UPDATE account " +
                     "SET user_id = ?, balance = ? " +
                     "WHERE account_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, account.getUserId(), account.getBalance(), account.getAccountId());
        return rowsUpdated == 1;
    }

    @Override
    public boolean deleteAccount(int id) {
        String sql = "DELETE FROM account " +
                     "WHERE account_id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);
        return rowsDeleted == 1;
    }

    private Account mapRowToAccount(SqlRowSet results){
        int accountId = results.getInt("account_id");
        int userId = results.getInt("user_id");
        BigDecimal balance = results.getBigDecimal("balance");
        return new Account(accountId,userId,balance);
    }
}
