import weblogic.cluster.replication.*;
import weblogic.rmi.spi.HostID;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Title: PocServerReplicationServicesInternal
 * Desc: TODO
 * Date:2020/4/3 21:24
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class PocServerReplicationServicesInternal implements ReplicationServicesInternal {
    @Override
    public Object create(HostID hostID, int i, ROID roid, Replicatable replicatable) throws RemoteException {
        return null;
    }

    @Override
    public void update(ROID roid, int i, Serializable serializable, Object o) throws NotFoundException, RemoteException {

    }

    @Override
    public void updateOneWay(ROID roid, int i, Serializable serializable, Object o) throws NotFoundException, RemoteException {

    }

    @Override
    public ReplicationManager.ROObject fetch(ROID roid) throws RemoteException, NotFoundException {
        return null;
    }

    @Override
    public void remove(ROID[] roids, Object o) throws RemoteException {

    }

    @Override
    public void removeOneWay(ROID[] roids, Object o) throws RemoteException {

    }

    @Override
    public void remove(ROID[] roids) throws RemoteException {

    }

    @Override
    public void update(AsyncBatch asyncBatch) throws RemoteException {

    }
}
