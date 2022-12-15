package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TransferDao transferDao;

    /*
    @GetMapping (path = "/myaccount")
    public Account seeMyAccount(Principal principal){
        int id = userDao.findIdByUsername(principal.getName());
        Account account = accountDao.getAccountByUserId(id);
        return account;
    }
    */

    @GetMapping (path = "/balance")
    public BigDecimal seeMyBalance(Principal principal){
        int id = userDao.findIdByUsername(principal.getName());
        Account account = accountDao.getAccountByUserId(id);
        return account.getBalance();
    }

    @PostMapping (path = "/transfer")
    public Transfer sendMoney(@RequestBody Transfer transfer, Principal principal) {
        int senderId = userDao.findIdByUsername(principal.getName());
        int otherId = transfer.getOtherId();
        transfer.setInitiatorId(senderId);
        transfer.setOtherId(otherId);
        if (accountDao.transferMoney(transfer)) {
            transfer.setStatus("Approved");
            return transferDao.createTransfer(transfer);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping (path = "/myTransfers")
    public List<Transfer> myTransfers(Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        return transferDao.getTransfersByUser(userId);
    }

    @GetMapping (path = "/transfer/{id}")
    public Transfer getTransfer(@PathVariable int id) {
        Transfer transfer = transferDao.getTransferById(id);
        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            return transfer;
        }
    }
}
