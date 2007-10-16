/*
 * $Log: ReceiverBase.java,v $
 * Revision 1.54  2007-10-16 12:40:36  europe\L190409
 * moved code to ReceiverBaseClassic
 *
 * Revision 1.53  2007/10/10 08:53:00  gerrit
 * transactions from JtaUtil
 * make runState externally accessible
 *
 * Revision 1.52  2007/10/08 13:33:31  gerrit
 * changed ArrayList to List where possible
 *
 * Revision 1.51  2007/10/04 12:01:37  gerrit
 * limit number of error messages written to log
 *
 * Revision 1.50  2007/10/03 08:57:04  gerrit
 * changed HashMap to Map
 *
 * Revision 1.49  2007/09/27 12:55:42  gerrit
 * introduction of monitoring
 *
 * Revision 1.48  2007/09/25 11:34:02  gerrit
 * added deprecation warning for ibi42compatibility
 *
 * Revision 1.47  2007/09/24 13:05:41  gerrit
 * fixed bug in close of errorStorage
 *
 * Revision 1.46  2007/09/12 09:27:06  gerrit
 * added attribute pollInterval
 *
 * Revision 1.45  2007/09/05 13:05:02  gerrit
 * moved copying of context to Misc
 *
 * Revision 1.44  2007/08/27 11:51:43  gerrit
 * modified afterMessageProcessed handling
 * added attribute 'returnedSessionKeys'
 *
 * Revision 1.43  2007/08/10 11:21:49  gerrit
 * catch more exceptions
 *
 * Revision 1.42  2007/06/26 12:06:08  gerrit
 * tuned logging
 *
 * Revision 1.41  2007/06/26 06:56:59  gerrit
 * set inProcessStorage type to 'E' if combined with errorStorage
 *
 * Revision 1.40  2007/06/21 07:07:06  gerrit
 * removed warnings about not transacted=true
 *
 * Revision 1.39  2007/06/19 12:07:32  gerrit
 * modifiy retryinterval handling
 *
 * Revision 1.38  2007/06/14 08:49:35  gerrit
 * catch less specific types of exception
 *
 * Revision 1.37  2007/06/12 11:24:04  gerrit
 * corrected typeSettings of transactional storages
 *
 * Revision 1.36  2007/06/08 12:49:03  gerrit
 * updated javadoc
 *
 * Revision 1.35  2007/06/08 12:17:40  gerrit
 * improved error handling
 * introduced retry mechanisme with increasing wait interval
 *
 * Revision 1.34  2007/06/08 07:49:13  gerrit
 * changed error to warning
 *
 * Revision 1.33  2007/06/07 15:22:44  gerrit
 * made stopping after receiving an exception configurable
 *
 * Revision 1.32  2007/05/23 09:25:17  gerrit
 * added support for attribute 'active' on transactional storages
 *
 * Revision 1.31  2007/05/21 12:22:47  gerrit
 * added setMessageLog()
 *
 * Revision 1.30  2007/05/02 11:37:51  gerrit
 * added attribute 'active'
 *
 * Revision 1.29  2007/02/12 14:03:45  gerrit
 * Logger from LogUtil
 *
 * Revision 1.28  2007/02/05 15:01:44  gerrit
 * configure inProcessStorage when it is present, not only when transacted
 *
 * Revision 1.27  2006/12/13 16:30:41  gerrit
 * added maxRetries to configuration javadoc
 *
 * Revision 1.26  2006/08/24 07:12:42  gerrit
 * documented METT tracing event numbers
 *
 * Revision 1.25  2006/06/20 14:10:43  gerrit
 * added stylesheet attribute
 *
 * Revision 1.24  2006/04/12 16:17:43  gerrit
 * retry after failed storing of message in inProcessStorage
 *
 * Revision 1.23  2006/02/20 15:42:41  gerrit
 * moved METT-support to single entry point for tracing
 *
 * Revision 1.22  2006/02/09 07:57:47  gerrit
 * METT tracing support
 *
 * Revision 1.21  2005/10/27 08:46:45  gerrit
 * introduced RunStateEnquiries
 *
 * Revision 1.20  2005/10/26 08:52:31  gerrit
 * allow for transacted="true" without inProcessStorage, (ohne Gewähr!)
 *
 * Revision 1.19  2005/10/17 11:29:24  gerrit
 * fixed nullpointerexception in startRunning
 *
 * Revision 1.18  2005/09/26 11:42:10  gerrit
 * added fileNameIfStopped attribute and replace from/to processing when stopped
 *
 * Revision 1.17  2005/09/13 15:42:14  gerrit
 * improved handling of non-serializable messages like Poison-messages
 *
 * Revision 1.16  2005/08/08 09:44:11  gerrit
 * start transactions if needed and not already started
 *
 * Revision 1.15  2005/07/19 15:27:14  gerrit
 * modified closing procedure
 * added errorStorage
 * modified implementation of transactionalStorage
 * allowed exceptions to bubble up
 * assume rawmessages to be serializable for transacted processing
 * added ibis42compatibility attribute, avoiding exception bubbling
 *
 * Revision 1.14  2005/07/05 12:54:38  gerrit
 * allow to set parameters from context for processRequest() methods
 *
 * Revision 1.13  2005/06/02 11:52:24  gerrit
 * limited number of actively polling threads to value of attriubte numThreadsPolling
 *
 * Revision 1.12  2005/04/13 12:53:09  gerrit
 * removed unused imports
 *
 * Revision 1.11  2005/03/31 08:22:49  gerrit
 * fixed bug in getIdleStatistics
 *
 * Revision 1.10  2005/03/07 11:04:36  johan
 * PipeLineSession became a extension of HashMap, using other iterator
 *
 * Revision 1.9  2005/03/04 08:53:29  johan
 * Fixed IndexOutOfBoundException in getProcessStatistics  due to multi threading.
 * Adjusted this too for getIdleStatistics
 *
 * Revision 1.8  2005/02/10 08:17:34  gerrit
 * included context dump in debug
 *
 * Revision 1.7  2005/01/13 08:56:04  gerrit
 * Make threadContext-attributes available in PipeLineSession
 *
 * Revision 1.6  2004/10/12 15:14:11  gerrit
 * removed unused code
 *
 * Revision 1.5  2004/08/25 09:11:33  unknown0
 * Add waitForRunstate with timeout
 *
 * Revision 1.4  2004/08/23 13:10:48  gerrit
 * updated JavaDoc
 *
 * Revision 1.3  2004/08/16 14:09:58  unknown0
 * Return returnIfStopped value in case adapter is stopped
 *
 * Revision 1.2  2004/08/09 13:46:52  gerrit
 * various changes
 *
 * Revision 1.1  2004/08/03 13:04:30  gerrit
 * introduction of GenericReceiver
 *
 */
