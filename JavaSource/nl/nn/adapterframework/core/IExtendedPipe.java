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
 * $Log: IExtendedPipe.java,v $
 * Revision 1.12  2012-10-10 10:19:37  jaco
 * Made it possible to use Locker on Pipe level too
 *
 * Revision 1.11  2011/11/30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.9  2011/08/22 14:22:32  gerrit
 * support for size statistics
 *
 * Revision 1.8  2009/11/12 12:37:15  peter
 * added 'Pipe Message Size Exceeding' event
 *
 * Revision 1.7  2008/08/12 15:32:04  gerrit
 * defined additional events
 *
 * Revision 1.6  2008/08/07 11:19:30  gerrit
 * added event supporting functions
 *
 * Revision 1.5  2007/05/02 11:30:30  gerrit
 * added attribute 'active'
 * added attribute getInputFromFixedValue
 *
 * Revision 1.4  2007/05/01 14:08:10  gerrit
 * introduction of PipeLine-exithandlers
 *
 * Revision 1.3  2006/12/28 14:21:54  gerrit
 * updated javadoc
 *
 * Revision 1.2  2006/08/22 12:51:00  gerrit
 * added preserveInput attribute
 *
 * Revision 1.1  2005/09/08 15:52:18  gerrit
 * moved extra functionality to IExtendedPipe
 *
 */
package nl.nn.adapterframework.core;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.util.Locker;

/**
 * extra attributes to do logging and use sessionvariables.
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setActive(boolean) active}</td><td>controls whether the Pipe is included in configuration. When set <code>false</code> or set to something else as "true", (even set to the empty string), the Pipe is not included in the configuration</td><td>true</td></tr>
 * <tr><td>{@link #setDurationThreshold(long) durationThreshold}</td><td>if durationThreshold >=0 and the duration (in milliseconds) of the message processing exceeded the value specified the message is logged informatory</td><td>-1</td></tr>
 * <tr><td>{@link #setGetInputFromSessionKey(String) getInputFromSessionKey}</td><td>when set, input is taken from this session key, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setGetInputFromFixedValue(String) getInputFromFixedValue}</td><td>when set, this fixed value is taken as input, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setStoreResultInSessionKey(String) storeResultInSessionKey}</td><td>when set, the result is stored under this session key</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setPreserveInput(boolean) preserveInput}</td><td>when set <code>true</code>, the input of a pipe is restored before processing the next one</td><td>false</td></tr>
 * </table>
 * </p>
 * 
 * <p>
 * <table border="1">
 * <tr><th>nested elements</th><th>description</th></tr>
 * <tr><td>{@link nl.nn.adapterframework.scheduler.Locker locker}</td><td>optional: the pipe will only be executed if a lock could be set successfully</td></tr>
 * </table>
 * </p>
 * 
 * @author  Gerrit van Brakel
 * @since   4.3
 * @version $Id$
 */
public interface IExtendedPipe extends IPipe {

	public static final String LONG_DURATION_MONITORING_EVENT="Pipe Long Processing Duration";
	public static final String PIPE_EXCEPTION_MONITORING_EVENT="Pipe Exception";
	public static final String MESSAGE_SIZE_MONITORING_EVENT="Pipe Message Size Exceeding";

	/**
	 * Extension, allowing Pipes to register things with the PipeLine at Configuration time.
	 * For IExtendedPipes, PileLine will call this method rather then the no-args configure().
	 */
	void configure(PipeLine pipeline) throws ConfigurationException;

	/**
	 * controls whether pipe is used in configuration. Can be used for debug-only pipes.
	 */
	public boolean isActive();

	/**
	 * Sets a threshold for the duration of message execution; 
	 * If the threshold is exceeded, the message is logged to be analyzed.
	 */
	public void setDurationThreshold(long maxDuration) ;
	public long getDurationThreshold();



	public void setGetInputFromSessionKey(String string);
	public String getGetInputFromSessionKey();

	public void setStoreResultInSessionKey(String string);
	public String getStoreResultInSessionKey();

	public void setGetInputFromFixedValue(String string);
	public String getGetInputFromFixedValue();

	public void setPreserveInput(boolean preserveInput);
	public boolean isPreserveInput();

	public void setLocker(Locker locker);
	public Locker getLocker();

	/**
	 * Register an event for flexible monitoring.
	 * @param description
	 */
	public void registerEvent(String description);
	/**
	 * Throw an event for flexible monitoring.
	 * @param description
	 */
	public void throwEvent(String event);

	public boolean hasSizeStatistics();

}
