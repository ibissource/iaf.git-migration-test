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
 * $Log: SenderWithParametersBase.java,v $
 * Revision 1.8  2011-11-30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2010/12/13 13:14:07  gerrit
 * removed unused code
 * added documentation
 *
 * Revision 1.5  2009/12/04 18:23:34  jaco
 * Added ibisDebugger.senderAbort and ibisDebugger.pipeRollback
 *
 * Revision 1.4  2007/02/26 16:53:38  gerrit
 * add throws clause to open and close
 *
 * Revision 1.3  2007/02/12 13:44:09  gerrit
 * Logger from LogUtil
 *
 * Revision 1.2  2005/08/30 15:55:43  gerrit
 * added log and getLogPrefix()
 *
 * Revision 1.1  2005/06/20 08:58:13  gerrit
 * introduction of SenderWithParametersBase
 *
 *
 */
package nl.nn.adapterframework.core;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.ParameterList;
import nl.nn.adapterframework.senders.SenderBase;

/**
 * Provides a base class for senders with parameters.
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Sender</td><td>&nbsp;</td></tr>
 * </table>
 * 
 * @author Gerrit van Brakel
 * @since  4.3
 * @version $Id$
 */
public abstract class SenderWithParametersBase extends SenderBase implements ISenderWithParameters {
	public static final String version="$RCSfile: SenderWithParametersBase.java,v $ $Revision: 1.8 $ $Date: 2011-11-30 13:51:55 $";
	
	protected ParameterList paramList = null;

	public void configure() throws ConfigurationException {
		if (paramList!=null) {
			paramList.configure();
		}
	}

	public String sendMessage(String correlationID, String message) throws SenderException, TimeOutException  {
		return sendMessage(correlationID,message,null);
	}

	public void addParameter(Parameter p) {
		if (paramList==null) {
			paramList=new ParameterList();
		}
		paramList.add(p);
	}

}
