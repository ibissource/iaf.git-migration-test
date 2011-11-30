/*
 * $Log: INamedObject.java,v $
 * Revision 1.6  2011-11-30 13:51:55  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.2  2011/10/19 14:56:13  peter
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
 * The <code>INamedObject</code> is implemented by all objects that have a name
 * 
 * @version Id
 * @author  Gerrit van Brakel
 */
public interface INamedObject {
	/**
	 * The functional name of the object implementing this interface
	 */
	public String getName();
	public void setName(String name);
}
