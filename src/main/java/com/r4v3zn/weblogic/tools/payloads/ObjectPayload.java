package com.r4v3zn.weblogic.tools.payloads;

import java.io.IOException;

/**
 * Title: ObjectPayload
 * Descrption: ObjectPayload
 * Date:2020/3/23 23:04
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 * @author R4v3zn
 * @version 1.0.0
 */
public interface ObjectPayload {

    /**
     * 漏洞验证,漏洞存在返回 true 否则返回 false
     * @param ip ip
     * @param port 端口
     * @return 漏洞存在返回 true 否则返回 false
     * @throws Exception 抛出异常
     */
    Boolean vulnerable(String ip, Integer port) throws Exception;

    /**
     * 漏洞利用
     * @param ip ip
     * @param port 端口
     * @throws Exception 抛出异常
     */
    void exploit(String ip, Integer port) throws Exception;
}
