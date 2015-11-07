package org.bdlions.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.bdlions.bean.SessionInfo;
import org.bdlions.bean.UserInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.db.repositories.Member;
import org.bdlions.db.repositories.Service;
import org.bdlions.db.repositories.Session;
import org.bdlions.db.repositories.Subscriber;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.exceptions.MaxMemberRegException;
import org.bdlions.exceptions.ServiceExpireException;
import org.bdlions.exceptions.SubscriptionExpireException;
import org.bdlions.exceptions.UnRegisterIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class AuthManager {

    private Member member;
    private Service service;
    private Subscriber subscriber;
    private Session session;
    private final Logger logger = LoggerFactory.getLogger(EasyStatement.class);

    /**
     * This method will create a new subscriber
     *
     * @param userInfo, user info
     * @param userServiceInfoList, user service info list
     */
    public void createSubscriber(UserInfo userInfo, List<UserServiceInfo> userServiceInfoList) {
        //validate the userInfo where required fields are
        //referenceUserName, referenceUserPassword, registrationDate, expiredDate, maxMembers, ipAddress
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            connection.setAutoCommit(false);
            
            subscriber = new Subscriber(connection);
            member = new Member(connection);
            service = new Service(connection);
            
            String userId = subscriber.createUser(userInfo);
            userInfo.setUserId(userId);

            subscriber.createSubscriber(userInfo);
            
            member.addMember(userInfo.getUserId(), userInfo.getUserId());
            for (UserServiceInfo userServiceInfo : userServiceInfoList) {
                //validate the userServiceInfo where required fields are
                //serviceId, registrationDate, expiredDate
                userServiceInfo.setUserId(userId);
                service.addService(userServiceInfo);
            }

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
        } catch (DBSetupException ex) {
            
        }
    }

    /**
     * This method will create a new user under a subscriber
     *
     * @param userInfo, user info
     * @throws UnRegisterIPException
     * @throws SubscriptionExpireException
     * @throws MaxMemberRegException
     *
     */
    public void createUser(UserInfo userInfo) throws UnRegisterIPException, SubscriptionExpireException, MaxMemberRegException {
        //validate the userInfo where required fields are
        //referenceUserName, referenceUserPassword, ipaddress,

        //now a dummy time is used
        int currentTime = 1;
        Connection connection = null;
        //check where there maximum members under a subscriber is not exceeded
        try {
            connection = Database.getInstance().getConnection();
            connection.setAutoCommit(false);
            
            subscriber = new Subscriber(connection);
            member = new Member(connection);
            subscriber = new Subscriber(connection);
            
            UserInfo subscriberInfo = subscriber.getSubscriberInfo(userInfo.getIpAddress());
            if (subscriberInfo.getUserId() == null) {
                //request from invalid ip address
                logger.error("request from invalid ip address.");
                throw new UnRegisterIPException();
            }
            if (subscriberInfo.getExpiredDate() < currentTime) {
                //subscription is expired
                logger.error("Subscription expired.");
                throw new SubscriptionExpireException();
            }
            if (subscriberInfo.getCurrentMemers() >= subscriberInfo.getMaxMembers()) {
                //subscriber already created maximum members
                logger.error("subscriber already created maximum members");
                throw new MaxMemberRegException();
            }
            String userId = subscriber.createUser(userInfo);
            userInfo.setUserId(userId);
            member.addMember(subscriberInfo.getUserId(), userInfo.getUserId());
            
            connection.commit();
            connection.close();
        } catch (SQLException ex) {
            if(connection != null){
                try{
                    connection.rollback();
                    connection.close();
                }
                catch(SQLException ex1){
                    
                }
            }
        } catch (DBSetupException ex) {
            
        }
    }
    
    /**
     * This method will return session info
     * @param userInfo, user info
     * @param APIKey, api key
     * @throws SubscriptionExpireException
     * @throws ServiceExpireException
     * @return String, session info
     */
    public String getSessionInfo(UserInfo userInfo, String APIKey) throws SubscriptionExpireException, ServiceExpireException
    {
        Connection connection = null;
        SessionInfo sessionInfo = new SessionInfo();
        try {
            connection = Database.getInstance().getConnection();
            session = new Session(connection);   
            sessionInfo = session.getSessionInfo(userInfo, APIKey);
            
            //authenticate available balance before sending session info
            
            //put session info into the hashmap at service api server
            
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
        return sessionInfo.toString();
    }
}
