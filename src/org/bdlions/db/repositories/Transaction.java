/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.Utils;

/**
 *
 * @author nazmul hasan
 */
public class Transaction {
    private Connection connection;
    /***
     * Restrict to call without connection
     */
    private Transaction(){}
    public Transaction(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * This method will return user service info based on API Key
     * @param APIKey, API Key
     * @return UserServiceInfo, user service info
     * @throws DBSetupException
     * @throws SQLException
     */
    public UserServiceInfo getUserServiceInfo(String APIKey) throws DBSetupException, SQLException
    {
        UserServiceInfo userServiceInfo = new UserServiceInfo();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_USER_SERVICE_INFO);){
            stmt.setString(QueryField.API_KEY, APIKey);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userServiceInfo.setServiceId(rs.getInt(QueryField.SERVICE_ID));
                //if required set other fields
            }
        }
        return userServiceInfo;
    }
    
    /**
     * This method will create a new transaciton
     * @param transactionInfo, transaction info
     * @throws DBSetupException
     * @throws SQLException
     * @return String, transaction id
     */
    public String createTransaction(TransactionInfo transactionInfo) throws DBSetupException, SQLException
    {
        int currentTime = Utils.getCurrentUnixTime();
        String transactionId = Utils.getTransactionId();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.CREATE_TRANSACTION)) {
            stmt.setString(QueryField.TRANSACTION_ID, transactionId);
            stmt.setString(QueryField.API_KEY, transactionInfo.getAPIKey());
            stmt.setDouble(QueryField.BALANCE_IN, transactionInfo.getBalanceIn());
            stmt.setDouble(QueryField.BALANCE_OUT, transactionInfo.getBalanceOut());            
            stmt.setInt(QueryField.TRANSACTION_TYPE_ID, transactionInfo.getTransactionTypeId());
            stmt.setInt(QueryField.TRANSACTION_STATUS_ID, transactionInfo.getTransactionStatusId());
            stmt.setString(QueryField.TRANSACTION_CELL_NUMBER, transactionInfo.getCellNumber());
            stmt.setString(QueryField.TRANSACTION_DESCRIPTION, transactionInfo.getDescription());
            stmt.setInt(QueryField.CREATED_ON, currentTime);
            stmt.setInt(QueryField.MODIFIED_ON, currentTime);
            stmt.executeUpdate();
        }
        return transactionId;
    }
    
    /**
     * This method will update transaction status
     * @param transactionInfo, transaction info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void updateTransactionStatus(TransactionInfo transactionInfo) throws DBSetupException, SQLException
    {
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.UPDATE_TRANSACTION_STATUS);){
            stmt.setInt(QueryField.TRANSACTION_STATUS_ID, transactionInfo.getTransactionStatusId());
            stmt.setString(QueryField.TRANSACTION_ID, transactionInfo.getTransactionId());
            stmt.executeUpdate();        
        }
    }
    
    /**
     * This method will return current available balance of an api key
     * @param APIKey, api key
     * @throws DBSetupException
     * @throws SQLException
     * @return double, current available balance
     */
    public double getAvailableBalance(String APIKey) throws DBSetupException, SQLException
    {
        double currentBalance = 0;
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_CURRENT_BALANCE);){
            stmt.setString(QueryField.API_KEY, APIKey);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                currentBalance = rs.getDouble(QueryField.CURRENT_BALANCE);
            }
        } 
        return currentBalance;
    }
}
