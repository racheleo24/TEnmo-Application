package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface AccountDao {
    //CRUD
    //Create
    Account createAccount(Account account);
    //Read
    Account getAccountByAccountId(int id);
    Account getAccountByUserId(int id);
    List<Account> getAllAccounts();
    //Update
    int transferMoney(Transfer transfer);
    boolean updateAccount(Account account);
    //Delete
    boolean deleteAccount(int id);
}
