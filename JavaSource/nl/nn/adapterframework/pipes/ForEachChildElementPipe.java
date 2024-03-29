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
 * $Log: ForEachChildElementPipe.java,v $
 * Revision 1.28  2012-06-01 10:52:49  jaco
 * Created IPipeLineSession (making it easier to write a debugger around it)
 *
 * Revision 1.27  2011/12/08 13:01:59  peter
 * fixed javadoc
 *
 * Revision 1.26  2011/11/30 13:51:50  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:45  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.24  2010/04/08 09:59:13  peter
 * added attribute xslt2
 *
 * Revision 1.23  2010/03/10 10:16:03  peter
 * added TimeOutException to iterateInput
 *
 * Revision 1.22  2010/02/25 13:41:54  peter
 * adjusted javadoc for resultOnTimeOut attribute
 *
 * Revision 1.21  2010/02/03 14:29:32  gerrit
 * check for interrupt
 *
 * Revision 1.20  2008/09/08 14:58:51  gerrit
 * corrected in error logging
 *
 * Revision 1.19  2008/05/27 16:56:34  gerrit
 * handle blocks correctly
 *
 * Revision 1.18  2008/05/21 09:40:34  gerrit
 * added block info to javadoc
 *
 * Revision 1.17  2008/05/15 15:32:31  gerrit
 * set root cause of SAX exception
 *
 * Revision 1.16  2008/02/22 14:32:39  gerrit
 * fix bug for nested elements
 *
 * Revision 1.15  2008/02/21 12:48:28  gerrit
 * added option for pushing iteration
 *
 * Revision 1.14  2007/10/08 12:23:51  gerrit
 * changed HashMap to Map where possible
 *
 * Revision 1.13  2007/09/10 11:19:19  gerrit
 * remove unused imports
 *
 * Revision 1.12  2007/07/17 11:06:30  gerrit
 * switch to new version
 *
 * Revision 1.1  2007/07/10 10:06:29  gerrit
 * switch back to original ForEachChildElementPipe
 *
 * Revision 1.10  2007/07/10 07:53:06  gerrit
 * new implementation based on IteratingPipe
 *
 *
 */
