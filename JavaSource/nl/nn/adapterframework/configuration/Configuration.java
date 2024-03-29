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
 * $Log: Configuration.java,v $
 * Revision 1.46  2012-10-10 10:19:37  jaco
 * Made it possible to use Locker on Pipe level too
 *
 * Revision 1.45  2012/08/09 12:04:33  jaco
 * Replaced jaxb-xalan-1.5.jar because of memory leak with IbisXalan.jar which is manually compiled with different package names to still be able to prevent WebSphere Xalan version to be used.
 * Made it possible to use IbisXalan.jar for Tomcat too (don't use javax.xml.transform.TransformerFactory system property and use a manually compiled IbisXtags.jar to prevent problems when this system property is set by other application in the same JVM (e.g. an older Ibis)).
 *
 * Revision 1.44  2012/03/19 15:07:22  jaco
 * Bugfix mangled file name of WSDL when adapter name contains a space
 *
 * Revision 1.43  2012/02/02 08:36:06  peter
 * default transformer factory for JVM should be the class org.apache.xalan.processor.TransformerFactoryImpl
 *
 * Revision 1.42  2012/02/01 11:36:11  peter
 * for XSLT 1.0 the class com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl is used to be backward compatible with WAS5
 *
 * Revision 1.41  2011/11/30 13:49:59  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:48  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.39  2011/05/19 15:06:18  gerrit
 * do not print versions anymore
 *
 * Revision 1.38  2010/01/28 15:07:33  gerrit
 * removed some version displays
 *
 * Revision 1.37  2009/12/29 14:32:20  gerrit
 * modified imports to reflect move of statistics classes to separate package
 *
 * Revision 1.36  2009/08/26 15:23:12  gerrit
 * support for configurable statisticsHandlers
 *
 * Revision 1.35  2009/06/05 07:19:56  gerrit
 * support for adapter level only statistics
 * added throws clause to forEachStatisticsKeeperBody()
 * end-processing of statisticskeeperhandler in a finally clause
 *
 * Revision 1.34  2009/03/17 10:29:35  peter
 * added getScheduledJob method
 *
 * Revision 1.33  2008/10/23 14:16:51  peter
 * XSLT 2.0 made possible
 *
 * Revision 1.32  2008/09/04 12:00:43  gerrit
 * collect interval statistics
 *
 * Revision 1.31  2008/08/27 15:53:01  gerrit
 * added statistics dump code
 * added reset option to statisticsdump
 *
 * Revision 1.30  2008/05/15 14:29:33  gerrit
 * added storage facility for configuration exceptions
 *
 * Revision 1.29  2008/01/29 15:49:53  gerrit
 * removed a class from versions
 *
 * Revision 1.28  2007/10/16 08:40:36  gerrit
 * removed ifsa facade version display
 *
 * Revision 1.27  2007/10/09 15:07:44  gerrit
 * copy changes from Ibis-EJB:
 * added formerly static classe appConstants to config
 * delegate work to IbisManager
 *
 * Revision 1.26  2007/10/08 13:29:28  gerrit
 * changed ArrayList to List where possible
 *
 * Revision 1.25  2007/07/24 08:04:49  gerrit
 * reversed shutdown sequence
 *
 * Revision 1.24  2007/07/17 15:07:35  gerrit
 * added list of adapters, to access them in order
 *
 * Revision 1.23  2007/06/26 09:35:41  gerrit
 * add instance name to log at startup
 *
 * Revision 1.22  2007/05/02 11:22:27  gerrit
 * added attribute 'active'
 *
 * Revision 1.21  2007/02/26 16:55:05  gerrit
 * start scheduler when a job is found in the configuration
 *
 * Revision 1.20  2007/02/21 15:57:18  gerrit
 * throw exception if scheduled job not OK
 *
 * Revision 1.19  2007/02/12 13:38:58  gerrit
 * Logger from LogUtil
 *
 * Revision 1.18  2005/12/28 08:59:15  gerrit
 * replaced application-name by instance-name
 *
 * Revision 1.17  2005/12/28 08:35:40  gerrit
 * introduced StatisticsKeeper-iteration
 *
 * Revision 1.16  2005/11/01 08:53:35  unknown2
 * Moved quartz scheduling knowledge to the SchedulerHelper class
 *
 * Revision 1.15  2005/05/31 09:11:24  gerrit
 * detailed version info for XML parsers and transformers
 *
 * Revision 1.14  2004/08/23 07:41:40  gerrit
 * renamed Pushers to Listeners
 *
 * Revision 1.13  2004/08/09 08:43:00  gerrit
 * removed pushing receiverbase
 *
 * Revision 1.12  2004/07/06 07:06:05  gerrit
 * added PushingReceiver and Sap-extensions
 *
 * Revision 1.11  2004/06/30 10:01:58  gerrit
 * modified error handling
 *
 * Revision 1.10  2004/06/16 12:34:46  johan
 * Added AutoStart functionality on Adapter
 *
 * Revision 1.9  2004/04/23 14:45:36  johan
 * added JMX support
 *
 * Revision 1.8  2004/03/30 07:30:05  gerrit
 * updated javadoc
 *
 * Revision 1.7  2004/03/26 09:56:43  johan
 * Updated javadoc
 *
 */
package nl.nn.adapterframework.configuration;

import java.net.URL;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import nl.nn.adapterframework.cache.IbisCacheManager;
import nl.nn.adapterframework.core.*;
import nl.nn.adapterframework.scheduler.JobDef;
import nl.nn.adapterframework.statistics.*;
import nl.nn.adapterframework.util.*;

/**
 * The Configuration is placeholder of all configuration objects. Besides that, it provides
 * functions for starting and stopping adapters as a facade.
 *
 * @version $Id$
 * @author Johan Verrips
 * @see    nl.nn.adapterframework.configuration.ConfigurationException
 * @see    nl.nn.adapterframework.core.IAdapter
 */
public class Configuration {
    protected Logger log=LogUtil.getLogger(this);

    private Map<String, IAdapter> adapterTable = new Hashtable<String, IAdapter>();
	private List<IAdapter> adapters = new ArrayList<IAdapter>();
	private Map jobTable = new Hashtable();
    private List<JobDef> scheduledJobs = new ArrayList<JobDef>();

    private URL configurationURL;
    private URL digesterRulesURL;
    private String configurationName = "";
    private boolean enableJMX=false;
    private StatisticsKeeperIterationHandler statisticsHandler=null;

    private AppConstants appConstants;

    private ConfigurationException configurationException=null;

    private static Date statisticsMarkDateMain=new Date();
	private static Date statisticsMarkDateDetails=statisticsMarkDateMain;

    /**
     *Set JMX extensions as enabled or not. Default is that JMX extensions are NOT enabled.
     * @param enable
     * @since 4.1.1
     */
    public void setEnableJMX(boolean enable){
    	enableJMX=enable;
    }

	/**
	 * Are JMX extensions enabled?
     * @since 4.1.1
	 * @return boolean
	 */
    public boolean isEnableJMX(){
    	return enableJMX;
    }

	public void forEachStatisticsKeeper(StatisticsKeeperIterationHandler hski, Date now, Date mainMark, Date detailMark, int action) throws SenderException {
		Object root=hski.start(now,mainMark,detailMark);
		try {
			Object groupData=hski.openGroup(root,appConstants.getString("instance.name",""),"instance");
			for (int i=0; i<adapters.size(); i++) {
				IAdapter adapter = getRegisteredAdapter(i);
				adapter.forEachStatisticsKeeperBody(hski,groupData,action);
			}
			IbisCacheManager.iterateOverStatistics(hski, groupData, action);
			hski.closeGroup(groupData);
		} finally {
			hski.end(root);
		}
	}

	public void dumpStatistics(int action) {
		Date now=new Date();
		boolean showDetails=(action==HasStatistics.STATISTICS_ACTION_FULL ||
							 action==HasStatistics.STATISTICS_ACTION_MARK_FULL ||
							 action==HasStatistics.STATISTICS_ACTION_RESET);
		try {
			if (statisticsHandler==null) {
				statisticsHandler =new StatisticsKeeperLogger();
				statisticsHandler.configure();
			}

//			StatisticsKeeperIterationHandlerCollection skihc = new StatisticsKeeperIterationHandlerCollection();
//
//			StatisticsKeeperLogger skl =new StatisticsKeeperLogger();
//			skl.configure();
//			skihc.registerIterationHandler(skl);
//
//			StatisticsKeeperStore skih = new StatisticsKeeperStore();
//			skih.setJmsRealm("lokaal");
//			skih.configure();
//			skihc.registerIterationHandler(skih);

			forEachStatisticsKeeper(statisticsHandler, now, statisticsMarkDateMain, showDetails ?statisticsMarkDateDetails : null, action);
		} catch (Exception e) {
			log.error("dumpStatistics() caught exception", e);
		}
		if (action==HasStatistics.STATISTICS_ACTION_RESET ||
			action==HasStatistics.STATISTICS_ACTION_MARK_MAIN ||
			action==HasStatistics.STATISTICS_ACTION_MARK_FULL) {
				statisticsMarkDateMain=now;
		}
		if (action==HasStatistics.STATISTICS_ACTION_RESET ||
			action==HasStatistics.STATISTICS_ACTION_MARK_FULL) {
				statisticsMarkDateDetails=now;
		}

	}


    /**
     *	initializes the log and the AppConstants
     * @see nl.nn.adapterframework.util.AppConstants
     */
    public Configuration() {
    }
    public Configuration(URL digesterRulesURL, URL configurationURL) {
        this();
        this.configurationURL = configurationURL;
        this.digesterRulesURL = digesterRulesURL;

    }
    protected void init() {
		log.info(VersionInfo());
    }

    /**
     * get a registered adapter by its name
     * @param name  the adapter to retrieve
     * @return IAdapter
     */
    public IAdapter getRegisteredAdapter(String name) {
        return adapterTable.get(name);
    }
	public IAdapter getRegisteredAdapter(int index) {
		return adapters.get(index);
	}

	public List<IAdapter> getRegisteredAdapters() {
		return adapters;
	}

    //Returns a sorted list of registered adapter names as an <code>Iterator</code>
    public Iterator<IAdapter> getRegisteredAdapterNames() {
        // Why is the set copied?
        SortedSet<IAdapter> sortedKeys = new TreeSet(adapterTable.keySet());
        return sortedKeys.iterator();
    }
    /**
     * returns wether an adapter is known at the configuration.
     * @param name the Adaptername
     * @return true if the adapter is known at the configuration
     */
    public boolean isRegisteredAdapter(String name){
        return getRegisteredAdapter(name)==null;
    }
    /**
     * @param adapterName the adapter
     * @param receiverName the receiver
     * @return true if the receiver is known at the adapter
     */
    public boolean isRegisteredReceiver(String adapterName, String receiverName){
        IAdapter adapter=getRegisteredAdapter(adapterName);
        if (null==adapter) {
        	return false;
		}
        return adapter.getReceiverByName(receiverName) != null;
    }
    public void listObjects() {
		for (int i=0; i<adapters.size(); i++) {
			IAdapter adapter = getRegisteredAdapter(i);

			log.info(i+") "+ adapter.getName()+ ": "	+ adapter.toString());
        }
    }

    /**
     * Register an adapter with the configuration.  If JMX is {@link #setEnableJMX(boolean) enabled},
     * the adapter will be visible and managable as an MBEAN.
     * @param adapter
     * @throws ConfigurationException
     */
    public void registerAdapter(IAdapter adapter) throws ConfigurationException {
    	if (adapter instanceof Adapter && !((Adapter)adapter).isActive()) {
    		log.debug("adapter [" + adapter.getName() + "] is not active, therefore not included in configuration");
    		return;
    	}
        if (null != adapterTable.get(adapter.getName())) {
            throw new ConfigurationException("Adapter [" + adapter.getName() + "] already registered.");
        }
        adapterTable.put(adapter.getName(), adapter);
		adapters.add(adapter);
		if (isEnableJMX()) {
			log.debug("Registering adapter [" + adapter.getName() + "] to the JMX server");
	        JmxMbeanHelper.hookupAdapter( (nl.nn.adapterframework.core.Adapter) adapter);
	        log.info ("[" + adapter.getName() + "] registered to the JMX server");
		}
        adapter.configure();

    }
    /**
     * Register an {@link JobDef job} for scheduling at the configuration.
     * The configuration will create an {@link JobDef AdapterJob} instance and a JobDetail with the
     * information from the parameters, after checking the
     * parameters of the job. (basically, it checks wether the adapter and the
     * receiver are registered.
     * <p>See the <a href="http://quartz.sourceforge.net">Quartz scheduler</a> documentation</p>
     * @param jobdef a JobDef object
     * @see nl.nn.adapterframework.scheduler.JobDef for a description of Cron triggers
     * @since 4.0
     */
    public void registerScheduledJob(JobDef jobdef) throws ConfigurationException {
		jobdef.configure(this);
		jobTable.put(jobdef.getName(), jobdef);
        scheduledJobs.add(jobdef);
    }

	public void registerStatisticsHandler(StatisticsKeeperIterationHandler handler) throws ConfigurationException {
		log.debug("registerStatisticsHandler() registering ["+ClassUtils.nameOf(handler)+"]");
		statisticsHandler=handler;
		handler.configure();
	}

    public String getInstanceInfo() {
		String instanceInfo=appConstants.getProperty("application.name")+" "+
							appConstants.getProperty("application.version")+" "+
							appConstants.getProperty("instance.name")+" "+
							appConstants.getProperty("instance.version")+" ";
		String buildId=	appConstants.getProperty("instance.build_id");
		if (StringUtils.isNotEmpty(buildId)) {
			instanceInfo+=" build "+buildId;
		}
		return instanceInfo;
    }

    public String VersionInfo() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(getInstanceInfo()+SystemUtils.LINE_SEPARATOR);
		sb.append(nl.nn.adapterframework.util.XmlUtils.getVersionInfo());
    	return sb.toString();

    }

	public void setConfigurationName(String name) {
		configurationName = name;
		log.debug("configuration name set to [" + name + "]");
	}
	public String getConfigurationName() {
		return configurationName;
	}


	public void setConfigurationURL(URL url) {
		configurationURL = url;
	}
	public URL getConfigurationURL() {
		return configurationURL;
	}


	public void setDigesterRulesURL(URL url) {
		digesterRulesURL = url;
	}
	public String getDigesterRulesFileName() {
		return digesterRulesURL.getFile();
	}


	public JobDef getScheduledJob(String name) {
		return (JobDef) jobTable.get(name);
	}
	public JobDef getScheduledJob(int index) {
		return (JobDef) scheduledJobs.get(index);
	}


	public List<JobDef> getScheduledJobs() {
		return scheduledJobs;
	}

    public void setAppConstants(AppConstants constants) {
        appConstants = constants;
    }
	public AppConstants getAppConstants() {
		return appConstants;
	}


	public void setConfigurationException(ConfigurationException exception) {
		configurationException = exception;
	}
	public ConfigurationException getConfigurationException() {
		return configurationException;
	}

}
