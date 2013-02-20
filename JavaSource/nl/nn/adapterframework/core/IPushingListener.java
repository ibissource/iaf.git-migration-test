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
 * $Log: IPushingListener.java,v $
 * Revision 1.6  2011-11-30 13:51:55  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2005/07/05 11:04:57  gerrit
 * updated javadoc
 *
 * Revision 1.3  2004/09/08 14:15:11  gerrit
 * removed unused imports
 *
 * Revision 1.2  2004/08/03 13:09:51  gerrit
 * moved afterMessageProcessed to IListener
 *
 * Revision 1.1  2004/07/15 07:38:22  gerrit
 * introduction of IListener as common root for Pulling and Pushing listeners
 *
 * Revision 1.2  2004/06/30 10:04:17  gerrit
 * added INamedObject implementation, added setExceptionListener
 *
 * Revision 1.1  2004/06/22 11:52:44  gerrit
 * first version
 *
 */
package nl.nn.adapterframework.core;

/**
 * Defines listening behaviour of message driven receivers.
 * 
 * @version $Id$
 * @author Gerrit van Brakel
 * @since 4.2
 */
public interface IPushingListener extends IListener {
	public static final String version = "$RCSfile: IPushingListener.java,v $ $Revision: 1.6 $ $Date: 2011-11-30 13:51:55 $";


	/**
	 * Set the handler that will do the processing of the message.
	 * Each of the received messages must be pushed through handler.processMessage()
	 */
	void setHandler(IMessageHandler handler);
	
	/**
	 * Set a (single) listener that will be notified of any exceptions.
	 * The listener should use this listener to notify the receiver of 
	 * any exception that occurs outside the processing of a message.
	 */
	void setExceptionListener(IbisExceptionListener listener);

}
