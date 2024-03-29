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
 * $Log: PullingIfsaProviderListener.java,v $
 * Revision 1.8  2012-06-01 10:52:56  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.7  2011/11/30 13:51:43  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.5  2011/06/20 13:18:41  gerrit
 * Java 5.0 compatibility
 *
 * Revision 1.4  2010/01/28 15:05:14  gerrit
 * renamed 'Connection' classes to 'MessageSource'
 *
 * Revision 1.3  2008/10/06 14:31:14  gerrit
 * encode contents of poisonmessage
 *
 * Revision 1.2  2008/02/28 16:20:38  gerrit
 * use PipeLineSession.setListenerParameters()
 *
 * Revision 1.1  2008/01/03 15:46:19  gerrit
 * split IfsaProviderListener into a Pulling and a Pushing version
 *
 * Revision 1.5  2007/11/21 13:17:47  gerrit
 * improved error logging
 *
 * Revision 1.4  2007/11/15 12:38:08  gerrit
 * fixed message wrapping
 *
 * Revision 1.3  2007/10/17 09:32:49  gerrit
 * store originalRawMessage when wrapper is created, use it to send reply
 *
 * Revision 1.2  2007/10/16 08:39:30  gerrit
 * moved IfsaException and IfsaMessageProtocolEnum back to main package
 *
 * Revision 1.1  2007/10/16 08:15:43  gerrit
 * introduced switch class for jms and ejb
 *
 * Revision 1.33  2007/10/03 08:32:41  gerrit
 * changed HashMap to Map
 *
 * Revision 1.32  2007/09/25 11:33:14  gerrit
 * show headers of incoming messages
 *
 * Revision 1.31  2007/09/13 09:12:43  gerrit
 * move message wrapper from ifsa to receivers
 *
 * Revision 1.30  2007/09/05 15:48:07  gerrit
 * moved XA determination capabilities to IfsaConnection
 *
 * Revision 1.29  2007/08/27 11:50:18  gerrit
 * provide default result for RR
 *
 * Revision 1.28  2007/08/10 11:18:50  gerrit
 * removed attribute 'transacted'
 * automatic determination of transaction state and capabilities
 * removed (more or less hidden) attribute 'commitOnState'
 * warning about non XA FF
 *
 * Revision 1.27  2007/02/16 14:19:08  gerrit
 * updated javadoc
 *
 * Revision 1.26  2007/02/05 14:57:00  gerrit
 * set default timeout to 3000
 *
 * Revision 1.25  2006/11/01 14:22:42  gerrit
 * avoid NPE for null commitOnState
 *
 * Revision 1.24  2006/10/13 08:23:59  gerrit
 * do not process null UDZ
 *
 * Revision 1.23  2006/10/13 08:11:30  gerrit
 * copy UDZ to session-variables
 *
 * Revision 1.22  2006/08/21 15:08:35  gerrit
 * corrected javadoc
 *
 * Revision 1.21  2006/07/17 08:54:18  gerrit
 * documented custom property ifsa.provider.useSelectors
 *
 * Revision 1.20  2006/03/08 13:55:49  gerrit
 * getRawMessage now returns null again if no message received if transacted, 
 * to avoid transaction time out
 *
 * Revision 1.19  2006/02/20 15:49:54  gerrit
 * improved handling of PoisonMessages, should now work under transactions control
 *
 * Revision 1.18  2006/01/05 13:55:27  gerrit
 * updated javadoc
 *
 * Revision 1.17  2005/12/20 16:59:27  gerrit
 * implemented support for connection-pooling
 *
 * Revision 1.16  2005/10/27 08:48:31  gerrit
 * introduced RunStateEnquiries
 *
 * Revision 1.15  2005/10/24 15:14:02  gerrit
 * shuffled positions of methods
 *
 * Revision 1.14  2005/09/26 11:47:26  gerrit
 * Jms-commit only if not XA-transacted
 * ifsa-messageWrapper for (non-serializable) ifsa-messages
 *
 * Revision 1.13  2005/09/13 15:48:27  gerrit
 * changed acknowledge mode back to AutoAcknowledge
 *
 * Revision 1.12  2005/07/28 07:31:54  gerrit
 * change default acknowledge mode to CLIENT
 *
 * Revision 1.11  2005/06/20 09:14:17  gerrit
 * avoid excessive logging
 *
 * Revision 1.10  2005/06/13 15:08:37  gerrit
 * avoid excessive logging in debug mode
 *
 * Revision 1.9  2005/06/13 12:43:03  gerrit
 * added support for pooled sessions and for XA-support
 *
 * Revision 1.8  2005/02/17 09:45:30  gerrit
 * increased logging
 *
 * Revision 1.7  2005/01/13 08:55:37  gerrit
 * Make threadContext-attributes available in PipeLineSession
 *
 * Revision 1.6  2004/09/22 07:03:36  johan
 * Added logstatements for closing receiver and session
 *
 * Revision 1.5  2004/09/22 06:48:08  johan
 * Changed loglevel in getStringFromRawMessage to warn
 *
 * Revision 1.4  2004/07/19 09:50:03  gerrit
 * try to send exceptionmessage as reply when sending reply results in exception
 *
 * Revision 1.3  2004/07/15 07:43:04  gerrit
 * updated javadoc
 *
 * Revision 1.2  2004/07/08 12:55:57  gerrit
 * logging refinements
 *
 * Revision 1.1  2004/07/05 14:28:38  gerrit
 * First version, converted from IfsaServiceListener
 *
 * Revision 1.4  2004/03/26 07:25:42  johan
 * Updated erorhandling
 *
 * Revision 1.3  2004/03/24 15:27:24  gerrit
 * solved uncaught exception in error message
 *
 */
