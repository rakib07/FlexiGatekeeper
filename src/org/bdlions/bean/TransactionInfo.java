/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.bean;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author nazmul hasan
 */
public class TransactionInfo {
    private int id;
    private String transactionId;
    private String APIKey;
    private double balanceIn;
    private double balanceOut;
    private int transactionTypeId;
    private int transactionStatusId;
    private String cellNumber;
    private String description;
    private String createdOn;
    private String modifiedOn;
    public TransactionInfo()
    {
        balanceIn = 0;
        balanceOut = 0;
        cellNumber = "";
        description = "";
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

    public double getBalanceIn() {
        return balanceIn;
    }

    public void setBalanceIn(double balanceIn) {
        this.balanceIn = balanceIn;
    }

    public double getBalanceOut() {
        return balanceOut;
    }

    public void setBalanceOut(double balanceOut) {
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

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }


    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }
    
     public static TransactionInfo getTransctionInfo(String jsonContent) {
        TransactionInfo transctionInfo = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            transctionInfo = mapper.readValue(jsonContent, TransactionInfo.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return transctionInfo;
    }
}
