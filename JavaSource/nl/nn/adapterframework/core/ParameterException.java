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
 * $Log: ParameterException.java,v $
 * Revision 1.3  2011-11-30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.2  2011/10/19 14:59:25  peter
 * do not print versions anymore
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2004/10/05 10:03:58  gerrit
 * reorganized parameter code
 *
 * Revision 1.4  2004/03/30 07:29:54  gerrit
 * updated javadoc
 *
 */
package nl.nn.adapterframework.core;

/**
 * Exception thrown by the ISender (implementation) to notify
 * that the sending did not succeed.
 * 
 * @version $Id$
 * @author  Gerrit van Brakel
 */
public class ParameterException extends IbisException {
		
	public ParameterException() {
		super();
	}
	public ParameterException(String errMsg) {
		super(errMsg);
	}
	public ParameterException(String errMsg, Throwable t) {
		super(errMsg, t);
	}
	public ParameterException(Throwable t) {
		super(t);
	}
}
