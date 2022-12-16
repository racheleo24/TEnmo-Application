package com.techelevator.dao;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcAccountDaoTests extends BaseDaoTests{
    private static final Account ACCOUNT_1 = new Account(2001, 1001, new BigDecimal("1000.00"));
    private static final Account ACCOUNT_2 = new Account(2002, 1002, new BigDecimal("1000.00"));
    private static final Account ACCOUNT_3 = new Account(2003, 1003, new BigDecimal("1000.00"));
    private static final Account ACCOUNT_4 = new Account(2004, 1004, new BigDecimal("1000.00"));

    private JdbcAccountDao sut;
    private Account testAccount;

    @Before
    public void setup(){
        sut = new JdbcAccountDao(new JdbcTemplate(dataSource));
        testAccount = new Account(2005, 1001, new BigDecimal("1000.00"));
    }

    @Test
    public void getAccountByAccountId_returns_correct_account(){
        Account returnedAccount = sut.getAccountByAccountId(2001);
        assertAccountsMatch(ACCOUNT_1,returnedAccount);

        returnedAccount=sut.getAccountByAccountId(2002);
        assertAccountsMatch(ACCOUNT_2, returnedAccount);
    }

    @Test
    public void getAccountByUserId_returns_correct_account(){
        Account returnedAccount = sut.getAccountByUserId(1003);
        assertAccountsMatch(ACCOUNT_3, returnedAccount);

        returnedAccount = sut.getAccountByUserId(1004);
        assertAccountsMatch(ACCOUNT_4, returnedAccount);
    }

    @Test
    public void getAllAccounts_returns_correct_number_and_accounts(){
        List<Account> allAccounts = sut.getAllAccounts();
        Assert.assertEquals(4, allAccounts.size());
        assertAccountsMatch(ACCOUNT_1,allAccounts.get(0));
        assertAccountsMatch(ACCOUNT_2,allAccounts.get(1));
        assertAccountsMatch(ACCOUNT_3,allAccounts.get(2));
        assertAccountsMatch(ACCOUNT_4,allAccounts.get(3));
    }


    @Test
    public void createAccount_returns_account_with_id_and_expected_values(){
        Account createdAccount = sut.createAccount(testAccount);
        int createdId = createdAccount.getAccountId();
        testAccount.setAccountId(createdId);
        Assert.assertTrue(createdId>2000);
        assertAccountsMatch(testAccount,createdAccount);
    }

    @Test
    public void createAccount_returns_account_with_expected_values_when_retrieved(){
        Account createdAccount = sut.createAccount(testAccount);
        int createdId= createdAccount.getAccountId();
        Account retrievedAccount = sut.getAccountByAccountId(createdId);
        assertAccountsMatch(createdAccount,retrievedAccount);
    }

    @Test
    public void updateAccount_updates_account_and_retrieved_account_is_updated(){
        Account accountToUpdate = new Account();
        accountToUpdate.setAccountId(2001);
        accountToUpdate.setUserId(1004);
        accountToUpdate.setBalance(new BigDecimal("2000.00"));
        sut.updateAccount(accountToUpdate);
        assertAccountsMatch(accountToUpdate, sut.getAccountByAccountId(2001));
    }

    @Test
    public void deleteAccount_cannot_be_retrieved(){
        sut.deleteAccount(2001);
        Assert.assertNull(sut.getAccountByAccountId(2001));

        sut.deleteAccount(2002);
        Assert.assertNull(sut.getAccountByAccountId(2002));
    }

    @Test
    public void transferMoney_updates_both_account_balances(){
        Transfer transfer = new Transfer(3001, 1001, 1002, new BigDecimal("100.00"), "Approved");
        int statusCode = sut.transferMoney(transfer);
        Assert.assertEquals(JdbcAccountDao.SUCCESS, statusCode);
        Account account = sut.getAccountByUserId(1001);
        Assert.assertEquals(new BigDecimal("900.00"), account.getBalance());
        account = sut.getAccountByUserId(1002);
        Assert.assertEquals(new BigDecimal("1100.00"), account.getBalance());
    }

    @Test
    public void transferMoney_send_all_updates_both_account_balances(){
        Transfer transfer = new Transfer(3001, 1001, 1002, new BigDecimal("1000.00"), "Approved");
        int statusCode = sut.transferMoney(transfer);
        Assert.assertEquals(JdbcAccountDao.SUCCESS, statusCode);
        Account account = sut.getAccountByUserId(1001);
        Assert.assertEquals(new BigDecimal("0.00"), account.getBalance());
        account = sut.getAccountByUserId(1002);
        Assert.assertEquals(new BigDecimal("2000.00"), account.getBalance());
    }

    @Test
    public void transferMoney_same_account_does_not_update_balance_throws_error_code(){
        Transfer transfer = new Transfer(3001, 1001, 1001, new BigDecimal("100.00"), "Approved");
        int statusCode = sut.transferMoney(transfer);
        Assert.assertEquals(JdbcAccountDao.SAME_ACCOUNT, statusCode);
        Account account = sut.getAccountByUserId(1001);
        Assert.assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }

    @Test
    public void transferMoney_overdraft_does_not_update_both_account_balances_throws_error_code(){
        Transfer transfer = new Transfer(3001, 1001, 1002, new BigDecimal("1000.01"), "Approved");
        int statusCode = sut.transferMoney(transfer);
        Assert.assertEquals(JdbcAccountDao.OVERDRAFT, statusCode);
        Account account = sut.getAccountByUserId(1001);
        Assert.assertEquals(new BigDecimal("1000.00"), account.getBalance());
        account = sut.getAccountByUserId(1002);
        Assert.assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }

    @Test
    public void transferMoney_send_zero_does_not_update_both_account_balances_throws_error_code(){
        Transfer transfer = new Transfer(3001, 1001, 1002, new BigDecimal("0.00"), "Approved");
        int statusCode = sut.transferMoney(transfer);
        Assert.assertEquals(JdbcAccountDao.ZERO_AMOUNT, statusCode);
        Account account = sut.getAccountByUserId(1001);
        Assert.assertEquals(new BigDecimal("1000.00"), account.getBalance());
        account = sut.getAccountByUserId(1002);
        Assert.assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }

    @Test
    public void transferMoney_account_not_found_does_not_update_account_balance_throws_error_code(){
        Transfer transfer = new Transfer(3001, 1001, 1010, new BigDecimal("100.00"), "Approved");
        int statusCode = sut.transferMoney(transfer);
        Assert.assertEquals(JdbcAccountDao.MISSING_ACCOUNT, statusCode);
        Account account = sut.getAccountByUserId(1001);
        Assert.assertEquals(new BigDecimal("1000.00"), account.getBalance());
        account = sut.getAccountByUserId(1010);
        Assert.assertNull(account);
    }

    private void assertAccountsMatch(Account expected, Account actual){
        Assert.assertEquals(expected.getAccountId(),actual.getAccountId());
        Assert.assertEquals(expected.getUserId(), actual.getUserId());
        Assert.assertEquals(expected.getBalance(), actual.getBalance());
    }
}
