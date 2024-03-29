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
 * $Log: MsgLogUtil.java,v $
 * Revision 1.3  2011-11-30 13:51:48  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2009/10/15 14:29:00  peter
 * first version
 *
 */

package nl.nn.adapterframework.util;

/**
 * Utility functions for message logging.
 * 
 * @author  Peter Leeuwenburgh
 * @version $Id$
 */

public class MsgLogUtil {
	public static final String MSGLOG_LEVEL_BY_DEFAULT_KEY = "msg.log.level.default";

	public static final String MSGLOG_LEVEL_NONE_STR = "None";
	public static final String MSGLOG_LEVEL_TERSE_STR = "Terse";
	public static final String MSGLOG_LEVEL_BASIC_STR = "Basic";
	public static final String MSGLOG_LEVEL_FULL_STR = "Full";

	public static final int MSGLOG_LEVEL_NONE = 0;
	public static final int MSGLOG_LEVEL_TERSE = 1;
	public static final int MSGLOG_LEVEL_BASIC = 2;
	public static final int MSGLOG_LEVEL_FULL = 3;

	private static int msgLogLevelByDefault = -1;

	public static final String msgLogLevels[] =
		{
			MSGLOG_LEVEL_NONE_STR,
			MSGLOG_LEVEL_TERSE_STR,
			MSGLOG_LEVEL_BASIC_STR,
			MSGLOG_LEVEL_FULL_STR };

	public static final int msgLogLevelNums[] =
		{
			MSGLOG_LEVEL_NONE,
			MSGLOG_LEVEL_TERSE,
			MSGLOG_LEVEL_BASIC,
			MSGLOG_LEVEL_FULL };

	public static synchronized int getMsgLogLevelByDefault() {
		if (msgLogLevelByDefault<0) {
			String msgLogLevelByDefaultString=AppConstants.getInstance().getString(MSGLOG_LEVEL_BY_DEFAULT_KEY, MSGLOG_LEVEL_NONE_STR);
			msgLogLevelByDefault = getMsgLogLevelNum(msgLogLevelByDefaultString);
		}
		return msgLogLevelByDefault;
	}

	public static int getMsgLogLevelNum(String msgLogLevel) {
		int i = msgLogLevels.length - 1;
		while (i >= 0 && !msgLogLevels[i].equalsIgnoreCase(msgLogLevel))
			i--; // try next
		if (i >= 0) {
			return msgLogLevelNums[i];
		} else {
			return i;
		}
	}

	public static String getMsgLogLevelString(int msgLogLevel) {
		int i = msgLogLevelNums.length - 1;
		while (i >= 0 && msgLogLevelNums[i] != msgLogLevel)
			i--; // try next
		if (i >= 0) {
			return msgLogLevels[i];
		} else {
			return "UnknownMsgLogLevel:" + msgLogLevel;
		}
	}
}
