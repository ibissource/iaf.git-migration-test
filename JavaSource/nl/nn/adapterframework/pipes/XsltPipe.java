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
 * $Log: XsltPipe.java,v $
 * Revision 1.37  2012-06-01 10:52:49  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.36  2011/11/30 13:51:50  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:45  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.34  2011/08/22 14:26:50  gerrit
 * set size statistics on by default
 *
 * Revision 1.33  2010/07/12 12:52:24  gerrit
 * allow to specfiy namespace prefixes to be used in XPath-epressions
 *
 * Revision 1.32  2008/10/24 14:41:41  peter
 * XSLT 2.0 made possible
 *
 * Revision 1.31  2008/10/23 14:16:51  peter
 * XSLT 2.0 made possible
 *
 * Revision 1.30  2008/10/21 11:29:31  peter
 * skipEmptyTags always  namespaceAware
 *
 * Revision 1.29  2008/10/10 12:47:54  peter
 * *** empty log message ***
 *
 * Revision 1.28  2008/10/07 10:57:40  peter
 * added removeNamespaces attribute
 *
 * Revision 1.27  2008/05/15 15:27:41  gerrit
 * corrected typo in documentation
 *
 * Revision 1.26  2008/02/13 13:36:27  gerrit
 * made indent optional for skipEmptyTags
 *
 * Revision 1.25  2007/08/03 08:48:06  gerrit
 * removed unused imports
 *
 * Revision 1.24  2007/07/26 16:23:08  gerrit
 * move most configuration to TransformerPool
 *
 * Revision 1.23  2007/07/17 10:51:36  gerrit
 * added check for null-input
 *
 * Revision 1.22  2007/04/24 11:35:47  gerrit
 * added skip empty tags feature
 *
 * Revision 1.21  2006/08/22 12:56:04  gerrit
 * allow use of parameters in xpathExpression
 *
 * Revision 1.20  2006/01/05 14:36:31  gerrit
 * updated javadoc
 *
 * Revision 1.19  2005/10/24 09:20:18  gerrit
 * made namespaceAware an attribute of AbstractPipe
 *
 * Revision 1.18  2005/08/09 15:55:32  gerrit
 * avoid nullpointer in configure when stylesheet does not exist
 *
 * Revision 1.17  2005/06/20 09:03:05  gerrit
 * added outputType attribute (for xpath-expressions)
 *
 * Revision 1.16  2005/06/13 11:46:26  gerrit
 * corrected version-string
 *
 * Revision 1.15  2005/06/13 11:45:02  gerrit
 * added attribute 'namespaceAware'
 *
 * Revision 1.14  2005/01/10 08:56:10  gerrit
 * Xslt parameter handling by Maps instead of by Ibis parameter system
 *
 * Revision 1.13  2004/10/19 15:27:19  gerrit
 * moved transformation to pool
 *
 * Revision 1.12  2004/10/14 16:11:12  gerrit
 * changed ParameterResolutionContext from Object,Hashtable to String, PipelineSession
 *
 * Revision 1.11  2004/10/12 15:13:43  gerrit
 * changed handling of output of  XmlDeclaration
 *
 * Revision 1.10  2004/10/05 10:55:59  gerrit
 * reworked pooling (using TransformerPool)
 * included functionality of XPathPipe:
 *   added xpathExpression attribute
 *   added sessionKey attribute
 *
 */
package nl.nn.adapterframework.pipes;

import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.core.PipeStartException;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.ParameterList;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;
import nl.nn.adapterframework.util.TransformerPool;
import nl.nn.adapterframework.util.XmlUtils;

import org.apache.commons.lang.StringUtils;


