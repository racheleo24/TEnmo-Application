package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
        return getAccount(id);
    }

    @Override
    public Account getAccount(int id) {
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
    public List<Account> getAllAccounts() {
        return null;
    }

    @Override
    public boolean updateAccount(Account account) {
        return false;
    }

    @Override
    public boolean deleteAccount(int id) {
        return false;
    }

    private Account mapRowToAccount(SqlRowSet results){
        int accountId = results.getInt("account_id");
        int userId = results.getInt("user_id");
        BigDecimal balance = results.getBigDecimal("balance");
        return new Account(accountId,userId,balance);
    }
}
