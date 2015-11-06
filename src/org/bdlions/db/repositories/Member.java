/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.SQLException;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;

/**
 *
 * @author alamgir
 */
public class Member {
    /**
     * This method will add a member under a subscriber
     *
     * @param subscriberId, subscriber user id
     * @param memberId, member user id
     * @throws DBSetupException
     * @throws SQLException
     */
    public void addMember(String subscriberId, String memberId) throws DBSetupException, SQLException {
        try {
            EasyStatement stmt = new EasyStatement(QueryManager.ADD_SUBSCRIBER_MEMBER);
            stmt.setString(QueryField.SUBSCRIBER_USER_ID, subscriberId);
            stmt.setString(QueryField.MEMBER_USER_ID, memberId);
            stmt.executeUpdate();
        } catch (SQLException | DBSetupException ex) {

        }
    }
}
