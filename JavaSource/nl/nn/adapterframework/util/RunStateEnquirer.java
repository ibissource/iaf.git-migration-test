/*
 * $Log: RunStateEnquirer.java,v $
 * Revision 1.3  2011-11-30 13:51:48  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2005/10/27 08:42:15  gerrit
 * introduced RunStateEnquiries
 *
 */
package nl.nn.adapterframework.util;

/**
 * Interface to support enquiries about the run state.
 * 
 * @version Id
 * @author Gerrit van Brakel
 */
public interface RunStateEnquirer {
	public static final String version="$RCSfile: RunStateEnquirer.java,v $ $Revision: 1.3 $ $Date: 2011-11-30 13:51:48 $";

	public RunStateEnum getRunState();
	public boolean isInState(RunStateEnum state);	
}
