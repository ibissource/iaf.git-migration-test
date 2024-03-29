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
 * $Log: TibcoMessagingSource.java,v $
 * Revision 1.4  2012-09-07 13:15:16  jaco
 * Messaging related changes:
 * - Use CACHE_CONSUMER by default for ESB RR
 * - Don't use JMSXDeliveryCount to determine whether message has already been processed
 * - Added maxDeliveries
 * - Delay wasn't increased when unable to write to error store (it was reset on every new try)
 * - Don't call session.rollback() when isTransacted() (it was also called in afterMessageProcessed when message was moved to error store)
 * - Some cleaning along the way like making some synchronized statements unnecessary
 * - Made BTM and ActiveMQ work for testing purposes
 *
 * Revision 1.3  2011/11/30 13:51:58  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:54  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2010/01/28 14:49:29  gerrit
 * *** empty log message ***
 *
 * Revision 1.3  2008/07/24 12:30:05  gerrit
 * added support for authenticated JMS
 *
 * Revision 1.2  2008/05/15 14:53:12  gerrit
 * remove unnecessary overridden code
 *
 * Revision 1.1  2008/05/15 14:32:58  gerrit
 * first version
 *
 */
package nl.nn.adapterframework.extensions.tibco;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TopicSession;
import javax.naming.Context;

import nl.nn.adapterframework.jms.JmsException;
import nl.nn.adapterframework.jms.JmsMessagingSource;
import nl.nn.adapterframework.jms.MessagingSource;

import org.apache.commons.lang.StringUtils;

import com.tibco.tibjms.TibjmsConnectionFactory;

/**
 * {@link MessagingSource} for Tibco connections.
 * 
 * @author 	Gerrit van Brakel
 * @since   4.9
 * @version $Id$
 */
public class TibcoMessagingSource extends JmsMessagingSource {
	
	private TibjmsConnectionFactory connectionFactory;
	
	public TibcoMessagingSource(String connectionFactoryName, Context context, ConnectionFactory connectionFactory, Map messagingSourceMap, String authAlias) {
		super(connectionFactoryName, "", context, connectionFactory, messagingSourceMap, authAlias, false, true);
		this.connectionFactory=(TibjmsConnectionFactory)connectionFactory;
	}

	protected Connection createConnection() throws JMSException {
		if (StringUtils.isNotEmpty(getAuthAlias())) {
			return super.createConnection();
		}
		String userName=null;
		String password=null;
		return connectionFactory.createConnection(userName,password);
	}


	public Destination lookupDestination(String destinationName) throws JmsException {
		Session session=null;		
		try {
			session = createSession(false,Session.AUTO_ACKNOWLEDGE);
			log.debug("Session class ["+session.getClass().getName()+"]");
			Destination destination;

			/* create the destination */
			if (session instanceof TopicSession) {
				destination = ((TopicSession)session).createTopic(destinationName);
			} else {
				destination = ((QueueSession)session).createQueue(destinationName);
			}

			return destination;
		} catch (Exception e) {
			throw new JmsException("cannot create destination", e);
		} finally {
			releaseSession(session);
		}
	}
	
}
