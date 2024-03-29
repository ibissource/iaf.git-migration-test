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
 * $Log: SenderWrapper.java,v $
 * Revision 1.12  2011-12-08 13:02:00  peter
 * fixed javadoc
 *
 * Revision 1.11  2011/11/30 13:52:00  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:51  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.9  2010/09/13 14:07:54  gerrit
 * call super in open() and close()
 *
 * Revision 1.8  2010/09/07 15:55:13  jaco
 * Removed IbisDebugger, made it possible to use AOP to implement IbisDebugger functionality.
 *
 * Revision 1.7  2009/12/29 14:37:28  gerrit
 * modified imports to reflect move of statistics classes to separate package
 *
 * Revision 1.6  2009/06/05 07:29:32  gerrit
 * added throws clause to iterateOverStatistics()
 *
 * Revision 1.5  2008/09/04 12:16:03  gerrit
 * collect interval statistics
 *
 * Revision 1.4  2008/08/27 16:22:40  gerrit
 * added reset option to statisticsdump
 *
 * Revision 1.3  2008/07/17 16:18:46  gerrit
 * call sender.configure in configure
 *
 * Revision 1.2  2008/05/21 10:41:50  gerrit
 * added documentation
 *
 * Revision 1.1  2008/05/15 15:08:26  gerrit
 * created senders package
 * moved some sender to senders package
 * created special senders
 *
 */
package nl.nn.adapterframework.senders;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.ISender;
import nl.nn.adapterframework.core.ISenderWithParameters;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.core.TimeOutException;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;
import nl.nn.adapterframework.statistics.HasStatistics;
import nl.nn.adapterframework.statistics.StatisticsKeeperIterationHandler;

/**
 * Wrapper for senders, that allows to get input from a session variable, and to store output in a session variable.
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>className</td><td>nl.nn.adapterframework.senders.SenderWrapper</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setGetInputFromSessionKey(String) getInputFromSessionKey}</td><td>when set, input is taken from this session key, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setGetInputFromFixedValue(String) getInputFromFixedValue}</td><td>when set, this fixed value is taken as input, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setStoreResultInSessionKey(String) storeResultInSessionKey}</td><td>when set, the result is stored under this session key</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setPreserveInput(boolean) preserveInput}</td><td>when set <code>true</code>, the input of a pipe is restored before processing the next one</td><td>false</td></tr>
 * </table>
 * </p>
 * <table border="1">
 * <tr><th>nested elements</th><th>description</th></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ISender sender}</td><td>specification of sender to send messages with</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.parameters.Parameter param}</td><td>any parameters defined on the senderwrapper will be handed to the sender, if this is a {@link nl.nn.adapterframework.core.ISenderWithParameters ISenderWithParameters}</td></tr>
 * </table>
 * </p>
 * 
 * @author  Gerrit van Brakel
 * @since   4.9
 * @version $Id$
 */
public class SenderWrapper extends SenderWrapperBase {
	private ISender sender;
	
	protected boolean isSenderConfigured() {
		return getSender()!=null;
	}

	public void configure() throws ConfigurationException {
		super.configure();
		getSender().configure();
	}
	public void open() throws SenderException {
		getSender().open();
		super.open();
	}
	public void close() throws SenderException {
		super.close();
		getSender().close();
	}

	public String doSendMessage(String correlationID, String message, ParameterResolutionContext prc) throws SenderException, TimeOutException {
		String result;
		if (sender instanceof ISenderWithParameters) {
			result = ((ISenderWithParameters)sender).sendMessage(correlationID,message,prc);
		} else {
			result = sender.sendMessage(correlationID,message);
		}
		return result;
	}

	public void iterateOverStatistics(StatisticsKeeperIterationHandler hski, Object data, int action) throws SenderException {
		if (getSender() instanceof HasStatistics) {
			((HasStatistics)getSender()).iterateOverStatistics(hski,data,action);
		}
	}

	public boolean isSynchronous() {
		return getSender().isSynchronous();
	}

	public void setSender(ISender sender) {
		this.sender=sender;
	}
	protected ISender getSender() {
		return sender;
	}

}
