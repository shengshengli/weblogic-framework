package com.r4v3zn.weblogic.tools.payloads.impl;

import com.r4v3zn.weblogic.tools.annotation.Authors;
import com.r4v3zn.weblogic.tools.annotation.Dependencies;
import com.r4v3zn.weblogic.tools.annotation.Tags;
import com.r4v3zn.weblogic.tools.annotation.Versions;
import com.r4v3zn.weblogic.tools.payloads.VulTest;

/**
 * Title: CVE_2020_2555_EXT
 * Desc: TODO
 * Date:2020/4/1 0:56
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({":LimitFilter"})
@Versions({"12.1.3.0", "12.2.1.3.0", "12.2.1.4.0"})
@Tags({"Nday"})
public class CVE_2020_2555_EXT implements VulTest {

    /**
     * 漏洞验证,漏洞存在返回 true 否则返回 false
     * @param ip ip
     * @param port 端口
     * @param param 执行参数
     * @return 漏洞存在返回 true 否则返回 false
     * @throws Exception 抛出异常
     */
    @Override
    public Boolean vulnerable(String ip, Integer port, String... param) throws Exception {
        return null;
    }

    /**
     * 漏洞利用
     * @param ip ip
     * @param port 端口
     * @param param
     * @throws Exception 抛出异常
     */
    @Override
    public void exploit(String ip, Integer port, String... param) throws Exception {

    }
}
