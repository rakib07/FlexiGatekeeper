/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.bean.UserInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.repositories.Transaction;
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
    
    @Test
    public void setUpDatabase() throws DBSetupException, SQLException{
        Database.getInstance();
    }
    
   
    //@Test
    public void createSubscriberLibrary() throws DBSetupException, SQLException{
        UserInfo userInfo = new UserInfo();
        userInfo.setReferenceUserName("ru40");
        userInfo.setReferencePassword("rp40");
        userInfo.setMaxMembers(3);
        userInfo.setRegistrationDate(12345);
        userInfo.setExpiredDate(123456789);
        userInfo.setIpAddress("192.168.1.40");
        
        UserServiceInfo userServiceInfo = new UserServiceInfo();
        userServiceInfo.setServiceId(1);
        userServiceInfo.setRegistrationDate(12345);
        userServiceInfo.setExpiredDate(123456789);  
        userServiceInfo.setCallbackFunction("callback40");
        List<UserServiceInfo> userServiceInfoList = new ArrayList<>();
        userServiceInfoList.add(userServiceInfo);
        
        AuthManager authenticationLibrary = new AuthManager();
        authenticationLibrary.createSubscriber(userInfo, userServiceInfoList);
    }
    
    //@Test
    public void createUserLibrary() throws DBSetupException, SQLException, UnRegisterIPException, SubscriptionExpireException, MaxMemberRegException{
        UserInfo userInfo = new UserInfo();
        userInfo.setReferenceUserName("ru13");
        userInfo.setReferencePassword("rp13");
        userInfo.setIpAddress("192.168.1.6");
        
        AuthManager authenticationLibrary = new AuthManager();
        authenticationLibrary.createUser(userInfo);
    }
    
    //@Test
    public void getSessionInfo()
    {
        UserInfo userInfo = new UserInfo();
        userInfo.setReferenceUserName("ru31");
        userInfo.setReferencePassword("rp31");
        userInfo.setIpAddress("192.168.1.30");
        
        try
        {
            AuthManager authManager = new AuthManager();
            String sessionInfo = authManager.getSessionInfo(userInfo, "vr22old1v415kipdk9uob4kv07");
            System.out.println(sessionInfo);
        }
        catch(Exception ex)
        {
        
        }        
    }
    
    //@Test
    public void createTransaction()
    {
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setAPIKey("key1");
        transactionInfo.setBalanceOut(100);
        
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.addTransaction(transactionInfo);
    }
    
    //@Test
    public void addUserTransaction()
    {
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setAPIKey("key1");
        transactionInfo.setBalanceIn(50000);
        
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.addUserPayment(transactionInfo);
    }
    
    //@Test
    public void createService() throws DBSetupException, SQLException{
        EasyStatement stmt = new EasyStatement(Database.getInstance().getConnection(), QueryManager.ADD_SERVICE);
        stmt.setString("title", "Super string");
        stmt.setInt("id", 12);
        stmt.executeUpdate();
    }
}
