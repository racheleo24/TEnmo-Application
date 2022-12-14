package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping (path = "/accounts")
public class AccountController {

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserDao userDao;

    @GetMapping (path = "/myaccount")
    @PreAuthorize("isAuthenticated()")
    public Account seeMyAccount(Principal principal){
        int id = userDao.findIdByUsername(principal.getName());
        Account account = accountDao.getAccountByUserId(id);
        return account;
    }

    @GetMapping (path = "/myaccount/balance")
    @PreAuthorize("isAuthenticated()")
    public BigDecimal seeMyBalance(Principal principal){
        int id = userDao.findIdByUsername(principal.getName());
        Account account = accountDao.getAccountByUserId(id);
        return account.getBalance();
    }

}
