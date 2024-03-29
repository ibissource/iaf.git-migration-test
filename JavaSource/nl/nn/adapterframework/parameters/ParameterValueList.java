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
 * $Log: ParameterValueList.java,v $
 * Revision 1.8  2011-11-30 13:52:03  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2008/01/29 12:13:13  gerrit
 * added removeParameterValue()
 *
 * Revision 1.5  2007/10/08 13:31:49  gerrit
 * changed ArrayList to List where possible
 *
 * Revision 1.4  2007/10/08 12:21:23  gerrit
 * changed HashMap to Map where possible
 *
 * Revision 1.3  2005/10/24 09:59:23  unknown2
 * Add support for pattern parameters, and include them into several listeners,
 * senders and pipes that are file related
 *
 * Revision 1.2  2004/10/12 15:09:13  gerrit
 * only nested map and list
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.nn.adapterframework.core.ParameterException;

/**
 * List of parametervalues.
 * 
 * @author Gerrit van Brakel
 * @version $Id$
 */
public class ParameterValueList {
	
	List list;
	Map   map;
	
	public ParameterValueList() {
		super();
		list = new ArrayList();
		map  = new HashMap();
	}

	public ParameterValueList(int i) {
		super();
		list = new ArrayList(i);
		map  = new HashMap();
	}
	
	public void add(ParameterValue pv) {
		list.add(pv);
		map.put(pv.getDefinition().getName(),pv);
	}
	
	public ParameterValue getParameterValue(int i) {
		return (ParameterValue)(list.get(i));
	}

	public ParameterValue getParameterValue(String name) {
		return (ParameterValue)(map.get(name));
	}

	public ParameterValue removeParameterValue(String name) {
		ParameterValue pv = (ParameterValue)map.remove(name);
		if (pv!=null) {
			list.remove(pv);
		}
		return pv;
	}
	
	public boolean parameterExists(String name) {
		return map.get(name)!=null;
	}

	public int size() {
		return list.size();
	}
	
	Map getParameterValueMap() {
		return map;
	}

	/*
	 * Helper routine for quickly iterating through the resolved parameters
	 * in the order in which they are defined 
	 */
	public void forAllParameters(IParameterHandler handler) throws ParameterException {
		for (Iterator param = list.iterator(); param.hasNext();) {
			ParameterValue paramValue = (ParameterValue)param.next();
			handler.handleParam(paramValue.getDefinition().getName(), paramValue.getValue());
		}
	}
}
