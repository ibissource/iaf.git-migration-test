/*
   Copyright 2013 IbisSource Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
/*
 * $Log: IfsaFacade.java,v $
 * Revision 1.16  2011-11-30 13:51:44  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.14  2010/12/13 15:57:10  gerrit
 * restored setting of IFSA_MODE session
 *
 * Revision 1.13  2010/12/13 15:44:24  gerrit
 * enable ackmode setting in configuration
 *
 * Revision 1.12  2010/12/13 13:17:13  gerrit
 * made acknowledgemode configurable
 *
 * Revision 1.11  2010/03/22 11:08:13  peter
 * moved message logging from INFO level to DEBUG level
 *
 * Revision 1.10  2010/01/28 15:08:15  gerrit
 * renamed 'Connection' classes to 'MessageSource'
 *
 * Revision 1.9  2009/11/12 12:34:38  peter
 * prevent NullPointerException
 *
 * Revision 1.8  2008/10/06 14:30:36  gerrit
 * use JMS transacted sessions for FF
 *
 * Revision 1.7  2008/09/02 11:43:57  gerrit
 * close reply sender in finally clause
 *
 * Revision 1.6  2008/07/14 17:17:49  gerrit
 * added space after logprefix
 *
 * Revision 1.5  2008/05/22 07:23:35  gerrit
 * added serviceId to logPrefix of requester
 * added some support for bif and btc
 *
 * Revision 1.4  2008/03/27 12:00:14  gerrit
 * set default timeout to 20s
 *
 * Revision 1.3  2008/01/17 16:20:01  gerrit
 * never use jmsTransacted sessions
 *
 * Revision 1.2  2007/10/16 08:39:30  gerrit
 * moved IfsaException and IfsaMessageProtocolEnum back to main package
 *
 * Revision 1.1  2007/10/16 08:15:43  gerrit
 * introduced switch class for jms and ejb
 *
 * Revision 1.46  2007/09/05 15:48:07  gerrit
 * moved XA determination capabilities to IfsaConnection
 *
 * Revision 1.45  2007/08/10 11:11:16  gerrit
 * removed attribute 'transacted'
 * automatic determination of transaction state and capabilities
 *
 * Revision 1.44  2007/02/12 13:47:55  gerrit
 * Logger from LogUtil
 *
 * Revision 1.43  2007/02/05 14:56:29  gerrit
 * make isJmsTransacted() protected
 *
 * Revision 1.42  2006/11/06 08:15:30  gerrit
 * modifications for dynamic serviceId
 *
 * Revision 1.41  2006/10/13 08:08:45  gerrit
 * modify comments
 *
 * Revision 1.40  2006/08/21 15:08:03  gerrit
 * corrected javadoc
 *
 * Revision 1.39  2006/07/17 08:54:18  gerrit
 * documented custom property ifsa.provider.useSelectors
 *
 * Revision 1.38  2006/02/09 07:59:40  gerrit
 * restored compatibility with IFSA releases without provider selection mechanism
 *
 * Revision 1.37  2006/01/23 08:55:45  gerrit
 * use providerSelector (when available in ifsajms)
 *
 * Revision 1.36  2005/12/28 08:47:34  gerrit
 * improved logging
 *
 * Revision 1.35  2005/12/20 16:59:27  gerrit
 * implemented support for connection-pooling
 *
 * Revision 1.34  2005/11/02 09:08:05  gerrit
 * ifsa-mode connection not for single dynamic reply queue
 *
 * Revision 1.33  2005/10/26 08:23:57  gerrit
 * improved logging
 *
 * Revision 1.32  2005/10/24 15:10:13  gerrit
 * made sessionsArePooled configurable via appConstant 'jms.sessionsArePooled'
 *
 * Revision 1.31  2005/10/18 07:04:46  gerrit
 * better handling of dynamic reply queues
 *
 * Revision 1.30  2005/09/26 11:44:30  gerrit
 * Jms-commit only if not XA-transacted
 *
 * Revision 1.29  2005/09/13 15:48:00  gerrit
 * changed acknowledge mode back to AutoAcknowledge
 *
 * Revision 1.28  2005/08/31 16:32:16  gerrit
 * corrected code for static reply queues
 *
 * Revision 1.27  2005/07/28 07:31:25  gerrit
 * change default acknowledge mode to CLIENT
 *
 * Revision 1.26  2005/07/19 12:33:56  gerrit
 * implements IXAEnabled 
 * polishing of serviceIds, to work around problems with ':' and '/'
 *
 * Revision 1.25  2005/06/20 09:12:47  gerrit
 * set sessionsArePooled false by default
 *
 * Revision 1.24  2005/06/13 15:07:58  gerrit
 * avoid excessive logging in debug mode
 *
 * Revision 1.23  2005/06/13 11:59:00  gerrit
 * corrected version-string
 *
 * Revision 1.22  2005/06/13 11:57:44  gerrit
 * added support for pooled sessions and for XA-support
 *
 * Revision 1.21  2005/05/03 15:58:49  gerrit
 * rework of shared connection code
 *
 * Revision 1.20  2005/04/26 15:17:28  gerrit
 * rework, using IfsaApplicationConnection resulting in shared usage of connection objects
 *
 * Revision 1.19  2005/01/13 08:15:08  gerrit
 * made queue type IfsaQueue
 *
 * Revision 1.18  2004/08/23 13:12:25  gerrit
 * updated JavaDoc
 *
 * Revision 1.17  2004/08/09 08:46:07  gerrit
 * small changes
 *
 * Revision 1.16  2004/08/03 13:07:27  gerrit
 * improved closing
 *
 * Revision 1.15  2004/07/22 13:19:02  gerrit
 * let requestor receive IFSATimeOutMessages
 *
 * Revision 1.14  2004/07/22 11:01:04  gerrit
 * added configurable timeOut
 *
 * Revision 1.13  2004/07/20 16:37:47  gerrit
 * toch maar niet IFSA-mode timeout
 *
 * Revision 1.12  2004/07/20 13:28:07  gerrit
 * implemented IFSA timeout mode
 *
 * Revision 1.11  2004/07/19 13:20:20  gerrit
 * increased logging + close connection on 'close'
 *
 * Revision 1.10  2004/07/15 07:35:44  gerrit
 * cosmetic changes
 *
 * Revision 1.9  2004/07/08 12:55:57  gerrit
 * logging refinements
 *
 * Revision 1.8  2004/07/08 08:56:46  gerrit
 * show physical destination after configure
 *
 * Revision 1.7  2004/07/06 14:50:06  gerrit
 * included PhysicalDestination
 *
 * Revision 1.6  2004/07/05 14:29:45  gerrit
 * restructuring to align with IFSA naming scheme
 *
 */
