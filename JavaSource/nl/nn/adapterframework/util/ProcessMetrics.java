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
 * $Log: ProcessMetrics.java,v $
 * Revision 1.5  2011-11-30 13:51:48  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.3  2006/08/21 15:15:43  gerrit
 * correctly formatted current time
 *
 * Revision 1.2  2006/07/10 14:36:29  gerrit
 * added currentTime, separate function normalizedNotation()
 *
 * Revision 1.1  2004/07/20 13:27:29  gerrit
 * first version
 *
 *  */
package nl.nn.adapterframework.util;

import java.util.Date;

/**
 * Utility class to report process parameters like memory usage as an xml-element.
 * 
 * @since 4.2
 */
public class ProcessMetrics {

	private final static long K_LIMIT=10*1024;
	private final static long M_LIMIT=K_LIMIT*1024;
	private final static long G_LIMIT=M_LIMIT*1024;
	private final static long T_LIMIT=G_LIMIT*1024;
	
	public static String normalizedNotation(long value) {
		String valueString;
		
		if (value < K_LIMIT) {
			valueString = Long.toString(value);
		} else {
			if (value < M_LIMIT) {
				valueString = Long.toString(value/1024)+"K";
			} else { 
				if (value < G_LIMIT) {
					valueString = Long.toString(value/(1024*1024))+"M";
				} else { 
					if (value < T_LIMIT) {
						valueString = Long.toString(value/(1024*1024*1024))+"G";
					} else { 
						valueString = Long.toString(value/(1024*1024*1024*1024))+"T";
					}
				}
			}
		}
		return valueString;
	}
	
	public static void addNumberProperty(XmlBuilder list, String name, long value) {
		addProperty(list,name,normalizedNotation(value));
	}
	
	public static void addProperty(XmlBuilder list, String name, String value) {
		XmlBuilder p=new XmlBuilder("property");
		p.addAttribute("name", name);
		p.setValue(value);
		list.addSubElement(p);
	}

	public static String toXml() {
		XmlBuilder xmlh=new XmlBuilder("processMetrics");
		XmlBuilder props=new XmlBuilder("properties");
		xmlh.addSubElement(props);
		
		long freeMem = Runtime.getRuntime().freeMemory();
		long totalMem = Runtime.getRuntime().totalMemory();
		
		addNumberProperty(props, "freeMemory", freeMem);
		addNumberProperty(props, "totalMemory", totalMem);
		addNumberProperty(props, "heapSize", totalMem-freeMem);
		addProperty(props, "currentTime", DateUtils.format(new Date(),DateUtils.FORMAT_FULL_GENERIC));
		return xmlh.toXML();
	}


}