package nl.nn.adapterframework.pipes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeStartException;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.core.TimeOutException;
import nl.nn.adapterframework.util.StreamUtil;
import nl.nn.adapterframework.util.TransformerPool;
import nl.nn.adapterframework.util.XmlUtils;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Sends a message to a Sender for each child element of the input XML.
 * Input can be a String containing XML, a filename (set processFile true), an InputStream or a Reader.
 * 
 * <br>
 * The output of each of the processing of each of the elements is returned in XML as follows:
 * <pre>
 *  &lt;results count="num_of_elements"&gt;
 *    &lt;result&gt;result of processing of first item&lt;/result&gt;
 *    &lt;result&gt;result of processing of second item&lt;/result&gt;
 *       ...
 *  &lt;/results&gt;
 * </pre>
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>className</td><td>nl.nn.adapterframework.pipes.ForEachChildElementPipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMaxThreads(int) maxThreads}</td><td>maximum number of threads that may call {@link #doPipe(Object, nl.nn.adapterframework.core.PipeLineSession)} simultaneously</td><td>0 (unlimited)</td></tr>
 * <tr><td>{@link #setDurationThreshold(long) durationThreshold}</td><td>if durationThreshold >=0 and the duration (in milliseconds) of the message processing exceeded the value specified the message is logged informatory</td><td>-1</td></tr>
 * <tr><td>{@link #setGetInputFromSessionKey(String) getInputFromSessionKey}</td><td>when set, input is taken from this session key, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setStoreResultInSessionKey(String) storeResultInSessionKey}</td><td>when set, the result is stored under this session key</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setNamespaceAware(boolean) namespaceAware}</td><td>controls namespace-awareness of possible XML parsing in descender-classes</td><td>application default</td></tr>
 * <tr><td>{@link #setForwardName(String) forwardName}</td>  <td>name of forward returned upon completion</td><td>"success"</td></tr>
 * <tr><td>{@link #setResultOnTimeOut(String) resultOnTimeOut}</td><td>result returned when no return-message was received within the timeout limit (e.g. "receiver timed out").</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setLinkMethod(String) linkMethod}</td><td>Indicates wether the server uses the correlationID or the messageID in the correlationID field of the reply</td><td>CORRELATIONID</td></tr>
 * <tr><td>{@link #setStopConditionXPathExpression(String) stopConditionXPathExpression}</td><td>expression evaluated on each result if set. 
 * 		Iteration stops if condition returns anything other than <code>false</code> or an empty result.
 * For example, to stop after the second child element has been processed, one of the following expressions could be used:
 * <table> 
 * <tr><td><li><code>result[position()='2']</code></td><td>returns result element after second child element has been processed</td></tr>
 * <tr><td><li><code>position()='2'</code></td><td>returns <code>false</code> after second child element has been processed, <code>true</code> for others</td></tr>
 * </table> 
 * </td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setElementXPathExpression(String) elementXPathExpression}</td><td>expression used to determine the set of elements iterated over, i.e. the set of child elements</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setRemoveXmlDeclarationInResults(boolean) removeXmlDeclarationInResults}</td><td>postprocess each partial result, to remove the xml-declaration, as this is not allowed inside an xml-document</td><td>false</td></tr>
 * <tr><td>{@link #setProcessFile(boolean) processFile}</td><td>when set <code>true</code>, the input is assumed to be the name of a file to be processed. Otherwise, the input itself is transformed</td><td>application default</td></tr>
 * <tr><td>{@link #setCharset(String) charset}</td><td>characterset used for reading file or inputstream, only used when {@link #setProcessFile(boolean) processFile} is <code>true</code>, or the input is of type InputStream</td><td>UTF-8</td></tr>
 * <tr><td>{@link #setBlockSize(int) blockSize}</td><td>controls multiline behaviour. when set to a value greater than 0, it specifies the number of rows send in a block to the sender.</td><td>0 (one line at a time, no prefix of suffix)</td></tr>
 * <tr><td>{@link #setBlockPrefix(String) blockPrefix}</td><td>When <code>blockSize &gt; 0</code>, this string is inserted at the start of the set of lines.</td><td>&lt;block&gt;</td></tr>
 * <tr><td>{@link #setBlockSuffix(String) blockSuffix}</td><td>When <code>blockSize &gt; 0</code>, this string is inserted at the end of the set of lines.</td><td>&lt;/block&gt;</td></tr>
 * <tr><td>{@link #setXslt2(boolean) xslt2}</td><td>when set <code>true</code> XSLT processor 2.0 (net.sf.saxon) will be used for <code>elementXPathExpression</code>, otherwise XSLT processor 1.0 (org.apache.xalan)</td><td>false</td></tr>
 * </table>
 * <table border="1">
 * <tr><th>nested elements</th><th>description</th></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ISender sender}</td><td>specification of sender to send messages with</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.core.ICorrelatedPullingListener listener}</td><td>specification of listener to listen to for replies</td></tr>
 * <tr><td>{@link nl.nn.adapterframework.parameters.Parameter param}</td><td>any parameters defined on the pipe will be handed to the sender, if this is a {@link nl.nn.adapterframework.core.ISenderWithParameters ISenderWithParameters}</td></tr>
 * </table>
 * </p>
 * 
 * For more configuration options, see {@link MessageSendingPipe}.
 * <br>
 * use parameters like:
 * <pre>
 *	&lt;param name="element-name-of-current-item"  xpathExpression="name(/*)" /&gt;
 *	&lt;param name="value-of-current-item"         xpathExpression="/*" /&gt;
 * </pre>
 * 
 * @author Gerrit van Brakel
 * @since 4.6.1
 * 
 * $Id$
 */
public class ForEachChildElementPipe extends IteratingPipe {
	public static final String version="$RCSfile: ForEachChildElementPipe.java,v $ $Revision: 1.28 $ $Date: 2012-06-01 10:52:49 $";

	private String elementXPathExpression=null;
	private boolean processFile=false;
	private String charset=StreamUtil.DEFAULT_INPUT_STREAM_ENCODING;

	private TransformerPool identityTp;
	private TransformerPool extractElementsTp=null;
	private boolean xslt2=false;


	public void configure() throws ConfigurationException {
		super.configure();
		try {
			identityTp=new TransformerPool(XmlUtils.IDENTITY_TRANSFORM);
			if (StringUtils.isNotEmpty(getElementXPathExpression())) {
				extractElementsTp=new TransformerPool(makeEncapsulatingXslt("root",getElementXPathExpression()), isXslt2());
			}
		} catch (TransformerConfigurationException e) {
			throw new ConfigurationException(getLogPrefix(null)+"elementXPathExpression ["+getElementXPathExpression()+"]",e);
		}
	}

	public void start() throws PipeStartException  {
		try {
			identityTp.open();
		} catch (Exception e) {
			throw new PipeStartException(e);
		}
		super.start();
	}

	public void stop()   {
		if (identityTp!=null) {
			identityTp.close();
		}
		super.stop();
	}

	private class ItemCallbackCallingHandler extends DefaultHandler {
		
		ItemCallback callback;
		
		StringBuffer elementbuffer=new StringBuffer();
		int elementLevel=0;
		int itemCounter=0;
		Exception rootException=null;
		int startLength;		
		boolean contentSeen;
		boolean stopRequested;
		TimeOutException timeOutException;
		
		public ItemCallbackCallingHandler(ItemCallback callback) {
			this.callback=callback;
			elementbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			if (getBlockSize()>0) {
				elementbuffer.append(getBlockPrefix());
			}
			startLength=elementbuffer.length();
		}

		private void checkInterrupt() throws SAXException {
			if (Thread.currentThread().isInterrupted()) {
				rootException = new InterruptedException("Thread has been interrupted");
				rootException.fillInStackTrace();
				throw new SAXException("Thread has been interrupted");
			}
		}
		
		public void characters(char[] ch, int start, int length) throws SAXException {
			checkInterrupt();
			if (elementLevel>1) {
				if (!contentSeen) {
					contentSeen=true;
					elementbuffer.append(">");
				}
				elementbuffer.append(XmlUtils.encodeChars(ch, start, length));
			}
		}

		public void endElement(String uri, String localName, String qname) throws SAXException {
			checkInterrupt();
			if (elementLevel>1) {
				if (!contentSeen) {
					contentSeen=true;
					elementbuffer.append("/>");
				} else {
					elementbuffer.append("</"+localName+">");
				}
			}
			if (--elementLevel<=1 && ++itemCounter>=getBlockSize()) {
				try {
					if (elementLevel==1 || itemCounter>1) {
						if (getBlockSize()>0) {
							elementbuffer.append(getBlockSuffix());
						}
						stopRequested = !callback.handleItem(elementbuffer.toString());
						elementbuffer.setLength(startLength);
					}
					itemCounter=0;
				} catch (Exception e) {
					if (e instanceof TimeOutException) {
						timeOutException = (TimeOutException)e;
					}
					rootException =e;
					Throwable rootCause = e;
					while (rootCause.getCause()!=null) {
						rootCause=rootCause.getCause();
					}
					SAXException se = new SAXException(e);
					se.setStackTrace(rootCause.getStackTrace());
					throw se;
					
				}
				if (stopRequested) {
					throw new SAXException("stop maar");
				}
			}
		}


		public void startElement(String uri, String localName, String qName, Attributes attributes)	throws SAXException {
			checkInterrupt();
			if (elementLevel>1 && !contentSeen) {
				elementbuffer.append(">");
			}
			if (++elementLevel>1) {
				elementbuffer.append("<"+localName);
				for (int i=0; i<attributes.getLength(); i++) {
					elementbuffer.append(" "+attributes.getLocalName(i)+"=\""+attributes.getValue(i)+"\"");
				}
				contentSeen=false;
			}
		}

		public Exception getRootException() {
			return rootException;	
		}
		public boolean isStopRequested() {
			return stopRequested;
		}

		public TimeOutException getTimeOutException() {
			return timeOutException;
		}
	}


	protected void iterateInput(Object input, IPipeLineSession session, String correlationID, Map threadContext, ItemCallback callback) throws SenderException, TimeOutException {
		Reader reader=null;
		try {
			if (input instanceof Reader) {
				reader = (Reader)input;
			} else 	if (input instanceof InputStream) {
				reader=new InputStreamReader((InputStream)input,getCharset());
			} else 	if (isProcessFile()) {
				// TODO: arrange for non-namespace aware processing of files
				reader=new InputStreamReader(new FileInputStream((String)input),getCharset());
			}
		} catch (FileNotFoundException e) {
			throw new SenderException("could not find file ["+input+"]",e);
		} catch (UnsupportedEncodingException e) {
			throw new SenderException("could not use charset ["+getCharset()+"]",e);
		}
		ItemCallbackCallingHandler handler = new ItemCallbackCallingHandler(callback);
		
		if (getExtractElementsTp()!=null) {
			log.debug("transforming input to obtain list of elements using xpath ["+getElementXPathExpression()+"]");
			try {
				SAXResult transformedStream = new SAXResult();
				Source src;
				if (reader!=null) {
					src=new StreamSource(reader);
				} else {
					src = XmlUtils.stringToSourceForSingleUse((String)input, isNamespaceAware());
				}
				transformedStream.setHandler(handler);
				getExtractElementsTp().transform(src, transformedStream, null);
			} catch (Exception e) {
				if (handler.getTimeOutException()!=null) {
					throw handler.getTimeOutException();
				}
				if (!handler.isStopRequested()) {
					throw new SenderException("Could not extract list of elements using xpath ["+getElementXPathExpression()+"]",e);
				}
			}
		} else {
			
			try {
				if (reader!=null) {
					XmlUtils.parseXml(handler,new InputSource(reader));
				} else {
					XmlUtils.parseXml(handler,(String)input);
				}
			} catch (Exception e) {
				if (handler.getTimeOutException()!=null) {
					throw handler.getTimeOutException();
				}
				if (!handler.isStopRequested()) {
					throw new SenderException("Could not parse input",e);
				}
			}
		}
		
	}

	
//	public class ElementIterator implements IDataIterator {
//		private static final boolean elementsOnly=true;
//
//		Node node;
//		boolean nextElementReady;
//
//		public ElementIterator(String inputString) throws SenderException {
//			super();
//
//			Reader reader=null;
//			if (isProcessFile()) {
//				try {
//					// TODO: arrange for non-namespace aware processing of files
//					reader=new InputStreamReader(new FileInputStream(inputString));
//				} catch (FileNotFoundException e) {
//					throw new SenderException("could not find file ["+inputString+"]",e);
//				}
//			}
//
//			if (getExtractElementsTp()!=null) {
//				log.debug("transforming input to obtain list of elements using xpath ["+getElementXPathExpression()+"]");
//				try {
//					DOMResult fullMessage = new DOMResult();
//					Source src;
//					if (reader!=null) {
//						src=new StreamSource(reader);
//					} else {
//						src = XmlUtils.stringToSourceForSingleUse(inputString, isNamespaceAware());
//					}
//					getExtractElementsTp().transform(src, fullMessage, null);
//					node=fullMessage.getNode().getFirstChild();
//				} catch (Exception e) {
//					throw new SenderException("Could not extract list of elements using xpath ["+getElementXPathExpression()+"]");
//				}
//			} else {
//				Document fullMessage;
//				try {
//					if (reader!=null) {
//						fullMessage=XmlUtils.buildDomDocument(reader, isNamespaceAware());
//					} else {
//						fullMessage=XmlUtils.buildDomDocument(inputString, isNamespaceAware());
//					}
//					node=fullMessage.getDocumentElement().getFirstChild();
//				} catch (DomBuilderException e) {
//					throw new SenderException("Could not build elements",e);
//				}
//			}
//			nextElementReady=false;
//		}
//
//		private void findNextElement() {
//			if (elementsOnly) {
//				while (node!=null && !(node instanceof Element)) { 
//					node=node.getNextSibling();
//				}
//			}
//		}
//
//		public boolean hasNext() {
//			findNextElement();
//			return node!=null;
//		}
//
//		public Object next() throws SenderException {
//			findNextElement();
//			if (node==null) {
//				return null;
//			}
//			DOMSource src = new DOMSource(node);
//			String result;
//			try {
//				result = getIdentityTp().transform(src, null);
//			} catch (Exception e) {
//				throw new SenderException("could not extract element",e);
//			}
//			if (node!=null) {
//				node=node.getNextSibling();
//			} 
//			return result; 
//		}
//
//		public void close() {
//		}
//	}

	
//	protected IDataIterator getIterator(Object input, PipeLineSession session, String correlationID, Map threadContext) throws SenderException {
//		return new ElementIterator((String)input);
//	}

	protected TransformerPool getExtractElementsTp() {
		return extractElementsTp;
	}
	protected TransformerPool getIdentityTp() {
		return identityTp;
	}



	public void setElementXPathExpression(String string) {
		elementXPathExpression = string;
	}
	public String getElementXPathExpression() {
		return elementXPathExpression;
	}

	public void setProcessFile(boolean b) {
		processFile = b;
	}
	public boolean isProcessFile() {
		return processFile;
	}

	public void setCharset(String string) {
		charset = string;
	}
	public String getCharset() {
		return charset;
	}

	public boolean isXslt2() {
		return xslt2;
	}

	public void setXslt2(boolean b) {
		xslt2 = b;
	}
}