package nl.nn.adapterframework.extensions.ifsa.jms;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IMessageWrapper;
import nl.nn.adapterframework.core.INamedObject;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.IPullingListener;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.core.PipeLineResult;
import nl.nn.adapterframework.core.PipeLineSessionBase;
import nl.nn.adapterframework.extensions.ifsa.IfsaException;
import nl.nn.adapterframework.extensions.ifsa.IfsaMessageProtocolEnum;
import nl.nn.adapterframework.receivers.MessageWrapper;
import nl.nn.adapterframework.util.ClassUtils;
import nl.nn.adapterframework.util.DateUtils;
import nl.nn.adapterframework.util.JtaUtil;
import nl.nn.adapterframework.util.RunStateEnquirer;
import nl.nn.adapterframework.util.RunStateEnquiring;
import nl.nn.adapterframework.util.RunStateEnum;
import nl.nn.adapterframework.util.XmlUtils;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.ing.ifsa.IFSAHeader;
import com.ing.ifsa.IFSAMessage;
import com.ing.ifsa.IFSAPoisonMessage;
import com.ing.ifsa.IFSAServiceName;
import com.ing.ifsa.IFSAServicesProvided;
import com.ing.ifsa.IFSATextMessage;

/**
 * Implementation of {@link IPullingListener} that acts as an IFSA-service.
 * 
 * There is no need or possibility to set the ServiceId as the Provider will receive all messages
 * for this Application on the same serviceQueue.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>className</td><td>nl.nn.adapterframework.extensions.ifsa.IfsaProviderListener</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the object</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setApplicationId(String) applicationId}</td><td>the ApplicationID, in the form of "IFSA://<i>AppId</i>"</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMessageProtocol(String) messageProtocol}</td><td>protocol of IFSA-Service to be called. Possible values 
 * <ul>
 *   <li>"FF": Fire & Forget protocol</li>
 *   <li>"RR": Request-Reply protocol</li>
 * </ul></td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setTransacted(boolean) transacted}</td><td>must be set <code>true</true> for FF listeners in transacted mode</td><td>false</td></tr>
 * <tr><td>{@link #setTimeOut(long) timeOut}</td><td>receiver timeout, in milliseconds</td><td>3000</td></tr>
 * </table>
 * The following session keys are set for each message:
 * <ul>
 *   <li>id (the message id)</li>
 *   <li>cid (the correlation id)</li>
 *   <li>timestamp</li>
 *   <li>replyTo</li>
 *   <li>messageText</li>
 *   <li>fullIfsaServiceName</li>
 *   <li>ifsaServiceName</li>
 *   <li>ifsaGroup</li>
 *   <li>ifsaOccurrence</li>
 *   <li>ifsaVersion</li>
 * </ul>
 * N.B. 
 * Starting from IFSA-jms version 2.2.10.055(beta) a feature was created to have separate service-queues for Request/Reply
 * and for Fire & Forget services. This allows applications to provide both types of services, each in its own transaction
 * mode. This options is not compatible with earlier versions of IFSA-jms. If an earlier version of IFSA-jms is deployed on 
 * the server, this behaviour must be disabled by the following setting in DeploymentSpecifics.properties:
 * 
 * <code>ifsa.provider.useSelectors=false</code>
 * 
 * @author  Gerrit van Brakel
 * @since   4.2
 * @version $Id$
 */
