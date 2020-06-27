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

package com.r4v3zn.weblogic.framework.translator;


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

    /**
     * IIOP SOCKET Class name
     * "weblogic.socket.SocketMuxer";
     * "weblogic.iiop.MuxableSocketIIOP";
     */
    public static final String IIOP_SOCKET_CLASS_NAME= "weblogic.socket.SocketMuxer";

    private String ip;

    private Integer port;

    public MyTranslator(String ip, Integer port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void start(ClassPool cp) throws NotFoundException, CannotCompileException {
        CtClass ctClass = cp.get(IIOP_SOCKET_CLASS_NAME);
        if(ctClass.isFrozen()){
            ctClass.defrost();
        }
        String code = "{" +
                "$1 = java.net.InetAddress.getByName(\""+ip+"\");\n" +
                "$2 = "+port+";}";
        code = "{\n" +
                "    java.net.InetAddress inet = java.net.InetAddress.getByName(\""+ip+"\");\n" +
                "    $1 = inet;\n" +
                "    java.net.Socket var4 = new java.net.Socket();\n" +
                "    initSocket(var4);\n" +
                "    var4.connect(new java.net.InetSocketAddress($1, $2), $3);\n" +
                "    return var4;\n" +
                "}";
        System.out.println(code);
//        ctMethod.insertBefore(code);
//        CtMethod ctMethod = ctClass.getDeclaredMethods("newSocket")[1];
//        ctMethod.setBody("{\n" +
//                "    java.net.InetAddress inet = java.net.InetAddress.getByName(\""+ip+"\");\n" +
//                "    $1 = inet;\n" +
//                "    java.net.Socket var4 = new java.net.Socket();\n" +
//                "    initSocket(var4);\n" +
//                "    var4.connect(new java.net.InetSocketAddress($1, $2), $3);\n" +
//                "    return var4;\n" +
//                "}");
//        CtMethod ctMethod = ctClass.getDeclaredMethods("newSocket")[1];
//        ctMethod.setBody(code);
//        ctClass.toClass();
//        ctClass.toClass();
    }

    @Override
    public void onLoad(ClassPool cp, String classname) throws NotFoundException, CannotCompileException {
    }
}