package nl.nn.adapterframework.extensions.ifsa.jms;


import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.HasPhysicalDestination;
import nl.nn.adapterframework.core.INamedObject;
import nl.nn.adapterframework.core.IbisException;
import nl.nn.adapterframework.extensions.ifsa.IfsaException;
import nl.nn.adapterframework.extensions.ifsa.IfsaMessageProtocolEnum;
import nl.nn.adapterframework.util.AppConstants;
import nl.nn.adapterframework.util.JtaUtil;
import nl.nn.adapterframework.util.LogUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.ing.ifsa.IFSAConstants;
import com.ing.ifsa.IFSAMessage;
import com.ing.ifsa.IFSAQueue;
import com.ing.ifsa.IFSAQueueSender;
import com.ing.ifsa.IFSAServerQueueSender;
import com.ing.ifsa.IFSATextMessage;

/**
 * Base class for IFSA 2.0/2.2 functions.
 * <br/>
 * <p>Descenderclasses must set either Requester or Provider behaviour in their constructor.</p>
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.extensions.ifsa.IfsaFacade</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the object</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setApplicationId(String) applicationId}</td><td>the ApplicationID, in the form of "IFSA://<i>AppId</i>"</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setServiceId(String) serviceId}</td><td>only for Requesters: the ServiceID, in the form of "IFSA://<i>ServiceID</i>"</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMessageProtocol(String) messageProtocol}</td><td>protocol of IFSA-Service to be called. Possible values 
 * <ul>
 *   <li>"FF": Fire & Forget protocol</li>
 *   <li>"RR": Request-Reply protocol</li>
 * </ul></td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setTransacted(boolean) transacted}</td><td>must be set <code>true</true> for FF senders/listeners in transacted mode</td><td>false</td></tr>
 * <tr><td>{@link #setTimeOut(long) timeOut}</td><td>receiver timeout, in milliseconds. To use the timeout defined as IFSA expiry, set this value to -1</td><td>20000 (20s)</td></tr>
 * </table>
 * 
 * N.B. 
 * Starting from IFSA-jms version 2.2.10.055(beta) a feature was created to have separate service-queues for Request/Reply
 * and for Fire & Forget services. This allows applications to provide both types of services, each in its own transaction
 * mode. This options is not compatible with earlier versions of IFSA-jms. If an earlier version of IFSA-jms is deployed on 
 * the server, this behaviour must be disabled by the following setting in DeploymentSpecifics.properties:
 * 
 * <code>    ifsa.provider.useSelectors=false</code>
 * 
 * @author Johan Verrips / Gerrit van Brakel
 * @since 4.2
 * @version $Id$
 */
