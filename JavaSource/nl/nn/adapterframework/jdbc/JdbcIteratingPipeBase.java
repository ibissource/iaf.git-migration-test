/*
 * $Log: JdbcIteratingPipeBase.java,v $
 * Revision 1.1  2008-02-26 08:34:48  europe\L190409
 * renamed IteratingPipeBase to JdbcIteratingPipeBase
 *
 * Revision 1.3  2007/10/08 12:18:43  gerrit
 * changed HashMap to Map where possible
 *
 * Revision 1.2  2007/07/26 16:14:38  gerrit
 * check for null resultset
 *
 * Revision 1.1  2007/07/17 11:16:49  gerrit
 * added iterating classes
 *
 */
package nl.nn.adapterframework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IDataIterator;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.core.PipeStartException;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;
import nl.nn.adapterframework.pipes.IteratingPipe;


/**
 * Base class for JDBC iterating pipes.
 *
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>className</td><td>nl.nn.adapterframework.jdbc.JdbcIteratingPipeBase</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMaxThreads(int) maxThreads}</td><td>maximum number of threads that may call {@link #doPipe(Object, nl.nn.adapterframework.core.PipeLineSession)} simultaneously</td><td>0 (unlimited)</td></tr>
 * <tr><td>{@link #setDurationThreshold(long) durationThreshold}</td><td>if durationThreshold >=0 and the duration (in milliseconds) of the message processing exceeded the value specified the message is logged informatory</td><td>-1</td></tr>
 * <tr><td>{@link #setGetInputFromSessionKey(String) getInputFromSessionKey}</td><td>when set, input is taken from this session key, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setStoreResultInSessionKey(String) storeResultInSessionKey}</td><td>when set, the result is stored under this session key</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCheckXmlWellFormed(boolean) checkXmlWellFormed}</td><td>when set <code>true</code>, the XML well-formedness of the result is checked</td><td>false</td></tr>
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
 * <tr><td>{@link #setResultOnTimeOut(String) resultOnTimeOut}</td><td>result returned when no return-message was received within the timeout limit</td><td>"receiver timed out"</td></tr>
 * <tr><td>{@link #setLinkMethod(String) linkMethod}</td><td>Indicates wether the server uses the correlationID or the messageID in the correlationID field of the reply</td><td>CORRELATIONID</td></tr>
 * <tr><td>{@link #setAuditTrailXPath(String) auditTrailXPath}</td><td>xpath expression to extract audit trail from message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCorrelationIDXPath(String) correationIdXPath}</td><td>xpath expression to extract correlationID from message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setStopConditionXPathExpression(String) stopConditionXPathExpression}</td><td>expression evaluated on each result if set. 
 * 		Iteration stops if condition returns anything other than <code>false</code> or an empty result.
 * For example, to stop after the second child element has been processed, one of the following expressions could be used:
 * <table> 
 * <tr><td><li><code>result[@item='2']</code></td><td>returns result element after second child element has been processed</td></tr>
 * <tr><td><li><code>result/@item='2'</code></td><td>returns <code>false</code> after second child element has been processed, <code>true</code> for others</td></tr>
 * </table> 
 * </td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setRemoveXmlDeclarationInResults(boolean) removeXmlDeclarationInResults}</td><td>postprocess each partial result, to remove the xml-declaration, as this is not allowed inside an xml-document</td><td>false</td></tr>
 * <tr><td>{@link #setCollectResults(boolean) collectResults}</td><td>controls whether all the results of each iteration will be collected in one result message. If set <code>false</code>, only a small summary is returned</td><td>true</td></tr>
 * <tr><td>{@link #setQuery(String) query}</td><td>the SQL query text to be excecuted each time sendMessage() is called</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setDatasourceName(String) datasourceName}</td><td>can be configured from JmsRealm, too</td><td>&nbsp;</td></tr>
 * </table>
 * <table border="1">
 * <tr><th>nested elements</th><th>description</th></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ISender sender}</td><td>specification of sender to send messages with</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ICorrelatedPullingListener listener}</td><td>specification of listener to listen to for replies</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.parameters.Parameter param}</td><td>any parameters defined on the pipe will be handed to the sender, if this is a {@link IParameterizedSender}</td></tr>
 * <tr><td><code>inputValidator</code></td><td>specification of Pipe to validate input messages</td></tr>
 * <tr><td><code>outputValidator</code></td><td>specification of Pipe to validate output messages</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ITransactionalStorage messageLog}</td><td>log of all messages sent</td></tr>
 * </table>
 * </p>
 * <p><b>Exits:</b>
 * <table border="1">
 * <tr><th>state</th><th>condition</th></tr>
 * <tr><td>"success"</td><td>default when a good message was retrieved (synchronous sender), or the message was successfully sent and no listener was specified and the sender was not synchronous</td></tr>
 * <tr><td><i>{@link #setForwardName(String) forwardName}</i></td><td>if specified, and otherwise under same condition as "success"</td></tr>
 * <tr><td>"timeout"</td><td>no data was received (timeout on listening), if the sender was synchronous or a listener was specified.</td></tr>
 * <tr><td>"exception"</td><td>an exception was thrown by the Sender or its reply-Listener. The result passed to the next pipe is the exception that was caught.</td></tr>
 * </table>
 * </p>
 * 
 * @author  Gerrit van Brakel
 * @since   4.7
 * @version Id
 */
