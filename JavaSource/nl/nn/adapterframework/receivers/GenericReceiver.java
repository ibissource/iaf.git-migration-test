/*
 * $Log: GenericReceiver.java,v $
 * Revision 1.1  2004-08-03 13:04:30  L190409
 * introduction of GenericReceiver
 *
 * Revision 1.3  2004/03/30 07:30:04  gerrit
 * updated javadoc
 *
 * Revision 1.2  2004/03/26 10:43:03  johan
 * added @version tag in javadoc
 *
 * Revision 1.1  2004/03/23 17:24:08  gerrit
 * initial version
 *
 */
package nl.nn.adapterframework.receivers;

import nl.nn.adapterframework.core.IListener;
import nl.nn.adapterframework.core.IPullingListener;
import nl.nn.adapterframework.core.ISender;
import nl.nn.adapterframework.core.ITransactionalStorage;

/**
 * Plain extension of {@link ReceiverBase} that can be used directly in configurations.
 * Only extension is that the setters for its three worker-objects are public, and can therefore
 * be set from the configuration file.
 * For configuration options, see {@link PullingReceiverBase}.
 * 
 * @version Id
 * @author  Gerrit van Brakel
 * @since 4.1
 */
public class GenericReceiver extends ReceiverBase {
	public static final String version="$Id: GenericReceiver.java,v 1.1 2004-08-03 13:04:30 L190409 Exp $";

	public void setListener(IListener listener) {
		super.setListener(listener);
	}
	public void setInProcessStorage(ITransactionalStorage inProcessStorage) {
		super.setInProcessStorage(inProcessStorage);
	}
	public void setErrorSender(ISender errorSender) {
		super.setErrorSender(errorSender);
	}
	public void setSender(ISender sender) {
		super.setSender(sender);
	}
}
