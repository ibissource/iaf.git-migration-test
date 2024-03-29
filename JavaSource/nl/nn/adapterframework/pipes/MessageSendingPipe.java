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
 * $Log: MessageSendingPipe.java,v $
 * Revision 1.83  2013-01-25 13:34:20  peter
 * added useInputForExtract attribute
 *
 * Revision 1.82  2012/10/24 14:14:08  peter
 * added attribute auditTrailSessionKey
 *
 * Revision 1.81  2012/08/21 15:51:35  jaco
 * Show duration statistics of sender message log too.
 *
 * Revision 1.80  2012/06/01 10:52:49  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.79  2012/05/04 09:42:36  jaco
 * Use PipeProcessors (to e.g. handle statistics) for Validators and Wrappers
 *
 * Revision 1.78  2012/03/05 14:45:55  peter
 * changed order of validate and wrap
 *
 * Revision 1.77  2011/11/30 13:51:50  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.2  2011/11/25 11:27:58  peter
 * added inputWrapper and outputWrapper pipes to PipeLine and MessageSendingPipes
 *
 * Revision 1.1  2011/10/19 14:49:45  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.75  2011/08/25 14:33:58  jaco
 * Exclude IbisLocalSender from check on messageLog
 *
 * Revision 1.74  2011/08/22 14:26:50  gerrit
 * set size statistics on by default
 *
 * Revision 1.73  2011/08/18 14:38:43  gerrit
 * added validator statistics
 *
 * Revision 1.72  2011/05/09 15:27:11  gerrit
 * fixed bug in timeout monitoring
 *
 * Revision 1.71  2011/02/25 11:06:49  peter
 * adjusted javadoc
 *
 * Revision 1.70  2011/01/13 12:30:15  peter
 * added check on existence messageLog for asynchronous senders with sibling listener
 *
 * Revision 1.69  2010/12/07 14:31:21  peter
 * added retry facility (new attributes maxRetries, retryMinInterval, retryMaxInterval, retryXPath and retryNamespaceDefs)
 *
 * Revision 1.68  2010/09/10 11:21:45  gerrit
 * corrected labelNamespaceDefs
 *
 * Revision 1.67  2010/09/07 15:55:13  jaco
 * Removed IbisDebugger, made it possible to use AOP to implement IbisDebugger functionality.
 *
 * Revision 1.66  2010/08/20 07:36:26  peter
 * added timeOutOnResult and exceptionOnResult attribute
 *
 * Revision 1.65  2010/07/12 12:54:00  gerrit
 * allow to specfiy namespace prefixes to be used in XPath-epressions
 *
 * Revision 1.64  2010/03/10 14:30:05  peter
 * rolled back testtool adjustments (IbisDebuggerDummy)
 *
 * Revision 1.63  2010/03/05 15:49:51  peter
 * added attribute correlationIDStyleSheet
 *
 * Revision 1.62  2010/03/04 15:51:20  peter
 * added attribute labelStyleSheet
 *
 * Revision 1.61  2010/02/25 13:32:03  peter
 * removed default value and adjusted functioning of resultOnTimeOut attribute
 *
 * Revision 1.59  2010/02/03 14:27:33  gerrit
 * check for interrupt
 *
 * Revision 1.58  2009/12/29 15:00:02  peter
 * added attribute labelXPath
 *
 * Revision 1.57  2009/12/29 14:35:19  gerrit
 * modified imports to reflect move of statistics classes to separate package
 *
 * Revision 1.56  2009/10/26 14:01:20  peter
 * cosmetic change
 *
 * Revision 1.55  2009/07/13 11:49:02  peter
 * added attribute correlationIDSessionKey
 *
 * Revision 1.54  2009/07/13 10:08:53  peter
 * adjusted javadoc
 *
 * Revision 1.53  2009/06/05 07:24:55  gerrit
 * added throws clause to iterateOverStatistics()
 *
 * Revision 1.52  2009/05/06 11:41:08  gerrit
 * improved configuration of validators
 *
 * Revision 1.51  2009/04/09 12:15:53  peter
 * store message from MailSender in mail-safe form to MessageLog
 *
 * Revision 1.50  2009/03/13 14:32:36  peter
 * *** empty log message ***
 *
 * Revision 1.49  2008/10/06 14:28:57  gerrit
 * avoid NPE
 *
 * Revision 1.48  2008/09/04 12:13:00  gerrit
 * collect interval statistics
 *
 * Revision 1.47  2008/09/01 12:59:38  gerrit
 * corrected log message
 *
 * Revision 1.46  2008/08/27 16:18:49  gerrit
 * added reset option to statisticsdump
 *
 * Revision 1.45  2008/08/13 13:40:34  gerrit
 * modified eventnames
 *
 * Revision 1.44  2008/07/14 17:25:52  gerrit
 * use flexible monitoring
 *
 * Revision 1.43  2008/06/18 12:29:09  gerrit
 * monitor timeouts
 *
 * Revision 1.42  2008/05/15 15:16:42  gerrit
 * make ParameterResolutionContext also for senders parameters
 *
 * Revision 1.41  2008/05/14 09:31:39  gerrit
 * introduction of interface HasStatistics
 *
 * Revision 1.40  2008/03/20 12:08:01  gerrit
 * improved stub handling
 *
 * Revision 1.39  2008/02/26 09:18:50  gerrit
 * updated javadoc
 *
 * Revision 1.38  2008/01/30 14:49:21  gerrit
 * updated javadoc, removed superfluous configure()s
 *
 * Revision 1.37  2007/12/17 08:57:40  gerrit
 * input and output validation
 *
 * Revision 1.36  2007/12/10 10:11:39  gerrit
 * added input/output validation
 *
 * Revision 1.35  2007/10/03 08:52:56  gerrit
 * changed HashMap to Map
 *
 * Revision 1.34  2007/07/10 08:03:04  gerrit
 * move String check to calling of sender
 *
 * Revision 1.33  2007/06/19 12:08:31  gerrit
 * log when using stub
 *
 * Revision 1.32  2007/06/12 11:23:18  gerrit
 * added correlationIdXPath (...)
 *
 * Revision 1.30  2007/06/07 12:28:32  gerrit
 * avoid messageid to be null
 *
 * Revision 1.29  2007/05/23 09:24:27  gerrit
 * added messageLog functionality
 *
 * Revision 1.28  2007/05/09 09:46:22  gerrit
 * corrected javadoc
 * optimized stubFile code
 *
 * Revision 1.27  2007/02/12 14:50:07  gerrit
 * added result checking facilities (by PL)
 *
 * Revision 1.26  2007/02/12 10:38:00  gerrit
 * added attribute stubFileName (by PL)
 *
 * Revision 1.25  2007/02/05 14:59:40  gerrit
 * update javadoc
 *
 * Revision 1.24  2006/12/28 14:21:22  gerrit
 * updated javadoc
 *
 * Revision 1.23  2006/12/13 16:29:20  gerrit
 * catch null input
 *
 * Revision 1.22  2006/01/05 14:36:30  gerrit
 * updated javadoc
 *
 * Revision 1.21  2005/10/24 09:20:20  gerrit
 * made namespaceAware an attribute of AbstractPipe
 *
 * Revision 1.20  2005/09/08 15:59:14  gerrit
 * return something when asynchronous sender has no listener
 *
 * Revision 1.19  2005/08/24 15:53:57  gerrit
 * improved error message for configuration exception
 *
 * Revision 1.18  2005/07/05 11:51:54  gerrit
 * improved logging of receiving result
 *
 * Revision 1.17  2004/10/19 06:39:20  gerrit
 * modified parameter handling, introduced IWithParameters
 *
 * Revision 1.16  2004/10/14 16:11:12  gerrit
 * changed ParameterResolutionContext from Object,Hashtable to String, PipelineSession
 *
 * Revision 1.15  2004/10/05 10:53:20  gerrit
 * added support for parameterized senders
 *
 * Revision 1.14  2004/09/08 14:16:37  gerrit
 * catch more throwables in doPipe()
 *
 * Revision 1.13  2004/09/01 11:28:14  gerrit
 * added exception-forward
 *
 * Revision 1.12  2004/08/23 13:10:09  gerrit
 * updated JavaDoc
 *
 * Revision 1.11  2004/08/09 13:52:34  gerrit
 * introduction of function propagateName()
 * catches more exceptions in start()
 *
 * Revision 1.10  2004/07/19 13:23:51  gerrit
 * improved logging + no exception but only warning on timeout if no timeoutforward exists
 *
 * Revision 1.9  2004/07/07 13:49:12  gerrit
 * improved handling of timeout when no timeout-forward exists
 *
 * Revision 1.8  2004/06/21 09:58:54  gerrit
 * Changed exception handling for starting pipe; Exception thrown now contains pipename
 *
 * Revision 1.7  2004/05/21 07:59:30  unknown0
 * Add (modifications) due to the postbox sender implementation
 *
 * Revision 1.6  2004/04/15 15:07:57  johan
 * when a timeout occured, the receiver was not closed. Fixed it.
 *
 * Revision 1.5  2004/03/30 07:30:05  gerrit
 * updated javadoc
 *
 */
