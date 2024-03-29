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
 * $Log: IRecordHandlerManager.java,v $
 * Revision 1.10  2012-06-01 10:52:48  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.9  2011/11/30 13:51:56  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:48  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.7  2007/10/08 12:14:56  gerrit
 * changed HashMap to Map where possible
 *
 * Revision 1.6  2007/07/24 16:14:06  gerrit
 * added configure()
 *
 * Revision 1.5  2007/07/24 07:59:15  gerrit
 * update javadoc
 *
 * Revision 1.4  2006/05/19 09:28:38  unknown3
 * Restore java files from batch package after unwanted deletion.
 *
 * Revision 1.2  2005/10/31 14:38:03  unknown2
 * Add . in javadoc
 *
 * Revision 1.1  2005/10/11 13:00:22  unknown2
 * New ibis file related elements, such as DirectoryListener, MoveFilePie and 
 * BatchFileTransformerPipe
 *
 */
package nl.nn.adapterframework.batch;

import java.util.Map;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.INamedObject;
import nl.nn.adapterframework.core.IPipeLineSession;

/**
 * Interface for handling a transformed record.
 * 
 * A RecordHandlerManager decides, based on some implementation dependent algorithm, which record handler
 * is to be used to process a record. 
 * A record manager keeps a table of flow-elements, that each define a recordhandler, resulthandler and, 
 * optionally, a next-manager.
 * 
 * @author john
 */
public interface IRecordHandlerManager extends INamedObject {

	public void configure(Map registeredManagers, Map registeredRecordHandlers, Map registeredResultHandlers, IResultHandler defaultHandler) throws ConfigurationException;

	/**
	 * @param flow New flow to be added to the managed flow elements
	 */
	void addHandler(RecordHandlingFlow flow);
	
	/**
	 * @param record 
	 * @return the RecordHandlingFlow element to be used to handle the record
	 * @throws Exception
	 */
	RecordHandlingFlow getRecordHandler(IPipeLineSession session, String record) throws Exception;
	
	/**
	 * @param filename
	 * @return the IRecordHandlingManager to be used initially based on the name of the input file 
	 */
	IRecordHandlerManager getRecordFactoryUsingFilename(IPipeLineSession session, String filename);
	
	/**
	 * @param initialFactory inidicates if this manager is the initial manager
	 */
	void setInitial(boolean initialFactory);
	boolean isInitial();
}
