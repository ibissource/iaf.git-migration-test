/*
 * $Log: HasStatistics.java,v $
 * Revision 1.3  2011-11-30 13:51:48  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:52  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2009/12/29 14:25:18  gerrit
 * moved statistics to separate package
 *
 * Revision 1.4  2009/06/05 07:35:16  gerrit
 * support for adapter level only statistics
 * added throws clause to iterateOverStatistics()
 *
 * Revision 1.3  2008/09/04 12:17:53  gerrit
 * added action codes
 *
 * Revision 1.2  2008/08/27 16:23:24  gerrit
 * added reset option to statisticsdump
 *
 * Revision 1.1  2008/05/14 09:29:33  gerrit
 * introduction of interface HasStatistics
 *
 */
package nl.nn.adapterframework.statistics;

import nl.nn.adapterframework.core.SenderException;

/**
 * Interface to be implemented by objects like Pipes or Senders that maintain additional statistics themselves.
 * 
 * @author  Gerrit van Brakel
 * @since   4.9
 * @version Id
 */
public interface HasStatistics {

	public static final int STATISTICS_ACTION_SUMMARY=0;
	public static final int STATISTICS_ACTION_FULL=1;
	public static final int STATISTICS_ACTION_RESET=2;
	public static final int STATISTICS_ACTION_MARK_MAIN=3;
	public static final int STATISTICS_ACTION_MARK_FULL=4;

	public void iterateOverStatistics(StatisticsKeeperIterationHandler hski, Object data, int action) throws SenderException ;
}
