/*
 * $Log: IfsaRRSessionBean.java,v $
 * Revision 1.2  2007-11-22 08:48:19  europe\L190409
 * update from ejb-branch
 *
 * Revision 1.1.2.3  2007/11/15 12:59:51  tim
 * Add bit more logging
 *
 * Revision 1.1.2.2  2007/11/01 10:35:24  tim
 * Add remote interfaces for IFSA Session beans, since that is what's expected by the IFSA libraries
 *
 * Revision 1.1.2.1  2007/10/29 12:25:34  tim
 * Create EJb Beans required to connect to IFSA J2EE implementation as an IFSA Provider application
 *
 * 
 */

package nl.nn.adapterframework.extensions.ifsa.ejb;

import com.ing.ifsa.api.BusinessMessage;
import com.ing.ifsa.api.RequestReplyService;
import com.ing.ifsa.api.ServiceReply;
import com.ing.ifsa.api.ServiceRequest;
import com.ing.ifsa.exceptions.ServiceException;
import java.rmi.RemoteException;
import javax.ejb.SessionBean;

/**
 *
 * @author Tim van der Leeuw
 * @version Id
 */
public class IfsaRRSessionBean extends IfsaEjbBeanBase implements SessionBean, RequestReplyService
{

    public ServiceReply onServiceRequest(ServiceRequest request) throws RemoteException, ServiceException {
        log.debug(">>> onServiceRequest() Processing RR Request from IFSA");
        String replyText = processRequest(request);

        ServiceReply reply = new ServiceReply(request, new BusinessMessage(replyText));
        log.debug("<<< onServiceRequest() Done processing RR Request from IFSA");
        return reply;
    }

}