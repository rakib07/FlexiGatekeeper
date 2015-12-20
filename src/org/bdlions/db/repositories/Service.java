/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import static java.lang.Boolean.TRUE;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.ServiceInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.db.Database;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.Utils;

/**
 *
 * @author alamgir
 */
public class Service {

    private Connection connection;

    /**
     * *
     * Restrict to call without connection
     */
    private Service() {
    }

    public Service(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method will add subscriber service info
     *
     * @param userServiceInfo, subscriber service info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void addService(UserServiceInfo userServiceInfo) throws DBSetupException, SQLException {
        //right now api key is generated from the admin panel
        //String APIKey = Utils.getAPIKey();
        //userServiceInfo.setAPIKey(APIKey);

        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_SUBSCRIBER_SERVICE);) {
            stmt.setString(QueryField.SUBSCRIBER_USER_ID, userServiceInfo.getUserId());
            stmt.setInt(QueryField.SERVICE_ID, userServiceInfo.getServiceId());
            stmt.setString(QueryField.API_KEY, userServiceInfo.getAPIKey());
            stmt.setInt(QueryField.REGISTRATION_DATE, userServiceInfo.getRegistrationDate());
            stmt.setInt(QueryField.EXPIRED_DATE, userServiceInfo.getExpiredDate());
            stmt.executeUpdate();
        }
        //adding callback function for the APIKey of this service
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_CALLBACK_FUNCTION);) {
            stmt.setString(QueryField.API_KEY, userServiceInfo.getAPIKey());
            stmt.setString(QueryField.CALLBACK_FUNCTION, userServiceInfo.getCallbackFunction());
            stmt.executeUpdate();
        }
    }

    /**
     * This method will add service
     *
     * @param serviceInfo, service info
     * @throws DBSetupException
     * @throws SQLException
     */
    public int createService(ServiceInfo serviceInfo) throws DBSetupException, SQLException {
        int serviceId = Utils.getServiceId();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_SERVICE)) {
            stmt.setInt(QueryField.ID, serviceId);
            stmt.setString(QueryField.SERVICE_TITLE, serviceInfo.getTitle());
            stmt.executeUpdate();
        }
        return serviceId;

    }

    /**
     * This method will return all service
     *
     */
    public List<ServiceInfo> getAllServices() {
        Connection conn = null;
        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        try {
            conn = Database.getInstance().getConnection();

            try (EasyStatement stmt = new EasyStatement(conn, QueryManager.GET_ALL_SERVICES);) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    ServiceInfo serviceInfo = new ServiceInfo();
                    serviceInfo.setId(rs.getInt(QueryField.SERVICE_ID));
                    serviceInfo.setTitle(rs.getString(QueryField.SERVICE_TITLE));
                    serviceInfoList.add(serviceInfo);
                }
            }
        } catch (DBSetupException | SQLException excepton) {

        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {

            }
        }
        return serviceInfoList;
    }

    public ServiceInfo getServiceInfo(int service_id) throws DBSetupException, SQLException {
        ServiceInfo serviceInfo = new ServiceInfo();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_SERVICE_INFO);) {
            stmt.setInt(QueryField.ID, service_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                serviceInfo.setId(rs.getInt(QueryField.ID));
                serviceInfo.setTitle(rs.getString(QueryField.SERVICE_TITLE));
                break;
            }
        }
        return serviceInfo;
    }

    /**
     * This method will update service
     *
     * @param serviceInfo, service info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void updateServiceInfo(ServiceInfo serviceInfo) throws DBSetupException, SQLException {
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.UPDATE_SERVICE_INFO)) {
            stmt.setString(QueryField.SERVICE_TITLE, serviceInfo.getTitle());
            stmt.setInt(QueryField.ID, serviceInfo.getId());
            stmt.executeUpdate();
        }

    }
}
