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
 * $Log: ISecurityHandler.java,v $
 * Revision 1.4  2012-06-01 10:52:52  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.3  2011/11/30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2005/07/05 09:57:00  gerrit
 * introduction of ISecurityHandler
 *
 */
package nl.nn.adapterframework.core;

import java.security.Principal;

import org.apache.commons.lang.NotImplementedException;

/**
 * Defines behaviour that can be used to assert identity of callers of a pipeline.
 * 
 * @author Gerrit van Brakel
 * @since  4.3
 * @version $Id$
 */
public interface ISecurityHandler {
	
	public boolean isUserInRole(String role, IPipeLineSession session) throws NotImplementedException;
	public Principal getPrincipal(IPipeLineSession session) throws NotImplementedException;

}
