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
 * $Log: AbstractPipe.java,v $
 * Revision 1.45  2012-10-10 10:19:37  jaco
 * Made it possible to use Locker on Pipe level too
 *
 * Revision 1.44  2012/08/23 11:57:44  jaco
 * Updates from Michiel
 *
 * Revision 1.43  2012/06/01 10:52:50  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.42  2012/03/16 15:35:44  jaco
 * Michiel added EsbSoapValidator and WsdlXmlValidator, made WSDL's available for all adapters and did a bugfix on XML Validator where it seems to be dependent on the order of specified XSD's
 *
 * Revision 1.41  2011/11/30 13:51:51  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:45  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.39  2011/08/22 14:25:55  gerrit
 * support for size statistics
 *
 * Revision 1.38  2011/05/25 07:41:24  gerrit
 * removed unused imports
 *
 * Revision 1.37  2010/11/12 15:12:14  peter
 * improved check on forwards
 *
 * Revision 1.36  2010/09/13 13:43:01  gerrit
 * removed 'final' modifier at findForward()
 *
 * Revision 1.35  2009/05/06 11:39:50  gerrit
 * keep reference to pipeline
 *
 * Revision 1.34  2008/12/30 17:01:12  peter
 * added configuration warnings facility (in Show configurationStatus)
 *
 * Revision 1.33  2008/07/14 17:24:05  gerrit
 * support for flexibile monitoring
 *
 * Revision 1.32  2008/02/06 15:57:09  gerrit
 * added support for setting of transaction timeout
 *
 * Revision 1.31  2008/01/11 09:47:18  gerrit
 * use Springs transaction definition
 *
 * Revision 1.30  2007/12/10 10:08:58  gerrit
 * assume usertransaction can be obtained
 *
 * Revision 1.29  2007/09/27 12:54:38  gerrit
 * improved warning about duplicate forward
 *
 * Revision 1.28  2007/06/08 07:58:14  gerrit
 * set default transactionAttribute to Supports
 *
 * Revision 1.27  2007/05/02 11:34:34  gerrit
 * added attribute 'active'
 * added attribute getInputFromFixedValue
 *
 * Revision 1.26  2007/05/01 14:09:39  gerrit
 * introduction of PipeLine-exithandlers
 *
 * Revision 1.25  2007/02/12 14:02:19  gerrit
 * Logger from LogUtil
 *
 * Revision 1.24  2006/12/28 14:21:23  gerrit
 * updated javadoc
 *
 * Revision 1.23  2006/10/13 08:17:24  gerrit
 * cache UserTransaction at startup
 *
 * Revision 1.22  2006/09/14 11:59:09  gerrit
 * corrected javadoc
 *
 * Revision 1.21  2006/08/24 07:12:42  gerrit
 * documented METT tracing event numbers
 *
 * Revision 1.20  2006/08/22 12:52:36  gerrit
 * added preserveInput attribute
 *
 * Revision 1.19  2006/08/21 15:21:23  gerrit
 * introduction of transaction attribute handling
 *
 * Revision 1.18  2006/02/20 15:42:41  gerrit
 * moved METT-support to single entry point for tracing
 *
 * Revision 1.17  2006/02/09 08:01:48  gerrit
 * METT tracing support
 *
 * Revision 1.16  2006/01/05 14:34:48  gerrit
 * updated javadoc
 *
 * Revision 1.15  2005/10/24 09:20:18  gerrit
 * made namespaceAware an attribute of AbstractPipe
 *
 * Revision 1.14  2005/09/08 15:53:01  gerrit
 * moved extra functionality to IExtendedPipe
 *
 * Revision 1.13  2005/09/07 15:26:16  gerrit
 * added attributes getInputFromSessionKey and storeResultInSessionKey
 *
 * Revision 1.12  2005/09/05 07:00:06  gerrit
 * changed maxDuration into durationThreshold
 *
 * Revision 1.11  2005/09/01 08:51:10  gerrit
 * added maxDuration attribute, to be used to log messages that take long time
 *
 * Revision 1.10  2005/08/24 15:52:16  gerrit
 * improved error message for configuration exception
 *
 * Revision 1.9  2005/06/13 10:00:15  gerrit
 * modified handling of empty forwards
 *
 * Revision 1.8  2004/10/19 13:51:58  gerrit
 * parameter-configure in configure()
 *
 * Revision 1.7  2004/10/05 10:47:03  gerrit
 * retyped ParameterList
 *
 * Revision 1.6  2004/05/21 07:37:08  unknown0
 * Moved PipeParameter to core
 *
 * Revision 1.5  2004/04/06 10:16:16  johan
 * Added PipeParameter and implemented it. Added XsltParamPipe also
 *
 * Revision 1.4  2004/03/30 07:30:05  gerrit
 * updated javadoc
 *
 */
