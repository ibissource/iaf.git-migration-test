/*
 * $Log: JavaListener.java,v $
 * Revision 1.1  2004-08-23 07:38:20  L190409
 * renamed JavaPusher to JavaListener
 *
 * Revision 1.4  2004/08/16 14:10:32  unknown0
 * Remove warnings
 *
 * Revision 1.3  2004/08/16 14:09:58  unknown0
 * Return returnIfStopped value in case adapter is stopped
 *
 * Revision 1.2  2004/08/13 06:47:26  unknown0
 * Allow usage of JavaPusher without JNDI
 *
 * Revision 1.1  2004/08/12 10:58:43  unknown0
 * Replaced JavaReceiver by the JavaPusher that is to be used in a GenericPushingReceiver
 *
 * Revision 1.1  2004/04/26 06:21:38  unknown0
 * Add java receiver
 *
 */
package nl.nn.adapterframework.receivers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IMessageHandler;
import nl.nn.adapterframework.core.IPushingListener;
import nl.nn.adapterframework.core.IbisExceptionListener;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.core.PipeLineResult;
import nl.nn.adapterframework.jms.JNDIBase;
import nl.nn.adapterframework.jms.JmsRealm;

/** * 
 * The JavaListener listens to java requests.
 *  
 * @author JDekker
 * @version Id
 */
public class JavaListener implements IPushingListener {
	private static Map registeredListeners; 
	public static final String version="$Id: JavaListener.java,v 1.1 2004-08-23 07:38:20 L190409 Exp $";
	protected Logger log = Logger.getLogger(this.getClass());;
	private String name;
	private String jndiName;
	private IMessageHandler handler;
	private JNDIBase jndiBase = new JNDIBase();        	
	
	

	/**
	 * @return
	 */
	public IMessageHandler getHandler() {
		return handler;
	}

	/**
	 * Register receiver so that it can be used by a proxy
	 * @param name
	 * @param receiver
	 */
	private static void registerListener(String name, JavaListener listener) {
		getListeners().put(name, listener);
	}

	/**
	 * Unregister recevier, so that it can't be used by proxies
	 * @param name
	 */
//	private static void unregisterJavaListener(String name) {
//		getListeners().remove(name);
//	}

	/**
	 * @param name
	 * @return JavaReiver registered under name
	 */
	public static JavaListener getListener(String name) {
		return (JavaListener)getListeners().get(name);
	}

	/**
	 * Get all registered JavaListeners
	 * @return
	 */
	private synchronized static Map getListeners() {
		if (registeredListeners == null) {
			registeredListeners = Collections.synchronizedMap(new HashMap());
		}
		return registeredListeners;
	}
	
	/* (non-Javadoc)
	 * @see nl.nn.adapterframework.core.IPushingListener#setExceptionListener(nl.nn.adapterframework.core.IbisExceptionListener)
	 */
	public void setExceptionListener(IbisExceptionListener listener) {
		// do nothing, no exceptions known
	}

	/* (non-Javadoc)
	 * @see nl.nn.adapterframework.core.IPushingListener#setHandler(nl.nn.adapterframework.core.IMessageHandler)
	 */
	public void setHandler(IMessageHandler handler) {
		this.handler = handler;
	}

	/* (non-Javadoc)
	 * @see nl.nn.adapterframework.core.IListener#afterMessageProcessed(nl.nn.adapterframework.core.PipeLineResult, java.lang.Object, java.util.HashMap)
	 */
	public void afterMessageProcessed(PipeLineResult processResult, Object rawMessage, HashMap context) throws ListenerException {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see nl.nn.adapterframework.core.IListener#close()
	 */
	public void close() throws ListenerException {
		if (getJndiName() != null) {
			try {
					getContext().unbind(jndiName);
					closeContext();
				}
			catch (NamingException e) {
				log.error("error occured while stopping listener [" + getName() + "]", e);
			}
		} 
		// do not unregister, leave it to handler to handle this
		// unregisterJavaPusher(getName());		
	}

	public void configure() throws ConfigurationException {
		try {
			if (handler==null) {
				throw new ConfigurationException("handler has not been set");
			}
			if (StringUtils.isEmpty(getName())) {
				throw new ConfigurationException("name has not been set");
			}
		} 
		catch (Exception e){
			throw new ConfigurationException(e);
		}
	}

	public String getIdFromRawMessage(Object rawMessage, HashMap context) throws ListenerException {
		// do nothing
		return null;
	}

	public String getStringFromRawMessage(Object rawMessage, HashMap context) throws ListenerException {
		return (String)rawMessage;
	}

	public void open() throws ListenerException {
		// add myself to list so that proxy can find me
		registerListener(getName(), this);
		try {
			if (getJndiName() != null)
				getContext().rebind(jndiName, new JavaProxy(this));
		} 
		catch (NamingException e) {
			log.error("error occured while starting listener [" + getName() + "]", e);
		}		
	}

	/**
	 * @param message
	 * @return result of processing
	 */
	public String processRequest(String message) {
		String result;
		try {
			return handler.processRequest(this, message);
		} 
		catch (ListenerException e) {
			return handler.formatException(null,null, message,e);
		}
	}

	/**
	 * @param correlationId
	 * @param message
	 * @return result of processing
	 */
	public String processRequest(String correlationId, String message) {
		String result;
		try {
			if (log.isDebugEnabled())
				log.debug("javareceiver " + getName() + " processing [" + correlationId + "]");
			return handler.processRequest(this, correlationId, message);
		} 
		catch (ListenerException e) {
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
	 * @return
	 * @throws NamingException
	 */
	private Context getContext() throws NamingException {
		return jndiBase.getContext();
	}
	
	/**
	 * @throws NamingException
	 */
	private void closeContext() throws NamingException {
		jndiBase.closeContext();
	}

	/**
	 * @param jmsRealmName
	 */
	public void setJmsRealm(String jmsRealmName){
		JmsRealm.copyRealm(jndiBase, jmsRealmName);
	}
	/**
	 * @return the name under which the java receiver registers the java proxy in JNDI
	 */
	public String getJndiName() {
		return jndiName;
	}

	/**
	 * @param jndiName
	 */
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param string
	 */
	public void setName(String name) {
		this.name = name;
	}

}