/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.bean.UserInfo;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.DateUtils;
import org.bdlions.utility.Utils;

/**
 *
 * @author nazmul hasan
 */
public class Transaction {

    private Connection connection;

    /**
     * *
     * Restrict to call without connection
     */
    private Transaction() {
    }

    public Transaction(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method will return all transctions
     *
     * @throws DBSetupException
     * @throws SQLException
     * @return transactionList all transactions
     */
    public List<TransactionInfo> getAllTransactions() throws DBSetupException, SQLException {
        List<TransactionInfo> transactionList = new ArrayList<>();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_ALL_TRANSACTIONS);) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TransactionInfo transactionInfo = new TransactionInfo();
                transactionInfo.setTransactionId(rs.getString(QueryField.TRANSACTION_ID));
                transactionInfo.setAPIKey(rs.getString(QueryField.API_KEY));
                transactionInfo.setBalanceIn(rs.getInt(QueryField.BALANCE_IN));
                transactionInfo.setBalanceOut(rs.getInt(QueryField.BALANCE_OUT));
                transactionInfo.setTransactionStatusId(rs.getInt(QueryField.TRANSACTION_STATUS_ID));
                transactionInfo.setTransactionTypeId(rs.getInt(QueryField.TRANSACTION_TYPE_ID));
                transactionInfo.setCellNumber(rs.getString(QueryField.TRANSACTION_CELL_NUMBER));
                transactionInfo.setDescription(rs.getString(QueryField.TRANSACTION_DESCRIPTION));
                transactionInfo.setCreatedOn(DateUtils.getUnixToHuman(rs.getInt(QueryField.CREATED_ON)));
                transactionInfo.setModifiedOn(DateUtils.getUnixToHuman(rs.getInt(QueryField.MODIFIED_ON)));
                transactionList.add(transactionInfo);
            }
        }
        return transactionList;
    }

    /**
     * This method will return transction info
     *
     * @throws DBSetupException
     * @throws SQLException
     * @return transactionList all transactions
     */
    public TransactionInfo getTransactionInfo(String transctionId) throws DBSetupException, SQLException {
        TransactionInfo transactionInfo = new TransactionInfo();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_TRANSACTION_INFO);) {
            stmt.setString(QueryField.TRANSACTION_ID, transctionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactionInfo.setTransactionId(rs.getString(QueryField.TRANSACTION_ID));
                transactionInfo.setAPIKey(rs.getString(QueryField.API_KEY));
                transactionInfo.setBalanceIn(rs.getInt(QueryField.BALANCE_IN));
                transactionInfo.setBalanceOut(rs.getInt(QueryField.BALANCE_OUT));
                transactionInfo.setTransactionStatusId(rs.getInt(QueryField.TRANSACTION_STATUS_ID));
                transactionInfo.setTransactionTypeId(rs.getInt(QueryField.TRANSACTION_TYPE_ID));
                transactionInfo.setCellNumber(rs.getString(QueryField.TRANSACTION_CELL_NUMBER));
                transactionInfo.setDescription(rs.getString(QueryField.TRANSACTION_DESCRIPTION));
                transactionInfo.setCreatedOn(DateUtils.getUnixToHuman(rs.getInt(QueryField.CREATED_ON)));
                transactionInfo.setModifiedOn(DateUtils.getUnixToHuman(rs.getInt(QueryField.MODIFIED_ON)));
            }
        }
        return transactionInfo;
    }

    /**
     * This method will create a new transaciton
     *
     * @param transactionInfo, transaction info
     * @throws DBSetupException
     * @throws SQLException
     * @return String, transaction id
     */
    public String createTransaction(TransactionInfo transactionInfo) throws DBSetupException, SQLException {
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
     * This method will update a new transaciton
     *
     * @param transactionInfo, transaction info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void updateTransaction(TransactionInfo transactionInfo) throws DBSetupException, SQLException, ParseException {
        int currentTime = Utils.getCurrentUnixTime();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.UPDATE_TRANSACTION)) {
            stmt.setString(QueryField.TRANSACTION_ID, transactionInfo.getTransactionId());
            stmt.setString(QueryField.API_KEY, transactionInfo.getAPIKey());
            stmt.setDouble(QueryField.BALANCE_IN, transactionInfo.getBalanceIn());
            stmt.setDouble(QueryField.BALANCE_OUT, transactionInfo.getBalanceOut());
            stmt.setInt(QueryField.TRANSACTION_TYPE_ID, transactionInfo.getTransactionTypeId());
            stmt.setInt(QueryField.TRANSACTION_STATUS_ID, transactionInfo.getTransactionStatusId());
            stmt.setString(QueryField.TRANSACTION_CELL_NUMBER, transactionInfo.getCellNumber());
            stmt.setString(QueryField.TRANSACTION_DESCRIPTION, transactionInfo.getDescription());
            stmt.setInt(QueryField.CREATED_ON, (int) DateUtils.getHumanToUnix(transactionInfo.getCreatedOn()));
            stmt.setInt(QueryField.MODIFIED_ON, currentTime);
            stmt.executeUpdate();
        }
    }

    public void deleteTransaction(String transactionId) throws DBSetupException, SQLException {
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.DELETE_TRANSACTION)) {
            stmt.setString(QueryField.TRANSACTION_ID, transactionId);
            stmt.executeUpdate();
        }
    }

    /**
     * This method will return current available balance of an api key
     *
     * @param APIKey, api key
     * @throws DBSetupException
     * @throws SQLException
     * @return double, current available balance
     */
    public double getAvailableBalance(String APIKey) throws DBSetupException, SQLException {
        double currentBalance = 0;
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_CURRENT_BALANCE);) {
            stmt.setString(QueryField.API_KEY, APIKey);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                currentBalance = rs.getDouble(QueryField.CURRENT_BALANCE);
            }
        }
        return currentBalance;
    }

}
