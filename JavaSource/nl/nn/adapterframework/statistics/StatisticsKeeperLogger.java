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
 * $Log: StatisticsKeeperLogger.java,v $
 * Revision 1.3  2011-11-30 13:51:48  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:51  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2009/12/29 14:25:18  gerrit
 * moved statistics to separate package
 *
 * Revision 1.9  2009/08/26 15:43:15  gerrit
 * support for configurable statisticsHandlers
 *
 * Revision 1.8  2009/06/05 07:37:36  gerrit
 * support for adapter level only statistics
 *
 * Revision 1.7  2009/01/08 16:41:31  gerrit
 * made statisticsfile weekly rolling
 *
 * Revision 1.6  2008/09/04 12:19:05  gerrit
 * log to daily rolling file
 *
 * Revision 1.5  2008/07/24 12:24:12  gerrit
 * log statistics as XML
 *
 * Revision 1.4  2008/05/14 09:30:43  gerrit
 * simplified methodnames of StatisticsKeeperIterationHandler
 *
 * Revision 1.3  2007/02/12 14:12:03  gerrit
 * Logger from LogUtil
 *
 * Revision 1.2  2006/02/09 08:02:46  gerrit
 * iterate over string scalars too
 *
 * Revision 1.1  2005/12/28 08:31:33  gerrit
 * introduced StatisticsKeeper-iteration
 *
 */
package nl.nn.adapterframework.statistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.util.AppConstants;
import nl.nn.adapterframework.util.FileUtils;

/**
 * Logs statistics-keeper contents to log.
 * 
 * @author  Gerrit van Brakel
 * @since   4.4.3
 * @version $Id$
 */
public class StatisticsKeeperLogger extends StatisticsKeeperXmlBuilder {

	private String directory=null;
	private int retentionDays=-1;

	public void configure() throws ConfigurationException {
		super.configure();
		AppConstants ac = AppConstants.getInstance();
		if (directory==null)	{
			setDirectory(ac.getResolvedProperty("log.dir"));
		}
		if (retentionDays<0) {	
			setRetentionDays(ac.getInt("statistics.retention",7));
		}
	}


	public void end(Object data) {
		super.end(data);

		if (StringUtils.isNotEmpty(getDirectory())) {
			AppConstants ac = AppConstants.getInstance();		
			String filenamePattern=ac.getResolvedProperty("instance.name")+"-stats_";
			String extension=".log";
			File outfile=FileUtils.getWeeklyRollingFile(directory, filenamePattern, extension, retentionDays);

			FileWriter fw=null;
			try {
				fw = new FileWriter(outfile,true);
				fw.write(getXml(data).toXML());
				fw.write("\n");
			} catch (IOException e) {
				log.error("Could not write statistics to file ["+outfile.getPath()+"]",e);
			} finally {
				if (fw!=null) {
					try {
						fw.close();
					} catch (Exception e) {
						log.error("Could not close statistics file ["+outfile.getPath()+"]",e);
					}
				}
			}
		}
	}
	
	
	public void setRetentionDays(int i) {
		retentionDays = i;
	}
	public int getRetentionDays() {
		return retentionDays;
	}

	public void setDirectory(String string) {
		directory = string;
	}
	public String getDirectory() {
		return directory;
	}

}
