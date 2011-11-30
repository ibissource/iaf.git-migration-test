/*
 * $Log: LobLineIteratingPipeBase.java,v $
 * Revision 1.4  2011-11-30 13:51:43  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2008/02/26 08:36:40  gerrit
 * renamed IteratingPipeBase to JdbcIteratingPipeBase
 *
 * Revision 1.1  2007/07/26 16:15:28  gerrit
 * first version
 *
 */
package nl.nn.adapterframework.jdbc;

import java.io.Reader;
import java.sql.ResultSet;

import nl.nn.adapterframework.core.IDataIterator;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.util.JdbcUtil;
import nl.nn.adapterframework.util.ReaderLineIterator;

/**
 * abstract baseclass for Pipes that iterate over the lines in a lob.
 * 
 * @author  Gerrit van Brakel
 * @since   4.7
 * @version Id
 */
public abstract class LobLineIteratingPipeBase extends JdbcIteratingPipeBase {

	protected abstract Reader getReader(ResultSet rs) throws SenderException;

	protected class ResultStreamIterator extends ReaderLineIterator {
		ResultSet rs;
		
		ResultStreamIterator(ResultSet rs, Reader reader) throws SenderException {
			super(reader);
			this.rs=rs;
		}
		
		public void close() throws SenderException {
			try {
				super.close();
			} finally {
				JdbcUtil.fullClose(rs);
			}
		}

	}

	protected IDataIterator getIterator(ResultSet rs) throws SenderException {
		return new ResultStreamIterator(rs, getReader(rs));
	}

}
