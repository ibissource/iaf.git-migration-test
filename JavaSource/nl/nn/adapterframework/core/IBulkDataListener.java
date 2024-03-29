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
 * $Log: IBulkDataListener.java,v $
 * Revision 1.4  2011-11-30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2008/07/24 12:31:45  gerrit
 * fix
 *
 * Revision 1.1  2008/07/24 12:03:40  gerrit
 * fix transactional FXF
 *
 */
package nl.nn.adapterframework.core;

import java.util.Map;

/**
 * Listener extension that allows to transfer of a lot of data, and do it within the transaction handling.
 * 
 * @author  Gerrit van Brakel
 * @since   4.9
 * @version $Id$
 */
public interface IBulkDataListener extends IListener {

	/**
	 * Retrieves the bulk data associated with the message, stores it in a file or something similar.
	 * It returns the handle to the file as a result, and uses that as the message for the pipeline.
	 * @return input message for adapter.
	 */
	String retrieveBulkData(Object rawMessage, String message, Map context) throws ListenerException;

}
