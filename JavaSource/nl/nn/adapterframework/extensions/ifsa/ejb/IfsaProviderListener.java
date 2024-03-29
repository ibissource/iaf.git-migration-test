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
 * $Log: IfsaProviderListener.java,v $
 * Revision 1.8  2012-06-01 10:52:50  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.7  2011/11/30 13:51:58  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.5  2010/12/13 13:17:13  gerrit
 * made acknowledgemode configurable
 *
 * Revision 1.4  2008/03/27 11:55:53  gerrit
 * modified cid handling
 *
 * Revision 1.3  2008/01/03 15:45:28  gerrit
 * rework port connected listener interfaces
 *
 * Revision 1.2  2007/11/22 08:48:19  gerrit
 * update from ejb-branch
 *
 * Revision 1.1.2.12  2007/11/14 09:11:50  tim
 * Fix unimplemented-method error (no implementation required, add no-op implementation)
 *
 * Revision 1.1.2.11  2007/11/06 14:03:10  tim
 * Fix method to get name of WebSphere Listener Port
 *
 * Revision 1.1.2.10  2007/11/06 13:34:52  tim
 * Remove unused imports
 *
 * Revision 1.1.2.9  2007/11/06 13:15:10  tim
 * Move code putting properties into threadContext from 'getIdFromRawMessage' to 'populateThreadContext'
 *
 * Revision 1.1.2.8  2007/11/06 12:49:33  tim
 * Add methods 'populateThreadContext' and 'destroyThreadContext' to interface IPortConnectedListener
 *
 * Revision 1.1.2.7  2007/11/06 12:33:07  tim
 * Implement more closely some of the details of original code
 *
 * Revision 1.1.2.6  2007/11/06 10:40:24  tim
 * Make IfsaProviderListener follow state of it's ListenerPort, like with JmsListener
 *
 * Revision 1.1.2.5  2007/11/06 10:36:49  tim
 * Make IfsaProviderListener follow state of it's ListenerPort, like with JmsListener
 *
 * Revision 1.1.2.4  2007/11/05 13:51:37  tim
 * Add 'version' string to new IFSA classes
 *
 * Revision 1.1.2.3  2007/10/29 12:25:34  tim
 * Create EJb Beans required to connect to IFSA J2EE implementation as an IFSA Provider application
 *
 * Revision 1.1.2.2  2007/10/29 09:33:00  tim
 * Refactor: pullup a number of methods to abstract base class so they can be shared between IFSA parts
 *
 * Revision 1.1.2.1  2007/10/25 15:03:44  tim
 * Begin work on implementing IFSA-EJB
 *
 * 
 */

package nl.nn.adapterframework.extensions.ifsa.ejb;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.jms.Session;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IListenerConnector;
import nl.nn.adapterframework.core.IMessageHandler;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.IPortConnectedListener;
import nl.nn.adapterframework.core.IReceiver;
import nl.nn.adapterframework.core.IbisExceptionListener;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.core.PipeLineResult;

import com.ing.ifsa.api.ServiceRequest;
import com.ing.ifsa.api.ServiceURI;

/**
 *
 * @author Tim van der Leeuw
 * @since 4.8
 * @version $Id$
 */
public class IfsaProviderListener extends IfsaEjbBase implements IPortConnectedListener {
    public static final String version = "$RCSfile: IfsaProviderListener.java,v $ $Revision: 1.8 $ $Date: 2012-06-01 10:52:50 $";
    
    private IMessageHandler handler;
    private IbisExceptionListener exceptionListener;
    private IReceiver receiver;
    private IListenerConnector listenerPortConnector;
    
    public void setHandler(IMessageHandler handler) {
        this.handler = handler;
    }

    public void setExceptionListener(IbisExceptionListener listener) {
        this.exceptionListener = listener;
    }

    public void configure() throws ConfigurationException {
        super.configure();
        listenerPortConnector.configureEndpointConnection(this, null, null, getExceptionListener(), null, Session.AUTO_ACKNOWLEDGE, false, null);
    }

    public void open() throws ListenerException {
        listenerPortConnector.start();
    }

    public void close() throws ListenerException {
        listenerPortConnector.stop();
    }

