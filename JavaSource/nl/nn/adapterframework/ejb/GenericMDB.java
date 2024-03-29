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
 * $Log: GenericMDB.java,v $
 * Revision 1.9  2011-11-30 13:51:57  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:51  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.7  2008/02/28 16:18:34  gerrit
 * simplified onMessage
 *
 * Revision 1.6  2008/01/03 15:43:36  gerrit
 * rework port connected listener interfaces
 *
 * Revision 1.5  2007/11/22 08:47:43  gerrit
 * update from ejb-branch
 *
 * Revision 1.4.2.7  2007/11/15 10:27:49  tim
 * * Add exception-registration via ExceptionListener of the listener
 * * Move code up to parent class
 *
 * Revision 1.4.2.6  2007/11/06 12:41:16  tim
 * Add original raw message as parameter to method 'createThreadContext' of 'pushingJmsListener' in preparation of adding it to interface
 *
 * Revision 1.4.2.5  2007/11/06 09:39:13  tim
 * Merge refactoring/renaming from HEAD
 *
 * Revision 1.4.2.4  2007/10/29 10:37:25  tim
 * Fix method visibility error
 *
 * Revision 1.4.2.3  2007/10/29 10:29:13  tim
 * Refactor: pullup a number of methods to abstract base class so they can be shared with new IFSA Session EJBs
 *
 * Revision 1.4.2.2  2007/10/25 08:36:57  tim
 * Add shutdown method for IBIS which shuts down the scheduler too, and which unregisters all EjbJmsConfigurators from the ListenerPortPoller.
 * Unregister JmsListener from ListenerPortPoller during ejbRemove method.
 * Both changes are to facilitate more proper shutdown of the IBIS adapters.
 *
 * Revision 1.4.2.1  2007/10/24 15:04:43  tim
 * Let runstate of receivers/listeners follow the state of WebSphere ListenerPorts if they are changed outside the control of IBIS.
 *
 * Revision 1.4  2007/10/16 09:52:35  tim
 * Change over JmsListener to a 'switch-class' to facilitate smoother switchover from older version to spring version
 *
 * Revision 1.3  2007/10/15 13:08:38  gerrit
 * EJB updates
 *
 * Revision 1.1.2.6  2007/10/15 11:35:51  tim
 * Fix direct retrieving of Logger w/o using the LogUtil
 *
 * Revision 1.1.2.5  2007/10/12 11:53:42  tim
 * Add variable to indicate to MDB if it's transactions are container-managed, or bean-managed
 *
 * Revision 1.1.2.4  2007/10/10 14:30:43  gerrit
 * synchronize with HEAD (4.8-alpha1)
 *
 * Revision 1.2  2007/10/10 09:48:23  gerrit
 * Direct copy from Ibis-EJB:
 * first version in HEAD
 *
 */
package nl.nn.adapterframework.ejb;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;

import nl.nn.adapterframework.core.IMessageHandler;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.receivers.GenericReceiver;

/**
 * @author  Tim van der Leeuw
 * @since   4.8
 * @version $Id$
 */
public class GenericMDB extends AbstractListenerConnectingEJB implements MessageDrivenBean, MessageListener {
    
    protected MessageDrivenContext ejbContext;
    
    public void setMessageDrivenContext(MessageDrivenContext ejbContext) throws EJBException {
        log.info("Received EJB-MDB Context");
        this.ejbContext = ejbContext;
    }
    
    public void ejbCreate() {
        log.info("Creating MDB");
        onEjbCreate();
    }
    
    public void ejbRemove() throws EJBException {
        log.info("Removing MDB");
        onEjbRemove();
    }

    public void onMessage(Message message) {
        try {
            // Code is not thread-safe but the same instance
            // should be looked up always so there's no point
            // in locking
            if (this.listener == null) {
                this.listener = retrieveListener();
            }

            IMessageHandler handler = this.listener.getHandler();
            handler.processRawMessage(listener,message);
        } catch (ListenerException ex) {
            log.error(ex, ex);
            listener.getExceptionListener().exceptionThrown(listener, ex);
            rollbackTransaction();
        }
    }

    /* (non-Javadoc)
     * @see nl.nn.adapterframework.ejb.AbstractEJBBase#getEJBContext()
     */
    protected EJBContext getEJBContext() {
        return this.ejbContext;
    }
    
    
}
