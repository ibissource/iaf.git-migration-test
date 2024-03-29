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
 * $Log: ResultWriter.java,v $
 * Revision 1.13  2012-06-01 10:52:48  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.12  2011/11/30 13:51:56  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:48  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.10  2010/01/27 13:33:39  gerrit
 * added documentation for attribute blockByRecordType
 *
 * Revision 1.9  2010/01/27 12:21:51  gerrit
 * removed some debug logging
 *
 * Revision 1.8  2007/09/24 14:55:33  gerrit
 * support for parameters
 *
 * Revision 1.7  2007/09/24 13:02:38  gerrit
 * updated javadoc
 *
 * Revision 1.6  2007/09/19 13:22:25  gerrit
 * avoid NPE
 *
 * Revision 1.5  2007/09/19 13:00:54  gerrit
 * added openDocument() and closeDocument()
 * added openBlock() and closeBlock()
 *
 * Revision 1.4  2007/09/11 11:51:44  gerrit
 * updated javadoc
 *
 * Revision 1.3  2007/09/10 11:11:59  gerrit
 * removed logic processing from writePrefix to calling class
 * renamed writePrefix() and writeSuffix() into open/closeRecordType()
 *
 * Revision 1.2  2007/09/05 13:02:33  gerrit
 * updated javadoc
 *
 * Revision 1.1  2007/08/03 08:37:51  gerrit
 * first version
 *
 */
package nl.nn.adapterframework.batch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;

import org.apache.commons.lang.StringUtils;


/**
 * Baseclass for resulthandlers that write the transformed record to a writer.
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>classname</td><td>nl.nn.adapterframework.batch.ResultWriter</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>Name of the resulthandler</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setDefault(boolean) default}</td><td>If true, this resulthandler is the default for all RecordHandlingFlow that do not have a handler specified</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setPrefix(String) prefix}</td><td><i>Deprecated</i> Prefix that has to be written before record, if the record is in another block than the previous record</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSuffix(String) suffix}</td><td><i>Deprecated</i> Suffix that has to be written after the record, if the record is in another block than the next record</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setOnOpenDocument(String) onOpenDocument}</td><td>String that is written before any data of results is written</td><td>&lt;document name=&quot;#name#&quot;&gt;</td></tr>
 * <tr><td>{@link #setOnCloseDocument(String) onCloseDocument}</td><td>String that is written after all data of results is written</td><td>&lt;/document&gt;</td></tr>
 * <tr><td>{@link #setOnOpenBlock(String) onOpenBlock}</td><td>String that is written before the start of each logical block, as defined in the flow</td><td>&lt;#name#&gt;</td></tr>
 * <tr><td>{@link #setOnCloseBlock(String) onCloseBlock}</td><td>String that is written after the end of each logical block, as defined in the flow</td><td>&lt;/#name#&gt;</td></tr>
 * <tr><td>{@link #setBlockNamePattern(String) blockNamePattern}</td><td>String that is replaced by name of block or name of stream in above strings</td><td>#name#</td></tr>
 * <tr><td>{@link #setBlockByRecordType(boolean) blockByRecordType}</td><td>when set <code>true</code>(default), every group of records, as indicated by {@link IRecordHandler.isNewRecordType RecordHandler.newRecordType} is handled as a block.</td><td>true</td></tr>
 * </table>
 * </p>
 * 
 * @author  Gerrit van Brakel
 * @since   4.7
 * @version $Id$
 */
public abstract class ResultWriter extends AbstractResultHandler {
	
	private String onOpenDocument="<document name=\"#name#\">";
	private String onCloseDocument="</document>";
	private String onOpenBlock="<#name#>";
	private String onCloseBlock="</#name#>";
	private String blockNamePattern="#name#";
	
	private Map openWriters = Collections.synchronizedMap(new HashMap());
	
	protected abstract Writer createWriter(IPipeLineSession session, String streamId, ParameterResolutionContext prc) throws Exception;

	public void openDocument(IPipeLineSession session, String streamId, ParameterResolutionContext prc) throws Exception {
		super.openDocument(session, streamId, prc);
		getWriter(session, streamId, true, prc);
		write(session,streamId,replacePattern(getOnOpenDocument(),streamId), prc);
	}

	public void closeDocument(IPipeLineSession session, String streamId, ParameterResolutionContext prc) {
		Writer w = (Writer)openWriters.remove(streamId);
		if (w != null) {
			try {
				w.close();
			} catch (IOException e) {
				log.error("Exception closing ["+streamId+"]",e);
			}
		}
		super.closeDocument(session,streamId, prc);
	}