package nl.nn.adapterframework.pipes;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.configuration.ConfigurationUtils;
import nl.nn.adapterframework.configuration.ConfigurationWarnings;
import nl.nn.adapterframework.core.HasPhysicalDestination;
import nl.nn.adapterframework.core.HasSender;
import nl.nn.adapterframework.core.ICorrelatedPullingListener;
import nl.nn.adapterframework.core.IExtendedPipe;
import nl.nn.adapterframework.core.IPipe;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.ISender;
import nl.nn.adapterframework.core.ISenderWithParameters;
import nl.nn.adapterframework.core.ITransactionalStorage;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.core.ParameterException;
import nl.nn.adapterframework.core.PipeForward;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.core.PipeStartException;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.core.TimeOutException;
import nl.nn.adapterframework.errormessageformatters.ErrorMessageFormatter;
import nl.nn.adapterframework.jdbc.JdbcTransactionalStorage;
import nl.nn.adapterframework.monitoring.EventThrowing;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.ParameterList;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;
import nl.nn.adapterframework.processors.ListenerProcessor;
import nl.nn.adapterframework.processors.PipeProcessor;
import nl.nn.adapterframework.senders.MailSender;
import nl.nn.adapterframework.statistics.HasStatistics;
import nl.nn.adapterframework.statistics.StatisticsKeeper;
import nl.nn.adapterframework.statistics.StatisticsKeeperIterationHandler;
import nl.nn.adapterframework.util.AppConstants;
import nl.nn.adapterframework.util.ClassUtils;
import nl.nn.adapterframework.util.Misc;
import nl.nn.adapterframework.util.TransformerPool;
import nl.nn.adapterframework.util.XmlUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

