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
 * $Log: ParameterResolutionContext.java,v $
 * Revision 1.19  2012-06-01 10:52:57  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.18  2011/11/30 13:52:03  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.16  2008/10/27 08:08:50  peter
 * XSLT 2.0 made possible
 *
 * Revision 1.15  2008/10/23 14:16:51  peter
 * XSLT 2.0 made possible
 *
 * Revision 1.14  2007/10/08 12:21:02  gerrit
 * changed HashMap to Map where possible
 *
 * Revision 1.13  2007/02/12 13:59:42  gerrit
 * Logger from LogUtil
 *
 * Revision 1.12  2006/11/06 08:19:52  gerrit
 * added default constructor
 *
 * Revision 1.11  2005/10/26 08:49:57  gerrit
 * reintroduced check for empty parameterlist in getValueMap
 *
 * Revision 1.10  2005/10/24 09:59:24  unknown2
 * Add support for pattern parameters, and include them into several listeners,
 * senders and pipes that are file related
 *
 * Revision 1.9  2005/10/17 11:43:34  gerrit
 * namespace-awareness configurable
 *
 * Revision 1.8  2005/06/13 11:55:21  gerrit
 * made namespaceAware
 *
 * Revision 1.7  2005/06/02 11:47:07  gerrit
 * obtain source from XmlUtils
 *
 * Revision 1.6  2005/03/31 08:15:48  gerrit
 * generalized Source
 *
 * Revision 1.5  2005/02/24 10:49:56  johan
 * 4.2.e dd 24-02-2005
 *
 * Revision 1.4  2005/02/10 08:15:24  gerrit
 * fixed bug in map-generation
 *
 * Revision 1.3  2005/01/13 08:08:33  gerrit
 * Xslt parameter handling by Maps instead of by Ibis parameter system
 *
 * Revision 1.2  2004/10/14 16:07:34  gerrit
 * changed from Object,Hashtable to String, PipelineSession
 *
 * Revision 1.1  2004/10/05 09:51:54  gerrit
 * changed from ParameterResolver to ParameterResolutionContext
 * moved to package parameters
 *
 */
package nl.nn.adapterframework.parameters;


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.IbisException;
import nl.nn.adapterframework.core.ParameterException;
import nl.nn.adapterframework.util.DomBuilderException;
import nl.nn.adapterframework.util.LogUtil;
import nl.nn.adapterframework.util.XmlUtils;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
 
/**
 * Determines the parameter values of the specified parameter during runtime
 * 
 * @author Gerrit van Brakel
 * @version $Id$
 */
public class ParameterResolutionContext {
	public static final String version="$RCSfile: ParameterResolutionContext.java,v $ $Revision: 1.19 $ $Date: 2012-06-01 10:52:57 $";
	protected Logger log = LogUtil.getLogger(this);

	private String input;
	private IPipeLineSession session;
	private Source xmlSource;
	private boolean namespaceAware;
	private boolean xslt2;

	/**
	 * constructor
	 * @param input contains the input (xml formatted) message
	 * @param session 
	 */		
	public ParameterResolutionContext(String input, IPipeLineSession session, boolean namespaceAware, boolean xslt2) {
		this.input = input;
		this.session = session;
		this.namespaceAware = namespaceAware;
		this.xslt2 = xslt2;
	}

	public ParameterResolutionContext(String input, IPipeLineSession session, boolean namespaceAware) {
		this(input, session, namespaceAware, false);
	}

	public ParameterResolutionContext(String input, IPipeLineSession session) {
		this(input, session, XmlUtils.isNamespaceAwareByDefault());
	}

	public ParameterResolutionContext(Source xmlSource, IPipeLineSession session, boolean namespaceAware) {
		this("", session, namespaceAware);
		this.xmlSource=xmlSource;
	}

	public ParameterResolutionContext(Source xmlSource, IPipeLineSession session) {
		this(xmlSource, session, XmlUtils.isNamespaceAwareByDefault());
	}

	public ParameterResolutionContext() {
	}
			
	/**
	 * @param p
	 * @return value as a <link>ParameterValue<link> object
	 * @throws IbisException
	 */
	private ParameterValue getValue(ParameterValueList alreadyResolvedParameters, Parameter p) throws ParameterException {
		return new ParameterValue(p, p.getValue(alreadyResolvedParameters, this));
	}
	
	/**
	 * @param parameters
	 * @return arraylist of <link>ParameterValue<link> objects
	 */
	public ParameterValueList getValues(ParameterList parameters) throws ParameterException {
		if (parameters == null)
			return null;
		
		ParameterValueList result = new ParameterValueList(parameters.size());
		for (Iterator it= parameters.iterator(); it.hasNext(); ) {
			result.add(getValue(result, (Parameter)it.next()));
		}
		return result;
	}

	/**
	 * @param parameters
	 * @return map of value objects
	 */
	public HashMap getValueMap(ParameterList parameters) throws ParameterException {
		if (parameters==null) {
			return null;
		}
		Map paramValuesMap = getValues(parameters).getParameterValueMap();

		// convert map with parameterValue to map with value		
		HashMap result = new HashMap(paramValuesMap.size());
		for (Iterator it= paramValuesMap.values().iterator(); it.hasNext(); ) {
			ParameterValue pv = (ParameterValue)it.next();
			result.put(pv.getDefinition().getName(), pv.getValue());
		}
		return result;
	}
	

	public ParameterValueList forAllParameters(ParameterList parameters, IParameterHandler handler) throws ParameterException {
		ParameterValueList values = getValues(parameters);
		if (values != null) {
			values.forAllParameters(handler);
		}
		return values;
	}
		
	/**
	 * @return the DOM document parsed from the (xml formatted) input
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Source getInputSource() throws DomBuilderException {
		if (xmlSource == null) {
			log.debug("Constructing InputSource for ParameterResolutionContext");
			xmlSource = XmlUtils.stringToSource(input,isNamespaceAware()); 

		}
		return xmlSource;
	}

	/**
	 * @return the (possibly xml formatted) input message
	 */
	public String getInput() {
		return input;
	}

	/**
	 * @return hashtable with session variables
	 */
	public IPipeLineSession getSession() {
		return session;
	}

	/**
	 * @param input the (xml formatted) input message
	 */
	public void setInput(String input) {
		this.input = input;
		this.xmlSource = null;
	}

	/**
	 * @param session
	 */
	public void setSession(IPipeLineSession session) {
		this.session = session;
	}

	/**
	 * @return
	 */
	public boolean isNamespaceAware() {
		return namespaceAware;
	}

	/**
	 * @param b
	 */
	public void setNamespaceAware(boolean b) {
		namespaceAware = b;
	}

	public boolean isXslt2() {
		return xslt2;
	}

	/**
	 * @param b
	 */
	public void setXslt2(boolean b) {
		xslt2 = b;
	}
}
