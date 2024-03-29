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
 * $Log: RequestReplyExecutor.java,v $
 * Revision 1.4  2011-11-30 13:51:55  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2010/09/07 15:55:13  jaco
 * Removed IbisDebugger, made it possible to use AOP to implement IbisDebugger functionality.
 *
 *
 */
package nl.nn.adapterframework.core;

/**
 * Runnable object for calling a request reply service. When a
 * <code>Throwable</code> has been thrown during execution is should be returned
 * by getThrowable() otherwise the reply should be returned by getReply().
 *    
 * @author Jaco de Groot
 * @version $Id$
 */
public abstract class RequestReplyExecutor implements Runnable {
	protected String correlationID;
	protected String request;
	protected Object reply;
	protected Throwable throwable;

	public void setCorrelationID(String correlationID) {
		this.correlationID = correlationID;
	}
	
	public String getCorrelationID() {
		return correlationID;
	}

	public void setRequest(String request)  {
		this.request = request;
	}
		
	public Object getRequest() {
		return request;
	}

	public void setReply(Object reply)  {
		this.reply = reply;
	}
		
	public Object getReply() {
		return reply;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}

}
