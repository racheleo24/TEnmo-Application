package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Transfer {
    private int transferId;
    private int initiatorId;
    @Min (value = 1000, message = "UserId's begin at 1001.")
    private int otherId;
    @Positive (message = "Transfer amount must be larger than 0.")
    private BigDecimal amount;
    private String status;

    public Transfer() {
    }

    public Transfer(int transferId, int initiatorId, int otherId, BigDecimal transferAmount, String status) {
        this.transferId = transferId;
        this.initiatorId = initiatorId;
        this.otherId = otherId;
        this.amount = transferAmount;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", initiatorId=" + initiatorId +
                ", otherId=" + otherId +
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
