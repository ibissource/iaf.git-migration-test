/*
 * $Log: CleanupOldFilesPipe.java,v $
 * Revision 1.1  2006-08-24 07:10:36  europe\L190409
 * moved CleanupOldFilesPipe to pipes-package
 *
 * Revision 1.5  2006/05/19 09:28:36  unknown3
 * Restore java files from batch package after unwanted deletion.
 *
 * Revision 1.3  2005/12/20 09:57:20  gerrit
 * updated javadoc
 *
 * Revision 1.2  2005/11/08 09:31:08  unknown2
 * Bug concerning filenames resolved
 *
 * Revision 1.1  2005/11/01 08:54:00  unknown2
 * Initial version
 *
 */
package nl.nn.adapterframework.pipes;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.util.FileUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Pipe for deleting files.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.batch.CleanupOldFilesPipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMaxThreads(int) maxThreads}</td><td>maximum number of threads that may call {@link #doPipe(Object, nl.nn.adapterframework.core.PipeLineSession)} simultaneously</td><td>0 (unlimited)</td></tr>
 * <tr><td>{@link #setFilePattern(String) filePattern}</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSubdirectories(boolean) subdirectories}</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setLastModifiedDelta(long) lastModifiedDelta}</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * </table>
 * </p>
 * 
 * @author: John Dekker
 * @since:  4.2
 */
public class CleanupOldFilesPipe extends FixedForwardPipe {
	public static final String version = "$RCSfile: CleanupOldFilesPipe.java,v $  $Revision: 1.1 $ $Date: 2006-08-24 07:10:36 $";
	
	private String filePattern;
	private boolean subdirectories;
	private long lastModifiedDelta;

	private _FileFilter fileFilter = new _FileFilter();
	private _DirFilter dirFilter = new _DirFilter();
		
	public CleanupOldFilesPipe() {
	}
	
	public void configure() throws ConfigurationException {
		super.configure();
		
		if (StringUtils.isEmpty(filePattern)) {
			throw new ConfigurationException("Property [move2dir] is not set");
		}
	}
	
	/** 
	 * @see nl.nn.adapterframework.core.IPipe#doPipe(Object, PipeLineSession)
	 */
	public PipeRunResult doPipe(Object input, PipeLineSession session) throws PipeRunException {
		try {
			File in = (input == null) ? null : new File(input.toString());
			String filename = FileUtils.getFilename(null, session, in, filePattern);
			ArrayList delFiles = getFilesForDeletion(filename);
			if (delFiles != null && delFiles.size() > 0) {
				for (Iterator fileIt = delFiles.iterator(); fileIt.hasNext();) {
					File file = (File)fileIt.next();
					file.delete();
				}
			}
			return new PipeRunResult(getForward(), input);
		}
		catch(Exception e) {
			throw new PipeRunException(this, "Error while deleting file(s)", e); 
		}
	}

	private ArrayList getFilesForDeletion(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			ArrayList result = new ArrayList();
			if (file.isDirectory()) {
				getFilesForDeletion(result, file);
			}
			else {
				if (fileFilter.accept(file))
					result.add(file);
			}
			return result;
		}
		return null;
	}

	private void getFilesForDeletion(ArrayList result, File directory) {
		File[] files = directory.listFiles(fileFilter);
		for (int i = 0; i < files.length; i++) {
			result.add(files[i]);
		}
		
		if (subdirectories) {
			files = directory.listFiles(dirFilter);
			for (int i = 0; i < files.length; i++) {
				getFilesForDeletion(result, files[i]);
			}		
		}
	}

	private class _FileFilter implements FileFilter {
		public boolean accept(File file) {
			if (file.isFile()) {
				if ((System.currentTimeMillis() - file.lastModified()) > lastModifiedDelta) {
					return true;
				}
			}
			return false;
		}
	}

	private class _DirFilter implements FileFilter {
		public boolean accept(File file) {
			return file.isDirectory();
		}
	}


	public void setFilePattern(String string) {
		filePattern = string;
	}

	public void setLastModifiedDelta(long l) {
		lastModifiedDelta = l;
	}

	public void setSubdirectories(boolean b) {
		subdirectories = b;
	}

}