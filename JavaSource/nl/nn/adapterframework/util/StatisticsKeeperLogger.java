/*
 * $Log: StatisticsKeeperLogger.java,v $
 * Revision 1.5  2008-07-24 12:24:12  europe\L190409
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
package nl.nn.adapterframework.util;

import org.apache.log4j.Logger;

/**
 * Logs statistics-keeper contents to log
 * 
 * @author  Gerrit van Brakel
 * @since   4.4.3
 * @version Id
 */
public class StatisticsKeeperLogger extends StatisticsKeeperXmlBuilder {
	protected Logger log = LogUtil.getLogger("nl.nn.adapterframework.statistics");

	public void end(Object data) {
		super.end(data);
		log.info(getXml().toXML());
	}
	
	
}
