package org.bdlions.activemq;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.bdlions.utility.ServerPropertyProvider;

public class Producer {
    private String message = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public void produce() throws Exception
    {
        // Create a connection factory referring to the broker host and port
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory
          (ServerPropertyProvider.get("activemq_producer_url"));

        // Note that a new thread is created by createConnection, and it
        //  does not stop even if connection.stop() is called. We must
        //  shut down the JVM using System.exit() to end the program
        Connection connection = factory.createConnection();

        // Start the connection
        connection.start();
        
        // Create a non-transactional session with automatic acknowledgement
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create a reference to the queue test_queue in this session. Note
        //  that ActiveMQ has auto-creation enabled by default, so this JMS
        //  destination will be created on the broker automatically
        Queue queue = session.createQueue(ServerPropertyProvider.get("activemq_queue_name"));

        // Create a producer for test_queue
        MessageProducer producer = session.createProducer(queue);

        // Create a simple text message and send it
        TextMessage message = session.createTextMessage (this.getMessage());
        producer.send(message);

        // Stop the connection — good practice but redundant here
        connection.stop();
    }
    
}