/**
 * Sends a message using a {@link ISender} and optionally receives a reply from the same sender, or 
 * from a {@link nl.nn.adapterframework.core.ICorrelatedPullingListener listener}.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>className</td><td>nl.nn.adapterframework.pipes.MessageSendingPipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMaxThreads(int) maxThreads}</td><td>maximum number of threads that may call {@link #doPipe(Object, nl.nn.adapterframework.core.PipeLineSession)} simultaneously</td><td>0 (unlimited)</td></tr>
 * <tr><td>{@link #setStubFileName(String) stubFileName}</td><td>when set, the Pipe returns a message from a file, instead of doing the regular process</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setDurationThreshold(long) durationThreshold}</td><td>if durationThreshold >=0 and the duration (in milliseconds) of the message processing exceeded the value specified the message is logged informatory</td><td>-1</td></tr>
 * <tr><td>{@link #setGetInputFromSessionKey(String) getInputFromSessionKey}</td><td>when set, input is taken from this session key, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setStoreResultInSessionKey(String) storeResultInSessionKey}</td><td>when set, the result is stored under this session key</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCheckXmlWellFormed(boolean) checkXmlWellFormed}</td><td>when set <code>true</code>, the XML well-formedness of the result is checked</td><td>false</td></tr>
 * <tr><td>{@link #setCheckRootTag(String) checkRootTag}</td><td>when set, besides the XML well-formedness the root element of the result is checked to be equal to the value set</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setPreserveInput(boolean) preserveInput}</td><td>when set <code>true</code>, the input of a pipe is restored before processing the next one</td><td>false</td></tr>
 * <tr><td>{@link #setNamespaceAware(boolean) namespaceAware}</td><td>controls namespaceAwareness for parameters</td><td>application default</td></tr>
 * <tr><td>{@link #setTransactionAttribute(String) transactionAttribute}</td><td>Defines transaction and isolation behaviour. Equal to <A href="http://java.sun.com/j2ee/sdk_1.2.1/techdocs/guides/ejb/html/Transaction2.html#10494">EJB transaction attribute</a>. Possible values are: 
 *   <table border="1">
 *   <tr><th>transactionAttribute</th><th>callers Transaction</th><th>Pipe excecuted in Transaction</th></tr>
 *   <tr><td colspan="1" rowspan="2">Required</td>    <td>none</td><td>T2</td></tr>
 * 											      <tr><td>T1</td>  <td>T1</td></tr>
 *   <tr><td colspan="1" rowspan="2">RequiresNew</td> <td>none</td><td>T2</td></tr>
 * 											      <tr><td>T1</td>  <td>T2</td></tr>
 *   <tr><td colspan="1" rowspan="2">Mandatory</td>   <td>none</td><td>error</td></tr>
 * 											      <tr><td>T1</td>  <td>T1</td></tr>
 *   <tr><td colspan="1" rowspan="2">NotSupported</td><td>none</td><td>none</td></tr>
 * 											      <tr><td>T1</td>  <td>none</td></tr>
 *   <tr><td colspan="1" rowspan="2">Supports</td>    <td>none</td><td>none</td></tr>
 * 											      <tr><td>T1</td>  <td>T1</td></tr>
 *   <tr><td colspan="1" rowspan="2">Never</td>       <td>none</td><td>none</td></tr>
 * 											      <tr><td>T1</td>  <td>error</td></tr>
 *  </table></td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setBeforeEvent(int) beforeEvent}</td>      <td>METT eventnumber, fired just before a message is processed by this Pipe</td><td>-1 (disabled)</td></tr>
 * <tr><td>{@link #setAfterEvent(int) afterEvent}</td>        <td>METT eventnumber, fired just after message processing by this Pipe is finished</td><td>-1 (disabled)</td></tr>
 * <tr><td>{@link #setExceptionEvent(int) exceptionEvent}</td><td>METT eventnumber, fired when message processing by this Pipe resulted in an exception</td><td>-1 (disabled)</td></tr>
 * <tr><td>{@link #setForwardName(String) forwardName}</td>  <td>name of forward returned upon completion</td><td>"success"</td></tr>
 * <tr><td>{@link #setResultOnTimeOut(String) resultOnTimeOut}</td><td>result returned when no return-message was received within the timeout limit (e.g. "receiver timed out").</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setLinkMethod(String) linkMethod}</td><td>Indicates wether the server uses the correlationID or the messageID in the correlationID field of the reply. This requirers the sender to have set the correlationID at the time of sending.</td><td>CORRELATIONID</td></tr>
 * <tr><td>{@link #setAuditTrailXPath(String) auditTrailXPath}</td><td>xpath expression to extract audit trail from message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setAuditTrailNamespaceDefs(String) auditTrailNamespaceDefs}</td><td>namespace defintions for auditTrailXPath. Must be in the form of a comma or space separated list of <code>prefix=namespaceuri</code>-definitions</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setAuditTrailSessionKey(String) auditTrailSessionKey}</td><td>Key of a PipeLineSession-variable. Is specified, the value of the PipeLineSession variable is used as audit trail (instead of the default "no audit trail")</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCorrelationIDXPath(String) correlationIDXPath}</td><td>xpath expression to extract correlationID from message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCorrelationIDNamespaceDefs(String) correlationIDNamespaceDefs}</td><td>namespace defintions for correlationIDXPath. Must be in the form of a comma or space separated list of <code>prefix=namespaceuri</code>-definitions</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCorrelationIDStyleSheet(String) correlationIDStyleSheet}</td><td>stylesheet to extract correlationID from message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCorrelationIDSessionKey(String) correlationIDSessionKey}</td><td>Key of a PipeLineSession-variable. Is specified, the value of the PipeLineSession variable is used as input for the XpathExpression or StyleSheet, instead of the current input message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setLabelXPath(String) labelXPath}</td><td>xpath expression to extract label from message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setLabelNamespaceDefs(String) labelNamespaceDefs}</td><td>namespace defintions for labelXPath. Must be in the form of a comma or space separated list of <code>prefix=namespaceuri</code>-definitions</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setLabelStyleSheet(String) labelStyleSheet}</td><td>stylesheet to extract label from message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setTimeOutOnResult(String) timeOutOnResult}</td><td>when not empty, a TimeOutException is thrown when the result equals this value (for testing purposes only)</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setExceptionOnResult(String) exceptionOnResult}</td><td>when not empty, a PipeRunException is thrown when the result equals this value (for testing purposes only)</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMaxRetries(int) maxRetries}</td><td>the number of times a processing attempt is retried after a timeout or an exception is caught or after a incorrect reply is received (see also <code>retryXPath</code>)</td><td>0</td></tr>
 * <tr><td>{@link #setRetryMinInterval(int) retryMinInterval}</td><td>The starting number of seconds waited after an unsuccessful processing attempt before another processing attempt is made. Each next retry this interval is doubled with a upper limit of <code>retryMaxInterval</code></td><td>1</td></tr>
 * <tr><td>{@link #setRetryMaxInterval(int) retryMaxInterval}</td><td>The maximum number of seconds waited after an unsuccessful processing attempt before another processing attempt is made</td><td>600</td></tr>
 * <tr><td>{@link #setRetryXPath(String) retryXPath}</td><td>xpath expression evaluated on each technical successful reply. Retry is done if condition returns true</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setRetryNamespaceDefs(String) retryNamespaceDefs}</td><td>namespace defintions for retryXPath. Must be in the form of a comma or space separated list of <code>prefix=namespaceuri</code>-definitions</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setUseInputForExtract(boolean) useInputForExtract}</td><td>when set <code>true</code>, the input of a pipe is used to extract audit trail, correlationID and label (instead of the wrapped input)</td><td>true</td></tr>
 * <tr><td><code>sender.*</td><td>any attribute of the sender instantiated by descendant classes</td><td>&nbsp;</td></tr>
 * </table>
 * <table border="1">
 * <tr><th>nested elements</th><th>description</th></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ISender sender}</td><td>specification of sender to send messages with</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ICorrelatedPullingListener listener}</td><td>specification of listener to listen to for replies</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.parameters.Parameter param}</td><td>any parameters defined on the pipe will be handed to the sender, 
 * if this is a {@link nl.nn.adapterframework.core.ISenderWithParameters ISenderWithParameters}. 
 * When a parameter with the name stubFileName is present, it will <u>not</u> be handed to the sender 
 * and it is used at runtime instead of the stubFileName specified by the attribute. A lookup of the 
 * file for this stubFileName will be done at runtime, while the file for the stubFileName specified 
 * as an attribute will be done at configuration time.</td></tr>
 * <tr><td><code>inputValidator</code></td><td>specification of Pipe to validate input messages</td></tr>
 * <tr><td><code>outputValidator</code></td><td>specification of Pipe to validate output messages</td></tr>
 * <tr><td><code>inputWrapper</code></td><td>specification of Pipe to wrap input messages (before validating)</td></tr>
 * <tr><td><code>outputWrapper</code></td><td>specification of Pipe to wrap output messages (after validating)</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ITransactionalStorage messageLog}</td><td>log of all messages sent</td></tr>
 * </table>
 * </p>
 * <p><b>Exits:</b>
 * <table border="1">
 * <tr><th>state</th><th>condition</th></tr>
 * <tr><td>"success"</td><td>default when a good message was retrieved (synchronous sender), or the message was successfully sent and no listener was specified and the sender was not synchronous</td></tr>
 * <tr><td><i>{@link #setForwardName(String) forwardName}</i></td><td>if specified, and otherwise under same condition as "success"</td></tr>
 * <tr><td>"timeout"</td><td>no data was received (timeout on listening), if the sender was synchronous or a listener was specified. If "timeout" and <code>resultOnTimeOut</code> are not specified, "exception" is used in such a case</td></tr>
 * <tr><td>"exception"</td><td>an exception was thrown by the Sender or its reply-Listener. The result passed to the next pipe is the exception that was caught.</td></tr>
 * <tr><td>"illegalResult"</td><td>the received data does not comply with <code>checkXmlWellFormed</code> or <code>checkRootTag</code>.</td></tr>
 * </table>
 * </p>
 * @author  Gerrit van Brakel
 * @version $Id$</p>
 */