package nl.nn.adapterframework.pipes;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.configuration.ConfigurationWarnings;
import nl.nn.adapterframework.core.HasTransactionAttribute;
import nl.nn.adapterframework.core.IAdapter;
import nl.nn.adapterframework.core.IExtendedPipe;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeForward;
import nl.nn.adapterframework.core.PipeLine;
import nl.nn.adapterframework.core.PipeLineExit;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.core.PipeStartException;
import nl.nn.adapterframework.monitoring.EventHandler;
import nl.nn.adapterframework.monitoring.EventThrowing;
import nl.nn.adapterframework.monitoring.MonitorManager;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.ParameterList;
import nl.nn.adapterframework.util.JtaUtil;
import nl.nn.adapterframework.util.Locker;
import nl.nn.adapterframework.util.LogUtil;
import nl.nn.adapterframework.util.TracingEventNumbers;
import nl.nn.adapterframework.util.XmlUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionDefinition;

/**
 * Base class for {@link nl.nn.adapterframework.core.IPipe Pipe}.
 * A Pipe represents an action to take in a {@link nl.nn.adapterframework.core.PipeLine Pipeline}. This class is ment to be extended
 * for defining steps or actions to take to complete a request. <br/>
 * The contract is that a pipe is created (by the digester), {@link #setName(String)} is called and
 * other setters are called, and then {@link nl.nn.adapterframework.core.IPipe#configure()} is called, optionally
 * throwing a {@link nl.nn.adapterframework.configuration.ConfigurationException}. <br/>
 * As much as possible, class instantiating should take place in the
 * {@link nl.nn.adapterframework.core.IPipe#configure()} method.
 * The object remains alive while the framework is running. When the pipe is to be run,
 * the {@link nl.nn.adapterframework.core.IPipe#doPipe(Object, PipeLineSession) doPipe} method is activated.
 * <p>
 * For the duration of the processing of a message by the {@link nl.nn.adapterframework.core.PipeLine pipeline} has a {@link nl.nn.adapterframework.core.PipeLineSession session}.
 * <br/>
 * By this mechanism, pipes may communicate with one another.<br/>
 * However, use this functionality with caution, as it is not desirable to make pipes dependend
 * on each other. If a pipe expects something in a session, it is recommended that
 * the key under which the information is stored is configurable (has a setter for this keyname).
 * Also, the setting of something in the <code>PipeLineSession</code> should be done using
 * this technique (specifying the key under which to store the value by a parameter).
 * </p>
 * <p>Since 4.1 this class also has parameters, so that decendants of this class automatically are parameter-enabled.
 * However, your documentation should say if and how parameters are used!<p>
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>className</td><td>nl.nn.adapterframework.pipes.AbstractPipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMaxThreads(int) maxThreads}</td><td>maximum number of threads that may call {@link #doPipe(Object, PipeLineSession)} simultaneously</td><td>0 (unlimited)</td></tr>
 * <tr><td>{@link #setActive(boolean) active}</td><td>controls whether Pipe is included in configuration. When set <code>false</code> or set to something else as "true", (even set to the empty string), the Pipe is not included in the configuration</td><td>true</td></tr>
 * <tr><td>{@link #setDurationThreshold(long) durationThreshold}</td><td>if durationThreshold >=0 and the duration (in milliseconds) of the message processing exceeded the value specified, then the message is logged informatory</td><td>-1</td></tr>
 * <tr><td>{@link #setGetInputFromSessionKey(String) getInputFromSessionKey}</td><td>when set, input is taken from this session key, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setGetInputFromFixedValue(String) getInputFromFixedValue}</td><td>when set, this fixed value is taken as input, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setStoreResultInSessionKey(String) storeResultInSessionKey}</td><td>when set, the result is stored under this session key</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setPreserveInput(boolean) preserveInput}</td><td>when set <code>true</code>, the input of a pipe is restored before processing the next one</td><td>false</td></tr>
 * <tr><td>{@link #setNamespaceAware(boolean) namespaceAware}</td><td>controls namespace-awareness of possible XML parsing in descender-classes</td><td>application default</td></tr>
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
 *  </table></td><td>Supports</td></tr>
 * <tr><td>{@link #setTransactionTimeout(int) transactionTimeout}</td><td>Timeout (in seconds) of transaction started to process a message.</td><td><code>0</code> (use system default)</code></td></tr>
 * <tr><td>{@link #setBeforeEvent(int) beforeEvent}</td>      <td>METT eventnumber, fired just before a message is processed by this Pipe</td><td>-1 (disabled)</td></tr>
 * <tr><td>{@link #setAfterEvent(int) afterEvent}</td>        <td>METT eventnumber, fired just after message processing by this Pipe is finished</td><td>-1 (disabled)</td></tr>
 * <tr><td>{@link #setExceptionEvent(int) exceptionEvent}</td><td>METT eventnumber, fired when message processing by this Pipe resulted in an exception</td><td>-1 (disabled)</td></tr>
 * </table>
 * </p>
 * 
 * <p>
 * <table border="1">
 * <tr><th>nested elements</th><th>description</th></tr>
 * <tr><td>{@link nl.nn.adapterframework.scheduler.Locker locker}</td><td>optional: the pipe will only be executed if a lock could be set successfully</td></tr>
 * </table>
 * </p>
 * 
 * @version $Id$
 * @author     Johan Verrips / Gerrit van Brakel
 *
 * @see nl.nn.adapterframework.core.PipeLineSession
 */
