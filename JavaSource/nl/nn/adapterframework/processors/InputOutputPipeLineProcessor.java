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
 * $Log: InputOutputPipeLineProcessor.java,v $
 * Revision 1.7  2012-06-01 10:52:49  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.6  2011/11/30 13:51:54  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2011/08/22 14:29:58  gerrit
 * added first pipe to interface
 *
 * Revision 1.3  2011/08/18 14:41:00  gerrit
 * now extends PipeLineProcessorBase
 *
 * Revision 1.2  2010/09/07 15:55:13  jaco
 * Removed IbisDebugger, made it possible to use AOP to implement IbisDebugger functionality.
 *
 */
package nl.nn.adapterframework.processors;

import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeLine;
import nl.nn.adapterframework.core.PipeLineResult;
import nl.nn.adapterframework.core.PipeLineSessionBase;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.util.Misc;

/**
 * @author Jaco de Groot
 * @version $Id$
 */
public class InputOutputPipeLineProcessor extends PipeLineProcessorBase {
	
	public PipeLineResult processPipeLine(PipeLine pipeLine, String messageId,
			String message, IPipeLineSession pipeLineSession, String firstPipe
			) throws PipeRunException {
		if (pipeLineSession==null) {
			pipeLineSession= new PipeLineSessionBase();
		}
		// reset the PipeLineSession and store the message and its id in the session
		if (messageId==null) {
				messageId=Misc.createSimpleUUID();
				log.error("null value for messageId, setting to ["+messageId+"]");
	
		}
		if (message == null) {
			throw new PipeRunException(null, "Pipeline of adapter ["+ pipeLine.getOwner().getName()+"] received null message");
		}
		// store message and messageId in the pipeLineSession
		pipeLineSession.put(IPipeLineSession.originalMessageKey, message);
		pipeLineSession.put(IPipeLineSession.messageIdKey, messageId);
		return pipeLineProcessor.processPipeLine(pipeLine, messageId, message, pipeLineSession, firstPipe);
	}

}
