/*
 * $Log: JMSFacade.java,v $
 * Revision 1.10  2004-04-26 09:58:06  NNVZNL01#L180564
 * Added time-to-live on sent messages
 *
 * Revision 1.9  2004/03/31 12:04:19  gerrit
 * fixed javadoc
 *
 * Revision 1.8  2004/03/30 07:30:03  gerrit
 * updated javadoc
 *
 * Revision 1.7  2004/03/26 10:42:51  johan
 * added @version tag in javadoc
 *
 * Revision 1.6  2004/03/26 09:50:51  johan
 * Updated javadoc
 *
 * Revision 1.5  2004/03/24 08:24:46  gerrit
 * enabled XA transactions
 * renamed original 'transacted' into 'jmsTransacted'
 *
 */
package nl.nn.adapterframework.jms;

import nl.nn.adapterframework.core.HasPhysicalDestination;
import nl.nn.adapterframework.core.IXAEnabled;
import nl.nn.adapterframework.core.IbisException;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import nl.nn.adapterframework.core.INamedObject;

import javax.jms.*;
import javax.naming.NamingException;


/**
 * Provides functions for jms connections, queues and topics and acts as a facade
 * to hide for clients whether a <code>Queue</code> or <code>Topic</code> is used. 
 * <br/>
 * The <code>destinationType</code> field specifies which
 * type should be used.<br/>
 * @version Id
 * @author    Gerrit van Brakel
 */
public class JMSFacade extends JNDIBase implements INamedObject, HasPhysicalDestination, IXAEnabled {
	public static final String version="$Id: JMSFacade.java,v 1.10 2004-04-26 09:58:06 NNVZNL01#L180564 Exp $";

	private String name;

	private boolean transacted = false;
	private boolean jmsTransacted = false;
	private String subscriberType = "DURABLE"; // DURABLE or TRANSIENT

    private int ackMode = Session.AUTO_ACKNOWLEDGE;
    private boolean persistent;
    private String destinationName;
    private boolean useTopicFunctions = false;

    private String destinationType="QUEUE"; // QUEUE or TOPIC

    private Connection connection;
    private Destination destination;

	private long messageTimeToLive=0;


    //<code>forceMQCompliancy</code> is used to perform MQ specific replying.
    //If the MQ destination is not a JMS receiver, format errors occur.
    //To prevent this, settting replyToComplianceType to MQ will inform
    //MQ that the queue (or destination) on which a message is sent, is not JMS compliant.
    
    private String forceMQCompliancy=null;
    
    //---------------------------------------------------------------------
    // Queue fields
    //---------------------------------------------------------------------
    private String queueConnectionFactoryName;
	private String queueConnectionFactoryNameXA;
    private QueueConnectionFactory queueConnectionFactory = null;
    //---------------------------------------------------------------------
    // Topic fields
    //---------------------------------------------------------------------
    private String topicConnectionFactoryName;
	private String topicConnectionFactoryNameXA;
    private TopicConnectionFactory topicConnectionFactory = null;

    
    
	/**
	 *  Gets the queueConnectionFactory 
	 *
	 * @return                                   The queueConnectionFactory value
	 * @exception  javax.naming.NamingException  Description of the Exception
	 */
	private QueueConnectionFactory getQueueConnectionFactory()
		throws NamingException {
		if (null == queueConnectionFactory) {
			String qcfName = isTransacted() ? getQueueConnectionFactoryNameXA() : getQueueConnectionFactoryName();
			log.debug("["+name+"] searching for queueConnectionFactory [" + qcfName + "]");
			queueConnectionFactory =
				(QueueConnectionFactory) getContext().lookup(qcfName);
			log.info("["+name+"] queueConnectionFactory [" + qcfName + "] found: [" + queueConnectionFactory + "]");
		}
		return queueConnectionFactory;
	}
	private TopicConnectionFactory getTopicConnectionFactory()
		throws NamingException, JMSException {
		if (null == topicConnectionFactory) {
			String tcfName = isTransacted() ? getTopicConnectionFactoryNameXA() : getTopicConnectionFactoryName();
			log.debug("["+name+"] searching for topicConnectionFactory [" + tcfName + "]");
			topicConnectionFactory =
				(TopicConnectionFactory) getContext().lookup(tcfName);
			log.info("["+name+"] topicConnectionFactory [" + tcfName + "] found: [" + topicConnectionFactory + "]");
		}
		return topicConnectionFactory;
	}

