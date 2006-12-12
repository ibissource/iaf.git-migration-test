/*
 * $Log: JdbcException.java,v $
 * Revision 1.5  2006-12-12 09:58:41  europe\L190409
 * fix version string
 *
 * Revision 1.4  2006/12/12 09:57:35  gerrit
 * restore jdbc package
 *
 * Revision 1.2  2004/03/26 10:43:07  johan
 * added @version tag in javadoc
 *
 * Revision 1.1  2004/03/24 13:28:20  gerrit
 * initial version
 *
 */
package nl.nn.adapterframework.jdbc;

import nl.nn.adapterframework.core.IbisException;

/**
 * Wrapper for JDBC related exceptions.
 * 
 * @version Id
 * @author Gerrit van Brakel
 * @since  4.1
 */
public class JdbcException extends IbisException {
	public static final String version = "$RCSfile: JdbcException.java,v $ $Revision: 1.5 $ $Date: 2006-12-12 09:58:41 $";

	public JdbcException() {
		super();
	}

	public JdbcException(String arg1) {
		super(arg1);
	}

	public JdbcException(String arg1, Throwable arg2) {
		super(arg1, arg2);
	}

	public JdbcException(Throwable arg1) {
		super(arg1);
	}

}
