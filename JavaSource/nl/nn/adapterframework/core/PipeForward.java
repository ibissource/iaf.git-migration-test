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
 * $Log: PipeForward.java,v $
 * Revision 1.6  2011-11-30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.2  2011/10/19 14:59:25  peter
 * do not print versions anymore
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2004/03/30 07:29:54  gerrit
 * updated javadoc
 *
 * Revision 1.3  2004/03/26 10:42:44  johan
 * added @version tag in javadoc
 *
 */
package nl.nn.adapterframework.core;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Bean that knows a functional name of a Forward, to be referred by
 * other Pipes and the PipeLine.
 * <br/>
 * The <code>name</code> is the name which a Pipe may lookup to return
 * to the PipeLine, indicating the next pipe to be executed. (this is done
 * in the {@link nl.nn.adapterframework.pipes.AbstractPipe#findForward(String) findForward()}-method. The actual
 * pipeName is defined in the <code>path</code> property.<br/><br/>
 * In this manner it is possible to influence the flow through the pipeline
 * without affecting the Java-code. Simply change the forwarding-XML.<br/>
 * 
 * @version $Id$
 * @author Johan Verrips
 * @see PipeLine
 * @see nl.nn.adapterframework.pipes.AbstractPipe#findForward
 */
public class PipeForward {

    private String name;
    private String path;

	/**
	 * the <code>name</code> is a symbolic reference to a <code>path</code>.<br/>
	 */
    public String getName() {
        return name;
    }
	/**
	 * The path is the name of the Pipe to execute/store
	 */
	public String getPath() {
        return path;
    }
  	/**
	 * the <code>name</code> is a symbolic reference to a <code>path</code>.<br/>
	 */
    public void setName(String name) {
        this.name = name;
    }
	/**
	 * The path is the name of the Pipe to execute/store
	 */
    public void setPath(String path) {
        this.path = path;
    }
 	/**
 	 * uses reflection to return the value
 	 */ 
	public String toString(){
      return ToStringBuilder.reflectionToString(this);
    }
}