/**
 * Perform an XSLT transformation with a specified stylesheet.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>className</td><td>nl.nn.adapterframework.pipes.XsltPipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMaxThreads(int) maxThreads}</td><td>maximum number of threads that may call {@link #doPipe(Object, PipeLineSession)} simultaneously</td><td>0 (unlimited)</td></tr>
 * <tr><td>{@link #setDurationThreshold(long) durationThreshold}</td><td>if durationThreshold >=0 and the duration (in milliseconds) of the message processing exceeded the value specified the message is logged informatory</td><td>-1</td></tr>
 * <tr><td>{@link #setGetInputFromSessionKey(String) getInputFromSessionKey}</td><td>when set, input is taken from this session key, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setStoreResultInSessionKey(String) storeResultInSessionKey}</td><td>when set, the result is stored under this session key</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setNamespaceAware(boolean) namespaceAware}</td><td>controls namespace-awareness of transformation</td><td>application default</td></tr>
 * <tr><td>{@link #setStyleSheetName(String) styleSheetName}</td><td>stylesheet to apply to the input message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setXpathExpression(String) xpathExpression}</td><td>alternatively: XPath-expression to create stylesheet from</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setNamespaceDefs(String) namespaceDefs}</td><td>namespace defintions for xpathExpression. Must be in the form of a comma or space separated list of <code>prefix=namespaceuri</code>-definitions</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setOutputType(String) outputType}</td><td>either 'text' or 'xml'. Only valid for xpathExpression</td><td>text</td></tr>
 * <tr><td>{@link #setOmitXmlDeclaration(boolean) omitXmlDeclaration}</td><td>force the transformer generated from the XPath-expression to omit the xml declaration</td><td>true</td></tr>
 * <tr><td>{@link #setSessionKey(String) sessionKey}</td><td>If specified, the result is put 
 * in the PipeLineSession under the specified key, and the result of this pipe will be 
 * the same as the input (the xml). If NOT specified, the result of the xpath expression 
 * will be the result of this pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSkipEmptyTags(boolean) skipEmptyTags}</td><td>when set <code>true</code> empty tags in the output are removed</td><td>false</td></tr>
 * <tr><td>{@link #setIndentXml(boolean) indentXml}</td><td>when set <code>true</code>, result is pretty-printed. (only used when <code>skipEmptyTags="true"</code>)</td><td>true</td></tr>
 * <tr><td>{@link #setRemoveNamespaces(boolean) removeNamespaces}</td><td>when set <code>true</code> namespaces (and prefixes) in the input message are removed</td><td>false</td></tr>
 * <tr><td>{@link #setXslt2(boolean) xslt2}</td><td>when set <code>true</code> XSLT processor 2.0 (net.sf.saxon) will be used, otherwise XSLT processor 1.0 (org.apache.xalan)</td><td>false</td></tr>
 * </table>
 * <table border="1">
 * <tr><th>nested elements</th><th>description</th></tr>
 * <tr><td>{@link nl.nn.adapterframework.parameters.Parameter param}</td><td>any parameters defined on the pipe will be applied to the created transformer</td></tr>
 * </table>
 * </p>
 * <p><b>Exits:</b>
 * <table border="1">
 * <tr><th>state</th><th>condition</th></tr>
 * <tr><td>"success"</td><td>default</td></tr>
 * <tr><td><i>{@link #setForwardName(String) forwardName}</i></td><td>if specified</td></tr>
 * </table>
 * </p>
 * @version $Id$
 * @author Johan Verrips
 */

public class XsltPipe extends FixedForwardPipe {

	private TransformerPool transformerPool;
	private String xpathExpression=null;
	private String namespaceDefs = null; 
	private String outputType="text";
	private String styleSheetName;
	private boolean omitXmlDeclaration=true;
	private boolean indentXml=true;
	private String sessionKey=null;
	private boolean skipEmptyTags=false;
	private boolean removeNamespaces=false;
	private boolean xslt2=false;

	private TransformerPool transformerPoolSkipEmptyTags;
	private TransformerPool transformerPoolRemoveNamespaces;

	{
		setSizeStatistics(true);
	}
	
