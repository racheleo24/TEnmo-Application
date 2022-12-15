package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class Account {
    private int accountId;
    @Min (value = 1000, message = "UserId's begin at 1001.")
    private int userId;
    @PositiveOrZero (message = "Balance cannot fall below 0.")
    private BigDecimal balance;

    public Account(int accountId, int userId, BigDecimal balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    public Account() {
    }

    public void subtractAmount(BigDecimal amount){
        this.balance=balance.subtract(amount);
    }
    public void addAmount(BigDecimal amount){
        this.balance=balance.add(amount);
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", balance=" + balance +
                '}';
    }
}
