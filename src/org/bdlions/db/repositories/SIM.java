package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.SIMInfo;
import org.bdlions.bean.SIMSMSInfo;
import org.bdlions.bean.SIMServiceInfo;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.Utils;

/**
 *
 * @author nazmul hasan
 */
public class SIM {
    private Connection connection;
    /***
     * Restrict to call without connection
     */
    private SIM(){}
    public SIM(Connection connection)
    {
        this.connection = connection;
    }
    
    
    /**
     * This method will add a new SIM into the database
     * @param simInfo, SIMInfo
     * @throws DBSetupException
     * @throws SQLException
     */
    public void addSIM(SIMInfo simInfo) throws DBSetupException, SQLException
    {
        int currentTime = Utils.getCurrentUnixTime();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_SIM)) {
            stmt.setString(QueryField.SIM_NO, simInfo.getSimNo());
            stmt.setString(QueryField.IDENTIFIER, simInfo.getIdentifier());
            stmt.setString(QueryField.DESCRIPTION, simInfo.getDescription());
            stmt.setInt(QueryField.STATUS, simInfo.getStatus());
            stmt.setInt(QueryField.CREATED_ON, currentTime);
            stmt.setInt(QueryField.MODIFIED_ON, currentTime);
            stmt.executeUpdate();
        }
        if(simInfo.getSimServiceList().size() > 0)
        {
            //right now we are storing one service under one sim
            SIMServiceInfo simServiceInfo = simInfo.getSimServiceList().get(0);
            try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_SIM_SERVICE)) {
                stmt.setString(QueryField.SIM_NO, simInfo.getSimNo());
                stmt.setInt(QueryField.SERVICE_ID, simServiceInfo.getId());
                stmt.setInt(QueryField.CATEGORY_ID, simServiceInfo.getCategoryId());
                stmt.setDouble(QueryField.CURRENT_BALANCE, simServiceInfo.getCurrentBalance());
                stmt.setInt(QueryField.CREATED_ON, currentTime);
                stmt.setInt(QueryField.MODIFIED_ON, currentTime);
                stmt.executeUpdate();
            }
        }
        
    }
    
    /**
     * This method will return all sims
     * @param identifier
     * @return List, sim list
     * @throws DBSetupException
     * @throws SQLException
     */
    public List<SIMInfo> getAllSIMs(String identifier) throws DBSetupException, SQLException
    {
        List<SIMInfo> simList = new ArrayList<>();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_ALL_SIMS);){
            stmt.setString(QueryField.IDENTIFIER, identifier);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SIMInfo simInfo = new SIMInfo();
                simInfo.setSimNo(rs.getString("sim_no"));
                simInfo.setIdentifier(rs.getString("identifier"));
                simInfo.setDescription(rs.getString("description"));
                simInfo.setStatus(rs.getInt("status"));
                SIMServiceInfo simServiceInfo = new SIMServiceInfo();
                simServiceInfo.setCurrentBalance(rs.getDouble("current_balance"));
                simServiceInfo.setCreatedOn(rs.getInt("created_on"));
                simServiceInfo.setModifiedOn(rs.getInt("modified_on"));
                simInfo.getSimServiceList().add(simServiceInfo);
                simList.add(simInfo);
            }
        }
        return simList;
    }
    
    /**
     * This method will return sim info
     * @param simNo, sim no
     * @return SIMInfo, sim info
     * @exception DBSetupException
     * @exception SQLException
     */
    public SIMInfo getSIMInfo(String simNo) throws DBSetupException, SQLException
    {
        SIMInfo simInfo = new SIMInfo();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_SIM_INFO);){
            stmt.setString(QueryField.SIM_NO, simNo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                simInfo.setSimNo(rs.getString("sim_no"));
                simInfo.setIdentifier(rs.getString("identifier"));
                simInfo.setDescription(rs.getString("description"));
                simInfo.setStatus(rs.getInt("status"));
                SIMServiceInfo simServiceInfo = new SIMServiceInfo();
                simServiceInfo.setCurrentBalance(rs.getDouble("current_balance"));
                simServiceInfo.setId(rs.getInt("service_id"));
                simServiceInfo.setCategoryId(rs.getInt("category_id"));
                simInfo.getSimServiceList().add(simServiceInfo);
            }
        }
        return simInfo;
    }
    
    /**
     * This method will update SIM Info
     *@param simInfo, SIM Info
     * @exception DBSetupException
     * @exception SQLException
     */
    public void updateSIMInfo(SIMInfo simInfo) throws DBSetupException, SQLException
    {
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.UPDATE_SIM_INFO);){
            stmt.setString(QueryField.IDENTIFIER, simInfo.getIdentifier());
            stmt.setString(QueryField.DESCRIPTION, simInfo.getDescription());
            stmt.setInt(QueryField.STATUS, simInfo.getStatus());
            stmt.setString(QueryField.SIM_NO, simInfo.getSimNo());
            stmt.executeUpdate();        
        }
    }
    
    /**
     * This method will update SIM Current Balance
     *@param simInfo, SIM Info
     * @exception DBSetupException
     * @exception SQLException
     */
    public void updateSIMServiceBalanceInfo(SIMInfo simInfo) throws DBSetupException, SQLException
    {
        int currentTime = Utils.getCurrentUnixTime();
        if(simInfo.getSimServiceList().size() > 0)
        {
            SIMServiceInfo simServiceInfo = simInfo.getSimServiceList().get(0);
            try (EasyStatement stmt = new EasyStatement(connection, QueryManager.UPDATE_SIM_SERVICE_BALANCE_INFO);){
                stmt.setDouble(QueryField.CURRENT_BALANCE, simServiceInfo.getCurrentBalance());
                stmt.setInt(QueryField.MODIFIED_ON, currentTime);
                stmt.setString(QueryField.SIM_NO, simInfo.getSimNo());
                stmt.setInt(QueryField.SERVICE_ID, simServiceInfo.getId());
                stmt.executeUpdate();        
            }
        }        
    }  
    
    /**
     * This method will add sim sms into the database
     * @param simSMSInfo
     * @throws DBSetupException
     * @throws SQLException
     */
    public void addSIMMessage(SIMSMSInfo simSMSInfo) throws DBSetupException, SQLException
    {
        int currentTime = Utils.getCurrentUnixTime();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_SIM_MESSAGE)) {
            stmt.setString(QueryField.COUNTRY_CODE, simSMSInfo.getCountryCode());
            stmt.setString(QueryField.SIM_NO, simSMSInfo.getSimNo());
            stmt.setString(QueryField.SENDER, simSMSInfo.getSender());
            stmt.setString(QueryField.SMS, simSMSInfo.getSms());
            stmt.setInt(QueryField.CREATED_ON,currentTime);
            stmt.setInt(QueryField.MODIFIED_ON,currentTime);
            stmt.executeUpdate();
        }
    }
}
