/*
 * $Log: SapLUWManager.java,v $
 * Revision 1.1  2012-02-06 14:33:04  m00f069
 * Implemented JCo 3 based on the JCo 2 code. JCo2 code has been moved to another package, original package now contains classes to detect the JCo version available and use the corresponding implementation.
 *
 * Revision 1.4  2011/11/30 13:51:54  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:52  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2007/07/10 07:13:00  gerrit
 * improved logging
 *
 * Revision 1.1  2007/05/01 14:21:31  gerrit
 * introduction of SAP LUW management
 *
 */
package nl.nn.adapterframework.extensions.sap.jco3;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IPipeLineExitHandler;
import nl.nn.adapterframework.core.PipeLine;
import nl.nn.adapterframework.core.PipeLineResult;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.pipes.FixedForwardPipe;

import org.apache.commons.lang.StringUtils;

import com.sap.conn.jco.JCoException;

/**
 * Manager for SAP Logical Units of Work (LUWs). 
 * Used to begin, commit or rollback LUWs. A SapLUWManager can be placed before a number
 * of SapSenders. The SapLUWManager and the SapSenders must each use the same value for
 * luwHandleSessionKey. By doing so, they use the same connection to SAP. This allows to
 * perform a commit on a number of actions.<br>
 * The placement of the the first SapLUWManager is optionan: By specifying a new 
 * luwHandleSessionKey a new handle is created implicitly.<br>
 * To explicityly commit or rollback a set of actions, a SapLUWManager-pipe can be used, with 
 * the action-attribute set apropriately.
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setName(String) name}</td><td>Name of the Ibis-object</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSapSystemName(String) sapSystemName}</td><td>name of the SapSystem used by this object</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setLuwHandleSessionKey(String) luwHandleSessionKey}</td><td>session key under which information is stored</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setAction(String) action}</td><td>one of: begin, commit, rollback, release</td><td>&nbsp;</td></tr>
 * </table>
 * 
 * @author  Gerrit van Brakel
 * @author  Jaco de Groot
 * @since   5.0
 * @version Id
 */
public class SapLUWManager extends FixedForwardPipe implements IPipeLineExitHandler {
	public static String version="$RCSfile: SapLUWManager.java,v $  $Revision: 1.1 $ $Date: 2012-02-06 14:33:04 $";

	public static final String ACTION_BEGIN="begin";
	public static final String ACTION_COMMIT="commit";
	public static final String ACTION_ROLLBACK="rollback";
	public static final String ACTION_RELEASE="release";

	private String luwHandleSessionKey;
	private String action;
	private String sapSystemName;
	
	private SapSystem sapSystem;


	public void configure(PipeLine pipeline) throws ConfigurationException {
		super.configure(pipeline);
		if (StringUtils.isEmpty(getAction())) {
			throw new ConfigurationException("action should be specified, it must be one of: "+
				ACTION_BEGIN+", "+ACTION_COMMIT+", "+ACTION_ROLLBACK+", "+ACTION_RELEASE+".");
		}
		if (!getAction().equalsIgnoreCase(ACTION_BEGIN) &&
			!getAction().equalsIgnoreCase(ACTION_COMMIT) &&
			!getAction().equalsIgnoreCase(ACTION_ROLLBACK) &&
			!getAction().equalsIgnoreCase(ACTION_RELEASE)) {
			throw new ConfigurationException("illegal action ["+getAction()+"] specified, it must be one of: "+
				ACTION_BEGIN+", "+ACTION_COMMIT+", "+ACTION_ROLLBACK+", "+ACTION_RELEASE+".");
		}
		if (getAction().equalsIgnoreCase(ACTION_BEGIN)) {
			pipeline.registerExitHandler(this);
		}
		if (StringUtils.isEmpty(getLuwHandleSessionKey())) {
			throw new ConfigurationException("action should be specified, it must be one of: "+
				ACTION_BEGIN+", "+ACTION_COMMIT+", "+ACTION_ROLLBACK+", "+ACTION_RELEASE+".");
		}
		sapSystem=SapSystem.getSystem(getSapSystemName());
		if (sapSystem==null) {
			throw new ConfigurationException(getLogPrefix(null)+"cannot find SapSystem ["+getSapSystemName()+"]");
		}
	}

	public void atEndOfPipeLine(String correlationId, PipeLineResult pipeLineResult, PipeLineSession session) throws PipeRunException {
		try {
			SapLUWHandle.releaseHandle(session,getLuwHandleSessionKey());
		} catch (JCoException e) {
			throw new PipeRunException(this, getLogPrefix(null)+"could not release handle", e);
		}
	}

	public void open() throws SenderException {
		try {
			sapSystem.openSystem();
		} catch (SapException e) {
			close();
			throw new SenderException(getLogPrefix(null)+"exception starting SapSender", e);
		}
	}
	
	public void close() {
		sapSystem.closeSystem();
	}


	public PipeRunResult doPipe(Object input, PipeLineSession session) throws PipeRunException {
		if (getAction().equalsIgnoreCase(ACTION_BEGIN)) {
			try {
				SapLUWHandle.retrieveHandle(session,getLuwHandleSessionKey(),true,getSapSystem(),false).begin();
			} catch (JCoException e) {
				throw new PipeRunException(this, "begin: could not retrieve handle", e);
			}
		} else
		if (getAction().equalsIgnoreCase(ACTION_COMMIT)) {
			SapLUWHandle handle=SapLUWHandle.retrieveHandle(session,getLuwHandleSessionKey());
			if (handle==null) {
				throw new PipeRunException(this, "commit: cannot find handle under sessionKey ["+getLuwHandleSessionKey()+"]");
			} else {
				try {
					handle.commit();
				} catch (JCoException e) {
					throw new PipeRunException(this, "commit: could not commit handle", e);
				}
			}
		} else
		if (getAction().equalsIgnoreCase(ACTION_ROLLBACK)) {
			SapLUWHandle handle=SapLUWHandle.retrieveHandle(session,getLuwHandleSessionKey());
			if (handle==null) {
				throw new PipeRunException(this, "rollback: cannot find handle under sessionKey ["+getLuwHandleSessionKey()+"]");
			} else {
				handle.rollback();
			}
		} else
		if (getAction().equalsIgnoreCase(ACTION_RELEASE)) {
			try {
				SapLUWHandle.releaseHandle(session,getLuwHandleSessionKey());
			} catch (JCoException e) {
				throw new PipeRunException(this, "release: could not release handle", e);
			}
		} 
		return new PipeRunResult(getForward(),input);
	}



	public SapSystem getSapSystem() {
		return sapSystem;
	}




	public void setSapSystemName(String string) {
		sapSystemName = string;
	}
	public String getSapSystemName() {
		return sapSystemName;
	}


	public void setAction(String string) {
		action = string;
	}
	public String getAction() {
		return action;
	}

	public void setLuwHandleSessionKey(String string) {
		luwHandleSessionKey = string;
	}
	public String getLuwHandleSessionKey() {
		return luwHandleSessionKey;
	}


}