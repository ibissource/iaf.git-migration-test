package nl.nn.adapterframework.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import nl.nn.adapterframework.util.Variant;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * $Log: ParameterValueResolver.java,v $
 * Revision 1.1  2004-05-21 07:58:47  a1909356#db2admin
 * Moved PipeParameter to core
 *
 */
 
/**
 * Determines the parameter values of the specified parameter during runtime
 * 
 * @author john
 *@version Id
 */
public class ParameterValueResolver {
	private Object input;
	private Hashtable session;
	private Document document;

	/**
	 * constructor
	 * @param input contains the input (xml formatted) message
	 * @param session 
	 */		
	public ParameterValueResolver(Object input, Hashtable session) {
		this.input = input;
		this.session = session;
	}
		
	/**
	 * determines the raw value 
	 * @param p
	 * @return the raw value as object
	 * @throws IbisException
	 */
	public Object getRawValue(Parameter p) throws IbisException {
		Object result = null;
		if (StringUtils.isNotEmpty(p.getSessionKey())) {
			result=getSession().get(p.getSessionKey());
		}
		else if (StringUtils.isNotEmpty(p.getXpathExpression())) {
			try {
				// apache specific classes for running xpath
				XPath xp = new XPath(p.getXpathExpression(), null, null, XPath.SELECT);
				result =  xp.execute(new XPathContext(), getDocument(), null).toString();
			}
			catch (Exception e) {
				throw new IbisException("Error while getting parametervalue for parameter " + p.getName(), e);
			}
		}
		// if value is null then return specified default value
		return (result == null) ? p.getDefaultValue() : result.toString(); 
	}
	
	/**
	 * @param p
	 * @return value as a <link>ParameterValue<link> object
	 * @throws IbisException
	 */
	public ParameterValue getValue(Parameter p) throws IbisException {
		return new ParameterValue(p, getRawValue(p));
	}
	
	/**
	 * @param parameters
	 * @return arraylist of <link>ParameterValue<link> objects
	 * @throws IbisException
	 */
	public ArrayList getValues(ArrayList parameters) throws IbisException {
		if (parameters == null)
			return null;
		
		ArrayList result = new ArrayList(parameters.size());
		for (Iterator it= parameters.iterator(); it.hasNext(); ) {
			result.add(getValue((Parameter)it.next()));
		}
		return result;
	}
	
	/**
	 * @author john
	 * interface to be used as callback handler in the forAllParameterValues methode
	 */
	public interface ValueHandler {
		/**
		 * @param pType the parameter type
		 * @param value the raw value 
		 * @param callbackObject  
		 */
		void handle(Parameter pType, Object value, Object callbackObject);	
	}

	/**
	 * Iterator through all parameters and call the handler for each parameter
	 * @param parameters
	 * @param handler
	 * @param callbackObject
	 * @throws IbisException
	 */		
	public void forAllParameterValues(ArrayList parameters, ValueHandler handler, Object callbackObject) throws IbisException {
		if (parameters != null) {
			for (Iterator it= parameters.iterator(); it.hasNext(); ) {
				Parameter p = (Parameter)it.next();
				Object val = getRawValue(p);
				handler.handle(p, val, callbackObject);
			}			
		}
	}
	
	/**
	 * @return the DOM document parsed from the (xml formatted) input
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document getDocument() throws ParserConfigurationException, SAXException, IOException {
		if (document == null) {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Variant inputVar = new Variant((String)input);
			InputSource in = inputVar.asXmlInputSource();
			document = db.parse(in);
		}
		return document;
	}

	/**
	 * @return the (xml formatted) input message
	 */
	public Object getInput() {
		return input;
	}

	/**
	 * @return hashtable with session variables
	 */
	public Hashtable getSession() {
		return session;
	}

	/**
	 * @param document contains the DOM document parsed from the (xml formatted) input 
	 */
	private void setDocument(Document document) {
		this.document = document;
	}

	/**
	 * @param input the (xml formatted) input message
	 */
	public void setInput(Object input) {
		this.input = input;
		this.document = null;
	}

	/**
	 * @param session
	 */
	public void setSession(PipeLineSession session) {
		this.session = session;
	}

}
