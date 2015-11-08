/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.bean;

/**
 *
 * @author nazmul hasan
 */
public class TransactionInfo {
    private int id;
    private String transactionId;
    private String APIKey;
    private long balanceIn;
    private long balanceOut;
    private int transactionTypeId;
    private int transactionStatusId;
    private int createdOn;
    private int modifiedOn;
    public TransactionInfo()
    {
        balanceIn = 0;
        balanceOut = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAPIKey() {
        return APIKey;
    }

    public void setAPIKey(String APIKey) {
        this.APIKey = APIKey;
    }

    public long getBalanceIn() {
        return balanceIn;
    }

    public void setBalanceIn(long balanceIn) {
        this.balanceIn = balanceIn;
    }

    public long getBalanceOut() {
        return balanceOut;
    }

    public void setBalanceOut(long balanceOut) {
        this.balanceOut = balanceOut;
    }

    public int getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(int transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public int getTransactionStatusId() {
        return transactionStatusId;
    }

    public void setTransactionStatusId(int transactionStatusId) {
        this.transactionStatusId = transactionStatusId;
    }

    public int getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(int createdOn) {
        this.createdOn = createdOn;
    }

    public int getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(int modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
    
}