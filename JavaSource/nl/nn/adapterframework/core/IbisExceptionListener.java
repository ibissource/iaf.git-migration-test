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
 * $Log: IbisExceptionListener.java,v $
 * Revision 1.3  2011-11-30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2004/06/30 10:05:01  gerrit
 * first version
 *
 */
package nl.nn.adapterframework.core;

/**
 * ExeceptionListener-class to signal exceptions to other objects, for instance 
 * MessagePushers to PushingReceivers.
 * 
 * @author Gerrit van Brakel
 * @since 4.2
 */
public interface IbisExceptionListener {

	/**
	 * Inform the implementing class that the exception <code>t</code> occured in <code>object</code>.
	 */
	void exceptionThrown(INamedObject object, Throwable t);
}