public class IfsaFacade implements INamedObject, HasPhysicalDestination {
    protected Logger log = LogUtil.getLogger(this);
    
 	private final static String USE_SELECTOR_FOR_PROVIDER_KEY="ifsa.provider.useSelectors";
 	private final static int DEFAULT_PROVIDER_ACKNOWLEDGMODE_RR=Session.CLIENT_ACKNOWLEDGE;
 	private final static int DEFAULT_PROVIDER_ACKNOWLEDGMODE_FF=Session.AUTO_ACKNOWLEDGE;
 	private final static int DEFAULT_REQUESTER_ACKNOWLEDGMODE_RR=Session.AUTO_ACKNOWLEDGE;
 	private final static int DEFAULT_REQUESTER_ACKNOWLEDGMODE_FF=Session.AUTO_ACKNOWLEDGE;
 	
	private static Boolean useSelectorsStore=null; 

    private int ackMode = -1;
   
	private String name;
	private String applicationId;
	private String serviceId;
	private String polishedServiceId=null;;
	private IfsaMessageProtocolEnum messageProtocol;

	private long timeOut = 20000; // when set less than zero the IFSA-expiry will be used

    private IFSAQueue queue;

	private IfsaMessagingSource messagingSource=null;
	
	private boolean requestor=false;
	private boolean provider=false;
		
	private String providerSelector=null;

	public IfsaFacade(boolean asProvider) {
		super();
		if (asProvider) {
			provider=true;
		}
		else
			requestor=true;
	}
	
	protected String getLogPrefix() {
		
		String objectType;
		String serviceInfo="";
		try {
			if (isRequestor()) {
				objectType = "IfsaRequester";
				serviceInfo = "of Application ["+getApplicationId()+"] "+(polishedServiceId!=null?"to Service ["+polishedServiceId+"] ":""); 
			} else {
				objectType = "IfsaProvider";				
				serviceInfo = "for Application ["+getApplicationId()+"] "; 
			} 
		} catch (IfsaException e) {
			log.debug("Exception determining objectType in getLogPrefix",e);
			objectType="Object";
			serviceInfo = "of Application ["+getApplicationId()+"]"; 
		}
		
		return objectType + "["+ getName()+ "] " + serviceInfo;  
	}