public abstract class AbstractPipe implements IExtendedPipe, HasTransactionAttribute, TracingEventNumbers, EventThrowing {
	protected Logger log = LogUtil.getLogger(this);

	private String name;

	private Map<String, PipeForward> pipeForwards = new Hashtable<String, PipeForward>();
	private int maxThreads = 0;
	private ParameterList parameterList = new ParameterList();
	private long durationThreshold = -1;
	private String getInputFromSessionKey=null;
	private String getInputFromFixedValue=null;
	private String storeResultInSessionKey=null;
	private boolean preserveInput=false;
	private boolean namespaceAware=XmlUtils.isNamespaceAwareByDefault();
	private int transactionAttribute=TransactionDefinition.PROPAGATION_SUPPORTS;
	private int transactionTimeout=0;
	private boolean sizeStatistics=false;
	private Locker locker;

	// METT event numbers
	private int beforeEvent=-1;
	private int afterEvent=-1;
	private int exceptionEvent=-1;

	private boolean active=true;

	private EventHandler eventHandler=null;

	private PipeLine pipeline;


	/**
	 * <code>configure()</code> is called after the {@link nl.nn.adapterframework.core.PipeLine Pipeline} is registered
	 * at the {@link nl.nn.adapterframework.core.Adapter Adapter}. Purpose of this method is to reduce
	 * creating connections to databases etc. in the {@link #doPipe(Object) doPipe()} method.
	 * As much as possible class-instantiating should take place in the
	 * <code>configure()</code> method, to improve performance.
	 */
	public void configure() throws ConfigurationException {
		ParameterList params = getParameterList();
		if (params!=null) {
			try {
				params.configure();
			} catch (ConfigurationException e) {
				throw new ConfigurationException(getLogPrefix(null)+"while configuring parameters",e);
			}
		}

		if (pipeForwards.isEmpty()) {
			ConfigurationWarnings configWarnings = ConfigurationWarnings.getInstance();
			String msg = getLogPrefix(null)+"has no forwards defined.";
			configWarnings.add(log, msg);
		} else {
			for (Iterator it = pipeForwards.keySet().iterator(); it.hasNext();) {
				String forwardName = (String)it.next();
				PipeForward forward= pipeForwards.get(forwardName);
				if (forward!=null) {
					String path=forward.getPath();
					if (path!=null) {
						PipeLineExit plExit= pipeline.getPipeLineExits().get(path);
						if (plExit==null){
							if (pipeline.getPipe(path)==null){
								ConfigurationWarnings configWarnings = ConfigurationWarnings.getInstance();
								String msg = getLogPrefix(null)+"has a forward of which the pipe to execute ["+path+"] is not defined.";
								configWarnings.add(log, msg);
							}
						}
					}
				}
			}
		}

		if (getLocker() != null) {
			getLocker().configure();
		}

		eventHandler = MonitorManager.getEventHandler();
	}

