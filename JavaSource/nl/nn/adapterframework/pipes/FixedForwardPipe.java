/*
 * $Log: FixedForwardPipe.java,v $
 * Revision 1.5  2004-10-19 13:52:31  L190409
 * super.configure in configure()
 *
 * Revision 1.4  2004/03/26 10:42:34  johan
 * added @version tag in javadoc
 *
 * Revision 1.3  2004/03/24 14:04:26  gerrit
 * getLogPrefix in thrown exceptions
 *
 */
package nl.nn.adapterframework.pipes;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.PipeForward;

/**
 * Provides provides a base-class for a Pipe that has always the same forward.
 * Ancestor classes should call <code>super.configure()</code> in their <code>configure()</code>-methods.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMaxThreads(int) maxThreads}</td><td>maximum number of threads that may call {@link #doPipe(Object, nl.nn.adapterframework.core.PipeLineSession)} simultaneously</td><td>0 (unlimited)</td></tr>
 * <tr><td>{@link #setForwardName(String) forwardName}</td>  <td>name of forward returned upon completion</td><td>"success"</td></tr>
 * </table>
 * </p>
 * <p><b>Exits:</b>
 * <table border="1">
 * <tr><th>state</th><th>condition</th></tr>
 * <tr><td>"success"</td><td>default</td></tr>
 * <tr><td><i>{@link #setForwardName(String) forwardName}</i></td><td>if specified</td></tr>
 * </table>
 * </p>
 * @version Id
 * @author Gerrit van Brakel
 */
public class FixedForwardPipe extends AbstractPipe {
 	public static final String version="$Id: FixedForwardPipe.java,v 1.5 2004-10-19 13:52:31 L190409 Exp $";

    private String forwardName = "success";
    private PipeForward forward;
    /**
     * checks for correct configuration of forward
     */
    public void configure() throws ConfigurationException {
    	super.configure();
        forward = findForward(forwardName);
        if (forward == null)
            throw new ConfigurationException(getLogPrefix(null) + "has no forward with name [" + forwardName + "]");
    }
	protected PipeForward getForward() {
		return forward;
	}
 	/**
 	 * Sets the name of the <code>forward</code> that is looked up
 	 * upon completion.
 	 */ 
	public void setForwardName(String forwardName) {
        this.forwardName = forwardName;
    }
	public String getForwardName() {
		return forwardName;
	}
}
