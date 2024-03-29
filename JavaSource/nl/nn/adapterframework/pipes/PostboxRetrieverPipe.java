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
 * $Log: PostboxRetrieverPipe.java,v $
 * Revision 1.9  2012-06-01 10:52:49  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.8  2011/11/30 13:51:50  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:45  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2007/10/03 08:56:30  gerrit
 * corrected version string
 *
 * Revision 1.5  2007/10/03 08:55:41  gerrit
 * changed HashMap to Map
 *
 * Revision 1.4  2004/10/05 11:39:36  gerrit
 * removed unused imports
 *
 * Revision 1.3  2004/08/23 13:10:09  gerrit
 * updated JavaDoc
 *
 * Revision 1.2  2004/05/21 12:05:09  unknown0
 * Correct errors in javadoc
 *
 * Revision 1.1  2004/05/21 10:47:30  unknown0
 * Add (modifications) due to the postbox retriever implementation
 *
 * Revision 1.1  2004/05/21 07:59:30  unknown0
 * Add (modifications) due to the postbox sender implementation
 *
 */
package nl.nn.adapterframework.pipes;


import java.util.Map;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IPostboxListener;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.core.PipeStartException;

/**
 * Retrieves a message using an {@link IPostboxListener}. 
 * 
 * Note that most listeners allow you to specify a timeout. The timeout has the following
 * meaning:
 * <ul> 
 * <li>&lt;0 = no wait</li>
 * <li>0 = block until message available</li>
 * <li>&gt;= 0 maximum wait in milliseconds<li>
 * </ul> 
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMaxThreads(int) maxThreads}</td><td>maximum number of threads that may call {@link #doPipe(Object, PipeLineSession)} simultaneously</td><td>0 (unlimited)</td></tr>
 * <tr><td>{@link #setResultOnEmptyPostbox(String) resultOnEmptyPostbox}</td><td>result when no object is on postbox</td><td>empty postbox</td></tr>
 * <tr><td>{@link #setForwardName(String) forwardName}</td>  <td>name of forward returned upon completion</td><td>"success"</td></tr>
 * <tr><td>{@link #addParameter(Parameter) parameterList}</td><td>Parameters of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td><code>listener.*</td><td>any attribute of the listener instantiated by descendant classes</td><td>&nbsp;</td></tr>
 * </table>
 * <table border="1">
 * <tr><th>nested elements</th><th>description</th></tr>
 * <tr><td>{@link IPostboxListener listener}</td><td>specification of postbox listener to retrieve messages from</td></tr>
 * </table>
 * </p>
 * <p><b>Exits:</b>
 * <table border="1">
 * <tr><th>state</th><th>condition</th></tr>
 * <tr><td>"success"</td><td>default when the message was successfully sent</td></tr>
 * <tr><td><i>{@link #setForwardName(String) forwardName}</i></td><td>if specified, and otherwise under same condition as "success"</td></tr>
 * </table>
 * </p>
  * 
 * @author  john
 * @version $Id$
 */
public class PostboxRetrieverPipe  extends FixedForwardPipe {
	public static final String version = "$RCSfile: PostboxRetrieverPipe.java,v $ $Revision: 1.9 $ $Date: 2012-06-01 10:52:49 $";

	private IPostboxListener listener = null;
	private String resultOnEmptyPostbox = "empty postbox";
		
	public void configure() throws ConfigurationException {
		super.configure();

		if (getListener() == null) {
				throw new ConfigurationException(getLogPrefix(null) + "no sender defined ");
		}
	}

	public IPostboxListener getListener() {
		return listener;
	}

	public void setListener(IPostboxListener listener) {
		this.listener = listener;
	}

	public void start() throws PipeStartException {
		try {
			getListener().open();
		} 
		catch (Exception e) {
			throw new PipeStartException(e);
		}
	}

	public void stop() {
		try {
			getListener().close();
		} 
		catch (Exception e) {
			log.warn(getLogPrefix(null) + " exception closing sender", e);
		}
	}

	public PipeRunResult doPipe(Object input, IPipeLineSession session) throws PipeRunException {
		if (! (input instanceof String)) {
			throw new PipeRunException(this, "String expected, got a [" + input.getClass().getName() + "]");
		}

		Map threadContext = null;
		try {
			threadContext = getListener().openThread();
			Object rawMessage = getListener().retrieveRawMessage((String)input, threadContext);
			
			if (rawMessage == null)
				return new PipeRunResult(findForward("emptyPostbox"), getResultOnEmptyPostbox());
				
			String result = getListener().getStringFromRawMessage(rawMessage, threadContext);
			return new PipeRunResult(getForward(), result);
		} 
		catch (Exception e) {
			throw new PipeRunException( this, getLogPrefix(session) + "caught exception", e);
		} 
		finally {
			try {
				getListener().closeThread(threadContext);
			} 
			catch (ListenerException le) {
				log.error(getLogPrefix(session)+"got error closing listener");
			}
		}
	}

	public String getResultOnEmptyPostbox() {
		return resultOnEmptyPostbox;
	}

	public void setResultOnEmptyPostbox(String string) {
		resultOnEmptyPostbox = string;
	}

}
