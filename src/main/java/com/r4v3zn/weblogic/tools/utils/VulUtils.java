package com.r4v3zn.weblogic.tools.utils;

import com.r4v3zn.weblogic.tools.annotation.Authors;
import com.r4v3zn.weblogic.tools.annotation.Dependencies;
import com.r4v3zn.weblogic.tools.annotation.Tags;
import com.r4v3zn.weblogic.tools.annotation.Versions;
import com.r4v3zn.weblogic.tools.payloads.VulTest;

import java.util.*;

/**
 * Title: VulUtils
 * Descrption: 漏洞工具类
 * Date:2020/4/2 21:02
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class VulUtils {

    /**
     * 私有化构造防止被实例化
     */
    private VulUtils(){}

    public static List<String[]> getVulList(){
        final List<Class<? extends VulTest>> vulClasses =
                new ArrayList<Class<? extends VulTest>>(VulTest.Utils.getVulTest());
        Collections.sort(vulClasses, new StringUtils.ToStringComparator());
        final List<String[]> rows = new LinkedList<String[]>();
        for (Class<? extends VulTest> payloadClass : vulClasses) {
            rows.add(new String[] {
                    payloadClass.getSimpleName().replace("_","-"),
                    StringUtils.join(Arrays.asList(Authors.Utils.getAuthors(payloadClass)), ", ", "@", ""),
//                    StringUtils.join(Arrays.asList(Dependencies.Utils.getDependenciesSimple(payloadClass)),", ", "", ""),
//                    StringUtils.join(Arrays.asList(Versions.Utils.getVersionsSimple(payloadClass)),", ", "", "")
            });
        }
        return rows;
    }

    /**
     * 获取漏洞信息
     * @return
     */
    public static String getVulInfo(){
        List<String[]> rows = getVulList();
        rows.add(0,new String[] {"Vul", "Authors"});
        TablePrintUtil printUtil = TablePrintUtil.build(rows).setAlign(TablePrintUtil.ALIGN_LEFT).setEquilong(true);
        return printUtil.getTableString();
    }
}
