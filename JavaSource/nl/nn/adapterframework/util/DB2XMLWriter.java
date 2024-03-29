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
 * $Log: DB2XMLWriter.java,v $
 * Revision 1.24  2011-11-30 13:51:48  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.22  2011/08/09 10:11:24  gerrit
 * use JdbcUtil.getValue()
 *
 * Revision 1.21  2011/04/13 08:48:03  gerrit
 * treat VARBINARY and LONGVARBINARY as BLOB
 *
 * Revision 1.20  2009/10/07 14:32:19  peter
 * added facility to exclude the field definition
 *
 * Revision 1.19  2009/09/07 13:45:51  gerrit
 * filled deliberatly empty catch blocks with debug statement
 * replaced e.printStackTrace with log.error()
 *
 * Revision 1.18  2009/09/07 13:33:34  gerrit
 * replaced new String() by literal
 * replaced '&' by '&&'
 *
 * Revision 1.17  2008/10/20 13:03:27  peter
 * also show not compressed blobs and not serialized blobs
 *
 * Revision 1.16  2008/05/15 15:18:20  gerrit
 * fixed trimming of values
 *
 * Revision 1.15  2008/05/14 09:21:40  gerrit
 * return null for LOBs when no LOB found
 * improved logging
 *
 * Revision 1.14  2007/07/17 11:00:35  gerrit
 * separate function to get single row
 *
 * Revision 1.13  2007/02/12 14:09:31  gerrit
 * Logger from LogUtil
 *
 * Revision 1.12  2006/12/13 16:31:28  gerrit
 * added blobCharset attribute
 *
 * Revision 1.11  2005/12/29 15:34:00  gerrit
 * added support for clobs
 *
 * Revision 1.10  2005/10/17 11:24:28  gerrit
 * added blobs and warnings
 *
 * Revision 1.9  2005/10/03 13:17:40  gerrit
 * added attribute trimSpaces, default=true
 *
 * Revision 1.8  2005/09/29 13:57:54  gerrit
 * made null an attribute of each field, where applicable
 *
 * Revision 1.7  2005/07/28 07:42:59  gerrit
 * return a value for CLOBs too;
 * catch numberFormatException for precision
 *
 * Revision 1.6  2005/06/13 11:52:12  gerrit
 * cosmetic changes
 *
 */
package nl.nn.adapterframework.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import nl.nn.adapterframework.core.SenderException;

import org.apache.log4j.Logger;

/**
 * Transforms a java.sql.Resultset to a XML stream.
 * Example of a result:
 * <code><pre>
 * &lt;result&gt;
	&lt;fielddefinition&gt;
		&lt;field name="fieldname"
		          type="columnType" 
		          columnDisplaySize=""
		          precision=""
		          scale=""
		          isCurrency=""
		          columnTypeName=""
		          columnClassName=""/&gt;
		 &lt;field ...../&gt;
	&lt;/fielddefinition&gt;
	&lt;rowset&gt;
		&lt;row number="1"&gt;
			&lt;field name="fieldname"&gt;value&lt;/field&gt;
			&lt;field name="fieldname" null="true" &gt;&lt;/field&gt;
			&lt;field name="fieldname"&gt;value&lt;/field&gt;
			&lt;field name="fieldname"&gt;value&lt;/field&gt;
		&lt;/row&gt;
	&lt;/rowset&gt;
&lt;/result&gt;
</pre></code>
 *
 * @author Johan Verrips
 * @version $Id$
 **/

public class DB2XMLWriter {
	protected static Logger log = LogUtil.getLogger(DB2XMLWriter.class);

	private String docname = "result";
	private String recordname = "rowset";
	private String nullValue = "";
	private boolean trimSpaces=true;
	private boolean decompressBlobs=false;
	private boolean getBlobSmart=false;
	private String blobCharset = Misc.DEFAULT_INPUT_STREAM_ENCODING;

   
    public static String getFieldType (int type) {
	    switch (type) {
	          case Types.INTEGER : return ("INTEGER");
	          case Types.NUMERIC : return ("NUMERIC");
	          case Types.CHAR :    return ("CHAR");
	          case Types.DATE :    return ("DATE");
	          case Types.TIMESTAMP : return ("TIMESTAMP");
	          case Types.DOUBLE : return ("DOUBLE");
	          case Types.FLOAT : return ("FLOAT");
	          case Types.ARRAY : return ("ARRAY");
	          case Types.BLOB : return ("BLOB");
	          case Types.CLOB : return ("CLOB");
	          case Types.DISTINCT : return ("DISTINCT");
	          case Types.LONGVARBINARY : return ("LONGVARBINARY");
	          case Types.VARBINARY : return ("VARBINARY");
	          case Types.BINARY : return ("BINARY");
	          case Types.REF : return ("REF");
	          case Types.STRUCT : return ("STRUCT");
	          case Types.JAVA_OBJECT  : return ("JAVA_OBJECT");
	          case Types.VARCHAR  : return ("VARCHAR");
	          case Types.TINYINT: return ("TINYINT");
	          case Types.TIME: return ("TIME");
	          case Types.REAL: return ("REAL");
		}
     	return ("Unknown");
    }
       

   /**
    * Retrieve the Resultset as a well-formed XML string
    */
	public synchronized String getXML(ResultSet rs) {
		return getXML(rs, Integer.MAX_VALUE);
	}

	/**
	 * Retrieve the Resultset as a well-formed XML string
	 */
	public synchronized String getXML(ResultSet rs, int maxlength) {
		return getXML(rs, maxlength, true);
	}

