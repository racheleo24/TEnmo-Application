package com.techelevator.tenmo.model;

public class ApprovalResponse {
    private int transferId;
    private boolean isApproved;

    public ApprovalResponse(int transferId, boolean isApproved) {
        this.transferId = transferId;
        this.isApproved = isApproved;
    }

    public ApprovalResponse() {
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
        return "ApprovalResponse{" +
                "transferId=" + transferId +
                ", isApproved=" + isApproved +
                '}';
    }
}
