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
 * $Log: ITransactionalStorage.java,v $
 * Revision 1.12  2011-11-30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.10  2009/12/29 14:55:43  peter
 * increased IBISSTORE with the field LABEL for adding user data
 *
 * Revision 1.9  2009/12/23 17:05:16  gerrit
 * modified MessageBrowsing interface to reenable and improve export of messages
 *
 * Revision 1.8  2008/07/24 12:04:04  gerrit
 * added messageCount
 *
 * Revision 1.7  2008/01/11 14:48:49  gerrit
 * added getTypeString() and getHostString()
 *
 * Revision 1.6  2007/10/09 15:33:32  gerrit
 * copy changes from Ibis-EJB:
 * added containsMessageId()
 *
 * Revision 1.5  2007/06/12 11:19:42  gerrit
 * added set/getType
 *
 * Revision 1.4  2007/05/23 09:07:57  gerrit
 * added attributes slotId and active
 *
 * Revision 1.3  2005/07/19 12:20:44  gerrit
 * adapted to work extend IMessageBrowser
 *
 * Revision 1.2  2004/03/26 10:42:44  johan
 * added @version tag in javadoc
 *
 * Revision 1.1  2004/03/23 16:50:49  gerrit
 * initial version
 *
 */
package nl.nn.adapterframework.core;

import java.io.Serializable;
import java.util.Date;

import nl.nn.adapterframework.configuration.ConfigurationException;

/**
 * The <code>ITransactionalStorage</code> is responsible for storing and 
 * retrieving-back messages under transaction control.
 * @see nl.nn.adapterframework.receivers.PullingReceiverBase
 * @author  Gerrit van Brakel
 * @since   4.1
 * @version $Id$
*/
public interface ITransactionalStorage extends IMessageBrowser, INamedObject {
	public static final String version = "$RCSfile: ITransactionalStorage.java,v $ $Revision: 1.12 $ $Date: 2011-11-30 13:51:55 $";

	/**
	 * Prepares the object for operation. After this
	 * method is called the storeMessage() and retrieveMessage() methods may be called
	 */ 
	public void open() throws SenderException, ListenerException;
	public void close() throws SenderException, ListenerException;
	
	public void configure() throws ConfigurationException;

	/**
	 * Store the message, returns new messageId.
	 * 
	 * The messageId should be unique.
	 */
	public String storeMessage(String messageId, String correlationId, Date receivedDate, String comments, String label, Serializable message) throws SenderException;
	
    /**
     * Check if the storage contains message with the given original messageId
     * (as passed to storeMessage).
     */
    public boolean containsMessageId(String originalMessageId) throws ListenerException;
    
	public void setName(String name);

	/**
	 *  slotId allows using component to define a kind of 'subsection'.
	 */	
	public String getSlotId();
	public void setSlotId(String string);


	/**
	 *  type is one character: E for error, I for inprocessStorage, L for logging.
	 */	
	public String getType();
	public void setType(String string);
	
	public boolean isActive();
	
	public int getMessageCount() throws ListenerException;

}