	public synchronized String getXML(ResultSet rs, int maxlength, boolean includeFieldDefinition) {
		if (null == rs)
			return "";
	
		if (maxlength < 0)
			maxlength = Integer.MAX_VALUE;
	
		XmlBuilder mainElement = new XmlBuilder(docname);
		Statement stmt=null;
		try {
			stmt = rs.getStatement();
			if (stmt!=null) {
				JdbcUtil.warningsToXml(stmt.getWarnings(),mainElement);
			}
		} catch (SQLException e1) {
			log.warn("exception obtaining statement warnings", e1);
		}
		int rowCounter=0;
		try {
			ResultSetMetaData rsmeta = rs.getMetaData();
			if (includeFieldDefinition) {
				int nfields = rsmeta.getColumnCount();
	
				XmlBuilder fields = new XmlBuilder("fielddefinition");
				for (int j = 1; j <= nfields; j++) {
					XmlBuilder field = new XmlBuilder("field");
	
					field.addAttribute("name", "" + rsmeta.getColumnName(j));
	
					//Not every JDBC implementation implements these attributes!
					try {
						field.addAttribute("type", "" + getFieldType(rsmeta.getColumnType(j)));
					} catch (SQLException e) {
						log.debug("Could not determine columnType",e);
					}
					try {
						field.addAttribute("columnDisplaySize", "" + rsmeta.getColumnDisplaySize(j));
					} catch (SQLException e) {
						log.debug("Could not determine columnDisplaySize",e);
					}
					try {
						field.addAttribute("precision", "" + rsmeta.getPrecision(j));
					} catch (SQLException e) {
						log.warn("Could not determine precision",e);
					} catch (NumberFormatException e2) {
						if (log.isDebugEnabled()) log.debug("Could not determine precision: "+e2.getMessage());
					}
					try {
						field.addAttribute("scale", "" + rsmeta.getScale(j));
					} catch (SQLException e) {
						log.debug("Could not determine scale",e);
					}
					try {
						field.addAttribute("isCurrency", "" + rsmeta.isCurrency(j));
					} catch (SQLException e) {
						log.debug("Could not determine isCurrency",e);
					}
					try {
						field.addAttribute("columnTypeName", "" + rsmeta.getColumnTypeName(j));
					} catch (SQLException e) {
						log.debug("Could not determine columnTypeName",e);
					}
					try {
						field.addAttribute("columnClassName", "" + rsmeta.getColumnClassName(j));
					} catch (SQLException e) {
						log.debug("Could not determine columnClassName",e);
					}
					fields.addSubElement(field);
				}
				mainElement.addSubElement(fields);
			}
		
			//----------------------------------------
			// Process result rows
			//----------------------------------------
	
			XmlBuilder queryresult = new XmlBuilder(recordname);
			while (rs.next() && rowCounter < maxlength) {
				XmlBuilder row = getRowXml(rs,rowCounter,rsmeta,getBlobCharset(),decompressBlobs,nullValue,trimSpaces,getBlobSmart);
				queryresult.addSubElement(row);
				rowCounter++;
			}
			mainElement.addSubElement(queryresult);
		} catch (Exception e) {
			log.error("Error occured at row [" + rowCounter+"]", e);
		}
		String answer = mainElement.toXML();
		return answer;
	}
	
	public static XmlBuilder getRowXml(ResultSet rs, int rowNumber, ResultSetMetaData rsmeta, String blobCharset, boolean decompressBlobs, String nullValue, boolean trimSpaces, boolean getBlobSmart) throws SenderException, SQLException {
		XmlBuilder row = new XmlBuilder("row");
		row.addAttribute("number", "" + rowNumber);
	
		for (int i = 1; i <= rsmeta.getColumnCount(); i++) {
			XmlBuilder resultField = new XmlBuilder("field");
	
			resultField.addAttribute("name", "" + rsmeta.getColumnName(i));
	
			try {
				String value = JdbcUtil.getValue(rs, i, rsmeta, blobCharset, decompressBlobs, nullValue, trimSpaces, getBlobSmart, false);
				if (rs.wasNull()) {
					resultField.addAttribute("null","true");
				}
				resultField.setValue(value);
	
			} catch (Exception e) {
				throw new SenderException("error getting fieldvalue column ["+i+"] fieldType ["+getFieldType(rsmeta.getColumnType(i))+ "]", e);
			}
			row.addSubElement(resultField);
		}
		JdbcUtil.warningsToXml(rs.getWarnings(),row);
		return row;
	}


   public void setDocumentName(String s) {
     docname = s;
   }
   public void setRecordName(String s) {
     recordname = s;
   }
   
   /**
	* Set the presentation of a <code>Null</code> value
	**/
   public void setNullValue(String s) {
	 nullValue=s;
   }
   /**
	* Get the presentation of a <code>Null</code> value
	**/
   public String getNullValue () {
	 return nullValue;
   }

	public void setTrimSpaces(boolean b) {
		trimSpaces = b;
	}
	public boolean isTrimSpaces() {
		return trimSpaces;
	}

	public void setDecompressBlobs(boolean b) {
		decompressBlobs = b;
	}
	public boolean isDecompressBlobs() {
		return decompressBlobs;
	}

	public void setGetBlobSmart(boolean b) {
		getBlobSmart = b;
	}
	public boolean isGetBlobSmart() {
		return getBlobSmart;
	}

	public String getBlobCharset() {
		return blobCharset;
	}

	public void setBlobCharset(String string) {
		blobCharset = string;
	}

}