    public String getIdFromRawMessage(Object rawMessage, Map threadContext) throws ListenerException {
        ServiceRequest request = (ServiceRequest) rawMessage;
        return request.getUniqueId();
    }

    public String getStringFromRawMessage(Object rawMessage, Map threadContext) throws ListenerException {
        ServiceRequest request = (ServiceRequest) rawMessage;
        return request.getBusinessMessage().getText();
    }

    public void afterMessageProcessed(PipeLineResult processResult, Object rawMessage, Map context) throws ListenerException {
        // Nothing to do here
        return;
    }

    public IbisExceptionListener getExceptionListener() {
        return exceptionListener;
    }

    public IMessageHandler getHandler() {
        return handler;
    }

    public IReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(IReceiver receiver) {
        this.receiver = receiver;
    }

    public String getListenerPort() {
        String appIdName = getApplicationId().replaceFirst("IFSA://", "");
        return "IFSA_" + appIdName + "_" + getMessageProtocol() + "_Listener";
    }

    public IListenerConnector getListenerPortConnector() {
        return listenerPortConnector;
    }

    public void setListenerPortConnector(IListenerConnector listenerPortConnector) {
        this.listenerPortConnector = listenerPortConnector;
    }


    public void populateThreadContext(Object rawMessage, Map threadContext, Session session) throws ListenerException {
        ServiceRequest request = (ServiceRequest) rawMessage;
        
        // Get variables from the IFSA Service Request, in as good manner
        // as possible to emulate the way that the JMS IfsaProviderListener works
        String mode = getMessageProtocol().equals("RR")? "NON_PERSISTENT" : "PERSISTENT";
        String id = request.getUniqueId();
        String cid = id;
        if (log.isDebugEnabled()) {
            log.debug("Setting correlation ID to MessageId");
        }
        Date dTimeStamp = new Date();
        String messageText = getStringFromRawMessage(rawMessage, threadContext);
        
        String fullIfsaServiceName = null;
        ServiceURI requestedService = request.getServiceURI();
        String ifsaServiceName=null, ifsaGroup=null, ifsaOccurrence=null, ifsaVersion=null;
        
        ifsaServiceName = requestedService.getService();
        ifsaGroup = requestedService.getGroup();
        ifsaOccurrence = requestedService.getOccurrence();
        ifsaVersion = requestedService.getVersion();
        
        if (log.isDebugEnabled()) {
                log.debug(getLogPrefix()+ "got message for [" + fullIfsaServiceName
                                + "] with JMSDeliveryMode=[" + mode
                                + "] \n  JMSMessageID=[" + id
                                + "] \n  JMSCorrelationID=["+ cid
                                + "] \n  ifsaServiceName=["+ ifsaServiceName
                                + "] \n  ifsaGroup=["+ ifsaGroup
                                + "] \n  ifsaOccurrence=["+ ifsaOccurrence
                                + "] \n  ifsaVersion=["+ ifsaVersion
                                + "] \n  Timestamp=[" + dTimeStamp.toString()
                                + "] \n  ReplyTo=[none"
                                + "] \n  MessageHeaders=[<unknown>"
                                + "] \n  Message=[" + messageText+"\n]");

        }
        threadContext.put("id", id);
        threadContext.put(IPipeLineSession.technicalCorrelationIdKey, cid);
        threadContext.put("timestamp", dTimeStamp);
        threadContext.put("replyTo", "none");
        threadContext.put("messageText", messageText);
        threadContext.put("fullIfsaServiceName", fullIfsaServiceName);
        threadContext.put("ifsaServiceName", ifsaServiceName);
        threadContext.put("ifsaGroup", ifsaGroup);
        threadContext.put("ifsaOccurrence", ifsaOccurrence);
        threadContext.put("ifsaVersion", ifsaVersion);

        Map udz = request.getAllUserDefinedZones();
        if (udz!=null) {
            String contextDump = "ifsaUDZ:";
            for (Iterator it = udz.keySet().iterator(); it.hasNext();) {
                String key = (String)it.next();
                String value = (String)udz.get(key);
                contextDump = contextDump + "\n " + key + "=[" + value + "]";
                threadContext.put(key, value);
            }
            if (log.isDebugEnabled()) {
                log.debug(getLogPrefix()+ contextDump);
            }
        }
    }
}