	/**
	 * Checks if messageProtocol and serviceId (only for Requestors) are specified
	 */
	public void configure() throws ConfigurationException {

		// perform some basic checks
		if (StringUtils.isEmpty(getApplicationId())) {
			throw new ConfigurationException(getLogPrefix()+"applicationId is not specified");
		}
		if (getMessageProtocolEnum() == null) {
			throw new ConfigurationException(getLogPrefix()+
				"invalid messageProtocol specified ["
					+ getMessageProtocolEnum()
					+ "], should be one of the following "
					+ IfsaMessageProtocolEnum.getNames());
		}
		try {
			if (getAckMode()<0) {
				if (getMessageProtocolEnum()==IfsaMessageProtocolEnum.FIRE_AND_FORGET) {
					if (isRequestor()) {
						setAckMode(DEFAULT_REQUESTER_ACKNOWLEDGMODE_FF);
					} else {
						setAckMode(DEFAULT_PROVIDER_ACKNOWLEDGMODE_FF);
					}
				} else if (getMessageProtocolEnum()==IfsaMessageProtocolEnum.REQUEST_REPLY) {
					if (isRequestor()) {
						setAckMode(DEFAULT_REQUESTER_ACKNOWLEDGMODE_RR);
					} else {
						setAckMode(DEFAULT_PROVIDER_ACKNOWLEDGMODE_RR);
					}
				} else {
					throw new ConfigurationException(getLogPrefix()+"illegal messageProtocol");
				}
			}
		} catch(IfsaException e) {
			throw new ConfigurationException(getLogPrefix()+"cannot set acknowledgemode",e);
		}
		// TODO: check if serviceId is specified, either as attribute or as parameter
//		try {
//			log.debug(getLogPrefix()+"opening connection for service, to obtain info about XA awareness");
//			getConnection();   // obtain and cache connection, then start it.
//			closeService();
//		} catch (IfsaException e) {
//			cleanUpAfterException();
//			throw new ConfigurationException(e);
//		}
	}

	protected void cleanUpAfterException() {
		try {
			closeService();
		} catch (IfsaException e) {
			log.warn("exception closing ifsaConnection after previous exception, current:",e);
		}
	}

	/** 
	 * Prepares object for communication on the IFSA bus.
	 * Obtains a connection and a serviceQueue.
	 */
	public void openService() throws IfsaException {
		try {
			log.debug(getLogPrefix()+"opening connection for service");
			getMessagingSource();
			getServiceQueue(); // obtain and cache service queue
		} catch (IfsaException e) {
			cleanUpAfterException();
			throw e;
		}
	}

	/** 
	 * Stops communication on the IFSA bus.
	 * Releases references to serviceQueue and connection.
	 */
	public void closeService() throws IfsaException {
	    try {
	        if (messagingSource != null) {
	            try {
					messagingSource.close();
				} catch (IbisException e) {
					if (e instanceof IfsaException) {
						throw (IfsaException)e;
					}
					throw new IfsaException(e);
	            }
                log.debug(getLogPrefix()+"closed connection for service");
	        }
	    } finally {
	    	// make sure all objects are reset, to be able to restart after IFSA parameters have changed (e.g. at iterative installation time)
	        queue = null;
			messagingSource = null;
	    }
	}
	

	/**
	 * Looks up the <code>serviceId</code> in the <code>IFSAContext</code>.<br/>
	 * <p>The method is knowledgable of Provider versus Requester processing.
	 * When the request concerns a Provider <code>lookupProviderInput</code> is used,
	 * when it concerns a Requester <code>lookupService(serviceId)</code> is used.
	 * This method distinguishes a server-input queue and a client-input queue
	 */
	protected IFSAQueue getServiceQueue() throws IfsaException {
		if (queue == null) {
			if (isRequestor()) {
				if (getServiceId() != null) {
					queue = getMessagingSource().lookupService(getServiceId());
					if (log.isDebugEnabled()) {
						log.info(getLogPrefix()+ "got Queue to send messages on "+getPhysicalDestinationName());
					}
				}
			} else {
				queue = getMessagingSource().lookupProviderInput();
				if (log.isDebugEnabled()) {
					log.info(getLogPrefix()+ "got Queue to receive messages from "+getPhysicalDestinationName());
				}
			}
		}
		return queue;
	}

