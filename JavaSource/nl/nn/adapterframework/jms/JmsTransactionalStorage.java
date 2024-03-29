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
 * $Log: JmsTransactionalStorage.java,v $
 * Revision 1.16  2011-11-30 13:51:51  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:48  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.14  2010/01/04 15:05:47  peter
 * added label
 *
 * Revision 1.13  2009/12/29 14:56:01  peter
 * increased IBISSTORE with the field LABEL for adding user data
 *
 * Revision 1.12  2009/12/23 17:09:57  gerrit
 * modified MessageBrowsing interface to reenable and improve export of messages
 *
 * Revision 1.11  2008/01/11 14:51:55  gerrit
 * added getTypeString() and getHostString()
 *
 * Revision 1.10  2007/10/09 15:35:19  gerrit
 * copy changes from Ibis-EJB:
 * added containsMessageId()
 *
 * Revision 1.9  2007/06/12 11:21:34  gerrit
 * adapted to new functionality
 *
 * Revision 1.8  2007/05/23 09:16:08  gerrit
 * added attribute 'active'
 *
 * Revision 1.7  2005/12/20 16:59:26  gerrit
 * implemented support for connection-pooling
 *
 * Revision 1.6  2005/10/20 15:44:51  gerrit
 * modified JMS-classes to use shared connections
 * open()/close() became openFacade()/closeFacade()
 *
 * Revision 1.5  2005/08/04 15:40:30  gerrit
 * fixed slotId code
 *
 * Revision 1.4  2005/07/28 07:38:10  gerrit
 * added slotId attribute
 *
 * Revision 1.3  2005/07/19 15:12:40  gerrit
 * adapted to an implementation extending IMessageBrowser
 *
 * Revision 1.2  2004/03/26 10:42:54  johan
 * added @version tag in javadoc
 *
 * Revision 1.1  2004/03/23 18:02:25  gerrit
 * initial version
 *
 */
package nl.nn.adapterframework.jms;

import java.io.Serializable;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.lang.StringUtils;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.ITransactionalStorage;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.core.SenderException;

/**
 * JMS implementation of <code>ITransactionalStorage</code>.
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.jms.JmsTransactionalStorage</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSlotId(String) slotId}</td><td>optional identifier for this storage, to be able to share the physical storage between a number of receivers</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setTimeOut(long) timeOut}</td><td>timeout for receiving messages from queue</td><td>3000 [ms]</td></tr>
 * <tr><td>{@link #setDestinationName(String) destinationName}</td><td>JNDI name of the queue to store messages on</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setJmsRealm(String) jmsRealm}</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * </table>
 * </p>
 * @version $Id$
 * @author  Gerrit van Brakel
 * @since   4.1
 */
public class JmsTransactionalStorage extends JmsMessageBrowser implements ITransactionalStorage {
	public static final String version = "$RCSfile: JmsTransactionalStorage.java,v $ $Revision: 1.16 $ $Date: 2011-11-30 13:51:51 $";

	public static final String FIELD_TYPE="type";
	public static final String FIELD_ORIGINAL_ID="originalId";
	public static final String FIELD_RECEIVED_DATE="receivedDate";
	public static final String FIELD_COMMENTS="comments";
	public static final String FIELD_SLOTID="SlotId";
	public static final String FIELD_HOST="host";
	public static final String FIELD_LABEL="label";

	private String slotId=null;
	private String type=null;
	private boolean active=true;   

	public JmsTransactionalStorage() {
		super();
		setTransacted(true);
		setPersistent(true);
		setDestinationType("QUEUE");
	}

	public void configure() throws ConfigurationException {
	}
	
	public void open() throws ListenerException {
		try {
			super.openFacade();
		} catch (Exception e) {
			throw new ListenerException(e);
		}
	}
	
	public void close() throws ListenerException {
		try {
			closeFacade();
		} catch (Exception e) {
			throw new ListenerException(e);
		}
	}
	
	

	public String storeMessage(String messageId, String correlationId, Date receivedDate, String comments, String label, Serializable message) throws SenderException {
		Session session=null;
		try {
			session = createSession();
			ObjectMessage msg = session.createObjectMessage(message);
			msg.setStringProperty(FIELD_TYPE,getType());
			msg.setStringProperty(FIELD_ORIGINAL_ID,messageId);
			msg.setJMSCorrelationID(correlationId);
			msg.setLongProperty(FIELD_RECEIVED_DATE,receivedDate.getTime());
			msg.setStringProperty(FIELD_COMMENTS,comments);
			if (StringUtils.isNotEmpty(getSlotId())) {
				msg.setStringProperty(FIELD_SLOTID,getSlotId());
			}
			msg.setStringProperty(FIELD_LABEL,label);
			return send(session,getDestination(),msg);
		} catch (Exception e) {
			throw new SenderException(e);
		} finally {
			closeSession(session);
		}
	}

    public boolean containsMessageId(String originalMessageId) throws ListenerException {
        Object msg = doBrowse(FIELD_ORIGINAL_ID, originalMessageId);
        return msg != null;
    }
    
	public Object browseMessage(String messageId) throws ListenerException {
		try {
			ObjectMessage msg=(ObjectMessage)super.browseMessage(messageId);
			return msg.getObject();
		} catch (JMSException e) {
			throw new ListenerException(e);
		}
	}

	public Object getMessage(String messageId) throws ListenerException {
		try {
			ObjectMessage msg=(ObjectMessage)super.getMessage(messageId);
		return msg.getObject();
		} catch (JMSException e) {
			throw new ListenerException(e);
		}
	}




	public String getSelector() {
		if (StringUtils.isEmpty(getSlotId())) {
			return null; 
		}
		return FIELD_SLOTID+"='"+getSlotId()+"'";
	}


	public void setSlotId(String string) {
		slotId = string;
	}
	public String getSlotId() {
		return slotId;
	}

	public void setType(String string) {
		type = string;
	}
	public String getType() {
		return type;
	}

	public void setActive(boolean b) {
		active = b;
	}
	public boolean isActive() {
		return active;
	}
	
}
