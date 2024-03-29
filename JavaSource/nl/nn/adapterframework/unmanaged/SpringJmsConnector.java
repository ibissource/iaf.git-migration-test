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
 * $Log: SpringJmsConnector.java,v $
 * Revision 1.25  2012-09-07 13:15:17  jaco
 * Messaging related changes:
 * - Use CACHE_CONSUMER by default for ESB RR
 * - Don't use JMSXDeliveryCount to determine whether message has already been processed
 * - Added maxDeliveries
 * - Delay wasn't increased when unable to write to error store (it was reset on every new try)
 * - Don't call session.rollback() when isTransacted() (it was also called in afterMessageProcessed when message was moved to error store)
 * - Some cleaning along the way like making some synchronized statements unnecessary
 * - Made BTM and ActiveMQ work for testing purposes
 *
 * Revision 1.24  2011/11/30 13:52:01  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:54  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.22  2010/12/13 13:16:05  gerrit
 * made acknowledgemode configurable
 *
 * Revision 1.21  2010/09/01 09:17:30  gerrit
 * set all default cache levels to cach_none
 *
 * Revision 1.20  2008/10/07 08:46:26  gerrit
 * do not commit JMS transacted sessions from within a global transaction
 *
 * Revision 1.19  2008/09/01 15:14:56  gerrit
 * use session key definition from parent to store session
 *
 * Revision 1.18  2008/09/01 13:01:37  gerrit
 * set default cache level for non transacted back to CACHE_CONSUMER
 *
 * Revision 1.17  2008/08/27 16:23:02  gerrit
 * improved logging
 *
 * Revision 1.16  2008/08/21 17:56:20  gerrit
 * removed ifsa specific timing evaluation, that was not working
 *
 * Revision 1.15  2008/06/30 14:18:27  gerrit
 * use more robust detection transaction and setting of rollbackonly
 *
 * Revision 1.14  2008/06/24 15:13:08  gerrit
 * improved logging of exceptions
 *
 * Revision 1.13  2008/06/18 12:39:24  gerrit
 * set default cache mode CACHE_NONE, for both transacted and non transacted
 *
 * Revision 1.12  2008/05/14 11:51:45  gerrit
 * improved handling when not completly configured
 *
 * Revision 1.11  2008/02/19 13:58:35  gerrit
 * tiny little bug, pushed into 4.8.0
 *
 * Revision 1.10  2008/02/15 14:11:16  gerrit
 * avoid NPE when not configured
 *
 * Revision 1.9  2008/02/13 13:32:49  gerrit
 * show detailed processing times
 *
 * Revision 1.8  2008/02/06 16:38:51  gerrit
 * added support for setting of transaction timeout
 * removed global transaction inserted for jmsTransacted handling
 *
 * Revision 1.7  2008/01/29 12:17:26  gerrit
 * added support for thread number control
 *
 * Revision 1.6  2008/01/17 16:24:47  gerrit
 * txManager in onMessage only for only local transacted sessions
 *
 * Revision 1.5  2008/01/11 10:23:59  gerrit
 * fixed a lot of things
 *
 * Revision 1.4  2008/01/03 15:57:58  gerrit
 * rework port connected listener interfaces
 *
 * Revision 1.3  2007/11/22 09:12:03  gerrit
 * added message as parameter of populateThreadContext
 *
 * Revision 1.2  2007/11/05 13:06:55  tim
 * Rename and redefine methods in interface IListenerConnector to remove 'jms' from names
 *
 * Revision 1.1  2007/11/05 12:24:01  tim
 * Rename 'SpringJmsConfigurator' to 'SpringJmsConnector'
 *
 * Revision 1.5  2007/11/05 10:33:15  tim
 * Move interface 'IListenerConnector' from package 'configuration' to package 'core' in preparation of renaming it
 *
 * Revision 1.4  2007/10/17 11:33:40  gerrit
 * add at least one consumer
 *
 * Revision 1.3  2007/10/16 09:52:35  tim
 * Change over JmsListener to a 'switch-class' to facilitate smoother switchover from older version to spring version
 *
 * Revision 1.2  2007/10/15 13:11:04  gerrit
 * copy from EJB branch
 *
 */
