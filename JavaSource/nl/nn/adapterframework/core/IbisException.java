/*
 * $Log: IbisException.java,v $
 * Revision 1.12  2004-07-20 13:56:31  L190409
 * improved message parsing
 *
 * Revision 1.11  2004/07/20 13:50:32  gerrit
 * improved message parsing
 *
 * Revision 1.10  2004/07/08 08:55:58  gerrit
 * cosmetic changes in toString()
 *
 * Revision 1.9  2004/07/07 14:30:15  gerrit
 * toString(): classes, messages and root cause
 *
 * Revision 1.8  2004/07/07 13:55:06  gerrit
 * improved toString() method, including fields of causes
 *
 * Revision 1.7  2004/07/06 06:57:57  gerrit
 * improved workings of getMessage()
 *
 * Revision 1.6  2004/03/30 07:29:59  gerrit
 * updated javadoc
 *
 * Revision 1.5  2004/03/26 10:42:50  johan
 * added @version tag in javadoc
 *
 * Revision 1.4  2004/03/23 16:48:17  gerrit
 * modified getMessage() to avoid endless-loop
 *
 */
package nl.nn.adapterframework.core;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.exception.NestableException;

/**
 * Base Exception with exhaustive toString() and compact getMessage()
 * 
 * @version Id
 * @author Gerrit van Brakel
 */
public class IbisException extends NestableException {
		public static final String version="$Id: IbisException.java,v 1.12 2004-07-20 13:56:31 L190409 Exp $";

	static {
		// add methodname to find cause of JMS-Exceptions
		ExceptionUtils.addCauseMethodName("getLinkedException");
	}
	
	public IbisException() {
		super();
	}
	public IbisException(String message) {
		super(message);
	}
	public IbisException(String message, Throwable cause) {
		super(message, cause);
	}
	public IbisException(Throwable cause) {
		super(cause);
	}
	
    public String getMessage() {
	    String messages[]=getMessages(); 
	    String last_message=null;
		String result=null;
	    
	    for(int i=0; i<messages.length; i++) {
		    if (messages[i]!=null && (last_message==null || !last_message.endsWith(messages[i]))) {
			    last_message = messages[i];
			    if (result==null) {
				    result=last_message;
			    } else {
				    result=result+": "+last_message;
			    }
		    }
	    }
		
	    if (result==null) {
	    	// do not replace the following with toString(), this causes an endless loop. GvB
		    result="no message, fields of this exception: "+ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE);
	    }
	   
	    return result;
    }

	public String toString() {
		String result="";
		Throwable t;

		result = "message: "+getMessage()+"\ncause-trace:\n"; 
		for (t=this; t!=null; t=ExceptionUtils.getCause(t)) {
			result += "\nclass: "+t.getClass().getName()+"\nmessage:"+t.getMessage()+"\n";
		}
		t=ExceptionUtils.getRootCause(this);
		if (t!=null) {
			result += "\nroot cause:\n"+ToStringBuilder.reflectionToString(t,ToStringStyle.MULTI_LINE_STYLE)+"\n";
		}
		return result;
	}
}
