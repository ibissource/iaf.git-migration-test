/*
 * $Log: JdbcFacade.java,v $
 * Revision 1.14  2006-12-12 09:57:37  europe\L190409
 * restore jdbc package
 *
 * Revision 1.12  2005/09/22 15:58:05  gerrit
 * added warning in comment for getParameterMetadata
 *
 * Revision 1.11  2005/08/24 15:47:25  gerrit
 * try to prefix with java:comp/env/ to find datasource (Tomcat compatibility)
 *
 * Revision 1.10  2005/08/17 16:10:56  gerrit
 * test for empty strings using StringUtils.isEmpty()
 *
 * Revision 1.9  2005/08/09 15:53:11  gerrit
 * improved exception construction
 *
 * Revision 1.8  2005/07/19 12:36:32  gerrit
 * moved applyParameters to JdbcFacade
 *
 * Revision 1.7  2005/06/13 09:57:03  gerrit
 * cosmetic changes
 *
 * Revision 1.6  2005/05/31 09:53:35  gerrit
 * corrected version
 *
 * Revision 1.5  2005/05/31 09:53:00  gerrit
 * introduction of attribute 'connectionsArePooled'
 *
 * Revision 1.4  2005/03/31 08:11:28  gerrit
 * corrected typo in logging
 *
 * Revision 1.3  2004/03/26 10:43:09  johan
 * added @version tag in javadoc
 *
 * Revision 1.2  2004/03/26 09:50:52  johan
 * Updated javadoc
 *
 * Revision 1.1  2004/03/24 13:28:20  gerrit
 * initial version
 *
 */
package nl.nn.adapterframework.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import nl.nn.adapterframework.core.HasPhysicalDestination;
import nl.nn.adapterframework.core.INamedObject;
import nl.nn.adapterframework.core.IXAEnabled;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.jms.JNDIBase;
import nl.nn.adapterframework.parameters.ParameterValueList;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Provides functions for JDBC connections.
 * 
 * @version Id
 * @author  Gerrit van Brakel
 * @since 	4.1
 * 
 */
public class JdbcFacade extends JNDIBase implements INamedObject, HasPhysicalDestination, IXAEnabled {
	public static final String version="$RCSfile: JdbcFacade.java,v $ $Revision: 1.14 $ $Date: 2006-12-12 09:57:37 $";
    protected Logger log = Logger.getLogger(this.getClass());
	
	private String name;
    private String username=null;
    private String password=null;
    
	private DataSource datasource = null;
	private String datasourceName = null;
	private String datasourceNameXA = null;

	private boolean transacted = false;
	private boolean connectionsArePooled=true;

	protected String getLogPrefix() {
		return "["+this.getClass().getName()+"] ["+getName()+"] ";
	}

	/**
	 * Returns either {@link #getDatasourceName() datasourceName} or {@link #getDatasourceNameXA() datasourceNameXA},
	 * depending on the value of {@link #isTransacted()}.
	 * If the right one is not specified, the other is used. 
	 */
	public String getDataSourceNameToUse() throws JdbcException {
		String result = isTransacted() ? getDatasourceNameXA() : getDatasourceName();
		if (StringUtils.isEmpty(result)) {
			// try the alternative...
			result = isTransacted() ? getDatasourceName() : getDatasourceNameXA();
			if (StringUtils.isEmpty(result)) {
				throw new JdbcException(getLogPrefix()+"neither datasourceName nor datasourceNameXA are specified");
			}
			log.warn(getLogPrefix()+"correct datasourceName attribute not specified, will use ["+result+"]");
		}
		return result;
	}

