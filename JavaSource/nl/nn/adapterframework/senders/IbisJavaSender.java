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
 * $Log: IbisJavaSender.java,v $
 * Revision 1.8  2011-11-30 13:52:00  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:51  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2010/09/07 15:55:13  jaco
 * Removed IbisDebugger, made it possible to use AOP to implement IbisDebugger functionality.
 *
 * Revision 1.5  2010/03/10 14:30:05  peter
 * rolled back testtool adjustments (IbisDebuggerDummy)
 *
 * Revision 1.3  2009/12/04 18:23:34  jaco
 * Added ibisDebugger.senderAbort and ibisDebugger.pipeRollback
 *
 * Revision 1.2  2009/11/18 17:28:04  jaco
 * Added senders to IbisDebugger
 *
 * Revision 1.1  2008/08/06 16:36:39  gerrit
 * moved from pipes to senders package
 *
 */
package nl.nn.adapterframework.senders;

import java.util.HashMap;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.HasPhysicalDestination;
import nl.nn.adapterframework.core.ParameterException;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.core.SenderWithParametersBase;
import nl.nn.adapterframework.core.TimeOutException;
import nl.nn.adapterframework.dispatcher.DispatcherManager;
import nl.nn.adapterframework.dispatcher.DispatcherManagerFactory;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;
import nl.nn.adapterframework.util.Misc;

import org.apache.commons.lang.StringUtils;

/**
 * Posts a message to another IBIS-adapter in the same JVM.
 *
 * An IbisJavaSender makes a call to a Receiver a {@link nl.nn.adapterframework.receivers.JavaListener JavaListener}
 * or any other application in the same JVM that has registered a <code>RequestProcessor</code> with the IbisServiceDispatcher. 
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.pipes.IbisLocalSender</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the sender</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setServiceName(String) serviceName}</td><td>serviceName of the 
 * {@link nl.nn.adapterframework.receivers.JavaListener JavaListener} that should be called</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setReturnedSessionKeys(String) returnedSessionKeys}</td><td>comma separated list of keys of session variables that should be returned to caller, for correct results as well as for erronous results. (Only for listeners that support it, like JavaListener)</td><td>&nbsp;</td></tr>
 * </table>
 * </p>
 * Any parameters are copied to the PipeLineSession of the service called.
 * 
 * <h4>configuring IbisJavaSender and JavaListener</h4>
 * <ul>
 *   <li>Define a GenericMessageSendingPipe with an IbisJavaSender</li>
 *   <li>Set the attribute <code>serviceName</code> to <i>yourExternalServiceName</i></li>
 * </ul>
 * In the Adapter to be called:
 * <ul>
 *   <li>Define a Receiver with a JavaListener</li>
 *   <li>Set the attribute <code>serviceName</code> to <i>yourExternalServiceName</i></li>
 * </ul>
 * N.B. Please make sure that the IbisServiceDispatcher-1.1.jar is present on the class path of the server.
 *
 * @author  Gerrit van Brakel
 * @since   4.4.5
 * @version $Id$
 */
public class IbisJavaSender extends SenderWithParametersBase implements HasPhysicalDestination {
	
	private String name;
	private String serviceName;
	private String returnedSessionKeys=null;

	public void configure() throws ConfigurationException {
		super.configure();
		if (StringUtils.isEmpty(getServiceName())) {
			throw new ConfigurationException(getLogPrefix()+"must specify serviceName");
		}
	}

	public String getPhysicalDestinationName() {
		return "JavaListener "+getServiceName();
	}

	public boolean isSynchronous() {
		return true;
	}

	public String sendMessage(String correlationID, String message, ParameterResolutionContext prc) throws SenderException, TimeOutException {
		String result = null;
		HashMap context = null;
		try {
			if (paramList!=null) {
				context = prc.getValueMap(paramList);
			} else {
				context=new HashMap();			
			}
			DispatcherManager dm = DispatcherManagerFactory.getDispatcherManager();
			result = dm.processRequest(getServiceName(),correlationID, message, context);
		} catch (ParameterException e) {
			throw new SenderException(getLogPrefix()+"exception evaluating parameters",e);
		} catch (Exception e) {
			throw new SenderException(getLogPrefix()+"exception processing message using request processor ["+getServiceName()+"]",e);
		} finally {
			if (log.isDebugEnabled() && StringUtils.isNotEmpty(getReturnedSessionKeys())) {
				log.debug("returning values of session keys ["+getReturnedSessionKeys()+"]");
			}
			if (prc!=null) {
				Misc.copyContext(getReturnedSessionKeys(),context, prc.getSession());
			}
		}
		return result;
	}


	public void setName(String name) {
		this.name=name;
	}
	public String getName() {
		return name;
	}


	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String string) {
		serviceName = string;
	}

	public void setReturnedSessionKeys(String string) {
		returnedSessionKeys = string;
	}
	public String getReturnedSessionKeys() {
		return returnedSessionKeys;
	}

}
