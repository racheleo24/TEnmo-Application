package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface AccountDao {

    Account createAccount(Account account);
    int transferMoney(Transfer transfer);
    int requestTransferMoney(Transfer transfer);
    Account getAccountByAccountId(int id);
    Account getAccountByUserId(int id);
    List<Account> getAllAccounts();
    boolean updateAccount(Account account);

   /*
    Methods not used:

    boolean deleteAccount(int id);
     */
}
