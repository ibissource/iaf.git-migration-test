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
 * $Log: IDbmsSupport.java,v $
 * Revision 1.6  2011-11-30 13:51:45  peter
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
 * Revision 1.2  2011/04/13 08:43:00  gerrit
 * Blob and Clob support using DbmsSupport
 *
 * Revision 1.1  2011/03/16 16:47:26  gerrit
 * introduction of DbmsSupport, including support for MS SQL Server
 *
 */
package nl.nn.adapterframework.jdbc.dbms;

import java.io.OutputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import nl.nn.adapterframework.jdbc.JdbcException;

/**
 * Interface to define DBMS specific SQL implementations.
 * 
 * @author  Gerrit van Brakel
 * @since  
 * @version $Id$
 */
public interface IDbmsSupport {

	/**
	 * Numeric value defining database type, defined in {@link DbmsSupportFactory}.
	 */
	int getDatabaseType(); 
	String getDbmsName();
	
	/**
	 * SQL String returning current date and time of dbms.
	 */
	String getSysDate();

	String getNumericKeyFieldType();
	
	String getAutoIncrementKeyFieldType();
	boolean autoIncrementKeyMustBeInserted();
	String autoIncrementInsertValue(String sequenceName);
	boolean autoIncrementUsesSequenceObject();
	String getInsertedAutoIncrementValueQuery(String sequenceName);

	String getTimestampFieldType();

	String getClobFieldType();
	boolean mustInsertEmptyClobBeforeData();
	String emptyClobValue();
	String getUpdateClobQuery(String table, String clobField, String keyField);
	Object getClobUpdateHandle(ResultSet rs, int column) throws SQLException, JdbcException;
	Object getClobUpdateHandle(ResultSet rs, String column) throws SQLException, JdbcException;
	Writer getClobWriter(ResultSet rs, int column, Object clobUpdateHandle) throws SQLException, JdbcException;
	Writer getClobWriter(ResultSet rs, String column, Object clobUpdateHandle) throws SQLException, JdbcException;
	void updateClob(ResultSet rs, int column, Object clobUpdateHandle) throws SQLException, JdbcException;
	void updateClob(ResultSet rs, String column, Object clobUpdateHandle) throws SQLException, JdbcException;

	String getBlobFieldType();
	boolean mustInsertEmptyBlobBeforeData();
	String emptyBlobValue();
	String getUpdateBlobQuery(String table, String clobField, String keyField);
	Object getBlobUpdateHandle(ResultSet rs, int column) throws SQLException, JdbcException;
	Object getBlobUpdateHandle(ResultSet rs, String column) throws SQLException, JdbcException;
	OutputStream getBlobOutputStream(ResultSet rs, int column, Object blobUpdateHandle) throws SQLException, JdbcException;
	OutputStream getBlobOutputStream(ResultSet rs, String column, Object blobUpdateHandle) throws SQLException, JdbcException;
	void updateBlob(ResultSet rs, int column, Object blobUpdateHandle) throws SQLException, JdbcException;
	void updateBlob(ResultSet rs, String column, Object blobUpdateHandle) throws SQLException, JdbcException;

	
	String getTextFieldType();
	
	String prepareQueryTextForWorkQueueReading(int batchSize, String selectQuery) throws JdbcException;

	String provideIndexHintAfterFirstKeyword(String tableName, String indexName);
	String provideFirstRowsHintAfterFirstKeyword(int rowCount);
	String provideTrailingFirstRowsHint(int rowCount);

	String getSchema(Connection conn) throws JdbcException;
 
	boolean isTablePresent(Connection conn, String schemaName, String tableName) throws JdbcException;
	boolean isTableColumnPresent(Connection conn, String schemaName, String tableName, String columnName) throws JdbcException;

}
