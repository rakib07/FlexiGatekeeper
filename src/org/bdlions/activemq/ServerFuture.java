package org.bdlions.activemq;

import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul
 */
public class ServerFuture {
    private static ServerFuture _serverFuture;
    static Logger _logger = LoggerFactory.getLogger(ServerFuture.class.getName());
    MQTT mqtt;
    FutureConnection connection;
    /**
     * Constructor
     */
    private ServerFuture()
    {
        try{
            mqtt = new MQTT();
            mqtt.setHost("127.0.0.1", 61612);

            connection = mqtt.futureConnection();
            Future<Void> f1 = connection.connect();
            f1.await();            
            System.out.println("Future Connection for server status:"+connection.isConnected());
            _logger.debug("Future Connection for server status:"+connection.isConnected());
         }
         catch(Exception ex)
         {
             _logger.error(ex.toString());
         }
    }
    
    public static ServerFuture getInstance()
    {
        if(_serverFuture == null)
        {
            _serverFuture = new ServerFuture();
        }
        return _serverFuture;
    }
    
    /**
     * Sending transaction to android local server
     * @param localServerIdentifier, local server identifier
     * @param transactionInfo, transaction info
     */
    public void setTransaction(String localServerIdentifier, String transactionInfo)
    {
        try{
            Future<Void> f3 = connection.publish(localServerIdentifier, transactionInfo.getBytes(), QoS.EXACTLY_ONCE, false);            
            System.out.println("Sending transaction to the android local server:"+transactionInfo);
         }
         catch(Exception ex)
         {
            _logger.error(ex.toString());
         }
    }
}
