/*
 * $Log: CacheSenderWrapperProcessor.java,v $
 * Revision 1.2  2010-12-13 13:29:01  L190409
 * optimize debugging
 *
 * Revision 1.1  2010/09/13 13:50:51  gerrit
 * created cache processors
 *
 */
package nl.nn.adapterframework.processors;

import nl.nn.adapterframework.cache.ICacheAdapter;
import nl.nn.adapterframework.core.SenderException;
import nl.nn.adapterframework.core.TimeOutException;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;
import nl.nn.adapterframework.senders.SenderWrapperBase;

/**
 * SenderWrapperProcessor that handles caching.
 * 
 * @author  Gerrit van Brakel
 * @since   4.11
 * @version Id
 */
public class CacheSenderWrapperProcessor extends SenderWrapperProcessorBase {
	
	public String sendMessage(SenderWrapperBase senderWrapperBase, String correlationID, String message, ParameterResolutionContext prc) throws SenderException, TimeOutException {
		ICacheAdapter cache=senderWrapperBase.getCache();
		if (cache==null) {
			return senderWrapperProcessor.sendMessage(senderWrapperBase, correlationID, message, prc);
		}
		
		String key=cache.transformKey(message);
		if (key==null) {
			if (log.isDebugEnabled()) log.debug("cache key is null, will not use cache");
			return senderWrapperProcessor.sendMessage(senderWrapperBase, correlationID, message, prc);
		}
		if (log.isDebugEnabled()) log.debug("cache key ["+key+"]");
		String result=cache.getString(key);
		if (result==null) {
			if (log.isDebugEnabled()) log.debug("no cached results found using key ["+key+"]");
			result=senderWrapperProcessor.sendMessage(senderWrapperBase, correlationID, message, prc);
			if (log.isDebugEnabled()) log.debug("caching result using key ["+key+"]");
			cache.putString(key, result);
		} else {
			if (log.isDebugEnabled()) log.debug("retrieved result from cache using key ["+key+"]");
		}
		return result;
	}

}