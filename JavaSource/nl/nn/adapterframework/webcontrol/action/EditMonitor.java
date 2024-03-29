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
 * $Log: EditMonitor.java,v $
 * Revision 1.8  2011-11-30 13:51:46  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2008/08/14 14:53:51  gerrit
 * fixed exit determination
 *
 * Revision 1.5  2008/08/13 13:46:57  gerrit
 * some bugfixing
 *
 * Revision 1.4  2008/08/07 11:32:30  gerrit
 * rework
 *
 * Revision 1.3  2008/07/24 12:42:10  gerrit
 * rework of monitoring
 *
 * Revision 1.2  2008/07/17 16:21:49  gerrit
 * work in progess
 *
 * Revision 1.1  2008/07/14 17:29:47  gerrit
 * support for flexibile monitoring
 *
 */
package nl.nn.adapterframework.webcontrol.action;

import javax.servlet.http.HttpServletResponse;

import nl.nn.adapterframework.monitoring.Monitor;
import nl.nn.adapterframework.monitoring.MonitorManager;

import org.apache.struts.action.DynaActionForm;



/**
 * Edit a Monitor - display the form.
 * 
 * @author  Gerrit van Brakel
 * @since   4.9
 * @version $Id$
 */
public class EditMonitor extends ShowMonitors {

	protected String performAction(DynaActionForm monitorForm, String action, int index, int triggerIndex, HttpServletResponse response) {
		MonitorManager mm = MonitorManager.getInstance();
	
		if (index>=0) {
			Monitor monitor = mm.getMonitor(index);
			monitorForm.set("monitor",monitor);
		}
		
		return null;
	}

}
