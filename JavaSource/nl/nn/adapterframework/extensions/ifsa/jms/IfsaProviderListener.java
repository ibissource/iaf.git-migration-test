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
 * $Log: IfsaProviderListener.java,v $
 * Revision 1.8  2011-11-30 13:51:43  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2008/01/03 15:46:19  gerrit
 * split IfsaProviderListener into a Pulling and a Pushing version
 *
 * Revision 1.5  2007/11/21 13:17:47  gerrit
 * improved error logging
 *
 * Revision 1.4  2007/11/15 12:38:08  gerrit
 * fixed message wrapping
 *
 * Revision 1.3  2007/10/17 09:32:49  gerrit
 * store originalRawMessage when wrapper is created, use it to send reply
 *
 * Revision 1.2  2007/10/16 08:39:30  gerrit
 * moved IfsaException and IfsaMessageProtocolEnum back to main package
 *
 * Revision 1.1  2007/10/16 08:15:43  gerrit
 * introduced switch class for jms and ejb
 *
 * Revision 1.33  2007/10/03 08:32:41  gerrit
 * changed HashMap to Map
 *
 * Revision 1.32  2007/09/25 11:33:14  gerrit
 * show headers of incoming messages
 *
 * Revision 1.31  2007/09/13 09:12:43  gerrit
 * move message wrapper from ifsa to receivers
 *
 * Revision 1.30  2007/09/05 15:48:07  gerrit
 * moved XA determination capabilities to IfsaConnection
 *
 * Revision 1.29  2007/08/27 11:50:18  gerrit
 * provide default result for RR
 *
 * Revision 1.28  2007/08/10 11:18:50  gerrit
 * removed attribute 'transacted'
 * automatic determination of transaction state and capabilities
 * removed (more or less hidden) attribute 'commitOnState'
 * warning about non XA FF
 *
 * Revision 1.27  2007/02/16 14:19:08  gerrit
 * updated javadoc
 *
 * Revision 1.26  2007/02/05 14:57:00  gerrit
 * set default timeout to 3000
 *
 * Revision 1.25  2006/11/01 14:22:42  gerrit
 * avoid NPE for null commitOnState
 *
 * Revision 1.24  2006/10/13 08:23:59  gerrit
 * do not process null UDZ
 *
 * Revision 1.23  2006/10/13 08:11:30  gerrit
 * copy UDZ to session-variables
 *
 * Revision 1.22  2006/08/21 15:08:35  gerrit
 * corrected javadoc
 *
 * Revision 1.21  2006/07/17 08:54:18  gerrit
 * documented custom property ifsa.provider.useSelectors
 *
 * Revision 1.20  2006/03/08 13:55:49  gerrit
 * getRawMessage now returns null again if no message received if transacted, 
 * to avoid transaction time out
 *
 * Revision 1.19  2006/02/20 15:49:54  gerrit
 * improved handling of PoisonMessages, should now work under transactions control
 *
 * Revision 1.18  2006/01/05 13:55:27  gerrit
 * updated javadoc
 *
 * Revision 1.17  2005/12/20 16:59:27  gerrit
 * implemented support for connection-pooling
 *
 * Revision 1.16  2005/10/27 08:48:31  gerrit
 * introduced RunStateEnquiries
 *
 * Revision 1.15  2005/10/24 15:14:02  gerrit
 * shuffled positions of methods
 *
 * Revision 1.14  2005/09/26 11:47:26  gerrit
 * Jms-commit only if not XA-transacted
 * ifsa-messageWrapper for (non-serializable) ifsa-messages
 *
 * Revision 1.13  2005/09/13 15:48:27  gerrit
 * changed acknowledge mode back to AutoAcknowledge
 *
 * Revision 1.12  2005/07/28 07:31:54  gerrit
 * change default acknowledge mode to CLIENT
 *
 * Revision 1.11  2005/06/20 09:14:17  gerrit
 * avoid excessive logging
 *
 * Revision 1.10  2005/06/13 15:08:37  gerrit
 * avoid excessive logging in debug mode
 *
 * Revision 1.9  2005/06/13 12:43:03  gerrit
 * added support for pooled sessions and for XA-support
 *
 * Revision 1.8  2005/02/17 09:45:30  gerrit
 * increased logging
 *
 * Revision 1.7  2005/01/13 08:55:37  gerrit
 * Make threadContext-attributes available in PipeLineSession
 *
 * Revision 1.6  2004/09/22 07:03:36  johan
 * Added logstatements for closing receiver and session
 *
 * Revision 1.5  2004/09/22 06:48:08  johan
 * Changed loglevel in getStringFromRawMessage to warn
 *
 * Revision 1.4  2004/07/19 09:50:03  gerrit
 * try to send exceptionmessage as reply when sending reply results in exception
 *
 * Revision 1.3  2004/07/15 07:43:04  gerrit
 * updated javadoc
 *
 * Revision 1.2  2004/07/08 12:55:57  gerrit
 * logging refinements
 *
 * Revision 1.1  2004/07/05 14:28:38  gerrit
 * First version, converted from IfsaServiceListener
 *
 * Revision 1.4  2004/03/26 07:25:42  johan
 * Updated erorhandling
 *
 * Revision 1.3  2004/03/24 15:27:24  gerrit
 * solved uncaught exception in error message
 *
 */
package nl.nn.adapterframework.extensions.ifsa.jms;

public class IfsaProviderListener extends PushingIfsaProviderListener {

}
