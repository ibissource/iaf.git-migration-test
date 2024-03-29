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
 * $Log: FileHandler.java,v $
 * Revision 1.3  2013-02-12 15:44:15  peter
 * added logging
 *
 * Revision 1.2  2013/02/12 15:07:25  peter
 * added skipBOM attribute
 *
 * Revision 1.1  2012/10/05 15:45:31  jaco
 * Introduced FileSender which is similar to FilePipe but can be used as a Sender (making is possible to have a MessageLog)
 *
 * Revision 1.33  2012/06/01 10:52:49  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.32  2012/02/20 13:30:58  jaco
 * Added attribute charset to FilePipe
 *
 * Revision 1.31  2012/02/13 09:14:33  peter
 * added attribute createDirectory to writing files
 *
 * Revision 1.30  2012/02/09 13:38:41  jaco
 * Fixed faceted error (Java facet 1.4 -> 1.5)
 *
 * Revision 1.29  2011/11/30 13:51:50  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.27  2011/05/16 14:49:44  gerrit
 * renamed FileListener to FileLister
 *
 * Revision 1.26  2011/05/16 12:29:41  peter
 * list action: if a directory is not specified, the fileName is expected to include the directory
 *
 * Revision 1.25  2011/05/12 13:50:34  peter
 * added list action
 *
 * Revision 1.24  2010/08/09 13:06:24  peter
 * added attribute testCanWrite and adjusted check for write permissions
 *
 * Revision 1.23  2010/01/22 09:17:09  martijno
 * Updated to conform to convention
 *
 * Revision 1.22  2010/01/20 14:57:06  martijno
 * FilePipe - FileDelete now accepts  filename, filenamesessionkey and/or directory
 * also logs delete/failure of delete/file not exists.
 *
 * Revision 1.21  2010/01/20 12:52:09  martijno
 * FilePipe - FileDelete now accepts  filename, filenamesessionkey and/or directory
 * also logs deletion.
 *
 * Revision 1.19  2009/12/11 15:04:44  martijno
 * Fixed problem with fileNameSessionKey when action is read file.
 * 
 * Revision 1.18  2007/12/27 16:04:15  gerrit
 * force file to be created for action 'create'
 *
 * Revision 1.17  2007/12/17 13:21:49  gerrit
 * added create option
 *
 * Revision 1.16  2007/12/17 08:57:21  gerrit
 * corrected documentation
 *
 * Revision 1.15  2007/09/26 13:54:37  jaco
 * directory isn't mandatory anymore, temp file will be created in java.io.tempdir, see updated javadoc for more info
 *
 * Revision 1.14  2007/09/24 13:03:58  gerrit
 * improved error messages
 *
 * Revision 1.13  2007/07/17 15:12:05  gerrit
 * added writeLineSeparator
 *
 * Revision 1.12  2007/05/21 12:20:27  gerrit
 * added attribute createDirectory
 *
 * Revision 1.11  2006/08/22 12:53:45  gerrit
 * added fileName and fileNameSessionKey attributes
 *
 * Revision 1.10  2006/05/04 06:47:55  gerrit
 * handles correctly incoming byte[]
 *
 * Revision 1.9  2005/12/08 08:00:26  gerrit
 * corrected version string
 *
 * Revision 1.8  2005/12/07 16:09:25  gerrit
 * modified handling for filename
 *
 * Revision 1.7  2004/08/23 13:44:13  unknown0
 * Add config checks
 *
 * Revision 1.5  2004/04/27 11:03:35  unknown0
 * Renamed internal Transformer interface to prevent naming confusions
 *
 * Revision 1.3  2004/04/26 13:06:53  unknown0
 * Support for file en- and decoding
 *
 * Revision 1.2  2004/04/26 13:04:50  unknown0
 * Support for file en- and decoding
 *
 * Revision 1.1  2004/04/26 11:51:34  unknown0
 * Support for file en- and decoding
 *
 */
