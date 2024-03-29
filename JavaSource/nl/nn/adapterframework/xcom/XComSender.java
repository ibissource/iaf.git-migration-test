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
 * $Log: XComSender.java,v $
 * Revision 1.17  2012-06-01 10:52:59  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.16  2011/12/08 09:12:09  peter
 * fixed javadoc
 *
 * Revision 1.15  2011/11/30 13:52:04  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:54  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.13  2010/03/10 14:30:05  peter
 * rolled back testtool adjustments (IbisDebuggerDummy)
 *
 * Revision 1.11  2005/12/19 17:18:55  gerrit
 * corrected typos in javadoc
 *
 * Revision 1.10  2005/12/19 17:14:42  gerrit
 * updated javadoc
 *
 * Revision 1.9  2005/12/19 16:59:39  gerrit
 * corrected typo: had only single r in carriageflag
 *
 * Revision 1.8  2005/12/19 16:40:15  gerrit
 * added authentication using authentication-alias
 *
 * Revision 1.7  2005/10/31 14:42:40  gerrit
 * updated javadoc
 *
 * Revision 1.6  2005/10/28 12:31:05  unknown2
 * Corrected bug with password added twice to command
 *
 * Revision 1.5  2005/10/27 13:29:26  unknown2
 * Add optional configFile property
 *
 * Revision 1.4  2005/10/27 07:58:57  unknown2
 * Host is not longer a required property, since it could be set in a config file
 *
 * Revision 1.3  2005/10/24 09:59:24  unknown2
 * Add support for pattern parameters, and include them into several listeners,
 * senders and pipes that are file related
 *
 * Revision 1.2  2005/10/11 13:04:50  unknown2
 * *** empty log message ***
 *
 * Revision 1.1  2005/10/11 13:04:24  unknown2
 * Support for sending files via the XComSender
 *
 */
package nl.nn.adapterframework.xcom;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.ParameterException;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.core.SenderWithParametersBase;
import nl.nn.adapterframework.core.TimeOutException;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;
import nl.nn.adapterframework.util.CredentialFactory;
import nl.nn.adapterframework.util.FileUtils;

import org.apache.commons.lang.StringUtils;

/**
 * XCom client voor het versturen van files via XCom.

 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.ibis4fundation.XComSender</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the sender</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setWorkingDirName(String) workingDirName}</td><td>directory in which to run the xcomtcp command</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setXcomtcp(String) xcomtcp}</td><td>Path to xcomtcp command</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setFileOption(String) fileOption}</td><td>One of CREATE, APPEND or REPLACE</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setQueue(Boolean) queue}</td><td>Set queue off or on</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setTruncation(Boolean) truncation}</td><td>Set truncation off or on</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setTracelevel(Integer) tracelevel}</td><td>Set between 0 (no trace) and 10</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCodeflag(String) codeflag}</td><td>Characterset conversion, one of ASCII or EBCDIC</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCarriageflag(String) carriageflag}</td><td>One of YES, NO, VRL, VRL2, MPACK or XPACK</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCompress(String) compress}</td><td>One of YES, NO, RLE, COMPACT, LZLARGE, LZMEDIUM or LZSMALL</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setLogfile(String) logfile}</td><td>Name of logfile for xcomtcp to be used</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setRemoteSystem(String) remoteSystem}</td><td>Hostname or tcpip adres of remote host</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setPort(String) port}</td><td>Port of remote host</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setRemoteDirectory(String) remoteDirectory}</td><td>Remote directory is prefixed witht the remote file</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setRemoteFilePattern(String) remoteFilePattern}</td><td>Remote file to create. If empty, the name is equal to the local file</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setAuthAlias(String) authAlias}</td><td>name of the alias to obtain credentials to authenticatie on remote server</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setUserid(String) userid}</td><td>Loginname of user on remote system</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setPassword(String) password}</td><td>Password of user on remote system</td><td>&nbsp;</td></tr>
 * </table>
 * </p>
 *  
 * @author john
 */
public class XComSender extends SenderWithParametersBase {
	public static final String version = "$RCSfile: XComSender.java,v $  $Revision: 1.17 $ $Date: 2012-06-01 10:52:59 $";

	private File workingDir;
	private String name;
	private String fileOption = null;
	private Boolean queue = null;
	private Boolean truncation = null;
	private Integer tracelevel = null;
	private String logfile = null;
	private String codeflag = null;
	private String carriageflag = null;
	private String port = null;
	private String authAlias = null;
	private String userid = null;
	private String password = null;
	private String compress = null;
	private String remoteSystem = null;
	private String remoteDirectory = null;
	private String remoteFilePattern = null;
	private String configFile = null;
	private String workingDirName = ".";
	private String xcomtcp = "xcomtcp";
	
