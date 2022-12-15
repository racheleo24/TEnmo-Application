package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests{
    private static final Transfer TRANSFER_1 = new Transfer(3001, 1001, 1002, new BigDecimal("100.00"), "Approved");
    private static final Transfer TRANSFER_2 = new Transfer(3002, 1002, 1001, new BigDecimal("200.00"), "Approved");
    private static final Transfer TRANSFER_3 = new Transfer(3003, 1001, 1003, new BigDecimal("500.00"), "Approved");
    private static final Transfer TRANSFER_4 = new Transfer(3004, 1003, 1004, new BigDecimal("700.00"), "Approved");

    private JdbcTransferDao sut;
    private Transfer testTransfer;

    @Before
    public void setup(){
        sut = new JdbcTransferDao(new JdbcTemplate(dataSource));
        testTransfer = new Transfer(3005, 1001, 1002, new BigDecimal("300.00"), "Approved");
    }

    @Test
    public void getTransferById_returns_correct_transfer(){
        Transfer retrievedTransfer = sut.getTransferById(3001);
        assertTransfersMatch(retrievedTransfer, TRANSFER_1);
        retrievedTransfer = sut.getTransferById(3002);
        assertTransfersMatch(retrievedTransfer, TRANSFER_2);
    }

    @Test
    public void  getTransferByUserId_returns_list_of_all_transfers_for_user(){
        List<Transfer> retrievedTransfers = sut.getTransfersByUser(1001);
        Assert.assertEquals(3,retrievedTransfers.size());
        assertTransfersMatch(TRANSFER_1, retrievedTransfers.get(0));
        assertTransfersMatch(TRANSFER_2, retrievedTransfers.get(1));
        assertTransfersMatch(TRANSFER_3, retrievedTransfers.get(2));

        retrievedTransfers=sut.getTransfersByUser(1004);
        Assert.assertEquals(1,retrievedTransfers.size());
        assertTransfersMatch(TRANSFER_4, retrievedTransfers.get(0));
    }

    @Test
    public void getAllTransfers_returns_list_of_all_transfers(){
        List<Transfer> retrievedTransfers = sut.getAllTransfers();
        Assert.assertEquals(4,retrievedTransfers.size());
        assertTransfersMatch(TRANSFER_1, retrievedTransfers.get(0));
        assertTransfersMatch(TRANSFER_2, retrievedTransfers.get(1));
        assertTransfersMatch(TRANSFER_3, retrievedTransfers.get(2));
        assertTransfersMatch(TRANSFER_4, retrievedTransfers.get(3));
    }

    @Test
    public void updateTransfer_updates_transfer(){
        Transfer transferToUpdate = new Transfer();
        transferToUpdate.setTransferId(3001);
        transferToUpdate.setMoneySenderId(1002);
        transferToUpdate.setMoneyRecipientId(1003);
        transferToUpdate.setAmount(new BigDecimal("250.00"));
        transferToUpdate.setStatus("Pending");

        sut.updateTransfer(transferToUpdate);
        assertTransfersMatch(transferToUpdate, sut.getTransferById(3001));
    }

    @Test
    public void deleteTransfer_cannot_retrieve(){
        sut.deleteTransfer(3001);
        Assert.assertNull(sut.getTransferById(3001));
    }

    @Test
    public void createTransfer_returns_transfer_with_id_and_expected_values(){
        Transfer createdTransfer = sut.createTransfer(testTransfer);
        int createdId= createdTransfer.getTransferId();
        Assert.assertTrue(createdId>3000);
        assertTransfersMatch(testTransfer,createdTransfer);
    }

    @Test
    public void createTransfer_returns_transfer_with_expected_values_when_retrieved(){
        Transfer createdTransfer = sut.createTransfer(testTransfer);
        int createdId= createdTransfer.getTransferId();
        Transfer retrievedTransfer = sut.getTransferById(createdId);
        assertTransfersMatch(createdTransfer,retrievedTransfer);
    }

    private void assertTransfersMatch(Transfer expectedTransfer, Transfer actualTransfer){
        Assert.assertEquals(expectedTransfer.getTransferId(), actualTransfer.getTransferId());
        Assert.assertEquals(expectedTransfer.getMoneySenderId(), actualTransfer.getMoneySenderId());
        Assert.assertEquals(expectedTransfer.getMoneyRecipientId(), actualTransfer.getMoneyRecipientId());
        Assert.assertEquals(expectedTransfer.getAmount(), actualTransfer.getAmount());
        Assert.assertEquals(expectedTransfer.getStatus(), actualTransfer.getStatus());
    }

}
