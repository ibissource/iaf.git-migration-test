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
 * $Log: SapFunctionHandler.java,v $
 * Revision 1.1  2012-02-06 14:33:05  jaco
 * Implemented JCo 3 based on the JCo 2 code. JCo2 code has been moved to another package, original package now contains classes to detect the JCo version available and use the corresponding implementation.
 *
 * Revision 1.5  2011/11/30 13:51:54  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:52  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.3  2008/01/29 15:41:13  gerrit
 * removed version string
 *
 * Revision 1.2  2008/01/29 15:40:20  gerrit
 * added support for idocs
 *
 * Revision 1.1  2004/07/06 07:09:05  gerrit
 * moved SAP functionality to extensions
 *
 * Revision 1.1  2004/06/22 06:56:45  gerrit
 * First version of SAP package
 *
 */
package nl.nn.adapterframework.extensions.sap.jco2;

import com.sap.mw.jco.JCO;
import com.sap.mw.idoc.IDoc;

/**
 * The interface clients (users) of a SAP function must implement.
 *
 * @author  Gerrit van Brakel
 * @version $Id$
 */
public interface SapFunctionHandler {

	public void processFunctionCall(JCO.Function function) throws SapException;
	public void processIDoc(IDoc.Document idoc) throws SapException;
}
