import weblogic.cluster.singleton.SingletonMonitorRemote;

import java.rmi.RemoteException;

/**
 * Title: PocSingletonMonitorRemote
 * Desc: TODO
 * Date:2020/4/3 21:23
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class PocSingletonMonitorRemote implements SingletonMonitorRemote {
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
    public String findServiceLocation(String s) throws RemoteException {
        return null;
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
}
