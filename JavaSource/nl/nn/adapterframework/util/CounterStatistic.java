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
 * $Log: CounterStatistic.java,v $
 * Revision 1.6  2011-11-30 13:51:49  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2009/12/29 14:40:48  gerrit
 * modified imports to reflect move of statistics classes to separate package
 *
 * Revision 1.3  2009/06/05 07:34:17  gerrit
 * support for adapter level only statistics
 *
 * Revision 1.2  2008/09/22 13:25:45  gerrit
 * renamed methodname
 *
 * Revision 1.1  2008/09/17 09:58:12  gerrit
 * first version
 *
 */
package nl.nn.adapterframework.util;

import nl.nn.adapterframework.statistics.HasStatistics;

/**
 * Counter value that is maintained with statistics.
 * 
 * @author  Gerrit van Brakel
 * @since   4.9
 * @version $Id$
 */
public class CounterStatistic extends Counter {

	long mark;
	
	public CounterStatistic(int startValue) {
		super(startValue);
		mark=startValue;
	}

	public void performAction(int action) {
		if (action==HasStatistics.STATISTICS_ACTION_FULL || action==HasStatistics.STATISTICS_ACTION_SUMMARY) {
			return;
		}
		if (action==HasStatistics.STATISTICS_ACTION_RESET) {
			clear();
		}
		if (action==HasStatistics.STATISTICS_ACTION_MARK_FULL || action==HasStatistics.STATISTICS_ACTION_MARK_MAIN) {
			synchronized (this) {
				mark=getValue();
			}
		}
	}

	public synchronized long getIntervalValue() {
		return getValue()-mark;
	}

	public synchronized void clear() {
		super.clear();
		mark=0;	
	}

}
