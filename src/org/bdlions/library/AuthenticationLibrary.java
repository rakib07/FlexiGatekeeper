package org.bdlions.library;

import java.util.List;
import org.bdlions.bean.UserInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.model.AuthenticationModel;

/**
 *
 * @author nazmul hasan
 */
public class AuthenticationLibrary {
    private final AuthenticationModel authenticationModel;
    public AuthenticationLibrary()
    {
        authenticationModel = new AuthenticationModel();
    }
    
    /**
     * This method will create a new subscriber
     * @param userInfo, user info
     * @param userServiceInfoList, user service info list
     */
    public void createSubscriber(UserInfo userInfo, List<UserServiceInfo> userServiceInfoList)
    {
        //validate the userInfo where required fields are
        //referenceUserName, referenceUserPassword, registrationDate, expiredDate, maxMembers, ipAddress
        try
        {
            String userId = authenticationModel.createUser(userInfo);
            userInfo.setUserId(userId);            
            
            authenticationModel.createSubscriber(userInfo);
            authenticationModel.addSubscriberMember(userInfo.getUserId(), userInfo.getUserId());
            for (UserServiceInfo userServiceInfo : userServiceInfoList) {
                //validate the userServiceInfo where required fields are
                //serviceId, registrationDate, expiredDate
                userServiceInfo.setUserId(userId);
                authenticationModel.addSubscriberService(userServiceInfo);
            }
            
        }
        catch(Exception ex)
        {
        
        }
    }
    
    /**
     * This method will create a new user under a subscriber
     * @param userInfo, user info
     * 
     */
    public void createUser(UserInfo userInfo)
    {              
        //validate the userInfo where required fields are
        //referenceUserName, referenceUserPassword, ipaddress,

        //now a dummy time is used
        int currentTime = 1;
        //check where there maximum members under a subscriber is not exceeded
        try
        {
            UserInfo subscriberInfo = authenticationModel.getSubscriberInfo(userInfo.getIpAddress());
            if(subscriberInfo.getUserId() == null)
            {
                //request from invalid ip address
                System.out.println("request from invalid ip address.");
                return;
            }
            if(subscriberInfo.getExpiredDate() < currentTime)
            {
                //subscription is expired
                System.out.println("Suscription expired.");
                return;
            }
            if(subscriberInfo.getCurrentMemers() >= subscriberInfo.getMaxMembers())
            {
                //subscriber already created maximum members
                System.out.println("subscriber already created maximum members");
                return;
            }
            String userId = authenticationModel.createUser(userInfo);
            userInfo.setUserId(userId);
            authenticationModel.addSubscriberMember(subscriberInfo.getUserId(), userInfo.getUserId());
        }
        catch(Exception ex)
        {
        
        }
    }
}