	/**
	 * Returns a connection for a topic or a queue
	 */
	protected Connection getConnection() throws NamingException, JMSException {
		if (connection == null) {
		log.debug("["+getName()+"] creating connection, useTopicFunctions=["+useTopicFunctions+"], isTransacted=["+isTransacted()+"]");
		if (useTopicFunctions)
			connection = getTopicConnectionFactory().createTopicConnection();
		else
			connection = getQueueConnectionFactory().createQueueConnection();
		}
		connection.start();
		return connection;
	}
    
	/**
	 *  Gets the queueSession 
	 *
	 * @see javax.jms.QueueSession
	 * @return                                   The queueSession value
	 * @exception  javax.naming.NamingException
	 * @exception  javax.jms.JMSException
	 */
	private QueueSession createQueueSession(QueueConnection connection)
		throws NamingException, JMSException {
		return connection.createQueueSession(isJmsTransacted(), getAckMode());
	}
	private TopicSession createTopicSession(TopicConnection connection)
		throws NamingException, JMSException {
		return connection.createTopicSession(isJmsTransacted(), getAckMode());
	}
	/**
	 * Returns a session on the connection for a topic or a queue
	 */
	public Session createSession() throws NamingException, JMSException {
		if (useTopicFunctions)
			return createTopicSession((TopicConnection)getConnection());
		else
			return createQueueSession((QueueConnection)getConnection());
	}

	public void open() throws IbisException {
		try {
			connection = getConnection();
			destination = getDestination();
		} catch (Exception e) {
			throw new IbisException(e);
		}
	}
	   
	public void close() throws IbisException {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (JMSException e) {
			throw new IbisException(e);
		} finally {
			destination = null;
			connection = null;
		}
	}
	
	
	public TextMessage createTextMessage(Session session, String correlationID, String message)
	   throws javax.naming.NamingException, JMSException {
	    TextMessage textMessage = null;
	    textMessage = session.createTextMessage();
	    if (null != correlationID) {
	        textMessage.setJMSCorrelationID(correlationID);
	    }
	    textMessage.setText(message);
	    return textMessage;
	}

