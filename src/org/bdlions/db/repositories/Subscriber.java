/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bdlions.bean.UserInfo;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.Utils;

/**
 *
 * @author alamgir
 */
public class Subscriber {

    private Connection connection;
    /***
     * Restrict to call without connection
     */
    private Subscriber(){}
    public Subscriber(Connection connection) {
        this.connection = connection;
    }

    
    /**
     * This method will create a new user
     *
     * @param userInfo, user info
     * @return String, user id
     * @throws DBSetupException
     * @throws SQLException
     */
    public String createUser(UserInfo userInfo) throws DBSetupException, SQLException {
        int currentTime = Utils.getCurrentUnixTime();
        String userId = Utils.getRandomString();
        userInfo.setUserId(userId);
        try {
            EasyStatement stmt = new EasyStatement(connection, QueryManager.CREATE_USER);
            stmt.setString(QueryField.USER_ID, userInfo.getUserId());
            stmt.setString(QueryField.REFERENCE_USERNAME, userInfo.getReferenceUserName());
            stmt.setString(QueryField.REFERENCE_PASSWORD, userInfo.getReferencePassword());
            stmt.setInt(QueryField.CREATED_ON, currentTime);
            stmt.setInt(QueryField.MODIFIED_ON, currentTime);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            //handle exception here
        }
        return userId;
    }

    /**
     * This method will create a new subscriber
     *
     * @param userInfo, user info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void createSubscriber(UserInfo userInfo) throws DBSetupException, SQLException {
        try {
            EasyStatement stmt = new EasyStatement(connection, QueryManager.CREATE_SUBSCRIBER);
            stmt.setString(QueryField.USER_ID, userInfo.getUserId());
            stmt.setInt(QueryField.REGISTRATION_DATE, userInfo.getRegistrationDate());
            stmt.setInt(QueryField.EXPIRED_DATE, userInfo.getExpiredDate());
            stmt.setInt(QueryField.MAX_MEMBERS, userInfo.getMaxMembers());
            stmt.setString(QueryField.IP_ADDRESS, userInfo.getIpAddress());
            stmt.executeUpdate();
        } catch (SQLException ex) {

        }
    }

    /**
     * This method will return subscriber info based on given ip address
     *
     * @param ipAddress, ip address
     * @throws DBSetupException,
     * @throws SQLException
     * @return Userinfo, user info including details of the user
     */
    public UserInfo getSubscriberInfo(String ipAddress) throws DBSetupException, SQLException {
        UserInfo userInfo = new UserInfo();
        try {
            EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_SUBSCRIBER_INFO);
            stmt.setString(QueryField.IP_ADDRESS, ipAddress);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userInfo.setUserId(rs.getString(QueryField.USER_ID));
                userInfo.setSubscriberId(rs.getString(QueryField.USER_ID));
                userInfo.setMaxMembers(rs.getInt(QueryField.MAX_MEMBERS));
                userInfo.setCurrentMemers(rs.getInt(QueryField.CURRENT_MEMBERS));
                userInfo.setRegistrationDate(rs.getInt(QueryField.REGISTRATION_DATE));
                userInfo.setExpiredDate(rs.getInt(QueryField.EXPIRED_DATE));
            }
        } catch (SQLException  ex) {

        }

        return userInfo;
    }
}
