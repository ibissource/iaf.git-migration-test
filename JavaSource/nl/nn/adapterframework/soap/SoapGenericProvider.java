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
 * $Log: SoapGenericProvider.java,v $
 * Revision 1.11  2012-06-11 13:10:01  jaco
 * Log incoming SOAP message on debug level
 *
 * Revision 1.10  2012/06/01 10:52:48  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.9  2012/02/06 13:18:20  gerrit
 * improved SOAP error logging
 *
 * Revision 1.8  2011/11/30 13:52:00  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:53  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2011/09/28 06:52:25  peter
 * disabled automatic (un)wrapping soap envelope
 *
 * Revision 1.5  2011/05/19 15:08:55  gerrit
 * use simplified ServiceDispatcher
 *
 * Revision 1.4  2011/02/21 17:55:25  gerrit
 * improved errorhandling and logging
 *
 * Revision 1.3  2007/10/08 12:24:48  gerrit
 * changed HashMap to Map where possible
 *
 * Revision 1.2  2007/02/12 14:06:28  gerrit
 * Logger from LogUtil
 *
 * Revision 1.1  2005/10/18 08:14:48  gerrit
 * created separate soap-package
 *
 * Revision 1.2  2005/07/05 13:29:49  gerrit
 * introduction of SecurityHandlers
 *
 * Revision 1.1  2005/04/26 09:28:25  gerrit
 * introduction of SoapGenericProvider
 *
 */
package nl.nn.adapterframework.soap;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.ISecurityHandler;
import nl.nn.adapterframework.http.HttpSecurityHandler;
import nl.nn.adapterframework.receivers.ServiceDispatcher;
import nl.nn.adapterframework.util.LogUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.soap.Constants;
import org.apache.soap.Envelope;
import org.apache.soap.SOAPException;
import org.apache.soap.rpc.Call;
import org.apache.soap.rpc.SOAPContext;
import org.apache.soap.server.DeploymentDescriptor;
import org.apache.soap.util.Provider;

/**
 * Soap Provider that accepts any message and routes it to a listener with a corresponding TargetObjectNamespacURI.
 * 
 * @version $Id$
 * @author Gerrit van Brakel
 */
public class SoapGenericProvider implements Provider {
	public static final String version = "$RCSfile: SoapGenericProvider.java,v $ $Revision: 1.11 $ $Date: 2012-06-11 13:10:01 $";
	protected Logger log=LogUtil.getLogger(this);
	
	private final String TARGET_OBJECT_URI_KEY = "TargetObjectNamespaceURI";

	private ServiceDispatcher sd=null;
	//private SoapWrapper soapWrapper=null;

	public void locate(DeploymentDescriptor dd, Envelope env, Call call, String methodName, String targetObjectURI, SOAPContext reqContext)
		throws SOAPException {
		if (log.isDebugEnabled()){
			log.debug("Locate: dd=["+dd+"]+ targetObjectURI=[" +targetObjectURI+"]");
			try {
				log.debug("Incoming SOAP message: " + reqContext.getBodyPart(0).getContent().toString());
			} catch (Exception e) {
				log.debug("Could not log SOAP message", e);
			}
		}
		if (sd==null) {
			sd= ServiceDispatcher.getInstance();
		}
		/*if (soapWrapper==null) {
			try {
				soapWrapper = SoapWrapper.getInstance();
			} catch (ConfigurationException e) {
				throw new SOAPException(Constants.FAULT_CODE_SERVER, "cannot instantiate SoapWrapper");
			}
		}*/
		if (StringUtils.isEmpty(targetObjectURI)) {
			String msg="no targetObjectURI specified";
			log.warn(msg);
			throw new SOAPException(Constants.FAULT_CODE_SERVER, msg);
		}
		if (!sd.isRegisteredServiceListener(targetObjectURI)){
			String msg="no receiver registered for targetObjectURI ["+targetObjectURI+"]";
			log.warn(msg);
			throw new SOAPException(Constants.FAULT_CODE_SERVER, msg);
		}
		reqContext.setProperty(TARGET_OBJECT_URI_KEY, targetObjectURI);
	}
	
	public void invoke(SOAPContext reqContext, SOAPContext resContext) throws SOAPException {

		 try {
		 	String targetObjectURI = (String) reqContext.getProperty(TARGET_OBJECT_URI_KEY);
			if (log.isDebugEnabled()){
				log.debug("Invoking service for targetObjectURI=[" +targetObjectURI+"]");
			}
			//String message=soapWrapper.getBody(reqContext.getBodyPart(0).getContent().toString());
			String message=reqContext.getBodyPart(0).getContent().toString();
			HttpServletRequest httpRequest=(HttpServletRequest) reqContext.getProperty(Constants.BAG_HTTPSERVLETREQUEST);
			ISecurityHandler securityHandler = new HttpSecurityHandler(httpRequest);
			Map messageContext= new HashMap();
			messageContext.put(IPipeLineSession.securityHandlerKey, securityHandler);
			String result=sd.dispatchRequest(targetObjectURI, null, message, messageContext);
			//resContext.setRootPart( soapWrapper.putInEnvelope(result,null), Constants.HEADERVAL_CONTENT_TYPE_UTF8);
			resContext.setRootPart( result, Constants.HEADERVAL_CONTENT_TYPE_UTF8);
				
		 }
		 catch( Exception e ) {
		 	//log.warn("GenericSoapProvider caught exception:",e);
			if ( e instanceof SOAPException ) {
				throw (SOAPException ) e;
			} 
			SOAPException se=new SOAPException( Constants.FAULT_CODE_SERVER, "GenericSoapProvider caught exception");
			se.initCause(e);
			throw se;
		 }
	}
	
}

