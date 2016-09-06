package ttl.Queues;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.naming.NamingException;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by pubudup on 9/5/16.
 */
public class QueueReceiveDefault {

    public static final String QPID_ICF = "org.wso2.andes.jndi.PropertiesFileInitialContextFactory";
    private static final String CF_NAME_PREFIX = "connectionfactory.";
    private static final String CF_NAME = "qpidConnectionfactory";
    String userName = "admin";
    String password = "admin";
    private static String CARBON_CLIENT_ID = "carbon";
    private static String CARBON_VIRTUAL_HOST_NAME = "carbon";
    private static String CARBON_DEFAULT_HOSTNAME = "10.100.7.95";
    private static String CARBON_DEFAULT_PORT = "5672";
    String topicName = "DefaultQueue";


    public static void main(String[] args) throws NamingException, JMSException {
        QueueReceiveDefault queueReceiver = new QueueReceiveDefault();
        queueReceiver.receiveMessages();
    }

    public void receiveMessages() throws NamingException, JMSException {
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, QPID_ICF);
        properties.put(CF_NAME_PREFIX + CF_NAME, getTCPConnectionURL(userName, password));
        System.out.println("getTCPConnectionURL(userName,password) = " + getTCPConnectionURL(userName, password));
        InitialContext ctx = new InitialContext(properties);
        // Lookup connection factory
        TopicConnectionFactory connFactory = (TopicConnectionFactory) ctx.lookup(CF_NAME);
        TopicConnection topicConnection = connFactory.createTopicConnection();
        topicConnection.start();
        TopicSession topicSession =
                topicConnection.createTopicSession(false, QueueSession.AUTO_ACKNOWLEDGE);
        Topic topic = topicSession.createTopic(topicName);

        javax.jms.TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic);

        int count = 1;

        while (true) {

            TextMessage message = (TextMessage) topicSubscriber.receive();

            System.out.println("::Message Count::" + count + ":::::::::::::Received message with content::::::::::::" + message.getText());

            count++;

        }

    }

        public String getTCPConnectionURL(String username, String password) {
            // amqp://{username}:{password}@carbon/carbon?brokerlist='tcp://{hostname}:{port}'
            return new StringBuffer()
                    .append("amqp://").append(username).append(":").append(password)
                    .append("@").append(CARBON_CLIENT_ID)
                    .append("/").append(CARBON_VIRTUAL_HOST_NAME)
                    .append("?brokerlist='tcp://").append(CARBON_DEFAULT_HOSTNAME).append(":").append(CARBON_DEFAULT_PORT).append("'")
                    .toString();
        }

}