public class PullingIfsaProviderListener extends IfsaFacade implements IPullingListener, INamedObject, RunStateEnquiring {

    private final static String THREAD_CONTEXT_SESSION_KEY = "session";
    private final static String THREAD_CONTEXT_RECEIVER_KEY = "receiver";
	private final static String THREAD_CONTEXT_ORIGINAL_RAW_MESSAGE_KEY = "originalRawMessage";
	private RunStateEnquirer runStateEnquirer=null;

	public PullingIfsaProviderListener() {
		super(true); //instantiate as a provider
		setTimeOut(3000); // set default timeout, to be able to stop adapter!
	}


	protected QueueSession getSession(Map threadContext) throws ListenerException {
		if (isSessionsArePooled()) {
			try {
				return createSession();
			} catch (IfsaException e) {
				throw new ListenerException(getLogPrefix()+"exception creating QueueSession", e);
			}
		} 
		return (QueueSession) threadContext.get(THREAD_CONTEXT_SESSION_KEY);
	}
	
	protected void releaseSession(Session session) throws ListenerException {
		if (isSessionsArePooled()) {
			closeSession(session);
		}
	}

	protected QueueReceiver getReceiver(Map threadContext, QueueSession session) throws ListenerException {
		if (isSessionsArePooled()) {
			try {
				return getServiceReceiver(session);
			} catch (IfsaException e) {
				throw new ListenerException(getLogPrefix()+"exception creating QueueReceiver", e);
			}
		} 
		return (QueueReceiver) threadContext.get(THREAD_CONTEXT_RECEIVER_KEY);
	}
	
	protected void releaseReceiver(QueueReceiver receiver) throws ListenerException {
		if (isSessionsArePooled() && receiver != null) {
			try {
				receiver.close();
				// do not write to log, this occurs too often
			} catch (Exception e) {
				throw new ListenerException(getLogPrefix()+"exception closing QueueReceiver", e);
			}
		}
	}
	
	
	public void configure() throws ConfigurationException {
		super.configure();
//		if (IfsaMessageProtocolEnum.FIRE_AND_FORGET.equals(getMessageProtocolEnum())) {
//			if (!isXaEnabledForSure()) {
//				if (isNotXaEnabledForSure()) {
//					log.warn(getLogPrefix()+"The installed IFSA libraries do not have XA enabled. Transaction integrity cannot be fully guaranteed");
//				} else {
//					log.warn(getLogPrefix()+"XA-support of the installed IFSA libraries cannot be determined. It is assumed XA is NOT enabled. Transaction integrity cannot be fully guaranteed");
//				}
//			}
//		}
	}



