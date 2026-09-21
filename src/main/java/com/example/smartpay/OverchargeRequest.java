package com.example.smartpay;

public class OverchargeRequest {
    private double amount;
    private String method;
    private double feeCharged;

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public double getFeeCharged() { return feeCharged; }
    public void setFeeCharged(double feeCharged) { this.feeCharged = feeCharged; }
}