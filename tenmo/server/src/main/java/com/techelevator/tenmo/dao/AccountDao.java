package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.util.List;

public interface AccountDao {
    //CRUD
    //Create
    Account createAccount(Account account);
    //Read
    Account getAccount(int id);
    List<Account> getAllAccounts();
    //Update
    boolean updateAccount(Account account);
    //Delete
    boolean deleteAccount(int id);
}
