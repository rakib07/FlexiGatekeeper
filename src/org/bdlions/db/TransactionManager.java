/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import org.bdlions.activemq.Producer;
import org.bdlions.bean.TransactionInfo;
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

    public TransactionManager() {

    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    /**
     * This method will add a user payment as transaction
     *
     * @param transactionInfo, transaction info
     */
    public void addUserPayment(TransactionInfo transactionInfo) {
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
                if (connection != null) {
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
     *
     * @param transactionInfo, transaction info
     */
    public void addTransaction(TransactionInfo transactionInfo) {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);

            //check available balance of the user if required
            transactionInfo.setTransactionStatusId(Transactions.TRANSACTION_STATUS_PENDING);
            transactionInfo.setTransactionTypeId(Transactions.TRANSACTION_TYPE_USE_SERVICE);
            this.transactionId = transaction.createTransaction(transactionInfo);
            connection.close();
        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }

        //activemq to enqueue a new transaction
        try {
            Producer producer = new Producer();
            producer.setMessage(transactionInfo.toString());
            producer.produce();
            this.responseCode = ResponseCodes.SUCCESS;
        } catch (Exception ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_SERVER_EXCEPTION;
            logger.error(ex.getMessage());
        }
    }

    public String updateTransaction(TransactionInfo transactionInfo) throws ParseException {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            transaction.updateTransaction(transactionInfo);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        } catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex1) {
                    logger.error(ex1.getMessage());
                }
            }
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return "";
    }
    public String deleteTransaction(String transactionId){
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            transaction.deleteTransaction(transactionId);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        } catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex1) {
                    logger.error(ex1.getMessage());
                }
            }
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return "";
    }

    public List<TransactionInfo> getAllTransactions() {
        List<TransactionInfo> transactionList = null;
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            transactionList = transaction.getAllTransactions();
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        } catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex1) {
                    logger.error(ex1.getMessage());
                }
            }
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return transactionList;
    }

    public TransactionInfo getTransactionInfo(String transctionId) {
        TransactionInfo transactionInfo = null;
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            transactionInfo = transaction.getTransactionInfo(transctionId);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        } catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex1) {
                    logger.error(ex1.getMessage());
                }
            }
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return transactionInfo;
    }

    public static void main(String args[]) {
        TransactionManager obj = new TransactionManager();

    }
}
