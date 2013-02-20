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
 * $Log: ListenerException.java,v $
 * Revision 1.6  2011-11-30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.2  2011/10/19 14:58:23  peter
 * do not print versions anymore
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2004/03/30 07:29:59  gerrit
 * updated javadoc
 *
 * Revision 1.3  2004/03/26 10:42:45  johan
 * added @version tag in javadoc
 *
 */
package nl.nn.adapterframework.core;

/**
 * Exception thrown by implementations of methods of listeners.
 * 
 * @version $Id$
 * @author Gerrit van Brakel
 */
public class ListenerException extends IbisException {

	public ListenerException() {
		super();
	}
	public ListenerException(String errMsg) {
		super(errMsg);
	}
	public ListenerException(String errMsg, Throwable t) {
		super(errMsg, t);
	}
	public ListenerException(Throwable t) {
		super(t);
	}
}