	public void open() throws ListenerException {
		try {
			openService();
			
			IFSAServicesProvided services = getServiceQueue().getIFSAServicesProvided();

			for (int i = 0; i < services.getNumberOfServices(); i++) {
				IFSAServiceName service = services.getService(i);
				
				String protocol=(service.IsFireAndForgetService() ? "Fire and Forget" : "Request/Reply");
				log.info(getLogPrefix()+"providing ServiceName ["+service.getServiceName()+"] ServiceGroup [" + service.getServiceGroup()+"] protocol [" + protocol+"] ServiceVersion [" + service.getServiceVersion()+"]");				
			}
		} catch (Exception e) {
			throw new ListenerException(getLogPrefix(),e);
		}
	}
	
	public Map openThread() throws ListenerException {
		Map threadContext = new HashMap();
	
		try {
			if (!isSessionsArePooled()) {
				QueueSession session = createSession();
				threadContext.put(THREAD_CONTEXT_SESSION_KEY, session);
	
				QueueReceiver receiver;
				receiver = getServiceReceiver(session);
				threadContext.put(THREAD_CONTEXT_RECEIVER_KEY, receiver);
			}
			return threadContext;
		} catch (IfsaException e) {
			throw new ListenerException(getLogPrefix()+"exception in openThread()", e);
		}
	}

	public void close() throws ListenerException {
		try {
			closeService();
		} catch (IfsaException e) {
			throw new ListenerException(getLogPrefix(),e);
		}
	}
	
	public void closeThread(Map threadContext) throws ListenerException {
	
		if (!isSessionsArePooled()) {
			QueueReceiver receiver = (QueueReceiver) threadContext.remove(THREAD_CONTEXT_RECEIVER_KEY);
			releaseReceiver(receiver);
	
			QueueSession session = (QueueSession) threadContext.remove(THREAD_CONTEXT_SESSION_KEY);
			closeSession(session);
		}
	}


	public void afterMessageProcessed(PipeLineResult plr, Object rawMessage, Map threadContext) throws ListenerException {	
	    		    
		try {
			if (isJmsTransacted() && !(getMessagingSource().isXaEnabledForSure() && JtaUtil.inTransaction())) {
				QueueSession session = (QueueSession) threadContext.get(THREAD_CONTEXT_SESSION_KEY);
			
				try {
					session.commit();
				} catch (JMSException e) {
					log.error(getLogPrefix()+"got error committing the received message", e);
				}
			    if (isSessionsArePooled()) {
					threadContext.remove(THREAD_CONTEXT_SESSION_KEY);
					releaseSession(session);
			    }
			}
		} catch (Exception e) {
			log.error(getLogPrefix()+"exception in closing or releasing session", e);
		}
	    // on request-reply send the reply.
	    if (getMessageProtocolEnum().equals(IfsaMessageProtocolEnum.REQUEST_REPLY)) {
			Message originalRawMessage;
			if (rawMessage instanceof Message) { 
				originalRawMessage = (Message)rawMessage;
			} else {
				originalRawMessage = (Message)threadContext.get(THREAD_CONTEXT_ORIGINAL_RAW_MESSAGE_KEY);
			}
			if (originalRawMessage==null) {
				String id = (String) threadContext.get(IPipeLineSession.messageIdKey);
				String cid = (String) threadContext.get(IPipeLineSession.businessCorrelationIdKey);
				log.warn(getLogPrefix()+"no original raw message found for messageId ["+id+"] correlationId ["+cid+"], cannot send result");
			} else {
				QueueSession session = getSession(threadContext);
				try {
					String result="<exception>no result</exception>";
					if (plr!=null && plr.getResult()!=null) {
						result=plr.getResult();
					}
					sendReply(session, originalRawMessage, result);
				} catch (IfsaException e) {
					try {
						sendReply(session, originalRawMessage, "<exception>"+e.getMessage()+"</exception>");
					} catch (IfsaException e2) {
						log.warn(getLogPrefix()+"exception sending errormessage as reply",e2);
					}
					throw new ListenerException(getLogPrefix()+"Exception on sending result", e);
				} finally {
					releaseSession(session);
				}
			}
	    }
	}
	