	protected IfsaMessagingSource getMessagingSource() throws IfsaException {
		if (messagingSource == null) {
			synchronized (this) {
				if (messagingSource == null) {
					log.debug(getLogPrefix()+"instantiating IfsaConnectionFactory");
					IfsaMessagingSourceFactory ifsaConnectionFactory = new IfsaMessagingSourceFactory();
					try {
						log.debug(getLogPrefix()+"creating IfsaConnection");
						messagingSource = (IfsaMessagingSource)ifsaConnectionFactory.getConnection(getApplicationId());
					} catch (IbisException e) {
						if (e instanceof IfsaException) {
							throw (IfsaException)e;
						}
						throw new IfsaException(e);
					}
				}
			}
		}
		return messagingSource;
	}
	
	/**
	 *  Create a session on the connection to the service
	 */
	protected QueueSession createSession() throws IfsaException {
		try {
			int mode = getAckMode(); 
			if (isRequestor() && messagingSource.canUseIfsaModeSessions()) {
				mode += IFSAConstants.QueueSession.IFSA_MODE; // let requestor receive IFSATimeOutMessages
			}
			return (QueueSession) messagingSource.createSession(isJmsTransacted(), mode);
		} catch (IbisException e) {
			if (e instanceof IfsaException) {
				throw (IfsaException)e;
			}
			throw new IfsaException(e);
		}
	}

	protected void closeSession(Session session) {
		try {
			getMessagingSource().releaseSession(session);
		} catch (IfsaException e) {
			log.warn("Exception releasing session", e);
		}
	}

	
	protected QueueSender createSender(QueueSession session, Queue queue)
	    throws IfsaException {
	
	    try {
	        QueueSender queueSender = session.createSender(queue);
	        if (log.isDebugEnabled()) {
	            log.debug(getLogPrefix()+ "got queueSender ["
	                            + ToStringBuilder.reflectionToString((IFSAQueueSender) queueSender)+ "]");
	        }
	        return queueSender;
	    } catch (Exception e) {
	        throw new IfsaException(e);
	    }
	}

	protected synchronized String getProviderSelector() {
		if (providerSelector==null && useSelectorsForProviders()) {
			try {
				providerSelector=""; // set default, also to avoid re-evaluation time and time again for lower ifsa-versions.
				if (messageProtocol.equals(IfsaMessageProtocolEnum.REQUEST_REPLY)) {
					providerSelector=IFSAConstants.QueueReceiver.SELECTOR_RR;
				}
				if (messageProtocol.equals(IfsaMessageProtocolEnum.FIRE_AND_FORGET)) {
					providerSelector=IFSAConstants.QueueReceiver.SELECTOR_FF;
				}
			} catch (Throwable t) {
				log.debug(getLogPrefix()+"exception determining selector, probably lower ifsa version, ignoring");
			}
		}
		return providerSelector;
	}

	/**
	 * Gets the queueReceiver, by utilizing the <code>getInputQueue()</code> method.<br/>
	 * For serverside getQueueReceiver() the creating of the QueueReceiver is done
	 * without the <code>selector</code> information, as this is not allowed
	 * by IFSA.<br/>
	 * For a clientconnection, the receiver is done with the <code>getClientReplyQueue</code>
	 * @see javax.jms.QueueReceiver
	 */
	protected QueueReceiver getServiceReceiver(
		QueueSession session)
		throws IfsaException {
	
		try {
			QueueReceiver queueReceiver;
			    
			if (isProvider()) {
				String selector = getProviderSelector();			
				if (StringUtils.isEmpty(selector)) {
					queueReceiver = session.createReceiver(getServiceQueue());
				} else {
					//log.debug(getLogPrefix()+"using selector ["+selector+"]");
					try {
						queueReceiver = session.createReceiver(getServiceQueue(), selector);
					} catch (JMSException e) {
						log.warn("caught exception, probably due to use of selector ["+selector+"], falling back to non-selected mode",e);
						queueReceiver = session.createReceiver(getServiceQueue());
					}
				}
			} else {
				throw new IfsaException("cannot obtain ServiceReceiver: Requestor cannot act as Provider");
			}
			if (log.isDebugEnabled() && !isSessionsArePooled()) {
				log.debug(getLogPrefix()+ "got receiver for queue ["
						+ queueReceiver.getQueue().getQueueName()
						+ "] "+ ToStringBuilder.reflectionToString(queueReceiver));
			}
			return queueReceiver;
		} catch (JMSException e) {
			throw new IfsaException(e);
		}
	}
	
