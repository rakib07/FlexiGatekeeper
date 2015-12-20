package org.bdlions.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.bdlions.bean.ServiceInfo;
import org.bdlions.bean.UserInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.db.repositories.Service;
import org.bdlions.db.repositories.Subscriber;
import org.bdlions.exceptions.DBSetupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class ServiceManager {

    private int responseCode;
    private int serviceId;
    private Service service;
    private final Logger logger = LoggerFactory.getLogger(EasyStatement.class);

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getServiceId() {
        return this.serviceId;
    }

    /**
     * This method will return all services
     *
     */
    public List<ServiceInfo> getAllServices() {
        List<ServiceInfo> serviceList = null;
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            service = new Service(connection);
            serviceList = service.getAllServices();
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        } catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex1) {
                    logger.error(ex1.getMessage());
                }
            }
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return serviceList;
    }

    /**
     * This method will add a service
     *
     * @param ServiceInfo, service info
     */
    public void createService(ServiceInfo serviceInfo) {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            service = new Service(connection);
            this.serviceId = service.createService(serviceInfo);
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
    }

    /**
     * This method will return a service info
     *
     * @param Service_id, service id
     */
    public ServiceInfo getServiceInfo(int service_id) {
        Connection connection = null;
        ServiceInfo serviceInfo = null;
        try {
            connection = Database.getInstance().getConnection();
            service = new Service(connection);
            serviceInfo = service.getServiceInfo(service_id);
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
        } catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex1) {
                    logger.error(ex1.getMessage());
                }
            }
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return serviceInfo;

    }

    /**
     * This method will update a service
     *
     * @param ServiceInfo, service info
     */
    public String updateServiceInfo(ServiceInfo serviceInfo) {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            service = new Service(connection);
            service.updateServiceInfo(serviceInfo);
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return " ";

    }

   

}
