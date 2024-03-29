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
 * $Log: JdbcQueryListener.java,v $
 * Revision 1.9  2011-11-30 13:51:43  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.7  2011/03/16 16:42:40  gerrit
 * introduction of DbmsSupport, including support for MS SQL Server
 *
 * Revision 1.6  2010/11/22 13:09:02  peter
 * replaced configuration warning "has no updateStatusToErrorQuery specified, will use updateStatusToProcessedQuery instead" by info logging message
 *
 * Revision 1.5  2009/08/04 11:24:21  gerrit
 * support for messages in CLOBs and BLOBs
 *
 * Revision 1.4  2008/12/30 17:01:12  peter
 * added configuration warnings facility (in Show configurationStatus)
 *
 * Revision 1.3  2008/12/10 08:35:55  gerrit
 * improved locking and selection mechanism: now works in multiple threads. 
 * improved disaster recovery: no more specific 'in process' status, rolls back to original state (where apropriate)
 *
 * Revision 1.2  2008/06/19 08:10:55  gerrit
 * set default updateStatusToError-query to updateStatusToProcessed
 *
 * Revision 1.1  2008/02/28 16:22:45  gerrit
 * first version
 *
 */
package nl.nn.adapterframework.jdbc;

import org.apache.commons.lang.StringUtils;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.configuration.ConfigurationWarnings;

/**

/**
 * Database Listener that operates on a table having at least a key and a status field.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.jdbc.JdbcQueryListener</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td>  <td>name of the listener</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setKeyField(String) keyField}</td>  <td>primary key field of the table, used to identify messages</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMessageField(String) messageField}</td>  <td>(optional) field containing the message data</td><td><i>same as keyField</i></td></tr>
 * <tr><td>{@link #setMessageFieldType(String) messageFieldType}</td>  <td>type of the field containing the message data: either String, clob or blob</td><td><i>String</i></td></tr>
 * <tr><td>{@link #setBlobCharset(String) blobCharset}</td><td>charset used to read BLOBs</td><td>UTF-8</td></tr>
 * <tr><td>{@link #setBlobsCompressed(boolean) blobsCompressed}</td><td>controls whether blobdata is considered stored compressed in the database</td><td>true</td></tr>
 * <tr><td>{@link #setBlobSmartGet(boolean) blobSmartGet}</td><td>controls automatically whether blobdata is stored compressed and/or serialized in the database</td><td>false</td></tr>

 * <tr><td>{@link #setSelectQuery(String) selectQuery}</td> <td>query that returns a row to be processed. Must contain a key field and optionally a message field</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setUpdateStatusToProcessedQuery(String) updateStatusToProcessedQuery}</td> <td>SQL Statement to the status of a row to 'processed'. Must contain one parameter, that is set to the value of the key</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setUpdateStatusToErrorQuery(String) updateStatusToErrorQuery}</td> <td>SQL Statement to the status of a row to 'error'. Must contain one parameter, that is set to the value of the key</td><td>same as <code>updateStatusToProcessedQuery</code></td></tr>

 * <tr><td>{@link #setDatasourceName(String) datasourceName}</td><td>can be configured from JmsRealm, too</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setUsername(String) username}</td><td>username used to connect to datasource</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setPassword(String) password}</td><td>password used to connect to datasource</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setConnectionsArePooled(boolean) connectionsArePooled}</td><td>when true, it is assumed that an connectionpooling mechanism is present. Before a message is sent, a new connection is obtained, that is closed after the message is sent. When transacted is true, connectionsArePooled is true, too</td><td>true</td></tr>
 * <tr><td>{@link #setJmsRealm(String) jmsRealm}</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * </table>
 * @author  Gerrit van Brakel
 * @since   4.7
 * @version $Id$
 */
public class JdbcQueryListener extends JdbcListener {

	public void configure() throws ConfigurationException {
		if (StringUtils.isEmpty(getSelectQuery())) {
			throw new ConfigurationException("selectQuery must be specified");
		}
		if (StringUtils.isEmpty(getUpdateStatusToProcessedQuery())) {
			throw new ConfigurationException("updateStatusToProcessedQuery must be specified");
		}
		if (StringUtils.isEmpty(getKeyField())) {
			throw new ConfigurationException("keyField must be specified");
		}
		if (StringUtils.isEmpty(getUpdateStatusToErrorQuery())) {
			log.info(getLogPrefix()+"has no updateStatusToErrorQuery specified, will use updateStatusToProcessedQuery instead");
			setUpdateStatusToErrorQuery(getUpdateStatusToProcessedQuery());
		}
		super.configure();
	}
	

	public void setKeyField(String fieldname) {
		super.setKeyField(fieldname);
	}

	public void setMessageField(String fieldname) {
		super.setMessageField(fieldname);
	}

	public void setSelectQuery(String string) {
		super.setSelectQuery(string);
	}

	public void setUpdateStatusToErrorQuery(String string) {
		super.setUpdateStatusToErrorQuery(string);
	}

	public void setUpdateStatusToProcessedQuery(String string) {
		super.setUpdateStatusToProcessedQuery(string);
	}


}
