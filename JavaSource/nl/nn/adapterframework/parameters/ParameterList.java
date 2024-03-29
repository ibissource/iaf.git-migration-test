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
 * $Log: ParameterList.java,v $
 * Revision 1.5  2011-11-30 13:52:03  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.3  2006/10/13 08:15:57  gerrit
 * added findParameter()
 *
 * Revision 1.2  2004/10/12 15:07:26  gerrit
 * added configure()-method
 *
 * Revision 1.1  2004/10/05 09:52:25  gerrit
 * moved parameter code  to package parameters
 *
 * Revision 1.1  2004/05/21 07:58:47  unknown0
 * Moved PipeParameter to core
 *
 */
package nl.nn.adapterframework.parameters;

import java.util.ArrayList;
import java.util.Iterator;

import nl.nn.adapterframework.configuration.ConfigurationException;


/**
 * List of parameters.
 * 
 * @author Gerrit van Brakel
 * @version $Id$
 */
public class ParameterList extends ArrayList {
	
	public ParameterList() {
		super();
	}

	public ParameterList(int i) {
		super(i);
	}
	
	public void configure() throws ConfigurationException {
		for (int i=0; i<size(); i++) {
			getParameter(i).configure();
		}
	}
	
	public Parameter getParameter(int i) {
		return (Parameter)get(i);
	}
	
	public Parameter findParameter(String name) {
		for (Iterator it=iterator();it.hasNext();) {
			Parameter p = (Parameter)it.next();
			if (p!=null && p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
}