public class MessageSendingPipe extends FixedForwardPipe implements HasSender, HasStatistics, EventThrowing {

	public static final String PIPE_TIMEOUT_MONITOR_EVENT = "Sender Timeout";
	public static final String PIPE_CLEAR_TIMEOUT_MONITOR_EVENT = "Sender Received Result on Time";
	public static final String PIPE_EXCEPTION_MONITOR_EVENT = "Sender Exception Caught";

	private final static String TIMEOUTFORWARD = "timeout";
	private final static String EXCEPTIONFORWARD = "exception";
	private final static String ILLEGALRESULTFORWARD = "illegalResult";
	private final static String STUBFILENAME = "stubFileName";

	public static final int MIN_RETRY_INTERVAL=1;
	public static final int MAX_RETRY_INTERVAL=600;

	private String resultOnTimeOut;
	private String linkMethod = "CORRELATIONID";

	private String stubFileName;
	private boolean checkXmlWellFormed = false;
	private String checkRootTag;
	private String auditTrailXPath;
	private String auditTrailNamespaceDefs;
	private String correlationIDXPath;
	private String correlationIDNamespaceDefs;
	private String correlationIDStyleSheet;
	private String labelXPath;
	private String labelNamespaceDefs;
	private String labelStyleSheet;
	private String timeOutOnResult;
	private String exceptionOnResult;
	private boolean useInputForExtract = true;

	private int maxRetries=0;
	private int retryMinInterval=1;
	private int retryMaxInterval=1;
	private String retryXPath;
	private String retryNamespaceDefs;

	private ISender sender = null;
	private ICorrelatedPullingListener listener = null;
	private ITransactionalStorage messageLog=null;

	private String returnString;
	private TransformerPool auditTrailTp=null;
	private String auditTrailSessionKey = null;
	private TransformerPool correlationIDTp=null;
	private String correlationIDSessionKey = null;
	private TransformerPool labelTp=null;
	private TransformerPool retryTp=null;

	public final static String INPUT_VALIDATOR_NAME_PREFIX="- ";
	public final static String INPUT_VALIDATOR_NAME_SUFFIX=": validate input";
	public final static String OUTPUT_VALIDATOR_NAME_PREFIX="- ";
	public final static String OUTPUT_VALIDATOR_NAME_SUFFIX=": validate output";
	public final static String INPUT_WRAPPER_NAME_PREFIX="- ";
	public final static String INPUT_WRAPPER_NAME_SUFFIX=": wrap input";
	public final static String OUTPUT_WRAPPER_NAME_PREFIX="- ";
	public final static String OUTPUT_WRAPPER_NAME_SUFFIX=": wrap output";
	public final static String MESSAGE_LOG_NAME_PREFIX="- ";
	public final static String MESSAGE_LOG_NAME_SUFFIX=": message log";

	private IPipe inputValidator=null;
	private IPipe outputValidator=null;
	private IPipe inputWrapper=null;
	private IPipe outputWrapper=null;
	
	private boolean timeoutPending=false;

	boolean checkMessageLog = AppConstants.getInstance().getBoolean("messageLog.check", false);

	private PipeProcessor pipeProcessor;
	private ListenerProcessor listenerProcessor;


	protected void propagateName() {
		ISender sender=getSender();
		if (sender!=null && StringUtils.isEmpty(sender.getName())) {
			sender.setName(getName() + "-sender");
		}
		ICorrelatedPullingListener listener=getListener();
		if (listener!=null && StringUtils.isEmpty(listener.getName())) {
			listener.setName(getName() + "-replylistener");
		}
	}

	public void setName(String name) {
		super.setName(name);
		propagateName();
	}

	public void addParameter(Parameter p){
		if (getSender() instanceof ISenderWithParameters && getParameterList()!=null) {
			if (p.getName().equals(STUBFILENAME)) {
				super.addParameter(p);
			} else {
				((ISenderWithParameters)getSender()).addParameter(p);
			}
		}
	}

