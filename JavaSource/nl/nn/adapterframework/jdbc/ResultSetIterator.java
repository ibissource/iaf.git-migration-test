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
 * $Log: ResultSetIterator.java,v $
 * Revision 1.8  2012-02-17 18:04:02  jaco
 * Use proxiedDataSources for JdbcIteratingPipeBase too
 * Call close on original/proxied connection instead of connection from statement that might be the unproxied connection
 *
 * Revision 1.7  2011/11/30 13:51:43  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.5  2008/10/23 14:16:51  peter
 * XSLT 2.0 made possible
 *
 * Revision 1.4  2008/05/15 14:36:12  gerrit
 * change startup behaviour, first line is now returned too
 *
 * Revision 1.3  2008/02/26 08:36:05  gerrit
 * updated javadoc
 *
 * Revision 1.2  2007/07/26 16:14:08  gerrit
 * use JdbcUtil.fullClose()
 *
 * Revision 1.1  2007/07/17 11:16:50  gerrit
 * added iterating classes
 *
 */
package nl.nn.adapterframework.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import nl.nn.adapterframework.core.IDataIterator;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.util.DB2XMLWriter;
import nl.nn.adapterframework.util.JdbcUtil;
import nl.nn.adapterframework.util.LogUtil;
import nl.nn.adapterframework.util.Misc;

import org.apache.log4j.Logger;

/**
 * Iterator over ResultSet.
 * 
 * Each row is returned in the same way a row is usually returned from a query.
 * 
 * @author  Gerrit van Brakel
 * @since   4.7
 * @version $Id$
 */
class ResultSetIterator implements IDataIterator {
	protected Logger log = LogUtil.getLogger(this);

	private Connection conn;
	private ResultSet rs;
		
	private ResultSetMetaData rsmeta;
	private boolean lineChecked=true; // assumes at least one line is present, and cursor is on it!
	private boolean lineAvailable=true;

	int rowNumber=0;

	public ResultSetIterator(Connection conn, ResultSet rs) throws SQLException {
		super();
		this.conn=conn;
		this.rs=rs;
		rsmeta=rs.getMetaData();
	}

	public boolean hasNext() throws SenderException {
		try {
			if (!lineChecked) {
				lineAvailable=rs.next();
				lineChecked=true;
			}
			return lineAvailable;
		} catch (Exception e) {
			throw new SenderException(e);
		}
	}

	public Object next() throws SenderException {
		try {
			lineChecked=false;
			return DB2XMLWriter.getRowXml(rs, rowNumber++, rsmeta, Misc.DEFAULT_INPUT_STREAM_ENCODING, false, "", true, false).toXML();
		} catch (Exception e) {
			throw new SenderException(e);
		}
	}

	public void close() {
		JdbcUtil.fullClose(conn, rs);
	}
}
