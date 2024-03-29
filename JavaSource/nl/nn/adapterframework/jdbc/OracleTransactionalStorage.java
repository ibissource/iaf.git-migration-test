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
 * $Log: OracleTransactionalStorage.java,v $
 * Revision 1.16  2011-11-30 13:51:43  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.14  2010/01/06 15:18:45  peter
 * added type to index ix_ibisstore
 *
 * Revision 1.13  2008/12/30 17:01:12  peter
 * added configuration warnings facility (in Show configurationStatus)
 *
 * Revision 1.12  2007/12/27 16:02:55  gerrit
 * add grant to SYS.PENDING_TRANSACTIONS to create DDL-comments
 *
 * Revision 1.11  2007/12/10 10:06:28  gerrit
 * modified SQL creation scripts
 *
 * Revision 1.10  2007/06/12 11:20:57  gerrit
 * added depreciation warning
 *
 * Revision 1.9  2007/05/23 09:13:17  gerrit
 * incorporated functionality of OracleTransactionalStorage
 * in basic JdbcTransactionalStorage
 *
 * Revision 1.8  2006/12/12 09:57:36  gerrit
 * restore jdbc package
 *
 * Revision 1.6  2005/12/28 08:43:48  gerrit
 * corrected javadoc
 *
 * Revision 1.5  2005/10/26 13:34:21  gerrit
 * updated JavaDoc
 *
 * Revision 1.4  2005/10/18 07:18:07  gerrit
 * use generic blob functions
 *
 * Revision 1.3  2005/09/07 15:37:07  gerrit
 * updated javadoc
 *
 * Revision 1.2  2005/08/24 15:49:27  gerrit
 * use getObject from superclass
 *
 * Revision 1.1  2005/08/18 13:31:19  gerrit
 * JdbcTransactionalStorate descender-class for Oracle
 *
 */
package nl.nn.adapterframework.jdbc;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.configuration.ConfigurationWarnings;

/**
 * Oracle implementation of {@link ITransactionalStorage}.
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.jdbc.JdbcTransactionalStorage</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSlotId(String) slotId}</td><td>optional identifier for this storage, to be able to share the physical table between a number of receivers</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setDatasourceNameXA(String) datasourceNameXA}</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setUsername(String) username}</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setPassword(String) password}</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setJmsRealm(String) jmsRealm}</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setTableName(String) tableName}</td><td>the name of the table messages are stored in</td><td>ibisstore</td></tr>
 * <tr><td>{@link #setCreateTable(boolean) createTable}</td><td>when set to <code>true</code>, the table is created if it does not exist</td><td>false</td></tr>
 * <tr><td>{@link #setKeyField(String) keyField}</td><td>the name of the column that contains the primary key of the table</td><td>messageKey</td></tr>
 * <tr><td>{@link #setIdField(String) idField}</td><td>the name of the column messageids are stored in</td><td>messageId</td></tr>
 * <tr><td>{@link #setCorrelationIdField(String) correlationIdField}</td><td>the name of the column correlation-ids are stored in</td><td>correlationId</td></tr>
 * <tr><td>{@link #setDateField(String) dateField}</td><td>the name of the column the timestamp is stored in</td><td>messageDate</td></tr>
 * <tr><td>{@link #setMessageField(String) messageField}</td><td>the name of the column message themselves are stored in</td><td>message</td></tr>
 * <tr><td>{@link #setSlotIdField(String) slotIdField}</td><td>the name of the column slotIds are stored in</td><td>slotId</td></tr>
 * <tr><td>{@link #setKeyFieldType(String) keyFieldType}</td><td>the type of the column that contains the primary key of the table</td><td>NUMBER(10)</td></tr>
 * <tr><td>{@link #setDateFieldType(String) dateFieldType}</td><td>the type of the column the timestamp is stored in</td><td>TIMESTAMP</td></tr>
 * <tr><td>{@link #setTextFieldType(String) textFieldType}</td><td>the type of the columns messageId and correlationId, slotId and comments are stored in. N.B. (100) is appended for id's, (1000) is appended for comments.</td><td>VARCHAR2</td></tr>
 * <tr><td>{@link #setMessageFieldType(String) messageFieldType}</td><td>the type of the column message themselves are stored in</td><td>BLOB</td></tr>
 * <tr><td>{@link #setSequenceName(String) sequenceName}</td><td>the name of the sequence used to generate the primary key</td><td>ibisstore_seq</td></tr>
 * </table>
 * </p>
 * 
 * The default uses the following objects:
 *  <pre>
	CREATE TABLE IBISSTORE
	(
	MESSAGEKEY NUMBER(10),
	TYPE CHAR(1 CHAR),
	SLOTID VARCHAR2(100 CHAR),
	HOST VARCHAR2(100 CHAR),
	MESSAGEID VARCHAR2(100 CHAR),
	CORRELATIONID VARCHAR2(100 CHAR),
	MESSAGEDATE TIMESTAMP(6),
	COMMENTS VARCHAR2(1000 CHAR),
	MESSAGE BLOB,
	CONSTRAINT PK_IBISSTORE PRIMARY KEY (MESSAGEKEY)
	);
	
	CREATE INDEX IX_IBISSTORE ON IBISSTORE (TYPE, SLOTID, MESSAGEDATE);
	CREATE SEQUENCE SEQ_IBISSTORE;

	GRANT DELETE, INSERT, SELECT, UPDATE ON IBISSTORE TO <rolenaam>;
	GRANT SELECT ON SEQ_IBISSTORE TO <rolenaam>;
	GRANT SELECT ON SYS.DBA_PENDING_TRANSACTIONS TO <rolenaam>;
 *  </pre>
 * If these objects do not exist, Ibis will try to create them if the attribute createTable="true".
 * 
 * @version $Id$
 * @author  Gerrit van Brakel
 * @since 	4.3
 * @deprecated The functionality of the OracleTransactionalStorage has been incorporated in de JdbcTransactionalStorage.
 */
