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
 * $Log: EventThrowing.java,v $
 * Revision 1.4  2011-11-30 13:51:43  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2009/05/13 08:18:50  gerrit
 * improved monitoring: triggers can now be filtered multiselectable on adapterlevel
 *
 * Revision 1.1  2008/07/14 17:21:18  gerrit
 * first version of flexible monitoring
 *
 */
package nl.nn.adapterframework.monitoring;

import nl.nn.adapterframework.core.IAdapter;

/**
 * Interface to be implemented by objects to be monitored; Is called by code that handles event.
 * 
 * @author  Gerrit van Brakel
 * @since   4.9
 * @version $Id$
 */
public interface EventThrowing {
	
	public String getEventSourceName();
	public IAdapter getAdapter();
}
