/*
 * Copyright  2020.  r4v3zn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.weblogic.framework.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title: IIOPUtils
 * Desc: IIOP 工具类
 * Date:2020/3/23 23:50
 * @version 1.0.0
 */
public class IIOPUtils {

    /**
     * host 提取正则表达式
     */
    private static final String HOST_REGX = "https?://([\\w\\:.-]+)/";

    /**
     * 私有化构造防止被实例化
     */
    private IIOPUtils(){}


    /**
     * 获取 IIOP 结构 key
     * @param content IIOP 内容
     * @param flag 标识符
     * @return key
     */
    public static String getKey(String content, Boolean flag){
        String startHex = "00424541";
        int startIndex = -1;
        if(flag){
            startIndex = content.indexOf(startHex);
        }else if(content.contains("0000000300000000")){
            return null;
        }else{
            startIndex = content.lastIndexOf(startHex);
        }
        if(startIndex != -1) {
            int keyLength = Integer.parseInt(content.substring(startIndex-8, startIndex), 16);
            // 提取key
            return content.substring(startIndex, startIndex + keyLength*2);
        }else{
            return null;
        }
    }

    /**
     * 获取 NAT 网络 host
     * @param content 内容信息
     * @return NAT 网络 host
     */
    public static String getNatHost(String content){
        Pattern p = Pattern.compile(HOST_REGX);
        Matcher m=p.matcher(content);
        String ip = "";
        if(m.find()){
            ip = m.group(1);
        }
        return ip;
    }

    /**
     * 填充函数
     * @param str 需要填充的字符串
     * @param strLength 总长度
     * @return 填充结果
     */
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 左补0
                // sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }
}
