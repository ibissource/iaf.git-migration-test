/*
 * $Log: IfsaProviderListener.java,v $
 * Revision 1.1  2004-07-05 14:28:38  L190409
 * First version, converted from IfsaServiceListener
 *
 * Revision 1.4  2004/03/26 07:25:42  johan
 * Updated erorhandling
 *
 * Revision 1.3  2004/03/24 15:27:24  gerrit
 * solved uncaught exception in error message
 *
 */
package nl.nn.adapterframework.extensions.ifsa;

import nl.nn.adapterframework.core.IPullingListener;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.PipeLineResult;
import nl.nn.adapterframework.core.INamedObject;

import com.ing.ifsa.IFSAPoisonMessage;
import com.ing.ifsa.IFSAHeader;
import com.ing.ifsa.IFSAServiceName;
import com.ing.ifsa.IFSATextMessage;

import java.util.HashMap;
import java.util.Date;

import javax.jms.Message;
import javax.jms.QueueSession;
import javax.jms.QueueReceiver;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.TextMessage;
import javax.jms.JMSException;


import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Implementation of {@link IPullingListener} that acts as an IFSA-service.
 * 
 * There is no need or possibility to set the ServiceId as the Provider will receive all messages
 * for this Application on the same serviceQueue.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.extensions.ifsa.IfsaProviderListener</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the object</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setApplicationId(String) applicationId}</td><td>the ApplicationID, in the form of "IFSA://<i>AppId</i>"</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMessageProtocol(String) messageProtocol}</td><td>protocol of IFSA-Service to be called. Possible values 
 * <ul>
 *   <li>"FF": Fire & Forget protocol</li>
 *   <li>"RR": Request-Reply protocol</li>
 * </ul></td><td><td>&nbsp;</td></td></tr>
 * </table>
 * @author Gerrit van Brakel
 * @since 4.2
 */
public class IfsaProviderListener extends IfsaFacade implements IPullingListener, INamedObject {
	public static final String version="$Id: IfsaProviderListener.java,v 1.1 2004-07-05 14:28:38 L190409 Exp $";

    private final static String THREAD_CONTEXT_SESSION_KEY = "session";
    private final static String THREAD_CONTEXT_RECEIVER_KEY = "receiver";

    private String commitOnState;
    private long timeOut = 3000;

	public IfsaProviderListener() {
		super(true); //instantiate as a provider
	}

	public void open() throws ListenerException {
		try {
			openService();
		} catch (IfsaException e) {
			throw new ListenerException(getLogPrefix()+"error opening", e);
		}
	}
	public HashMap openThread() throws ListenerException {
		HashMap threadContext = new HashMap();
	
		try {
		QueueSession session = createSession();
		threadContext.put(THREAD_CONTEXT_SESSION_KEY, session);
	
		QueueReceiver receiver;
		receiver = getServiceReceiver(session);
		threadContext.put(THREAD_CONTEXT_RECEIVER_KEY, receiver);
	
		return threadContext;
		} catch (IfsaException e) {
			throw new ListenerException(getLogPrefix()+" opening listener thread", e);
		}
	}

	public void close() throws ListenerException {
		try {
			closeService();
		} catch (Exception e) {
			throw new ListenerException(getLogPrefix()+"exception in close",e);
		}
	}
	public void closeThread(HashMap threadContext) throws ListenerException {
	
		try {
	
			QueueReceiver receiver = (QueueReceiver) threadContext.remove(THREAD_CONTEXT_RECEIVER_KEY);
			if (receiver != null) {
				receiver.close();
			}
	
			QueueSession session = (QueueSession) threadContext.remove(THREAD_CONTEXT_SESSION_KEY);
			if (session != null) {
				session.close();
			}
		} catch (Exception e) {
			throw new ListenerException(getLogPrefix()+"exception closing thread", e);
		}
	}