	public long getExpiry() throws IfsaException {
		return getExpiry((IFSAQueue) getServiceQueue());
	}
	
	public long getExpiry(IFSAQueue queue) throws IfsaException {
		long expiry = getTimeOut();
		if (expiry>=0) {
			return expiry;
		}
		try {
			return queue.getExpiry();
		} catch (JMSException e) {
			throw new IfsaException("error retrieving timeOut value", e);
		}
	}

    public String getMessageProtocol() {
		if (messageProtocol==null) {
			return null;
		} else {
			return messageProtocol.getName();
		}
    }
    public IfsaMessageProtocolEnum getMessageProtocolEnum() {
        return messageProtocol;
    }
    
	/**
	 * Gets the queueReceiver, by utilizing the <code>getInputQueue()</code> method.<br/>
	 * For serverside getQueueReceiver() the creating of the QueueReceiver is done
	 * without the <code>selector</code> information, as this is not allowed
	 * by IFSA.<br/>
	 * For a clientconnection, the receiver is done with the <code>getClientReplyQueue</code>
	 */
	public QueueReceiver getReplyReceiver(QueueSession session, Message sentMessage)
	    throws IfsaException {
		    
	    if (isProvider()) {
	        throw new IfsaException("cannot get ReplyReceiver: Provider cannot act as Requestor");
	    } 
	
		return getMessagingSource().getReplyReceiver(session, sentMessage);
	}

	public void closeReplyReceiver(QueueReceiver receiver) throws IfsaException {
		log.debug(getLogPrefix()+"closing replyreceiver");
		getMessagingSource().closeReplyReceiver(receiver);
	}
	
	/**
	 * Indicates whether the object at hand represents a Client (returns <code>True</code>) or
	 * a Server (returns <code>False</code>).
	 */
	public boolean isRequestor() throws IfsaException {
			
		if (requestor && provider) {
	        throw new IfsaException("cannot be both Requestor and Provider");
		}
		if (!requestor && !provider) {
	        throw new IfsaException("not configured as Requestor or Provider");
		}
		return requestor;
	}
	/**
	 * Indicates whether the object at hand represents a Client (returns <code>False</code>) or
	 * a Server (returns <code>True</code>).
	 *
	 * @see #isRequestor()
	 */
	public boolean isProvider() throws IfsaException {
		return ! isRequestor();
	}
    /**
     * Sends a message,and if transacted, the queueSession is committed.
     * <p>This method is intended for <b>clients</b>, as <b>server</b>s
     * will use the <code>sendReply</code>.
     * @return the correlationID of the sent message
     */
    public TextMessage sendMessage(QueueSession session, QueueSender sender, String message, Map udzMap, String bifName, byte btcData[])
        throws IfsaException {

	    try {
			if (!isRequestor()) {
				throw new IfsaException(getLogPrefix()+ "Provider cannot use sendMessage, should use sendReply");
			}
	        IFSATextMessage msg = (IFSATextMessage)session.createTextMessage();
	        msg.setText(message);
			if (udzMap != null && msg instanceof IFSAMessage) {
				// Handle UDZs
				log.debug(getLogPrefix()+"add UDZ map to IFSAMessage");
				// process the udzMap
				Map udzObject = (Map)((IFSAMessage) msg).getOutgoingUDZObject();
				udzObject.putAll(udzMap);
			}
			String replyToQueueName="-"; 
	        //Client side
	        if (messageProtocol.equals(IfsaMessageProtocolEnum.REQUEST_REPLY)) {
	            // set reply-to address
	            Queue replyTo=getMessagingSource().getClientReplyQueue(session);
	            msg.setJMSReplyTo(replyTo);
	            replyToQueueName=replyTo.getQueueName();
	        }
	        if (messageProtocol.equals(IfsaMessageProtocolEnum.FIRE_AND_FORGET)) {
	         	// not applicable
	        }
			if (StringUtils.isNotEmpty(bifName)) {
				msg.setBifName(bifName);
			}
			if (btcData!=null && btcData.length>0) {
				msg.setBtcData(btcData);
			}
	
			if (log.isDebugEnabled()) {
				log.debug(getLogPrefix()
						+ " messageProtocol ["
						+ messageProtocol
						+ "] replyToQueueName ["
						+ replyToQueueName
						+ "] sending message ["
						+ message
						+ "]");
			} else {
				if (log.isInfoEnabled()) {
					log.info(getLogPrefix()
							+ " messageProtocol ["
							+ messageProtocol
							+ "] replyToQueueName ["
							+ replyToQueueName
							+ "] sending message");
				}
			}

	        // send the message
	        sender.send(msg);
	
	        // perform commit
	        if (isJmsTransacted() && !(messagingSource.isXaEnabledForSure() && JtaUtil.inTransaction())) {
	            session.commit();
	            log.debug(getLogPrefix()+ "committing (send) transaction");
	        }
	
	        return msg;
		    
	 	} catch (Exception e) {
			throw new IfsaException(e);
		}
	}
	