package nl.nn.adapterframework.receivers;

import nl.nn.adapterframework.core.HasSender;
import nl.nn.adapterframework.core.IMessageHandler;
import nl.nn.adapterframework.core.IReceiver;
import nl.nn.adapterframework.core.IReceiverStatistics;
import nl.nn.adapterframework.core.IbisExceptionListener;
import nl.nn.adapterframework.util.TracingEventNumbers;


/**
 * This {@link IReceiver Receiver} may be used as a base-class for developing receivers.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>name of the class, mostly a class that extends this class</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td>  <td>name of the receiver as known to the adapter</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setActive(boolean) active}</td>  <td>when set <code>false</code> or set to something else as "true", (even set to the empty string), the receiver is not included in the configuration</td><td>true</td></tr>
 * <tr><td>{@link #setNumThreads(int) numThreads}</td><td>the number of threads that may execute a pipeline concurrently (only for pulling listeners)</td><td>1</td></tr>
 * <tr><td>{@link #setNumThreadsPolling(int) numThreadsPolling}</td><td>the number of threads that are activily polling for messages concurrently. '0' means 'limited only by <code>numThreads</code>' (only for pulling listeners)</td><td>1</td></tr>
 * <tr><td>{@link #setStyleSheetName(String) styleSheetName}</td>  <td></td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setOnError(String) onError}</td><td>one of 'continue' or 'close'. Controls the behaviour of the receiver when it encounters an error sending a reply or receives an exception asynchronously</td><td>continue</td></tr>
 * <tr><td>{@link #setReturnedSessionKeys(String) returnedSessionKeys}</td><td>comma separated list of keys of session variables that should be returned to caller, for correct results as well as for erronous results. (Only for listeners that support it, like JavaListener)</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setTransacted(boolean) transacted}</td><td>if set to <code>true</code>, messages will be received and processed under transaction control. If processing fails, messages will be sent to the error-sender. (see below)</code></td><td><code>false</code></td></tr>
 * <tr><td>{@link #setMaxRetries(int) maxRetries}</td><td>The number of times a pulling listening attempt is retried after an exception is caught</td><td>3</td></tr>
 * <tr><td>{@link #setPollInterval(int) pollInterval}</td><td>The number of seconds waited after an unsuccesful poll attempt before another poll attempt is made.</td><td>0</td></tr>
 * <tr><td>{@link #setIbis42compatibility(boolean) ibis42compatibility}</td><td>if set to <code>true</code>, the result of a failed processing of a message is a formatted errormessage. Otherwise a listener specific error handling is performed</code></td><td><code>false</code></td></tr>
 * <tr><td>{@link #setBeforeEvent(int) beforeEvent}</td>      <td>METT eventnumber, fired just before a message is processed by this Receiver</td><td>-1 (disabled)</td></tr>
 * <tr><td>{@link #setAfterEvent(int) afterEvent}</td>        <td>METT eventnumber, fired just after message processing by this Receiver is finished</td><td>-1 (disabled)</td></tr>
 * <tr><td>{@link #setExceptionEvent(int) exceptionEvent}</td><td>METT eventnumber, fired when message processing by this Receiver resulted in an exception</td><td>-1 (disabled)</td></tr>
 * </table>
 * </p>
 * <p>
 * <table border="1">
 * <tr><th>nested elements (accessible in descender-classes)</th><th>description</th></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.IPullingListener listener}</td><td>the listener used to receive messages from</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ITransactionalStorage inProcessStorage}</td><td>mandatory for {@link #setTransacted(boolean) transacted} receivers: place to store messages during processing.</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ITransactionalStorage errorStorage}</td><td>optional for {@link #setTransacted(boolean) transacted} receivers: place to store messages if message processing has gone wrong. If no errorStorage is specified, the inProcessStorage is used for errorStorage</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ISender errorSender}</td><td>optional for {@link #setTransacted(boolean) transacted} receviers: 
 * will be called to store messages that failed to process. If no errorSender is specified, failed messages will remain in inProcessStorage</td></tr>
 * </table>
 * </p>
 * <p><b>Transaction control</b><br>
 * If {@link #setTransacted(boolean) transacted} is set to <code>true</code>, messages will be received and processed under transaction control.
 * This means that after a message has been read and processed and the transaction has ended, one of the following apply:
 * <ul>
 * <table border="1">
 * <tr><th>situation</th><th>input listener</th><th>Pipeline</th><th>inProcess storage</th><th>errorSender</th><th>summary of effect</th></tr>
 * <tr><td>successful</td><td>message read and committed</td><td>message processed</td><td>unchanged</td><td>unchanged</td><td>message processed</td></tr>
 * <tr><td>procesing failed</td><td>message read and committed</td><td>message processing failed and rolled back</td><td>unchanged</td><td>message sent</td><td>message only transferred from listener to errroSender</td></tr>
 * <tr><td>listening failed</td><td>unchanged: listening rolled back</td><td>no processing performed</td><td>unchanged</td><td>unchanged</td><td>no changes, input message remains on input available for listener</td></tr>
 * <tr><td>transfer to inprocess storage failed</td><td>unchanged: listening rolled back</td><td>no processing performed</td><td>unchanged</td><td>unchanged</td><td>no changes, input message remains on input available for listener</td></tr>
 * <tr><td>transfer to errorSender failed</td><td>message read and committed</td><td>message processing failed and rolled back</td><td>message present</td><td>unchanged</td><td>message only transferred from listener to inProcess storage</td></tr>
 * </table> 
 * If the application or the server crashes in the middle of one or more transactions, these transactions 
 * will be recovered and rolled back after the server/application is restarted. Then allways exactly one of 
 * the following applies for any message touched at any time by Ibis by a transacted receiver:
 * <ul>
 * <li>It is processed correctly by the pipeline and removed from the input-queue, 
 *     not present in inProcess storage and not send to the errorSender</li> 
 * <li>It is not processed at all by the pipeline, or processing by the pipeline has been rolled back; 
 *     the message is removed from the input queue and either (one of) still in inProcess storage <i>or</i> sent to the errorSender</li>
 * </ul>
 * </p>
 *
 * <p><b>commit or rollback</b><br>
 * If {@link #setTransacted(boolean) transacted} is set to <code>true</code>, messages will be either committed or rolled back.
 * All message-processing transactions are committed, unless one or more of the following apply:
 * <ul>
 * <li>The PipeLine is transacted and the exitState of the pipeline is not equal to {@link nl.nn.adapterframework.core.PipeLine#setCommitOnState(String) commitOnState} (that defaults to 'success')</li>
 * <li>a PipeRunException or another runtime-exception has been thrown by any Pipe or by the PipeLine</li>
 * <li>the setRollBackOnly() method has been called on the userTransaction (not accessible by Pipes)</li>
 * </ul>
 * </p>
 *
 * @version Id
 * @author     Gerrit van Brakel
 * @since 4.2
 */
public class ReceiverBase extends ReceiverBaseClassic {
}