	protected String getIdFromWrapper(IMessageWrapper wrapper, Map threadContext)  {
		for (Iterator it=wrapper.getContext().keySet().iterator(); it.hasNext();) {
			String key = (String)it.next();
			Object value = wrapper.getContext().get(key);
			log.debug(getLogPrefix()+"setting variable ["+key+"] to ["+value+"]");
			threadContext.put(key, value);
		}
		return wrapper.getId();
	}
	protected String getStringFromWrapper(IMessageWrapper wrapper, Map threadContext)  {
		return wrapper.getText();
	}

	

	
	/**
	 * Extracts ID-string from message obtained from {@link #getRawMessage(Map)}. 
	 * Puts also the following parameters  in the threadContext:
	 * <ul>
	 *   <li>id</li>
	 *   <li>cid</li>
	 *   <li>timestamp</li>
	 *   <li>replyTo</li>
	 *   <li>messageText</li>
	 *   <li>fullIfsaServiceName</li>
	 *   <li>ifsaServiceName</li>
	 *   <li>ifsaGroup</li>
	 *   <li>ifsaOccurrence</li>
	 *   <li>ifsaVersion</li>
	 * </ul>
	 * @return ID-string of message for adapter.
	 */
	public String getIdFromRawMessage(Object rawMessage, Map threadContext) throws ListenerException {
	
		IFSAMessage message = null;
	 
	 	if (rawMessage instanceof IMessageWrapper) {
	 		return getIdFromWrapper((IMessageWrapper)rawMessage,threadContext);
	 	}
	 
	    try {
	        message = (IFSAMessage) rawMessage;
	    } catch (ClassCastException e) {
	        log.error(getLogPrefix()+
	            "message received was not of type IFSAMessage, but [" + rawMessage.getClass().getName() + "]", e);
	        return null;
	    }
	    String mode = "unknown";
	    String id = "unset";
	    String cid = "unset";
	    Date tsSent = null;
	    Destination replyTo = null;
	    String messageText = null;
		String fullIfsaServiceName = null;
	    IFSAServiceName requestedService = null;
	    String ifsaServiceName=null, ifsaGroup=null, ifsaOccurrence=null, ifsaVersion=null;
	    try {
	        if (message.getJMSDeliveryMode() == DeliveryMode.NON_PERSISTENT) {
	            mode = "NON_PERSISTENT";
	        } else
	            if (message.getJMSDeliveryMode() == DeliveryMode.PERSISTENT) {
	                mode = "PERSISTENT";
	            }
	    } catch (JMSException ignore) {
	    }
	    // --------------------------
	    // retrieve MessageID
	    // --------------------------
	    try {
	        id = message.getJMSMessageID();
	    } catch (JMSException ignore) {
	    }
	    // --------------------------
	    // retrieve CorrelationID
	    // --------------------------
	    try {
	        cid = message.getJMSCorrelationID();
	        if (cid == null) {
	            cid = id;
	            log.debug("Setting correlation ID to MessageId");
	        }
	    } catch (JMSException ignore) {
	    }
	    // --------------------------
	    // retrieve TimeStamp
	    // --------------------------
	    try {
	        long lTimeStamp = message.getJMSTimestamp();
			tsSent = new Date(lTimeStamp);
	
	    } catch (JMSException ignore) {
	    }
	    // --------------------------
	    // retrieve ReplyTo address
	    // --------------------------
	    try {
	        replyTo = message.getJMSReplyTo();
	
	    } catch (JMSException ignore) {
	    }
	    // --------------------------
	    // retrieve message text
	    // --------------------------
	    try {
	        messageText = ((TextMessage)message).getText();
	    } catch (Throwable ignore) {
	    }
	    // --------------------------
	    // retrieve ifsaServiceDestination
	    // --------------------------
	    try {
			fullIfsaServiceName = message.getServiceString();
			requestedService = message.getService();
			
			ifsaServiceName = requestedService.getServiceName();
			ifsaGroup = requestedService.getServiceGroup();
			ifsaOccurrence = requestedService.getServiceOccurance();
			ifsaVersion = requestedService.getServiceVersion();
			
	    } catch (JMSException e) {
	        log.error(getLogPrefix() + "got error getting serviceparameter", e);
	    }

		if (log.isDebugEnabled()) {
			log.debug(getLogPrefix()+ "got message for [" + fullIfsaServiceName
					+ "] with JMSDeliveryMode=[" + mode
					+ "] \n  JMSMessageID=[" + id
					+ "] \n  JMSCorrelationID=["+ cid
					+ "] \n  ifsaServiceName=["+ ifsaServiceName
					+ "] \n  ifsaGroup=["+ ifsaGroup
					+ "] \n  ifsaOccurrence=["+ ifsaOccurrence
					+ "] \n  ifsaVersion=["+ ifsaVersion
					+ "] \n  Timestamp Sent=[" + DateUtils.format(tsSent) 
					+ "] \n  ReplyTo=[" + ((replyTo == null) ? "none" : replyTo.toString())
					+ "] \n  MessageHeaders=["+displayHeaders(message)+"\n"
					+ "] \n  Message=[" + message.toString()+"\n]");
					
		}
	
		PipeLineSessionBase.setListenerParameters(threadContext, id, cid, null, tsSent);
	    threadContext.put("timestamp", tsSent);
	    threadContext.put("replyTo", ((replyTo == null) ? "none" : replyTo.toString()));
	    threadContext.put("messageText", messageText);
	    threadContext.put("fullIfsaServiceName", fullIfsaServiceName);
	    threadContext.put("ifsaServiceName", ifsaServiceName);
	    threadContext.put("ifsaGroup", ifsaGroup);
	    threadContext.put("ifsaOccurrence", ifsaOccurrence);
	    threadContext.put("ifsaVersion", ifsaVersion);

		Map udz = (Map)message.getIncomingUDZObject();
		if (udz!=null) {
			String contextDump = "ifsaUDZ:";
			for (Iterator it = udz.keySet().iterator(); it.hasNext();) {
				String key = (String)it.next();
				String value = (String)udz.get(key);
				contextDump = contextDump + "\n " + key + "=[" + value + "]";
				threadContext.put(key, value);
			}
			if (log.isDebugEnabled()) {
				log.debug(getLogPrefix()+ contextDump);
			}
		}

	    return id;
	}
	