	/**
	 * Intended for server-side reponse sending and implies that the received
	 * message *always* contains a reply-to address.
	 */
	public void sendReply(QueueSession session, Message received_message, String response) throws IfsaException {
		QueueSender tqs=null;
	    try {
	        TextMessage answer = session.createTextMessage();
	        answer.setText(response);
			Queue replyQueue = (Queue)received_message.getJMSReplyTo();
	        tqs = session.createSender(replyQueue );
	        if (log.isDebugEnabled()) log.debug(getLogPrefix()+ "sending reply to ["+ received_message.getJMSReplyTo()+ "]");
	        ((IFSAServerQueueSender) tqs).sendReply(received_message, answer);
	    } catch (Throwable t) {
	        throw new IfsaException(t);
	    } finally {
	    	if (tqs!=null) {
				try {
					tqs.close();
				} catch (JMSException e) {
					log.warn(getLogPrefix()+ "exception closing reply queue sender",e);
				}	
	    	}
	    }
	}

    /**
     * Method logs a warning when the newMessageProtocol is not FF or RR.
     * <p>When the messageProtocol equals to FF, transacted is set to true</p>
     * <p>Creation date: (08-05-2003 9:03:53)</p>
     * @see IfsaMessageProtocolEnum
     * @param newMessageProtocol String
     */
    public void setMessageProtocol(String newMessageProtocol) {
	    if (null==IfsaMessageProtocolEnum.getEnum(newMessageProtocol)) {
        	throw new IllegalArgumentException(getLogPrefix()+
                "illegal messageProtocol ["
                    + newMessageProtocol
                    + "] specified, it should be one of the values "
                    + IfsaMessageProtocolEnum.getNames());

        	}
        messageProtocol = IfsaMessageProtocolEnum.getEnum(newMessageProtocol);
        log.debug(getLogPrefix()+"message protocol set to "+messageProtocol.getName());
    }
 
	public boolean isSessionsArePooled() {
		try {
			return getMessagingSource().sessionsArePooled();
		} catch (IfsaException e) {
			log.error(getLogPrefix()+"could not get session",e);
			return false;
		}
	}
    
    /**
     * controls whether sessions are created in JMS transacted mode. JMS transacted sessions
     * are required by IFSA for FF, although they result in log messages about active transactions
     * that should be present.
     */
    protected boolean isJmsTransacted() {
		return getMessageProtocolEnum().equals(IfsaMessageProtocolEnum.FIRE_AND_FORGET);
    }
    
