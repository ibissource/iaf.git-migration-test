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
 * $Log: ICorrelatedPullingListener.java,v $
 * Revision 1.7  2011-11-30 13:51:55  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.5  2007/10/03 08:09:34  gerrit
 * changed HashMap to Map
 *
 * Revision 1.4  2004/03/30 07:29:59  gerrit
 * updated javadoc
 *
 * Revision 1.3  2004/03/26 10:42:50  johan
 * added @version tag in javadoc
 *
 */
package nl.nn.adapterframework.core;

import java.util.Map;
/**
 * Additional behaviour for pulling listeners that are able to listen to a specific
 * message, specified by a correlation ID.
 * 
 * @author  Gerrit van Brakel
 * @since   4.0
 * @version $Id$
 */
public interface ICorrelatedPullingListener extends IPullingListener{
	public static final String version = "$RCSfile: ICorrelatedPullingListener.java,v $ $Revision: 1.7 $ $Date: 2011-11-30 13:51:55 $";

	/**
	 * Retrieves messages from queue or other channel,  but retrieves only
	 * messages with the specified correlationId.
	 */
	Object getRawMessage(String correlationId, Map threadContext) throws ListenerException, TimeOutException;
}
