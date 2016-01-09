/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import java.sql.Connection;
import java.sql.SQLException;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.db.repositories.Payment;
import org.bdlions.exceptions.DBSetupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class PaymentManager {

    private int responseCode;
    private Payment payment;
    private final Logger logger = LoggerFactory.getLogger(EasyStatement.class);

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void addSubscriberPayment() {

    }

    public void getSubscriberPaymentList() {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            payment = new Payment(connection);
            payment.getSubscriberPaymentList();
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

    }
}
