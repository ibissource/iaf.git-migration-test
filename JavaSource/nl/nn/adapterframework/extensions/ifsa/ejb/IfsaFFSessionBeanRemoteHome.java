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
 * $Log: IfsaFFSessionBeanRemoteHome.java,v $
 * Revision 1.4  2011-11-30 13:51:57  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2007/11/22 08:48:20  gerrit
 * update from ejb-branch
 *
 * Revision 1.1.2.3  2007/11/08 13:51:30  tim
 * create() method should return Remote versions of EJB Bean interfaces
 *
 * Revision 1.1.2.2  2007/11/08 13:31:36  tim
 * create() method should throw RemoteException
 *
 * Revision 1.1.2.1  2007/11/01 10:35:24  tim
 * Add remote interfaces for IFSA Session beans, since that is what's expected by the IFSA libraries
 *
 */

package nl.nn.adapterframework.extensions.ifsa.ejb;

import javax.ejb.EJBHome;

/**
 *
 * @author Tim van der Leeuw
 * @version $Id$
 */
public interface IfsaFFSessionBeanRemoteHome extends EJBHome {
    public IfsaFFSessionBeanRemote create()
        throws javax.ejb.CreateException, java.rmi.RemoteException;

}
