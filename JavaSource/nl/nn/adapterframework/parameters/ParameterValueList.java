/*
 * $Log: ParameterValueList.java,v $
 * Revision 1.2  2004-10-12 15:09:13  L190409
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

/**
 * List of parametervalues.
 * 
 * @author Gerrit van Brakel
 * @version Id
 */
public class ParameterValueList {
	
	ArrayList list;
	HashMap   map;
	
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
	
	public boolean parameterExists(String name) {
		return map.get(name)!=null;
	}

	public int size() {
		return list.size();
	}

}
