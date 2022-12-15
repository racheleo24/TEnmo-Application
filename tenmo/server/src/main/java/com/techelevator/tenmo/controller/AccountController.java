package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Transfer sendMoney(@Valid @RequestBody Transfer transfer, Principal principal) {
        int senderId = userDao.findIdByUsername(principal.getName());
        int otherId = transfer.getMoneyRecipientId();
        transfer.setMoneySenderId(senderId);
        int statusCode = accountDao.transferMoney(transfer);
        if (statusCode == JdbcAccountDao.SUCCESS) {
            transfer.setStatus("Approved");
            return transferDao.createTransfer(transfer);
        } else {
            throw getException(statusCode);
        }
    }

    @PostMapping(path = "/request")
    public Transfer requestTransfer(@RequestBody Transfer transfer, Principal principal){
        int moneyRecipientId = userDao.findIdByUsername(principal.getName());
        transfer.setMoneyRecipientId(moneyRecipientId);
        int moneySenderId = transfer.getMoneySenderId();
        int statusCode = accountDao.requestTransferMoney(transfer);
        if(statusCode==JdbcAccountDao.SUCCESS){
            transfer.setStatus("Pending");
            return transferDao.createTransfer(transfer);
        }else {
            throw getException(statusCode);
        }
    }
    @GetMapping(path = "/myTransfers/pending")
    public List<Transfer> myPendingTransfers(Principal principal){
        int userId = userDao.findIdByUsername(principal.getName());
        return transferDao.getAllMyPendingTransfers(userId);
    }

    @GetMapping(path = "/myTransfers/approve")
    public List<Transfer> myTransfersToApprove(Principal principal){
        int userId = userDao.findIdByUsername(principal.getName());
        return transferDao.getTransfersWaitingForMyApproval(userId);
    }

    @PostMapping(path = "/myTransfers/approve/{id}")
    public Transfer resolvePendingTransfer(@PathVariable int id, @RequestBody boolean approve, Principal principal){
        Transfer transfer = transferDao.getTransferById(id);
        if(userDao.findIdByUsername(principal.getName()) != transfer.getMoneySenderId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot resolve this transfer");
        }
        if(approve) {
            int status = accountDao.transferMoney(transfer);
            if (status == JdbcAccountDao.SUCCESS) {
                transfer.setStatus("Approved");
                transferDao.updateTransfer(transfer);
                return transferDao.getTransferById(id);
            } else {
                throw getException(status);
            }
        }else{
            transfer.setStatus("Rejected");
            transferDao.updateTransfer(transfer);
            return transferDao.getTransferById(id);
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

    @GetMapping (path = "/users")
    public Map<Integer, String> getAllUsers(Principal principal) {
        List<User> allUsers = userDao.findAll();
        Map<Integer, String> userMap = new HashMap<>();
        for (User user : allUsers) {
            if (!principal.getName().equals(user.getUsername())) {
                userMap.put(user.getId(), user.getUsername());
            }
        }
        return userMap;
    }


    public ResponseStatusException getException(int statusCode) {
        if (statusCode == JdbcAccountDao.DATABASE_ERROR) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Database error.");
        }
        if (statusCode == JdbcAccountDao.MISSING_ACCOUNT) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Selected user does not exist.");
        }
        if (statusCode == JdbcAccountDao.OVERDRAFT) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds.");
        }
        if (statusCode == JdbcAccountDao.SAME_ACCOUNT) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to transfer money to own account.");
        }
        if (statusCode == JdbcAccountDao.ZERO_AMOUNT) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer amount must be greater than 0.");
        }
        return new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "This should never happen.");
    }
}
