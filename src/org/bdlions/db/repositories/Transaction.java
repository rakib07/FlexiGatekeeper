/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import org.bdlions.bean.TransactionInfo;
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
            stmt.setLong(QueryField.BALANCE_IN, transactionInfo.getBalanceIn());
            stmt.setLong(QueryField.BALANCE_OUT, transactionInfo.getBalanceOut());
            stmt.setInt(QueryField.TRANSACTION_TYPE_ID, transactionInfo.getTransactionTypeId());
            stmt.setInt(QueryField.TRANSACTION_STATUS_ID, transactionInfo.getTransactionStatusId());
            stmt.setInt(QueryField.CREATED_ON, currentTime);
            stmt.setInt(QueryField.MODIFIED_ON, currentTime);
            stmt.executeUpdate();
        }
        return transactionId;
    }
}