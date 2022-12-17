package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    Transfer createTransfer(Transfer transfer);
    Transfer getTransferById(int id);
    List<Transfer> getTransfersByUser(int id);
    List<Transfer> getAllTransfers();
    List<Transfer> getAllMyPendingTransfers(int id);
    List<Transfer> getTransfersWaitingForMyApproval(int id);
    boolean updateTransfer(Transfer transfer);

    /*
    Methods not used:

    boolean deleteTransfer(int id);
     */
}
