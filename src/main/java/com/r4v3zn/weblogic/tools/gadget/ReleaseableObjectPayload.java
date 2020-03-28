package com.r4v3zn.weblogic.tools.gadget;


/**
 * @author mbechler
 *
 */
public interface ReleaseableObjectPayload<T> extends ObjectPayload<T> {

    void release(T obj) throws Exception;
}
