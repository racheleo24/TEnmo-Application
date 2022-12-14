package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping (path = "/accounts")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TransferDao transferDao;

    @GetMapping (path = "/myaccount")
    public Account seeMyAccount(Principal principal){
        int id = userDao.findIdByUsername(principal.getName());
        Account account = accountDao.getAccountByUserId(id);
        return account;
    }

    @GetMapping (path = "/myaccount/balance")
    public BigDecimal seeMyBalance(Principal principal){
        int id = userDao.findIdByUsername(principal.getName());
        Account account = accountDao.getAccountByUserId(id);
        return account.getBalance();
    }

    @PostMapping (path = "/transfer/{username}")
    public Transfer sendMoney(@PathVariable String username, @RequestBody Transfer transfer, Principal principal) {
        // get account ids
        int senderId = userDao.findIdByUsername(principal.getName());
        int otherId = userDao.findIdByUsername(username);
        transfer.setInitiatorId(senderId);
        transfer.setOtherId(otherId);

        // create account objects from ids
        // update the accounts by the transfer amount using setters
        // call the update account method with new balances to update in DB
        // simultaneously create transfer

        BigDecimal transferAmount = transfer.getTransferAmount();
    }
}
