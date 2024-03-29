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
 * $Log: IfsaMessagingSource.java,v $
 * Revision 1.7  2012-09-07 13:15:16  jaco
 * Messaging related changes:
 * - Use CACHE_CONSUMER by default for ESB RR
 * - Don't use JMSXDeliveryCount to determine whether message has already been processed
 * - Added maxDeliveries
 * - Delay wasn't increased when unable to write to error store (it was reset on every new try)
 * - Don't call session.rollback() when isTransacted() (it was also called in afterMessageProcessed when message was moved to error store)
 * - Some cleaning along the way like making some synchronized statements unnecessary
 * - Made BTM and ActiveMQ work for testing purposes
 *
 * Revision 1.6  2011/11/30 13:51:44  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2010/02/10 13:51:09  gerrit
 * improved getPhysicalName()
 *
 * Revision 1.3  2010/02/10 09:36:24  gerrit
 * fixed getPhysicalName()
 *
 * Revision 1.2  2010/01/28 15:01:57  gerrit
 * removed unused imports
 *
 * Revision 1.1  2010/01/28 14:49:06  gerrit
 * renamed 'Connection' classes to 'MessageSource'
 *
 * Revision 1.3  2008/07/24 12:26:26  gerrit
 * added support for authenticated JMS
 *
 * Revision 1.2  2007/10/16 08:39:30  gerrit
 * moved IfsaException and IfsaMessageProtocolEnum back to main package
 *
 * Revision 1.1  2007/10/16 08:15:43  gerrit
 * introduced switch class for jms and ejb
 *
 * Revision 1.12  2007/10/08 12:17:00  gerrit
 * changed HashMap to Map where possible
 *
 * Revision 1.11  2007/09/05 15:46:37  gerrit
 * moved XA determination capabilities to IfsaConnection
 *
 * Revision 1.10  2006/02/28 08:44:16  gerrit
 * cleanUp on close configurable
 *
 * Revision 1.9  2005/11/02 09:40:52  gerrit
 * made useSingleDynamicReplyQueue configurable from appConstants
 *
 * Revision 1.8  2005/11/02 09:08:06  gerrit
 * ifsa-mode connection not for single dynamic reply queue
 *
 * Revision 1.7  2005/10/26 08:24:54  gerrit
 * pulled dynamic reply code out of IfsaConnection to ConnectionBase
 *
 * Revision 1.6  2005/10/20 15:34:09  gerrit
 * renamed JmsConnection into ConnectionBase
 *
 * Revision 1.5  2005/10/18 07:04:47  gerrit
 * better handling of dynamic reply queues
 *
 * Revision 1.4  2005/08/31 16:29:50  gerrit
 * corrected code for static reply queues
 *
 * Revision 1.3  2005/07/19 12:34:50  gerrit
 * corrected version-string
 *
 * Revision 1.2  2005/07/19 12:33:56  gerrit
 * implements IXAEnabled 
 * polishing of serviceIds, to work around problems with ':' and '/'
 *
 * Revision 1.1  2005/05/03 15:58:49  gerrit
 * rework of shared connection code
 *
 * Revision 1.2  2005/04/26 15:16:07  gerrit
 * removed most bugs
 *
 * Revision 1.1  2005/04/26 09:36:16  gerrit
 * introduction of IfsaApplicationConnection
 */
package nl.nn.adapterframework.extensions.ifsa.jms;

import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.naming.NamingException;

import nl.nn.adapterframework.extensions.ifsa.IfsaException;
import nl.nn.adapterframework.jms.MessagingSource;
import nl.nn.adapterframework.util.AppConstants;
import nl.nn.adapterframework.util.ClassUtils;

import com.ing.ifsa.IFSAContext;
import com.ing.ifsa.IFSAQueue;
import com.ing.ifsa.IFSAQueueConnectionFactory;

/**
 * {@link nl.nn.adapterframework.jms.MessagingSource} for IFSA connections.
 * 
 * IFSA related IBIS objects can obtain an connection from this class. The physical connection is shared
 * between all IBIS objects that have the same ApplicationID.
 * 
 * @author Gerrit van Brakel
 * @version $Id$
 */
public class IfsaMessagingSource extends MessagingSource {

	private final static String CLEANUP_ON_CLOSE_KEY="ifsa.cleanUpOnClose";
	private static Boolean cleanUpOnClose=null; 

	private boolean preJms22Api;
	private boolean xaEnabled;
	
	public IfsaMessagingSource(String applicationId, IFSAContext context, IFSAQueueConnectionFactory connectionFactory, Map messagingSourceMap, boolean preJms22Api, boolean xaEnabled) {
		super(applicationId,context,connectionFactory,messagingSourceMap,null,false,true);
		this.preJms22Api=preJms22Api;
		this.xaEnabled=xaEnabled;
		log.debug("created new IfsaMessagingSource for ["+applicationId+"] context ["+context+"] connectionfactory ["+connectionFactory+"]");
	}


