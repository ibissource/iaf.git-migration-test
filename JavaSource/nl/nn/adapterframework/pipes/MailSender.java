/*
 * $Log: MailSender.java,v $
 * Revision 1.5  2004-10-14 16:13:28  L190409
 * parametrization and adding of attachments
 *
 * Revision 1.4  2004/03/26 10:42:34  johan
 * added @version tag in javadoc
 *
 * Revision 1.3  2004/03/24 13:58:36  gerrit
 * removed TimeOutException
 *
 */
package nl.nn.adapterframework.pipes;

import nl.nn.adapterframework.core.IParameterizedSender;
import nl.nn.adapterframework.core.ParameterException;
import nl.nn.adapterframework.core.TimeOutException;
import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.parameters.ParameterList;
import nl.nn.adapterframework.parameters.ParameterValue;
import nl.nn.adapterframework.parameters.ParameterValueList;
import nl.nn.adapterframework.util.DomBuilderException;
import nl.nn.adapterframework.util.XmlUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

/**
 * {@link ISender} that sends a mail specified by an XML message. <br/>
 *
 * Sample email.xml:<br/><code><pre>
 *	&lt;email&gt;
 *	    &lt;recipients&gt;
 *	        &lt;recipient&gt;***@natned&lt;/recipient&gt;
 *	        &lt;recipient&gt;***@nn.nl&lt;/recipient&gt;
 *	    &lt;/recipients&gt;
 *	    &lt;from&gt;***@nn.nl&lt;/from&gt;
 *	    &lt;subject&gt;this is the subject&lt;/subject&gt;
 *	    &lt;message&gt;dit is de message&lt;/message&gt;
 *	    &lt;attachments&gt;
 *	        &lt;attachment name="filename1.txt" type="text" &gt;<i>contents of first attachment</i>&lt;/attachment&gt;
 *	        &lt;attachment name="filename2.txt" type="text" &gt;<i>this is an attachment with a resource</i>&lt;/attachment&gt;
 *	    &lt;/attachments&gt;
 *	&lt;/email&gt;
 * </pre></code> <br/>
 * Notice: it must be valid XML. Therefore, especially the message element
 * must be plain text or be wrapped as CDATA.<br/><br/>
 * example:<br/><code><pre>
 * &lt;message&gt;&lt;![CDATA[&lt;h1&gt;This is a HtmlMessage&lt;/h1&gt;]]&gt;&lt;/message&gt;
 * </pre></code><br/>
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setSmtpHost(String) smtpHost}</td><td>name of the host by which the messages are to be send</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSmtpUserid(String) smtpUserid}</td><td>userid on the smtpHost</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSmtpPassword(String) smtpPassword}</td><td>password of userid on the smtpHost</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setDefaultFrom(String) defaultFrom}</td><td>value of the From: header if not specified in message itself</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setDefaultSubject(String) defaultSubject}</td><td>value of the Subject: header if not specified in message itself</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setDefaultAttachmentType(String) defaultAttachmentType}</td><td>&nbsp;</td><td>text</td></tr>
 * <tr><td>{@link #setDefaultAttachmentName(String) defaultAttachmentName}</td><td>&nbsp;</td><td>attachment</td></tr>
 * </table>
 * <table border="1">
 * <p><b>Parameters:</b>
 * <tr><th>name</th><th>type</th><th>remarks</th></tr>
 * <tr><td>from</td><td>string</td><td>email address of the sender</td></tr>
 * <tr><td>subject</td><td>string</td><td>subject field of the message</td></tr>
 * <tr><td>message</td><td>string</td><td>message itself. If absent, the complete input message is assumed to be the message</td></tr>
 * <tr><td>recipients</td><td>xml</td><td>recipients of the message. must result in a structure like: <code><pre>
 *	        &lt;recipient&gt;***@natned&lt;/recipient&gt;
 *	        &lt;recipient&gt;***@nn.nl&lt;/recipient&gt;
 * </pre></code></td></tr>
 * <tr><td>attachments</td><td>xml</td><td>attachments to the message. must result in a structure like: <code><pre>
 *	        &lt;attachment name="filename1.txt" type="text" &gt;<i>contents of first attachment</i>&lt;/attachment&gt;
 *	        &lt;attachment name="filename2.txt" type="text" url="url-to-resource" &gt;<i>this is an attachment with a resource</i>&lt;/attachment&gt;
 * </pre></code></td></tr>
 * </table>
 * </p>
 * NB Compilation and Deployment Note: mail.jar (v1.2) and activation.jar must appear BEFORE j2ee.jar
 * Otherwise errors like the following might occur:
 *   NoClassDefFoundException: com/sun/mail/util/MailDateFormat 
 * 
 * @version Id
 * @author Johan Verrips/Gerrit van Brakel
 */

public class MailSender implements IParameterizedSender {
	public static final String version = "$Id: MailSender.java,v 1.5 2004-10-14 16:13:28 L190409 Exp $";

