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
 * $Log: JdbcUtil.java,v $
 * Revision 1.34  2012-02-17 18:04:02  jaco
 * Use proxiedDataSources for JdbcIteratingPipeBase too
 * Call close on original/proxied connection instead of connection from statement that might be the unproxied connection
 *
 * Revision 1.33  2011/11/30 13:51:49  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.31  2011/09/16 07:41:50  martijno
 * renamed some local variables in streamBlob to less confusing names.
 *
 * Revision 1.30  2011/09/15 11:53:58  martijno
 * added streamBlob' base64 encoding/decoding
 *
 * 
 * Revision 1.29  2011/08/09 10:10:43  gerrit
 * moved isTablePresent() and isTableColumnPresent() to DbmsSupport
 * added isBlobType(), isClobType(), getValue(), streamBlob, streamClob
 * replaced base64 library
 *
 * Revision 1.28  2011/04/13 08:49:04  gerrit
 * Blob and Clob support using DbmsSupport
 *
 * Revision 1.27  2011/03/16 16:38:13  gerrit
 * moved detection of databaseType to DbmsSupportFactory
 *
 * Revision 1.26  2010/12/20 10:39:47  gerrit
 * added hasIndexOnColumn() and hasIndexOnColumns()
 *
 * Revision 1.25  2010/09/10 11:42:44  gerrit
 * improved error  handling for tableExists()
 *
 * Revision 1.24  2010/07/12 12:25:38  gerrit
 * improved debug message
 *
 * Revision 1.23  2010/02/11 14:22:50  peter
 * added several methods for checking IBISSTORE
 *
 * Revision 1.22  2009/11/17 09:04:12  peter
 * blobSmartGet: fixed bug for MessageLog blobs
 *
 * Revision 1.21  2009/08/04 11:31:30  gerrit
 * support for CLOBs and BLOBs by name
 * additional applyParameters and displayParameters methods
 *
 * Revision 1.20  2009/06/03 14:16:37  peter
 * fixed bug that caused a loop in QuerySender when getBlobSmart=true
 *
 * Revision 1.19  2009/03/03 14:34:41  gerrit
 * added putByteArrayAsBlob()
 *
 * Revision 1.18  2008/10/20 13:02:26  peter
 * also show not compressed blobs and not serialized blobs
 *
 * Revision 1.17  2008/08/27 16:23:44  gerrit
 * added columnExists
 *
 * Revision 1.16  2008/06/19 15:14:14  gerrit
 * added inputstream and outputstream methods for blobs
 *
 * Revision 1.15  2007/09/12 09:27:36  gerrit
 * added warning in fullClose()
 *
 * Revision 1.14  2007/09/05 13:06:47  gerrit
 * avoid NPE when putting null BLOBs and CLOBs
 *
 * Revision 1.13  2007/07/26 16:25:03  gerrit
 * added fullClose()
 *
 * Revision 1.12  2007/07/19 15:14:11  gerrit
 * handle charsets of BLOB and CLOB streams correctly
 *
 * Revision 1.11  2007/02/12 14:12:03  gerrit
 * Logger from LogUtil
 *
 * Revision 1.10  2006/12/13 16:33:03  gerrit
 * added blobCharset attribute
 *
 * Revision 1.9  2005/12/29 15:34:00  gerrit
 * added support for clobs
 *
 * Revision 1.8  2005/10/19 11:37:48  gerrit
 * removed cause from warning, due to ' unresolved compilation problems'
 *
 * Revision 1.7  2005/10/17 11:25:35  gerrit
 * added code to handel blobs and warnings
 *
 * Revision 1.6  2005/08/24 15:55:57  gerrit
 * added getBlobInputStream()
 *
 * Revision 1.5  2005/08/18 13:37:22  gerrit
 * corrected version String
 *
 * Revision 1.4  2005/08/18 13:36:09  gerrit
 * rework using prepared statement
 * close() finally
 *
 * Revision 1.3  2004/03/26 10:42:42  johan
 * added @version tag in javadoc
 *
 * Revision 1.2  2004/03/25 13:36:07  gerrit
 * table exists via count(*) rather then via metadata
 *
 * Revision 1.1  2004/03/23 17:16:14  gerrit
 * initial version
 *
 */
package nl.nn.adapterframework.util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import nl.nn.adapterframework.core.IMessageWrapper;
import nl.nn.adapterframework.jdbc.JdbcException;
import nl.nn.adapterframework.jdbc.dbms.DbmsSupportFactory;
import nl.nn.adapterframework.jdbc.dbms.IDbmsSupport;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.log4j.Logger;

/**
 * Database-oriented utility functions.
 * 
 * @author  Gerrit van Brakel
 * @since   4.1
 * @version $Id$
 */
public class JdbcUtil {
	protected static Logger log = LogUtil.getLogger(JdbcUtil.class);

	private static final boolean useMetaDataForTableExists=false;
	/**
	 * @return true if tableName exists in database in this connection
	 */
	public static boolean tableExists(Connection conn, String tableName ) throws SQLException {
		
		PreparedStatement stmt = null;
		if (useMetaDataForTableExists) {
			DatabaseMetaData dbmeta = conn.getMetaData();
			ResultSet tableset = dbmeta.getTables(null, null, tableName, null);
			return !tableset.isAfterLast();
		} 
		String query=null;
		try {
			query="select count(*) from "+tableName;
			log.debug("create statement to check for existence of ["+tableName+"] using query ["+query+"]");
			stmt = conn.prepareStatement(query);
			log.debug("execute statement");
			ResultSet rs = stmt.executeQuery();
			log.debug("statement executed");
			rs.close();
			return true;
		} catch (SQLException e) {
			if (log.isDebugEnabled()) log.debug("exception checking for existence of ["+tableName+"] using query ["+query+"]", e);
			return false;
		} finally {
			if (stmt!=null) {
				stmt.close();
			}
		}
	}
	
