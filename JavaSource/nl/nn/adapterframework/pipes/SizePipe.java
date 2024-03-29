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
 * $Log: SizePipe.java,v $
 * Revision 1.4  2012-06-01 10:52:50  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.3  2011/11/30 13:51:50  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:45  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2006/08/22 12:56:32  gerrit
 * first version
 *
 */
package nl.nn.adapterframework.pipes;

import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;

/**
 * Returns the number of bytes or characters in the input.
 *
 * @author Jaco de Groot (***@dynasol.nl)
 * @version $Id$
 *
 */
public class SizePipe extends FixedForwardPipe {
	public static final String version="$RCSfile: SizePipe.java,v $ $Revision: 1.4 $ $Date: 2012-06-01 10:52:50 $";

	/**
	 * @see nl.nn.adapterframework.core.IPipe#doPipe(Object, PipeLineSession)
	 */
	public PipeRunResult doPipe(Object input, IPipeLineSession session) throws PipeRunException {
		try {
			int size = -1;
			if (input instanceof String) {
				size = ((String)input).length();
			} else if (input instanceof byte[]) {
				size = ((byte[])input).length;
			}
			return new PipeRunResult(getForward(), "" + size);
		} catch(Exception e) {
			throw new PipeRunException(this, "Error while transforming input", e);
		}
	}

}