	/**
	 * Checks whether a sender is defined for this pipe.
	 */
	public void configure() throws ConfigurationException {
		super.configure();
		if (StringUtils.isNotEmpty(getStubFileName())) {
			URL stubUrl;
			try {
				stubUrl = ClassUtils.getResourceURL(this,getStubFileName());
			} catch (Throwable e) {
				throw new ConfigurationException(getLogPrefix(null)+"got exception finding resource for stubfile ["+getStubFileName()+"]", e);
			}
			if (stubUrl==null) {
				throw new ConfigurationException(getLogPrefix(null)+"could not find resource for stubfile ["+getStubFileName()+"]");
			}
			try {
				returnString = Misc.resourceToString(stubUrl, SystemUtils.LINE_SEPARATOR);
			} catch (Throwable e) {
				throw new ConfigurationException(getLogPrefix(null)+"got exception loading stubfile ["+getStubFileName()+"] from resource ["+stubUrl.toExternalForm()+"]", e);
			}
		} else {
			propagateName();
			if (getSender() == null) {
				throw new ConfigurationException(
					getLogPrefix(null) + "no sender defined ");
			}
	
			try {
				getSender().configure();
			} catch (ConfigurationException e) {
				throw new ConfigurationException(getLogPrefix(null)+"while configuring sender",e);
			}
			if (getSender() instanceof HasPhysicalDestination) {
				log.info(getLogPrefix(null)+"has sender on "+((HasPhysicalDestination)getSender()).getPhysicalDestinationName());
			}
			if (getListener() != null) {
				if (getSender().isSynchronous()) {
					throw new ConfigurationException(
						getLogPrefix(null)
							+ "cannot have listener with synchronous sender");
				}
				try {
					getListener().configure();
				} catch (ConfigurationException e) {
					throw new ConfigurationException(getLogPrefix(null)+"while configuring listener",e);
				}
				if (getListener() instanceof HasPhysicalDestination) {
					log.info(getLogPrefix(null)+"has listener on "+((HasPhysicalDestination)getListener()).getPhysicalDestinationName());
				}
			}
			if (!(getLinkMethod().equalsIgnoreCase("MESSAGEID"))
				&& (!(getLinkMethod().equalsIgnoreCase("CORRELATIONID")))) {
				throw new ConfigurationException(getLogPrefix(null)+
					"Invalid argument for property LinkMethod ["+getLinkMethod()+ "]. it should be either MESSAGEID or CORRELATIONID");
			}	

			if (isCheckXmlWellFormed() || StringUtils.isNotEmpty(getCheckRootTag())) {
				if (findForward(ILLEGALRESULTFORWARD) == null)
					throw new ConfigurationException(getLogPrefix(null) + "has no forward with name [illegalResult]");
			}
			if (!ConfigurationUtils.stubConfiguration()) {
				if (StringUtils.isNotEmpty(getTimeOutOnResult())) {
					throw new ConfigurationException(getLogPrefix(null)+"timeOutOnResult only allowed in stub mode");
				}
				if (StringUtils.isNotEmpty(getExceptionOnResult())) {
					throw new ConfigurationException(getLogPrefix(null)+"exceptionOnResult only allowed in stub mode");
				}
			}			
			if (getMaxRetries()>0) {
				ConfigurationWarnings configWarnings = ConfigurationWarnings.getInstance();
				if (getRetryMinInterval() < MIN_RETRY_INTERVAL) {
					String msg = "retryMinInterval ["+getRetryMinInterval()+"] should be greater than or equal to ["+MIN_RETRY_INTERVAL+"], assuming the lower limit";
					configWarnings.add(log, msg);
					setRetryMinInterval(MIN_RETRY_INTERVAL);
				}
				if (getRetryMaxInterval() > MAX_RETRY_INTERVAL) {
					String msg = "retryMaxInterval ["+getRetryMaxInterval()+"] should be less than or equal to ["+MAX_RETRY_INTERVAL+"], assuming the upper limit";
					configWarnings.add(log, msg);
					setRetryMaxInterval(MAX_RETRY_INTERVAL);
				}
				if (getRetryMaxInterval() < getRetryMinInterval()) {
					String msg = "retryMaxInterval ["+getRetryMaxInterval()+"] should be greater than or equal to ["+getRetryMinInterval()+"], assuming the lower limit";
					configWarnings.add(log, msg);
					setRetryMaxInterval(getRetryMinInterval());
				}
			}
		}
		ITransactionalStorage messageLog = getMessageLog();
		if (checkMessageLog) {
			if (!getSender().isSynchronous() && getListener()==null && !(getSender() instanceof nl.nn.adapterframework.senders.IbisLocalSender)) {
				if (messageLog==null) {
					ConfigurationWarnings configWarnings = ConfigurationWarnings.getInstance();
					String msg = "asynchronous sender [" + getSender().getName() + "] without sibling listener has no messageLog. Integrity check not possible";
					configWarnings.add(log, msg);
				}
			}
		}
		if (messageLog!=null) {
			messageLog.configure();
			if (StringUtils.isNotEmpty(getAuditTrailXPath())) {
				auditTrailTp = TransformerPool.configureTransformer(getLogPrefix(null),getAuditTrailNamespaceDefs(), getAuditTrailXPath(), null,"text",false,null);
			}
			if (StringUtils.isNotEmpty(getCorrelationIDXPath()) || StringUtils.isNotEmpty(getCorrelationIDStyleSheet())) {
				correlationIDTp=TransformerPool.configureTransformer(getLogPrefix(null),getCorrelationIDNamespaceDefs(), getCorrelationIDXPath(), getCorrelationIDStyleSheet(),"text",false,null);
			}
			if (StringUtils.isNotEmpty(getLabelXPath()) || StringUtils.isNotEmpty(getLabelStyleSheet())) {
				labelTp=TransformerPool.configureTransformer(getLogPrefix(null),getLabelNamespaceDefs(), getLabelXPath(), getLabelStyleSheet(),"text",false,null);
			}
		}
		if (StringUtils.isNotEmpty(getRetryXPath())) {
			retryTp = TransformerPool.configureTransformer(getLogPrefix(null),getRetryNamespaceDefs(), getRetryXPath(), null,"text",false,null);
		}
		if (getInputValidator()!=null) {
			PipeForward pf = new PipeForward();
			pf.setName("success");
			getInputValidator().registerForward(pf);
			if (getInputValidator() instanceof IExtendedPipe) {
				((IExtendedPipe)getInputValidator()).configure(getPipeLine());
			} else {
				getInputValidator().configure();
			}
		}
		if (getOutputValidator()!=null) {
			PipeForward pf = new PipeForward();
			pf.setName("success");
			getOutputValidator().registerForward(pf);
			if (getOutputValidator() instanceof IExtendedPipe) {
				((IExtendedPipe)getOutputValidator()).configure(getPipeLine());
			} else {
				getOutputValidator().configure();
			}
		}
		if (getInputWrapper()!=null) {
			PipeForward pf = new PipeForward();
			pf.setName("success");
			getInputWrapper().registerForward(pf);
			if (getInputWrapper() instanceof IExtendedPipe) {
				((IExtendedPipe)getInputWrapper()).configure(getPipeLine());
			} else {
				getInputWrapper().configure();
			}
		}
		if (getOutputWrapper()!=null) {
			PipeForward pf = new PipeForward();
			pf.setName("success");
			getOutputWrapper().registerForward(pf);
			if (getOutputWrapper() instanceof IExtendedPipe) {
				((IExtendedPipe)getOutputWrapper()).configure(getPipeLine());
			} else {
				getOutputWrapper().configure();
			}
		}

		registerEvent(PIPE_TIMEOUT_MONITOR_EVENT);
		registerEvent(PIPE_CLEAR_TIMEOUT_MONITOR_EVENT);
		registerEvent(PIPE_EXCEPTION_MONITOR_EVENT);
	}

	
	public PipeRunResult doPipe(Object input, IPipeLineSession session)	throws PipeRunException {
		String originalMessage = input.toString();
		String result = null;
		String correlationID = session.getMessageId();

		if (getInputWrapper()!=null) {
			log.debug(getLogPrefix(session)+"wrapping input");
			PipeRunResult wrapResult = pipeProcessor.processPipe(getPipeLine(), inputWrapper, correlationID, input, session);
			if (wrapResult!=null && !wrapResult.getPipeForward().getName().equals("success")) {
				return wrapResult;
			} else {
				input = wrapResult.getResult();
			}
			log.debug(getLogPrefix(session)+"input after wrapping [" + input.toString() + "]");
		}

		if (getInputValidator()!=null) {
			log.debug(getLogPrefix(session)+"validating input");
			PipeRunResult validationResult = pipeProcessor.processPipe(getPipeLine(), inputValidator, correlationID, input, session);
			if (validationResult!=null && !validationResult.getPipeForward().getName().equals("success")) {
				return validationResult;
			}
		}

		if (StringUtils.isNotEmpty(getStubFileName())) {
			ParameterList pl = getParameterList();
			result=returnString;
			if (pl != null) {
				ParameterResolutionContext prc = new ParameterResolutionContext((String)input, session);
				Map params;
				try {
					params = prc.getValueMap(pl);
				} catch (ParameterException e1) {
					throw new PipeRunException(this,getLogPrefix(session)+"got exception evaluating parameters",e1);
				}
				String sfn = null;
				if (params != null && params.size() > 0) {
					sfn = (String)params.get(STUBFILENAME);
				}
				if (sfn != null) {
					try {
						result = Misc.resourceToString(ClassUtils.getResourceURL(this,sfn), SystemUtils.LINE_SEPARATOR);
						log.info(getLogPrefix(session)+"returning result from dynamic stub ["+sfn+"]");
					} catch (Throwable e) {
						throw new PipeRunException(this,getLogPrefix(session)+"got exception loading result from stub [" + sfn + "]",e);
					}
				} else {
					log.info(getLogPrefix(session)+"returning result from static stub ["+getStubFileName()+"]");
				}
			} else {
				log.info(getLogPrefix(session)+"returning result from static stub ["+getStubFileName()+"]");
			}
		} else {
			Map threadContext=new HashMap();
			try {
				String messageID = null;
				// sendResult has a messageID for async senders, the result for sync senders
				int retryInterval = getRetryMinInterval();
				String sendResult = null;
				boolean replyIsValid = false;
				int retriesLeft = 0;
				if (getMaxRetries()>0) {
					retriesLeft = getMaxRetries() + 1;
				} else {
					retriesLeft = 1;
				}
				while (retriesLeft-->=1 && !replyIsValid) {
					try {
						sendResult = sendMessage(input, session, correlationID, getSender(), threadContext);
						if (retryTp!=null) {
							String retry=retryTp.transform(sendResult,null);
							if (retry.equalsIgnoreCase("true")) {
								if (retriesLeft>=1) {
									retryInterval = increaseRetryIntervalAndWait(session, retryInterval, "xpathRetry result ["+retry+"], retries left [" + retriesLeft + "]");
								}
							} else {
								replyIsValid = true;
							} 
						} else {
							replyIsValid = true;
						}
					} catch (TimeOutException toe) {
						if (retriesLeft>=1) {
							retryInterval = increaseRetryIntervalAndWait(session, retryInterval, "timeout occured, retries left [" + retriesLeft + "]");
						} else {
							throw toe;
						}
					} catch (SenderException se) {
						if (retriesLeft>=1) {
							retryInterval = increaseRetryIntervalAndWait(session, retryInterval, "exception ["+(se!=null?se.getMessage():"")+"] occured, retries left [" + retriesLeft + "]");
						} else {
							throw se;
						}
					}
				}

				if (!replyIsValid){
					throw new PipeRunException(this, getLogPrefix(session)+"invalid reply message is received");
				}
	
				if (getSender().isSynchronous()) {
					if (log.isInfoEnabled()) {
						log.info(getLogPrefix(session)+ "sent message to ["+ getSender().getName()+ "] synchronously");
					}
					result = sendResult;
				} else {
					messageID = sendResult;
					if (log.isInfoEnabled()) {
						log.info(getLogPrefix(session) + "sent message to [" + getSender().getName()+ "] messageID ["+ messageID+ "] correlationID ["+ correlationID+ "] linkMethod ["+ getLinkMethod()	+ "]");
					}
					// if linkMethod is MESSAGEID overwrite correlationID with the messageID
					// as this will be used with the listener
					if (getLinkMethod().equalsIgnoreCase("MESSAGEID")) {
						correlationID = sendResult;
						if (log.isDebugEnabled()) log.debug(getLogPrefix(session)+"setting correlationId to listen for to messageId ["+correlationID+"]");
					}
				}

				ITransactionalStorage messageLog = getMessageLog();
				if (messageLog!=null) {
					long messageLogStartTime= System.currentTimeMillis();
					String messageTrail="no audit trail";
					if (auditTrailTp!=null) {
						if (isUseInputForExtract()){
							messageTrail=auditTrailTp.transform(originalMessage,null);
						} else {
							messageTrail=auditTrailTp.transform((String)input,null);
						}
					} else {
						if (StringUtils.isNotEmpty(getAuditTrailSessionKey())) {
							messageTrail = (String)(session.get(getAuditTrailSessionKey()));
						}
					}
					String storedMessageID=messageID;
					if (storedMessageID==null) {
						storedMessageID="-";
					}
					if (correlationIDTp!=null) {
						if (StringUtils.isNotEmpty(getCorrelationIDSessionKey())) {
							String sourceString = (String)(session.get(getCorrelationIDSessionKey()));
							correlationID=correlationIDTp.transform(sourceString,null);
						} else {
							if (isUseInputForExtract()) {
								correlationID=correlationIDTp.transform(originalMessage,null);
							} else {
								correlationID=correlationIDTp.transform((String)input,null);
							}
						}
						if (StringUtils.isEmpty(correlationID)) {
							correlationID="-";
						}
					}
					String label=null;
					if (labelTp!=null) {
						if (isUseInputForExtract()) {
							label=labelTp.transform(originalMessage,null);
						} else {
							label=labelTp.transform((String)input,null);
						}
					}
					if (sender instanceof MailSender) {
						String messageInMailSafeForm = (String)session.get("messageInMailSafeForm");
						messageLog.storeMessage(storedMessageID,correlationID,new Date(),messageTrail,label,messageInMailSafeForm);
					} else {
						messageLog.storeMessage(storedMessageID,correlationID,new Date(),messageTrail,label,(String)input);
					}
					long messageLogEndTime = System.currentTimeMillis();
					long messageLogDuration = messageLogEndTime - messageLogStartTime;
					StatisticsKeeper sk = getPipeLine().getPipeStatistics(messageLog);
					sk.addValue(messageLogDuration);
				}

				if (sender instanceof MailSender) {
					session.remove("messageInMailSafeForm");
				}
				
				if (getListener() != null) {
					result = listenerProcessor.getMessage(getListener(), correlationID, session);
					} else {
					result = sendResult;
				}
				if (result == null) {
					result = "";
				}
				if (timeoutPending) {
					timeoutPending=false;
					throwEvent(PIPE_CLEAR_TIMEOUT_MONITOR_EVENT);
				}
		
			} catch (TimeOutException toe) {
				throwEvent(PIPE_TIMEOUT_MONITOR_EVENT);
				if (!timeoutPending) {
					timeoutPending=true;
				}
				PipeForward timeoutForward = findForward(TIMEOUTFORWARD);
				log.warn(getLogPrefix(session) + "timeout occured");
				if (timeoutForward==null) {
					if (StringUtils.isEmpty(getResultOnTimeOut())) {
						timeoutForward=findForward(EXCEPTIONFORWARD);
					} else {
						timeoutForward=getForward();
					}
				}
				if (timeoutForward!=null) {
					String resultmsg;
					if (StringUtils.isNotEmpty(getResultOnTimeOut())) {
						resultmsg =getResultOnTimeOut();
					} else {
						if (input instanceof String) {
							resultmsg=new ErrorMessageFormatter().format(getLogPrefix(session),toe,this,(String)input,session.getMessageId(),0);
						} else {
							if (input==null) {
								input="null";
							}
							resultmsg=new ErrorMessageFormatter().format(getLogPrefix(session),toe,this,input.toString(),session.getMessageId(),0);
						}
					}
					return new PipeRunResult(timeoutForward,resultmsg);
				}
				throw new PipeRunException(this, getLogPrefix(session) + "caught timeout-exception", toe);
	
			} catch (Throwable t) {
				throwEvent(PIPE_EXCEPTION_MONITOR_EVENT);
				PipeForward exceptionForward = findForward(EXCEPTIONFORWARD);
				if (exceptionForward!=null) {
					log.warn(getLogPrefix(session) + "exception occured, forwarding to exception-forward ["+exceptionForward.getPath()+"], exception:\n", t);
					String resultmsg;
					if (input instanceof String) {
						resultmsg=new ErrorMessageFormatter().format(getLogPrefix(session),t,this,(String)input,session.getMessageId(),0);
					} else {
						if (input==null) {
							input="null";
						}
						resultmsg=new ErrorMessageFormatter().format(getLogPrefix(session),t,this,input.toString(),session.getMessageId(),0);
					}
					return new PipeRunResult(exceptionForward,resultmsg);
				}
				throw new PipeRunException(this, getLogPrefix(session) + "caught exception", t);
					}
			}
		if (!validResult(result)) {
			PipeForward illegalResultForward = findForward(ILLEGALRESULTFORWARD);
			return new PipeRunResult(illegalResultForward, result);
		}
		if (getOutputValidator()!=null) {
			log.debug(getLogPrefix(session)+"validating response");
			long validationStartTime= System.currentTimeMillis();
			PipeRunResult validationResult = pipeProcessor.processPipe(getPipeLine(), outputValidator, correlationID, result,session);
			if (validationResult!=null && !validationResult.getPipeForward().getName().equals("success")) {
				return validationResult;
			}
		}
		if (getOutputWrapper()!=null) {
			log.debug(getLogPrefix(session)+"wrapping response");
			PipeRunResult wrapResult = pipeProcessor.processPipe(getPipeLine(), outputWrapper, correlationID, result, session);
			if (wrapResult!=null && !wrapResult.getPipeForward().getName().equals("success")) {
				return wrapResult;
			} else {
				result = wrapResult.getResult().toString();
			}
			log.debug(getLogPrefix(session)+"response after wrapping [" + result + "]");
		}
		return new PipeRunResult(getForward(), result);
	}

