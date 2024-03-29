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
 * $Log: IPostboxSender.java,v $
 * Revision 1.5  2011-11-30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.3  2004/10/19 06:39:20  gerrit
 * modified parameter handling, introduced IWithParameters
 *
 * Revision 1.2  2004/10/05 10:03:04  gerrit
 * replaced by IParameterizedSender
 *
 * Revision 1.1  2004/05/21 07:59:30  unknown0
 * Add (modifications) due to the postbox sender implementation
 *
 */
package nl.nn.adapterframework.core;

/**
 * The <code>IPostboxSender</code> is responsible for storing a message
 * in a postbox
 *
 * @author john
 * @version $Id$
 */
public interface IPostboxSender extends ISenderWithParameters {
}
