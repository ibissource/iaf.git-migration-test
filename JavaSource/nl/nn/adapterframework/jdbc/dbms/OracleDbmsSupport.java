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
 * $Log: OracleDbmsSupport.java,v $
 * Revision 1.7  2012-04-17 11:51:14  peter
 * The syntax "NOWAIT SKIP LOCKED" is not accepted in Oracle 11.2. The "NOWAIT" keyword and "SKIP LOCKED" keyword have become mutually exclusive. Remove the "NOWAIT" if you want to use "SKIP LOCKED".
 *
 * Revision 1.6  2011/11/30 13:51:45  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:47  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2011/10/04 09:54:55  gerrit
 * added getDbmsName()
 *
 * Revision 1.3  2011/08/09 08:07:30  gerrit
 * added getSchema(), isTablePresent() and isTableColumnPresent()
 *
 * Revision 1.2  2011/04/13 08:45:23  gerrit
 * Blob and Clob support using DbmsSupport
 *
 * Revision 1.1  2011/03/16 16:47:26  gerrit
 * introduction of DbmsSupport, including support for MS SQL Server
 *
 */
package nl.nn.adapterframework.jdbc.dbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import nl.nn.adapterframework.jdbc.JdbcException;
import nl.nn.adapterframework.util.JdbcUtil;

import org.apache.commons.lang.StringUtils;

/**
 * @author  Gerrit van Brakel
 * @since  
 * @version $Id$
 */
public class OracleDbmsSupport extends GenericDbmsSupport {

	public int getDatabaseType() {
		return DbmsSupportFactory.DBMS_ORACLE;
	}

	public String getDbmsName() {
		return "Oracle";
	}

	public String getSysDate() {
		return "SYSDATE";
	}

	public String getNumericKeyFieldType() {
		return "NUMBER(10)";
	}

	public String getAutoIncrementKeyFieldType() {
		return "NUMBER(10)";
	}
	
	public boolean autoIncrementKeyMustBeInserted() {
		return true;
	}

	public String autoIncrementInsertValue(String sequenceName) {
		return sequenceName+".NEXTVAL";
	}

	public boolean autoIncrementUsesSequenceObject() {
		return true;
	}

	public String getInsertedAutoIncrementValueQuery(String sequenceName) {
		return "SELECT "+sequenceName+".CURRVAL FROM DUAL";
	}

	public String getTimestampFieldType() {
		return "TIMESTAMP";
	}

	public String getBlobFieldType() {
		return "BLOB";
	}

	public boolean mustInsertEmptyBlobBeforeData() {
		return true;
	}
	public String getUpdateBlobQuery(String table, String blobField, String keyField) {
		return "SELECT "+blobField+ " FROM "+table+ " WHERE "+keyField+"=?"+ " FOR UPDATE";	
	}

	public String emptyBlobValue() {
		return "empty_blob()";
	}
	public String getTextFieldType() {
		return "VARCHAR2";
	}
	
	
	public String prepareQueryTextForWorkQueueReading(int batchSize, String selectQuery) throws JdbcException {
		if (StringUtils.isEmpty(selectQuery) || !selectQuery.toLowerCase().startsWith(KEYWORD_SELECT)) {
			throw new JdbcException("query ["+selectQuery+"] must start with keyword ["+KEYWORD_SELECT+"]");
		}
			/*
			 * see:
			 * http://www.psoug.org/reference/deadlocks.html
			 * http://www.psoug.org/reference/select.html
			 * http://www.ss64.com/ora/select.html
			 * http://forums.oracle.com/forums/thread.jspa?threadID=664986
			 */
			return selectQuery+" FOR UPDATE SKIP LOCKED";
	}

	public String provideIndexHintAfterFirstKeyword(String tableName, String indexName) {
		return " /*+ INDEX ( "+tableName+ " "+indexName+" ) */ "; 
	}

	public String provideFirstRowsHintAfterFirstKeyword(int rowCount) {
		return " /*+ FIRST_ROWS( "+rowCount+" ) */ "; 
	}

	public void updateClob(ResultSet rs, int column, Object clobUpdateHandle) throws SQLException {
		// updateClob is not required for Oracle
		// rs.updateClob(column, (Clob)clobUpdateHandle);
	}
	public void updateClob(ResultSet rs, String column, Object clobUpdateHandle) throws SQLException {
		// updateClob is not required for Oracle
		// rs.updateClob(column, (Clob)clobUpdateHandle);
	}

	public void updateBlob(ResultSet rs, int column, Object blobUpdateHandle) throws SQLException {
		// updateBlob is not required for Oracle
		// rs.updateBlob(column, (Blob)blobUpdateHandle);
	}
	public void updateBlob(ResultSet rs, String column, Object blobUpdateHandle) throws SQLException {
		// updateBlob is not required for Oracle
		// rs.updateBlob(column, (Blob)blobUpdateHandle);
	}

	public String getSchema(Connection conn) throws JdbcException {
		return JdbcUtil.executeStringQuery(conn, "SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM DUAL");
	}

	
	public boolean isTablePresent(Connection conn, String schemaName, String tableName) throws JdbcException {
		return doIsTablePresent(conn, "all_tables", "owner", "table_name", schemaName, tableName);
	}

	public boolean isTableColumnPresent(Connection conn, String schemaName, String tableName, String columnName) throws JdbcException {
		return doIsTableColumnPresent(conn, "all_tab_columns", "owner", "table_name", "column_name", schemaName, tableName, columnName);
	}

}