	public void afterMessageProcessed(PipeLineResult plr, Object rawMessage, HashMap threadContext)
    throws ListenerException {
	
	    String cid = (String) threadContext.get("cid");
	    QueueSession session = (QueueSession) threadContext.get(THREAD_CONTEXT_SESSION_KEY);
	    
	    /* 
	     * Message are only committed in the Fire & Forget scenario when the outcome
	     * of the adapter equals the getCommitOnResult value
	     */
	    if (getMessageProtocolEnum().equals(IfsaMessageProtocolEnum.FIRE_AND_FORGET)) {
	        if (getCommitOnState().equals(plr.getState())) {
	            try {
	                session.commit();
	            } catch (JMSException e) {
	                log.error(getLogPrefix()+"got error committing the received message", e);
	            }
	        } else {
	            log.warn(getLogPrefix()+"message with correlationID ["
	                    + cid
	                    + " message ["
	                    + getStringFromRawMessage(rawMessage, threadContext)
	                    + "]"
	                    + " is NOT committed. The result-state of the adapter is ["
	                    + plr.getState()
	                    + "] while the state for committing is set to ["
	                    + getCommitOnState()
	                    + "]");
	
	        }
	    }
	    // on request-reply send the reply. On error: halt the listener
	    if (getMessageProtocolEnum().equals(IfsaMessageProtocolEnum.REQUEST_REPLY)) {
	        try {
	            sendReply(session, (Message) rawMessage, plr.getResult());
	        } catch (IfsaException e) {
	            throw new ListenerException(getLogPrefix()+"got error sending result", e);
	        }
	    }
	}
	

	
	/**
	 * Extracts ID-string from message obtained from {@link #getRawMessage(HashMap)}. 
	 * Puts also the following parameters  in the threadContext:
	 * <ul>
	 * <li>id</li>
	 * <li>cid</li>
	 * <li>timestamp</li>
	 * <li>replyTo</li>
	 * <li>messageText</li>
	 * <li>serviceDestination</li>
	 * </ul>
	 * @return ID-string of message for adapter.
	 */
	public String getIdFromRawMessage(Object rawMessage, HashMap threadContext) throws ListenerException {
	
		TextMessage message = null;
	 
	    try {
	        message = (TextMessage) rawMessage;
	    } catch (ClassCastException e) {
	        log.error(
	            "message received by receiver ["
	                + getName()
	                + "] was not of type TextMessage, but ["
	                + rawMessage.getClass().getName()
	                + "]",
	            e);
	        return null;
	    }
	    String mode = "unknown";
	    String id = "unset";
	    String cid = "unset";
	    Date dTimeStamp = null;
	    Destination replyTo = null;
	    String messageText = null;
	    IFSAServiceName ifsaServiceDestination = null;
	    String serviceDestination = null;
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
	        dTimeStamp = new Date(lTimeStamp);
	
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
	        messageText = message.getText();
	    } catch (JMSException ignore) {
	    }
	    // --------------------------
	    // retrieve ifsaServiceDestination
	    // --------------------------
	    try {
	        ifsaServiceDestination = ((IFSATextMessage) message).getService();
	        serviceDestination = ToStringBuilder.reflectionToString(ifsaServiceDestination);
	    } catch (JMSException e) {
	        log.error("[" + getName() + "] got error getting serviceDestination", e);
	    }
	
	    log.info(
	        "Receiver ["
	            + getName()
	            + "] got message with JMSDeliveryMode=["
	            + mode
	            + "] \n  JMSMessageID=["
	            + id
	            + "] \n  JMSCorrelationID=["
	            + cid
	            + "] \n  Timestamp=["
	            + dTimeStamp.toString()
	            + "] \n  ReplyTo=["
	            + ((replyTo == null) ? "none" : replyTo.toString())
	            + "] \n Message=["
	            + message.toString()
	            + "]");
	
	    threadContext.put("id", id);
	    threadContext.put("cid", cid);
	    threadContext.put("timestamp", dTimeStamp);
	    threadContext.put("replyTo", replyTo);
	    threadContext.put("messageText", messageText);
	    threadContext.put("serviceDestination", serviceDestination);
	    return id;
	}
	/**
	 * Retrieves messages to be processed by the server, implementing an IFSA-service, but does no processing on it.
	 */
	public Object getRawMessage(HashMap threadContext) throws ListenerException {
		Object result;
	    try {
		    QueueReceiver receiver = (QueueReceiver)threadContext.get(THREAD_CONTEXT_RECEIVER_KEY);
	
	        result = receiver.receive(getTimeOut());
	    } catch (JMSException e) {
	        throw new ListenerException(e);
	    }
	    
	    if (result instanceof IFSAPoisonMessage) {
	        IFSAHeader header = ((IFSAPoisonMessage) result).getIFSAHeader();
	        String source;
	        try {
	        	source = header.getIFSA_Source();
	        } catch (Exception e) {
	        	source = "unknown due to exeption:"+e.getMessage();
	        }
	        log.error("["
	                + getName()
	                + "] received IFSAPoisonMessage "
	                + "source ["
	                + source
	                + "]"
	                + "content ["
	                + ToStringBuilder.reflectionToString((IFSAPoisonMessage) result)
	                + "]");
	    }
	    return result;
	}
	/**
	 * Extracts string from message obtained from {@link #getRawMessage(HashMap)}. May also extract
	 * other parameters from the message and put those in the threadContext.
	 * @return input message for adapter.
	 */
	public String getStringFromRawMessage(Object rawMessage, HashMap threadContext) throws ListenerException {
	    TextMessage message = null;
	    try {
	        message = (TextMessage) rawMessage;
	    } catch (ClassCastException e) {
	        log.error("message received by receiver ["+ getName()+ "] was not of type TextMessage, but ["+rawMessage.getClass().getName()+"]", e);
	        return null;
	    }
	    try {
	    	return message.getText();
	    } catch (JMSException e) {
		    throw new ListenerException(e);
	    }
	}
	
	public String getCommitOnState() {
		return commitOnState;
	}
	public void setCommitOnState(java.lang.String newCommitOnState) {
		commitOnState = newCommitOnState;
	}

	public long getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(long newTimeOut) {
		timeOut = newTimeOut;
	}
}
