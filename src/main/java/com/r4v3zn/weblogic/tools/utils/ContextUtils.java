package com.r4v3zn.weblogic.tools.utils;

import com.r4v3zn.weblogic.tools.entity.ContextPojo;
import com.r4v3zn.weblogic.tools.entity.MyException;
import com.r4v3zn.weblogic.tools.gadget.ObjectPayload;
import com.r4v3zn.weblogic.tools.gadget.impl.LimitFilterGadget;
import com.r4v3zn.weblogic.tools.manager.WeblogicTrustManager;
import weblogic.jndi.Environment;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;

import static com.r4v3zn.weblogic.tools.payloads.impl.S1274489.*;
import static com.r4v3zn.weblogic.tools.utils.SocketUtils.hexStrToBinaryStr;
import static com.r4v3zn.weblogic.tools.utils.VersionUtils.getVersion;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: Context
 * Desc: TODO
 * Date:2020/3/31 22:39
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
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
            Hashtable<String, String> env = new Hashtable<>();
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
            e.printStackTrace();
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

}