package nl.nn.adapterframework.unmanaged;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IListenerConnector;
import nl.nn.adapterframework.core.IPortConnectedListener;
import nl.nn.adapterframework.core.IThreadCountControllable;
import nl.nn.adapterframework.core.IbisExceptionListener;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.util.Counter;
import nl.nn.adapterframework.util.DateUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Configure a Spring JMS Container from a {@link nl.nn.adapterframework.jms.PushingJmsListener}.
 * 
 * <p>
 * This implementation expects to receive an instance of
 * org.springframework.jms.listener.DefaultMessageListenerContainer
 * from the Spring BeanFactory. If another type of MessageListenerContainer
 * is created by the BeanFactory, then another implementation of IListenerConnector
 * should be provided as well.
 * </p>
 * <p>
 * This implementation works only with a PushingJmsListener, and not with other types PortConnectedListeners.
 * </p>
 * @author  Tim van der Leeuw
 * @since   4.8
 * @version $Id$
 */
public class SpringJmsConnector extends AbstractJmsConfigurator implements IListenerConnector, IThreadCountControllable, BeanFactoryAware, ExceptionListener, SessionAwareMessageListener {

 	private PlatformTransactionManager txManager;
	private BeanFactory beanFactory;
	private DefaultMessageListenerContainer jmsContainer;
	private String messageListenerClassName;
    
    public static final int DEFAULT_CACHE_LEVEL_TRANSACTED=DefaultMessageListenerContainer.CACHE_NONE;
//	public static final int DEFAULT_CACHE_LEVEL_NON_TRANSACTED=DefaultMessageListenerContainer.CACHE_CONSUMER;
	public static final int DEFAULT_CACHE_LEVEL_NON_TRANSACTED=DefaultMessageListenerContainer.CACHE_NONE;
	
//	public static final int MAX_MESSAGES_PER_TASK=100;
	public static final int IDLE_TASK_EXECUTION_LIMIT=1000;
 
	private TransactionDefinition TX = null;

	final Counter threadsProcessing = new Counter(0);
    
	protected DefaultMessageListenerContainer createMessageListenerContainer() throws ConfigurationException {
		try {
			Class klass = Class.forName(messageListenerClassName);
			return (DefaultMessageListenerContainer) klass.newInstance();
		} catch (Exception e) {
			throw new ConfigurationException(getLogPrefix()+"error creating instance of MessageListenerContainer ["+messageListenerClassName+"]", e);
		}
	}
    
