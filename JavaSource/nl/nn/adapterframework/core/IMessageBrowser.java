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
 * $Log: IMessageBrowser.java,v $
 * Revision 1.8  2011-11-30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2011/03/16 16:36:59  gerrit
 * added getIterator() with time and order parameters
 *
 * Revision 1.5  2009/12/23 17:03:52  gerrit
 * modified MessageBrowsing interface to reenable and improve export of messages
 *
 * Revision 1.4  2009/03/13 14:23:22  peter
 * added method GetExpiryDate
 *
 * Revision 1.3  2005/12/28 08:33:22  gerrit
 * corrected javadoc
 *
 * Revision 1.2  2005/07/19 12:14:30  gerrit
 * introduction of IMessageBrowsingIterator
 *
 * Revision 1.1  2004/06/16 12:25:52  johan
 * Initial version of Queue browsing functionality
 *
 *
 */
package nl.nn.adapterframework.core;

import java.util.Date;


/**
 * @author  Gerrit van Brakel
 * @since   4.3
 * @version $Id$
 */
public interface IMessageBrowser extends IXAEnabled {
	public static final String version = "$RCSfile: IMessageBrowser.java,v $ $Revision: 1.8 $ $Date: 2011-11-30 13:51:55 $";

	/**
	 * Gets an enumeration of messages. This includes setting up connections, sessions etc.
	 */
	IMessageBrowsingIterator getIterator() throws ListenerException;
	IMessageBrowsingIterator getIterator(Date startTime, Date endTime, boolean forceDescending) throws ListenerException;
	
	/**
	 * Retrieves the message context as an iteratorItem.
	 * The result can be used in the methods above that use an iteratorItem as 
	 */
	IMessageBrowsingIteratorItem getContext(String messageId) throws ListenerException;

	/**
	 * Retrieves the message, but does not delete. 
	 */
	Object browseMessage(String messageId) throws ListenerException;
	/**
	 * Retrieves and deletes the message.
	 */
	Object getMessage(String messageId) throws ListenerException;
	/**
	 * Deletes the message.
	 */
	void   deleteMessage(String messageId) throws ListenerException;

}

