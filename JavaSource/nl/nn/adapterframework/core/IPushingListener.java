/*
 * $Log: IPushingListener.java,v $
 * Revision 1.3  2004-09-08 14:15:11  L190409
 * removed unused imports
 *
 * Revision 1.2  2004/08/03 13:09:51  gerrit
 * moved afterMessageProcessed to IListener
 *
 * Revision 1.1  2004/07/15 07:38:22  gerrit
 * introduction of IListener as common root for Pulling and Pushing listeners
 *
 * Revision 1.2  2004/06/30 10:04:17  gerrit
 * added INamedObject implementation, added setExceptionListener
 *
 * Revision 1.1  2004/06/22 11:52:44  gerrit
 * first version
 *
 */
package nl.nn.adapterframework.core;

/**
 * Defines listening behaviour of message driven receivers.
 * 
 * @version Id
 * @author Gerrit van Brakel
 * @since 4.2
 */
public interface IPushingListener extends IListener {
	public static final String version="$Id: IPushingListener.java,v 1.3 2004-09-08 14:15:11 L190409 Exp $";


/**
 * Set the handler that will do the processing of the message.
 * Each of the received messages must be pushed through handler.processMessage()
 */
void setHandler(IMessageHandler handler);

/**
 * Set a (single) listener that will be notified of any exceptions.
 */
void setExceptionListener(IbisExceptionListener listener);

}
