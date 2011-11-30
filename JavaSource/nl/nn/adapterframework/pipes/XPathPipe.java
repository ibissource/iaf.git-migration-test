/*
 * $Log: XPathPipe.java,v $
 * Revision 1.10  2011-11-30 13:51:50  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:45  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.8  2008/12/30 17:01:12  peter
 * added configuration warnings facility (in Show configurationStatus)
 *
 * Revision 1.7  2004/10/19 13:53:03  gerrit
 * replaced by XsltPipe
 *
 * Revision 1.6  2004/10/05 10:57:21  gerrit
 * deprecated,
 * all functionality now in XsltPipe
 *
 * Revision 1.5  2004/08/31 13:19:58  unknown0
 * Allow multithreading
 *
 * Revision 1.4  2004/08/03 12:28:46  gerrit
 * replaced embedded stylesheet with call to xmlutils.createxpathevaluator
 *
 * Revision 1.3  2004/05/05 09:30:53  johan
 * added sessionkey feature
 *
 * Revision 1.2  2004/04/27 11:42:40  unknown0
 * Access properties via getters
 *
 * Revision 1.1  2004/04/27 10:52:17  unknown0
 * Pipe that evaluates an xpath expression on the inpup
 * 
 */
package nl.nn.adapterframework.pipes;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.configuration.ConfigurationWarnings;

/**
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setXpathExpression(String) xpathExpression}</td><td>Expression to evaluate</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSessionKey(String) sessionKey}</td><td>If specified, the result is put 
 * in the PipeLineSession under the specified key, and the result of this pipe will be 
 * the same as the input (the xml). If NOT specified, the result of the xpath expression 
 * will be the result of this pipe</td><td>&nbsp;</td></tr>
 * </table>
 * </p>
 * <p><b>Exits:</b>
 * <table border="1">
 * <tr><th>state</th><th>condition</th></tr>
 * <tr><td>"success"</td><td>default</td></tr>
 * <tr><td><i>{@link #setForwardName(String) forwardName}</i></td><td>if specified</td></tr>
 * </table>
 * </p>
 * 
 * @author J. Dekker
 * @version Id
 * @deprecated Please use XsltPipe, that has the same functionality
 */
public class XPathPipe extends XsltPipe {
	public void configure() throws ConfigurationException {
		ConfigurationWarnings configWarnings = ConfigurationWarnings.getInstance();
		String msg = getLogPrefix(null)+"The class ["+getClass().getName()+"] has been deprecated. Please change to ["+getClass().getSuperclass().getName()+"]";
		configWarnings.add(log, msg);
		super.configure();
	}
}
