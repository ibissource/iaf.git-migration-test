/*
 * $Log: BrowseJdbcTableExecute.java,v $
 * Revision 1.1  2007-05-21 12:24:57  europe\L190409
 * added browseJdbcTable functions
 *
 *
 */
package nl.nn.adapterframework.webcontrol.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.nn.adapterframework.core.IMessageBrowser;
import nl.nn.adapterframework.core.IMessageBrowsingIterator;
import nl.nn.adapterframework.core.ListenerException;
import nl.nn.adapterframework.jms.JmsMessageBrowser;
import nl.nn.adapterframework.jms.JmsRealmFactory;
import nl.nn.adapterframework.util.AppConstants;
import nl.nn.adapterframework.util.StringTagger;
import nl.nn.adapterframework.util.XmlUtils;
import nl.nn.adapterframework.webcontrol.IniDynaActionForm;
import nl.nn.adapterframework.jdbc.DirectQuerySender;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.commons.lang.StringUtils;

public class BrowseJdbcTableExecute extends ActionBase {
	public static final String version = "$RCSfile: BrowseJdbcTableExecute.java,v $ $Revision: 1.1 $ $Date: 2007-05-21 12:24:57 $";

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

		// Initialize action
		initAction(request);
		if (null == config) {
			return (mapping.findForward("noconfig"));
		}

		// Was this transaction cancelled?
		// -------------------------------
		if (isCancelled(request)) {
			log.debug("browseJdbcTable was cancelled");
			removeFormBean(mapping, request);
			return (mapping.findForward("cancel"));
		}

		// Retrieve form content
		// ---------------------
		IniDynaActionForm browseJdbcTableForm = (IniDynaActionForm) form;
		String form_jmsRealm = (String) browseJdbcTableForm.get("jmsRealm");
		String form_tableName = (String) browseJdbcTableForm.get("tableName");
		boolean form_numberOfRowsOnly = false;
		String form_order = (String) browseJdbcTableForm.get("order");
		if (browseJdbcTableForm.get("numberOfRowsOnly") != null)
			form_numberOfRowsOnly =
				((Boolean) browseJdbcTableForm.get("numberOfRowsOnly"))
					.booleanValue();
		String form_rownumMin = (String) browseJdbcTableForm.get("rownumMin");
		String form_rownumMax = (String) browseJdbcTableForm.get("rownumMax");

		DirectQuerySender qs;
		String result = "";
		String query = null;
		try {
			qs = new DirectQuerySender();
			try {
				qs.setName("QuerySender");
				qs.setJmsRealm(form_jmsRealm);

				String subQuery = null;
				if (form_numberOfRowsOnly) {
					query = "SELECT COUNT(*) AS COUNT";
					subQuery =
						"SELECT ROWNUM AS ROWNUMBER FROM " + form_tableName;
				} else {
					query = "SELECT *";
					subQuery =
						"SELECT ROWNUM AS ROWNUMBER, "
							+ form_tableName
							+ ".* FROM "
							+ form_tableName;
				}
				if (StringUtils.isNotEmpty(form_order)) {
					subQuery = subQuery + " ORDER BY " + form_order;
				}
				query = query + " FROM (" + subQuery + ")";
				if (StringUtils.isNotEmpty(form_rownumMin)
					&& StringUtils.isNotEmpty(form_rownumMax)) {
					query =
						query
							+ " WHERE ROWNUMBER BETWEEN "
							+ form_rownumMin
							+ " AND "
							+ form_rownumMax;
				} else if (StringUtils.isNotEmpty(form_rownumMin)) {
					query = query + " WHERE ROWNUMBER >= " + form_rownumMin;
				} else if (StringUtils.isNotEmpty(form_rownumMax)) {
					query = query + " WHERE ROWNUMBER <= " + form_rownumMax;
				}
				qs.setQueryType("select");
				qs.configure();
				qs.open();
				result = qs.sendMessage("dummy", query);
			} catch (Throwable t) {
				log.error(t);
				errors.add(
					"",
					new ActionError(
						"errors.generic",
						"error occured on executing jdbc query: "
							+ XmlUtils.encodeChars(t.getMessage())));
			} finally {
				qs.close();
			}
		} catch (Exception e) {
			log.error(e);
			errors.add(
				"",
				new ActionError(
					"errors.generic",
					"error occured on creating or closing connection: "
						+ XmlUtils.encodeChars(e.getMessage())));
		}
		String resultEnvelope =
			"<resultEnvelope>"
				+ "<request "
				+ "tableName=\""
				+ form_tableName
				+ "\">"
				+ query
				+ "</request>"
				+ result
				+ "</resultEnvelope>";

		request.setAttribute("DB2Xml", resultEnvelope);

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			StoreFormData(browseJdbcTableForm);
			saveErrors(request, errors);
			return (new ActionForward(mapping.getInput()));
		}

		//Successfull: store cookie
		String cookieValue = "";
		cookieValue += "jmsRealm=\"" + form_jmsRealm + "\"";
		cookieValue += " "; //separator
		cookieValue += "tableName=\"" + form_tableName + "\"";
		cookieValue += " "; //separator          
		cookieValue += "rownumMin=\"" + form_rownumMin + "\"";
		cookieValue += " "; //separator          
		cookieValue += "rownumMax=\"" + form_rownumMax + "\"";
		Cookie sendJdbcBrowseCookie =
			new Cookie(
				AppConstants.getInstance().getProperty(
					"WEB_JDBCBROWSECOOKIE_NAME"),
				cookieValue);
		sendJdbcBrowseCookie.setMaxAge(Integer.MAX_VALUE);
		log.debug(
			"Store cookie for "
				+ request.getServletPath()
				+ " cookieName["
				+ AppConstants.getInstance().getProperty(
					"WEB_JDBCBROWSECOOKIE_NAME")
				+ "] "
				+ " cookieValue["
				+ new StringTagger(cookieValue).toString()
				+ "]");
		try {
			response.addCookie(sendJdbcBrowseCookie);
		} catch (Throwable e) {
			log.warn(
				"unable to add cookie to request. cookie value ["
					+ sendJdbcBrowseCookie.getValue()
					+ "]");
		}

		// Forward control to the specified success URI
		log.debug("forward to success");
		return (mapping.findForward("success"));

	}
	public void StoreFormData(IniDynaActionForm form) {
		ArrayList jmsRealms =
			JmsRealmFactory.getInstance().getRegisteredRealmNamesAsList();
		if (jmsRealms.size() == 0)
			jmsRealms.add("no realms defined");
		form.set("jmsRealms", jmsRealms);

	}

}