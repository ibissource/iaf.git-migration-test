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
 * $Log: EjbListenerPortConnector.java,v $
 * Revision 1.7  2011-11-30 13:51:57  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:51  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.5  2010/12/13 13:17:13  gerrit
 * made acknowledgemode configurable
 *
 * Revision 1.4  2008/01/03 15:42:47  gerrit
 * rework port connected listener interfaces
 *
 * Revision 1.3  2007/11/22 08:47:43  gerrit
 * update from ejb-branch
 *
 * Revision 1.2.2.4  2007/11/15 10:34:02  tim
 * Rename method 'getJmsListener' to 'getListener' to reflect fact that more types of listener can be controlled
 *
 * Revision 1.2.2.3  2007/11/15 10:28:42  tim
 * * Add JavaDoc
 * * Refactor a number of fields & variables to more logical names
 *
 * Revision 1.2.2.2  2007/11/09 14:18:16  tim
 * Fix NPE when WAS ListenerPort not found; replace a lot more illegal characters
 *
 * Revision 1.2.2.1  2007/11/06 09:39:13  tim
 * Merge refactoring/renaming from HEAD
 *
 * Revision 1.2  2007/11/05 13:06:55  tim
 * Rename and redefine methods in interface IListenerConnector to remove 'jms' from names
 *
 * Revision 1.1  2007/11/05 12:20:17  tim
 * Rename 'EjbJmsConfigurator' to 'EjbListenerPortConnector'
 *
 * Revision 1.5  2007/11/05 10:33:16  tim
 * Move interface 'IListenerConnector' from package 'configuration' to package 'core' in preparation of renaming it
 *
 * Revision 1.4  2007/10/16 09:52:35  tim
 * Change over JmsListener to a 'switch-class' to facilitate smoother switchover from older version to spring version
 *
 * Revision 1.3  2007/10/15 13:08:38  gerrit
 * EJB updates
 *
 * Revision 1.1.2.5  2007/10/15 08:36:31  tim
 * Fix lookup of JMX MBean for ListenerPort
 *
 * Revision 1.1.2.4  2007/10/12 14:29:31  tim
 * Several fixes and improvements to get EJB deployment mode running
 *
 * Revision 1.1.2.3  2007/10/10 14:30:43  gerrit
 * synchronize with HEAD (4.8-alpha1)
 *
 * Revision 1.2  2007/10/10 09:48:23  gerrit
 * Direct copy from Ibis-EJB:
 * first version in HEAD
 *
 */
package nl.nn.adapterframework.ejb;

import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;

import nl.nn.adapterframework.configuration.Configuration;
import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IListenerConnector;
import nl.nn.adapterframework.core.IPortConnectedListener;
import nl.nn.adapterframework.core.IReceiver;
import nl.nn.adapterframework.core.IbisExceptionListener;
import nl.nn.adapterframework.core.ListenerException;

import com.ibm.websphere.management.AdminService;
import com.ibm.websphere.management.AdminServiceFactory;

/**
 *
 * @author  Tim van der Leeuw
 * @since   4.8
 * @version $Id$
 */
public class EjbListenerPortConnector implements IListenerConnector {
    private final static String LISTENER_PORTNAME_SUFFIX = "ListenerPort";
    
    private IPortConnectedListener listener;
    private ObjectName listenerPortMBean;
    private AdminService adminService;
    private Destination destination;
    private Configuration configuration;
    private boolean closed;
    private ListenerPortPoller listenerPortPoller;
    
    public Destination getDestination() {
        return destination;
    }

    public void configureEndpointConnection(IPortConnectedListener listener, ConnectionFactory connectionFactory, Destination destination, IbisExceptionListener exceptionListener, String cacheMode, int acknowledgeMode, boolean sessionTransacted, String selector) throws ConfigurationException {
        try {
            this.listener = listener;
            this.listenerPortMBean = lookupListenerPortMBean(listener);
            // TODO: Verification that the ListenerPort is configured same as JmsListener
            String destinationName = (String) getAdminService().getAttribute(listenerPortMBean, "jmsDestJNDIName");
            Context ctx = new InitialContext();
            this.destination = (Destination) ctx.lookup(destinationName);
            
            closed = isListenerPortClosed();
            
            listenerPortPoller.registerEjbListenerPortConnector(this);
        } catch (ConfigurationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ConfigurationException(ex);
        }
    }

