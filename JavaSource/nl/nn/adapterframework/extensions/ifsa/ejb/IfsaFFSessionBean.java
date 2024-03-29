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
 * $Log: IfsaFFSessionBean.java,v $
 * Revision 1.4  2011-11-30 13:51:58  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2007/11/22 08:48:19  gerrit
 * update from ejb-branch
 *
 * Revision 1.1.2.2  2007/11/15 12:59:51  tim
 * Add bit more logging
 *
 * Revision 1.1.2.1  2007/10/29 12:25:34  tim
 * Create EJb Beans required to connect to IFSA J2EE implementation as an IFSA Provider application
 *
 * 
 */

package nl.nn.adapterframework.extensions.ifsa.ejb;

import com.ing.ifsa.api.FireForgetService;
import com.ing.ifsa.api.ServiceRequest;
import com.ing.ifsa.exceptions.ServiceException;
import java.rmi.RemoteException;
import javax.ejb.SessionBean;

/**
 *
 * @author Tim van der Leeuw
 * @version $Id$
 */
public class IfsaFFSessionBean extends IfsaEjbBeanBase implements SessionBean, FireForgetService {

    public void onServiceRequest(ServiceRequest request) throws RemoteException, ServiceException {
        log.debug(">>> onServiceRequest() Processing FF Request from IFSA");
        processRequest(request);
        log.debug("<<< onServiceRequest() Done processing FF Request from IFSA");
    }

}
