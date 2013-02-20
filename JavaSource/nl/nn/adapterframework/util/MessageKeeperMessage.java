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
 * $Log: MessageKeeperMessage.java,v $
 * Revision 1.7  2011-11-30 13:51:49  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.5  2005/10/18 08:17:14  gerrit
 * corrected version string
 * cosmetic changes
 *
 */
package nl.nn.adapterframework.util;

import java.util.Date;
/**
 * A message for the MessageKeeper. <br/>
 * Although this could be an inner class of the MessageKeeper,
 * it's made "standalone" to provide the use of iterators and
 * enumerators with the MessageKeeper.
 * @version $Id$
 * @author Johan Verrips IOS
 */
public class MessageKeeperMessage {
	public static final String version="$RCSfile: MessageKeeperMessage.java,v $ $Revision: 1.7 $ $Date: 2011-11-30 13:51:49 $";

	private Date messageDate=new Date();
	private String messageText;
	
	/**
	* Set the messagetext of this message. The text will be xml-encoded.
	*/
	public MessageKeeperMessage(String message){
	//	this.messageText=XmlUtils.encodeChars(message);
		this.messageText=message;
	}
	/**
	* Set the messagetext and -date of this message. The text will be xml-encoded.
	*/
	public MessageKeeperMessage(String message, Date date) {
	//	this.messageText=XmlUtils.encodeChars(message);
		this.messageText=message;
		this.messageDate=date;
	}
	public Date getMessageDate() {
		return messageDate;
	}
	public String getMessageText() {
		return messageText;
	}
}