	/**
	 * Extension for IExtendedPipe that calls configure(void) in its implementation.
	 */
	public void configure(PipeLine pipeline) throws ConfigurationException {
		this.pipeline=pipeline;
		configure();
	}


	/**
	 * This is where the action takes place. Pipes may only throw a PipeRunException,
	 * to be handled by the caller of this object.
	 * @deprecated use {@link #doPipe(Object,PipeLineSession)} instead
	 */
	public PipeRunResult doPipe (Object input) throws PipeRunException {
		throw new PipeRunException(this, "Pipe should implement method doPipe()");
	}

	/**
	 * This is where the action takes place. Pipes may only throw a PipeRunException,
	 * to be handled by the caller of this object.
	 */
	public PipeRunResult doPipe (Object input, IPipeLineSession session) throws PipeRunException {
		return doPipe(input);
	}

    /**
     * looks up a key in the pipeForward hashtable. <br/>
     * A typical use would be on return from a Pipe: <br/>
     * <code><pre>
     * return new PipeRunResult(findForward("success"), result);
     * </pre></code>
     * In the pipeForward hashtable are available:
     * <ul><li>All forwards defined in xml under the pipe element of this pipe</li>
     * <li> All global forwards defined in xml under the PipeLine element</li>
     * <li> All pipenames with their (identical) path</li>
     * </ul>
     * Therefore, you can directly jump to another pipe, although this is not recommended
     * as the pipe should not know the existence of other pipes. Nevertheless, this feature
     * may come in handy for switcher-pipes.<br/.<br/>
     * @param forward   Name of the forward
     * @return PipeForward
     */
    public PipeForward findForward(String forward){
    	if (StringUtils.isEmpty(forward)) {
    		return null;
    	}
        return pipeForwards.get(forward);
    }

	/**
	 * Convenience method for building up log statements.
	 * This method may be called from within the <code>doPipe()</code> method with the current <code>PipeLineSession</code>
	 * as a parameter. Then it will use this parameter to retrieve the messageId. The method can be called with a <code>null</code> parameter
	 * from the <code>configure()</code>, <code>start()</code> and <code>stop()</code> methods.
	 * @return String with the name of the pipe and the message id of the current message.
	 */
	protected String getLogPrefix(IPipeLineSession session){
		  StringBuilder sb = new StringBuilder();
		  sb.append("Pipe ["+getName()+"] ");
		  if (session!=null) {
			  sb.append("msgId ["+session.getMessageId()+"] ");
		  }
		  return sb.toString();
	}

	/**
	 * Register a PipeForward object to this Pipe. Global Forwards are added
	 * by the PipeLine. If a forward is already registered, it logs a warning.
	 * @param forward
	 * @see nl.nn.adapterframework.core.PipeLine
	 * @see PipeForward
	 */
	public void registerForward(PipeForward forward){
		PipeForward current = pipeForwards.get(forward.getName());
		if (current==null){
			pipeForwards.put(forward.getName(), forward);
		} else {
			if (forward.getPath().equals(current.getPath())) {
				ConfigurationWarnings configWarnings = ConfigurationWarnings.getInstance();
				String msg = getLogPrefix(null)+"PipeForward ["+forward.getName()+"] pointing to ["+forward.getPath()+"] already registered";
				configWarnings.add(log, msg);
			} else {
				log.info(getLogPrefix(null)+"PipeForward ["+forward.getName()+"] already registered, pointing to ["+current.getPath()+"]. Ignoring new one, that points to ["+forward.getPath()+"]");
			}
		}
 	}


	/**
	  * Perform necessary action to start the pipe. This method is executed
	  * after the {@link #configure()} method, for eacht start and stop command of the
	  * adapter.
	  */
	public void start() throws PipeStartException{
//		if (getTransactionAttributeNum()>0 && getTransactionAttributeNum()!=JtaUtil.TRANSACTION_ATTRIBUTE_SUPPORTS) {
//			try {
//				// getUserTransaction, to make sure its available
//				JtaUtil.getUserTransaction();
//			} catch (NamingException e) {
//				throw new PipeStartException(getLogPrefix(null)+"cannot obtain UserTransaction",e);
//			}
//		}
	}
	 /**
	  * Perform necessary actions to stop the <code>Pipe</code>.<br/>
	  * For instance, closing JMS connections, dbms connections etc.
	  */
	 public void stop() {}

	 /**
	  * The <code>toString()</code> method retrieves its value
	  * by reflection, so overriding this method is mostly not
	  * usefull.
	  * @see org.apache.commons.lang.builder.ToStringBuilder#reflectionToString
	  *
	  **/



