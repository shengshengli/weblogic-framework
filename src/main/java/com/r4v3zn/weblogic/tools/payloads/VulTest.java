package com.r4v3zn.weblogic.tools.payloads;

import com.r4v3zn.weblogic.tools.gadget.ObjectPayload;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;

/**
 * Title: ObjectPayload
 * Desc: ObjectPayload
 * Date:2020/3/23 23:04
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 * @author R4v3zn
 * @version 1.0.0
 */
public interface VulTest {

    /**
     * 漏洞验证,漏洞存在返回 true 否则返回 false
     * @param ip ip
     * @param port 端口
     * @param param 执行参数
     * @return 漏洞存在返回 true 否则返回 false
     * @throws Exception 抛出异常
     */
    Boolean vulnerable(String ip, Integer port, String... param) throws Exception;

    /**
     * 漏洞利用
     * @param ip ip
     * @param port 端口
     * @throws Exception 抛出异常
     */
    void exploit(String ip, Integer port) throws Exception;


    public static class Utils {
        // get payload classes by classpath scanning
        public static Set<Class<? extends VulTest>> getVulTest() {
            final Reflections reflections = new Reflections(VulTest.class.getPackage().getName());
            final Set<Class<? extends VulTest>> payloadTypes = reflections.getSubTypesOf(VulTest.class);
            for (Iterator<Class<? extends VulTest>> iterator = payloadTypes.iterator(); iterator.hasNext(); ) {
                Class<? extends VulTest> pc = iterator.next();
                if ( pc.isInterface() || Modifier.isAbstract(pc.getModifiers()) ) {
                    iterator.remove();
                }
            }
            return payloadTypes;
        }
    }
}
