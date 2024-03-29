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
 * $Log: IMonitorAdapter.java,v $
 * Revision 1.6  2011-11-30 13:51:43  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2008/08/07 11:31:27  gerrit
 * rework
 *
 * Revision 1.3  2008/07/24 12:34:01  gerrit
 * rework
 *
 * Revision 1.2  2008/05/21 10:52:17  gerrit
 * modified monitorAdapter interface
 *
 * Revision 1.1  2007/09/27 12:55:41  gerrit
 * introduction of monitoring
 *
 */
package nl.nn.adapterframework.monitoring;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.util.XmlBuilder;

/**
 * Interface to monitoring service. 
 * 
 * @author  Gerrit van Brakel
 * @since   4.7
 * @version $Id$
 */
public interface IMonitorAdapter {

	void configure() throws ConfigurationException;
	
	void fireEvent(String subSource, EventTypeEnum eventType, SeverityEnum severity, String message, Throwable t); 

	void register(Object x);
	public XmlBuilder toXml();
	
	void setName(String name);	
	String getName();
}
