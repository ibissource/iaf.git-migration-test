/*
 * $Log: ISecurityHandler.java,v $
 * Revision 1.4  2012-06-01 10:52:52  m00f069
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
 * @version Id
 */
public interface ISecurityHandler {
	
	public boolean isUserInRole(String role, IPipeLineSession session) throws NotImplementedException;
	public Principal getPrincipal(IPipeLineSession session) throws NotImplementedException;

}