	protected DataSource getDatasource() throws JdbcException {
		if (datasource==null) {
				String dsName = getDataSourceNameToUse();
				try {
					log.debug(getLogPrefix()+"looking up Datasource ["+dsName+"]");
					datasource =(DataSource) getContext().lookup( dsName );
					log.debug(getLogPrefix()+"looked up Datasource ["+dsName+"]: ["+datasource+"]");
				} catch (NamingException e) {
					try {
						String tomcatDsName="java:comp/env/"+dsName;
						log.debug(getLogPrefix()+"could not find ["+dsName+"], now trying ["+tomcatDsName+"]");
						datasource =(DataSource) getContext().lookup( tomcatDsName );
						log.debug(getLogPrefix()+"looked up Datasource ["+tomcatDsName+"]: ["+datasource+"]");
					} catch (NamingException e2) {
						throw new JdbcException(getLogPrefix()+"cannot find Datasource ["+dsName+"]", e);
					}
				}
		}
		return datasource;
	}
	
	/**
	 * Obtains a connection to the datasource. 
	 */
	// TODO: consider making this one protected.
	public Connection getConnection() throws JdbcException {
		try {
			if (StringUtils.isNotEmpty(getUsername())) {
				return getDatasource().getConnection(getUsername(),getPassword());
			} else {
				return getDatasource().getConnection();
			}
		} catch (SQLException e) {
			throw new JdbcException(getLogPrefix()+"cannot open connection on datasource ["+getDataSourceNameToUse()+"]", e);
		}
	}

	/**
	 * Returns the name and location of the database that this objects operates on.
	 *  
	 * @see nl.nn.adapterframework.core.HasPhysicalDestination#getPhysicalDestinationName()
	 */
	public String getPhysicalDestinationName() {
		String result="unknown";
		try {
			Connection connection = getConnection();
			DatabaseMetaData metadata = connection.getMetaData();
			result = metadata.getURL();
	
			String catalog=null;
			catalog=connection.getCatalog();
			result += catalog!=null ? ("/"+catalog):"";
			connection.close();
		} catch (Exception e) {
			log.warn(getLogPrefix()+"exception retrieving PhysicalDestinationName", e);		
		}
		return result;
	}

	protected void applyParameters(PreparedStatement statement, ParameterValueList parameters) throws SQLException, SenderException {
		// statement.clearParameters();
	
/*
		// getParameterMetaData() is not supported on the WebSphere java.sql.PreparedStatement implementation.
		int senderParameterCount = parameters.size();
		int statementParameterCount = statement.getParameterMetaData().getParameterCount();
		if (statementParameterCount<senderParameterCount) {
			throw new SenderException(getLogPrefix()+"statement has more ["+statementParameterCount+"] parameters defined than sender ["+senderParameterCount+"]");
		}
*/		
		
		for (int i=0; i< parameters.size(); i++) {
			String parameterValue = (String)parameters.getParameterValue(i).getValue();
	//		log.debug("applying parameter ["+(i+1)+","+parameters.getParameterValue(i).getDefinition().getName()+"], value["+parameterValue+"]");
			statement.setString(i+1, parameterValue);
		}
	}
	


	/**
	 * Sets the name of the object.
	 */
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	/**
	 * Sets the JNDI name of datasource that is used when {@link #isTransacted()} returns <code>false</code> 
	 */
	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}
	public String getDatasourceName() {
		return datasourceName;
	}
	/**
	 * Sets the JNDI name of datasource that is used when {@link #isTransacted()} returns <code>true</code> 
	 */
	public void setDatasourceNameXA(String datasourceNameXA) {
		this.datasourceNameXA = datasourceNameXA;
	}
	public String getDatasourceNameXA() {
		return datasourceNameXA;
	}

	/**
	 * Sets the user name that is used to open the database connection.
	 * If a value is set, it will be used together with the (@link #setPassword(String) specified password} 
	 * to open the connection to the database.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the password that is used to open the database connection.
	 * @see #setPassword(String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	protected String getPassword() {
		return password;
	}

	/**
	 * controls the use of transactions
	 */
	public void setTransacted(boolean transacted) {
		this.transacted = transacted;
	}
	public boolean isTransacted() {
		return transacted;
	}

	public boolean isConnectionsArePooled() {
		return connectionsArePooled || isTransacted();
	}
	public void setConnectionsArePooled(boolean b) {
		connectionsArePooled = b;
	}
	
}