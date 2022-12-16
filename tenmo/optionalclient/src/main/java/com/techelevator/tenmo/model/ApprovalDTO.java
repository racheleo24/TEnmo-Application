package com.techelevator.tenmo.model;

public class ApprovalDTO {
    private int transferId;
    private boolean isApproved;

    public ApprovalDTO(int id, boolean isApproved) {
        this.transferId = id;
        this.isApproved = isApproved;
    }

    public ApprovalDTO() {
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean approved) {
        isApproved = approved;
    }

    @Override
    public String toString() {
        return "ApprovalDTO{" +
                "id=" + transferId +
                ", isApproved=" + isApproved +
                '}';
    }
}