public class OracleTransactionalStorage extends JdbcTransactionalStorage {
	public static final String version = "$RCSfile: OracleTransactionalStorage.java,v $ $Revision: 1.16 $ $Date: 2011-11-30 13:51:43 $";

	public void configure() throws ConfigurationException {
		ConfigurationWarnings configWarnings = ConfigurationWarnings.getInstance();
		String msg = "Class OracleTransactionalStorage is no longer maintained. Please replace with JdbcTransactionalStorage";
		configWarnings.add(log, msg);
		super.configure();
	}



//	private String sequenceName="ibisstore_seq";
//		
//	protected String updateBlobQuery;		
//		
//	public OracleTransactionalStorage() {
//		super();
//		setKeyFieldType("NUMBER(10)");
//		setDateFieldType("TIMESTAMP");
//		setTextFieldType("VARCHAR2");
//		setMessageFieldType("BLOB");
//	}
//	    
//	protected String getLogPrefix() {
//		return "OracleTransactionalStorage ["+getName()+"] ";
//	}
//	
//
//
//	
//	protected void createQueryTexts() {
//		super.createQueryTexts(); 
//		insertQuery = "INSERT INTO "+getTableName()+" ("+
//						getKeyField()+","+
//						(StringUtils.isNotEmpty(getSlotId())?getSlotIdField()+",":"")+
//						getIdField()+","+getCorrelationIdField()+","+getDateField()+","+getCommentField()+","+getMessageField()+
//						") VALUES ("+getSequenceName()+".NEXTVAL,"+
//						(StringUtils.isNotEmpty(getSlotId())?"'"+
//						getSlotId()+"',":"")+"?,?,?,?,empty_blob())";
//		updateBlobQuery = "SELECT "+getKeyField()+","+getMessageField()+
//						  " FROM "+getTableName()+
//						  getWhereClause(getIdField()+"=?"+
//						  " AND " +getCorrelationIdField()+"=?"+
//						  " AND "+getDateField()+"=?")+
//						  " FOR UPDATE";
//	}

//	protected void createStorage(Connection conn, Statement stmt) throws JdbcException {
//		super.createStorage(conn,stmt);
//		String query=null;
//		try {
//			query="CREATE SEQUENCE "+getSequenceName()+" START WITH 1 INCREMENT BY 1";
//			log.debug(getLogPrefix()+"creating sequence for table ["+getTableName()+"] using query ["+query+"]");
//			stmt.execute(query);
//		} catch (SQLException e) {
//			throw new JdbcException(getLogPrefix()+" executing query ["+query+"]", e);
//		}
//	}	

//
//	protected String storeMessageInDatabase(Connection conn, String messageId, String correlationId, Timestamp receivedDateTime, String comments, Serializable message) throws IOException, SenderException, JdbcException, SQLException {
//		PreparedStatement stmt = null;
//		try { 
//			log.debug("preparing insert statement ["+insertQuery+"]");
//			stmt = conn.prepareStatement(insertQuery);			
//			stmt.clearParameters();
//			stmt.setString(1,messageId);
//			stmt.setString(2,correlationId);
//			stmt.setTimestamp(3, receivedDateTime);
//			stmt.setString(4, comments);
//			stmt.execute();
//
//			// now retrieve the key and update the blob
//			log.debug("preparing update statement ["+updateBlobQuery+"]");
//			stmt = conn.prepareStatement(updateBlobQuery);			
//			stmt.clearParameters();
//			stmt.setString(1,messageId);
//			stmt.setString(2,correlationId);
//			stmt.setTimestamp(3, receivedDateTime);
//
//			ResultSet rs = null;
//			try {
//				rs = stmt.executeQuery();
//				if (!rs.next()) {
//					throw new SenderException("could not retrieve row for stored message ["+ messageId+"]");
//				}
//				String newKey = rs.getString(1);
////				BLOB blob = (BLOB)rs.getBlob(2);
//				OutputStream outstream = JdbcUtil.getBlobUpdateOutputStream(rs,2);
//				ObjectOutputStream oos = new ObjectOutputStream(outstream);
//				oos.writeObject(message);
//				oos.close();
//				outstream.close();
//				return newKey;
//				
//			} finally {
//				if (rs!=null) {
//					rs.close();
//				}
//			}
//		} finally {
//			if (stmt!=null) {
//				stmt.close();
//			}
//		}
//	}
//
//


}
