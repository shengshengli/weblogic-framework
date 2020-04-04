package com.r4v3zn.weblogic.tools.payloads;

import com.r4v3zn.weblogic.tools.gadget.ObjectPayload;
import com.r4v3zn.weblogic.tools.utils.StringUtils;
import org.reflections.Reflections;
import weblogic.cluster.singleton.ClusterMasterRemote;

import java.lang.reflect.Modifier;
import java.util.*;

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
     * @param url url
     * @param param 执行参数
     * @return 漏洞存在返回 true 否则返回 false
     * @throws Exception 抛出异常
     */
    Boolean vulnerable(String url, String... param) throws Exception;

    /**
     * 漏洞利用
     * @param ip ip
     * @param port 端口
     * @throws Exception 抛出异常
     */
    String exploit(String ip, Integer port, String... param) throws Exception;



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
        public static String[] getVulNames(){
            final List<Class<? extends VulTest>> vulClasses = new ArrayList<Class<? extends VulTest>>(getVulTest());
            Collections.sort(vulClasses, new StringUtils.ToStringComparator());
            List<String> nameList = new ArrayList<>();
            for (Class<? extends VulTest> payloadClass:vulClasses) {
                String vulName = payloadClass.getSimpleName();
                vulName = vulName.trim().replace("_", "-");
                if(nameList.contains(vulName)){
                    continue;
                }
                nameList.add(vulName);
            }
            return nameList.toArray(new String[0]);
        }
    }
}