    /**
     * Start Listener-port to which the Listener is connected.
     * 
     * Sets internal stated to closed=false
     * 
     * @throws nl.nn.adapterframework.core.ListenerException
     */
    public void start() throws ListenerException {
        try {
            if (listenerPortMBean != null) {
                getAdminService().invoke(listenerPortMBean, "start", null, null);
                // Register again, to be sure, b/c a registration can have been
                // removed by some other controlling code.
                listenerPortPoller.registerEjbListenerPortConnector(this);
            }
            closed = false;
        } catch (Exception ex) {
            throw new ListenerException(ex);
        }
    }

    /**
     * Stop Listener-port to which the Listener is connected.
     * 
     * Sets internal stated to closed=true
     * 
     * @throws nl.nn.adapterframework.core.ListenerException
     */
    public void stop() throws ListenerException {
        try {
            if (listenerPortMBean != null) {
                getAdminService().invoke(listenerPortMBean, "stop", null, null);
            }
            closed = true;
        } catch (Exception ex) {
            throw new ListenerException(ex);
        }
    }

    /**
     * Check if ListenerPort to which the Listener is connected is
     * closed, or started.
     * 
     * @return <code>true</code> if the ListenerPort is closed, or
     * <code>false</code> if the ListenerPort is started.
     * @throws nl.nn.adapterframework.configuration.ConfigurationException
     */
    public boolean isListenerPortClosed() throws ConfigurationException {
        try {
            return !((Boolean)getAdminService().getAttribute(listenerPortMBean, "started")).booleanValue();
        } catch (Exception ex) {
            throw new ConfigurationException("Failure enquiring on state of Listener Port MBean '"
                    + listenerPortMBean + "'", ex);
        }
    }
    
    /**
     * Lookup the MBean for the listener-port in WebSphere that the JMS Listener
     * binds to.
     */
    protected ObjectName lookupListenerPortMBean(IPortConnectedListener jmsListener)  throws ConfigurationException {
        try {
            // Get the admin service
            AdminService as = getAdminService();
            String listenerPortName = getListenerPortName(jmsListener);
            
            // Create ObjectName instance to search for
//            Hashtable queryProperties = new Hashtable();
//            queryProperties.put("name",listenerPortName);
//            queryProperties.put("type", "ListenerPort");
//            ObjectName queryName = new ObjectName("WebSphere", queryProperties);
            
            ObjectName queryName = new ObjectName("WebSphere:type=ListenerPort,name="
                    + listenerPortName + ",*");
            // Query AdminService for the name
            Set names = as.queryNames(queryName, null);
            
            // Assume that only 1 is returned and return it
            if (names.size() == 0) {
                throw new ConfigurationException("Can not find WebSphere ListenerPort by name of '"
                        + listenerPortName + "', IPortConnectedListener can not be configured");
            } else if (names.size() > 1) {
                throw new ConfigurationException("Multiple WebSphere ListenerPorts found by name of '"
                        + listenerPortName + "': " + names + ", IPortConnectedListener can not be configured");
            } else {
                return (ObjectName) names.iterator().next();
            }
        } catch (MalformedObjectNameException ex) {
            throw new ConfigurationException(ex);
        }
    }
    
    /**
     * Get the WebSphere admin-service for accessing MBeans.
     */
    protected synchronized AdminService getAdminService() {
        if (this.adminService == null) {
            this.adminService = AdminServiceFactory.getAdminService();
        }
        return this.adminService;
    }
    
    /**
     * Get the name of the ListenerPort to look up as WebSphere MBean.
     * 
     * Construct the name of the WebSphere listenerport according to the
     * following logic:
     * <ol>
     * <li>If the property 'listenerPort' is set in the configuration, then use that</li>
     * <li>Otherwise, concatenate the configuration-name with the receiver-name, replaces all spaces with minus-signs, and append 'ListenerPort'
     * </ol>
     * 
     */
    protected String getListenerPortName(IPortConnectedListener listener) {
        String name = listener.getListenerPort();
        
        if (name == null) {
            IReceiver receiver = listener.getReceiver();
            name = configuration.getConfigurationName()
                    + '-' + receiver.getName() + LISTENER_PORTNAME_SUFFIX;
            name = name.replace(' ', '-').replaceAll("(:|\\(|\\)|\\\\|/|\\||<|>|&|\\^|%)", "");
        }
        
        return name;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Check if the Listener is supposed to be open, or closed.
     * This attribute basically indicates what IBIS <i>thinks</i>
     * should be the state of the WebSphere ListenerPort.
     * @return
     */
    public boolean isClosed() {
        return closed;
    }

    public IPortConnectedListener getListener() {
        return listener;
    }

    public ListenerPortPoller getListenerPortPoller() {
        return listenerPortPoller;
    }

    public void setListenerPortPoller(ListenerPortPoller listenerPortPoller) {
        this.listenerPortPoller = listenerPortPoller;
    }

}