	/**
	 * The <code>configure()</code> method instantiates a transformer for the specified
	 * XSL. If the stylesheetname cannot be accessed, a ConfigurationException is thrown.
	 * @throws ConfigurationException
	 */
	public void configure() throws ConfigurationException {
	    super.configure();
	
		transformerPool = TransformerPool.configureTransformer0(getLogPrefix(null), getNamespaceDefs(), getXpathExpression(), getStyleSheetName(), getOutputType(), !isOmitXmlDeclaration(), getParameterList(), isXslt2());
		if (isSkipEmptyTags()) {
			String skipEmptyTags_xslt = XmlUtils.makeSkipEmptyTagsXslt(isOmitXmlDeclaration(),isIndentXml());
			log.debug("test [" + skipEmptyTags_xslt + "]");
			try {
				transformerPoolSkipEmptyTags = new TransformerPool(skipEmptyTags_xslt);
			} catch (TransformerConfigurationException te) {
				throw new ConfigurationException(getLogPrefix(null) + "got error creating transformer from skipEmptyTags", te);
			}
		}
		if (isRemoveNamespaces()) {
			String removeNamespaces_xslt = XmlUtils.makeRemoveNamespacesXslt(isOmitXmlDeclaration(),isIndentXml());
			log.debug("test [" + removeNamespaces_xslt + "]");
			try {
				transformerPoolRemoveNamespaces = new TransformerPool(removeNamespaces_xslt);
			} catch (TransformerConfigurationException te) {
				throw new ConfigurationException(getLogPrefix(null) + "got error creating transformer from removeNamespaces", te);
			}
		}

		if (isXslt2()) {
			ParameterList parameterList = getParameterList();
			for (int i=0; i<parameterList.size(); i++) {
				Parameter parameter = parameterList.getParameter(i);
				if (StringUtils.isNotEmpty(parameter.getType()) && "node".equalsIgnoreCase(parameter.getType())) {
					throw new ConfigurationException(getLogPrefix(null) + "type \"node\" is not permitted in combination with XSLT 2.0, use type \"domdoc\"");
				}
			}
		}
	}

	public void start() throws PipeStartException {
		super.start();
		if (transformerPoolRemoveNamespaces!=null) {
			try {
				transformerPoolRemoveNamespaces.open();
			} catch (Exception e) {
				throw new PipeStartException(getLogPrefix(null)+"cannot start TransformerPool RemoveNamespaces", e);
			}
		}
		if (transformerPool!=null) {
			try {
				transformerPool.open();
			} catch (Exception e) {
				throw new PipeStartException(getLogPrefix(null)+"cannot start TransformerPool", e);
			}
		}
		if (transformerPoolSkipEmptyTags!=null) {
			try {
				transformerPoolSkipEmptyTags.open();
			} catch (Exception e) {
				throw new PipeStartException(getLogPrefix(null)+"cannot start TransformerPool SkipEmptyTags", e);
			}
		}
	}
	
	public void stop() {
		super.stop();
		if (transformerPoolRemoveNamespaces!=null) {
			transformerPoolRemoveNamespaces.close();
		}
		if (transformerPool!=null) {
			transformerPool.close();
		}
		if (transformerPoolSkipEmptyTags!=null) {
			transformerPoolSkipEmptyTags.close();
		}
	}
	
