import weblogic.cluster.migration.MigrationException;
import weblogic.cluster.migration.RemoteMigratableServiceCoordinator;
import weblogic.store.PersistentStoreException;

import java.rmi.RemoteException;

/**
 * Title: PocServerRemoteMigratableServiceCoordinator
 * Desc: TODO
 * Date:2020/4/3 21:24
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class PocServerRemoteMigratableServiceCoordinator implements RemoteMigratableServiceCoordinator {
    @Override
    public void migrateJTA(String s, String s1, boolean b, boolean b1) throws MigrationException {

    }

    @Override
    public void deactivateJTA(String s, String s1) throws RemoteException, MigrationException {

    }

    @Override
    public String getCurrentLocationOfJTA(String s) throws RemoteException, PersistentStoreException {
        return null;
    }

    @Override
    public void setCurrentLocation(String s, String s1) throws RemoteException, PersistentStoreException {

    }
}
