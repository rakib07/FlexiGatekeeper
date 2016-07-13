package org.bdlions.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.activemq.Producer;
import org.bdlions.bean.SIMInfo;
import org.bdlions.bean.SIMServiceInfo;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.constants.Services;
import org.bdlions.db.repositories.SIM;
import org.bdlions.exceptions.DBSetupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class SIMManager {
    private final Logger logger = LoggerFactory.getLogger(SIMManager.class);
    private int responseCode;
    private SIM sim;
    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    /**
     * This method will add a new sim
     * @param simInfo, sim info
     * @author nazmul hasan on 11th June 2016
     */
    public void addSIM(SIMInfo simInfo)
    {
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM();
            sim.addSIM(simInfo);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
    }
    
    /**
     * This method will return SIM Info
     * @param simNo, sim number
     * @return SIMInfo
     * @author nazmul hasan on 11th June 2016
     */
    public SIMInfo getSIMInfo(String simNo)
    {
        SIMInfo simInfo = new SIMInfo();
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM();
            simInfo = sim.getSIMInfo(simNo);
            connection.close();
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return simInfo;
    }
    
    /**
     * This method will return all SIMs
     * @return sim list
     * @author nazmul hasan on 11th June 2016
     */
    public List<SIMInfo> getAllSIMs() 
    {
        List<SIMInfo> simList = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM();
            simList = sim.getAllSIMs();
            connection.close();
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return simList;
    }
    
    /**
     * This method will update SIM info
     * @param simInfo, SIM Info
     * @author nazmul hasan on 11th June 2016
     */
    public void updateSIMInfo(SIMInfo simInfo)
    {
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            connection.setAutoCommit(false);
            sim = new SIM();
            sim.updateSIMInfo(simInfo);
            sim.updateSIMServiceBalanceInfo(simInfo);
            connection.commit();
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
    }
    
    public void updateSIMServiceBalanceInfo(SIMInfo simInfo)
    {
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM();
            sim.updateSIMServiceBalanceInfo(simInfo);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.toString());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.toString());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.toString());
        }
    }
    
    public void generateSIMBalance(SIMInfo simInfo)
    {
        try
        {
            //right now one sim has one service only. eg. bkash/dbbl/mcahs/ucash
            if(simInfo.getSimServiceList().size() > 0)
            {
                SIMServiceInfo simServiceInfo = simInfo.getSimServiceList().get(0);
                if(simServiceInfo.getId() == Services.SIM_SERVICE_TYPE_ID_BKASH)
                {
                    TransactionInfo transactionInfo = new TransactionInfo();
                    transactionInfo.setCellNumber(simInfo.getSimNo());
                    transactionInfo.setServiceId(Services.SERVICE_TYPE_ID_BKASH_CHECKBALANCE);
                    
                    Producer producer = new Producer();
                    producer.setMessage(transactionInfo.toString());
                    System.out.println("Executing the transaction:"+transactionInfo.toString());
                    producer.setBkashCheckBalanceQueueName(simInfo.getSimNo());
                    producer.produce();
                }                
            }            
        }
        catch(Exception ex)
        {
            logger.error(ex.toString());
        }
    }
}
