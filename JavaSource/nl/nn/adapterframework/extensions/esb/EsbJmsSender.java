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
 * $Log: EsbJmsSender.java,v $
 * Revision 1.7  2012-08-31 13:54:04  peter
 * changed derivation for soapAction attribute
 *
 * Revision 1.6  2012/08/30 14:08:24  peter
 * bugfix for soapAction attribute
 *
 * Revision 1.5  2012/08/30 14:02:39  peter
 * *** empty log message ***
 *
 * Revision 1.4  2012/04/13 13:45:15  peter
 * adjusted javadoc
 *
 * Revision 1.3  2012/01/06 13:29:51  peter
 * don't override soapAction when set
 *
 * Revision 1.2  2012/01/05 10:25:13  peter
 * corrected javadoc
 *
 * Revision 1.1  2012/01/05 09:59:16  peter
 * initial version
 *
 *
 */
package nl.nn.adapterframework.extensions.esb;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.jms.JmsSender;
import nl.nn.adapterframework.parameters.Parameter;

import org.apache.commons.lang.StringUtils;

/**
 * ESB (Enterprise Service Bus) extension of JmsSender.
 *
 * <p><b>Configuration </b><i>(where deviating from JmsSender)</i><b>:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.extensions.bis.BisSoapJmsSender</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMessageProtocol(String) messageProtocol}</td><td>protocol of ESB service to be called. Possible values 
 * <ul>
 *   <li>"FF": Fire & Forget protocol</li>
 *   <li>"RR": Request-Reply protocol</li>
 * </ul></td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setTimeOut(long) timeOut}</td><td>receiver timeout, in milliseconds</td><td>20000 (20s)</td></tr>
 * <tr><td>{@link #setMessageTimeToLive(String) messageTimeToLive}</td><td>if messageProtocol=<code>RR</code>: </td><td>{@link #setTimeOut(long) timeOut}</td></tr>
 * <tr><td>{@link #setReplyTimeout(String) replyTimeout}</td><td>if messageProtocol=<code>RR</code>: </td><td>{@link #setTimeOut(long) timeOut}</td></tr>
 * <tr><td>{@link #setSynchronous(String) synchronous}</td><td>if messageProtocol=<code>RR</code>: </td><td><code>true</code></td></tr>
 * <tr><td>{@link #setSoapAction(String) soapAction}</td><td>&nbsp;</td><td>if empty then derived from the element MessageHeader/To/Location in the SOAP header of the input message (if $messagingLayer='P2P' then '$applicationName_$applicationFunction' else '$operationName_$operationVersion)</td></tr>
 * </table></p>
 * 
 * @author  Peter Leeuwenburgh
 * @version $Id$
 */
public class EsbJmsSender extends JmsSender {
	private final static String REQUEST_REPLY = "RR";
	private final static String FIRE_AND_FORGET = "FF";

	private String messageProtocol = null;
	private long timeOut = 20000;

	public void configure() throws ConfigurationException {
		if (getMessageProtocol() == null) {
			throw new ConfigurationException(getLogPrefix() + "messageProtocol must be set");
		}
		if (!getMessageProtocol().equalsIgnoreCase(REQUEST_REPLY) && !getMessageProtocol().equalsIgnoreCase(FIRE_AND_FORGET)) {
			throw new ConfigurationException(getLogPrefix() + "illegal value for messageProtocol [" + getMessageProtocol() + "], must be '" + REQUEST_REPLY + "' or '" + FIRE_AND_FORGET + "'");
		}
		if (getMessageProtocol().equalsIgnoreCase(REQUEST_REPLY)) {
			setMessageTimeToLive(getTimeOut());
			setReplyTimeout((int) getTimeOut());
			setSynchronous(true);
		} else {
			if (getReplyTo() != null) {
				throw new ConfigurationException(getLogPrefix() + "replyToName [" + getReplyTo() + "] must not be set for messageProtocol [" + getMessageProtocol() + "]");
			}
		}
		if (StringUtils.isEmpty(getSoapAction()) && (paramList==null || paramList.findParameter("SoapAction")==null)) {
			Parameter p = new Parameter();
			p.setName("SoapAction");
			p.setStyleSheetName("/xml/xsl/esb/soapAction.xsl");
			p.setRemoveNamespaces(true);
			addParameter(p);
		}
		super.configure();
	}

	public void setMessageProtocol(String string) {
		messageProtocol = string;
	}

	public String getMessageProtocol() {
		return messageProtocol;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long l) {
		timeOut = l;
	}
}