	/* (non-Javadoc)
	 * @see nl.nn.adapterframework.configuration.IListenerConnector#configureReceiver(nl.nn.adapterframework.jms.PushingJmsListener)
	 */
	public void configureEndpointConnection(final IPortConnectedListener jmsListener, ConnectionFactory connectionFactory, Destination destination, IbisExceptionListener exceptionListener, String cacheMode, int acknowledgeMode, boolean sessionTransacted, String messageSelector) throws ConfigurationException {
		super.configureEndpointConnection(jmsListener, connectionFactory, destination, exceptionListener);
        
		// Create the Message Listener Container manually.
		// This is needed, because otherwise the Spring Factory will
		// call afterPropertiesSet() on the object which will validate
		// that all required properties are set before we get a chance
		// to insert our dynamic values from the config. file.
		this.jmsContainer = createMessageListenerContainer();
		
        
		if (getReceiver().isTransacted()) {
			log.debug(getLogPrefix()+"setting transction manager to ["+txManager+"]");
			jmsContainer.setTransactionManager(txManager);
			if (getReceiver().getTransactionTimeout()>0) {
				jmsContainer.setTransactionTimeout(getReceiver().getTransactionTimeout());
			}
			TX = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
		} else { 
			log.debug(getLogPrefix()+"setting no transction manager");
		}
		if (sessionTransacted) { 
			jmsContainer.setSessionTransacted(sessionTransacted);
		} 
		if (StringUtils.isNotEmpty(messageSelector)) {
			jmsContainer.setMessageSelector(messageSelector);
		}
		
		// Initialize with a number of dynamic properties which come from the configuration file
		jmsContainer.setConnectionFactory(getConnectionFactory());
		jmsContainer.setDestination(getDestination());
        
		jmsContainer.setExceptionListener(this);
		// the following is not required, the timeout set is the time waited to start a new poll attempt.
		//this.jmsContainer.setReceiveTimeout(getJmsListener().getTimeOut());
        
		if (getReceiver().getNumThreads() > 0) {
			jmsContainer.setMaxConcurrentConsumers(getReceiver().getNumThreads());
		} else {
			jmsContainer.setMaxConcurrentConsumers(1);
		}
		jmsContainer.setIdleTaskExecutionLimit(IDLE_TASK_EXECUTION_LIMIT);

		if (StringUtils.isNotEmpty(cacheMode)) {
			jmsContainer.setCacheLevelName(cacheMode);
		} else {
			if (getReceiver().isTransacted()) {
				jmsContainer.setCacheLevel(DEFAULT_CACHE_LEVEL_TRANSACTED);
			} else {
				jmsContainer.setCacheLevel(DEFAULT_CACHE_LEVEL_NON_TRANSACTED);
			}
		}
		if (acknowledgeMode>=0) {
			jmsContainer.setSessionAcknowledgeMode(acknowledgeMode);
		}
		jmsContainer.setMessageListener(this);
		// Use Spring BeanFactory to complete the auto-wiring of the JMS Listener Container,
		// and run the bean lifecycle methods.
		try {
			((AutowireCapableBeanFactory) this.beanFactory).configureBean(this.jmsContainer, "proto-jmsContainer");
		} catch (BeansException e) {
			throw new ConfigurationException(getLogPrefix()+"Out of luck wiring up and configuring Default JMS Message Listener Container for JMS Listener ["+ (getListener().getName()+"]"), e);
		}
        
		// Finally, set bean name to something we can make sense of
		if (getListener().getName() != null) {
			jmsContainer.setBeanName(getListener().getName());
		} else {
			jmsContainer.setBeanName(getReceiver().getName());
		}
	}

	public void start() throws ListenerException {
		log.debug(getLogPrefix()+"starting");
		if (jmsContainer!=null) {
			try {
				jmsContainer.start();
			} catch (Exception e) {
				throw new ListenerException(getLogPrefix()+"cannot start", e);
			}
		} else {
			throw new ListenerException(getLogPrefix()+"no jmsContainer defined");
		}
	}

	public void stop() throws ListenerException {
		log.debug(getLogPrefix()+"stopping");
		if (jmsContainer!=null) {
			try {
				jmsContainer.stop();
			} catch (Exception e) {
				throw new ListenerException(getLogPrefix()+"Exception while trying to stop", e);
			}
		} else {
			throw new ListenerException(getLogPrefix()+"no jmsContainer defined");
		}
	}


