/*
 * $Log: IReceiver.java,v $
 * Revision 1.6  2004-08-25 09:11:33  a1909356#db2admin
 * Add waitForRunstate with timeout
 *
 * Revision 1.5  2004/03/31 12:04:20  gerrit
 * fixed javadoc
 *
 * Revision 1.4  2004/03/30 07:29:54  gerrit
 * updated javadoc
 *
 * Revision 1.3  2004/03/26 10:42:50  johan
 * added @version tag in javadoc
 *
 */
package nl.nn.adapterframework.core;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.util.RunStateEnum;

/**
 * The receiver is the trigger and central communicator for the framework.
 * <br/>
 * The main responsibilities are:
 * <ul>
 *    <li>receiving messages</li>
 *    <li>for asynchronous receivers (which have a separate sender):<br/>
 *            <ul><li>initializing ISender objects</li>
 *                <li>stopping ISender objects</li>
 *                <li>sending the message with the ISender object</li>
 *            </ul>
 *    <li>synchronous receivers give the result directly</li>
 *    <li>take care of connection, sessions etc. to startup and shutdown</li>
 * </ul>
 * Listeners call the IAdapter.processMessage(String correlationID,String message)
 * to do the actual work, which returns a <code>{@link PipeLineResult}</code>. The receiver
 * may observe the status in the <code>{@link PipeLineResult}</code> to perfom committing
 * requests.
 * 
 *  @version Id
 *  @author Johan Verrips
 *  @see IAdapter
 *  @see IAdapter#processMessage(String, String)
 *  @see ISender
 *  @see PipeLineResult
 *
 */
public interface IReceiver extends IManagable {
	public static final String version="$Id: IReceiver.java,v 1.6 2004-08-25 09:11:33 a1909356#db2admin Exp $";

 	/**
 	 * This method is called by the <code>IAdapter</code> to let the
 	 * receiver do things to initialize itself before the <code>startListening</code>
 	 * method is called.
 	 * @see #startRunning
 	 * @throws ConfigurationException when initialization did not succeed.
 	 */ 
	public void configure() throws ConfigurationException;
	
	/**
	 * get the number of messages received by this receiver
	 */
	public long getMessagesReceived();
	
    /**
     * The processing of messages must be delegated to the <code>Adapter</code>
     * object. The adapter also provides a MessageKeeper, which the receiver
     * may use to store messages in.
     * @see nl.nn.adapterframework.core.IAdapter
     */
    public void setAdapter(IAdapter adapter);
    
	void waitForRunState(RunStateEnum requestedRunState) throws InterruptedException;
	boolean waitForRunState(RunStateEnum requestedRunState, long timeout) throws InterruptedException;
}
