/*
 * $Log: PercentileEstimator.java,v $
 * Revision 1.3  2011-11-30 13:52:02  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:51  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2009/12/29 14:25:18  gerrit
 * moved statistics to separate package
 *
 * Revision 1.4  2008/08/27 16:25:16  gerrit
 * added clear()
 *
 * Revision 1.3  2006/09/07 08:37:51  gerrit
 * added sample return facility
 *
 * Revision 1.2  2005/03/10 09:52:16  gerrit
 * reworked percentile estimation
 *
 * Revision 1.1  2005/02/02 16:30:07  gerrit
 * modular percentile estimation
 *
 */
package nl.nn.adapterframework.statistics.percentiles;

import nl.nn.adapterframework.util.XmlBuilder;

/**
 * Interface to define estimators for percentiles.
 * 
 * @author Gerrit van Brakel
 * @version Id
 */
public interface PercentileEstimator {
	
	int getNumPercentiles();
	int getPercentage(int index);
	void addValue(long value, long count, long min, long max);
	double getPercentileEstimate(int index, long count, long min, long max);

	int getSampleCount(long count, long min, long max);
	XmlBuilder getSample(int index, long count, long min, long max);
	
	public void clear();

}