	/* (non-Javadoc)
	 * @see nl.nn.adapterframework.core.ISender#configure()
	 */
	public void configure() throws ConfigurationException {
		if (StringUtils.isNotEmpty(fileOption) &&
				! "CREATE".equals(fileOption) && ! "APPEND".equals(fileOption) && 
				! "REPLACE".equals(fileOption)
		) {
			throw new ConfigurationException("Attribute [fileOption] has incorrect value " + fileOption + ", should be one of CREATE | APPEND or REPLACE");
		}
		if (! StringUtils.isEmpty(compress) &&
				! "YES".equals(compress) && ! "COMPACT".equals(compress) && 
				! "LZLARGE".equals(compress) && ! "LZMEDIUM".equals(compress) && 
				! "LZSMALL".equals(compress) && ! "RLE".equals(compress) && 
				! "NO".equals(compress)  
		) {
			throw new ConfigurationException("Attribute [compress] has incorrect value " + compress + ", should be one of YES | NO | RLE | COMPACT | LZLARGE | LZMEDIUM | LZSMALL");
		}
		if (! StringUtils.isEmpty(codeflag) &&
				! "EBCDIC".equals(codeflag) && ! "ASCII".equals(codeflag)  
		) {
			throw new ConfigurationException("Attribute [codeflag] has incorrect value " + fileOption + ", should be ASCII or EBCDIC");
		}
		if (! StringUtils.isEmpty(carriageflag) &&
				! "YES".equals(carriageflag) && ! "VLR".equals(carriageflag) && 
				! "VLR2".equals(carriageflag) && ! "MPACK".equals(carriageflag) && 
				! "XPACK".equals(carriageflag) && ! "NO".equals(carriageflag)  
		) {
			throw new ConfigurationException("Attribute [cariageflag] has incorrect value " + compress + ", should be one of YES | NO | VRL | VRL2 | MPACK | XPACK");
		}
		if (! StringUtils.isEmpty(port)) {
			try {
				Integer.parseInt(port);
			}
			catch(NumberFormatException e) {
				throw new ConfigurationException("Attribute [port] is not a number");
			}
		}
		if (tracelevel != null && (tracelevel.intValue() < 0 || tracelevel.intValue() > 10)) {
			throw new ConfigurationException("Attribute [tracelevel] should be between 0 (no trace) and 10, not " + tracelevel.intValue());
		}
		if (StringUtils.isEmpty(workingDirName)) {
			throw new ConfigurationException("Attribute [workingDirName] is not set");
		}
		else {
			workingDir = new File(workingDirName);
			if (! workingDir.isDirectory()) {
				throw new ConfigurationException("Working directory [workingDirName=" + workingDirName + "] is not a directory");
			}
		}
	}

	/* (non-Javadoc)
	 * @see nl.nn.adapterframework.core.ISender#isSynchronous()
	 */
	public boolean isSynchronous() {
		return true;
	}

