package org.bdlions.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.bdlions.bean.UserInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.db.exceptions.DBSetupException;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.db.query.helper.QueryManager;
import org.bdlions.utility.Utils;

/**
 *
 * @author nazmul hasan
 */
public class AuthenticationModel {
    private final Utils utils;
    public AuthenticationModel()
    {
        utils = new Utils();
    }
    /**
     * This method will create a new user
     * @param userInfo, user info
     * @return String, user id
     * @throws DBSetupException
     * @throws SQLException
     */
    public String createUser(UserInfo userInfo) throws DBSetupException, SQLException
    {
        int currentTime = utils.getCurrentUnixTime();
        String userId = utils.getRandomString();
        userInfo.setUserId(userId);
        try
        {
            EasyStatement stmt = new EasyStatement(QueryManager.CREATE_USER);
            stmt.setString("user_id", userInfo.getUserId());
            stmt.setString("reference_username", userInfo.getReferenceUserName());
            stmt.setString("reference_password", userInfo.getReferencePassword());
            stmt.setInt("created_on", currentTime);
            stmt.setInt("modified_on", currentTime);
            stmt.executeUpdate();
        }
        catch(Exception ex)
        {
            //handle exception here
        }  
        return userId;
    }
    
    /**
     * This method will create a new subscriber
     * @param userInfo, user info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void createSubscriber(UserInfo userInfo) throws DBSetupException, SQLException
    {
        try
        {
            EasyStatement stmt = new EasyStatement(QueryManager.CREATE_SUBSCRIBER);
            stmt.setString("user_id", userInfo.getUserId());
            stmt.setInt("registration_date", userInfo.getRegistrationDate());
            stmt.setInt("expired_date", userInfo.getExpiredDate());
            stmt.setInt("max_members", userInfo.getMaxMembers());
            stmt.setString("ip_address", userInfo.getIpAddress());
            stmt.executeUpdate();
        }
        catch(Exception ex)
        {
        
        }        
    }
    
    /**
     * This method will add a member under a subscriber
     * @param subscriberId, subscriber user id
     * @param memberId, member user id
     * @throws DBSetupException
     * @throws SQLException
     */
    public void addSubscriberMember(String subscriberId, String memberId) throws DBSetupException, SQLException
    {
        try
        {
            EasyStatement stmt = new EasyStatement(QueryManager.ADD_SUBSCRIBER_MEMBER);
            stmt.setString("subscriber_user_id", subscriberId);
            stmt.setString("member_user_id", memberId);
            stmt.executeUpdate();
        }
        catch(Exception ex)
        {
        
        }
    }
    
    /**
     * This method will add subscriber service info
     * @param userServiceInfo, subscriber service info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void addSubscriberService(UserServiceInfo userServiceInfo) throws DBSetupException, SQLException
    {
        userServiceInfo.setAPIKey(utils.getAPIKey());
        try
        {
            EasyStatement stmt = new EasyStatement(QueryManager.ADD_SUBSCRIBER_SERVICE);
            stmt.setString("subscriber_user_id", userServiceInfo.getUserId());
            stmt.setInt("service_id", userServiceInfo.getServiceId());
            stmt.setString("api_key", userServiceInfo.getAPIKey());
            stmt.setInt("registration_date", userServiceInfo.getRegistrationDate());
            stmt.setInt("expired_date", userServiceInfo.getExpiredDate());
            stmt.executeUpdate();
        }
        catch(Exception ex)
        {
        
        }
    }
    
    /**
     * This method will return subscriber info based on given ip address
     * @param ipAddress, ip address
     * @throws DBSetupException,
     * @throws SQLException
     * @return Userinfo, user info including details of the user
     */
    public UserInfo getSubscriberInfo(String ipAddress) throws DBSetupException, SQLException
    {
        UserInfo userInfo = new UserInfo();
        try
        {
            EasyStatement stmt = new EasyStatement(QueryManager.GET_SUBSCRIBER_INFO);
            stmt.setString("ip_address", ipAddress);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                userInfo.setUserId(rs.getString("user_id"));
                userInfo.setSubscriberId(rs.getString("user_id"));
                userInfo.setMaxMembers(rs.getInt("max_members"));
                userInfo.setCurrentMemers(rs.getInt("current_members"));
                userInfo.setRegistrationDate(rs.getInt("registration_date"));
                userInfo.setExpiredDate(rs.getInt("expired_date"));
            }
        }
        catch(Exception ex)
        {
        
        }
        
        return userInfo;
    }
}