	private String displayHeaders(IFSAMessage message) {
		StringBuffer result= new StringBuffer();
		try { 
			for(Enumeration enumeration = message.getPropertyNames(); enumeration.hasMoreElements();) {
				String tagName = (String)enumeration.nextElement();
				Object value = message.getObjectProperty(tagName);
				result.append("\n").append(tagName).append(": ");
				if (value==null) {
					result.append("null");
				} else {
					result.append("(").append(ClassUtils.nameOf(value)).append(") [").append(value).append("]");
					if (tagName.startsWith("ifsa") && 
						!tagName.equals("ifsa_unique_id") && 
						!tagName.startsWith("ifsa_epz_") && 
						!tagName.startsWith("ifsa_udz_")) {
							result.append(" * copied when sending reply");
							if (!(value instanceof String)) {
								result.append(" THIS CAN CAUSE A PROBLEM AS "+ClassUtils.nameOf(value)+" IS NOT String!");
							}
						}
				}
			}
		} catch(Throwable t) {
			log.warn("exception parsing headers",t);
		}
		return result.toString();
	}
	
	private boolean sessionNeedsToBeSavedForAfterProcessMessage(Object result)
	{
		try {
			return isJmsTransacted() && 
					!(getMessagingSource().isXaEnabledForSure() && JtaUtil.inTransaction()) &&
					isSessionsArePooled()&&
					result != null && 
					!(result instanceof IFSAPoisonMessage) ;
		} catch (Throwable t) {
			log.warn(t);
			return false;
		}
	}
	