	/**
	 * Here the actual transforming is done. Under weblogic the transformer object becomes
	 * corrupt when a not-well formed xml was handled. The transformer is then re-initialized
	 * via the configure() and start() methods.
	 */
	public PipeRunResult doPipe(Object input, IPipeLineSession session) throws PipeRunException {
		if (input==null) {
			throw new PipeRunException(this,
				getLogPrefix(session)+"got null input");
		}
 	    if (!(input instanceof String)) {
	        throw new PipeRunException(this,
	            getLogPrefix(session)+"got an invalid type as input, expected String, got "
	                + input.getClass().getName());
	    }
	    
		ParameterList parameterList = null;
		//ParameterResolutionContext prc = new ParameterResolutionContext((String)input, session, isNamespaceAware()); 
	    try {
			String stringResult = (String)input;

			if (isRemoveNamespaces()) {
				log.debug(getLogPrefix(session)+ " removing namespaces from input message");
				ParameterResolutionContext prc_RemoveNamespaces = new ParameterResolutionContext(stringResult, session, isNamespaceAware()); 
				stringResult = transformerPoolRemoveNamespaces.transform(prc_RemoveNamespaces.getInputSource(), null); 
				log.debug(getLogPrefix(session)+ " output message after removing namespaces [" + stringResult + "]");
			}

			ParameterResolutionContext prc = new ParameterResolutionContext(stringResult, session, isNamespaceAware(), isXslt2());
			Map parametervalues = null;
			if (getParameterList()!=null) {
				parameterList =  getParameterList();
				parametervalues = prc.getValueMap(parameterList);
			}
			
	        stringResult = transformerPool.transform(prc.getInputSource(), parametervalues); 

			if (isSkipEmptyTags()) {
				log.debug(getLogPrefix(session)+ " skipping empty tags from result [" + stringResult + "]");
				//URL xsltSource = ClassUtils.getResourceURL( this, skipEmptyTags_xslt);
				//Transformer transformer = XmlUtils.createTransformer(xsltSource);
				//stringResult = XmlUtils.transformXml(transformer, stringResult);
				ParameterResolutionContext prc_SkipEmptyTags = new ParameterResolutionContext(stringResult, session, true, true); 
				stringResult = transformerPoolSkipEmptyTags.transform(prc_SkipEmptyTags.getInputSource(), null); 
			}

			if (StringUtils.isEmpty(getSessionKey())){
				return new PipeRunResult(getForward(), stringResult);
			}
			session.put(getSessionKey(), stringResult);
			return new PipeRunResult(getForward(), input);
	    } 
	    catch (Exception e) {
	        throw new PipeRunException(this, getLogPrefix(session)+" Exception on transforming input", e);
	    } 
	}

	/**
	 * Specify the stylesheet to use
	 */
	public void setStyleSheetName(String stylesheetName){
		this.styleSheetName=stylesheetName;
	}
	public String getStyleSheetName() {
		return styleSheetName;
	}

	/**
	 * set the "omit xml declaration" on the transfomer. Defaults to true.
	 * @return true or false
	 */
	public void setOmitXmlDeclaration(boolean b) {
		omitXmlDeclaration = b;
	}
	public boolean isOmitXmlDeclaration() {
		return omitXmlDeclaration;
	}


	public void setXpathExpression(String string) {
		xpathExpression = string;
	}
	public String getXpathExpression() {
		return xpathExpression;
	}

	public void setNamespaceDefs(String namespaceDefs) {
		this.namespaceDefs = namespaceDefs;
	}
	public String getNamespaceDefs() {
		return namespaceDefs;
	}

	/**
	 * The name of the key in the <code>PipeLineSession</code> to store the input in
	 * @see nl.nn.adapterframework.core.PipeLineSession
	 */
	public void setSessionKey(String newSessionKey) {
		sessionKey = newSessionKey;
	}
	public String getSessionKey() {
		return sessionKey;
	}


	public void setOutputType(String string) {
		outputType = string;
	}
	public String getOutputType() {
		return outputType;
	}


	public void setSkipEmptyTags(boolean b) {
		skipEmptyTags = b;
	}
	public boolean isSkipEmptyTags() {
		return skipEmptyTags;
	}

	public void setIndentXml(boolean b) {
		indentXml = b;
	}
	public boolean isIndentXml() {
		return indentXml;
	}

	public void setRemoveNamespaces(boolean b) {
		removeNamespaces = b;
	}
	public boolean isRemoveNamespaces() {
		return removeNamespaces;
	}

	public boolean isXslt2() {
		return xslt2;
	}

	public void setXslt2(boolean b) {
		xslt2 = b;
	}
}
