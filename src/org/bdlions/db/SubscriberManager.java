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
import org.bdlions.bean.ServiceInfo;
import org.bdlions.bean.UserInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.db.repositories.Service;
import org.bdlions.db.repositories.Subscriber;
import org.bdlions.db.repositories.User;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sampan IT
 */
public class SubscriberManager {

    private int responseCode;
    private String subscriberId;
    private Subscriber subscriber;
    private User user;
    private final Logger logger = LoggerFactory.getLogger(EasyStatement.class);

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    /**
     * This method will return all subscribers
     *
     */
    public List<UserInfo> getAllSubscribers() {
        List<UserInfo> subscriberList = null;
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            subscriber = new Subscriber(connection);
            subscriberList = subscriber.geAllSubscribers();
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
        return subscriberList;
    }

    /**
     * This method will return subscriber info
     *
     * @param userId, subscriber user Id
     *
     */
    public UserInfo getSubscriberInfo(String userId) {
        UserInfo subscriberInfo = null;
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            subscriber = new Subscriber(connection);
            subscriberInfo = subscriber.getSubscriberInfo(userId);
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
        return subscriberInfo;
    }

    /**
     * This method will create subscriber
     *
     * @param userInfo, subscriber Info
     *
     */
    public void createSubscriber(UserInfo userInfo) throws ParseException {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            subscriber = new Subscriber(connection);
            user = new User(connection);
            String userId = user.createUser(userInfo);
            userInfo.setUserId(userId);
            subscriber.createSubscriber(userInfo);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
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
    }

    /**
     * This method will update subscriber
     *
     * @param userInfo, subscriber Info
     *
     */
    public String updateSubscriber(UserInfo userInfo) throws ParseException {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            subscriber = new Subscriber(connection);
            subscriber.updateSubscriber(userInfo);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
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
        return "";
    }



}
