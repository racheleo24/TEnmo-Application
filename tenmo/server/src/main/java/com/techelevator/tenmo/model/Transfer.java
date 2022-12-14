package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private int transferId;
    private int initiatorId;
    private int otherId;
    private BigDecimal transferAmount;
    private String status;

    public Transfer() {
    }

    public Transfer(int transferId, int initiatorId, int otherId, BigDecimal transferAmount, String status) {
        this.transferId = transferId;
        this.initiatorId = initiatorId;
        this.otherId = otherId;
        this.transferAmount = transferAmount;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", initiatorId=" + initiatorId +
                ", otherId=" + otherId +
                ", transferAmount=" + transferAmount +
                ", status='" + status + '\'' +
                '}';
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(int initiatorId) {
        this.initiatorId = initiatorId;
    }

    public int getOtherId() {
        return otherId;
    }

    public void setOtherId(int otherId) {
        this.otherId = otherId;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
