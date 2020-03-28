package com.r4v3zn.weblogic.tools.translator;


import javassist.*;

import java.net.InetAddress;

/**
 * Title: MyTranslator
 * Desc: TODO
 * Date:2020/3/28 19:28
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
public class MyTranslator implements Translator {

    private String ip;

    private Integer port;

    public MyTranslator(String ip, Integer port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void start(ClassPool cp) throws NotFoundException, CannotCompileException {
        String classname = "weblogic.socket.SocketMuxer";
        classname = "weblogic.iiop.MuxableSocketIIOP";
        String code = "{" +
                "$1 = java.net.InetAddress.getByName(\""+ip+"\");\n" +
                "$2 = "+port+";}";
        System.out.println(code);
        CtClass ctClass = cp.get(classname);
        CtMethod ctMethod = ctClass.getDeclaredMethod("connect", new CtClass[]{cp.get(InetAddress.class.getName()), cp.get("int")});
        ctMethod.insertBefore(code);
//        CtMethod ctMethod = ctClass.getDeclaredMethods("newSocket")[1];
//        ctMethod.setBody("{\n" +
//                "    java.net.InetAddress inet = java.net.InetAddress.getByName(\""+ip+"\");\n" +
//                "    $1 = inet;\n" +
//                "    java.net.Socket var4 = new java.net.Socket();\n" +
//                "    initSocket(var4);\n" +
//                "    var4.connect(new java.net.InetSocketAddress($1, $2), $3);\n" +
//                "    return var4;\n" +
//                "}");
        ctClass.toClass();
    }

    @Override
    public void onLoad(ClassPool cp, String classname) throws NotFoundException, CannotCompileException {
    }
}
