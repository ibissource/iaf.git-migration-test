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
 * $Log: ConfiguredJob.java,v $
 * Revision 1.3  2011-11-30 13:51:42  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:53  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2008/09/04 13:27:26  gerrit
 * restructured job scheduling
 *
 * Revision 1.7  2008/08/27 16:21:26  gerrit
 * added function dumpStatistics
 *
 * Revision 1.6  2007/12/12 09:09:56  gerrit
 * allow for query-type jobs
 *
 * Revision 1.5  2007/10/10 09:40:07  gerrit
 * Direct copy from Ibis-EJB:
 * version using IbisManager
 *
 * Revision 1.4  2007/02/21 16:02:46  gerrit
 * updated javadoc
 *
 */
package nl.nn.adapterframework.scheduler;

import nl.nn.adapterframework.configuration.IbisManager;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;



/**
 * Job, specified in Configuration.xml, for executing things to do with an adapter, like starting or stopping it.
 * The <a href="http://quartz.sourceforge.net">Quartz scheduler</a> is used for scheduling.
 * <p>
 * Expects a JobDetail with a datamap with the following fields:
 * <ul>
 * <li>function: the function to do, possible values:  "startreceiver","stopadapter",  "stopreceiver" and "stopadapter"</li>
 * <li>config: the Configuration object</li>
 * <li>adapterName: the name of the adapter<li>
 * <li>receiverName: the name of the receiver<li>
 * </ul>
 *<p><b>Design consideration</b></p>
 * <p>Currently, the {@link nl.nn.adapterframework.configuration.Configuration configuration}
 * is stored in the job data map. As the configuration is not serializable, due to the nature of the
 * adapters, the quartz database support cannot be used.
 * </p>
 * @version $Id$
 *
 * @author  Johan Verrips
 * @see nl.nn.adapterframework.core.IAdapter
 * @see nl.nn.adapterframework.configuration.Configuration
  */
public class ConfiguredJob extends BaseJob implements Job  {


	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			IbisManager ibisManager = (IbisManager)dataMap.get("manager");
			JobDef jobDef = (JobDef)dataMap.get("jobdef");
			log.info(getLogPrefix(jobDef) + "executing");
			jobDef.executeJob(ibisManager);
			log.debug(getLogPrefix(jobDef) + "completed");
		} catch (Exception e) {
			log.error(e);
			throw new JobExecutionException(e, false);
		}
	}
	
}
