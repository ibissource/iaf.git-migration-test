/*
 * $Log: IReceiverStatistics.java,v $
 * Revision 1.6  2011-11-30 13:51:55  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.2  2011/10/19 14:57:12  peter
 * do not print versions anymore
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2004/03/30 07:29:59  gerrit
 * updated javadoc
 *
 * Revision 1.3  2004/03/26 10:42:50  johan
 * added @version tag in javadoc
 *
 */
package nl.nn.adapterframework.core;

import java.util.Iterator;
/**
 * Methods for Receivers to supply statistics to a maintenance clients. Receivers indicate 
 * by implementing this interface that process- and idle statistics may be available for 
 * displaying.
 * 
 * @version Id
 * @author Gerrit van Brakel
 */
public interface IReceiverStatistics  {
/**
 * @return an iterator of {@link nl.nn.adapterframework.util.StatisticsKeeper}s describing the durations of time that
 * the receiver has been waiting between messages.
 */
Iterator getIdleStatisticsIterator();

/**
 * @return an iterator of {@link nl.nn.adapterframework.util.StatisticsKeeper}s describing the durations of time that
 * the receiver has been waiting for the adapter to process messages.
 */
Iterator getProcessStatisticsIterator();
}