	public void onMessage(Message message, Session session)	throws JMSException {
		TransactionStatus txStatus=null;
               
		long onMessageStart= System.currentTimeMillis();
		long jmsTimestamp= message.getJMSTimestamp();
		threadsProcessing.increase();
		Thread.currentThread().setName(getReceiver().getName()+"["+threadsProcessing.getValue()+"]");

		try {		
			if (TX!=null) {
				txStatus = txManager.getTransaction(TX);
			}
                
			Map threadContext = new HashMap();
			try {
				IPortConnectedListener listener = getListener();
				threadContext.put(THREAD_CONTEXT_SESSION_KEY,session);
//				if (log.isDebugEnabled()) log.debug("transaction status before: "+JtaUtil.displayTransactionStatus());
				getReceiver().processRawMessage(listener, message, threadContext);
//				if (log.isDebugEnabled()) log.debug("transaction status after: "+JtaUtil.displayTransactionStatus());
			} catch (ListenerException e) {
				getReceiver().increaseRetryIntervalAndWait(e,getLogPrefix());
				if (txStatus!=null) {
					txStatus.setRollbackOnly();
				} else {
					JMSException jmse = new JMSException(getLogPrefix()+"caught exception: "+e.getMessage());
					jmse.setLinkedException(e);
					throw jmse;
				}
//				if (JtaUtil.inTransaction()) {
//					log.warn(getLogPrefix()+"caught exception processing message, setting rollbackonly", e);
//					JtaUtil.setRollbackOnly();
//				} else {
//					if (jmsContainer.isSessionTransacted()) {
//						log.warn(getLogPrefix()+"caught exception processing message, rolling back JMS session", e);
//						session.rollback();
//					} else {
//						JMSException jmse = new JMSException(getLogPrefix()+"caught exception, no transactional stuff to rollback");
//						jmse.initCause(e);
//						throw jmse;
//					}
//				}
			} finally {
				if (txStatus==null && jmsContainer.isSessionTransacted()) {
					log.debug(getLogPrefix()+"committing JMS session");
					session.commit();
				}
			}
		} finally {
			if (txStatus!=null) {
				txManager.commit(txStatus);
			}
			threadsProcessing.decrease();
			if (log.isInfoEnabled()) {
				long onMessageEnd= System.currentTimeMillis();

				log.info(getLogPrefix()+"A) JMSMessageTime ["+DateUtils.format(jmsTimestamp)+"]");
				log.info(getLogPrefix()+"B) onMessageStart ["+DateUtils.format(onMessageStart)+"] diff (~'queing' time) ["+(onMessageStart-jmsTimestamp)+"]");
				log.info(getLogPrefix()+"C) onMessageEnd   ["+DateUtils.format(onMessageEnd)+"] diff (process time) ["+(onMessageEnd-onMessageStart)+"]");
			}
			
//			boolean simulateCrashAfterCommit=true;
//			if (simulateCrashAfterCommit) {
//				toggle=!toggle;
//				if (toggle) {
//					JtaUtil.setRollbackOnly();
//					throw new JMSException("simulate crash just before final commit");
//				}
//			}
		}
	}

//	private boolean toggle=true;

	public void onException(JMSException e) {
		IbisExceptionListener ibisExceptionListener = getExceptionListener();
		if (ibisExceptionListener!= null) {
			ibisExceptionListener.exceptionThrown(getListener(), e);
		} else {
			log.error(getLogPrefix()+"Cannot report the error to an IBIS Exception Listener", e);
		}
	}


	public boolean isThreadCountReadable() {
		return jmsContainer!=null;
	}
	public boolean isThreadCountControllable() {
		return jmsContainer!=null;
	}

	public int getCurrentThreadCount() {
		if (jmsContainer!=null) {
			return jmsContainer.getActiveConsumerCount();
		}
		return 0;
	}

	public int getMaxThreadCount() {
		if (jmsContainer!=null) {
			return jmsContainer.getMaxConcurrentConsumers();
		}
		return 0;
	}

	public void increaseThreadCount() {
		if (jmsContainer!=null) {
			jmsContainer.setMaxConcurrentConsumers(jmsContainer.getMaxConcurrentConsumers()+1);	
		}
	}

	public void decreaseThreadCount() {
		if (jmsContainer!=null) {
			int current=getMaxThreadCount();
			if (current>1) {
				jmsContainer.setMaxConcurrentConsumers(current-1);	
			}
		}
	}


	public String getLogPrefix() {
		String result="SpringJmsContainer ";
		if (getListener()!=null && getListener().getReceiver()!=null) {
			result += "of Receiver ["+getListener().getReceiver().getName()+"] ";
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}


	public void setTxManager(PlatformTransactionManager txManager) {
		this.txManager = txManager;
	}
	public PlatformTransactionManager getTxManager() {
		return txManager;
	}


	public void setMessageListenerClassName(String messageListenerClassName) {
		this.messageListenerClassName = messageListenerClassName;
	}
	public String getMessageListenerClassName() {
		return messageListenerClassName;
	}

}
