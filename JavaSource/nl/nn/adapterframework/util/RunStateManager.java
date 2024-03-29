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
 * $Log: RunStateManager.java,v $
 * Revision 1.10  2011-12-08 09:26:46  peter
 * fixed javadoc
 *
 * Revision 1.9  2011/11/30 13:51:48  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.7  2007/09/05 13:06:07  gerrit
 * updated javadoc
 *
 * Revision 1.6  2007/02/12 14:12:03  gerrit
 * Logger from LogUtil
 *
 * Revision 1.5  2005/10/27 08:42:54  gerrit
 * *** empty log message ***
 *
 */
package nl.nn.adapterframework.util;

import org.apache.log4j.Logger;

/**
 * Utility class to support run-state management.
 * 
 * @version $Id$
 * @author Gerrit van Brakel
 */
public class RunStateManager implements RunStateEnquirer {
	public static final String version="$RCSfile: RunStateManager.java,v $ $Revision: 1.10 $ $Date: 2011-12-08 09:26:46 $";
	protected Logger log = LogUtil.getLogger(this);

	private RunStateEnum runState = RunStateEnum.STOPPED;

	public synchronized RunStateEnum getRunState() {
		return runState;
	}
	public synchronized boolean isInState(RunStateEnum state) {
		return runState.equals(state);
	}
	public synchronized void setRunState(RunStateEnum newRunState) {
		if (! runState.equals(newRunState)) {
			if (log.isDebugEnabled())
				log.debug("Runstate [" + this + "] set from " + runState + " to " + newRunState);
			runState = newRunState;
			notifyAll();
		}
	}
	/**
	 * Performs a <code>wait()</code> until the object is in the requested state.
	 * @param requestedRunState    the RunStateEnum requested
	 * @throws InterruptedException when interruption occurs
	 */
	public synchronized void waitForRunState(RunStateEnum requestedRunState)
		throws InterruptedException {
		while (!runState.equals(requestedRunState)) {
			wait();
		}
	}
	
	/**
	 * Performs a <code>wait()</code> until the object is in the requested state, or maxWait ms elapsed.
	 * @param requestedRunState    the RunStateEnum requested
	 * @param maxWait              maximum amount of milliseconds to wait.
	 * @throws InterruptedException when interruption occurs
	 */
	public boolean waitForRunState(RunStateEnum requestedRunState, long maxWait) throws InterruptedException {
		long cts = System.currentTimeMillis();
		RunStateEnum newState = null;
		synchronized(this) {
			while (! (newState = getRunState()).equals(requestedRunState)) {
				long togo = maxWait - (System.currentTimeMillis() - cts);
				if (togo > 0) {
					wait(togo);
				}
				else {
					break;
				}
			}
		}
		return newState.equals(requestedRunState);
	}
	
}