	/* (non-Javadoc)
	 * @see nl.nn.adapterframework.core.ISender#sendMessage(java.lang.String, java.lang.String)
	 */
	public String sendMessage(String correlationID, String message, ParameterResolutionContext prc) throws SenderException, TimeOutException {
		for (Iterator filenameIt = getFileList(message).iterator(); filenameIt.hasNext(); ) {
			String filename = (String)filenameIt.next();
			log.debug("Start sending " + filename);
		
			// get file to send
			File localFile = new File(filename);
			
			// execute command in a new operating process
			try {
				String cmd = getCommand(prc.getSession(), localFile, true);
				
				Process p = Runtime.getRuntime().exec(cmd, null, workingDir);
	
				// read the output of the process
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				StringBuffer output = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null) {
					output.append(line);
				}
	
				// wait until the process is completely finished
				try {
					p.waitFor();
				}
				catch(InterruptedException e) {
				}
	
				log.debug("output for " + localFile.getName() + " = " + output.toString());
				log.debug(localFile.getName() + " exits with " + p.exitValue());
				
				// throw an exception if the command returns an error exit value
				if (p.exitValue() != 0) {
					throw new SenderException("XComSender failed for file " + localFile.getAbsolutePath() + "\r\n" + output.toString());
				}
			}
			catch(IOException e) {
				throw new SenderException("Error while executing command " + getCommand(prc.getSession(), localFile, false), e);
			}
		}
		return message;
	}
	
	private String getCommand(IPipeLineSession session, File localFile, boolean inclPasswd) throws SenderException {
		try {
			StringBuffer sb = new StringBuffer();
			
			sb.append(xcomtcp). append(" -c1");

			if (StringUtils.isNotEmpty(configFile)) {
				sb.append(" -f ").append(configFile);
			}

			if (StringUtils.isNotEmpty(remoteSystem)) {
				sb.append(" REMOTE_SYSTEM=").append(remoteSystem);
			}
				
			if (localFile != null) {			
				sb.append(" LOCAL_FILE=").append(localFile.getAbsolutePath());
	
				sb.append(" REMOTE_FILE=");
				if (! StringUtils.isEmpty(remoteDirectory)) 
					sb.append(remoteDirectory);
				if (StringUtils.isEmpty(remoteFilePattern))
					sb.append(localFile.getName());
				else 
					sb.append(FileUtils.getFilename(paramList, session, localFile, remoteFilePattern));
			}
				
			CredentialFactory cf = new CredentialFactory(getAuthAlias(), getUserid(), password);
	
						
			// optional parameters
			if (StringUtils.isNotEmpty(fileOption)) 
				sb.append(" FILE_OPTION=").append(fileOption);	 
			if (queue != null)
				sb.append(" QUEUE=").append(queue.booleanValue() ? "YES" : "NO");
			if (tracelevel != null)
				sb.append(" TRACE=").append(tracelevel.intValue());
			if (truncation != null)
				sb.append(" TRUNCATION=").append(truncation.booleanValue() ? "YES" : "NO");
			if (! StringUtils.isEmpty(port))
				sb.append(" PORT=" + port);
			if (! StringUtils.isEmpty(logfile)) 
				sb.append(" XLOGFILE=" + logfile);
			if (! StringUtils.isEmpty(compress)) 
				sb.append(" COMPRESS=").append(compress);
			if (! StringUtils.isEmpty(codeflag)) 
				sb.append(" CODE_FLAG=").append(codeflag);
			if (! StringUtils.isEmpty(carriageflag)) 
				sb.append(" CARRIAGE_FLAG=").append(carriageflag);
			if (! StringUtils.isEmpty(cf.getUsername())) 
				sb.append(" USERID=").append(cf.getUsername());
			if (inclPasswd && ! StringUtils.isEmpty(cf.getPassword())) 
				sb.append(" PASSWORD=").append(cf.getPassword());
				
			return sb.toString();
		}
		catch(ParameterException e) {
			throw new SenderException(e);
		}
	}
	
	public String getXcomtcp() {
		return xcomtcp;
	}

	private List getFileList(String message) {
		StringTokenizer st = new StringTokenizer(message, ";");
		LinkedList list = new LinkedList();
		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see nl.nn.adapterframework.core.INamedObject#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see nl.nn.adapterframework.core.INamedObject#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFileOption() {
		return fileOption;
	}

	public void setFileOption(String newVal) {
		fileOption = newVal;
	}

	public String getRemoteDirectory() {
		return remoteDirectory;
	}

	public void setRemoteDirectory(String string) {
		remoteDirectory = string;
	}

	public String getCariageflag() {
		return carriageflag;
	}

	public String getCodeflag() {
		return codeflag;
	}

	public String getCompress() {
		return compress;
	}

	public String getLogfile() {
		return logfile;
	}

	public String getPort() {
		return port;
	}

	public Boolean isQueue() {
		return queue;
	}

	public String getRemoteSystem() {
		return remoteSystem;
	}

	public Integer getTracelevel() {
		return tracelevel;
	}

	public Boolean isTruncation() {
		return truncation;
	}

	public String getUserid() {
		return userid;
	}

	public void setCarriageflag(String string) {
		carriageflag = string;
	}

	public void setCodeflag(String string) {
		codeflag = string;
	}

	public void setCompress(String string) {
		compress = string;
	}

	public void setLogfile(String string) {
		logfile = string;
	}

	public void setPassword(String string) {
		password = string;
	}

	public void setPort(String string) {
		port = string;
	}

	public void setQueue(Boolean b) {
		queue = b;
	}

	public void setRemoteSystem(String string) {
		remoteSystem = string;
	}

	public void setTracelevel(Integer i) {
		tracelevel = i;
	}

	public void setTruncation(Boolean b) {
		truncation = b;
	}

	public void setUserid(String string) {
		userid = string;
	}

	public String getRemoteFilePattern() {
		return remoteFilePattern;
	}

	public void setRemoteFilePattern(String string) {
		remoteFilePattern = string;
	}
	public String getWorkingDirName() {
		return workingDirName;
	}

	public void setWorkingDirName(String string) {
		workingDirName = string;
	}

	public void setXcomtcp(String string) {
		xcomtcp = string;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String string) {
		configFile = string;
	}

	public void setAuthAlias(String string) {
		authAlias = string;
	}
	public String getAuthAlias() {
		return authAlias;
	}


}
