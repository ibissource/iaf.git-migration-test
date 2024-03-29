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
 * $Log: IfsaRRSessionBeanRemote.java,v $
 * Revision 1.4  2011-11-30 13:51:57  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2007/11/22 08:48:20  gerrit
 * update from ejb-branch
 *
 * Revision 1.1.2.1  2007/11/01 10:35:25  tim
 * Add remote interfaces for IFSA Session beans, since that is what's expected by the IFSA libraries
 *
 */

package nl.nn.adapterframework.extensions.ifsa.ejb;

import com.ing.ifsa.api.RequestReplyService;
import javax.ejb.EJBObject;

/**
 *
 * @author Tim van der Leeuw
 * @version $Id$
 */
public interface IfsaRRSessionBeanRemote extends RequestReplyService, EJBObject {

}
