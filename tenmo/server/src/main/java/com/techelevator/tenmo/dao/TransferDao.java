package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    //CRUD
    //Create
    Transfer createTransfer(Transfer transfer);
    //Read
    Transfer getTransferById(int id);
    List<Transfer> getTransfersByUser(int id);
    List<Transfer> getAllTransfers();
    //Update
    boolean updateTransfer(Transfer transfer);
    //Delete
    boolean deleteTransfer(int id);
}