    public String toString() {
		return ToStringBuilder.reflectionToString(this);
    }

	/**
	 * Add a parameter to the list of parameters
	 * @param rhs the PipeParameter.
	 */
	public void addParameter(Parameter rhs) {
		log.debug("Pipe ["+getName()+"] added parameter ["+rhs.toString()+"]");
		parameterList.add(rhs);
	}

	/**
	 * return the Parameters
	 */
	public ParameterList getParameterList() {
		return parameterList;
	}

	public String getEventSourceName() {
		return getLogPrefix(null).trim();
	}
	public void registerEvent(String description) {
		if (eventHandler!=null) {
			eventHandler.registerEvent(this,description);
		}
	}
	public void throwEvent(String event) {
		if (eventHandler!=null) {
			eventHandler.fireEvent(this,event);
		}
	}

	public PipeLine getPipeLine() {
		return pipeline;
	}

	public IAdapter getAdapter() {
		if (getPipeLine()!=null) {
			return getPipeLine().getAdapter();
		}
		return null;
	}


	/**
	 * Indicates the maximum number of treads ;that may call {@link #doPipe(Object, PipeLineSession)} simultaneously in case
	 *  A value of 0 indicates an unlimited number of threads.
	 */
	public void setMaxThreads(int newMaxThreads) {
	  maxThreads = newMaxThreads;
	}
	public int getMaxThreads() {
		return maxThreads;
	}

	/**
	 * The functional name of this pipe
	 */
	public void setName(String name) {
		this.name=name;
	}
	public String getName() {
	  return this.name;
	}

	/**
	 * Sets a threshold for the duration of message execution;
	 * If the threshold is exceeded, the message is logged to be analyzed.
	 */
	public void setDurationThreshold(long maxDuration) {
		this.durationThreshold = maxDuration;
	}
	public long getDurationThreshold() {
		return durationThreshold;
	}





	public void setGetInputFromSessionKey(String string) {
		getInputFromSessionKey = string;
	}
	public String getGetInputFromSessionKey() {
		return getInputFromSessionKey;
	}

	public void setGetInputFromFixedValue(String string) {
		getInputFromFixedValue = string;
	}
	public String getGetInputFromFixedValue() {
		return getInputFromFixedValue;
	}

	public void setStoreResultInSessionKey(String string) {
		storeResultInSessionKey = string;
	}
	public String getStoreResultInSessionKey() {
		return storeResultInSessionKey;
	}

	public void setPreserveInput(boolean preserveInput) {
		this.preserveInput = preserveInput;
	}
	public boolean isPreserveInput() {
		return preserveInput;
	}


	public void setNamespaceAware(boolean b) {
		namespaceAware = b;
	}
	public boolean isNamespaceAware() {
		return namespaceAware;
	}


	// event numbers for tracing

	public int getAfterEvent() {
		return afterEvent;
	}

	public int getBeforeEvent() {
		return beforeEvent;
	}

	public int getExceptionEvent() {
		return exceptionEvent;
	}

	public void setAfterEvent(int i) {
		afterEvent = i;
	}

	public void setBeforeEvent(int i) {
		beforeEvent = i;
	}

	public void setExceptionEvent(int i) {
		exceptionEvent = i;
	}


	public void setTransactionAttribute(String attribute) throws ConfigurationException {
		transactionAttribute = JtaUtil.getTransactionAttributeNum(attribute);
		if (transactionAttribute<0) {
			throw new ConfigurationException("illegal value for transactionAttribute ["+attribute+"]");
		}
	}
	public String getTransactionAttribute() {
		return JtaUtil.getTransactionAttributeString(transactionAttribute);
	}

	public void setTransactionAttributeNum(int i) {
		transactionAttribute = i;
	}
	public int getTransactionAttributeNum() {
		return transactionAttribute;
	}

	public void setActive(boolean b) {
		active = b;
	}
	public boolean isActive() {
		return active;
	}

	public void setTransactionTimeout(int i) {
		transactionTimeout = i;
	}
	public int getTransactionTimeout() {
		return transactionTimeout;
	}

	public boolean hasSizeStatistics() {
		return sizeStatistics;
	}
	public void setSizeStatistics(boolean sizeStatistics) {
		this.sizeStatistics = sizeStatistics;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}
	public Locker getLocker() {
		return locker;
	}

}
