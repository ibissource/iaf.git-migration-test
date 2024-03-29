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
 * $Log: SoapRouterServlet.java,v $
 * Revision 1.5  2012-03-19 15:07:22  jaco
 * Bugfix mangled file name of WSDL when adapter name contains a space
 *
 * Revision 1.4  2011/12/15 09:55:31  jaco
 * Added Ibis WSDL generator (created by Michiel)
 *
 * Revision 1.1  2006/04/12 16:16:35  gerrit
 * extension to Apache RpcRouterServlet, that closes session after each call
 *
 */
package nl.nn.adapterframework.soap;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.soap.server.http.RPCRouterServlet;

/**
 * Modified Apache SOAP RPCRouterServlet, that invalidates the HTTPSession after each request.
 *
 * @author  Gerrit van Brakel
 * @since   4.4.5
 * @version $Id$
 */
public class SoapRouterServlet extends RPCRouterServlet {

    private final IbisSoapServlet ibisServlet = new IbisSoapServlet();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ibisServlet.init(config);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ibisServlet.doGet(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {
	  	try {
	  		super.doPost(req,res);
	  	} finally {
	  		HttpSession session = req.getSession();
	  		if (session!=null) {
	  			session.invalidate();
	  		}
	  	}
	  }

}