	public boolean hasDynamicReplyQueue() throws IfsaException {
		try {
			if (preJms22Api) {
				return !((IFSAQueueConnectionFactory) getConnectionFactory()).IsClientTransactional();
			} else {
				return ((IFSAContext) getContext()).hasDynamicReplyQueue();
			}
		} catch (NamingException e) {
			throw new IfsaException("could not find IfsaContext",e);
		}
	}

	public boolean canUseIfsaModeSessions() throws IfsaException {
		return hasDynamicReplyQueue() && !useSingleDynamicReplyQueue();
	}
	
	/**
	 * Retrieves the reply queue for a <b>client</b> connection. If the
	 * client is transactional the replyqueue is retrieved from IFSA,
	 * otherwise a temporary (dynamic) queue is created.
	 */
	public Queue getClientReplyQueue(QueueSession session) throws IfsaException {
		Queue replyQueue = null;
	
		try {
			/*
			 * if we don't know if we're using a dynamic reply queue, we can
			 * check this using the function IsClientTransactional
			 * Yes -> we're using a static reply queue
			 * No -> dynamic reply queue
			 */
			if (hasDynamicReplyQueue()) { // Temporary Dynamic
				replyQueue =  getDynamicReplyQueue(session);
				log.debug("got dynamic reply queue [" +replyQueue.getQueueName()+"]");
			} else { // Static
				replyQueue = (Queue) ((IFSAContext)getContext()).lookupReply(getId());
				log.debug("got static reply queue [" +replyQueue.getQueueName()+"]");            
			}
			return replyQueue;
		} catch (Exception e) {
			throw new IfsaException(e);
		}
	}
	
	protected void releaseClientReplyQueue(Queue replyQueue) throws IfsaException {
		if (hasDynamicReplyQueue()) { // Temporary Dynamic
			releaseDynamicReplyQueue(replyQueue);		
		}
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
	
		QueueReceiver queueReceiver;
		    
		String correlationId;
		Queue replyQueue;
		try {
			correlationId = sentMessage.getJMSMessageID(); // IFSA uses the messageId as correlationId
			replyQueue=(Queue)sentMessage.getJMSReplyTo();
		} catch (JMSException e) {
			throw new IfsaException(e);
		}
		
		try {
			if (hasDynamicReplyQueue() && !useSingleDynamicReplyQueue()) {
				queueReceiver = session.createReceiver(replyQueue);
				log.debug("created receiver on individual dynamic reply queue" );
			} else {
				String selector="JMSCorrelationID='" + correlationId + "'";
				queueReceiver = session.createReceiver(replyQueue, selector);
				log.debug("created receiver on static or shared-dynamic reply queue - selector ["+selector+"]");
			}	
		} catch (JMSException e) {
			throw new IfsaException(e);
		}
		return queueReceiver;
	}

	public void closeReplyReceiver(QueueReceiver receiver) throws IfsaException {
		try { 
			if (receiver!=null) {
				Queue replyQueue = receiver.getQueue();
				receiver.close();
				releaseClientReplyQueue(replyQueue);
			}
		} catch (JMSException e) {
			throw new IfsaException(e);
		}
	}

  	public IFSAQueue lookupService(String serviceId) throws IfsaException {
		try {
			return (IFSAQueue) ((IFSAContext)getContext()).lookupService(serviceId);
		} catch (NamingException e) {
			throw new IfsaException("cannot lookup queue for service ["+serviceId+"]",e);
		}
  	}
  	
	public IFSAQueue lookupProviderInput() throws IfsaException {
		try {
			return (IFSAQueue) ((IFSAContext)getContext()).lookupProviderInput();
		} catch (NamingException e) {
			throw new IfsaException("cannot lookup provider queue",e);
		}
	}
	
	protected String replaceLast(String string, char from, char to) {
		int lastTo=string.lastIndexOf(to);
		int lastFrom=string.lastIndexOf(from);
		
		if (lastFrom>0 && lastTo<lastFrom) {
			String result = string.substring(0,lastFrom)+to+string.substring(lastFrom+1);
			log.info("replacing for Ifsa-compatibility ["+string+"] by ["+result+"]");
			return result;
		}
		return string;
	}

	public String polishServiceId(String serviceId) {
		if (preJms22Api) {
			return replaceLast(serviceId, '/',':');
		} else {
			return replaceLast(serviceId, ':','/');
		}
	}

	public synchronized boolean cleanUpOnClose() {
		if (cleanUpOnClose==null) {
			boolean cleanup=AppConstants.getInstance().getBoolean(CLEANUP_ON_CLOSE_KEY, true);
			cleanUpOnClose = new Boolean(cleanup);
		}
		return cleanUpOnClose.booleanValue();
	}

	protected ConnectionFactory getConnectionFactoryDelegate() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		return (QueueConnectionFactory)ClassUtils.getDeclaredFieldValue(getConnectionFactory(),"qcf");
	}

	public boolean xaCapabilityCanBeDetermined() {
		return !preJms22Api;
	}

	public boolean isXaEnabled() {
		return xaEnabled;
	}

	public boolean isXaEnabledForSure() {
		return xaCapabilityCanBeDetermined() && isXaEnabled();
	}

	public boolean isNotXaEnabledForSure() {
		return xaCapabilityCanBeDetermined() && !isXaEnabled();
	}

}
