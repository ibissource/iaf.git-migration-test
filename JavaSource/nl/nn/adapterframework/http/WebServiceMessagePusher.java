/*
 * $Log: WebServiceMessagePusher.java,v $
 * Revision 1.2  2004-08-09 13:54:13  L190409
 * changed ServiceClient to MessageHandler
 *
 * Revision 1.1  2004/07/15 07:40:43  gerrit
 * introduction of http package
 *
 * Revision 1.2  2004/06/30 12:34:13  gerrit
 * added (dummy) setter for exceptionlistener
 *
 * Revision 1.1  2004/06/22 12:12:52  gerrit
 * introduction of MessagePushers and PushingReceivers
 *
 */
package nl.nn.adapterframework.http;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IMessageHandler;
import nl.nn.adapterframework.core.IPushingListener;
import nl.nn.adapterframework.core.IbisExceptionListener;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.core.PipeLineResult;
import nl.nn.adapterframework.receivers.ServiceClient;
import nl.nn.adapterframework.receivers.ServiceDispatcher;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Implementation of a {@link nl.nn.adapterframework.core.IMessagePusher pushing listener},
 * that enables a <code>PushingReceiverBase</code> to receive messages by generic services or by web-services.
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.receivers.WebServiceMessagePusher</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td>  <td>name of the listener as known to the adapter</td><td>&nbsp;</td></tr>
 * </table>
 * @version Id
 * @author Gerrit van Brakel 
 */
public class WebServiceMessagePusher  implements IPushingListener, ServiceClient, Serializable {
	public static final String version="$Id: WebServiceMessagePusher.java,v 1.2 2004-08-09 13:54:13 L190409 Exp $";
	protected Logger log = Logger.getLogger(this.getClass());;

	private IMessageHandler handler;        	
	private String name;

	/**
	 * initialize listener and register <code>this</code> to the JNDI
	 */
	public void configure() throws ConfigurationException {
		try {
			if (handler==null) {
				throw new ConfigurationException("handler has not been set");
			}
		    log.debug("registering listener ["+name+"] with ServiceDispatcher");
	        ServiceDispatcher.getInstance().registerServiceClient(name, this);
		} catch (Exception e){
			throw new ConfigurationException(e);
		}
	}

	public void open() {
		// do nothing special
	}
	public void close() {
		// do nothing special
	}


	public String getIdFromRawMessage(Object rawMessage, HashMap threadContext)  {
		return null;
	}
	public String getStringFromRawMessage(Object rawMessage, HashMap threadContext) {
		return (String) rawMessage;
	}
	public void afterMessageProcessed(PipeLineResult processResult, Object rawMessage, HashMap threadContext) throws ListenerException {
	}



	public String processRequest(String message) {
		
		String result;
		try {
			return handler.processRequest(this, message);
		} catch (ListenerException e) {
			return handler.formatException(null,null, message,e);
		}
	}

	public String processRequest(String correlationId, String message) {
		String result;
		try {
			log.debug("wspusher processing ["+correlationId+"]");
			return handler.processRequest(this, correlationId, message);
		} catch (ListenerException e) {
			return handler.formatException(null,correlationId, message,e);
		}
	}


 	/**
     * The <code>toString()</code> method retrieves its value
     * by reflection.
     * @see org.apache.commons.lang.builder.ToStringBuilder#reflectionToString
     *
     **/
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * Returns the name of the Listener. 
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the name of the Listener. 
	 */
	public void setName(String name) {
		this.name=name;
	}

	public void setHandler(IMessageHandler handler) {
		this.handler=handler;
	}

	public void setExceptionListener(IbisExceptionListener listener) {
		// do nothing, no exceptions known
	}
}
