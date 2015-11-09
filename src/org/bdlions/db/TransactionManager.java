/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import java.sql.Connection;
import java.sql.SQLException;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.constants.Transactions;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.db.repositories.Transaction;
import org.bdlions.exceptions.DBSetupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class TransactionManager {
    private Transaction transaction;
    private final Logger logger = LoggerFactory.getLogger(EasyStatement.class);
    public TransactionManager()
    {
    
    }
    
    /**
     * This method will add a user payment as transaction
     * @param transactionInfo, transaction info
     * @return String, transaction id
     */
    public String addUserPayment(TransactionInfo transactionInfo)
    {
        String transactionId = "";
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            
            transactionInfo.setTransactionStatusId(Transactions.TRANSACTION_STATUS_SUCCESS);
            transactionInfo.setTransactionTypeId(Transactions.TRANSACTION_TYPE_ADD_USER_PAYMENT);
            transactionId = transaction.createTransaction(transactionInfo);            
            
            connection.close();
        } catch (SQLException ex) {
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
        } catch (DBSetupException ex) {
            
        }
        return transactionId;
    }
    
    /**
     * This method will add a transaction
     * @param transactionInfo, transaction info
     * @return String, transaction id
     */
    public String addTransaction(TransactionInfo transactionInfo)
    {
        String transactionId = "";
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            
            //at first check available balance of the user
            
            transactionInfo.setTransactionStatusId(Transactions.TRANSACTION_STATUS_PENDING);
            transactionInfo.setTransactionTypeId(Transactions.TRANSACTION_TYPE_USE_SERVICE);
            transactionId = transaction.createTransaction(transactionInfo);            
            
            connection.close();
        } catch (SQLException ex) {
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
        } catch (DBSetupException ex) {
            
        }
        return transactionId;
    }
}