	public static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
		PreparedStatement stmt = null;
		String query=null;
		try {
			query = "SELECT count(" + columnName + ") FROM " + tableName;
			stmt = conn.prepareStatement(query);

			ResultSet rs = null;
			try {
				rs = stmt.executeQuery();
				return true;
			} catch (SQLException e) {
				if (log.isDebugEnabled()) log.debug("exception checking for existence of column ["+columnName+"] in table ["+tableName+"] executing query ["+query+"]", e);
				return false;
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
		} catch (SQLException e) {
			log.warn("exception checking for existence of column ["+columnName+"] in table ["+tableName+"] preparing query ["+query+"]", e);
			return false;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static boolean isIndexPresent(Connection conn, int databaseType, String schemaOwner, String tableName, String indexName) {
		if (databaseType==DbmsSupportFactory.DBMS_ORACLE) {
			String query="select count(*) from all_indexes where owner='"+schemaOwner.toUpperCase()+"' and table_name='"+tableName.toUpperCase()+"' and index_name='"+indexName.toUpperCase()+"'";
			try {
				if (JdbcUtil.executeIntQuery(conn, query)>=1) {
					return true;
				} 
				return false;
			} catch (Exception e) {
				log.warn("could not determine presence of index ["+indexName+"] on table ["+tableName+"]",e);
				return false;
			}
		} 
		log.warn("could not determine presence of index ["+indexName+"] on table ["+tableName+"] (not an Oracle database)");
		return true;
	}

	public static boolean isSequencePresent(Connection conn, int databaseType, String schemaOwner, String sequenceName) {
		if (databaseType==DbmsSupportFactory.DBMS_ORACLE) {
			String query="select count(*) from all_sequences where sequence_owner='"+schemaOwner.toUpperCase()+"' and sequence_name='"+sequenceName.toUpperCase()+"'";
			try {
				if (JdbcUtil.executeIntQuery(conn, query)>=1) {
					return true;
				} 
				return false;
			} catch (Exception e) {
				log.warn("could not determine presence of sequence ["+sequenceName+"]",e);
				return false;
			}
		} 
		log.warn("could not determine presence of sequence ["+sequenceName+"] (not an Oracle database)");
		return true;
	}


	public static boolean isIndexColumnPresent(Connection conn, int databaseType, String schemaOwner, String tableName, String indexName, String columnName) {
		if (databaseType==DbmsSupportFactory.DBMS_ORACLE) {
			String query="select count(*) from all_ind_columns where index_owner='"+schemaOwner.toUpperCase()+"' and table_name='"+tableName.toUpperCase()+"' and index_name='"+indexName.toUpperCase()+"' and column_name=?";
			try {
				if (JdbcUtil.executeIntQuery(conn, query, columnName.toUpperCase())>=1) {
					return true;
				} 
				return false;
			} catch (Exception e) {
				log.warn("could not determine correct presence of column ["+columnName+"] of index ["+indexName+"] on table ["+tableName+"]",e);
				return false;
			}
		} 
		log.warn("could not determine correct presence of column ["+columnName+"] of index ["+indexName+"] on table ["+tableName+"] (not an Oracle database)");
		return true;
	}

	public static int getIndexColumnPosition(Connection conn, int databaseType, String schemaOwner, String tableName, String indexName, String columnName) {
		if (databaseType==DbmsSupportFactory.DBMS_ORACLE) {
			String query="select column_position from all_ind_columns where index_owner='"+schemaOwner.toUpperCase()+"' and table_name='"+tableName.toUpperCase()+"' and index_name='"+indexName.toUpperCase()+"' and column_name=?";
			try {
				return JdbcUtil.executeIntQuery(conn, query, columnName.toUpperCase());
			} catch (Exception e) {
				log.warn("could not determine correct presence of column ["+columnName+"] of index ["+indexName+"] on table ["+tableName+"]",e);
				return -1;
			}
		} 
		log.warn("could not determine correct presence of column ["+columnName+"] of index ["+indexName+"] on table ["+tableName+"] (not an Oracle database)");
		return -1;
	}

	public static boolean hasIndexOnColumn(Connection conn, int databaseType, String schemaOwner, String tableName, String columnName) {
		if (databaseType==DbmsSupportFactory.DBMS_ORACLE) {
			String query="select count(*) from all_ind_columns";
			query+=" where TABLE_OWNER='"+schemaOwner.toUpperCase()+"' and TABLE_NAME='"+tableName.toUpperCase()+"'";
			query+=" and column_name=?";
			query+=" and column_position=1";
			try {
				if (JdbcUtil.executeIntQuery(conn, query, columnName.toUpperCase())>=1) {
					return true;
				} 
				return false;
			} catch (Exception e) {
				log.warn("could not determine presence of index column ["+columnName+"] on table ["+tableName+"] using query ["+query+"]",e);
				return false;
			}
		} 
		log.warn("could not determine presence of index column ["+columnName+"] on table ["+tableName+"] (not an Oracle database)");
		return true;
	}
	public static boolean hasIndexOnColumns(Connection conn, int databaseType, String schemaOwner, String tableName, List columns) {
		if (databaseType==DbmsSupportFactory.DBMS_ORACLE) {
			String query="select count(*) from all_indexes ai";
			for (int i=1;i<=columns.size();i++) {
				query+=", all_ind_columns aic"+i;
			}
			query+=" where ai.TABLE_OWNER='"+schemaOwner.toUpperCase()+"' and ai.TABLE_NAME='"+tableName.toUpperCase()+"'";
			for (int i=1;i<=columns.size();i++) {
				query+=" and ai.OWNER=aic"+i+".INDEX_OWNER";
				query+=" and ai.INDEX_NAME=aic"+i+".INDEX_NAME";
				query+=" and aic"+i+".column_name='"+((String)columns.get(i-1)).toUpperCase()+"'";
				query+=" and aic"+i+".column_position="+i;
			}
			try {
				if (JdbcUtil.executeIntQuery(conn, query)>=1) {
					return true;
				} 
				return false;
			} catch (Exception e) {
				log.warn("could not determine presence of index columns on table ["+tableName+"] using query ["+query+"]",e);
				return false;
			}
		} 
		log.warn("could not determine presence of index columns on table ["+tableName+"] (not an Oracle database)");
		return true;
	}

	public static String getSchemaOwner(Connection conn, int databaseType) throws SQLException, JdbcException  {
		if (databaseType==DbmsSupportFactory.DBMS_ORACLE) {
			String query="SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM DUAL";
			return executeStringQuery(conn, query);
		} 
		log.warn("could not determine current schema (not an Oracle database)");
		return "";
	}

	public static String warningsToString(SQLWarning warnings) {
		XmlBuilder warningsElem = warningsToXmlBuilder(warnings);
		if (warningsElem!=null) {
			return warningsElem.toXML();
		}
		return null;
	}

	public static void warningsToXml(SQLWarning warnings, XmlBuilder parent) {
		XmlBuilder warningsElem=warningsToXmlBuilder(warnings);
		if (warningsElem!=null) {
			parent.addSubElement(warningsElem);	
		}
	}
				
	public static XmlBuilder warningsToXmlBuilder(SQLWarning warnings) {	
		if (warnings!=null) {
			XmlBuilder warningsElem = new XmlBuilder("warnings");
			while (warnings!=null) {
				XmlBuilder warningElem = new XmlBuilder("warning"); 
				warningElem.addAttribute("errorCode",""+warnings.getErrorCode());
				warningElem.addAttribute("sqlState",""+warnings.getSQLState());
				String message=warnings.getMessage();
				
				// getCause() geeft unresolvedCompilationProblem (bij Peter Leeuwenburgh?)
 				Throwable cause=warnings.getCause();
				if (cause!=null) {
					warningElem.addAttribute("cause",cause.getClass().getName());
					if (message==null) {
						message=cause.getMessage();
					} else {
						message=message+": "+cause.getMessage();
					}
				}
				
				warningElem.addAttribute("message",message);
				warningsElem.addSubElement(warningElem);
				warnings=warnings.getNextWarning();
			}
			return warningsElem;
		}
		return null;
	}

	public static boolean isBlobType(final ResultSet rs, final int colNum, final ResultSetMetaData rsmeta) throws SQLException {
        switch(rsmeta.getColumnType(colNum))
        {
	        case Types.LONGVARBINARY :
	        case Types.VARBINARY :
			case Types.BLOB :
				return true;
			default:
				return false;
        }
	}

	public static boolean isClobType(final ResultSet rs, final int colNum, final ResultSetMetaData rsmeta) throws SQLException {
		switch (rsmeta.getColumnType(colNum)) {
			case Types.CLOB:
				return true;
			default:
				return false;
		}
	}
	
	public static String getValue(final ResultSet rs, final int colNum, final ResultSetMetaData rsmeta, String blobCharset, boolean decompressBlobs, String nullValue, boolean trimSpaces, boolean getBlobSmart, boolean encodeBlobBase64) throws JdbcException, IOException, SQLException {
        switch(rsmeta.getColumnType(colNum))
        {
	        case Types.LONGVARBINARY :
	        case Types.VARBINARY :
			case Types.BLOB :
				try {
					return JdbcUtil.getBlobAsString(rs,colNum,blobCharset,false,decompressBlobs,getBlobSmart,encodeBlobBase64);
				} catch (JdbcException e) {
					log.debug("Caught JdbcException, assuming no blob found",e);
					return nullValue;
				}
			case Types.CLOB :
				try {
					return JdbcUtil.getClobAsString(rs,colNum,false);
				} catch (JdbcException e) {
					log.debug("Caught JdbcException, assuming no clob found",e);
					return nullValue;
				}
        	// return "undefined" for types that cannot be rendered to strings easily
            case Types.ARRAY :
            case Types.DISTINCT :
            case Types.BINARY :
            case Types.REF :
            case Types.STRUCT :
                return "undefined";
            default :
            {
                String value = rs.getString(colNum);
                if (value == null) {
                    return nullValue;
                }
            	if (trimSpaces) {
            		return value.trim();
            	}
				return value;
            }
        }
    }
		
	
	public static InputStream getBlobInputStream(ResultSet rs, int columnIndex) throws SQLException, JdbcException {
		return getBlobInputStream(rs.getBlob(columnIndex),columnIndex+"");
	}
	public static InputStream getBlobInputStream(ResultSet rs, String columnName) throws SQLException, JdbcException {
		return getBlobInputStream(rs.getBlob(columnName),columnName);
	}
	public static InputStream getBlobInputStream(Blob blob, String column) throws SQLException, JdbcException {
		if (blob==null) {
			throw new JdbcException("no blob found in column ["+column+"]");
		}
		return blob.getBinaryStream();
	}

	public static InputStream getBlobInputStream(ResultSet rs, int columnIndex, boolean blobIsCompressed) throws SQLException, JdbcException {
		return getBlobInputStream(rs.getBlob(columnIndex),columnIndex+"",blobIsCompressed);
	}
	public static InputStream getBlobInputStream(ResultSet rs, String columnName, boolean blobIsCompressed) throws SQLException, JdbcException {
		return getBlobInputStream(rs.getBlob(columnName),columnName,blobIsCompressed);
	}
	public static InputStream getBlobInputStream(Blob blob, String column, boolean blobIsCompressed) throws SQLException, JdbcException {
		InputStream input = getBlobInputStream(blob,column);
		if (blobIsCompressed) {
			return new InflaterInputStream(input);
		} 
		return input;
	}

	public static Reader getBlobReader(final ResultSet rs, int columnIndex, String charset, boolean blobIsCompressed) throws IOException, JdbcException, SQLException {
		return getBlobReader(rs.getBlob(columnIndex),columnIndex+"",charset,blobIsCompressed);
	}
	public static Reader getBlobReader(final ResultSet rs, String columnName, String charset, boolean blobIsCompressed) throws IOException, JdbcException, SQLException {
		return getBlobReader(rs.getBlob(columnName),columnName,charset,blobIsCompressed);
	}
	public static Reader getBlobReader(Blob blob, String column, String charset, boolean blobIsCompressed) throws IOException, JdbcException, SQLException {
		Reader result;
		InputStream input = getBlobInputStream(blob,column);
		if (charset==null) {
			charset = Misc.DEFAULT_INPUT_STREAM_ENCODING;
		}
		if (blobIsCompressed) {
			result = new InputStreamReader(new InflaterInputStream(input), charset);
		} else {
			result = new InputStreamReader(input, charset);
		}
		return result;
	}

	public static void streamBlob(final ResultSet rs, int columnIndex, String charset, boolean blobIsCompressed, String blobBase64Direction, Object target, boolean close) throws JdbcException, SQLException, IOException {
		streamBlob(rs.getBlob(columnIndex),columnIndex+"",charset,blobIsCompressed,blobBase64Direction,target,close);
	}
	public static void streamBlob(final ResultSet rs, String columnName, String charset, boolean blobIsCompressed, String blobBase64Direction, Object target, boolean close) throws JdbcException, SQLException, IOException {
		streamBlob(rs.getBlob(columnName),columnName,charset,blobIsCompressed,blobBase64Direction,target,close);
	}
	
	public static void streamBlob(Blob blob, String column, String charset, boolean blobIsCompressed, String blobBase64Direction, Object target, boolean close) throws JdbcException, SQLException, IOException {
		if (target==null) {
			throw new JdbcException("cannot stream Blob to null object");
		}
		OutputStream outputStream=StreamUtil.getOutputStream(target);
		if (outputStream!=null) {
			InputStream inputStream = JdbcUtil.getBlobInputStream(blob, column, blobIsCompressed);
			if ("decode".equalsIgnoreCase(blobBase64Direction)){
				Base64InputStream base64DecodedStream = new Base64InputStream (inputStream);
				StreamUtil.copyStream(base64DecodedStream, outputStream, 50000);   					
			}
			else if ("encode".equalsIgnoreCase(blobBase64Direction)){
				Base64InputStream base64EncodedStream = new Base64InputStream (inputStream, true);
				StreamUtil.copyStream(base64EncodedStream, outputStream, 50000);   									
			}
			else {	
				StreamUtil.copyStream(inputStream, outputStream, 50000);
			}
			
			if (close) {
				outputStream.close();
			}
			return;
		}
		Writer writer = StreamUtil.getWriter(target);
		if (writer !=null) {
			Reader reader = JdbcUtil.getBlobReader(blob, column, charset, blobIsCompressed);
			StreamUtil.copyReaderToWriter(reader, writer, 50000, false, false);
			if (close) {
				writer.close();
			}
			return;
		}
		throw new IOException("cannot stream Blob to ["+target.getClass().getName()+"]");
	}

	public static void streamClob(final ResultSet rs, int columnIndex, Object target, boolean close) throws JdbcException, SQLException, IOException {
		streamClob(rs.getClob(columnIndex),columnIndex+"",target,close);
	}
	public static void streamClob(final ResultSet rs, String columnName, Object target, boolean close) throws JdbcException, SQLException, IOException {
		streamClob(rs.getClob(columnName),columnName,target,close);
	}
	
	public static void streamClob(Clob clob, String column, Object target, boolean close) throws JdbcException, SQLException, IOException {
		if (target==null) {
			throw new NullPointerException("cannot stream Clob to null object");
		}
		OutputStream outputStream=StreamUtil.getOutputStream(target);
		if (outputStream!=null) {
			InputStream inputstream = JdbcUtil.getClobInputStream(clob);
			StreamUtil.copyStream(inputstream, outputStream, 50000);
			if (close) {
				outputStream.close();
			}
			return;
		}
		Writer writer = StreamUtil.getWriter(target);
		if (writer !=null) {
			Reader reader = JdbcUtil.getClobReader(clob);
			StreamUtil.copyReaderToWriter(reader, writer, 50000, false, false);
			if (close) {
				writer.close();
			}
			return;
		}
		throw new IOException("cannot stream Clob to ["+target.getClass().getName()+"]");
	}
	
	public static String getBlobAsString(final ResultSet rs, int columnIndex, String charset, boolean xmlEncode, boolean blobIsCompressed) throws IOException, JdbcException, SQLException {
		return getBlobAsString(rs, columnIndex, charset, xmlEncode, blobIsCompressed, false, false);
	}

	public static String getBlobAsString(final ResultSet rs, int columnIndex, String charset, boolean xmlEncode, boolean blobIsCompressed, boolean blobSmartGet, boolean encodeBlobBase64) throws IOException, JdbcException, SQLException {
		return getBlobAsString(rs.getBlob(columnIndex),columnIndex+"",charset, xmlEncode, blobIsCompressed, blobSmartGet, encodeBlobBase64);
	}
	public static String getBlobAsString(final ResultSet rs, String columnName, String charset, boolean xmlEncode, boolean blobIsCompressed, boolean blobSmartGet, boolean encodeBlobBase64) throws IOException, JdbcException, SQLException {
		return getBlobAsString(rs.getBlob(columnName),columnName,charset, xmlEncode, blobIsCompressed, blobSmartGet, encodeBlobBase64);
	}
	public static String getBlobAsString(Blob blob, String column, String charset, boolean xmlEncode, boolean blobIsCompressed, boolean blobSmartGet, boolean encodeBlobBase64) throws IOException, JdbcException, SQLException {
		if (encodeBlobBase64) {
			InputStream blobStream = JdbcUtil.getBlobInputStream(blob, column, blobIsCompressed);
			return Misc.streamToString(new Base64InputStream(blobStream,true),null,false);
		}
		if (blobSmartGet) {
			if (blob==null) {
				log.debug("no blob found in column ["+column+"]");
				return null;
			}
			int bl = (int)blob.length();

			InputStream is = blob.getBinaryStream();
			byte[] buf = new byte[bl];
			int bl1 = is.read(buf);

			Inflater decompressor = new Inflater();
			decompressor.setInput(buf);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(buf.length);
			byte[] bufDecomp = new byte[1024];
			boolean decompresOK = true;
			while (!decompressor.finished()) {
				try {
					int count = decompressor.inflate(bufDecomp);
					if (count==0) {
						break;
					}
					bos.write(bufDecomp, 0, count);
				} catch (DataFormatException e) {
					log.debug("message in column ["+column+"] is not compressed");
					decompresOK = false;
					break;
				}
			}
			bos.close();
			if (decompresOK)
				buf = bos.toByteArray(); 

			Object result = null;
			ObjectInputStream ois = null;
			boolean objectOK = true;
			try {
				ByteArrayInputStream bis = new ByteArrayInputStream(buf);
				ois = new ObjectInputStream(bis);
				result = ois.readObject();
			} catch (Exception e) {
				log.debug("message in column ["+column+"] is probably not a serialized object: "+e.getClass().getName());
				objectOK=false;
			}
			if (ois!=null)
				ois.close();
		
			String rawMessage;
			if (objectOK) {
				if (result instanceof IMessageWrapper) {
					rawMessage = ((IMessageWrapper)result).getText();
				} else {
					rawMessage = (String)result;
				}
			} else {
				rawMessage = new String(buf,charset);
			}

			String message = XmlUtils.encodeCdataString(rawMessage);
			return message;
		} 
		return Misc.readerToString(getBlobReader(blob,column,charset,blobIsCompressed),null,xmlEncode);
	}

//	/**
//	 * retrieves an outputstream to a blob column from an updatable resultset.
//	 */
//	public static OutputStream getBlobUpdateOutputStream(IDbmsSupport dbmsSupport, Object blobUpdateHandle, ResultSet rs, int columnIndex) throws SQLException, JdbcException {
//		Blob blob = rs.getBlob(columnIndex);
//		if (blob==null) {
//			throw new JdbcException("no blob found in column ["+columnIndex+"]");
//		}
//		return blob.setBinaryStream(1L);
//	}

	public static OutputStream getBlobOutputStream(IDbmsSupport dbmsSupport, Object blobUpdateHandle, final ResultSet rs, int columnIndex, boolean compressBlob) throws IOException, JdbcException, SQLException {
		OutputStream result;
		OutputStream out = dbmsSupport.getBlobOutputStream(rs, columnIndex, blobUpdateHandle);
		if (compressBlob) {
			result = new DeflaterOutputStream(out);
		} else {
			result = out;
		}
		return result;	
	}

	public static Writer getBlobWriter(IDbmsSupport dbmsSupport, Object blobUpdateHandle, final ResultSet rs, int columnIndex, String charset, boolean compressBlob) throws IOException, JdbcException, SQLException {
		Writer result;
		OutputStream out = dbmsSupport.getBlobOutputStream(rs, columnIndex, blobUpdateHandle);
		if (charset==null) {
			charset = Misc.DEFAULT_INPUT_STREAM_ENCODING;
		}
		if (compressBlob) {
			result = new BufferedWriter(new OutputStreamWriter(new DeflaterOutputStream(out),charset));
		} else {
			result = new BufferedWriter(new OutputStreamWriter(out,charset));
		}
		return result;	
	}

	public static void putStringAsBlob(IDbmsSupport dbmsSupport, final ResultSet rs, int columnIndex, String content, String charset, boolean compressBlob) throws IOException, JdbcException, SQLException {
		if (content!=null) {
			Object blobHandle=dbmsSupport.getBlobUpdateHandle(rs, columnIndex);
			OutputStream out = dbmsSupport.getBlobOutputStream(rs, columnIndex, blobHandle);
			if (charset==null) {
				charset = Misc.DEFAULT_INPUT_STREAM_ENCODING;
			}
			if (compressBlob) {
				DeflaterOutputStream dos = new DeflaterOutputStream(out);
				dos.write(content.getBytes(charset));
				dos.close();
			} else {
				out.write(content.getBytes(charset));
			}
			out.close();
			dbmsSupport.updateBlob(rs, columnIndex, blobHandle);
		} else {
			log.warn("content to store in blob was null");
		}
	}

	public static void putByteArrayAsBlob(IDbmsSupport dbmsSupport, final ResultSet rs, int columnIndex, byte content[], boolean compressBlob) throws IOException, JdbcException, SQLException {
		if (content!=null) {
			Object blobHandle=dbmsSupport.getBlobUpdateHandle(rs, columnIndex);
			OutputStream out = dbmsSupport.getBlobOutputStream(rs, columnIndex, blobHandle);
			if (compressBlob) {
				DeflaterOutputStream dos = new DeflaterOutputStream(out);
				dos.write(content);
				dos.close();
			} else {
				out.write(content);
			}
			out.close();
			dbmsSupport.updateBlob(rs, columnIndex, blobHandle);
		} else {
			log.warn("content to store in blob was null");
		}
	}
	

	public static InputStream getClobInputStream(ResultSet rs, int columnIndex) throws SQLException, JdbcException {
		Clob clob = rs.getClob(columnIndex);
		if (clob==null) {
			throw new JdbcException("no clob found in column ["+columnIndex+"]");
		}
		return getClobInputStream(clob);
	}

	public static InputStream getClobInputStream(Clob clob) throws SQLException, JdbcException {
		return clob.getAsciiStream();
	}

	public static Reader getClobReader(ResultSet rs, int columnIndex) throws SQLException, JdbcException {
		Clob clob = rs.getClob(columnIndex);
		if (clob==null) {
			throw new JdbcException("no clob found in column ["+columnIndex+"]");
		}
		return getClobReader(clob);
	}
	public static Reader getClobReader(ResultSet rs, String columnName) throws SQLException, JdbcException {
		Clob clob = rs.getClob(columnName);
		if (clob==null) {
			throw new JdbcException("no clob found in column ["+columnName+"]");
		}
		return getClobReader(clob);
	}
	public static Reader getClobReader(Clob clob) throws SQLException, JdbcException {
		return clob.getCharacterStream();
	}
	
	
	/**
	 * retrieves an outputstream to a clob column from an updatable resultset.
	 */
	public static OutputStream getClobUpdateOutputStreamxxx(ResultSet rs, int columnIndex) throws SQLException, JdbcException {
		Clob clob = rs.getClob(columnIndex);
		if (clob==null) {
			throw new JdbcException("no clob found in column ["+columnIndex+"]");
		}
		return clob.setAsciiStream(1L);
	}

//	public static Writer getClobWriterxxx(ResultSet rs, int columnIndex) throws SQLException, JdbcException {
//		Clob clob = rs.getClob(columnIndex);
//		if (clob==null) {
//			throw new JdbcException("no clob found in column ["+columnIndex+"]");
//		}
//		return clob.setCharacterStream(1L);
//	}

	public static String getClobAsString(final ResultSet rs, int columnIndex, boolean xmlEncode) throws IOException, JdbcException, SQLException {
		Reader reader = getClobReader(rs,columnIndex);
		return Misc.readerToString(reader, null, xmlEncode);
	}
	public static String getClobAsString(final ResultSet rs, String columnName, boolean xmlEncode) throws IOException, JdbcException, SQLException {
		Reader reader = getClobReader(rs,columnName);
		return Misc.readerToString(reader, null, xmlEncode);
	}

	public static void putStringAsClob(IDbmsSupport dbmsSupport, final ResultSet rs, int columnIndex, String content) throws IOException, JdbcException, SQLException {
		if (content!=null) {
			Object clobHandle=dbmsSupport.getClobUpdateHandle(rs, columnIndex);
			Writer writer = dbmsSupport.getClobWriter(rs, columnIndex, clobHandle);
			writer.write(content);
			writer.close();
			dbmsSupport.updateClob(rs, columnIndex, clobHandle);
		} else {
			log.warn("content to store in blob was null");
		}
	}

	public static void fullClose(Connection connection, ResultSet rs) {
		Statement statement=null;
		
		if (rs==null) {
			log.warn("resultset to close was null");
			return;
		}
		try {
			statement = rs.getStatement();
		} catch (SQLException e) {
			log.warn("Could not obtain statement or connection from resultset",e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				log.warn("Could not close resultset", e);
			} finally {
				if (statement!=null) {
					fullClose(connection, statement);
				}
			}
		}
	}

	/**
	 * Note: Depending on the connect pool used (for example with Tomcat 7) the
	 * connection retrieved from the statement will be the direct connection
	 * instead of the proxied connection. After a close on this (unproxied)
	 * connection the transaction manager isn't able to do a commit anymore.
	 * Hence, this method doesn't get it from the statement but has an extra
	 * connection parameter.
	 * 
	 * @param connection  the proxied/original connection the statement was created with
	 * @param statement   the statement to close
	 */
	public static void fullClose(Connection connection, Statement statement) {
		try {
			statement.close();
		} catch (SQLException e) {
			log.warn("Could not close statement", e);
		} finally {
			if (connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					log.warn("Could not close connection", e);
				}
			}
		}
	}


	private static String displayParameters(String param1, String param2) {
		return (param1==null?"":(" param1 ["+param1+"]"+(param2==null?"":(" param2 ["+param2+"]"))));
	}
	private static String displayParameters(int param1, String param2, String param3) {
		return " param1 ["+param1+"]"+(param2==null?"":(" param2 ["+param2+"]"+(param3==null?"":(" param3 ["+param3+"]"))));
	}
	private static String displayParameters(int param1, int param2, String param3, String param4) {
		return " param1 ["+param1+"] param2 ["+param2+"]"+(param3==null?"":(" param3 ["+param3+"]"+(param4==null?"":(" param4 ["+param4+"]"))));
	}
	private static String displayParameters(int param1, int param2, int param3, String param4, String param5) {
		return " param1 ["+param1+"] param2 ["+param2+"] param3 ["+param3+"]"+(param4==null?"":(" param4 ["+param4+"]"+(param5==null?"":(" param5 ["+param5+"]"))));
	}
	

	private static void applyParameters(PreparedStatement stmt, String param1, String param2) throws SQLException {
		if (param1!=null) {
			//if (log.isDebugEnabled()) log.debug("set"+displayParameters(param1,param2));
			stmt.setString(1,param1);
			if (param2!=null) {
				stmt.setString(2,param2);
			}
		}
	}
	private static void applyParameters(PreparedStatement stmt, int param1, String param2, String param3) throws SQLException {
		//if (log.isDebugEnabled()) log.debug("set"+displayParameters(param1,param2,param3));
		stmt.setInt(1,param1);
		if (param2!=null) {
			stmt.setString(2,param2);
			if (param3!=null) {
				stmt.setString(3,param3);
			}
		}
	}
	private static void applyParameters(PreparedStatement stmt, int param1, int param2, String param3, String param4) throws SQLException {
		// if (log.isDebugEnabled()) log.debug("set"+displayParameters(param1,param2,param3,param4));
		stmt.setInt(1,param1);
		stmt.setInt(2,param2);
		if (param3!=null) {
			stmt.setString(3,param3);
			if (param4!=null) {
				stmt.setString(4,param4);
			}
		}
	}
	private static void applyParameters(PreparedStatement stmt, int param1, int param2, int param3, String param4, String param5) throws SQLException {
		//if (log.isDebugEnabled()) log.debug("set"+displayParameters(param1,param2,param3,param4,param5));
		stmt.setInt(1,param1);
		stmt.setInt(2,param2);
		stmt.setInt(3,param3);
		if (param4!=null) {
			stmt.setString(4,param4);
			if (param5!=null) {
				stmt.setString(5,param5);
			}
		}
	}

	/**
	 * exectues query that returns a string. Returns null if no results are found. 
	 */
	public static String executeStringQuery(Connection connection, String query) throws JdbcException {
		PreparedStatement stmt = null;

		try {
			if (log.isDebugEnabled()) log.debug("prepare and execute query ["+query+"]");
			stmt = connection.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			try {
				if (!rs.next()) {
					return null;
				}
				return rs.getString(1);
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			throw new JdbcException("could not obtain value using query ["+query+"]",e);
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (Exception e) {
					throw new JdbcException("could not close statement of query ["+query+"]",e);
				}
			}
		}
	}


	public static int executeIntQuery(Connection connection, String query) throws JdbcException {
		return executeIntQuery(connection,query,null,null);
	}
	public static int executeIntQuery(Connection connection, String query, String param) throws JdbcException {
		return executeIntQuery(connection,query,param,null);
	}

	/**
	 * exectues query that returns an integer. Returns -1 if no results are found. 
	 */
	public static int executeIntQuery(Connection connection, String query, String param1, String param2) throws JdbcException {
		PreparedStatement stmt = null;

		try {
			if (log.isDebugEnabled()) log.debug("prepare and execute query ["+query+"]"+displayParameters(param1,param2));
			stmt = connection.prepareStatement(query);
			applyParameters(stmt,param1,param2);
			ResultSet rs = stmt.executeQuery();
			try {
				if (!rs.next()) {
					return -1;
				}
				return rs.getInt(1);
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			throw new JdbcException("could not obtain value using query ["+query+"]"+displayParameters(param1,param2),e);
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (Exception e) {
					throw new JdbcException("could not close statement of query ["+query+"]"+displayParameters(param1,param2),e);
				}
			}
		}
	}

	public static int executeIntQuery(Connection connection, String query, int param) throws JdbcException {
		return executeIntQuery(connection,query,param,null,null);
	}
	public static int executeIntQuery(Connection connection, String query, int param1, String param2) throws JdbcException {
		return executeIntQuery(connection,query,param1,param2,null);
	}
	
	public static int executeIntQuery(Connection connection, String query, int param1, String param2, String param3) throws JdbcException {
		PreparedStatement stmt = null;

		try {
			if (log.isDebugEnabled()) log.debug("prepare and execute query ["+query+"]"+displayParameters(param1,param2,param3));
			stmt = connection.prepareStatement(query);
			applyParameters(stmt,param1,param2,param3);
			ResultSet rs = stmt.executeQuery();
			try {
				if (!rs.next()) {
					return -1;
				}
				return rs.getInt(1);
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			throw new JdbcException("could not obtain value using query ["+query+"]"+displayParameters(param1,param2,param3),e);
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (Exception e) {
					throw new JdbcException("could not close statement of query ["+query+"]"+displayParameters(param1,param2,param3),e);
				}
			}
		}
	}


	public static int executeIntQuery(Connection connection, String query, int param1, int param2) throws JdbcException {
		return executeIntQuery(connection,query,param1,param2,null,null);
	}
	public static int executeIntQuery(Connection connection, String query, int param1, int param2, String param3) throws JdbcException {
		return executeIntQuery(connection,query,param1,param2,param3,null);
	}
	
	public static int executeIntQuery(Connection connection, String query, int param1, int param2, String param3, String param4) throws JdbcException {
		PreparedStatement stmt = null;

		try {
			if (log.isDebugEnabled()) log.debug("prepare and execute query ["+query+"]"+displayParameters(param1,param2,param3,param4));
			stmt = connection.prepareStatement(query);
			applyParameters(stmt,param1,param2,param3,param4);
			ResultSet rs = stmt.executeQuery();
			try {
				if (!rs.next()) {
					return -1;
				}
				return rs.getInt(1);
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			throw new JdbcException("could not obtain value using query ["+query+"]"+displayParameters(param1,param2,param3,param4),e);
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (Exception e) {
					throw new JdbcException("could not close statement of query ["+query+"]"+displayParameters(param1,param2,param3,param4),e);
				}
			}
		}
	}


	public static void executeStatement(Connection connection, String query) throws JdbcException {
		executeStatement(connection,query,null,null);
	}
	public static void executeStatement(Connection connection, String query, String param) throws JdbcException {
		executeStatement(connection,query,param,null);
	}
	
	public static void executeStatement(Connection connection, String query, String param1, String param2) throws JdbcException {
		PreparedStatement stmt = null;

		try {
			if (log.isDebugEnabled()) log.debug("prepare and execute query ["+query+"]"+displayParameters(param1,param2));
			stmt = connection.prepareStatement(query);
			applyParameters(stmt,param1,param2);
			stmt.execute();
		} catch (Exception e) {
			throw new JdbcException("could not execute query ["+query+"]"+displayParameters(param1,param2),e);
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (Exception e) {
					throw new JdbcException("could not close statement for query ["+query+"]"+displayParameters(param1,param2),e);
				}
			}
		}
	}

