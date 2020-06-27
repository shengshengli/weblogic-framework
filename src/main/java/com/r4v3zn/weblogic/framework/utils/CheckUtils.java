package com.r4v3zn.weblogic.framework.utils;

import com.r4v3zn.weblogic.framework.entity.MyException;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

/**
 * Title: CheckUtils
 * Desc: 检测目标
 * Date:2020/6/25 18:00
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class CheckUtils {

    /**
     * 私有化构造，防止被实例化
     */
    private CheckUtils(){}

    private static final Integer SOCKET_TIME_OUT = 15000;

    /**
     * 检测目标
     * @param target 目标URL
     * @throws MyException
     */
    public static Socket getSocket(String target) throws Exception {
        URL url = new URL(target);
        int port = url.getPort() == -1 ? 80:url.getPort();
        String host = url.getHost();
        SocketAddress socketAddress = new InetSocketAddress(host ,port);
        Socket socket = new Socket();
        socket.connect(socketAddress,SOCKET_TIME_OUT);
        socket.setSoTimeout(SOCKET_TIME_OUT);
        return socket;
    }

    /**
     * 检测 IIOP 协议是否开放
     * @param target 目标URL
     * @throws MyException
     */
    public static void checkIIOP(String target) throws Exception {
        Socket socket = getSocket(target);
        try {
            byte[] rspByte = SocketUtils.getNameService(socket);
            String rsp = new String(rspByte);
            if(!rsp.contains("NamingContextAny") && !rsp.contains("weblogic") && !rsp.contains("corba")){
                throw new MyException(target+" IIOP 协议未开放!");
            }
        } catch (Exception e) {
            throw new MyException(target+" IIOP 协议未开放!");
        }finally {
            socket.close();
        }
    }

    /**
     * 检测T3协议是否开放
     * @param target 目标URL
     * @throws MyException
     */
    public static void checkT3(String target) throws Exception {
        Socket socket = getSocket(target);
        try{
            byte[] rspByte = SocketUtils.send(VersionUtils.VERSION_T3, socket);
            String rsp = new String(rspByte);
            if(rsp.contains("<title>") || rsp.contains("<html>") || rsp.contains("400") || rsp.contains("403")){
                throw new MyException(target+" T3 协议未开放!");
            }
        }catch (Exception e){
            throw new MyException(target+" T3 协议未开放!");
        }finally {
            socket.close();
        }
    }
}
