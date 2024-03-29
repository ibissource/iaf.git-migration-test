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
 * $Log: TracingEventNumbers.java,v $
 * Revision 1.3  2011-11-30 13:51:49  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:43  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2006/02/20 15:42:40  gerrit
 * moved METT-support to single entry point for tracing
 *
 * Revision 1.1  2006/02/09 07:57:22  gerrit
 * METT tracing support
 *
 */
package nl.nn.adapterframework.util;

/**
 * Piefje om METT te kunnen gebruiken in IBIS
 * 
 * @author L190409
 * @since  
 * @version $Id$
 */
public interface TracingEventNumbers {

	public int getAfterEvent();
	public int getBeforeEvent();
	public int getExceptionEvent();

}