	protected Logger log = Logger.getLogger(this.getClass());
	private String name;
	private String smtpHost;
	private String smtpUserid;
	private String smtpPassword;
	private String defaultAttachmentType = "text";
	private String defaultAttachmentName = "attachment";

	// defaults
	private String defaultSubject;
	private String defaultFrom;

	private Session session;
	private Properties properties;
	private Transport transport;

	public void configure(ParameterList parameterList) throws ConfigurationException {
		parameterList.configure();
		configure();
	}

	public void configure() throws ConfigurationException {
		if (StringUtils.isEmpty(getSmtpHost())) {
			throw new ConfigurationException("MailSender ["+getName()+"] has no smtpHost configured");
		}
 		properties = System.getProperties();
		try { 		
			properties.put("mail.smtp.host", getSmtpHost());
		} catch (Throwable t) {
			throw new ConfigurationException("MailSender ["+getName()+"] cannot set smtpHost ["+getSmtpHost()+"] in properties");
		}
	}

	/**
	 * Create a <code>Session</code> and <code>Transport</code> to the
	 * smtp host.
	  * @throws SenderException
	 */
	public void open() throws SenderException {
		try {
			session = Session.getInstance(properties, null);
			session.setDebug(log.isDebugEnabled());
//			log.debug("MailSender [" + getName() + "] got session to [" + properties + "]");

			// connect to the transport 
			transport = session.getTransport("smtp");

			transport.connect(smtpHost, smtpUserid, smtpPassword);
			if (log.isDebugEnabled()) {
				log.debug("MailSender [" + getName() + "] got transport to" 
						+ " [smtpHost=" + smtpHost+ "]"
						+ " [smtpUserid="+ smtpUserid
						+ " [smtpPassword="+ smtpPassword + "]");
			}

		} catch (Exception e) {
			throw new SenderException("Error opening MailSender", e);
		}
	}

	/**
	 * Close the <code>transport</code> layer.
	 */
	public void close() throws SenderException {
		try {
			if (transport!=null) {
				transport.close();
			}
		} catch (Exception e) {
			throw new SenderException("error closing transport", e);
		}
	}

	public boolean isSynchronous() {
		return false;
	}



	public String sendMessage(String correlationID,	String message,	ParameterValueList parameters) throws SenderException, TimeOutException {
		String from=null;;
		String subject=null;
		Collection recipients=null;
		Collection attachments=null;
		ParameterValue pv;
		
		try {
			pv = parameters.getParameterValue("from");
			if (pv != null) {
				from = pv.asStringValue(null);  
				log.debug("MailSender ["+getName()+"] retrieved from-parameter ["+from+"]");
			}
			pv = parameters.getParameterValue("subject");
			if (pv != null) {
				subject = pv.asStringValue(null);  
				log.debug("MailSender ["+getName()+"] retrieved subject-parameter ["+subject+"]");
			}
			pv = parameters.getParameterValue("message");
			if (pv != null) {
				message = pv.asStringValue(message);  
				log.debug("MailSender ["+getName()+"] retrieved message-parameter ["+message+"]");
			}
			pv = parameters.getParameterValue("recipients");
			if (pv != null) {
				recipients = pv.asCollection();  
			}
			pv = parameters.getParameterValue("attachments");
			if (pv != null) {
				attachments = pv.asCollection();  
			}
		} catch (ParameterException e) {
			throw new SenderException("MailSender ["+getName()+"] got exception determining parametervalues",e);
		}
		
		sendEmail(from, subject, message, recipients, attachments);
		return correlationID;
	}
	
	/**
	 * Send a mail conforming to the XML input
	 */
	public String sendMessage(String correlationID, String input) throws SenderException {
		// initialize this request
		String from;
		String subject;
		String message;
		Collection recipients;
		Collection attachments;
		
		Element emailElement;
		try {
			emailElement = XmlUtils.buildElement(input);

			from = XmlUtils.getChildTagAsString(emailElement, "from");
			subject = XmlUtils.getChildTagAsString(emailElement, "subject");
			message = XmlUtils.getChildTagAsString(emailElement, "message");

			Element recipientsElement = XmlUtils.getFirstChildTag(emailElement, "recipients");
			recipients = XmlUtils.getChildTags(recipientsElement, "recipient");

			Element attachmentsElement = XmlUtils.getFirstChildTag(emailElement, "attachments");
			attachments = attachmentsElement==null ? null :XmlUtils.getChildTags(attachmentsElement, "attachment");

		} catch (DomBuilderException e) {
			throw new SenderException("exception parsing [" + input + "]", e);
		}

		sendEmail(from, subject, message, recipients, attachments);
		return correlationID;
	}
	


