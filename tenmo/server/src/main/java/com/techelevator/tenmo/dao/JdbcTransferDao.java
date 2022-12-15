package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfer (initiator_user_id, other_user_id, transfer_amount, status) " +
                "VALUES (?,?,?,?) RETURNING transfer_id";
        int id = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getMoneySenderId(),
                transfer.getMoneyRecipientId(), transfer.getAmount(), transfer.getStatus());
        return getTransferById(id);
    }

    @Override
    public Transfer getTransferById(int id) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, initiator_user_id, other_user_id, transfer_amount, status " +
                "FROM transfer " +
                "WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            transfer=mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getTransfersByUser(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, initiator_user_id, other_user_id, transfer_amount, status " +
                "FROM transfer " +
                "WHERE initiator_user_id = ? OR other_user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);
        while(results.next()){
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getAllTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, initiator_user_id, other_user_id, transfer_amount, status " +
                "FROM transfer ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()){
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getAllMyPendingTransfers(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, initiator_user_id, other_user_id, transfer_amount, status " +
                "FROM transfer " +
                "WHERE status = 'Pending' AND (initiator_user_id = ? OR other_user_id = ?)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id, id);
        while(results.next()){
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getTransfersWaitingForMyApproval(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, initiator_user_id, other_user_id, transfer_amount, status " +
                "FROM transfer " +
                "WHERE status = 'Pending' AND initiator_user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id);
        while(results.next()){
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    @Override
    public boolean updateTransfer(Transfer transfer) {
        String sql = "UPDATE transfer SET initiator_user_id=?, other_user_id=?, transfer_amount=?, status=? " +
                "WHERE transfer_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, transfer.getMoneySenderId(), transfer.getMoneyRecipientId(),
                transfer.getAmount(), transfer.getStatus(), transfer.getTransferId());
        return rowsUpdated==1;
    }

    @Override
    public boolean deleteTransfer(int id) {
        String sql = "DELETE FROM transfer " +
                "WHERE transfer_id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);
        return rowsDeleted==1;
    }

    private Transfer mapRowToTransfer(SqlRowSet results){
        int id= results.getInt("transfer_id");
        int user1= results.getInt("initiator_user_id");
        int user2= results.getInt("other_user_id");
        BigDecimal transferAmount = results.getBigDecimal("transfer_amount");
        String status = results.getString("status");
        return new Transfer(id,user1,user2,transferAmount,status);
    }
}
