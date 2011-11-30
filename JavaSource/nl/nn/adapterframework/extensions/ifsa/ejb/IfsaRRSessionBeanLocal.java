/*
 * $Log: IfsaRRSessionBeanLocal.java,v $
 * Revision 1.4  2011-11-30 13:51:58  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2007/11/22 08:48:19  gerrit
 * update from ejb-branch
 *
 * Revision 1.1.2.1  2007/10/29 12:25:34  tim
 * Create EJb Beans required to connect to IFSA J2EE implementation as an IFSA Provider application
 *
 * 
 */

package nl.nn.adapterframework.extensions.ifsa.ejb;

import com.ing.ifsa.api.RequestReplyService;
import javax.ejb.EJBLocalObject;

/**
 *
 * @author Tim van der Leeuw
 * @version Id
 */
public interface IfsaRRSessionBeanLocal extends RequestReplyService, EJBLocalObject {

}
