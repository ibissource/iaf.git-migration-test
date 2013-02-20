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
 * $Log: ListenerFactory.java,v $
 * Revision 1.10  2011-11-30 13:51:56  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.8  2011/04/13 08:23:13  gerrit
 * now extends GeneircFactory
 *
 * Revision 1.7  2010/03/18 10:15:27  peter
 * Overrided method copyAttrsToMap instead of createObject so that check of attributes is included
 *
 * Revision 1.6  2007/11/22 08:36:31  gerrit
 * improved logging
 *
 * Revision 1.2.2.4  2007/11/09 12:32:05  tim
 * Conditionalize logging for performance
 *
 * Revision 1.2.2.3  2007/11/09 12:05:56  tim
 * Improve logging of actions
 *
 * Revision 1.2.2.2  2007/11/09 11:59:46  tim
 * Reformat
 *
 * Revision 1.2.2.1  2007/10/24 09:39:48  tim
 * Merge changes from HEAD
 *
 * Revision 1.5  2007/10/24 08:04:23  tim
 * Add logging for case when classname of Listener implementation is replaced
 *
 * Revision 1.4  2007/10/24 07:13:21  tim
 * Rename abstract method 'getBeanName()' to 'getSuggestedBeanName()' since it better reflects the role of the method in the class.
 *
 * Revision 1.3  2007/10/22 14:42:55  tim
 * Override createObject so that a JMS PullingJmsListener is created instead of a 'default' JmsListener when the parent is instance of MessageSendingPipe;

 * this is for compatibility with the MessageSendingPipe using an instance of ICorrelatedPullingListener (which the PushingJmsListener can not provide).

 * 

 * This solution is a workaround to be used until we decide how to refactor the MessageSendingPipe, or if the functionality of correlated listener should be

 * added to PushingJmsListener (which would contradict it's design, and currently have the unwanted side-effect ofcreating a JMS Container for the queue).
 *
 * Revision 1.2  2007/10/09 15:29:43  gerrit
 * Direct copy from Ibis-EJB:
 * first version in HEAD
 *
 */
package nl.nn.adapterframework.configuration;

import java.util.Map;

import nl.nn.adapterframework.pipes.MessageSendingPipe;

import org.xml.sax.Attributes;

/**
 * Factory for instantiating listeners from the Digester framework.
 * Instantiates correlated listener in the context of a MessageSendingPipe.
 * 
 * @author  Tim van der Leeuw
 * @since   4.8
 * @version $Id$
 */
public class ListenerFactory extends GenericFactory {
    public static final String JMS_LISTENER_CLASSNAME_SUFFIX = ".JmsListener";
    protected static final String CORRELATED_LISTENER_CLASSNAME = "nl.nn.adapterframework.jms.PullingJmsListener";

	protected Map copyAttrsToMap(Attributes attrs) {
		Map map = super.copyAttrsToMap(attrs); 
		String className = attrs.getValue("className");
		if (className != null && getDigester().peek() instanceof MessageSendingPipe && className.endsWith(JMS_LISTENER_CLASSNAME_SUFFIX)) {
			if (log.isDebugEnabled()) {
				log.debug("JmsListener is created as part of a MessageSendingPipe; replace classname with '" + CORRELATED_LISTENER_CLASSNAME + "' to ensure compatibility");
			}
			map.put("className",CORRELATED_LISTENER_CLASSNAME);
		}
		return map;
	}

}
