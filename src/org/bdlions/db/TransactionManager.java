/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import java.sql.Connection;
import java.sql.SQLException;
import org.bdlions.activemq.Producer;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.callback.CallbackTransactionManager;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.constants.Transactions;
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
    private String transactionId;
    private int responseCode;
    private final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    public TransactionManager()
    {
    
    }
    
    public String getTransactionId()
    {
        return this.transactionId;
    }
    
    public int getResponseCode()
    {
        return this.responseCode;
    }
    
    /**
     * This method will add a user payment as transaction
     * @param transactionInfo, transaction info
     */
    public void addUserPayment(TransactionInfo transactionInfo)
    {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            
            transactionInfo.setTransactionStatusId(Transactions.TRANSACTION_STATUS_SUCCESS);
            transactionInfo.setTransactionTypeId(Transactions.TRANSACTION_TYPE_ADD_USER_PAYMENT);
            this.transactionId = transaction.createTransaction(transactionInfo);            
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
        } catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
    }
    
    /**
     * This method will add a transaction
     * @param transactionInfo, transaction info
     */
    public void addTransaction(TransactionInfo transactionInfo)
    {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            connection.setAutoCommit(false);
            transaction = new Transaction(connection);
            
            //check available balance of the user if required
            
            transactionInfo.setTransactionStatusId(Transactions.TRANSACTION_STATUS_PENDING);
            transactionInfo.setTransactionTypeId(Transactions.TRANSACTION_TYPE_USE_SERVICE);
            this.transactionId = transaction.createTransaction(transactionInfo);  
            transactionInfo.setTransactionId(this.transactionId);
            UserServiceInfo userServiceInfo = transaction.getUserServiceInfo(transactionInfo.getAPIKey());
            transactionInfo.setServiceId(userServiceInfo.getServiceId());
            
            //activemq to enqueue a new transaction
            Producer producer = new Producer();
            System.out.println(transactionInfo.toString());
            producer.setMessage(transactionInfo.toString());
            producer.setServiceQueueName(transactionInfo.getServiceId());
            producer.produce();
            this.responseCode = ResponseCodes.SUCCESS;
            
            connection.commit();
            connection.close();
        } catch (SQLException ex) {
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        catch (Exception ex) {            
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            this.responseCode = ResponseCodes.ERROR_CODE_SERVER_EXCEPTION;
            logger.error(ex.getMessage());
        }        
    }
    
    /**
     * This method will update transaction status
     * @param transactionInfo, transaction info
     */
    public void updateTransactionStatus(TransactionInfo transactionInfo)
    {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            
            transaction.updateTransactionStatus(transactionInfo); 
            CallbackTransactionManager callbackTransactionManager = new CallbackTransactionManager();
            callbackTransactionManager.updateTransactionStatus(transactionInfo.getTransactionId(), transactionInfo.getTransactionStatusId(), transactionInfo.getSenderCellNumber());
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
        } catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
    }
}
