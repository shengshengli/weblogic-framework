import weblogic.cluster.singleton.MissedHeartbeatException;
import weblogic.cluster.singleton.RemoteLeasingBasis;

import java.io.IOException;
import java.util.Set;

/**
 * Title: PocServerRemoteLeasingBasis
 * Desc: TODO
 * Date:2020/4/3 21:23
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class PocServerRemoteLeasingBasis implements RemoteLeasingBasis {
    @Override
    public boolean acquire(String s, String s1, int i) throws IOException {
        return false;
    }

    @Override
    public void release(String s, String s1) throws IOException {

    }

    @Override
    public String findOwner(String s) throws IOException {
        return null;
    }

    @Override
    public String findPreviousOwner(String s) throws IOException {
        return null;
    }

    @Override
    public int renewAllLeases(int i, String s) throws IOException, MissedHeartbeatException {
        return 0;
    }

    @Override
    public String[] findExpiredLeases(int i) throws IOException {
        return new String[0];
    }

    @Override
    public int renewLeases(String s, Set set, int i) throws IOException, MissedHeartbeatException {
        return 0;
    }
}
