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
 * $Log: CacheAdapterBase.java,v $
 * Revision 1.6  2011-11-30 13:51:48  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:51  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2011/06/20 13:14:03  gerrit
 * added output-types for xpath expressions
 *
 * Revision 1.3  2011/05/31 15:30:02  gerrit
 * support for transformation of values,
 * support for configurable caching of empty keys and values
 *
 * Revision 1.2  2010/09/20 15:48:41  gerrit
 * added warning for empty key
 *
 * Revision 1.1  2010/09/13 13:28:19  gerrit
 * added cache facility
 *
 */
package nl.nn.adapterframework.cache;

import java.io.Serializable;
import java.util.Map;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.util.LogUtil;
import nl.nn.adapterframework.util.TransformerPool;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Baseclass for caching.
 * Provides key transformation functionality.
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Cache, will be set from owner</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setKeyXPath(String) keyXPath}</td><td>xpath expression to extract cache key from request message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setKeyXPathOutputType(String) keyXPathOutputType}</td><td>output type of xpath expression to extract cache key from request message, must be 'xml' or 'text'</td><td>text</td></tr>
 * <tr><td>{@link #setKeyNamespaceDefs(String) keyNamespaceDefs}</td><td>namespace defintions for keyXPath. Must be in the form of a comma or space separated list of <code>prefix=namespaceuri</code>-definitions</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setKeyStyleSheet(String) keyStyleSheet}</td><td>stylesheet to extract cache key from request message. Use in combination with {@link #setCacheEmptyKeys(String) cacheEmptyKeys} to inhibit caching for certain groups of request messages</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setKeyInputSessionKey(String) keyInputSessionKey}</td><td>session key to use as input for transformation of request message to key by keyXPath or keyStyleSheet</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCacheEmptyKeys(String) cacheEmptyKeys}</td><td>controls whether empty keys are used for caching. When set true, cache entries with empty keys can exist.</td><td>false</td></tr>
 * <tr><td>{@link #setValueXPath(String) valueXPath}</td><td>xpath expression to extract value to be cached key from response message. Use in combination with {@link #setCacheEmptyValues(String) cacheEmptyValues} to inhibit caching for certain groups of response messages</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setValueKeyXPathOutputType(String) valueXPathOutputType}</td><td>output type of xpath expression to extract value to be cached key from response message, must be 'xml' or 'text'</td><td>xml</td></tr>
 * <tr><td>{@link #setValueNamespaceDefs(String) valueNamespaceDefs}</td><td>namespace defintions for valueXPath. Must be in the form of a comma or space separated list of <code>prefix=namespaceuri</code>-definitions</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setValueStyleSheet(String) valueStyleSheet}</td><td>stylesheet to extract value to be cached from response message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setValueInputSessionKey(String) valueInputSessionKey}</td><td>session key to use as input for transformation of response message to cached value by valueXPath or valueStyleSheet</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setCacheEmptyValues(String) cacheEmptyValues}</td><td>controls whether empty values will be cached. When set true, empty cache entries can exist for any key.</td><td>false</td></tr>
 * </table>
 * </p>
 * 
 * @author  Gerrit van Brakel
 * @since   4.11
 * @version $Id$
 */
public abstract class CacheAdapterBase implements ICacheAdapter {
	protected Logger log = LogUtil.getLogger(this);

	private String name;

	private String keyXPath;
	private String keyXPathOutputType="text";
	private String keyNamespaceDefs;
	private String keyStyleSheet;
	private String keyInputSessionKey;
	private boolean cacheEmptyKeys=false;

	private String valueXPath;
	private String valueXPathOutputType="xml";
	private String valueNamespaceDefs;
	private String valueStyleSheet;
	private String valueInputSessionKey;
	private boolean cacheEmptyValues=false;

	private TransformerPool keyTp=null;
	private TransformerPool valueTp=null;

	public void configure(String ownerName) throws ConfigurationException {
		if (StringUtils.isEmpty(getName())) {
			setName(ownerName+"Cache");
		}
		if (!("xml".equals(getKeyXPathOutputType()) || "text".equals(getKeyXPathOutputType()))) {
			throw new ConfigurationException(getLogPrefix()+"keyXPathOutputType ["+getKeyXPathOutputType()+"] must be either 'xml' or 'text'");
		}
		if (!("xml".equals(getValueXPathOutputType()) || "text".equals(getValueXPathOutputType()))) {
			throw new ConfigurationException(getLogPrefix()+"valueXPathOutputType ["+getValueXPathOutputType()+"] must be either 'xml' or 'text'");
		}
		if (StringUtils.isNotEmpty(getKeyXPath()) || StringUtils.isNotEmpty(getKeyStyleSheet())) {
			keyTp=TransformerPool.configureTransformer(getLogPrefix(),getKeyNamespaceDefs(), getKeyXPath(), getKeyStyleSheet(),getKeyXPathOutputType(),false,null);
		}
		if (StringUtils.isNotEmpty(getValueXPath()) || StringUtils.isNotEmpty(getValueStyleSheet())) {
			valueTp=TransformerPool.configureTransformer(getLogPrefix(),getValueNamespaceDefs(), getValueXPath(), getValueStyleSheet(),getValueXPathOutputType(),false,null);
		}
	}
	
	protected abstract Serializable getElement(String key);
	protected abstract void putElement(String key, Serializable value);

	public String transformKey(String input, Map sessionContext) {
		if (StringUtils.isNotEmpty(getKeyInputSessionKey()) && sessionContext!=null) {
			input=(String)sessionContext.get(getKeyInputSessionKey());
		}
		if (keyTp!=null) {
			try {
				input=keyTp.transform(input, null);
			} catch (Exception e) {
			   log.error(getLogPrefix()+"cannot determine cache key",e);
			}
		}
		if (StringUtils.isEmpty(input)) {
			log.debug("determined empty cache key");
			if (isCacheEmptyKeys()) {
				return "";
			}
			return null;
		}
		return input;
	}

	public String transformValue(String value, Map sessionContext) {
		if (StringUtils.isNotEmpty(getValueInputSessionKey()) && sessionContext!=null) {
			value=(String)sessionContext.get(getValueInputSessionKey());
		}
		if (valueTp!=null) {
			try{
				value=valueTp.transform(value, null);
			} catch (Exception e) {
			   log.error(getLogPrefix()+"transformValue() cannot transform cache value ["+value+"], will not cache",e);
			   return null; 
			}
		}
		if (StringUtils.isEmpty(value)) {
			log.debug("determined empty cache value");
			if (isCacheEmptyValues()) {
				return "";
			}
			return null;
		}
		return value;
	}

	public String getString(String key) {
		return (String)getElement(key);
	}
	public void putString(String key, String value) {
		putElement(key, value);
	}

	public Serializable get(String key){
		return getElement(key);
	}
	public void put(String key, Serializable value) {
		putElement(key, value);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name=name;
	}

	public String getLogPrefix() {
		return "cache ["+getName()+"] ";
	}
	
	public String getKeyXPath() {
		return keyXPath;
	}
	public void setKeyXPath(String keyXPath) {
		this.keyXPath = keyXPath;
	}
	public String getKeyXPathOutputType() {
		return keyXPathOutputType;
	}
	public void setKeyXPathOutputType(String keyXPathOutputType) {
		this.keyXPathOutputType = keyXPathOutputType;
	}
	public String getKeyNamespaceDefs() {
		return keyNamespaceDefs;
	}
	public void setKeyNamespaceDefs(String keyNamespaceDefs) {
		this.keyNamespaceDefs = keyNamespaceDefs;
	}
	public String getKeyStyleSheet() {
		return keyStyleSheet;
	}
	public void setKeyStyleSheet(String keyStyleSheet) {
		this.keyStyleSheet = keyStyleSheet;
	}

	public String getKeyInputSessionKey() {
		return keyInputSessionKey;
	}
	public void setKeyInputSessionKey(String keyInputSessionKey) {
		this.keyInputSessionKey = keyInputSessionKey;
	}

	public boolean isCacheEmptyKeys() {
		return cacheEmptyKeys;
	}
	public void setCacheEmptyKeys(boolean cacheEmptyKeys) {
		this.cacheEmptyKeys = cacheEmptyKeys;
	}

	public String getValueXPath() {
		return valueXPath;
	}
	public void setValueXPath(String valueXPath) {
		this.valueXPath = valueXPath;
	}
	public String getValueXPathOutputType() {
		return valueXPathOutputType;
	}
	public void setValueXPathOutputType(String valueXPathOutputType) {
		this.valueXPathOutputType = valueXPathOutputType;
	}
	public String getValueNamespaceDefs() {
		return valueNamespaceDefs;
	}
	public void setValueNamespaceDefs(String valueNamespaceDefs) {
		this.valueNamespaceDefs = valueNamespaceDefs;
	}
	public String getValueStyleSheet() {
		return valueStyleSheet;
	}
	public void setValueStyleSheet(String valueStyleSheet) {
		this.valueStyleSheet = valueStyleSheet;
	}
	
	public String getValueInputSessionKey() {
		return valueInputSessionKey;
	}
	public void setValueInputSessionKey(String valueInputSessionKey) {
		this.valueInputSessionKey = valueInputSessionKey;
	}

	public boolean isCacheEmptyValues() {
		return cacheEmptyValues;
	}
	public void setCacheEmptyValues(boolean cacheEmptyValues) {
		this.cacheEmptyValues = cacheEmptyValues;
	}

}