package nl.nn.adapterframework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.INamedObject;
import nl.nn.adapterframework.core.IPipeLineSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * FileHandler, available to the Ibis developer as {@link nl.nn.adapterframework.senders.FileSender} and
 * {@link nl.nn.adapterframework.pipes.FilePipe}, allows to write to or read from a file.
 * Write will create a file in the specified directory. If a directory is not
 * specified, the fileName is expected to include the directory. If both the
 * fileName and the directory are not specified a temporary file is created as
 * specified by the {@link java.io.File.createTempFile} method using the string "ibis"
 * as a prefix and a suffix as specified bij the writeSuffix attribute. If only
 * the directory is specified, the temporary file is created the same way except
 * that the temporay file is created in the specified directory.
 * The pipe also support base64 en- and decoding.
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setCharset(String) charset}</td><td>The charset to be used when transforming a string to a byte array and/or the other way around</td><td>The value of the system property file.encoding</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setDirectory(String) directory}</td><td>base directory where files are stored in or read from</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setFileName(String) fileName}</td><td>The name of the file to use</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setFileNameSessionKey(String) fileNameSessionKey}</td><td>The session key that contains the name of the file to use (only used if fileName is not set)</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setActions(String) actions}</td><td>comma separated list of actions to be performed. Possible action values:
 * <ul>
 * <li>write: create a new file and write input to it</li>
 * <li>write_append: create a new file if it does not exist, otherwise append to existing file; then write input to it</li>
 * <li>create: create a new file, but do not write anything to it</li>
 * <li>read: read from file</li>
 * <li>delete: delete the file</li>
 * <li>read_delete: read the contents, then delete</li>
 * <li>encode: encode base64</li>
 * <li>decode: decode base64</li>
 * <li>list: returns the files and directories in the directory that satisfy the specified filter (see {@link nl.nn.adapterframework.util.Dir2Xml dir2xml}). If a directory is not specified, the fileName is expected to include the directory</li>
 * </ul></td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setWriteSuffix(String) writeSuffix}</td><td>suffix of the file to be created (only used if fileName and fileNameSession are not set)</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCreateDirectory(boolean) createDirectory}</td><td>when set to <code>true</code>, the directory to read from or write to is created if it does not exist</td><td>false</td></tr>
 * <tr><td>{@link #setWriteLineSeparator(boolean) writeLineSeparator}</td><td>when set to <code>true</code>, a line separator is written after the content is written</td><td>false</td></tr>
 * <tr><td>{@link #setTestCanWrite(boolean) testCanWrite}</td><td>when set to <code>true</code>, a test is performed to find out if a temporary file can be created and deleted in the specified directory (only used if directory is set and combined with the action write, write_append or create)</td><td>true</td></tr>
 * <tr><td>{@link #setSkipBOM(boolean) skipBOM}</td><td>when set to <code>true</code>, a possible Bytes Order Mark (BOM) at the start of the file is skipped (only used for the action read and encoding UFT-8)</td><td>false</td></tr>
 * </table>
 * </p>
 * <p><b>Exits:</b>
 * <table border="1">
 * <tr><th>state</th><th>condition</th></tr>
 * <tr><td>"success"</td><td>default</td></tr>
 * <tr><td><i>{@link #setForwardName(String) forwardName}</i></td><td>if specified</td></tr>
 * </table>
 * </p>
 * 
 * @author J. Dekker
 * @author Jaco de Groot (***@dynasol.nl)
 * @version $Id$
 *
 */
public class FileHandler {
	protected Logger log = LogUtil.getLogger(this);

	protected static final byte[] BOM_UTF_8 = new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF};
	
	protected String charset = System.getProperty("file.encoding");
	protected String actions;
	protected String directory;
	protected String writeSuffix;
	protected String fileName;
	protected String fileNameSessionKey;
	protected boolean createDirectory = false;
	protected boolean writeLineSeparator = false;
	protected boolean testCanWrite = true;
	protected boolean skipBOM = false;

	protected List transformers;
	protected byte[] eolArray=null;
	
	/** 
	 * @see nl.nn.adapterframework.core.IPipe#configure()
	 */
	public void configure() throws ConfigurationException {
		// translation action seperated string to Transformers
		transformers = new LinkedList();
		if (StringUtils.isEmpty(actions))
			throw new ConfigurationException(getLogPrefix(null)+"should at least define one action");
			
		StringTokenizer tok = new StringTokenizer(actions, " ,\t\n\r\f");
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			
			if ("write".equalsIgnoreCase(token))
				transformers.add(new FileWriter(false));
			else if ("write_append".equalsIgnoreCase(token))
				transformers.add(new FileWriter(true));
			else if ("create".equalsIgnoreCase(token))
				transformers.add(new FileCreater());
			else if ("read".equalsIgnoreCase(token))
				transformers.add(new FileReader());
			else if ("delete".equalsIgnoreCase(token))
				transformers.add(new FileDeleter());
			else if ("read_delete".equalsIgnoreCase(token))
				transformers.add(new FileReader(true));
			else if ("encode".equalsIgnoreCase(token))
				transformers.add(new Encoder());
			else if ("decode".equalsIgnoreCase(token))
				transformers.add(new Decoder());
			else if ("list".equalsIgnoreCase(token))
				transformers.add(new FileLister());
			else
				throw new ConfigurationException(getLogPrefix(null)+"Action [" + token + "] is not supported");
		}
		
		if (transformers.size() == 0)
			throw new ConfigurationException(getLogPrefix(null)+"should at least define one action");
		
		// configure the transformers
		for (Iterator it = transformers.iterator(); it.hasNext(); ) {
			((TransformerAction)it.next()).configure();
		}
		eolArray = System.getProperty("line.separator").getBytes();
	}
	
	public String handle(Object input, IPipeLineSession session) throws Exception {
		byte[] inValue = null;
		if (input instanceof byte[]) {
			inValue = (byte [])input;
		}
		else {
			inValue = (input == null) ? null : input.toString().getBytes(charset);
		}
			
		for (Iterator it = transformers.iterator(); it.hasNext(); ) {
			inValue = ((TransformerAction)it.next()).go(inValue, session);
		}
		String outValue = inValue == null ? null : new String(inValue, charset);
		return outValue;
	}
	
	/**
	 * The pipe supports several actions. All actions are implementations in
	 * inner-classes that implement the Transformer interface.
	 */
	protected interface TransformerAction {
		/* 
		 * @see nl.nn.adapterframework.core.IPipe#configure()
		 */
		void configure() throws ConfigurationException;
		/*
		 * transform the in and return the result
		 * @see nl.nn.adapterframework.core.IPipe#doPipe(Object, PipeLineSession)
		 */
		byte[] go(byte[] in, IPipeLineSession session) throws Exception;
	}
	
	/**
	 * Encodes the input 
	 */
	private class Encoder implements TransformerAction {
		public void configure() {}
		public byte[] go(byte[] in, IPipeLineSession session) throws Exception {
			return Base64.encodeBase64(in);
		}
	}
	
	/**
	 * Decodes the input
	 */
	private class Decoder implements TransformerAction {
		public void configure() {}
		public byte[] go(byte[] in, IPipeLineSession session) throws Exception {
			return Base64.decodeBase64(in == null ? null : new String(in));
		}
	}

	private File createFile(IPipeLineSession session) throws IOException {
		File tmpFile;
			
		String name = fileName;
		if (StringUtils.isEmpty(name)) {
			name = (String)session.get(fileNameSessionKey);
		}
		if (StringUtils.isEmpty(getDirectory())) {
			if (StringUtils.isEmpty(name)) {
				tmpFile = File.createTempFile("ibis", writeSuffix);
			} else {
				tmpFile = new File(name);
			}
		} else {
			if (StringUtils.isEmpty(name)) {
				tmpFile = File.createTempFile("ibis", writeSuffix, new File(getDirectory()));
			} else {
				tmpFile = new File(getDirectory() + File.separator + name);
			}
		}
		return tmpFile;
	}

	/**
	 * Write the input to a file in the specified directory.
	 */
	private class FileWriter implements TransformerAction {
		private boolean append = false;
		public FileWriter(boolean append) {
			this.append = append;
		}
		// create the directory structure if not exists and
		// check the permissions
		public void configure() throws ConfigurationException {
			if (StringUtils.isNotEmpty(getDirectory()) && isTestCanWrite()) {
				if (!FileUtils.canWrite(getDirectory())) {
					throw new ConfigurationException(getLogPrefix(null)+"directory ["+ getDirectory() + "] is not a directory, or no write permission");
				}
			}
		}
		public byte[] go(byte[] in, IPipeLineSession session) throws Exception {
			File tmpFile=createFile(session);

			if (!tmpFile.getParentFile().exists()) {
				if (isCreateDirectory()) {
					if (tmpFile.getParentFile().mkdirs()) {
						log.debug( getLogPrefix(session) + "created directory [" + tmpFile.getParent() +"]");
					} else {
						log.warn( getLogPrefix(session) + "directory [" + tmpFile.getParent() +"] could not be created");
					}
				} else {
					log.warn( getLogPrefix(session) + "directory [" + tmpFile.getParent() +"] does not exists");
				}
			}
			// Use tmpFile.getPath() instead of tmpFile to be WAS 5.0 / Java 1.3 compatible
			FileOutputStream fos = new FileOutputStream(tmpFile.getPath(), append);
			
			try {
				if (in!=null) {
					fos.write(in);
					if (isWriteLineSeparator()) {
						fos.write(eolArray);
					}
				}
			} finally {
				fos.close();
			}
			
			return tmpFile.getPath().getBytes();
		}
	}

	/**
	 * create a new file.
	 */
	private class FileCreater implements TransformerAction {
		// create the directory structure if not exists and
		// check the permissions
		public void configure() throws ConfigurationException {
			if (StringUtils.isNotEmpty(getDirectory()) && isTestCanWrite()) {
				if (!FileUtils.canWrite(getDirectory())) {
					throw new ConfigurationException(getLogPrefix(null)+"directory ["+ getDirectory() + "] is not a directory, or no write permission");
				}
			}
		}
		public byte[] go(byte[] in, IPipeLineSession session) throws Exception {
			File tmpFile=createFile(session);
			FileOutputStream fos = new FileOutputStream(tmpFile.getPath(), false);
			fos.close();
			return tmpFile.getPath().getBytes();
		}
	}

	/**
	 * Reads the file, which name is specified in the input, from the specified directory.
	 * The class supports the deletion of the file after reading.
	 */
	private class FileReader implements TransformerAction {
		private boolean deleteAfterRead;
		
		FileReader() {
			deleteAfterRead = false;
		}
		FileReader(boolean deleteAfterRead) {
			this.deleteAfterRead = deleteAfterRead;
		}
		public void configure() throws ConfigurationException {
			if (StringUtils.isNotEmpty(getDirectory())) {
				File file = new File(getDirectory());
				if (!file.exists() && createDirectory) {
					if (!file.mkdirs()) {
						throw new ConfigurationException(directory + " could not be created");
					}
				}
				if (! (file.exists() && file.isDirectory() && file.canRead())) {
					throw new ConfigurationException(directory + " is not a directory, or no read permission");
				}
			}
		}
		public byte[] go(byte[] in, IPipeLineSession session) throws Exception {
			File file;
			 
			String name = (String)session.get(fileNameSessionKey);;
			
			if (StringUtils.isEmpty(name)) {
				name = new String(in);
			}
															
			if (StringUtils.isNotEmpty(getDirectory())) {
				file = new File(getDirectory(), name);
			} else {
				file = new File(name);
			}
			FileInputStream fis = new FileInputStream(file);
			
			try {
				byte[] result = new byte[fis.available()];
				fis.read(result);
				if (isSkipBOM()) {
					if ((result[0] == BOM_UTF_8[0]) && (result[1] == BOM_UTF_8[1]) && (result[2] == BOM_UTF_8[2])) {
					    byte[] resultWithoutBOM = new byte[result.length-3];
					    for(int i = 3; i < result.length; ++i)
					    	resultWithoutBOM[i-3]=result[i];
					    log.debug(getLogPrefix(session) + "removed UTF-8 BOM");
					    return resultWithoutBOM;
					} else {
						return result;
					}
				} else {
					return result;
				}
			} finally {
				fis.close();

				if (deleteAfterRead)
					file.delete();					
			}
		}
	}

	/**
	 * Delete the file.
	 */
	private class FileDeleter implements TransformerAction {
		public void configure() throws ConfigurationException {
															
			if (StringUtils.isNotEmpty(getDirectory())) {
				File file = new File(getDirectory());
				if (! (file.exists() && file.isDirectory())) {
					throw new ConfigurationException(directory + " is not a directory");
				}
			}
			
		}
		public byte[] go(byte[] in, IPipeLineSession session) throws Exception {
			File file;
			
			/* take filename from 
			 * 1) fileName attribute
			 * 2) fileNameSessionKey
			 * 3) otherwise take the pipe input  
			*/
			
			String name = fileName;
			
			if (StringUtils.isEmpty(name)) { 
				if (!(StringUtils.isEmpty(fileNameSessionKey))) { 
					name = (String)session.get(fileNameSessionKey); 
				}
			  	else {	
			  		name = new String(in); 
			  	}
			}

			/* check for directory path 
			 * if param directory not filled, 
			 * then filename's filepath.
			 */					
			if ( getDirectory() != null ) {
				file = new File(getDirectory(), name);
			} 
			else {
				file = new File( name );
			}
											
			/* if file exists, delete the file */
			if (file.exists()) {
				boolean success = file.delete();
				if (!success){
				   log.warn( getLogPrefix(session) + "could not delete file [" + file.toString() +"]");
				} 
				else {
				   log.debug(getLogPrefix(session) + "deleted file [" + file.toString() +"]");
				} 
			}
			else {
				log.warn( getLogPrefix(session) + "file [" + file.toString() +"] does not exist");
			}
			return in;
		}
	}

	private class FileLister implements TransformerAction {
		public void configure() throws ConfigurationException {
			if (StringUtils.isNotEmpty(getDirectory())) {
				File file = new File(getDirectory());
				if (! (file.exists() && file.isDirectory() && file.canRead())) {
					throw new ConfigurationException(directory + " is not a directory, or no read permission");
				}
			}
		}

		public byte[] go(byte[] in, IPipeLineSession session) throws Exception {
			String name = fileName;
			
			if (StringUtils.isEmpty(name)) { 
				if (!(StringUtils.isEmpty(fileNameSessionKey))) { 
					name = (String)session.get(fileNameSessionKey); 
				}
				else {	
					name = new String(in); 
				}
			}

			String dir = getDirectory();
			if (StringUtils.isEmpty(dir)) {
				File file = new File(name);
				String parent = file.getParent();
				if (parent!=null) {
					dir = parent;
					name = file.getName();
				}
			}

			Dir2Xml dx=new Dir2Xml();
			dx.setPath(dir);
			if (StringUtils.isNotEmpty(name)) { 
				dx.setWildCard(name);
			}
			String listResult=dx.getDirList();
			return listResult.getBytes();
		}
	}

	protected String getLogPrefix(IPipeLineSession session){
		StringBuilder sb = new StringBuilder();
		sb.append(ClassUtils.nameOf(this)).append(' ');
		if (this instanceof INamedObject) {
			sb.append("[").append(((INamedObject)this).getName()).append("] ");
		}
		if (session != null) {
			sb.append("msgId [").append(session.getMessageId()).append("] ");
		}
		return sb.toString();
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @param actions all the actions the pipe has to do
	 * 
	 * Possible actions are "read", "write", "write_append", "encode", "decode", "delete" and "read_delete"
	 * You can also define combinations, like "read encode write".
	 */
	public void setActions(String actions) {
		this.actions = actions;
	}
	public String getActions() {
		return actions;
	}

	/**
	 * @param directory in which the file resides or has to be created
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param suffix of the file that is written
	 */
	public void setWriteSuffix(String suffix) {
		this.writeSuffix = suffix;
	}
	public String getWriteSuffix() {
		return writeSuffix;
	}

	/**
	 * @param suffix of the file that is written
	 */
	public void setFileName(String filename) {
		this.fileName = filename;
	}
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param the session key that contains the name of the file to be created
	 */
	public void setFileNameSessionKey(String filenameSessionKey) {
		this.fileNameSessionKey = filenameSessionKey;
	}
	public String getFileNameSessionKey() {
		return fileNameSessionKey;
	}

	public void setCreateDirectory(boolean b) {
		createDirectory = b;
	}
	public boolean isCreateDirectory() {
		return createDirectory;
	}

	public void setWriteLineSeparator(boolean b) {
		writeLineSeparator = b;
	}
	public boolean isWriteLineSeparator() {
		return writeLineSeparator;
	}

	public void setTestCanWrite(boolean b) {
		testCanWrite = b;
	}
	public boolean isTestCanWrite() {
		return testCanWrite;
	}

	public void setSkipBOM(boolean b) {
		skipBOM = b;
	}
	public boolean isSkipBOM() {
		return skipBOM;
	}
}
