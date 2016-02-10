/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.exceptions.DBSetupException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 * @author alamgir
 */
public class TransactionManagerTest {
    
    public TransactionManagerTest() {
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
    public void addTransactionTest() throws DBSetupException, SQLException{
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setAPIKey("key1");
        transactionInfo.setCellNumber("01678112509");
        transactionInfo.setDescription("desc");
        transactionInfo.setBalanceOut(100);
        
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.addTransaction(transactionInfo);
    }
    
    //@Test
    public void updateTransactionStatusTest() throws DBSetupException, SQLException{
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setTransactionId("cqajvooe9sr5vsr4gprkbqnvk0");
        transactionInfo.setTransactionStatusId(2);
        
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.updateTransactionStatus(transactionInfo);
    }
    
    //@Test
    public void callbackAPITest(){
        try {
            URL obj = new URL("http://localhost/callbackws/callback/update_transaction_status");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            String urlParameters = "transaction_id=" + 5 + "&status_id=" + 3;

            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
                
                int responseCode = con.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()))) {
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        
                        String result = response.toString();
                        System.out.println(result);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
}
