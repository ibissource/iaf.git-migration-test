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
 * $Log: IAdapter.java,v $
 * Revision 1.19  2012-06-01 10:52:50  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.18  2011/11/30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.16  2011/08/22 14:21:39  gerrit
 * removed unused methods
 *
 * Revision 1.15  2010/09/13 13:35:50  gerrit
 * renamed configurePipes() into configure() (javadoc only)
 *
 * Revision 1.14  2009/12/29 14:32:20  gerrit
 * modified imports to reflect move of statistics classes to separate package
 *
 * Revision 1.13  2009/06/05 07:21:53  gerrit
 * added throws clause to forEachStatisticsKeeperBody()
 *
 * Revision 1.12  2008/09/04 12:02:50  gerrit
 * collect interval statistics
 *
 * Revision 1.11  2008/08/27 15:54:21  gerrit
 * *** empty log message ***
 *
 * Revision 1.10  2007/10/09 15:33:00  gerrit
 * copy changes from Ibis-EJB:
 * removed usertransaction-methods
 * added getErrorState()
 *
 * Revision 1.9  2005/12/28 08:34:45  gerrit
 * introduced StatisticsKeeper-iteration
 *
 * Revision 1.8  2005/07/05 12:28:56  gerrit
 * added possibility to end processing with an exception
 *
 * Revision 1.7  2005/01/13 08:55:15  gerrit
 * Make threadContext-attributes available in PipeLineSession
 *
 * Revision 1.6  2004/08/09 08:43:46  gerrit
 * added formatErrorMessage()
 *
 * Revision 1.5  2004/06/16 12:34:46  johan
 * Added AutoStart functionality on Adapter
 *
 * Revision 1.4  2004/03/26 10:42:50  johan
 * added @version tag in javadoc
 *
 * Revision 1.3  2004/03/23 17:36:58  gerrit
 * added methods for Transaction control
 *
 */
package nl.nn.adapterframework.core;

import java.util.Iterator;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.statistics.StatisticsKeeperIterationHandler;
import nl.nn.adapterframework.util.MessageKeeper;

/**
 * The Adapter is the central manager in the framework. It has knowledge of both
 * <code>IReceiver</code>s as well as the <code>PipeLine</code> and statistics.
 * The Adapter is the class that is responsible for configuring, initializing and
 * accessing/activating IReceivers, Pipelines, statistics etc.
 * 
 * @version $Id$
 **/
public interface IAdapter extends IManagable {

    /**
  	 * Instruct the adapter to configure itself. The adapter will call the
  	 * pipeline to configure itself, the pipeline will call the individual
  	 * pipes to configure themselves.
  	 * @see nl.nn.adapterframework.pipes.AbstractPipe#configure()
  	 * @see PipeLine#configure()
  	 */
  	public void configure() throws ConfigurationException;
  	
 	/**
 	 * The messagekeeper is used to keep the last x messages, relevant to
 	 * display in the web-functions.
 	 */ 
	public MessageKeeper getMessageKeeper();
	public IReceiver getReceiverByName(String receiverName);
	public Iterator getReceiverIterator();
	public PipeLineResult processMessage(String messageId, String message, IPipeLineSession pipeLineSession);
	public PipeLineResult processMessageWithExceptions(String messageId, String message, IPipeLineSession pipeLineSession) throws ListenerException;

  	public void registerPipeLine (PipeLine pipeline) throws ConfigurationException;
  	public void setName(String name);
  	public boolean isAutoStart();
	public String toString();

	public String formatErrorMessage(String errorMessage, Throwable t, String originalMessage, String messageID, INamedObject objectInError, long receivedTime);
		
	public void forEachStatisticsKeeperBody(StatisticsKeeperIterationHandler hski, Object data, int action) throws SenderException ;

    /**
     * state to put in PipeLineResult when a PipeRunException occurs.
     */
	public String getErrorState();
}