	public static void executeStatement(Connection connection, String query, int param) throws JdbcException {
		executeStatement(connection,query,param,null,null);
	}
	public static void executeStatement(Connection connection, String query, int param1, String param2) throws JdbcException {
		executeStatement(connection,query,param1,param2,null);
	}
	
	public static void executeStatement(Connection connection, String query, int param1, String param2, String param3) throws JdbcException {
		PreparedStatement stmt = null;

		try {
			if (log.isDebugEnabled()) log.debug("prepare and execute query ["+query+"]"+displayParameters(param1,param2,param3));
			stmt = connection.prepareStatement(query);
			applyParameters(stmt,param1,param2,param3);
			stmt.execute();
		} catch (Exception e) {
			throw new JdbcException("could not execute query ["+query+"]"+displayParameters(param1,param2,param3),e);
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (Exception e) {
					throw new JdbcException("could not close statement for query ["+query+"]"+displayParameters(param1,param2,param3),e);
				}
			}
		}
	}

	public static void executeStatement(Connection connection, String query, int param1, int param2, String param3, String param4) throws JdbcException {
		PreparedStatement stmt = null;

		try {
			if (log.isDebugEnabled()) log.debug("prepare and execute query ["+query+"]"+displayParameters(param1,param2,param3,param4));
			stmt = connection.prepareStatement(query);
			applyParameters(stmt,param1,param2,param3,param4);
			stmt.execute();
		} catch (Exception e) {
			throw new JdbcException("could not execute query ["+query+"]"+displayParameters(param1,param2,param3,param4),e);
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (Exception e) {
					throw new JdbcException("could not close statement for query ["+query+"]"+displayParameters(param1,param2,param3,param4),e);
				}
			}
		}
	}

	public static void executeStatement(Connection connection, String query, int param1, int param2, int param3, String param4, String param5) throws JdbcException {
		PreparedStatement stmt = null;

		try {
			if (log.isDebugEnabled()) log.debug("prepare and execute query ["+query+"]"+displayParameters(param1,param2,param3,param4,param5));
			stmt = connection.prepareStatement(query);
			applyParameters(stmt,param1,param2,param3,param4,param5);
			stmt.execute();
		} catch (Exception e) {
			throw new JdbcException("could not execute query ["+query+"]"+displayParameters(param1,param2,param3,param4,param5),e);
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (Exception e) {
					throw new JdbcException("could not close statement for query ["+query+"]"+displayParameters(param1,param2,param3,param4,param5),e);
				}
			}
		}
	}


}
