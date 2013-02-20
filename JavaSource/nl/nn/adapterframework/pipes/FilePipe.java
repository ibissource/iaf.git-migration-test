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
 * $Log: FilePipe.java,v $
 * Revision 1.36  2013-02-12 15:38:08  peter
 * added skipBOM attribute
 *
 * Revision 1.35  2012/11/07 15:03:49  jaco
 * Changed general error message
 *
 * Revision 1.34  2012/10/05 15:45:31  jaco
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
package nl.nn.adapterframework.pipes;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.util.FileHandler;


/**
 * <p>See {@link FileHandler}</p>
 * 
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
public class FilePipe extends FixedForwardPipe {
	FileHandler fileHandler;

	FilePipe() {
		fileHandler = new FileHandler();
	}
	
	public void configure() throws ConfigurationException {
		super.configure();
		fileHandler.configure();
	}
	
	/** 
	 * @see nl.nn.adapterframework.core.IPipe#doPipe(Object, PipeLineSession)
	 */
	public PipeRunResult doPipe(Object input, IPipeLineSession session) throws PipeRunException {
		try {
			return new PipeRunResult(getForward(), fileHandler.handle(input, session));
		}
		catch(Exception e) {
			throw new PipeRunException(this, getLogPrefix(session)+"Error while executing file action(s)", e); 
		}
	}

	public void setCharset(String charset) {
		fileHandler.setCharset(charset);
	}

	public void setActions(String actions) {
		fileHandler.setActions(actions);
	}

	public void setDirectory(String directory) {
		fileHandler.setDirectory(directory);
	}

	public void setWriteSuffix(String suffix) {
		fileHandler.setWriteSuffix(suffix);
	}

	public void setFileName(String filename) {
		fileHandler.setFileName(filename);
	}

	public void setFileNameSessionKey(String filenameSessionKey) {
		fileHandler.setFileNameSessionKey(filenameSessionKey);
	}

	public void setCreateDirectory(boolean b) {
		fileHandler.setCreateDirectory(b);
	}

	public void setWriteLineSeparator(boolean b) {
		fileHandler.setWriteLineSeparator(b);
	}

	public void setTestCanWrite(boolean b) {
		fileHandler.setTestCanWrite(b);
	}

	public void setSkipBOM(boolean b) {
		fileHandler.setSkipBOM(b);
	}
}
