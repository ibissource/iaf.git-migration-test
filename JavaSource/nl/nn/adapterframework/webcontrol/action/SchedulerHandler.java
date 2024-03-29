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
 * $Log: SchedulerHandler.java,v $
 * Revision 1.8  2011-11-30 13:51:46  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2008/08/27 16:27:22  gerrit
 * fixed scheduler client
 *
 * Revision 1.5  2008/05/22 07:39:14  gerrit
 * removed version string
 *
 * Revision 1.4  2008/05/22 07:38:48  gerrit
 * use inherited error() method
 *
 */
package nl.nn.adapterframework.webcontrol.action;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.nn.adapterframework.scheduler.SchedulerAdapter;
import nl.nn.adapterframework.scheduler.SchedulerHelper;
import nl.nn.adapterframework.unmanaged.DefaultIbisManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * @version $Id$
 * @author  Johan Verrips
 */
public class SchedulerHandler extends ActionBase {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	    // Extract attributes we will need
	    initAction(request);
	
	    String action = request.getParameter("action");
	    if (null == action)
	        action = mapping.getParameter();
	    String jobName = request.getParameter("jobName");
	    String groupName = request.getParameter("groupName");
	
		if (ibisManager==null) {
			error("Cannot find ibismanager",null);
			return null;
		}
	
		// TODO Dit moet natuurlijk netter...
		DefaultIbisManager manager = (DefaultIbisManager)ibisManager;
		SchedulerHelper sh = manager.getSchedulerHelper();

	    SchedulerAdapter schedulerAdapter = new SchedulerAdapter();
	    Scheduler scheduler;
		try {
			scheduler = sh.getScheduler();
		} catch (SchedulerException e) {
			error("Cannot find scheduler",e);
			return null;
		}
	    try {
	        if (action.equalsIgnoreCase("startScheduler")) {
	            log.info("start scheduler:" + new Date() + getCommandIssuedBy(request));
	            scheduler.start();
	        } else
	            if (action.equalsIgnoreCase("pauseScheduler")) {
	                log.info("pause scheduler:" + new Date() + getCommandIssuedBy(request));
	                scheduler.pause();
	            } else
	                if (action.equalsIgnoreCase("deleteJob")) {
	                    log.info("delete job jobName [" + jobName
	                            + "] groupName [" + groupName
	                            + "] " + getCommandIssuedBy(request));
	                    scheduler.deleteJob(jobName, groupName);
	                } else
	                    if (action.equalsIgnoreCase("triggerJob")) {
	                        log.info("trigger job jobName [" + jobName
	                                + "] groupName [" + groupName
	                                + "] " + getCommandIssuedBy(request));
	                        scheduler.triggerJob(jobName, groupName);
	                    } else {
	                        log.error("no valid argument for SchedulerHandler:" + action);
	                    } 
	
	    } catch (Exception e) {
	        error("",e);
	    }
	
	    // Report any errors
	    if (!errors.isEmpty()) {
	        saveErrors(request, errors);
	    }
	
	    // Remove the obsolete form bean
	    if (mapping.getAttribute() != null) {
	        if ("request".equals(mapping.getScope()))
	            request.removeAttribute(mapping.getAttribute());
	        else
	            session.removeAttribute(mapping.getAttribute());
	    }
	
	    // Forward control to the specified success URI
	    return (mapping.findForward("success"));
	}

}
