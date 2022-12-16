package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private int transferId;
    private int moneySenderId;
    private int moneyRecipientId;
    private BigDecimal amount;
    private String status;

    public Transfer() {
    }

    public Transfer(int transferId, int initiatorId, int otherId, BigDecimal transferAmount, String status) {
        this.transferId = transferId;
        this.moneySenderId = initiatorId;
        this.moneyRecipientId = otherId;
        this.amount = transferAmount;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", initiatorId=" + moneySenderId +
                ", otherId=" + moneyRecipientId +
                ", transferAmount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getMoneySenderId() {
        return moneySenderId;
    }

    public void setMoneySenderId(int moneySenderId) {
        this.moneySenderId = moneySenderId;
    }

    public int getMoneyRecipientId() {
        return moneyRecipientId;
    }

    public void setMoneyRecipientId(int moneyRecipientId) {
        this.moneyRecipientId = moneyRecipientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
