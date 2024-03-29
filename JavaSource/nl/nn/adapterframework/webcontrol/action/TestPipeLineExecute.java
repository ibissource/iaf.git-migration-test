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
 * $Log: TestPipeLineExecute.java,v $
 * Revision 1.22  2012-06-01 10:52:59  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.21  2011/11/30 13:51:46  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.19  2009/12/31 10:06:52  peter
 * SendJmsMessage/TestIfsaService/TestPipeLine: made zipfile-upload facility case-insensitive
 *
 * Revision 1.18  2009/09/03 08:58:10  peter
 * adjusted javadoc
 *
 * Revision 1.17  2009/09/03 08:48:00  peter
 * bugfix: or upload or message
 *
 * Revision 1.16  2009/08/31 09:48:12  peter
 * adjusted javadoc
 *
 * Revision 1.15  2009/03/17 10:52:48  peter
 * slight error fixed
 *
 * Revision 1.14  2009/02/05 14:08:37  peter
 * bugfix - non xml strings results in error
 *
 * Revision 1.13  2008/12/24 10:57:52  peter
 * added context facility (in xml processing instructions)
 *
 * Revision 1.12  2008/12/16 13:37:50  gerrit
 * read messages in the right encoding
 *
 * Revision 1.11  2008/07/24 12:41:09  gerrit
 * do not execute when cancelled
 *
 * Revision 1.10  2008/05/22 07:45:03  gerrit
 * use inherited error() method
 *
 * Revision 1.9  2007/10/08 13:41:35  gerrit
 * changed ArrayList to List where possible
 *
 * Revision 1.8  2007/07/19 15:16:19  gerrit
 * list Adapters in order of configuration
 *
 * Revision 1.7  2007/02/12 15:50:14  gerrit
 * removed remote directory facility
 *
 * Revision 1.6  2007/02/12 14:36:29  gerrit
 * added zipfile-upload capability
 *
 */
package nl.nn.adapterframework.webcontrol.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.nn.adapterframework.core.IAdapter;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeLineResult;
import nl.nn.adapterframework.core.PipeLineSessionBase;
import nl.nn.adapterframework.util.FileUtils;
import nl.nn.adapterframework.util.Misc;
import nl.nn.adapterframework.util.XmlUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;

/**
 * Test the Pipeline of an adapter.
 * <p>
 * SessionKeys can be created by using processing instructions with the name <code>ibiscontext</code><br/><br/>
 * example:<br/><code><pre>
 * &lt;?ibiscontext ifsaServiceName=GETPIPELINENAME?&gt;
 * &lt;?ibiscontext tcid=1234567890?&gt;
 * &lt;message&gt;This is a Message&lt;/message&gt;
 * </pre></code><br/>
 * When the key <code>tcid</code> is given, it is used as <code>technicalCorrelationId</code> in the <code>PipeLineSession</code>. 
 * 
 * @author  Johan Verrips
 * @see nl.nn.adapterframework.configuration.Configuration
 * @see nl.nn.adapterframework.core.Adapter
 * @see nl.nn.adapterframework.core.PipeLine
 * @version $Id$
 */
public final class TestPipeLineExecute extends ActionBase {
	public static final String version="$RCSfile: TestPipeLineExecute.java,v $  $Revision: 1.22 $ $Date: 2012-06-01 10:52:59 $";
	