	/**
	 * Retrieves messages to be processed by the server, implementing an IFSA-service, but does no processing on it.
	 */
	public Object getRawMessage(Map threadContext) throws ListenerException {
		Object result=null;
		QueueSession session=null;
		QueueReceiver receiver=null;
		
		threadContext.remove(THREAD_CONTEXT_ORIGINAL_RAW_MESSAGE_KEY);
	    try {	
			session = getSession(threadContext);
			try {	
				receiver = getReceiver(threadContext, session);
		        result = receiver.receive(getTimeOut());
				while (result==null && canGoOn() && !JtaUtil.inTransaction()) {
					result = receiver.receive(getTimeOut());
				}
			} catch (Exception e) {
				throw new ListenerException(getLogPrefix(),e);
		    } finally {
		    	releaseReceiver(receiver);
			}
		} finally {
			if (sessionNeedsToBeSavedForAfterProcessMessage(result)) {
				threadContext.put(THREAD_CONTEXT_SESSION_KEY, session);
			} else {
				releaseSession(session);
			}
	    }
	    
	    if (result instanceof IFSAPoisonMessage) {
	        IFSAHeader header = ((IFSAPoisonMessage) result).getIFSAHeader();
	        String source;
	        try {
	        	source = header.getIFSA_Source();
	        } catch (Exception e) {
	        	source = "unknown due to exeption:"+e.getMessage();
	        }
	        String msg=getLogPrefix()+ "received IFSAPoisonMessage "
	                	+ "source [" + source + "]"
		                + "content [" + ToStringBuilder.reflectionToString((IFSAPoisonMessage) result) + "]";
		    log.warn(msg);
	    }
	    try {
			if ((result instanceof IFSATextMessage || result instanceof IFSAPoisonMessage) &&
			     JtaUtil.inTransaction() 
			    ) {
				threadContext.put(THREAD_CONTEXT_ORIGINAL_RAW_MESSAGE_KEY, result);
				result = new MessageWrapper(result, this);
			}
		} catch (Exception e) {
			throw new ListenerException("cannot wrap non serialzable message in wrapper",e);
		}
	    return result;
	}
	/**
	 * Extracts string from message obtained from {@link #getRawMessage(Map)}. May also extract
	 * other parameters from the message and put those in the threadContext.
	 * @return input message for adapter.
	 */
	public String getStringFromRawMessage(Object rawMessage, Map threadContext) throws ListenerException {
		if (rawMessage instanceof IMessageWrapper) {
			return getStringFromWrapper((IMessageWrapper)rawMessage,threadContext);
		}
		if (rawMessage instanceof IFSAPoisonMessage) {
			IFSAPoisonMessage pm = (IFSAPoisonMessage)rawMessage;
			IFSAHeader header = pm.getIFSAHeader();
			String source;
			try {
				source = header.getIFSA_Source();
			} catch (Exception e) {
				source = "unknown due to exeption:"+e.getMessage();
			}
			return  "<poisonmessage>"+
					"  <source>"+source+"</source>"+
					"  <contents>"+XmlUtils.encodeChars(ToStringBuilder.reflectionToString(pm))+"</contents>"+
					"</poisonmessage>";
		}

	    TextMessage message = null;
	    try {
	        message = (TextMessage) rawMessage;
	    } catch (ClassCastException e) {
	        log.warn(getLogPrefix()+ "message received was not of type TextMessage, but ["+rawMessage.getClass().getName()+"]", e);
	        return null;
	    }
	    try {
	    	return message.getText();
	    } catch (JMSException e) {
		    throw new ListenerException(getLogPrefix(),e);
	    }
	}

	protected boolean canGoOn() {
		return runStateEnquirer!=null && runStateEnquirer.isInState(RunStateEnum.STARTED);
	}

	public void SetRunStateEnquirer(RunStateEnquirer enquirer) {
		runStateEnquirer=enquirer;
	}

}