	/**
	 * Enforces the setting of <code>forceMQCompliancy</code><br/>.
	 * this method has to be called prior to creating a <code>QueueSender</code>
	 */
 	private void enforceMQCompliancy(Queue queue) throws JMSException {
	    if (forceMQCompliancy!=null) {
	    	if (forceMQCompliancy.equalsIgnoreCase("MQ")){
			    ((MQQueue)queue).setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);
			    log.debug("["+name+"] MQ Compliancy for queue ["+queue.toString()+"] set to NONJMS");
	    	} else
	    	if (forceMQCompliancy.equalsIgnoreCase("JMS")) {
			    ((MQQueue)queue).setTargetClient(JMSC.MQJMS_CLIENT_JMS_COMPLIANT);
			    log.debug("MQ Compliancy for queue ["+queue.toString()+"] set to JMS");
	    	}
	    	
	    }
 
    }

	public Destination getDestination() throws NamingException, JMSException {
	
	    if (destination == null) {
		    if (!useTopicFunctions || persistent) {
		        destination = getDestination(getDestinationName());
		    } else {
		        destination = createTopicSession((TopicConnection) getConnection()).createTopic(
		            getDestinationName());
		    }
	    }
	    return destination;
	}
    /**
     * Utilitiy function to retrieve a Destination from a jndi.
     * @param destinationName
     * @return javax.jms.Destination
     * @throws javax.naming.NamingException
     */
    public Destination getDestination(String destinationName) throws javax.naming.NamingException {
        Destination dest=null;
        dest=(Destination) getContext().lookup(destinationName);
        return dest;
    }


	public MessageConsumer getMessageConsumerForCorrelationId(Session session, Destination destination, String correlationId) throws NamingException, JMSException {
		if (correlationId==null)
			return getMessageConsumer(session, destination, null);
		else
			return getMessageConsumer(session, destination, "JMSCorrelationID='" + correlationId + "'");
	}
	
	
	public MessageConsumer getMessageConsumer(Session session, Destination destination, String selector) throws NamingException, JMSException {
	    if (useTopicFunctions)
	        return getTopicSubscriber((TopicSession)session, (Topic)destination, selector);
	    else
	        return getQueueReceiver((QueueSession)session, (Queue)destination, selector);
	}
	
    /**
     * gets a sender. if topicName is used the <code>getTopicPublisher()</code>
     * is used, otherwise the <code>getQueueSender()</code>
     */
    public MessageProducer getMessageProducer(Session session, Destination destination)
        throws NamingException, JMSException {
        if (useTopicFunctions)
            return getTopicPublisher((TopicSession)session, (Topic)destination);

        else
            return getQueueSender((QueueSession)session, (Queue)destination);
    }
    
	public String getPhysicalDestinationName() {
	
	    String result = null;
	
		try {
		    if (getDestination() != null) {
	            if (useTopicFunctions)
	                result = ((Topic) destination).getTopicName();
	            else
	                result = ((Queue) destination).getQueueName();
		    }
	    } catch (Exception je) {
	        log.warn("[" + name + "] got exception in getPhysicalDestinationName", je);
	    }
	    return getDestinationType()+"("+getDestinationName()+") ["+result+"]";
	}
	
    /**
     *  Gets a queueReceiver 
     * @see javax.jms.QueueReceiver
     * @return                                   The queueReceiver value
     * @exception  javax.naming.NamingException  Description of the Exception
     * @exception  javax.jms.JMSException                  Description of the Exception
     */
	private QueueReceiver getQueueReceiver(QueueSession session, Queue destination, String selector)
	    throws NamingException, JMSException {
	    QueueReceiver queueReceiver = session.createReceiver(destination, selector);
	//    log.debug("["+name+"] got receiver for queue " + queueReceiver.getQueue().getQueueName()+"]");
	    return queueReceiver;
	}
	/**
	  *  Gets the queueSender for a specific queue, not the one in <code>destination</code>
	  * @see javax.jms.QueueSender
	  * @return                                   The queueReceiver value
	  * @exception  javax.naming.NamingException  Description of the Exception
	  * @exception  javax.jms.JMSException
	  */
	private QueueSender getQueueSender(QueueSession session, Queue destination)
	    throws NamingException, JMSException {
	    QueueSender queueSender;
	    enforceMQCompliancy(destination);
	    queueSender = session.createSender(destination);
	
	    return queueSender;
	}
	
	/**
	 * Gets a topicPublisher for a specified topic
	 */
	private TopicPublisher getTopicPublisher(TopicSession session, Topic topic)
	    throws NamingException, JMSException {
	    return session.createPublisher(topic);
	}
	private TopicSubscriber getTopicSubscriber(
	    TopicSession session,
	    Topic topic,
	    String selector)
	    throws NamingException, JMSException {
	
	    TopicSubscriber topicSubscriber;
	    if (subscriberType.equalsIgnoreCase("DURABLE")) {
	        topicSubscriber =
	            session.createDurableSubscriber(topic, destinationName, selector, false);
	        log.debug(
	            "["
	                + name
	                + "] got durable subscriber for topic ["
	                + destinationName
	                + "] with selector ["
	                + selector
	                + "]");
	
	    } else {
	        topicSubscriber = session.createSubscriber(topic, selector, false);
	        log.debug(
	            "["
	                + name
	                + "] got transient subscriber for topic ["
	                + destinationName
	                + "] with selector ["
	                + selector
	                + "]");
	    }
	
	    return topicSubscriber;
	}
	    
	/**
	 * Send a message
	 * @param messageProducer
	 * @param message
	 * @return messageID of the sent message
	 * @throws NamingException
	 * @throws JMSException
	 */    
	public String send(MessageProducer messageProducer, Message message)
	    throws NamingException, JMSException {

		if (getMessageTimeToLive()>0)
				messageProducer.setTimeToLive(getMessageTimeToLive());	    
	
	    if (messageProducer instanceof TopicPublisher) {
	         ((TopicPublisher) messageProducer).publish(message);
	    } else {
	         ((QueueSender) messageProducer).send(message);
		}

	    return message.getJMSMessageID();
	}
	/**
	 * Send a message
	 * @param session
	 * @param dest destination
	 * @param message
	 * @return message ID of the sent message
	 * @throws NamingException
	 * @throws JMSException
	 */
	public String send(Session session, Destination dest, Message message)
	    throws NamingException, JMSException {
	
	    if (dest instanceof Topic)
	        return sendByTopic((TopicSession)session, (Topic)dest, message);
	    else
	        return sendByQueue((QueueSession)session, (Queue)dest, message);
	}
	/**
	 * Send a message to a Destination of type Queue.
	 * This method respects the <code>replyToComplianceType</code> field,
	 * as if it is set to "MQ", 
	 * @return messageID of the sent message
	 */
	private String sendByQueue(QueueSession session, Queue destination, Message message)
	    throws NamingException, JMSException {
	    enforceMQCompliancy(destination);
	    QueueSender tqs = session.createSender(destination);
	    tqs.send(message);
	    tqs.close();
	    return message.getJMSMessageID();
	}
	private String sendByTopic(TopicSession session, Topic destination, Message message)
	    throws NamingException, JMSException {
	
	    TopicPublisher tps = session.createPublisher(destination);
	    tps.publish(message);
	    tps.close();
		return message.getJMSMessageID();
	
	}


    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        if (useTopicFunctions) {
            sb.append("[topicName=" + destinationName + "]");
	        sb.append("[topicConnectionFactoryName=" + topicConnectionFactoryName + "]");
	        sb.append("[topicConnectionFactoryNameXA=" + topicConnectionFactoryNameXA + "]");
        } else {
            sb.append("[queueName=" + destinationName + "]");
	        sb.append("[queueConnectionFactoryName=" + queueConnectionFactoryName + "]");
	        sb.append("[queueConnectionFactoryNameXA=" + queueConnectionFactoryNameXA + "]");
        }
	//  sb.append("[physicalDestinationName="+getPhysicalDestinationName()+"]");
        sb.append("[ackMode=" + getAcknowledgeModeAsString(ackMode) + "]");
        sb.append("[persistent=" + persistent + "]");
        sb.append("[transacted=" + transacted + "]");
        return sb.toString();
    }


	/**
	 * The name of the object. 
	 */
	public void setName(String newName) {
		name = newName;
	}
	public String getName() {
		return name;
	}

	/**
	 * The name of the destination, this may be a <code>queue</code> or <code>topic</code> name. 
	 */
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public String getDestinationName() {
		return destinationName;
	}
	

	/**
	 * should be <code>QUEUE</code> or <code>TOPIC</code><br/>
	 * This function also sets the <code>useTopicFunctions</code> field,
	 * that controls wether Topic functions are used or Queue functions.
	 */
	public void setDestinationType(String type) {
		this.destinationType = type;
		if (destinationType.equalsIgnoreCase("TOPIC"))
			useTopicFunctions = true;
		else
			useTopicFunctions = false;
	}
    public String getDestinationType() {
		return destinationType;
	}

	/**
	 * Sets the JMS-acknowledge mode. This controls for non transacted listeners the way messages are acknowledged. 
	 * See the jms-documentation.
	 */
	public void setAckMode(int ackMode) {
		this.ackMode = ackMode;
	}
	public int getAckMode() {
		return ackMode;
	}

	/**
	 * Convencience function to convert the numeric value of an (@link #setAckMode(int) acknowledgeMode} to a human-readable string.
	 */
	public static String getAcknowledgeModeAsString(int ackMode) {
		String ackString;
		if (Session.AUTO_ACKNOWLEDGE == ackMode) {
			ackString = "Auto";
		} else
			if (Session.CLIENT_ACKNOWLEDGE == ackMode) {
				ackString = "Client";
			} else
				if (Session.DUPS_OK_ACKNOWLEDGE == ackMode) {
					ackString = "Dups";
				} else {
					ackString = "none";
				}
	
		return ackString;
	}
	
	/**
	 * String-version of {@link #setAckMode(int)}
	 */
	public void setAcknowledgeMode(String acknowledgeMode) {

		if (acknowledgeMode.equalsIgnoreCase("auto")) {
			ackMode = Session.AUTO_ACKNOWLEDGE;
		} else
			if (acknowledgeMode.equalsIgnoreCase("dups")) {
				ackMode = Session.DUPS_OK_ACKNOWLEDGE;
			} else
				if (acknowledgeMode.equalsIgnoreCase("client")) {
					ackMode = Session.CLIENT_ACKNOWLEDGE;
				} else {
					// ignore all ack modes, to test no acking
					log.warn("["+name+"] invalid acknowledgemode:[" + acknowledgeMode + "] setting no acknowledge");
					ackMode = -1;
				}

	}
	/**
	 * String-version of {@link #getAckMode()}
	 */
	public String getAcknowledgeMode() {
		return getAcknowledgeModeAsString(getAckMode());
	}


	/**
	 * Controls whether messages are processed persistently. 
	 * 
	 * When set <code>true</code>, the JMS provider ensures that messages aren't lost when the application might crash.  
	 */
	public void setPersistent(boolean value) {
		persistent = value;
	}
	public boolean getPersistent() {
		return persistent;
	}

	/**
	 * SubscriberType should <b>DURABLE</b> or <b>TRANSIENT</b>
	 * Only applicable for topics <br/>
	 */
	public void setSubscriberType(String subscriberType) {
		if ((!subscriberType.equalsIgnoreCase("DURABLE"))
			&& (!subscriberType.equalsIgnoreCase("TRANSIENT"))) {
			throw new IllegalArgumentException(
				"invalid subscriberType, should be DURABLE or TRANSIENT. "
					+ this.subscriberType
					+ " is assumed");
		} else
			this.subscriberType = subscriberType;
	}
	public String getSubscriberType() {
		return subscriberType;
	}


	/**
	 * <code>forceMQCompliancy</code> is used to perform MQ specific sending.
	 * If the MQ destination is not a JMS receiver, format errors occur.
	 * To prevent this, settting <code>forceMQCompliancy</code>  to MQ will inform
	 * MQ that the replyto queue is not JMS compliant. Setting <code>forceMQCompliancy</code>
	 * to "JMS" will cause that on mq the destination is identified as jms-compliant.
	 * Other specifics information for different providers may be
	 * implemented. Defaults to "JMS".<br/>
	 */
	public void setForceMQCompliancy(String forceMQCompliancy) {
		if ((!(forceMQCompliancy.equals("MQ")) && (!(forceMQCompliancy.equals("JMS")))))
			throw new IllegalArgumentException("forceMQCompliancy has a wrong value ["+forceMQCompliancy+"] should be JMS or MQ");
		this.forceMQCompliancy=forceMQCompliancy;
	}
	public String getForceMQCompliancy() {
		return forceMQCompliancy;
	}


	/**
	 * The JNDI-name of the connection factory to use to connect to a <i>queue</i> if {@link #isTransacted()} returns <code>false</code>.
	 * The corresponding connection factory should be configured not to support XA transactions. 
	 */
	public void setQueueConnectionFactoryName(String name) {
		queueConnectionFactoryName=name;
	}
	public String getQueueConnectionFactoryName() {
		return queueConnectionFactoryName;
	}

	/**
	 * The JNDI-name of the connection factory to use to connect to a <i>queue</i> if {@link #isTransacted()} returns <code>true</code>.
	 * The corresponding connection factory should support XA transactions. 
	 */
	public void setQueueConnectionFactoryNameXA(String queueConnectionFactoryNameXA) {
		this.queueConnectionFactoryNameXA = queueConnectionFactoryNameXA;
	}
	public String getQueueConnectionFactoryNameXA() {
		return queueConnectionFactoryNameXA;
	}


	/**
	 * The JNDI-name of the connection factory to use to connect to a <i>topic</i> if {@link #isTransacted()} returns <code>false</code>.
	 * The corresponding connection factory should be configured not to support XA transactions. 
	 */
	public void setTopicConnectionFactoryNameX(String topicConnectionFactoryName) {
		this.topicConnectionFactoryName = topicConnectionFactoryName;
	}
	public String getTopicConnectionFactoryName() {
		return topicConnectionFactoryName;
	}

	/**
	 * The JNDI-name of the connection factory to use to connect to a <i>topic</i> if {@link #isTransacted()} returns <code>true</code>.
	 * The corresponding connection factory should support XA transactions. 
	 */
	public void setTopicConnectionFactoryNameXA(String topicConnectionFactoryNameXA) {
		this.topicConnectionFactoryNameXA = topicConnectionFactoryNameXA;
	}
	public String getTopicConnectionFactoryNameXA() {
		return topicConnectionFactoryNameXA;
	}

	/**
	 * Controls the use of JMS transacted session.
	 * In versions prior to 4.1, this attribute was called plainly 'transacted'. The {@link #setTransacted(boolean) transacted} 
	 * attribute, however, is now in uses to indicate the use of XA-transactions. XA transactions can be used 
	 * in a pipeline to simultaneously (in one transaction) commit or rollback messages send to a number of queues, or
	 * even together with database actions.
	 * 
	 * @since 4.1
	 * 
	 * @deprecated This attribute has been added to provide the pre-4.1 transaction functionality to configurations that
	 * relied this specific functionality. New configurations should not use it. 
	 * 
	 */
	public void setJmsTransacted(boolean jmsTransacted) {
		this.jmsTransacted = jmsTransacted;
	}
	public boolean isJmsTransacted() {
		return jmsTransacted;
	}

	/**
	 * Controls whether messages are send under transaction control.
	 * If set <code>true</code>, messages are committed or rolled back under control of an XA-transaction.
	 */
	public void setTransacted(boolean transacted) {
		this.transacted = transacted;
	}
	/**
	 * Set the time-to-live in milliseconds of a message
	 * @param exp time in milliseconds
	 */
	public void setMessageTimeToLive(long exp){
		this.messageTimeToLive=exp;
	}
	/**
	 * Get the  time-to-live in milliseconds of a message
	 * @param exp time in milliseconds
	 */
	public long getMessageTimeToLive(){
		return this.messageTimeToLive;
	}
	/**
	 * Indicates whether messages are send under transaction control.
	 * @see #setTransacted(boolean)
	 */
	public boolean isTransacted() {
		return transacted;
	}

}
