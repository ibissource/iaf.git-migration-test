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
 * $Log: IfsaEjbBase.java,v $
 * Revision 1.4  2011-11-30 13:51:57  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2007/11/22 08:48:18  gerrit
 * update from ejb-branch
 *
 * Revision 1.1.2.3  2007/11/06 12:33:07  tim
 * Implement more closely some of the details of original code
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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import nl.nn.adapterframework.extensions.ifsa.IfsaMessageProtocolEnum;
import nl.nn.adapterframework.util.LogUtil;

import org.apache.log4j.Logger;

import com.ing.ifsa.api.ServiceRequest;
import nl.nn.adapterframework.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;

/**
 * Base class for the IFSA EJB implementation.
 * 
 * This class has all properties common to IfsaProviderListener and
 * IfsaProviderRequesterSender, and some common utility methods.
 * 
 * @author Tim van der Leeuw
 * @version $Id$
 * 
 */
abstract public class IfsaEjbBase {

    protected Logger log = LogUtil.getLogger(this);

    protected String name;

    protected String applicationId;

    protected String serviceId;

    protected String polishedServiceId = null;

    protected IfsaMessageProtocolEnum messageProtocol;

    protected long timeOut = -1;

    protected String getLogPrefix() {
        return "IfsaRequester["+ getName()+ 
                "] of Application [" + getApplicationId()+"] ";  
    }
    
    protected void configure() throws ConfigurationException {
        // perform some basic checks
        if (StringUtils.isEmpty(getApplicationId()))
            throw new ConfigurationException(getLogPrefix()+"applicationId is not specified");
        if (getMessageProtocolEnum() == null)
            throw new ConfigurationException(getLogPrefix()+
                "invalid messageProtocol specified ["
                    + getMessageProtocolEnum()
                    + "], should be one of the following "
                    + IfsaMessageProtocolEnum.getNames());
    }
    
    protected void addUdzMapToRequest(Map udzMap, ServiceRequest request) {
        if (udzMap == null) {
            return;
        }
        for (Iterator it = udzMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            request.setUserDefinedZone(entry.getKey(), entry.getValue());
        }
    }

    public String getPhysicalDestinationName() {
            String result = null;
    
            try {
                result = getServiceId();
    //            log.debug("obtaining connection and servicequeue for "+result);
    //            if (getServiceQueue() != null) {
    //                result += " ["+ getServiceQueue().getQueueName()+"]";
    //            }
            } catch (Throwable t) {
                log.warn(getLogPrefix()+"got exception in getPhysicalDestinationName", t);
            }
            return result;
        }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getMessageProtocol() {
        return messageProtocol.getName();
    }

    public IfsaMessageProtocolEnum getMessageProtocolEnum() {
        return messageProtocol;
    }

    /**
         * Method logs a warning when the newMessageProtocol is not <code>FF</code>
         * or <code>RR</code>.
         * <p>When the messageProtocol equals to FF, transacted is set to true</p>
         * <p>Creation date: (08-05-2003 9:03:53)</p>
         * @see IfsaMessageProtocolEnum
         * @param newMessageProtocol String
         */
    public void setMessageProtocol(String newMessageProtocol) {
        if (null==IfsaMessageProtocolEnum.getEnum(newMessageProtocol)) {
            throw new IllegalArgumentException(getLogPrefix()+
            "illegal messageProtocol ["
                + newMessageProtocol
                + "] specified, it should be one of the values "
                + IfsaMessageProtocolEnum.getNames());
    
        }
        messageProtocol = IfsaMessageProtocolEnum.getEnum(newMessageProtocol);
        log.debug(getLogPrefix()+"message protocol set to "+messageProtocol.getName());
    }

    public String getPolishedServiceId() {
        return polishedServiceId;
    }

    public void setPolishedServiceId(String polishedServiceId) {
        this.polishedServiceId = polishedServiceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
