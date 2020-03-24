package com.r4v3zn.weblogic.tools.utils;

import cn.hutool.core.util.ReUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.r4v3zn.weblogic.tools.utils.SocketUtils.send;

/**
 * Title: VersionUtils
 * Descrption: weblogic 版本工具类
 * Date:2020/3/23 22:37
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 * @author R4v3zn
 * @version 1.0.0
 */
public class VersionUtils {

    /**
     * 获取 weblogic 版本号发包内容
     */
    private static String VERSION_T3 = "74332031322e322e310a41533a3235350a484c3a31390a4d533a31303030303030300a50553a74333a2f2f75732d6c2d627265656e733a373030310a0a";

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
        String version = getVersionByHttp(webLogicUrl);
        if("".equals(version)){
            version = getVersionByT3(ip, port);
        }
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
            version = getVersion(versionTmpStr);
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
            byte[] rspByte = send(VERSION_T3, socket);
            socket.close();
            version = getVersion(new String(rspByte));
        } catch (Exception e) {
            version = "";
        }
        return version;
    }

    /**
     * 根据内容提取版本号
     * @param content 内容信息
     * @return 版本号
     */
    public static String getVersion(String content){
        content = content.replace("HELO:", "").replace(".false","").replace(".true", "");
        String getVersionRegex = "[\\d\\.]+";
        List<String> result = ReUtil.findAll(getVersionRegex, content, 0 , new ArrayList<String>());
        return  result != null && result.size() > 0 ? result.get(0) : "";
    }

    public static void main(String[] args) throws NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
        String ip = "192.168.1.9";
        String port = "7001";
        env.put("java.naming.provider.url" , String.format("t3s://%s:%s", ip, port));
        Context context = new InitialContext(env);
    }
}
