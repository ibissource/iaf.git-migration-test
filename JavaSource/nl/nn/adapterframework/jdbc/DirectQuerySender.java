/*
 * $Log: DirectQuerySender.java,v $
 * Revision 1.11  2006-12-13 16:27:13  europe\L190409
 * added attribute blobCharset
 *
 * Revision 1.10  2006/12/12 09:57:35  gerrit
 * restore jdbc package
 *
 * Revision 1.8  2006/01/05 14:21:21  gerrit
 * updated javadoc
 *
 * Revision 1.7  2005/10/19 10:45:18  gerrit
 * moved prepareStatement-met-columnlist to separate method, 
 * to avoid compilation problems when non JDBC 3.0 drivers are used
 *
 * Revision 1.6  2005/10/18 07:10:12  gerrit
 * update javadoc
 *
 * Revision 1.5  2005/09/29 13:59:49  gerrit
 * provided attributes and handling for nullValue,columnsReturned and resultQuery
 *
 * Revision 1.4  2005/09/07 15:37:07  gerrit
 * updated javadoc
 *
 * Revision 1.3  2004/10/19 08:11:56  gerrit
 * modified JavaDoc
 *
 * Revision 1.2  2004/03/26 10:43:09  johan
 * added @version tag in javadoc
 *
 * Revision 1.1  2004/03/24 13:28:20  gerrit
 * initial version
 *
 */
package nl.nn.adapterframework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * QuerySender that interprets the input message as a query.
 * Messages are expected to contain sql-text.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.jdbc.DirectQuerySender</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td>  <td>name of the sender</td><td>&nbsp;</td></tr>

 * <tr><td>{@link #setDatasourceName(String) datasourceName}</td><td>can be configured from JmsRealm, too</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setDatasourceNameXA(String) datasourceNameXA}</td><td>can be configured from JmsRealm, too</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setUsername(String) username}</td><td>username used to connect to datasource</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setPassword(String) password}</td><td>password used to connect to datasource</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setConnectionsArePooled(boolean) connectionsArePooled}</td><td>when true, it is assumed that an connectionpooling mechanism is present. Before a message is sent, a new connection is obtained, that is closed after the message is sent. When transacted is true, connectionsArePooled is true, too</td><td>true</td></tr>
 * <tr><td>{@link #setTransacted(boolean) transacted}</td><td>&nbsp;</td><td>false</td></tr>
 * <tr><td>{@link #setJmsRealm(String) jmsRealm}</td><td>&nbsp;</td><td>&nbsp;</td></tr>

 * <tr><td>{@link #setQueryType(String) queryType}</td><td>one of:
 * <ul><li>"select" for queries that return data</li>
 *     <li>"updateBlob" for queries that update a BLOB</li>
 *     <li>anything else for queries that return no data.</li>
 * </ul></td><td>"other"</td></tr>
 * <tr><td>{@link #setMaxRows(int) maxRows}</td><td>maximum number of rows returned</td><td>-1 (unlimited)</td></tr>
 * <tr><td>{@link #setStartRow(int) startRow}</td><td>the number of the first row returned from the output</td><td>1</td></tr>
 * <tr><td>{@link #setScalar(boolean) scalar}</td><td>when true, the value of the first column of the first row (or the StartRow) is returned as the only result, as a simple non-XML value</td><td>false</td></tr>
 * <tr><td>{@link #setNullValue(String) nullValue}</td><td>value used in result as contents of fields that contain no value (SQL-NULL)</td><td><i>empty string</></td></tr>
 * <tr><td>{@link #setResultQuery(String) resultQuery}</td><td>query that can be used to obtain result of side-effecto of update-query, like generated value of sequence. Example: SELECT mysequence.currval FROM DUAL</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSynchronous(boolean) synchronous}</td><td>&nbsp;</td><td>true</td></tr>
 * <tr><td>{@link #setTrimSpaces(boolean) trimSpaces}</td><td>remove trailing blanks from all values.</td><td>true</td></tr>
 * <tr><td>{@link #setBlobCharset(String) blobCharset}</td><td>charset used to read and write BLOBs</td><td>UTF-8</td></tr>
 * <tr><td>{@link #setBlobsCompressed(boolean) blobsCompressed}</td><td>controls whether blobdata is stored compressed in the database</td><td>true</td></tr>
 * <tr><td>{@link #setColumnsReturned(String) columnsReturned}</td><td>comma separated list of columns whose values are to be returned. Works only if the driver implements JDBC 3.0 getGeneratedKeys()</td><td>&nbsp;</td></tr>
 * </table>
 * </p>
 * <table border="1">
 * <p><b>Parameters:</b>
 * <tr><th>name</th><th>type</th><th>remarks</th></tr>
 * <tr><td>&nbsp;</td><td>all parameters present are applied to the statement to be executed</td></tr>
 * </table>
 * </p>
 * 
 * @version Id
 * @author  Gerrit van Brakel
 * @since 	4.1
 */
public class DirectQuerySender extends JdbcQuerySenderBase {
	public static final String version="$RCSfile: DirectQuerySender.java,v $ $Revision: 1.11 $ $Date: 2006-12-13 16:27:13 $";

	protected PreparedStatement getStatement(Connection con, String correlationID, String message) throws SQLException {
		return prepareQuery(con, message);
	}
}