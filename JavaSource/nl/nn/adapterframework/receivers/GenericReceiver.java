/*
 * $Log: GenericReceiver.java,v $
 * Revision 1.4.4.2  2007-10-10 14:30:44  europe\L190409
 * synchronize with HEAD (4.8-alpha1)
 *
 * Revision 1.5  2007/10/10 08:54:27  gerrit
 * added getAdapter()
 *
 * Revision 1.4  2007/05/21 12:24:20  gerrit
 * added public setMessageLog()
 *
 * Revision 1.3  2005/07/19 15:27:45  gerrit
 * added errorStorage nested element
 *
 * Revision 1.2  2004/10/05 09:59:56  gerrit
 * removed unused code
 *
 * Revision 1.1  2004/08/03 13:04:30  gerrit
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

import nl.nn.adapterframework.core.IAdapter;
import nl.nn.adapterframework.core.IListener;
import nl.nn.adapterframework.core.ISender;
import nl.nn.adapterframework.core.ITransactionalStorage;

/**
 * Plain extension of {@link ReceiverBase} that can be used directly in configurations.
 * Only extension is that the setters for its three worker-objects are public, and can therefore
 * be set from the configuration file.
 * For configuration options, see {@link ReceiverBase}.
 * 
 * @version Id
 * @author  Gerrit van Brakel
 * @since   4.1
 */
public class GenericReceiver extends ReceiverBase {
	public static final String version="$RCSfile: GenericReceiver.java,v $ $Revision: 1.4.4.2 $ $Date: 2007-10-10 14:30:44 $";

	public void setListener(IListener listener) {
		super.setListener(listener);
	}
	public void setInProcessStorage(ITransactionalStorage inProcessStorage) {
		super.setInProcessStorage(inProcessStorage);
	}
	public void setErrorSender(ISender errorSender) {
		super.setErrorSender(errorSender);
	}			
	public void setErrorStorage(ITransactionalStorage errorStorage) {
		super.setErrorStorage(errorStorage);
	}
	public void setMessageLog(ITransactionalStorage messageLog) {
		super.setMessageLog(messageLog);
	}
	
	public void setSender(ISender sender) {
		super.setSender(sender);
	}
    
    public IAdapter getAdapter() {
        return super.getAdapter();
    }
}
