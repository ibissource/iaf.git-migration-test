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
 * $Log: StatisticsKeeperIterationHandler.java,v $
 * Revision 1.3  2011-11-30 13:51:48  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:52  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2009/12/29 14:25:18  gerrit
 * moved statistics to separate package
 *
 * Revision 1.5  2009/08/26 15:40:07  gerrit
 * support for separated adapter-only and detailed statistics
 *
 * Revision 1.4  2009/06/05 07:36:54  gerrit
 * allow methods to throw SenderException
 * handle scalar now accepts only long and date values
 *
 * Revision 1.3  2008/05/14 09:30:33  gerrit
 * simplified methodnames of StatisticsKeeperIterationHandler
 *
 * Revision 1.2  2006/02/09 08:02:46  gerrit
 * iterate over string scalars too
 *
 * Revision 1.1  2005/12/28 08:31:33  gerrit
 * introduced StatisticsKeeper-iteration
 *
 */
package nl.nn.adapterframework.statistics;

import java.util.Date;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.SenderException;

/**
 * Allows operations on iterations over all statistics keepers.
 * 
 * @author  Gerrit van Brakel
 * @since  
 * @version $Id$
 */
public interface StatisticsKeeperIterationHandler {

	public static final long PERIOD_ALLOWED_LENGTH_HOUR=1100*60*60; // 10% extra
	public static final long PERIOD_ALLOWED_LENGTH_DAY=PERIOD_ALLOWED_LENGTH_HOUR*24;
	public static final long PERIOD_ALLOWED_LENGTH_WEEK=PERIOD_ALLOWED_LENGTH_DAY*7;
	public static final long PERIOD_ALLOWED_LENGTH_MONTH=PERIOD_ALLOWED_LENGTH_DAY*31;
	public static final long PERIOD_ALLOWED_LENGTH_YEAR=PERIOD_ALLOWED_LENGTH_DAY*366;

	public static final String[] PERIOD_FORMAT_HOUR={"hour","HH"};
	public static final String[] PERIOD_FORMAT_DATEHOUR={"datehour","yyyy-MM-dd HH"};
	public static final String[] PERIOD_FORMAT_DAY={"day","dd"};
	public static final String[] PERIOD_FORMAT_DATE={"date","yyyy-MM-dd"};
	public static final String[] PERIOD_FORMAT_WEEKDAY={"weekday","E"};
	public static final String[] PERIOD_FORMAT_WEEK={"week","ww"};
	public static final String[] PERIOD_FORMAT_YEARWEEK={"yearweek","yyyy'W'ww"};
	public static final String[] PERIOD_FORMAT_MONTH={"month","MM"};
	public static final String[] PERIOD_FORMAT_YEARMONTH={"yearmonth","yyyy-MM"};
	public static final String[] PERIOD_FORMAT_YEAR={"year","yyyy"};


	public void configure() throws ConfigurationException;
	public Object start(Date now, Date mainMark, Date detailMark) throws SenderException;
	public void end(Object data) throws SenderException;
	public void handleStatisticsKeeper(Object data, StatisticsKeeper sk) throws SenderException;
	public void handleScalar(Object data, String scalarName, long value) throws SenderException;
	public void handleScalar(Object data, String scalarName, Date value) throws SenderException;
	public Object openGroup(Object parentData, String name, String type) throws SenderException;
	public void  closeGroup(Object data) throws SenderException;
}