	protected void sendEmail(String from, String subject, String message, Collection recipients, Collection attachments) throws SenderException {

		StringBuffer sb = new StringBuffer();

		if (recipients==null || recipients.size()==0) {
			throw new SenderException("MailSender ["+getName()+"] has no recipients for message");
		}
		if (StringUtils.isEmpty(from)) {
			from = defaultFrom;
		}
		if (StringUtils.isEmpty(subject)) {
			subject = defaultSubject;
		}
		log.debug("MailSender ["+getName()+"] requested to send message from ["+from+"] subject ["+subject+"] to #recipients ["+recipients.size()+"]");
		
		try {
			if (log.isDebugEnabled()) {
				sb.append("MailSender [" + getName() + "] sending message ");
				sb.append("[smtpHost=" + smtpHost);
				sb.append("[from=" + from + "]");
				sb.append("[subject=" + subject + "]");
				sb.append("[text=" + message + "]");
			}

			// construct a message  
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			msg.setSubject(subject);
			Iterator iter = recipients.iterator();
			while (iter.hasNext()) {
				Element recipientElement = (Element) iter.next();
				String recipient = XmlUtils.getStringValue(recipientElement);
				String typeAttr = recipientElement.getAttribute("type");
				Message.RecipientType recipientType = Message.RecipientType.TO;
				if ("cc".equalsIgnoreCase(typeAttr)) {
					recipientType = Message.RecipientType.CC;
				}
				if ("bcc".equalsIgnoreCase(typeAttr)) {
					recipientType = Message.RecipientType.BCC;
				}
				msg.addRecipient(recipientType, new InternetAddress(recipient));
				if (log.isDebugEnabled()) {
					sb.append("[recipient("+typeAttr+")=" + recipient + "]");
				}
			}

			if (attachments==null || attachments.size()==0) {
				msg.setText(message);
			} else {
				Multipart multipart = new MimeMultipart();
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(message);
				multipart.addBodyPart(messageBodyPart);
				
				iter = attachments.iterator();
				while (iter.hasNext()) {
					Element attachmentElement = (Element) iter.next();
					String attachmentText = XmlUtils.getStringValue(attachmentElement);
					String attachmentName = attachmentElement.getAttribute("name");
					String attachmentUrl = attachmentElement.getAttribute("url");
					String attachmentType = attachmentElement.getAttribute("type");
					if (StringUtils.isEmpty(attachmentType)) {
						attachmentType = getDefaultAttachmentType();
					}
					if (StringUtils.isEmpty(attachmentName)) {
						attachmentName = getDefaultAttachmentName();
					}
					log.debug("found attachment ["+attachmentName+"] type ["+attachmentType+"] url ["+attachmentUrl+"]contents ["+attachmentText+"]");
					
					messageBodyPart = new MimeBodyPart();
//					messageBodyPart.setText(attachment);
					
					DataSource attachmentDataSource;
					if (!StringUtils.isEmpty(attachmentUrl)) {
						attachmentDataSource = new URLDataSource(new URL(attachmentUrl));
						/*	
						messageBodyPart.setDataHandler(new DataHandler(attachmentDataSource));
					} 
					else {
						attachmentDataSource = new StringDataSource(attachment,attachmentName,attachmentType);
					*/			
						messageBodyPart.setDataHandler(new DataHandler(attachmentDataSource));
					}
				    
					messageBodyPart.setFileName(attachmentName);
					messageBodyPart.setText(attachmentText);
					multipart.addBodyPart(messageBodyPart);
				}
				msg.setContent(multipart);	
			}
			

			log.debug(sb.toString());
			msg.setSentDate(new Date());
			// send the message
			transport.sendMessage(msg, msg.getAllRecipients());

		} catch (Exception e) {
			throw new SenderException("MailSender got error", e);
		}
	}



	/**
	 * Set the default for From
	 */
	public void setDefaultFrom(String newFrom) {
		defaultFrom = newFrom;
	}
	/**
	 * Set the default for Subject>
	 */
	public void setDefaultSubject(String newSubject) {
		defaultSubject = newSubject;
	}
	
	/**
	 * Name of the SMTP Host.
	 */
	public void setSmtpHost(String newSmtpHost) {
		smtpHost = newSmtpHost;
	}
	public String getSmtpHost() {
		return smtpHost;
	}

	/**
	 * The userid of the SMTP Host
	 */
	public void setSmtpUserid(java.lang.String newSmtpUserid) {
		smtpUserid = newSmtpUserid;
	}
	public String getSmtpUserid() {
		return smtpUserid;
	}
	
	/**
	 * The password of the SMTP host.
	 */
	public void setSmtpPassword(String newSmtpPassword) {
		smtpPassword = newSmtpPassword;
	}
	public String getSmtpPassword() {
		return smtpPassword;
	}

	/**
	 * Name of the sender.
	 */
	public void setName(String newName) {
		name = newName;
	}
	public String getName() {
		return name;
	}
	
	public String getDefaultAttachmentName() {
		return defaultAttachmentName;
	}

	public String getDefaultAttachmentType() {
		return defaultAttachmentType;
	}

	public void setDefaultAttachmentName(String string) {
		defaultAttachmentName = string;
	}

	public void setDefaultAttachmentType(String string) {
		defaultAttachmentType = string;
	}

}
