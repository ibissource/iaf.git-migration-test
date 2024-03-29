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
 * $Log: XmlValidatorErrorHandler.java,v $
 * Revision 1.1  2012-10-26 16:13:38  jaco
 * Moved *Xmlvalidator*, Schema and SchemasProvider to new validation package
 *
 * Revision 1.3  2012/10/16 15:50:57  jaco
 * Prevent NullPointerException when no root validations need to be done
 *
 * Revision 1.2  2012/10/01 07:59:29  jaco
 * Improved messages stored in reasonSessionKey and xmlReasonSessionKey
 * Cleaned XML validation code and documentation a bit.
 *
 * Revision 1.1  2012/09/19 09:49:58  jaco
 * - Set reasonSessionKey to "failureReason" and xmlReasonSessionKey to "xmlFailureReason" by default
 * - Fixed check on unknown namspace in case root attribute or xmlReasonSessionKey is set
 * - Fill reasonSessionKey with a message when an exception is thrown by parser instead of the ErrorHandler being called
 * - Added/fixed check on element of soapBody and soapHeader
 * - Cleaned XML validation code a little (e.g. moved internal XmlErrorHandler class (double code in two classes) to an external class, removed MODE variable and related code)
 *
 */
package nl.nn.adapterframework.validation;

import java.io.PrintWriter;
import java.io.StringWriter;

import nl.nn.adapterframework.util.LogUtil;
import nl.nn.adapterframework.util.XmlBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlValidatorErrorHandler implements ErrorHandler {
	private Logger log = LogUtil.getLogger(this);
	private boolean errorOccured = false;
	private String reasons;
	private final XmlValidatorContentHandler xmlValidatorContentHandler;
	private XmlBuilder xmlReasons = new XmlBuilder("reasons");


	public XmlValidatorErrorHandler(
			XmlValidatorContentHandler xmlValidatorContentHandler,
			String mainMessage) {
		this.xmlValidatorContentHandler = xmlValidatorContentHandler;
		XmlBuilder message = new XmlBuilder("message");;
		message.setValue(mainMessage);
		xmlReasons.addSubElement(message);
		reasons = mainMessage + ":";
	}

	public void addReason(String message, String location) {
		String xpath = xmlValidatorContentHandler.getXpath();

		XmlBuilder reason = new XmlBuilder("reason");
		XmlBuilder detail;

		detail = new XmlBuilder("xpath");;
		detail.setValue(xpath);
		reason.addSubElement(detail);

		detail = new XmlBuilder("location");;
		detail.setValue(location);
		reason.addSubElement(detail);

		detail = new XmlBuilder("message");;
		detail.setValue(message);
		reason.addSubElement(detail);

		xmlReasons.addSubElement(reason);

		if (StringUtils.isNotEmpty(location)) {
			message = location + ": " + message;
		}
		message = xpath + ": " + message;

		errorOccured = true;

		if (reasons == null) {
			reasons = message;
		} else {
			reasons = reasons + "\n" + message;
		}
	}

	public void addReason(Throwable t) {
		String message = null;
		String location = null;
		if (t instanceof SAXParseException) {
			SAXParseException spe = (SAXParseException)t;
			location = "at ("+spe.getLineNumber()+ ","+spe.getColumnNumber()+")";
		}
		if (t instanceof SAXException) {
			message = t.getMessage();
		} else {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			t.printStackTrace(printWriter);
			printWriter.close();
			message = stringWriter.toString();
		}
		addReason(message,location);
	}

	public void warning(SAXParseException exception) {
		addReason(exception);
	}
    public void error(SAXParseException exception) {
    	addReason(exception);
    }
    public void fatalError(SAXParseException exception) {
		addReason(exception);
    }

    public boolean hasErrorOccured() {
        return errorOccured;
    }

     public String getReasons() {
        return reasons;
    }

	public String getXmlReasons() {
	   return xmlReasons.toXML();
   }
}