	public Object finalizeResult(IPipeLineSession session, String streamId, boolean error, ParameterResolutionContext prc) throws Exception {
		log.debug("finalizeResult ["+streamId+"]");
		write(session,streamId,replacePattern(getOnCloseDocument(),streamId), prc);
		return null;
	}
	

	
	public void handleResult(IPipeLineSession session, String streamId, String recordKey, Object result, ParameterResolutionContext prc) throws Exception {
		if (result instanceof String) {
			write(session, streamId, (String)result, prc);
		}
		else if (result instanceof String[]) {
			write(session, streamId, (String[])result, prc);
		}
	}
	
	protected void writeNewLine(Writer w) throws IOException {
		if (w instanceof BufferedWriter) {
			((BufferedWriter)w).newLine();
		} else {
			w.write("\n");
		}
	}
	
	private void write(IPipeLineSession session, String streamId, String line, ParameterResolutionContext prc) throws Exception {
		if (line!=null) {
			Writer w = getWriter(session, streamId, false, prc);
			if (w==null) {
				throw new NullPointerException("No Writer Found for stream ["+streamId+"]");
			}
			w.write(line);
			writeNewLine(w);
		}
	}

	private void write(IPipeLineSession session, String streamId, String[] lines, ParameterResolutionContext prc) throws Exception {
		Writer w = getWriter(session, streamId, false, prc);
		for (int i = 0; i < lines.length; i++) {
			if (lines[i]!=null) {
				w.write(lines[i]);
				writeNewLine(w);
			}
		}
	}
	
	public void openRecordType(IPipeLineSession session, String streamId, ParameterResolutionContext prc) throws Exception {
		Writer w = getWriter(session, streamId, false, prc);
		if (w != null && ! StringUtils.isEmpty(getPrefix())) {
			write(session, streamId, getPrefix(), prc);
		}
	}

	public void closeRecordType(IPipeLineSession session, String streamId, ParameterResolutionContext prc) throws Exception {
		Writer w = getWriter(session, streamId, false, prc);
		if (w != null && ! StringUtils.isEmpty(getSuffix())) {
			write(session, streamId, getSuffix(), prc);
		}
	}

	protected String replacePattern(String target, String blockName) {
		if (StringUtils.isEmpty(target)) {
			return null;
		}
		if (StringUtils.isEmpty(getBlockNamePattern())) {
			return target;
		}
		String result=target.replaceAll(getBlockNamePattern(),blockName);
		//if (log.isDebugEnabled()) log.debug("target ["+target+"] pattern ["+getBlockNamePattern()+"] value ["+blockName+"] result ["+result+"]");
		return result;   
	}

	public void openBlock(IPipeLineSession session, String streamId, String blockName, ParameterResolutionContext prc) throws Exception  {
		write(session,streamId, replacePattern(getOnOpenBlock(),blockName), prc);
	}
	public void closeBlock(IPipeLineSession session, String streamId, String blockName, ParameterResolutionContext prc) throws Exception {
		write(session,streamId, replacePattern(getOnCloseBlock(),blockName), prc);
	}


	protected Writer getWriter(IPipeLineSession session, String streamId, boolean create, ParameterResolutionContext prc) throws Exception {
		//log.debug("getWriter ["+streamId+"], create ["+create+"]");
		Writer writer;
		writer = (Writer)openWriters.get(streamId);
		if (writer != null) {
			return writer;
		}
		
		if (!create) {
			return null;
		}
		writer = createWriter(session,streamId, prc);
		if (writer==null) {
			throw new IOException("cannot get writer for stream ["+streamId+"]");
		}
		openWriters.put(streamId,writer);
		return writer;		
	}

	
	public void setOnOpenDocument(String line) {
		onOpenDocument = line;
	}
	public String getOnOpenDocument() {
		return onOpenDocument;
	}

	public void setOnCloseDocument(String line) {
		onCloseDocument = line;
	}
	public String getOnCloseDocument() {
		return onCloseDocument;
	}

	public void setOnOpenBlock(String line) {
		onOpenBlock = line;
	}
	public String getOnOpenBlock() {
		return onOpenBlock;
	}

	public void setOnCloseBlock(String line) {
		onCloseBlock = line;
	}
	public String getOnCloseBlock() {
		return onCloseBlock;
	}

	public void setBlockNamePattern(String pattern) {
		blockNamePattern = pattern;
	}
	public String getBlockNamePattern() {
		return blockNamePattern;
	}

}
