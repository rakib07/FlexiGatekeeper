/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.UserInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.db.query.QueryManager;
import org.bdlions.exceptions.MaxMemberRegException;
import org.bdlions.exceptions.SubscriptionExpireException;
import org.bdlions.exceptions.UnRegisterIPException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 * @author alamgir
 */
public class DatabaseTest {
    
    public DatabaseTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    //@Test
    public void setUpDatabase() throws DBSetupException, SQLException{
        Database.getInstance();
    }
    
   
    //@Test
    public void createSubscriberLibrary() throws DBSetupException, SQLException{
        UserInfo userInfo = new UserInfo();
        userInfo.setReferenceUserName("ru10");
        userInfo.setReferencePassword("rp10");
        userInfo.setMaxMembers(2);
        userInfo.setRegistrationDate(12345);
        userInfo.setExpiredDate(123456789);
        userInfo.setIpAddress("192.168.1.2");
        
        UserServiceInfo userServiceInfo = new UserServiceInfo();
        userServiceInfo.setServiceId(1);
        userServiceInfo.setRegistrationDate(12345);
        userServiceInfo.setExpiredDate(123456789);    
        List<UserServiceInfo> userServiceInfoList = new ArrayList<>();
        userServiceInfoList.add(userServiceInfo);
        
        AuthManager authenticationLibrary = new AuthManager();
        authenticationLibrary.createSubscriber(userInfo, userServiceInfoList);
    }
    
    @Test
    public void createUserLibrary() throws DBSetupException, SQLException, UnRegisterIPException, SubscriptionExpireException, MaxMemberRegException{
        UserInfo userInfo = new UserInfo();
        userInfo.setReferenceUserName("ru13");
        userInfo.setReferencePassword("rp13");
        userInfo.setIpAddress("192.168.1.6");
        
        AuthManager authenticationLibrary = new AuthManager();
        authenticationLibrary.createUser(userInfo);
    }
    
    //@Test
    public void createService() throws DBSetupException, SQLException{
        EasyStatement stmt = new EasyStatement(Database.getInstance().getConnection(), QueryManager.ADD_SERVICE);
        stmt.setString("title", "Super string");
        stmt.setInt("id", 12);
        stmt.executeUpdate();
    }
}
