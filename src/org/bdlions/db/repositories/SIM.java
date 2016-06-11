package org.bdlions.db.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.SIMInfo;
import org.bdlions.bean.SIMServiceInfo;
import org.bdlions.db.Database;
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
    public SIM()
    {
    
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
        try (EasyStatement stmt = new EasyStatement(Database.getInstance().getConnection(), QueryManager.ADD_SIM)) {
            stmt.setString(QueryField.SIM_NO, simInfo.getSimNo());
            stmt.setString(QueryField.DESCRIPTION, simInfo.getDescription());
            stmt.setInt(QueryField.CREATED_ON, currentTime);
            stmt.setInt(QueryField.MODIFIED_ON, currentTime);
            stmt.executeUpdate();
        }
        if(simInfo.getSimServiceList().size() > 0)
        {
            //right now we are storing one service under one sim
            SIMServiceInfo simServiceInfo = simInfo.getSimServiceList().get(0);
            try (EasyStatement stmt = new EasyStatement(Database.getInstance().getConnection(), QueryManager.ADD_SIM_SERVICE)) {
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
     * @return List, sim list
     * @throws DBSetupException
     * @throws SQLException
     */
    public List<SIMInfo> getAllSIMs() throws DBSetupException, SQLException
    {
        List<SIMInfo> simList = new ArrayList<>();
        try (EasyStatement stmt = new EasyStatement(Database.getInstance().getConnection(), QueryManager.GET_ALL_SIMS);){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SIMInfo simInfo = new SIMInfo();
                simInfo.setSimNo(rs.getString("sim_no"));
                simInfo.setDescription(rs.getString("description"));
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
        try (EasyStatement stmt = new EasyStatement(Database.getInstance().getConnection(), QueryManager.GET_SIM_INFO);){
            stmt.setString(QueryField.SIM_NO, simNo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                simInfo.setSimNo(rs.getString("sim_no"));
                simInfo.setDescription(rs.getString("description"));
                SIMServiceInfo simServiceInfo = new SIMServiceInfo();
                simServiceInfo.setCurrentBalance(rs.getDouble("current_balance"));
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
        try (EasyStatement stmt = new EasyStatement(Database.getInstance().getConnection(), QueryManager.UPDATE_SIM_INFO);){
            stmt.setString(QueryField.DESCRIPTION, simInfo.getDescription());
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
            try (EasyStatement stmt = new EasyStatement(Database.getInstance().getConnection(), QueryManager.UPDATE_SIM_SERVICE_BALANCE_INFO);){
                stmt.setDouble(QueryField.CURRENT_BALANCE, simServiceInfo.getCurrentBalance());
                stmt.setInt(QueryField.MODIFIED_ON, currentTime);
                stmt.setString(QueryField.SIM_NO, simInfo.getSimNo());
                stmt.setInt(QueryField.SERVICE_ID, simServiceInfo.getId());
                stmt.executeUpdate();        
            }
        }        
    }    
}
