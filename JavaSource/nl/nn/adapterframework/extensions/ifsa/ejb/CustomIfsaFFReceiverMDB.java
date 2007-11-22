/*
 * $Log: CustomIfsaFFReceiverMDB.java,v $
 * Revision 1.2  2007-11-22 08:48:19  europe\L190409
 * update from ejb-branch
 *
 * Revision 1.1.2.6  2007/11/15 13:44:29  tim
 * Add logging of success-case
 *
 * Revision 1.1.2.5  2007/11/15 13:02:06  tim
 * Remove unused import
 *
 * Revision 1.1.2.4  2007/11/15 13:01:26  tim
 * Add bit more logging
 *
 * Revision 1.1.2.3  2007/11/14 08:54:33  tim
 * Use LogUtil to initialize logging (since this class in in IBIS, not in IFSA, it doesn't use Log4j loaded/initalized from same classloader as IFSA); put logger as protected instance-variable in AbstractBaseMDB class
 *
 * Revision 1.1.2.2  2007/11/02 13:01:09  tim
 * Add JavaDoc comment
 *
 * Revision 1.1.2.1  2007/11/02 11:47:05  tim
 * Add custom versions of IFSA MDB Receiver beans, and subclass of IFSA ServiceLocatorEJB
 *
 *
 * $Id: CustomIfsaFFReceiverMDB.java,v 1.2 2007-11-22 08:48:19 europe\L190409 Exp $
 *
 */
package nl.nn.adapterframework.extensions.ifsa.ejb;

import com.ing.ifsa.provider.FFReceiver;
import com.ing.ifsa.provider.Receiver;
import javax.jms.Message;

/**
 * IfsaReceiverMDB for FireForget services.
 * 
 * @author Tim van der Leeuw
 * @version Id
 */
public class CustomIfsaFFReceiverMDB extends CustomIfsaReceiverMDBAbstractBase {

    public void onMessage(Message msg) {
        if (log.isInfoEnabled()) {
            log.info(">>> onMessage() enter");
        }
        if (!((FFReceiver) receiver).handleMessage(msg)) {
            log.warn("*** onMessage() message was not handled succesfully, rollback transaction");
            getMessageDrivenContext().setRollbackOnly();
        } else {
            if (log.isInfoEnabled()) {
                log.info("Message was handled succesfully");
            }
        }
        if (log.isInfoEnabled()) {
            log.info("<<< onMessage exit");
        }
    }

    protected Receiver createReceiver() {
        return new FFReceiver(serviceLocator);
    }

}
