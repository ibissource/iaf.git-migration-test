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
 * $Log: SynchedLocalTransactionFailedException.java,v $
 * Revision 1.1  2012-02-06 14:33:04  jaco
 * Implemented JCo 3 based on the JCo 2 code. JCo2 code has been moved to another package, original package now contains classes to detect the JCo version available and use the corresponding implementation.
 *
 * Revision 1.3  2011/11/30 13:51:53  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:54  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2008/01/29 15:49:02  gerrit
 * first version
 *
 */

package nl.nn.adapterframework.extensions.sap.jco2.tx;

import nl.nn.adapterframework.extensions.sap.jco2.SapException;

/**
 * Exception thrown when a synchronized local transaction failed to complete
 * (after the main transaction has already completed).
 *
 * @author Gerrit van Brakel
 * @since  4.8
 * @see    ConnectionFactoryUtils
 */
public class SynchedLocalTransactionFailedException extends RuntimeException {

	/**
	 * Create a new SynchedLocalTransactionFailedException.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public SynchedLocalTransactionFailedException(String msg, SapException cause) {
		super(msg, cause);
	}

}
