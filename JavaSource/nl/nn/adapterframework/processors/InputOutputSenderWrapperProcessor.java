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
 * $Log: InputOutputSenderWrapperProcessor.java,v $
 * Revision 1.3  2011-11-30 13:51:54  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2010/09/13 14:02:11  gerrit
 * split SenderWrapper-processing in chain of processors
 *
 */
package nl.nn.adapterframework.processors;

import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.core.TimeOutException;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;
import nl.nn.adapterframework.senders.SenderWrapperBase;

import org.apache.commons.lang.StringUtils;

/**
 * @author  Gerrit van Brakel
 * @since   4.11
 * @version $Id$
 */
public class InputOutputSenderWrapperProcessor extends SenderWrapperProcessorBase {

	public String sendMessage(SenderWrapperBase senderWrapperBase, String correlationID, String message, ParameterResolutionContext prc) throws SenderException, TimeOutException {
		String senderInput=(String)message;
		if (StringUtils.isNotEmpty(senderWrapperBase.getGetInputFromSessionKey())) {
			senderInput=(String)prc.getSession().get(senderWrapperBase.getGetInputFromSessionKey());
			if (log.isDebugEnabled()) log.debug(senderWrapperBase.getLogPrefix()+"set contents of session variable ["+senderWrapperBase.getGetInputFromSessionKey()+"] as input ["+senderInput+"]");
		} else {
			if (StringUtils.isNotEmpty(senderWrapperBase.getGetInputFromFixedValue())) {
				senderInput=senderWrapperBase.getGetInputFromFixedValue();
				if (log.isDebugEnabled()) log.debug(senderWrapperBase.getLogPrefix()+"set input to fixed value ["+senderInput+"]");
			}
		}
		String result = senderWrapperProcessor.sendMessage(senderWrapperBase, correlationID, message, prc);
		if (StringUtils.isNotEmpty(senderWrapperBase.getStoreResultInSessionKey())) {
			if (log.isDebugEnabled()) log.debug(senderWrapperBase.getLogPrefix()+"storing results in session variable ["+senderWrapperBase.getStoreResultInSessionKey()+"]");
			prc.getSession().put(senderWrapperBase.getStoreResultInSessionKey(),result);
		}
		return senderWrapperBase.isPreserveInput()?(String)message:result;
	}

}
