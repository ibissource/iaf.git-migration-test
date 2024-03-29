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
 * $Log: SoapConfigManager.java,v $
 * Revision 1.10  2012-02-06 13:18:19  l190409
 * improved SOAP error logging
 *
 * Revision 1.9  2011/11/30 13:51:49  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.7  2007/02/12 14:12:03  gerrit
 * Logger from LogUtil
 *
 * Revision 1.6  2005/10/18 08:18:49  gerrit
 * corrected version string
 *
 */
package nl.nn.adapterframework.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import nl.nn.adapterframework.soap.LoggingSOAPFaultListener;
import nl.nn.adapterframework.soap.SoapGenericProvider;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.soap.Constants;
import org.apache.soap.SOAPException;
import org.apache.soap.server.DeploymentDescriptor;
import org.apache.soap.server.XMLConfigManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Read-only implementation of org.apache.soap.server.XMLConfigManager.
 *
 * Uses resources rather than files as the original XMLConfigManager. The latter does not work
 * on BEA WebLogic and probably other Containers that do not extract their .war or .ear files,
 * but run them directly from the archive-file itself.
 *
 * <b>Note</b>: Make sure the <code>soap.xml</code>-file is placed in the 'current' directory. This still
 * needs to be a file, but often only single file per instance is sufficient.
 * @version $Id$
 *
 * @author Gerrit van Brakel IOS
 */
public class SoapConfigManager extends XMLConfigManager {
	public static final String version="$RCSfile: SoapConfigManager.java,v $ $Revision: 1.10 $ $Date: 2012-02-06 13:18:19 $";
    protected Logger log = LogUtil.getLogger(this);

	private String defaultProvider=SoapGenericProvider.class.getName();    
	DeploymentDescriptor defaultDD = null; 
    
	public void loadRegistry() throws SOAPException {
	    URL servicesRegistry;
	    String message;
	    try {
	        servicesRegistry = context.getResource(filename);
	    } catch (MalformedURLException e) {
		    message = "cannot find URL for registry from resource-name '" + filename + "'";
		    log.error(message,e);
	        throw new SOAPException(Constants.FAULT_CODE_SERVER, message, e);
	    }
	
	    if (servicesRegistry == null) {
			message = "cannot find registry from resource-name '" + filename + "'";
			log.error(message);
	        throw new SOAPException(Constants.FAULT_CODE_SERVER, message);
	    }
	
	    Element element = null;
	    try {
	        Document document = xdb.parse(servicesRegistry.openStream());
	        element = document.getDocumentElement();
	    } catch (Exception e) {
		    message = "exception while reading servicesRegistry from "+servicesRegistry;
			log.error(message,e);
	        throw new SOAPException(Constants.FAULT_CODE_SERVER, message, e);
	    }
	    log.info("loading servicesRegistry from "+servicesRegistry);;
	    NodeList nodelist =
	        element.getElementsByTagNameNS(
	            "http://xml.apache.org/xml-soap/deployment",
	            "service");
	    int i = nodelist.getLength();
	    dds = new Hashtable();
	    for (int j = 0; j < i; j++) {
	        Element element1 = (Element) nodelist.item(j);
	        DeploymentDescriptor deploymentdescriptor =
	            DeploymentDescriptor.fromXML(element1);
	        String s = deploymentdescriptor.getID();
	        log.info("deploying service "+s);
	        dds.put(s, deploymentdescriptor);
	    }
	}
	
	public DeploymentDescriptor query(String id) throws SOAPException {
		DeploymentDescriptor dd = super.query(id);
		if (dd==null) {
			if (defaultDD==null) {
				synchronized (this) {
					if (defaultDD==null && StringUtils.isNotEmpty(getDefaultProvider())) {
						defaultDD = new DeploymentDescriptor();
						defaultDD.setID("urn:default");
						defaultDD.setScope(DeploymentDescriptor.SCOPE_REQUEST);
						defaultDD.setServiceClass(getDefaultProvider());
						defaultDD.setProviderType(DeploymentDescriptor.PROVIDER_USER_DEFINED);
						defaultDD.setIsStatic(true);
						defaultDD.setMethods(new String[]{"dummy"});
						defaultDD.setFaultListener(new String[]{"org.apache.soap.server.DOMFaultListener"});
						defaultDD.setDefaultSMRClass(SoapMappingRegistryWithDefault.class.getName());
					}
				}
			}
			dd=defaultDD;
		}
		dd.setFaultListener(new String[] {LoggingSOAPFaultListener.class.getName()});
		log.info("SoapConfigManager.query["+id+"] returned DeploymentDescriptor ["+dd+"]");
		return (dd);
	}
	
    public void saveRegistry()
        throws SOAPException
    {
        throw new SOAPException(Constants.FAULT_CODE_SERVER, "Will not save services-registry: this is a read-only ConfigManager ");
    }

	public String getDefaultProvider() {
		return defaultProvider;
	}
	public void setDefaultProvider(String defaultProvider) {
		this.defaultProvider = defaultProvider;
	}

}