	private boolean validResult(String result) {
		boolean validResult = true;
		if (isCheckXmlWellFormed()  || StringUtils.isNotEmpty(getCheckRootTag())) {
			if (!XmlUtils.isWellFormed(result, getCheckRootTag())) {
				validResult = false;
			}
		}
		return validResult;
	}

	protected String sendMessage(Object input, IPipeLineSession session, String correlationID, ISender sender, Map threadContext) throws SenderException, TimeOutException, InterruptedException {
		String sendResult = sendTextMessage(input, session, correlationID, getSender(), threadContext);
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}
		if (StringUtils.isNotEmpty(getTimeOutOnResult()) && getTimeOutOnResult().equals(sendResult)) {
			throw new TimeOutException(getLogPrefix(session)+"timeOutOnResult ["+getTimeOutOnResult()+"]");
		}
		if (StringUtils.isNotEmpty(getExceptionOnResult()) && getExceptionOnResult().equals(sendResult)) {
			throw new SenderException(getLogPrefix(session)+"exceptionOnResult ["+getExceptionOnResult()+"]");
		}
		return sendResult;
	}
	
	protected String sendTextMessage(Object input, IPipeLineSession session, String correlationID, ISender sender, Map threadContext) throws SenderException, TimeOutException {
		if (input!=null && !(input instanceof String)) {
			throw new SenderException("String expected, got a [" + input.getClass().getName() + "]");
		}
		// sendResult has a messageID for async senders, the result for sync senders
		if (sender instanceof ISenderWithParameters) { // do not only check own parameters, sender may have them by itself
			ISenderWithParameters psender = (ISenderWithParameters) sender;
			ParameterResolutionContext prc = new ParameterResolutionContext((String)input, session, isNamespaceAware());
			return psender.sendMessage(correlationID, (String) input, prc);
		} 
		return sender.sendMessage(correlationID, (String) input);
	}

	public int increaseRetryIntervalAndWait(IPipeLineSession session, int retryInterval, String description) throws InterruptedException {
		long currentInterval;
		synchronized (this) {
			if (retryInterval < getRetryMinInterval()) {
				retryInterval = getRetryMinInterval();
			}
			if (retryInterval > getRetryMaxInterval()) {
				retryInterval = getRetryMaxInterval();
			}
			currentInterval = retryInterval;
			retryInterval = retryInterval * 2;
		}
		log.warn(getLogPrefix(session)+description+", starts waiting for [" + currentInterval + "] seconds");
		while (currentInterval-->0) {
			Thread.sleep(1000);
		}
		return retryInterval;
	}

	public void start() throws PipeStartException {
		if (StringUtils.isEmpty(getStubFileName())) {
			try {
				getSender().open();
				if (getListener() != null) {
					getListener().open();
				}
	
			} catch (Throwable t) {
				PipeStartException pse = new PipeStartException(getLogPrefix(null)+"could not start", t);
				pse.setPipeNameInError(getName());
				throw pse;
			}
		}
		ITransactionalStorage messageLog = getMessageLog();
		if (messageLog!=null) {
			try {
				messageLog.open();
			} catch (Exception e) {
				PipeStartException pse = new PipeStartException(getLogPrefix(null)+"could not open messagelog", e);
				pse.setPipeNameInError(getName());
				throw pse;
			}
		}
	}
	public void stop() {
		if (StringUtils.isEmpty(getStubFileName())) {
			log.info(getLogPrefix(null) + "is closing");
			try {
				getSender().close();
			} catch (SenderException e) {
				log.warn(getLogPrefix(null) + "exception closing sender", e);
			}
			if (getListener() != null) {
				try {
					log.info(getLogPrefix(null) + "is closing; closing listener");
					getListener().close();
				} catch (ListenerException e) {
					log.warn(getLogPrefix(null) + "Exception closing listener", e);
				}
			}
		}
		ITransactionalStorage messageLog = getMessageLog();
		if (messageLog!=null) {
			try {
				messageLog.close();
			} catch (Exception e) {
				log.warn(getLogPrefix(null) + "Exception closing messageLog", e);
			}
		}
	}

	public void iterateOverStatistics(StatisticsKeeperIterationHandler hski, Object data, int action) throws SenderException {
		if (sender instanceof HasStatistics) {
			((HasStatistics)sender).iterateOverStatistics(hski,data,action);
		}
	}

	/**
	 * Register a {@link ICorrelatedPullingListener} at this Pipe
	 */
	protected void setListener(ICorrelatedPullingListener listener) {
		this.listener = listener;
		log.debug(
			"pipe ["
				+ getName()
				+ "] registered listener ["
				+ listener.toString()
				+ "]");
	}
	public ICorrelatedPullingListener getListener() {
		return listener;
	}

	/**
	 * Sets the messageLog.
	 */
	protected void setMessageLog(ITransactionalStorage messageLog) {
		if (messageLog.isActive()) {
			this.messageLog = messageLog;
			messageLog.setName(MESSAGE_LOG_NAME_PREFIX+getName()+MESSAGE_LOG_NAME_SUFFIX);
			if (StringUtils.isEmpty(messageLog.getSlotId())) {
				messageLog.setSlotId(getName());
			}
			messageLog.setType(JdbcTransactionalStorage.TYPE_MESSAGELOG_PIPE);
		}
	}
	public ITransactionalStorage getMessageLog() {
		return messageLog;
	}

 

	/**
	 * Register a ISender at this Pipe
	 * @see ISender
	 */
	protected void setSender(ISender sender) {
		this.sender = sender;
		log.debug(
			"pipe ["
				+ getName()
				+ "] registered sender ["
				+ sender.getName()
				+ "] with properties ["
				+ sender.toString()
				+ "]");
	}
	public ISender getSender() {
		return sender;
	}
	
	/**
	 * The message that is returned when the time listening for a reply message
	 * exceeds the timeout, or in other situations no reply message is received.
	 */
	public void setResultOnTimeOut(String newResultOnTimeOut) {
		resultOnTimeOut = newResultOnTimeOut;
	}
	public String getResultOnTimeOut() {
		return resultOnTimeOut;
	}

	/**
	 * For asynchronous communication, the server side may either use the messageID or the correlationID
	 * in the correlationID field of the reply message. Use this property to set the behaviour of the reply-listener.
	 * <ul>
	 * <li>Use <code>MESSAGEID</code> to let the listener wait for a message with the messageID of the
	 * sent message in the correlation ID field</li>
	 * <li>Use <code>CORRELATIONID</code> to let the listener wait for a message with the correlationID of the
	 * sent message in the correlation ID field</li>
	 * </ul>
	 * When you use the method CORRELATIONID you have the advantage that you can trace your request
	 * as the messageID as it is known in the Adapter is used as the correlationID. In the logging you should be able
	 * to follow the message more clearly. When you use the method MESSAGEID, the messageID (unique for every
	 * message) will be expected in the correlationID field of the returned message.
	 * 
	 * @param method either MESSAGEID or CORRELATIONID
	 */
	public void setLinkMethod(String method) {
		linkMethod = method;
	}
	public String getLinkMethod() {
		return linkMethod;
	}

	public void setStubFileName(String fileName) {
		stubFileName = fileName;
	}
	public String getStubFileName() {
		return stubFileName;
	}

	public void setCheckXmlWellFormed(boolean b) {
		checkXmlWellFormed = b;
	}
	public boolean isCheckXmlWellFormed() {
		return checkXmlWellFormed;
	}

	public void setCheckRootTag(String s) {
		checkRootTag = s;
	}
	public String getCheckRootTag() {
		return checkRootTag;
	}

	public void setAuditTrailXPath(String string) {
		auditTrailXPath = string;
	}
	public String getAuditTrailXPath() {
		return auditTrailXPath;
	}

	public void setCorrelationIDXPath(String string) {
		correlationIDXPath = string;
	}
	public String getCorrelationIDXPath() {
		return correlationIDXPath;
	}

	public void setCorrelationIDStyleSheet(String string) {
		correlationIDStyleSheet = string;
	}
	public String getCorrelationIDStyleSheet() {
		return correlationIDStyleSheet;
	}

	public void setLabelXPath(String string) {
		labelXPath = string;
	}
	public String getLabelXPath() {
		return labelXPath;
	}

	public void setLabelStyleSheet(String string) {
		labelStyleSheet = string;
	}
	public String getLabelStyleSheet() {
		return labelStyleSheet;
	}
	
	public void setInputValidator(IPipe inputValidator) {
//		if (inputValidator.isActive()) {
			inputValidator.setName(INPUT_VALIDATOR_NAME_PREFIX+getName()+INPUT_VALIDATOR_NAME_SUFFIX);
			this.inputValidator = inputValidator;
//		}
	}
	public IPipe getInputValidator() {
		return inputValidator;
	}

	public void setOutputValidator(IPipe outputValidator) {
//		if (outputValidator.isActive()) {
			outputValidator.setName(OUTPUT_VALIDATOR_NAME_PREFIX+getName()+OUTPUT_VALIDATOR_NAME_SUFFIX);
			this.outputValidator = outputValidator;
//		}
	}
	public IPipe getOutputValidator() {
		return outputValidator;
	}

	public void setInputWrapper(IPipe inputWrapper) {
		inputWrapper.setName(INPUT_WRAPPER_NAME_PREFIX+getName()+INPUT_WRAPPER_NAME_SUFFIX);
		this.inputWrapper = inputWrapper;
	}
	public IPipe getInputWrapper() {
		return inputWrapper;
	}

	public void setOutputWrapper(IPipe outputWrapper) {
		outputWrapper.setName(OUTPUT_WRAPPER_NAME_PREFIX+getName()+OUTPUT_WRAPPER_NAME_SUFFIX);
		this.outputWrapper = outputWrapper;
	}
	public IPipe getOutputWrapper() {
		return outputWrapper;
	}

	public void setCorrelationIDSessionKey(String string) {
		correlationIDSessionKey = string;
	}

	public String getCorrelationIDSessionKey() {
		return correlationIDSessionKey;
	}

	public String getAuditTrailNamespaceDefs() {
		return auditTrailNamespaceDefs;
	}
	public void setAuditTrailNamespaceDefs(String auditTrailNamespaceDefs) {
		this.auditTrailNamespaceDefs = auditTrailNamespaceDefs;
	}

	public String getCorrelationIDNamespaceDefs() {
		return correlationIDNamespaceDefs;
	}
	public void setCorrelationIDNamespaceDefs(String correlationIDNamespaceDefs) {
		this.correlationIDNamespaceDefs = correlationIDNamespaceDefs;
	}

	public String getLabelNamespaceDefs() {
		return labelNamespaceDefs;
	}
	public void setLabelNamespaceDefs(String labelXNamespaceDefs) {
		this.labelNamespaceDefs = labelXNamespaceDefs;
	}
	
	public void setTimeOutOnResult(String string) {
		timeOutOnResult = string;
	}
	public String getTimeOutOnResult() {
		return timeOutOnResult;
	}

	public void setExceptionOnResult(String string) {
		exceptionOnResult = string;
	}
	public String getExceptionOnResult() {
		return exceptionOnResult;
	}

	public void setPipeProcessor(PipeProcessor pipeProcessor) {
		this.pipeProcessor = pipeProcessor;
	}

	public void setListenerProcessor(ListenerProcessor listenerProcessor) {
		this.listenerProcessor = listenerProcessor;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int i) {
		maxRetries = i;
	}

	public int getRetryMinInterval() {
		return retryMinInterval;
	}

	public void setRetryMinInterval(int i) {
		retryMinInterval = i;
	}

	public int getRetryMaxInterval() {
		return retryMaxInterval;
	}

	public void setRetryMaxInterval(int i) {
		retryMaxInterval = i;
	}

	public void setRetryXPath(String string) {
		retryXPath = string;
	}

	public String getRetryXPath() {
		return retryXPath;
	}

	public String getRetryNamespaceDefs() {
		return retryNamespaceDefs;
	}

	public void setRetryNamespaceDefs(String retryNamespaceDefs) {
		this.retryNamespaceDefs = retryNamespaceDefs;
	}

	public boolean hasSizeStatistics() {
		return getSender().isSynchronous();
	}

	public void setAuditTrailSessionKey(String string) {
		auditTrailSessionKey = string;
	}

	public String getAuditTrailSessionKey() {
		return auditTrailSessionKey;
	}

	public void setUseInputForExtract(boolean b) {
		useInputForExtract = b;
	}
	public boolean isUseInputForExtract() {
		return useInputForExtract;
	}
}