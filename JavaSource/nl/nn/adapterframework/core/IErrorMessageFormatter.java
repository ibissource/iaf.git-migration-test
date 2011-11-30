/*
 * $Log: IErrorMessageFormatter.java,v $
 * Revision 1.6  2011-11-30 13:51:55  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.2  2011/10/19 14:55:45  peter
 * do not print versions anymore
 *
 * Revision 1.1  2011/10/19 14:49:46  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2004/03/30 07:29:54  gerrit
 * updated javadoc
 *
 * Revision 1.3  2004/03/26 10:42:45  johan
 * added @version tag in javadoc
 *
 */
package nl.nn.adapterframework.core;

/**
 * An <code>errorMessageFormatter</code> is responsible for returning a string
 * describing the error at hand in a format that the receiver expects. 
 * By implementing this interface, it is possible to customize messages.
 * 
 * @version Id
 * @author Johan Verrips
 */
public interface IErrorMessageFormatter {

	public String format(
	String errorMessage,
    Throwable t,
    INamedObject location,
    String originalMessage,
    String messageId,
    long receivedTime);
}
