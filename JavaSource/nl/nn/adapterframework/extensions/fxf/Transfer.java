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
 * $Log: Transfer.java,v $
 * Revision 1.3  2011-11-30 13:51:51  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:54  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2009/03/04 15:56:57  gerrit
 * added support for FXF 2.0
 *
 */
package nl.nn.adapterframework.extensions.fxf;

/**
 * Placeholder for Transfer-information of Trigger message.
 * 
 * @author  Gerrit van Brakel
 * @since   FXF 2.0
 * @version $Id$
 */
public class Transfer {
	
	private String name;
	private String filename;

	public String getName() {
		return name;
	}
	public void setName(String string) {
		name = string;
	}

	public void setFilename(String string) {
		filename = string;
	}
	public String getFilename() {
		return filename;
	}

}