	public String toString() {
	    String result = super.toString();
	    ToStringBuilder ts = new ToStringBuilder(this);
		ts.append("applicationId", applicationId);
	    ts.append("serviceId", serviceId);
	    if (messageProtocol != null) {
			ts.append("messageProtocol", messageProtocol.getName());
//			ts.append("transacted", isTransacted());
			ts.append("jmsTransacted", isJmsTransacted());
	    }
	    else
	        ts.append("messageProtocol", "null!");
	
	    result += ts.toString();
	    return result;
	
	}

	public String getPhysicalDestinationName() {
	
		String result = null;
	
		try {
			if (isRequestor()) {
				result = getServiceId();
			} else {
				result = getApplicationId();
			}
			log.debug("obtaining connection and servicequeue for "+result);
			if (getMessagingSource()!=null && getServiceQueue() != null) {
				result += " ["+ getServiceQueue().getQueueName()+"]";
			}
		} catch (Throwable t) {
			log.warn(getLogPrefix()+"got exception in getPhysicalDestinationName", t);
		}
		try {
			result+=" on "+getMessagingSource().getPhysicalName();
		} catch (Exception e) {
			log.warn("[" + name + "] got exception in messagingSource.getPhysicalName", e);
		}
		return result;
	}


	/**
	 * set the IFSA service Id, for requesters only
	 * @param newServiceId the name of the service, e.g. IFSA://SERVICE/CLAIMINFORMATIONMANAGEMENT/NLDFLT/FINDCLAIM:01
	 */
	public void setServiceId(String newServiceId) {
		serviceId = newServiceId;
	}

	public String getServiceId() {
		if (polishedServiceId==null && serviceId!=null) {
			try {
				IfsaMessagingSource messagingSource = getMessagingSource();
				polishedServiceId = messagingSource.polishServiceId(serviceId);
			} catch (IfsaException e) {
				log.warn("could not obtain connection, no polishing of serviceId",e);
				polishedServiceId = serviceId;
			}
		}
		return polishedServiceId;
	}


	public void setApplicationId(String newApplicationId) {
		applicationId = newApplicationId;
	}
	public String getApplicationId() {
		return applicationId;
	}

	protected synchronized boolean useSelectorsForProviders() {
		if (useSelectorsStore==null) {
			boolean pooled=AppConstants.getInstance().getBoolean(USE_SELECTOR_FOR_PROVIDER_KEY, true);
			useSelectorsStore = new Boolean(pooled);
		}
		return useSelectorsStore.booleanValue();
	}


	public void setName(String newName) {
		name = newName;
	}
	public String getName() {
		return name;
	}

	public long getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public void setAckMode(int ackMode) {
		this.ackMode = ackMode;
	}
	public int getAckMode() {
		return ackMode;
	}
	public void setAcknowledgeMode(String acknowledgeMode) {

		if (acknowledgeMode.equalsIgnoreCase("auto") || acknowledgeMode.equalsIgnoreCase("AUTO_ACKNOWLEDGE")) {
			ackMode = Session.AUTO_ACKNOWLEDGE;
		} else
			if (acknowledgeMode.equalsIgnoreCase("dups") || acknowledgeMode.equalsIgnoreCase("DUPS_OK_ACKNOWLEDGE")) {
				ackMode = Session.DUPS_OK_ACKNOWLEDGE;
			} else
				if (acknowledgeMode.equalsIgnoreCase("client") || acknowledgeMode.equalsIgnoreCase("CLIENT_ACKNOWLEDGE")) {
					ackMode = Session.CLIENT_ACKNOWLEDGE;
				} else {
					// ignore all ack modes, to test no acking
					log.warn("["+name+"] invalid acknowledgemode:[" + acknowledgeMode + "] setting no acknowledge");
					ackMode = -1;
				}

	}
}
