/*
 * $Log: IbisException.java,v $
 * Revision 1.7  2004-07-06 06:57:57  L190409
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
		public static final String version="$Id: IbisException.java,v 1.7 2004-07-06 06:57:57 L190409 Exp $";

	static {
		// add methodname to find cause of JMS-Exceptions
		ExceptionUtils.addCauseMethodName("getLinkedException");
	}
	
public IbisException() {
	super();
}
public IbisException(String arg1) {
	super(arg1);
}
public IbisException(String arg1, Throwable arg2) {
	super(arg1, arg2);
}
public IbisException(Throwable arg1) {
	super(arg1);
}
    public String getMessage() {
	    String messages[]=getMessages(); 
	    String last_message=null;
		String result=null;
	    
	    for(int i=0; i<messages.length; i++) {
		    if (messages[i]!=null && messages[i]!=last_message) {
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
 
		for (t=this; t!=null; t=ExceptionUtils.getCause(t)) {
			result += "\nclass: "+t.getClass().getName()+"\nmessage:"+t.getMessage()+"\n";
//			result += "fields:\n"+ToStringBuilder.reflectionToString(t,ToStringStyle.MULTI_LINE_STYLE)+"\n";
		}
		return result;
	}
}
