/*
 * $Log: OneParameterQuerySender.java,v $
 * Revision 1.3  2004-10-19 08:12:32  L190409
 * made obsolete with introduction of generic parameter handling
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
 * QuerySender that assumes a fixed query with one string parameter that is substituted with the message.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.jdbc.OneParameterQuerySender</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setQuery(String) query}</td><td>the SQL query text to be excecuted each time sendMessage() is called</td><td>&nbsp;</td></tr>
 * </table>
 * for further configuration options, see {@link JdbcQuerySenderBase}
 * </p>
 * 
 * @version Id
 * @author  Gerrit van Brakel
 * @since 	4.1
 * @deprecated Please use FixedQuerySender with nested {@link nl.nn.adapterframework.parameters.Parameter parameters} instead.
 */
public class OneParameterQuerySender extends FixedQuerySender {
	public static final String version="$Id: OneParameterQuerySender.java,v 1.3 2004-10-19 08:12:32 L190409 Exp $";

	protected PreparedStatement getStatement(Connection con, String correlationID, String message) throws JdbcException, SQLException {
		PreparedStatement stmt = super.getStatement(con, correlationID, message);
		stmt.setString(1,message);
		return stmt;
	}
}
