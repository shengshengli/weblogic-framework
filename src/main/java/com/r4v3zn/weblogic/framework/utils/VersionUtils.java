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

package com.r4v3zn.weblogic.framework.utils;

import cn.hutool.core.util.ReUtil;
import com.r4v3zn.weblogic.framework.entity.MyException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: VersionUtils
 * Desc: weblogic 版本工具类
 * Date:2020/3/23 22:37
 * @version 1.0.0
 */
public class VersionUtils {

//    static Logger log = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    /**
     * 获取 weblogic 版本号发包内容
     */
    public static String VERSION_T3 = "74332031322e322e310a41533a3235350a484c3a31390a4d533a31303030303030300a50553a74333a2f2f75732d6c2d627265656e733a373030310a0a";

    /**
     * 私有化构造防止被实例化
     */
    private VersionUtils(){}

    /**
     * 获取 weblogic 版本号
     * @param ip ip
     * @param port 端口
     * @return 版本号
     */
    public static String getVersion(String ip, Integer port){
        String webLogicUrl = "http://"+ip+":"+port;
        String version = getVersionByT3(ip, port);
        if("".equals(version) || version.length() < 3){
            version =  getVersionByHttp (webLogicUrl);
        }
        return version;
    }

    public static String getVersionClear(String version){
        if(!isBlank(version)){
            try{
                if (Integer.parseInt(version.split("\\.")[0]) < 10){
                    version = "10.3.6.0";
                }else if (Integer.parseInt(version.split("\\.")[0]) == 12 && Integer.parseInt(version.split("\\.")[1]) == 1){
                    version = "12.1.3.0";
                }
            }catch (Exception e){
            }
        }else{
            // log.error("版本获取失败，设置默认版本为10.3.6.0");
            version = "10.3.6.0";
        }
        // log.info("[*] weblogic version --> "+version);
        return version;
    }

    /**
     * 根据 HTTP 协议获取 weblogic 版本
     * @param url weblogic url
     * @return 版本号
     */
    public static String getVersionByHttp(String url){
        String version = "";
        url += "/console/login/LoginForm.jsp";
        try {
            Document doc = Jsoup.connect(url).get();
            String versionTmpStr = doc.getElementById("footerVersion").text();
            version = getVersionByContent(versionTmpStr);
        } catch (Exception e) {
            version = "";
        }
        return version;
    }

    /**
     * 根据 T3 协议获取 weblogic 版本
     * @param ip ip
     * @param port 端口
     * @return 版本号
     */
    public static String getVersionByT3(String ip, Integer port){
        String version = "";
        try {
            Socket socket = new Socket(ip, port);
            byte[] rspByte = SocketUtils.send(VERSION_T3, socket);
            socket.close();
            version = getVersionByContent(new String(rspByte));
        } catch (Exception e) {
            version = "";
        }
        return version;
    }

    /**
     * 根据内容提取版本号
     * @param url 内容信息
     * @return 版本号
     */
    public static String getVersion(String url){
        try {
            if(!url.startsWith("http")){
                return "";
            }
            URL checkUrl = new URL(url);
            int port = checkUrl.getPort() == -1 ? 80 : checkUrl.getPort();
            String ip = checkUrl.getHost();
            return getVersion(ip, port);
        } catch (MalformedURLException e) {
            return "";
        }
    }

    /**
     * 获取版本号
     * @param content
     * @return
     */
    public static String getVersionByContent(String content){
        if ((content.contains("404") || content.contains("400")) && content.contains("NotFound")){
            return "";
        }
        content = content.replace("HELO:", "").replace(".false","").replace(".true", "");
        String getVersionRegex = "[\\d\\.]+";
        List<String> result = ReUtil.findAll(getVersionRegex, content, 0 , new ArrayList<String>());
        String version = result != null && result.size() > 0 ? result.get(0) : "";
        if(version.length() < 4){
            version = "";
        }
        if(!version.contains(".")){
            version = "";
        }
        return  version;
    }

    /**
     * 检测是否属于影响范围
     * @param version 版本
     * @param vulVersions 影响版本
     * @return
     */
    public static Boolean checkVersion(String version, final String... vulVersions){
        if (isBlank(version)){
            throw new MyException("无法获取版本");
        }
        version = version.replace(".0.0",".0");
        if(vulVersions.length == 0){
            throw new MyException("影响版本不能为空");
        }
        return Arrays.asList(vulVersions).contains(version);
    }

    public static void main(String[] args) {
        String version = getVersion("http://222.212.254.100:8101/");
        System.out.println(version);
    }
}
