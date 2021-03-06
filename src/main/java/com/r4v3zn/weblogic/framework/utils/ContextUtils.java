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

package com.r4v3zn.weblogic.framework.utils;

import com.r4v3zn.weblogic.framework.entity.ContextPojo;
import com.r4v3zn.weblogic.framework.entity.MyException;
import com.r4v3zn.weblogic.framework.manager.WeblogicTrustManager;
import com.r4v3zn.weblogic.framework.vuls.VulTest;
import weblogic.jndi.Environment;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: ContextUtils
 * Desc: ContextUtils
 * Date:2020/3/31 22:39
 * @version 1.0.0
 */
public class ContextUtils {


    /**
     * 私有化构造
     */
    private ContextUtils(){}


    /**
     * 获取连接上下文
     * @param url 连接字符串
     * @return 连接成功响应内容
     * @throws Exception 失败抛出异常
     */
    public static Context getContext(String url) throws Exception{
        if(isBlank(url)){
            throw new MyException("url 不能为空");
        }
        if(url.startsWith("iiop")){
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
            env.put("java.naming.provider.url" , url);
            return new InitialContext(env);
        }else if (url.startsWith("t3")){
            Environment environment = new Environment();
            environment.setProviderUrl(url);
            environment.setEnableServerAffinity(false);
            environment.setSSLClientTrustManager(new WeblogicTrustManager());
            return environment.getInitialContext();
        }
        return null;
    }

    /**
     * rebind
     * @param context context
     * @param bindName bindName
     * @param object object
     * @param urlClassLoader URLClassLoader
     * @return
     */
    public static ContextPojo rebind(Context context, String bindName, Object object, URLClassLoader urlClassLoader){
        try{
            context.rebind(bindName, object);
        }catch (Exception e){
            //
        }
        ContextPojo contextPojo = new ContextPojo();
        contextPojo.setContext(context);
        contextPojo.setUrlClassLoader(urlClassLoader);
        return contextPojo;
    }

    /**
     * rebind bind name default is "hello"
     * @param url jndi url
     * @param object object
     * @param urlClassLoader
     * @return
     * @throws Exception
     */
    public static ContextPojo rebind(String url, Object object, URLClassLoader urlClassLoader) throws Exception {
        return rebind(url, "hello", object,urlClassLoader);
    }

    /**
     * rebind
     * @param url jndi url
     * @param bindName bind name
     * @param object object
     * @param urlClassLoader URLClassLoader
     * @return ContextPojo
     * @throws Exception
     */
    public static ContextPojo rebind(String url,String bindName, Object object, URLClassLoader urlClassLoader) throws Exception {
        Context context = getContext(url);
        return rebind(context, bindName, object, urlClassLoader);
    }

    /**
     * unbind
     * @param context context
     * @param name unbind name
     * @return true or false
     */
    public static Boolean unbind(Context context,String name){
        try{
            context.unbind(name);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 清空绑定信息
     * @param vulMap
     */
    public static void clearContext(Map<String, VulTest> vulMap){
        for (String bindName:vulMap.keySet()) {
            VulTest vul = vulMap.get(bindName);
            try{
                Field contextField = vul.getClass().getDeclaredField("currentContext");
                contextField.setAccessible(true);
                Context context = (Context)contextField.get(vul);
                System.out.println("[*] unbind name "+ bindName + "  "+unbind(context, bindName));
                unbind(context, "hello");
            }catch (Exception e){
                //
            }
        }
        vulMap.clear();
    }

    public static void main(String[] args) throws Exception {
        Context context = getContext("iiop://10.10.10.172:7001");
        context.unbind("testInfo");
    }
}
