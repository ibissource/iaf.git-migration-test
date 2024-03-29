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
 * $Log: MoveFilePipe.java,v $
 * Revision 1.9  2012-06-01 10:52:49  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.8  2012/02/13 14:31:51  peter
 * updated javadoc
 *
 * Revision 1.7  2011/11/30 13:51:51  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:45  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.5  2009/02/04 13:05:47  peter
 * added attributes move2file, move2fileSessionKey and append
 *
 * Revision 1.4  2008/02/19 09:58:31  gerrit
 * updated javadoc
 *
 * Revision 1.3  2008/02/15 14:10:10  gerrit
 * added attributes numberOfBackups and overwrite
 *
 * Revision 1.2  2007/07/10 15:17:54  gerrit
 * improve logging
 *
 * Revision 1.1  2006/08/23 11:35:16  gerrit
 * moved batch-pipes to pipes-package
 *
 * Revision 1.6  2006/08/22 12:48:57  gerrit
 * added filename-attribute
 *
 * Revision 1.5  2006/05/19 09:28:38  unknown3
 * Restore java files from batch package after unwanted deletion.
 *
 * Revision 1.3  2005/10/31 14:38:02  unknown2
 * Add . in javadoc
 *
 * Revision 1.2  2005/10/24 09:59:22  unknown2
 * Add support for pattern parameters, and include them into several listeners,
 * senders and pipes that are file related
 *
 * Revision 1.1  2005/10/11 13:00:22  unknown2
 * New ibis file related elements, such as DirectoryListener, MoveFilePie and 
 * BatchFileTransformerPipe
 *
 */
package nl.nn.adapterframework.pipes;

import java.io.File;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.util.FileUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Pipe for moving files to another directory.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.ibis4fundation.FtpSender</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the sender</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setFilename(String) filename}</td><td>The name of the file to move (if not specified, the input for this pipe is assumed to be the name of the file</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMove2dir(String) move2dir}</td><td>destination directory</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMove2file(String) move2file}</td><td>The name of the destination file (if not specified, the name of the file to move is taken)</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMove2fileSessionKey(String) move2fileSessionKey}</td><td>The session key that contains the name of the file to use (only used if move2file is not set)</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setNumberOfBackups(int) numberOfBackups}</td><td>number of copies held of a file with the same name. Backup files have a dot and a number suffixed to their name. If set to 0, no backups will be kept.</td><td>5</td></tr>
 * <tr><td>{@link #setOverwrite(boolean) overwrite}</td><td>when set <code>true</code>, the destination file will be deleted if it already exists</td><td>false</td></tr>
 * <tr><td>{@link #setNumberOfAttempts(int) numberOfAttempts}</td><td>maximum number of attempts before throwing an exception</td><td>10</td></tr>
 * <tr><td>{@link #setWaitBeforeRetry(long) waitBeforeRetry}</td><td>Time between attempts</td><td>1000 [ms]</td></tr>
 * <tr><td>{@link #setAppend(boolean) append}</td><td> when set <code>true</code> and the destination file already exists, the content of the file to move is written to the end of the destination file. This implies <code>overwrite=true</code></td><td>false</td></tr>
 * </table>
 * </p>
 * 
 * @author  john
 * @author  Jaco de Groot (***@dynasol.nl)
 * @author  Gerrit van Brakel
 * @version $Id$
 */
public class MoveFilePipe extends FixedForwardPipe {
	public static final String version = "$RCSfile: MoveFilePipe.java,v $  $Revision: 1.9 $ $Date: 2012-06-01 10:52:49 $";

	private String filename;
	private String move2dir;
	private String move2file;
	protected String move2fileSessionKey;
	private int numberOfAttempts = 10;
	private long waitBeforeRetry = 1000;
	private int numberOfBackups = 5;
	private boolean overwrite = false;
	private boolean append = false;
	
		
	public void configure() throws ConfigurationException {
		super.configure();
		if (isAppend()) {
			setOverwrite(false);
		}
		if (StringUtils.isEmpty(getMove2dir())) {
			throw new ConfigurationException("Property [move2dir] is not set");
		}
	}
	
	/** 
	 * @see nl.nn.adapterframework.core.IPipe#doPipe(Object, PipeLineSession)
	 */
	public PipeRunResult doPipe(Object input, IPipeLineSession session) throws PipeRunException {
		String orgFilename;
		String dstFilename;

		if (StringUtils.isEmpty(getFilename())) {
			orgFilename = input.toString();
		} else {
			orgFilename = getFilename();
		}
		File srcFile = new File(orgFilename);

		if (StringUtils.isEmpty(getMove2file())) {
			if (StringUtils.isEmpty(getMove2fileSessionKey())) {
				dstFilename = srcFile.getName();
			} else {
				dstFilename = (String)session.get(getMove2fileSessionKey());
			}
		} else {
			dstFilename = getMove2file();
		}
		File dstFile = new File(getMove2dir(), dstFilename);

		try {
			if (isAppend()) {
				if (FileUtils.appendFile(srcFile,dstFile,getNumberOfAttempts(), getWaitBeforeRetry()) == null) {
					throw new PipeRunException(this, "Could not move file [" + srcFile.getAbsolutePath() + "] to file ["+dstFile.getAbsolutePath()+"]"); 
				} else {
					srcFile.delete();
					log.info(getLogPrefix(session)+"moved file ["+srcFile.getAbsolutePath()+"] to file ["+dstFile.getAbsolutePath()+"]");
				}			 
			} else {
				if (FileUtils.moveFile(srcFile, dstFile, isOverwrite(), getNumberOfBackups(), getNumberOfAttempts(), getWaitBeforeRetry()) == null) {
					throw new PipeRunException(this, "Could not move file [" + srcFile.getAbsolutePath() + "] to file ["+dstFile.getAbsolutePath()+"]"); 
				} else {
					log.info(getLogPrefix(session)+"moved file ["+srcFile.getAbsolutePath()+"] to file ["+dstFile.getAbsolutePath()+"]");
				}			 
			}
		} catch(Exception e) {
			throw new PipeRunException(this, "Error while moving file [" + srcFile.getAbsolutePath() + "] to file ["+dstFile.getAbsolutePath()+"]", e); 
		}
		return new PipeRunResult(getForward(), dstFile.getAbsolutePath());
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilename() {
		return filename;
	}



	public void setMove2dir(String string) {
		move2dir = string;
	}
	public String getMove2dir() {
		return move2dir;
	}

	public void setMove2file(String string) {
		move2file = string;
	}
	public String getMove2file() {
		return move2file;
	}

	public void setMove2fileSessionKey(String move2fileSessionKey) {
		this.move2fileSessionKey = move2fileSessionKey;
	}
	public String getMove2fileSessionKey() {
		return move2fileSessionKey;
	}

	public void setNumberOfAttempts(int i) {
		numberOfAttempts = i;
	}
	public int getNumberOfAttempts() {
		return numberOfAttempts;
	}


	public void setWaitBeforeRetry(long l) {
		waitBeforeRetry = l;
	}
	public long getWaitBeforeRetry() {
		return waitBeforeRetry;
	}


	public void setNumberOfBackups(int i) {
		numberOfBackups = i;
	}
	public int getNumberOfBackups() {
		return numberOfBackups;
	}

	public void setOverwrite(boolean b) {
		overwrite = b;
	}
	public boolean isOverwrite() {
		return overwrite;
	}

	public void setAppend(boolean b) {
		append = b;
	}
	public boolean isAppend() {
		return append;
	}
}