	public ActionForward execute(
	    ActionMapping mapping,
	    ActionForm form,
	    HttpServletRequest request,
	    HttpServletResponse response)
	    throws IOException, ServletException {
	
	    // Initialize action
	    initAction(request);
	    if (null == config) {
	        return (mapping.findForward("noconfig"));
	    }

		if (isCancelled(request)) {
			return (mapping.findForward("success"));
		}
	    
	
	    DynaActionForm pipeLineTestForm = (DynaActionForm) form;
	    String form_adapterName = (String) pipeLineTestForm.get("adapterName");
	    String form_message = (String) pipeLineTestForm.get("message");
	    String form_resultText = "";
	    String form_resultState = "";
	    FormFile form_file = (FormFile) pipeLineTestForm.get("file");
	
	    // if no message and no formfile, send an error
	    if ( StringUtils.isEmpty(form_message) &&
	        ( form_file==null || form_file.getFileSize() == 0 )) {
	
	        storeFormData(null, null, null, pipeLineTestForm);
	        warn("Nothing to send or test");
	    }
	    // Report any errors we have discovered back to the original form
	    if (!errors.isEmpty()) {
	        saveErrors(request, errors);
	        storeFormData(null, null, null, pipeLineTestForm);
	        return (new ActionForward(mapping.getInput()));
	    }
	    if ((form_adapterName == null) || (form_adapterName.length() == 0)) {
	        warn("No adapter selected");
	    }
	    // Report any errors we have discovered back to the original form
	    if (!errors.isEmpty()) {
	        saveErrors(request, errors);
	        storeFormData(null, null, form_message, pipeLineTestForm);
	        return (new ActionForward(mapping.getInput()));
	    }
	    // Execute the request
	    IAdapter adapter = config.getRegisteredAdapter(form_adapterName);
	    if (adapter == null) {
			warn("Adapter with specified name ["+form_adapterName+"] could not be retrieved");
	    }
	    // Report any errors we have discovered back to the original form
	    if (!errors.isEmpty()) {
	        saveErrors(request, errors);
	        storeFormData(null, null, form_message, pipeLineTestForm);
	        return (new ActionForward(mapping.getInput()));
	    }
	
	    // if upload is choosen, it prevails over the message
	    if ((form_file != null) && (form_file.getFileSize() > 0)) {
			log.debug("Upload of file ["+form_file.getFileName()+"] ContentType["+form_file.getContentType()+"]");
			if (FileUtils.extensionEqualsIgnoreCase(form_file.getFileName(),"zip")) {
	    		ZipInputStream archive = new ZipInputStream(new ByteArrayInputStream(form_file.getFileData()));
	    		for (ZipEntry entry=archive.getNextEntry(); entry!=null; entry=archive.getNextEntry()) {
	    			String name = entry.getName();
					int size = (int)entry.getSize();
					if (size>0) {
						byte[] b=new byte[size];
						int rb=0;
						int chunk=0;
						while (((int)size - rb) > 0) {
							chunk=archive.read(b,rb,(int)size - rb);
							if (chunk==-1) {
							   break;
							}
							rb+=chunk;
						}
						String currentMessage = XmlUtils.readXml(b,0,rb,request.getCharacterEncoding(),false);
						//PipeLineResult pipeLineResult = adapter.processMessage(name+"_" + Misc.createSimpleUUID(), currentMessage);
						PipeLineResult pipeLineResult = processMessage(adapter, name+"_" + Misc.createSimpleUUID(), currentMessage);
						form_resultText += name + ":" + pipeLineResult.getState() + "\n";
						form_resultState = pipeLineResult.getState();
					}
					archive.closeEntry();
	    		}
	    		archive.close();
	    		form_message = null;
	    	} else {
				form_message = XmlUtils.readXml(form_file.getFileData(),request.getCharacterEncoding(),false);
	    	}
	    } else {
			form_message=new String(form_message.getBytes(),Misc.DEFAULT_INPUT_STREAM_ENCODING);
	    }
	
		if(form_message != null && form_message.length() > 0) {
	    // Execute the request
			//PipeLineResult pipeLineResult = adapter.processMessage("testmessage" + Misc.createSimpleUUID(), form_message);
			PipeLineResult pipeLineResult = processMessage(adapter, "testmessage" + Misc.createSimpleUUID(), form_message);
	    	form_resultText = pipeLineResult.getResult();
			form_resultState = pipeLineResult.getState();
		}
	    storeFormData(form_resultText, form_resultState, form_message, pipeLineTestForm);
	
	    // Report any errors we have discovered back to the original form
	    if (!errors.isEmpty()) {
	        saveErrors(request, errors);
	        return (new ActionForward(mapping.getInput()));
	    }
	
	    // Forward control to the specified success URI
	    log.debug("forward to success");
	    return (mapping.findForward("success"));
	
	}

	private PipeLineResult processMessage(IAdapter adapter, String messageId, String message) {
		IPipeLineSession pls=new PipeLineSessionBase();
		Map ibisContexts = XmlUtils.getIbisContext(message);
		String technicalCorrelationId = null;
		if (ibisContexts!=null) {
			String contextDump = "ibisContext:";
			for (Iterator it = ibisContexts.keySet().iterator(); it.hasNext();) {
				String key = (String)it.next();
				String value = (String)ibisContexts.get(key);
				if (log.isDebugEnabled()) {
					contextDump = contextDump + "\n " + key + "=[" + value + "]";
				}
				if (key.equals(IPipeLineSession.technicalCorrelationIdKey)) {
					technicalCorrelationId = value;
				} else {
					pls.put(key, value);
				}
			}
			if (log.isDebugEnabled()) {
				log.debug(contextDump);
			}
		}
		Date now=new Date();
		PipeLineSessionBase.setListenerParameters(pls,messageId,technicalCorrelationId,now,now);
		return adapter.processMessage(messageId, message, pls);
	}

	public void storeFormData(String result, String state, String message, DynaActionForm pipeLineTestForm) {
	
	    // refresh list of stopped adapters
	    // =================================
	    List adapters = new ArrayList();
	    adapters.add("-- select an adapter --");
	
	    // get the names of the Adapters
		for(int i=0; i<config.getRegisteredAdapters().size(); i++) {
			IAdapter adapter = config.getRegisteredAdapter(i);
			adapters.add(adapter.getName());
		}
	    pipeLineTestForm.set("adapters", adapters);
	    if (null!=message) pipeLineTestForm.set("message", message);
	    if (null != result) {
	        pipeLineTestForm.set("result", result);	
	    }
	    if (null != state) {
	        pipeLineTestForm.set("state", state);
	
	    }	
	}
}
