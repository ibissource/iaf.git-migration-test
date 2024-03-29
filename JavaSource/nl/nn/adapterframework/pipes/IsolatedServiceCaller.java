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
 * $Log: IsolatedServiceCaller.java,v $
 * Revision 1.15  2011-11-30 13:51:50  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.13  2011/05/19 15:07:30  gerrit
 * use simplified ServiceDispatcher
 *
 * Revision 1.12  2010/09/07 15:55:13  jaco
 * Removed IbisDebugger, made it possible to use AOP to implement IbisDebugger functionality.
 *
 * Revision 1.11  2010/03/10 14:30:05  peter
 * rolled back testtool adjustments (IbisDebuggerDummy)
 *
 * Revision 1.9  2009/11/18 17:28:04  jaco
 * Added senders to IbisDebugger
 *
 * Revision 1.8  2009/09/07 13:28:30  gerrit
 * removed unused code
 *
 * Revision 1.7  2007/12/10 10:10:51  gerrit
 * no transactions in IsolatedServiceCaller
 *
 * Revision 1.6  2007/06/07 15:19:39  gerrit
 * set names of isolated threads
 *
 * Revision 1.5  2007/02/12 14:02:19  gerrit
 * Logger from LogUtil
 *
 * Revision 1.4  2006/08/22 06:50:09  gerrit
 * added asynchronous and transactional features
 *
 * Revision 1.3  2005/09/26 11:54:05  gerrit
 * enabeld isolated calls from IbisLocalSender to JavaListener as well as to WebServiceListener
 *
 * Revision 1.2  2005/09/20 13:27:57  gerrit
 * added asynchronous call-facility
 *
 * Revision 1.1  2005/09/07 15:35:10  gerrit
 * introduction of IsolatedServiceCaller
 *
 */
package nl.nn.adapterframework.pipes;

import java.util.HashMap;

import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.core.RequestReplyExecutor;
import nl.nn.adapterframework.receivers.JavaListener;
import nl.nn.adapterframework.receivers.ServiceDispatcher;
import nl.nn.adapterframework.util.ClassUtils;
import nl.nn.adapterframework.util.Guard;
import nl.nn.adapterframework.util.LogUtil;

import org.apache.log4j.Logger;
import org.springframework.core.task.TaskExecutor;

/**
 * Helper class for IbisLocalSender that wraps around {@link ServiceDispatcher} to make calls to a local Ibis adapter in a separate thread.
 * 
 * @author  Gerrit van Brakel
 * @since   4.3
 * @version $Id$
 */
public class IsolatedServiceCaller {
	public static final String version="$RCSfile: IsolatedServiceCaller.java,v $ $Revision: 1.15 $ $Date: 2011-11-30 13:51:50 $";
	protected Logger log = LogUtil.getLogger(this);
	
	/**
	 * The thread-pool for spawning threads, injected by Spring
	 */
	private TaskExecutor taskExecutor;

	public void setTaskExecutor(TaskExecutor executor) {
		taskExecutor = executor;
	}

	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	public void callServiceAsynchronous(String serviceName, String correlationID, String message, HashMap context, boolean targetIsJavaListener) throws ListenerException {
		IsolatedServiceExecutor ise=new IsolatedServiceExecutor(serviceName, correlationID, message, context, targetIsJavaListener, null);
		getTaskExecutor().execute(ise);
	}
	
	public String callServiceIsolated(String serviceName, String correlationID, String message, HashMap context, boolean targetIsJavaListener) throws ListenerException {
		Guard guard= new Guard();
		guard.addResource();
		IsolatedServiceExecutor ise=new IsolatedServiceExecutor(serviceName, correlationID, message, context, targetIsJavaListener, guard);
		getTaskExecutor().execute(ise);
		try {
			guard.waitForAllResources();
		} catch (InterruptedException e) {
			throw new ListenerException(ClassUtils.nameOf(this)+" was interupted",e);
		}
		if (ise.getThrowable()!=null) {
			if (ise.getThrowable() instanceof ListenerException) {
				throw (ListenerException)ise.getThrowable();
			} else {
				throw new ListenerException(ise.getThrowable());
			}
		} else {
			return (String)ise.getReply();
		}
	}

	public class IsolatedServiceExecutor extends RequestReplyExecutor {
		String serviceName; 
		HashMap context;
		boolean targetIsJavaListener;
		Guard guard;
		
		public IsolatedServiceExecutor(String serviceName, String correlationID, String message, HashMap context, boolean targetIsJavaListener, Guard guard) {
			super();
			this.serviceName=serviceName;
			this.correlationID=correlationID;
			request=message;
			this.context=context;
			this.targetIsJavaListener=targetIsJavaListener;
			this.guard=guard;
		}

		public void run() {
			try {
				if (targetIsJavaListener) {
					reply = JavaListener.getListener(serviceName).processRequest(correlationID, request, context);
				} else {
					reply = ServiceDispatcher.getInstance().dispatchRequest(serviceName, correlationID, request, context);
				}
			} catch (Throwable t) {
				log.warn("IsolatedServiceCaller caught exception",t);
				throwable=t;
			} finally {
				if (guard != null) {
					guard.releaseResource();
				}
			}
		}

	}

}
