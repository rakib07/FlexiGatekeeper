/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bdlions.db.Database;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;

/**
 *
 * @author nazmul hasan
 */
public class Payment {

    private Connection connection;

    /**
     * *
     * Restrict to call without connection
     */
    private Payment() {
    }

    public Payment(Connection connection) {
        this.connection = connection;
    }

    public void addSubscriberPayment() {

    }

    public void getSubscriberPaymentList() {
        Connection conn = null;
        try {
            conn = Database.getInstance().getConnection();

            try (EasyStatement stmt = new EasyStatement(conn, QueryManager.GET_ALL_SUBSCRIBER_PAYMENTS);) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {

                }
            }
        } catch (DBSetupException | SQLException excepton) {

        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {

            }
        }
    }
}