public abstract class JdbcIteratingPipeBase extends IteratingPipe {
	
	protected FixedQuerySender querySender = new FixedQuerySender();

	public void configure() throws ConfigurationException {
		super.configure();
		querySender.setName("source of "+getName());
		querySender.configure();
	}
	
	public void start() throws PipeStartException {
		try {
			querySender.open();
		} catch (SenderException e) {
			throw new PipeStartException(e);
		}
		super.start();
	}

	public void stop() {
		super.stop();
		try {
			querySender.close();
		} catch (SenderException e) {
			log.warn("Exception closing sender",e);
		}
	}

	protected abstract IDataIterator getIterator(ResultSet rs) throws SenderException; 

	protected IDataIterator getIterator(Object input, PipeLineSession session, String correlationID, Map threadContext) throws SenderException {
		Connection connection = null;
		try {
			connection = querySender.getConnection();
			PreparedStatement statement=null;
//			try {
				String msg = (String)input;
				statement = querySender.getStatement(connection, correlationID, msg);
				ParameterResolutionContext prc = new ParameterResolutionContext(msg,session);
				if (prc != null && querySender.paramList != null) {
					querySender.applyParameters(statement, prc.getValues(querySender.paramList));
				}
				ResultSet rs = statement.executeQuery();
				if (rs==null || !rs.next()) {
					throw new SenderException("query has empty resultset");
				}
				return getIterator(rs);
//			} finally {
//				try {
//					if (statement!=null) {
//						statement.close();
//					}
//				} catch (SQLException e) {
//					log.warn(new SenderException(getLogPrefix(session) + "got exception closing SQL statement",e ));
//				}
//			}
		} catch (Exception e) {
			throw new SenderException(e);
//		} finally {
//			if (connection!=null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					log.warn(new SenderException(getLogPrefix(session) + "caught exception closing sender after sending message, ID=["+correlationID+"]", e));
//				}
//			}
		}
	}

	public void addParameter(Parameter p) {
		querySender.addParameter(p);
	}


	public void setQuery(String query) {
		querySender.setQuery(query);
	}
	public String getQuery() {
		return querySender.getQuery();
	}
	
	public void setDatasourceName(String datasourceName) {
		querySender.setDatasourceName(datasourceName);
	}
	public String getDatasourceName() {
		return querySender.getDatasourceName();
	}

	public String getPhysicalDestinationName() {
		return querySender.getPhysicalDestinationName();
	}

	public void setJmsRealm(String jmsRealmName) {
		querySender.setJmsRealm(jmsRealmName);
	}
	

}