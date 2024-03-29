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
 * $Log: SummaryRecord.java,v $
 * Revision 1.5  2011-11-30 13:52:02  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:52  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.3  2011/05/23 13:41:51  gerrit
 * renamed 'total' fields to 'sum'
 *
 * Revision 1.2  2010/01/07 13:18:15  gerrit
 * allow to show trends of statistics
 *
 * Revision 1.1  2009/12/29 14:25:18  gerrit
 * moved statistics to separate package
 *
 */
package nl.nn.adapterframework.statistics.parser;

import java.text.DecimalFormat;

import nl.nn.adapterframework.statistics.Basics;
import nl.nn.adapterframework.util.LogUtil;
import nl.nn.adapterframework.util.XmlBuilder;

import org.apache.log4j.Logger;

/**
 * Record use to gather statistics data from a file.
 * @author  Gerrit van Brakel
 * @since   4.9.10
 * @version $Id$
 */
public class SummaryRecord extends Basics {
	protected Logger log = LogUtil.getLogger(this);

	private String name;
	
	public void registerItem(Item item) {
		String name=item.getName();
		String value=item.getValue();
		if (!value.equals(ITEM_VALUE_NAN)) {
			if (ITEM_NAME_COUNT.equals(name)) {
				count=Long.parseLong(value);
			} else
			if (ITEM_NAME_MIN.equals(name)) {
				min=Long.parseLong(value);
			} else
			if (ITEM_NAME_MAX.equals(name)) {
				max=Long.parseLong(value);
			} else
			if (ITEM_NAME_SUM.equals(name)) {
				sum=Long.parseLong(value);
			} else
			if (ITEM_NAME_SUMSQ.equals(name)) {
				sumOfSquares=Long.parseLong(value);
			} 
		}
	}

	public XmlBuilder toXml(String name, DecimalFormat timeFormat, DecimalFormat percentageFormat) {
		return super.toXml("stat", name, timeFormat, percentageFormat);
	}

	public XmlBuilder toXml(DecimalFormat timeFormat, DecimalFormat percentageFormat) {
		return super.toXml("stat", getName(), timeFormat, percentageFormat);
	}

	public void setName(String string) {
		name = string;
	}
	public String getName() {
		return name;
	}
}
